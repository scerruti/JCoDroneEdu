package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.DeviceType;
import com.otabi.jcodroneedu.protocol.Header;
import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.receiver.Receiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Receiver class.
 * These tests do not require a physical device and focus on verifying the
 * correctness of the byte stream parsing logic.
 */
class ReceiverTest {

    private Receiver receiver;
    private DroneStatus droneStatus;

    @Test
    @DisplayName("Should correctly parse a valid State message")
    void shouldCorrectlyParseValidStateMessage() {
        // Use try-with-resources to create and close Drone per test
        try (Drone drone = new Drone()) {
            receiver = drone.getReceiver();
            droneStatus = drone.getDroneStatus();

            // --- 1. Arrange ---
            State expectedState = new State(
                    DroneSystem.ModeSystem.RUNNING,
                    DroneSystem.ModeFlight.FLIGHT,
                    DroneSystem.ModeControlFlight.ATTITUDE,
                    DroneSystem.ModeMovement.HOVERING,
                    DroneSystem.Headless.NORMAL_MODE,
                    (byte) 2, // Control Speed
                    DroneSystem.SensorOrientation.NORMAL,
                    (byte) 85  // Battery
            );

            Header header = new Header(
                    DataType.State,
                    expectedState.getSize(),
                    DeviceType.Drone,
                    DeviceType.Base
            );

            byte[] messageBytes = buildTestPacket(header, expectedState);

            // --- 2. Act ---
            for (byte b : messageBytes) {
                receiver.call(b);
            }

            // --- 3. Assert ---
            State actualState = droneStatus.getState();

            assertNotNull(actualState, "State object should not be null after parsing.");
            assertEquals(expectedState.getBattery(), actualState.getBattery(), "Battery level should match.");
            assertTrue(actualState.isFlight(), "State should report as being in flight.");
        }
    }

    /**
     * Helper method to construct a valid, wire-format byte array for a given message.
     * This includes start bytes, header, payload, and a calculated CRC16.
     *
     * @param header The message header.
     * @param data   The serializable message payload.
     * @return A complete byte array ready to be sent to the receiver.
     */
    private byte[] buildTestPacket(Header header, Serializable data) {
        byte[] headerArray = header.toArray();
        byte[] dataArray = data.toArray();

        // Calculate the CRC on the header and data.
        int crc16 = CRC16.calc(headerArray, 0);
        crc16 = CRC16.calc(dataArray, crc16);

        // Allocate a buffer for the entire message.
        ByteBuffer buffer = ByteBuffer.allocate(2 + headerArray.length + dataArray.length + 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Build the message.
        buffer.put((byte) 0x0A); // Start byte 1
        buffer.put((byte) 0x55); // Start byte 2
        buffer.put(headerArray);
        buffer.put(dataArray);
        buffer.putShort((short) crc16);

        return buffer.array();
    }

    // The following tests cover additional receiver scenarios:
    // - Bad CRC (message rejected)
    // - Malformed start sequence (ignored)
    // - ACK handling (completes expected future)
    // - Zero-length payload messages (handled correctly)

    @Test
    @DisplayName("Should reject message with bad CRC")
    void shouldRejectBadCrc() {
    try (Drone drone = new Drone()) {
        receiver = drone.getReceiver();
        droneStatus = drone.getDroneStatus();
        droneStatus.setState(null); // Ensure state is clear before test

        State s = new State(
            DroneSystem.ModeSystem.RUNNING,
            DroneSystem.ModeFlight.FLIGHT,
            DroneSystem.ModeControlFlight.ATTITUDE,
            DroneSystem.ModeMovement.HOVERING,
            DroneSystem.Headless.NORMAL_MODE,
            (byte)2,
            DroneSystem.SensorOrientation.NORMAL,
            (byte)50
        );
        Header h = new Header(DataType.State, s.getSize(), DeviceType.Drone, DeviceType.Base);
        byte[] pkt = buildTestPacket(h, s);

        // Corrupt one byte in payload to break CRC
        pkt[pkt.length - 3] ^= 0xFF;

            // Feed and ensure receiver does not set state
            for (byte b : pkt) receiver.call(b);
            State actualState = droneStatus.getState();
            assertNotNull(actualState, "State object should not be null after bad CRC (should be default/bad data)");
            // Check for default/bad values
            assertEquals(0, actualState.getBattery(), "Battery should be 0 for bad data");
            assertFalse(actualState.isFlight(), "Should not report as being in flight for bad data");
    }
    }

    @Test
    @DisplayName("Should ignore malformed start sequence")
    void shouldIgnoreMalformedStart() {
    try (Drone drone = new Drone()) {
        receiver = drone.getReceiver();
        droneStatus = drone.getDroneStatus();
        droneStatus.setState(null); // Ensure state is clear before test

        State s = new State(
            DroneSystem.ModeSystem.RUNNING,
            DroneSystem.ModeFlight.FLIGHT,
            DroneSystem.ModeControlFlight.ATTITUDE,
            DroneSystem.ModeMovement.HOVERING,
            DroneSystem.Headless.NORMAL_MODE,
            (byte)2,
            DroneSystem.SensorOrientation.NORMAL,
            (byte)50
        );
        Header h = new Header(DataType.State, s.getSize(), DeviceType.Drone, DeviceType.Base);
        byte[] pkt = buildTestPacket(h, s);

        // Change the start bytes to be invalid
        pkt[0] = 0x00; pkt[1] = 0x00;

            for (byte b : pkt) receiver.call(b);
            State actualState = droneStatus.getState();
            assertNotNull(actualState, "State object should not be null after malformed start (should be default/bad data)");
            // Check for default/bad values
            assertEquals(0, actualState.getBattery(), "Battery should be 0 for bad data");
            assertFalse(actualState.isFlight(), "Should not report as being in flight for bad data");
    }
    }

    @Test
    @DisplayName("Should process ACK messages and complete future")
    void shouldProcessAck() throws Exception {
        try (Drone drone = new Drone()) {
            receiver = drone.getReceiver();
            droneStatus = drone.getDroneStatus();

            // Arrange: expect an ack for DataType.State
            receiver.expectAck(DataType.State);
            CompletableFuture<Void> f = receiver.getAckFuture(DataType.State);
            assertNotNull(f);

            // Build an Ack payload containing the DataType.State byte
            Header h = new Header(DataType.Ack, (byte)1, DeviceType.Drone, DeviceType.Base);
            // Ack payload is a single byte with the DataType value
            byte[] headerArray = h.toArray();
            byte[] payload = new byte[]{DataType.State.value()};

            int crc = CRC16.calc(headerArray, 0);
            crc = CRC16.calc(payload, crc);

            ByteBuffer buf = ByteBuffer.allocate(2 + headerArray.length + payload.length + 2).order(ByteOrder.LITTLE_ENDIAN);
            buf.put((byte)0x0A); buf.put((byte)0x55);
            buf.put(headerArray);
            buf.put(payload);
            buf.putShort((short)crc);

            byte[] pkt = buf.array();
            for (byte b : pkt) receiver.call(b);

            // Wait a little for future to complete
            f.get(250, TimeUnit.MILLISECONDS);
            assertTrue(f.isDone(), "ACK future should be completed after receiving ack");
        }
    }

    @Test
    @DisplayName("Should handle zero-length payload messages (END only)")
    void shouldHandleZeroLengthPayload() {
        try (Drone drone = new Drone()) {
            receiver = drone.getReceiver();
            droneStatus = drone.getDroneStatus();

            Header h = new Header(DataType.Information, (byte)0, DeviceType.Drone, DeviceType.Base);
            byte[] headerArray = h.toArray();
            int crc = CRC16.calc(headerArray, 0);

            ByteBuffer buf = ByteBuffer.allocate(2 + headerArray.length + 0 + 2).order(ByteOrder.LITTLE_ENDIAN);
            buf.put((byte)0x0A); buf.put((byte)0x55);
            buf.put(headerArray);
            buf.putShort((short)crc);

            byte[] pkt = buf.array();
            for (byte b : pkt) receiver.call(b);

            // No exception should have been thrown and code should continue
            assertTrue(true);
        }
    }
}

