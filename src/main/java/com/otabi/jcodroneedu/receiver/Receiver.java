package com.otabi.jcodroneedu.receiver;

import com.otabi.jcodroneedu.*;
import com.otabi.jcodroneedu.protocol.*;
import com.otabi.jcodroneedu.protocol.dronestatus.Attitude;
import com.otabi.jcodroneedu.protocol.dronestatus.Motion;
import com.otabi.jcodroneedu.protocol.dronestatus.Range;
import com.otabi.jcodroneedu.protocol.dronestatus.Altitude;
import com.otabi.jcodroneedu.protocol.dronestatus.Position;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.protocol.dronestatus.Flow;
import com.otabi.jcodroneedu.protocol.dronestatus.RawFlow;
import com.otabi.jcodroneedu.protocol.cardreader.CardColor;
import com.otabi.jcodroneedu.protocol.linkmanager.Information;
import com.otabi.jcodroneedu.protocol.controllerinput.Joystick;
import com.otabi.jcodroneedu.protocol.controllerinput.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    private final Drone drone;
    private final DroneStatus droneStatus;
    private final LinkManager linkManager;

    // --- Acknowledgment Handling ---
    private final Map<DataType, CompletableFuture<Void>> pendingAcks = new ConcurrentHashMap<>();
    private final Map<DataType, Consumer<Serializable>> handlers = new HashMap<>();
    // Simple rate-limiting for repeated debug messages
    private DataType lastLoggedDataType = null;
    private long lastLoggedTimeMs = 0;

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
        this.drone = drone;
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
    handlers.put(DataType.Motion, msg -> droneStatus.setMotion((Motion) msg));
    handlers.put(DataType.Range, msg -> droneStatus.setRange((Range) msg));
        handlers.put(DataType.CardColor, msg -> droneStatus.setCardColor((CardColor) msg));
        handlers.put(DataType.Trim, msg -> droneStatus.setTrim((com.otabi.jcodroneedu.protocol.settings.Trim) msg));
        handlers.put(DataType.RawFlow, msg -> droneStatus.setRawFlow((RawFlow) msg));
        handlers.put(DataType.Flow, msg -> droneStatus.setFlow((Flow) msg));
        handlers.put(DataType.Joystick, msg -> drone.updateJoystickData((Joystick) msg));
        handlers.put(DataType.Button, msg -> drone.updateButtonData((Button) msg));
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
                log.warn("Invalid start sequence. Expected 0x55, got 0x{}", String.format("%02X", data & 0xFF));
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
                // Log a concise warning with the raw byte in hex, avoid stack-trace spam from unexpected bytes
                String hex = String.format("%02X", data & DroneSystem.CommunicationConstants.BYTE_MASK);
                log.debug("Invalid DataType received: 0x{}", hex);
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
                log.error("Invalid 'From' DeviceType received: 0x{}", String.format("%02X", data & 0xFF), e);
                return false;
            }
        } else if (index == 3) {
            try {
                header.setTo(DeviceType.fromByte(data));
            } catch (Exception e) {
                log.error("Invalid 'To' DeviceType received: 0x{}", String.format("%02X", data & 0xFF), e);
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
                // Avoid spamming the log for high-frequency telemetry (e.g., Joystick)
                long now = System.currentTimeMillis();
                DataType dt = header.getDataType();
                if (dt != lastLoggedDataType || (now - lastLoggedTimeMs) > 250) {
                    String hex = String.format("%04X", crc16received & 0xFFFF);
                    log.debug("CRC OK for {} (0x{}).", dt, hex);
                    lastLoggedDataType = dt;
                    lastLoggedTimeMs = now;
                }
                processPayload();
                reset();
            } else {
                String rhex = String.format("%04X", crc16received & 0xFFFF);
                String chex = String.format("%04X", crc16calculated & 0xFFFF);
        // CRC mismatches are expected in tests that mutate packets; make this debug to avoid noisy test output
        log.debug("CRC Mismatch for {}. Received: 0x{}, Calculated: 0x{}",
            header.getDataType(), rhex, chex);
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
    // The controller sends multi-byte fields in little-endian order. Ensure
    // we interpret the incoming payload as little-endian before parsing.
    payloadBuffer.order(ByteOrder.LITTLE_ENDIAN);

        try {
            // Handle Ack separately as it doesn't create a new object to store.
            if (header.getDataType() == DataType.Ack) {
                if (payloadBuffer.hasRemaining()) {
                    DataType ackedType = DataType.fromByte(payloadBuffer.get());
                    onAckReceived(ackedType);
                }
                return;
            }

            // Defensive: if header length is zero, there is no payload to parse.
            // Some message types (e.g., END-only Information frames) may legitimately
            // carry no payload; attempting to parse will throw BufferUnderflowException.
            if (header.getLength() == 0) {
                log.debug("Zero-length payload for {} — skipping parse.", header.getDataType());
                return;
            }

            // Use the factory to create an empty instance of the correct message type.
            Serializable message = header.getDataType().createInstance();
            if (message == null) {
                log.warn("No factory defined for DataType: {}", header.getDataType());
                return;
            }

            // Tell the message to parse itself from the payload.
            // Diagnostic: if this is a Motion payload, log the raw payload bytes and a little-endian
            // interpretation of the shorts so we can verify endianness/scale without altering parsing.
            if (header.getDataType() == com.otabi.jcodroneedu.protocol.DataType.Motion) {
                try {
                    ByteBuffer dup = payloadBuffer.duplicate();
                    dup.order(ByteOrder.BIG_ENDIAN); // original buffer default
                    byte[] arr = new byte[dup.remaining()];
                    dup.get(arr);
                    StringBuilder sb = new StringBuilder();
                    for (byte b : arr) sb.append(String.format("%02X ", b));
                    log.info("Motion payload bytes (len={}): {}", arr.length, sb.toString());

                    // Interpret as little-endian signed shorts for quick human-readable check
                    ByteBuffer peek = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN);
                    StringBuilder vals = new StringBuilder();
                    for (int i = 0; i < 9 && peek.remaining() >= 2; i++) {
                        short v = peek.getShort();
                        vals.append(String.format("%d%s", (int) v, (i < 8 ? "," : "")));
                    }
                    log.info("Motion payload as little-endian shorts: {}", vals.toString());
                } catch (Exception e) {
                    log.warn("Failed to dump Motion payload bytes: {}", e.getMessage());
                }
            }
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
        // Defensive: ackType may be null if the incoming byte did not map to a known DataType
        if (ackType == null) {
            // Lowered to debug: we often receive unknown/invalid ACK bytes when controller is chatty.
            log.debug("Received an ACK for an unknown/invalid DataType (null). Ignoring.");
            return;
        }

        CompletableFuture<Void> future = pendingAcks.remove(ackType);
        if (future != null) {
            future.complete(null);
            log.info("ACK received and processed for {}", ackType);
        } else {
            // Unexpected ACKs are common for unsolicited status updates — log at debug to avoid spam.
            log.debug("Received an unexpected ACK for {}", ackType);
        }
    }

    private enum Section { START, HEADER, DATA, END }
}
