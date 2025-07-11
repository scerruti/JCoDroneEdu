package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.DeviceType;
import com.otabi.jcodroneedu.protocol.Header;
import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.receiver.Receiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Receiver class.
 * These tests do not require a physical device and focus on verifying the
 * correctness of the byte stream parsing logic.
 */
class ReceiverTest {

    private Drone drone;
    private Receiver receiver;
    private DroneStatus droneStatus;

    @BeforeEach
    void setUp() {
        // Create a new Drone instance before each test. This also creates fresh
        // instances of the Receiver and DroneStatus, ensuring test isolation.
        drone = new Drone();
        receiver = drone.getReceiver(); // Assuming a getter exists on Drone
        droneStatus = drone.getDroneStatus();
    }

    @Test
    @DisplayName("Should correctly parse a valid State message")
    void shouldCorrectlyParseValidStateMessage() {
        // --- 1. Arrange ---
        // Create a complete, valid State object using the full constructor.
        // This is the state we expect the drone to be in.
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

        // Create a valid header for this message.
        Header header = new Header(
                DataType.State,
                expectedState.getSize(),
                DeviceType.Drone,
                DeviceType.Base
        );

        // Manually construct the full, valid byte stream.
        byte[] messageBytes = buildTestPacket(header, expectedState);

        // --- 2. Act ---
        // Feed the byte stream to the receiver, one byte at a time.
        for (byte b : messageBytes) {
            receiver.call(b);
        }

        // --- 3. Assert ---
        // After the last byte, the receiver should have parsed the message
        // and updated the DroneStatus object.
        State actualState = droneStatus.getState();

        assertNotNull(actualState, "State object should not be null after parsing.");
        assertEquals(expectedState.getBattery(), actualState.getBattery(), "Battery level should match.");

        // These assertions assume that getters for these fields exist or will be added to the State class.
        // assertEquals(expectedState.getModeSystem(), actualState.getModeSystem(), "System mode should match.");
        // assertEquals(expectedState.getModeFlight(), actualState.getModeFlight(), "Flight mode should match.");
        // assertEquals(expectedState.getControlSpeed(), actualState.getControlSpeed(), "Control speed should match.");

        // We can also use the boolean helper methods for checks
        assertTrue(actualState.isFlight(), "State should report as being in flight.");
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

    // TODO: Add more tests for other scenarios:
    // - Test with a bad CRC to ensure the message is rejected.
    // - Test with a malformed packet (e.g., wrong start bytes) to ensure it's ignored.
    // - Test messages with no payload.
    // - Test other message types like Attitude, Information, etc.
}
