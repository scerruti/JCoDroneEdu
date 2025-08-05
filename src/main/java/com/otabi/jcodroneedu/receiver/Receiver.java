package com.otabi.jcodroneedu.receiver;

import com.otabi.jcodroneedu.*;
import com.otabi.jcodroneedu.protocol.*;
import com.otabi.jcodroneedu.protocol.dronestatus.Attitude;
import com.otabi.jcodroneedu.protocol.dronestatus.Altitude;
import com.otabi.jcodroneedu.protocol.dronestatus.Position;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.protocol.dronestatus.Flow;
import com.otabi.jcodroneedu.protocol.dronestatus.RawFlow;
import com.otabi.jcodroneedu.protocol.cardreader.CardColor;
import com.otabi.jcodroneedu.protocol.linkmanager.Information;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Handles the byte-by-byte parsing of the drone's communication protocol.
 * This class uses a state machine to build a complete message from the serial stream,
 * validates its integrity using CRC16, and then dispatches the payload using a
 * factory/handler pattern.
 */
public class Receiver {
    private static final Logger log = LogManager.getLogger(Receiver.class);
    private static final int MAX_PAYLOAD_SIZE = DroneSystem.CommunicationConstants.MAX_PAYLOAD_SIZE;
    private static final long RECEIVE_TIMEOUT_MS = 600;

    // References to other drone components
    private final DroneStatus droneStatus;
    private final LinkManager linkManager;

    // --- Acknowledgment Handling ---
    private final Map<DataType, CompletableFuture<Void>> pendingAcks = new ConcurrentHashMap<>();
    private final Map<DataType, Consumer<Serializable>> handlers = new HashMap<>();

    // --- State machine fields ---
    private Section currentSection;
    private int index;
    private long timeReceiveStart;

    // --- Message components being built ---
    private Header header;
    private final ByteBuffer dataBuffer;
    private int crc16received;
    private int crc16calculated;

    public Receiver(Drone drone, DroneStatus droneStatus, LinkManager linkManager) {
        this.droneStatus = droneStatus;
        this.linkManager = linkManager;
        this.dataBuffer = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);
        initializeHandlers();
        reset();
    }

    /**
     * Populates the handler map with functions that know how to process
     * each type of message.
     */
    private void initializeHandlers() {
        handlers.put(DataType.State, msg -> droneStatus.setState((State) msg));
        handlers.put(DataType.Information, msg -> linkManager.setInformation((Information) msg));
        handlers.put(DataType.Attitude, msg -> droneStatus.setAttitude((Attitude) msg));
        handlers.put(DataType.Position, msg -> droneStatus.setPosition((Position) msg));
        handlers.put(DataType.Altitude, msg -> droneStatus.setAltitude((Altitude) msg));
        handlers.put(DataType.CardColor, msg -> droneStatus.setCardColor((CardColor) msg));
        handlers.put(DataType.Trim, msg -> droneStatus.setTrim((com.otabi.jcodroneedu.protocol.settings.Trim) msg));
        handlers.put(DataType.RawFlow, msg -> droneStatus.setRawFlow((RawFlow) msg));
        handlers.put(DataType.Flow, msg -> droneStatus.setFlow((Flow) msg));
        // ... add handlers for all other message types here ...
    }

    private void reset() {
        currentSection = Section.START;
        index = 0;
        header = null;
        dataBuffer.clear();
        crc16calculated = 0;
        crc16received = 0;
        timeReceiveStart = 0;
    }

    public void call(byte data) {
        if (timeReceiveStart > 0 && (System.currentTimeMillis() - timeReceiveStart > RECEIVE_TIMEOUT_MS)) {
            log.error("Receiver timeout. Resetting.");
            reset();
        }

        Section sectionBefore = this.currentSection;
        boolean success;

        switch (currentSection) {
            case START:
                success = handleStart(data);
                break;
            case HEADER:
                success = handleHeader(data);
                break;
            case DATA:
                success = handleData(data);
                break;
            case END:
                success = handleEnd(data);
                break;
            default:
                log.error("Unknown receiver section: {}", currentSection);
                success = false;
        }

        if (!success) {
            reset();
            return;
        }

        if (timeReceiveStart > 0 && this.currentSection == sectionBefore) {
            index++;
        }
    }

    private boolean handleStart(byte data) {
        if (index == 0) {
            if (data == DroneSystem.CommunicationConstants.PROTOCOL_START_BYTE_1) {
                timeReceiveStart = System.currentTimeMillis();
            } else {
                return false; // Not a start byte, ignore
            }
        } else if (index == 1) {
            if (data == DroneSystem.CommunicationConstants.PROTOCOL_START_BYTE_2) {
                currentSection = Section.HEADER; // Transition to next section
                index = 0;
            } else {
                log.warn("Invalid start sequence. Expected 0x55, got 0x{:02X}", data);
                return false;
            }
        }
        return true;
    }

    private boolean handleHeader(byte data) {
        if (index == 0) {
            header = new Header();
            try {
                header.setDataType(DataType.fromByte(data));
                if (header.getDataType() == null) throw new IllegalArgumentException("DataType is null");
            } catch (Exception e) {
                log.error("Invalid DataType received: 0x{:02X}", data, e);
                return false;
            }
        } else if (index == 1) {
            header.setLength(data);
            if (header.getLength() > MAX_PAYLOAD_SIZE) {
                log.error("Invalid payload length: {}. Exceeds max of {}", header.getLength(), MAX_PAYLOAD_SIZE);
                return false;
            }
        } else if (index == 2) {
            try {
                header.setFrom(DeviceType.fromByte(data));
            } catch (Exception e) {
                log.error("Invalid 'From' DeviceType received: 0x{:02X}", data, e);
                return false;
            }
        } else if (index == 3) {
            try {
                header.setTo(DeviceType.fromByte(data));
            } catch (Exception e) {
                log.error("Invalid 'To' DeviceType received: 0x{:02X}", data, e);
                return false;
            }

            if (header.getLength() == 0) {
                currentSection = Section.END;
            } else {
                currentSection = Section.DATA;
            }
            index = 0;
        }

        crc16calculated = CRC16.calc(data, crc16calculated);
        return true;
    }

    private boolean handleData(byte data) {
        dataBuffer.put(data);
        crc16calculated = CRC16.calc(data, crc16calculated);

        if (index == header.getLength() - 1) {
            currentSection = Section.END;
            index = 0;
        }
        return true;
    }

    private boolean handleEnd(byte data) {
        if (index == 0) {
            crc16received = data & DroneSystem.CommunicationConstants.BYTE_MASK;
        } else if (index == 1) {
            crc16received |= (data & DroneSystem.CommunicationConstants.BYTE_MASK) << 8;

            if (crc16received == crc16calculated) {
                log.info("CRC OK for {}. Received: 0x{:04X}", header.getDataType(), crc16received);
                processPayload();
                reset();
            } else {
                log.error("CRC Mismatch for {}. Received: 0x{:04X}, Calculated: 0x{:04X}",
                        header.getDataType(), crc16received, crc16calculated);
                return false;
            }
        }
        return true;
    }

    /**
     * Called after a message is successfully received and validated.
     * This method uses a factory to create the message object and a handler
     * map to dispatch it to the correct processor.
     */
    private void processPayload() {
        dataBuffer.flip(); // Prepare buffer for reading
        ByteBuffer payloadBuffer = dataBuffer;

        try {
            // Handle Ack separately as it doesn't create a new object to store.
            if (header.getDataType() == DataType.Ack) {
                if (payloadBuffer.hasRemaining()) {
                    DataType ackedType = DataType.fromByte(payloadBuffer.get());
                    onAckReceived(ackedType);
                }
                return;
            }

            // Use the factory to create an empty instance of the correct message type.
            Serializable message = header.getDataType().createInstance();
            if (message == null) {
                log.warn("No factory defined for DataType: {}", header.getDataType());
                return;
            }

            // Tell the message to parse itself from the payload.
            message.parse(payloadBuffer);

            // Look up the correct handler function and execute it.
            Consumer<Serializable> handler = handlers.get(header.getDataType());
            if (handler != null) {
                handler.accept(message);
            } else {
                log.debug("No handler registered for DataType: {}", header.getDataType());
            }

        } catch (Exception e) {
            log.error("Failed to process payload for DataType: {}", header.getDataType(), e);
        }
    }

    // --- Public ACK Management Methods ---
    public void expectAck(DataType dataType) {
        pendingAcks.put(dataType, new CompletableFuture<>());
    }
    public CompletableFuture<Void> getAckFuture(DataType dataType) {
        return pendingAcks.get(dataType);
    }
    public void onAckReceived(DataType ackType) {
        CompletableFuture<Void> future = pendingAcks.remove(ackType);
        if (future != null) {
            future.complete(null);
            log.info("ACK received and processed for {}", ackType);
        } else {
            log.warn("Received an unexpected ACK for {}", ackType);
        }
    }

    private enum Section { START, HEADER, DATA, END }
}
