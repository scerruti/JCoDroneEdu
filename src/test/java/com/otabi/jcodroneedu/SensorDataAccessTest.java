package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.dronestatus.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for sensor data access methods.
 * 
 * Tests Python parity for sensor methods:
 * - get_battery()
 * - get_flight_state()
 * - get_movement_state()
 * - get_height()
 * - get_front_range()
 * - get_pos_x/y/z()
 * - get_accel_x/y/z()
 * - get_angle_x/y/z()
 */
@DisplayName("Sensor Data Access Tests")
public class SensorDataAccessTest {

    private FlightController flightController;
    private Drone mockDrone;
    private DroneStatus mockStatus;
    private State mockState;
    private Motion mockMotion;
    private Range mockRange;
    private Position mockPosition;

    @BeforeEach
    void setUp() {
        // Create mock objects
        mockDrone = mock(Drone.class);
        mockStatus = mock(DroneStatus.class);
        mockState = mock(State.class);
        mockMotion = mock(Motion.class);
        mockRange = mock(Range.class);
        mockPosition = mock(Position.class);
        
        // Configure mocks
        when(mockDrone.getDroneStatus()).thenReturn(mockStatus);
        
        // Create real FlightController with mocked drone
        flightController = new FlightController(mockDrone);
    }

    @Nested
    @DisplayName("Battery and State Tests")
    class BatteryAndStateTests {

        @Test
        @DisplayName("getBattery() returns correct battery level")
        void testGetBattery() {
            // Arrange
            when(mockStatus.getState()).thenReturn(mockState);
            when(mockState.getBattery()).thenReturn((byte) 85);

            // Act
            int battery = flightController.getBattery();

            // Assert
            assertEquals(85, battery, "Battery level should match state data");
            verify(mockDrone).sendRequest(DataType.State);
        }

        @Test
        @DisplayName("getBattery() handles null state gracefully")
        void testGetBatteryNullState() {
            // Arrange
            when(mockStatus.getState()).thenReturn(null);

            // Act
            int battery = flightController.getBattery();

            // Assert
            assertEquals(0, battery, "Should return 0 when state is null");
        }

        @Test
        @DisplayName("getFlightState() returns correct flight state")
        void testGetFlightState() {
            // Arrange
            when(mockStatus.getState()).thenReturn(mockState);
            when(mockState.getModeFlight()).thenReturn(DroneSystem.ModeFlight.FLIGHT);

            // Act
            String flightState = flightController.getFlightState();

            // Assert
            assertEquals("FLIGHT", flightState, "Flight state should match enum name");
            verify(mockDrone).sendRequest(DataType.State);
        }

        @Test
        @DisplayName("getFlightState() handles null state gracefully")
        void testGetFlightStateNullState() {
            // Arrange
            when(mockStatus.getState()).thenReturn(null);

            // Act
            String flightState = flightController.getFlightState();

            // Assert
            assertEquals("UNKNOWN", flightState, "Should return UNKNOWN when state is null");
        }

        @Test
        @DisplayName("getMovementState() returns correct movement state")
        void testGetMovementState() {
            // Arrange
            when(mockStatus.getState()).thenReturn(mockState);
            when(mockState.getModeMovement()).thenReturn(DroneSystem.ModeMovement.HOVERING);

            // Act
            String movementState = flightController.getMovementState();

            // Assert
            assertEquals("HOVERING", movementState, "Movement state should match enum name");
        }
    }

    @Nested
    @DisplayName("Range Sensor Tests")
    class RangeSensorTests {

        @Test
        @DisplayName("getHeight() returns bottom range in centimeters")
        void testGetHeight() {
            // Arrange
            when(mockStatus.getRange()).thenReturn(mockRange);
            when(mockRange.getBottom()).thenReturn((short) 1200); // 1200mm = 120cm

            // Act
            double height = flightController.getHeight();

            // Assert
            assertEquals(120.0, height, 0.01, "Height should convert mm to cm");
            verify(mockDrone).sendRequest(DataType.Range);
        }

        @Test
        @DisplayName("getHeight(unit) converts to different units")
        void testGetHeightWithUnits() {
            // Arrange
            when(mockStatus.getRange()).thenReturn(mockRange);
            when(mockRange.getBottom()).thenReturn((short) 1000); // 1000mm

            // Act & Assert
            assertEquals(100.0, flightController.getHeight("cm"), 0.01, "Should convert to cm");
            assertEquals(1000.0, flightController.getHeight("mm"), 0.01, "Should return mm");
            assertEquals(1.0, flightController.getHeight("m"), 0.01, "Should convert to m");
            assertEquals(39.37, flightController.getHeight("in"), 0.01, "Should convert to inches");
        }

        @Test
        @DisplayName("getFrontRange() returns front sensor distance")
        void testGetFrontRange() {
            // Arrange
            when(mockStatus.getRange()).thenReturn(mockRange);
            when(mockRange.getFront()).thenReturn((short) 500); // 500mm = 50cm

            // Act
            double frontRange = flightController.getFrontRange();

            // Assert
            assertEquals(50.0, frontRange, 0.01, "Front range should convert mm to cm");
            verify(mockDrone).sendRequest(DataType.Range);
        }

        @Test
        @DisplayName("Range methods handle null range gracefully")
        void testRangeMethodsNullRange() {
            // Arrange
            when(mockStatus.getRange()).thenReturn(null);

            // Act & Assert
            assertEquals(0.0, flightController.getHeight(), "Should return 0 when range is null");
            assertEquals(0.0, flightController.getFrontRange(), "Should return 0 when range is null");
        }
    }

    @Nested
    @DisplayName("Position Sensor Tests")
    class PositionSensorTests {

        @Test
        @DisplayName("getPosX() returns X position in centimeters")
        void testGetPosX() {
            // Arrange
            when(mockStatus.getPosition()).thenReturn(mockPosition);
            when(mockPosition.getX()).thenReturn(1500); // 1500mm = 150cm

            // Act
            double posX = flightController.getPosX();

            // Assert
            assertEquals(150.0, posX, 0.01, "X position should convert mm to cm");
            verify(mockDrone).sendRequest(DataType.Position);
        }

        @Test
        @DisplayName("getPosY() returns Y position in centimeters")
        void testGetPosY() {
            // Arrange
            when(mockStatus.getPosition()).thenReturn(mockPosition);
            when(mockPosition.getY()).thenReturn(-800); // -800mm = -80cm

            // Act
            double posY = flightController.getPosY();

            // Assert
            assertEquals(-80.0, posY, 0.01, "Y position should convert mm to cm (negative)");
        }

        @Test
        @DisplayName("getPosZ() returns Z position in centimeters")
        void testGetPosZ() {
            // Arrange
            when(mockStatus.getPosition()).thenReturn(mockPosition);
            when(mockPosition.getZ()).thenReturn(2000); // 2000mm = 200cm

            // Act
            double posZ = flightController.getPosZ();

            // Assert
            assertEquals(200.0, posZ, 0.01, "Z position should convert mm to cm");
        }

        @Test
        @DisplayName("Position methods with units convert correctly")
        void testPositionWithUnits() {
            // Arrange
            when(mockStatus.getPosition()).thenReturn(mockPosition);
            when(mockPosition.getX()).thenReturn(1000); // 1000mm

            // Act & Assert
            assertEquals(100.0, flightController.getPosX("cm"), 0.01, "Should convert to cm");
            assertEquals(1.0, flightController.getPosX("m"), 0.01, "Should convert to m");
            assertEquals(39.37, flightController.getPosX("in"), 0.01, "Should convert to inches");
        }

        @Test
        @DisplayName("Position methods handle null position gracefully")
        void testPositionMethodsNullPosition() {
            // Arrange
            when(mockStatus.getPosition()).thenReturn(null);

            // Act & Assert
            assertEquals(0.0, flightController.getPosX(), "Should return 0 when position is null");
            assertEquals(0.0, flightController.getPosY(), "Should return 0 when position is null");
            assertEquals(0.0, flightController.getPosZ(), "Should return 0 when position is null");
        }
    }

    @Nested
    @DisplayName("Motion Sensor Tests")
    class MotionSensorTests {

        @Test
        @DisplayName("getAccelX() returns X acceleration in G-force")
        void testGetAccelX() {
            // Arrange
            when(mockStatus.getMotion()).thenReturn(mockMotion);
            when(mockMotion.getAccelX()).thenReturn((short) 1500); // Raw value

            // Act
            double accelX = flightController.getAccelX();

            // Assert
            assertEquals(1.5, accelX, 0.01, "Acceleration should be scaled to G-force");
            verify(mockDrone).sendRequest(DataType.Motion);
        }

        @Test
        @DisplayName("getAccelY() returns Y acceleration in G-force")
        void testGetAccelY() {
            // Arrange
            when(mockStatus.getMotion()).thenReturn(mockMotion);
            when(mockMotion.getAccelY()).thenReturn((short) -800); // Negative raw value

            // Act
            double accelY = flightController.getAccelY();

            // Assert
            assertEquals(-0.8, accelY, 0.01, "Acceleration should handle negative values");
        }

        @Test
        @DisplayName("getAccelZ() returns Z acceleration in G-force")
        void testGetAccelZ() {
            // Arrange
            when(mockStatus.getMotion()).thenReturn(mockMotion);
            when(mockMotion.getAccelZ()).thenReturn((short) 1000); // 1G

            // Act
            double accelZ = flightController.getAccelZ();

            // Assert
            assertEquals(1.0, accelZ, 0.01, "Z acceleration should represent gravity");
        }

        @Test
        @DisplayName("getAngleX() returns roll angle in degrees")
        void testGetAngleX() {
            // Arrange
            when(mockStatus.getMotion()).thenReturn(mockMotion);
            when(mockMotion.getAngleRoll()).thenReturn((short) 1500); // Raw value

            // Act
            double angleX = flightController.getAngleX();

            // Assert
            assertEquals(15.0, angleX, 0.01, "Angle should be scaled to degrees");
        }

        @Test
        @DisplayName("getAngleY() returns pitch angle in degrees")
        void testGetAngleY() {
            // Arrange
            when(mockStatus.getMotion()).thenReturn(mockMotion);
            when(mockMotion.getAnglePitch()).thenReturn((short) -1000); // Negative

            // Act
            double angleY = flightController.getAngleY();

            // Assert
            assertEquals(-10.0, angleY, 0.01, "Angle should handle negative values");
        }

        @Test
        @DisplayName("getAngleZ() returns yaw angle in degrees")
        void testGetAngleZ() {
            // Arrange
            when(mockStatus.getMotion()).thenReturn(mockMotion);
            when(mockMotion.getAngleYaw()).thenReturn((short) 9000); // 90 degrees

            // Act
            double angleZ = flightController.getAngleZ();

            // Assert
            assertEquals(90.0, angleZ, 0.01, "Yaw angle should be scaled correctly");
        }

        @Test
        @DisplayName("Motion methods handle null motion gracefully")
        void testMotionMethodsNullMotion() {
            // Arrange
            when(mockStatus.getMotion()).thenReturn(null);

            // Act & Assert
            assertEquals(0.0, flightController.getAccelX(), "Should return 0 when motion is null");
            assertEquals(0.0, flightController.getAccelY(), "Should return 0 when motion is null");
            assertEquals(0.0, flightController.getAccelZ(), "Should return 0 when motion is null");
            assertEquals(0.0, flightController.getAngleX(), "Should return 0 when motion is null");
            assertEquals(0.0, flightController.getAngleY(), "Should return 0 when motion is null");
            assertEquals(0.0, flightController.getAngleZ(), "Should return 0 when motion is null");
        }
    }

    @Nested
    @DisplayName("Python Parity Tests")
    class PythonParityTests {

        @Test
        @DisplayName("Unit conversion matches Python behavior")
        void testUnitConversionParity() {
            // Arrange - Set up known values
            when(mockStatus.getRange()).thenReturn(mockRange);
            when(mockRange.getBottom()).thenReturn((short) 1000); // 1000mm
            when(mockRange.getFront()).thenReturn((short) 500);   // 500mm
            
            when(mockStatus.getPosition()).thenReturn(mockPosition);
            when(mockPosition.getX()).thenReturn(2000); // 2000mm
            
            // Test unit conversions match Python behavior
            assertEquals(100.0, flightController.getHeight("cm"), 0.01, "cm conversion should match Python");
            assertEquals(1000.0, flightController.getHeight("mm"), 0.01, "mm should match Python");
            assertEquals(1.0, flightController.getHeight("m"), 0.01, "m conversion should match Python");
            assertEquals(39.37, flightController.getHeight("in"), 0.01, "inch conversion should match Python");
            
            // Test other sensor unit conversions
            assertEquals(50.0, flightController.getFrontRange("cm"), 0.01);
            assertEquals(200.0, flightController.getPosX("cm"), 0.01);
        }

        @Test
        @DisplayName("Sensor data return types are appropriate")
        void testSensorDataTypes() {
            // Arrange
            when(mockStatus.getState()).thenReturn(mockState);
            when(mockState.getBattery()).thenReturn((byte) 75);
            when(mockState.getModeFlight()).thenReturn(DroneSystem.ModeFlight.READY);
            
            when(mockStatus.getRange()).thenReturn(mockRange);
            when(mockRange.getBottom()).thenReturn((short) 1200);
            
            when(mockStatus.getMotion()).thenReturn(mockMotion);
            when(mockMotion.getAccelX()).thenReturn((short) 1000);
            
            // Act & Assert - Verify return types are appropriate
            int battery = flightController.getBattery();
            String flightState = flightController.getFlightState();
            double height = flightController.getHeight();
            double accel = flightController.getAccelX();
            
            assertTrue(battery >= 0 && battery <= 100, "Battery should be valid percentage");
            assertNotNull(flightState, "Flight state should not be null");
            assertTrue(height >= 0, "Height should be non-negative");
            assertTrue(Double.isFinite(accel), "Acceleration should be finite number");
        }
    }

    @Nested
    @DisplayName("Educational Integration Tests")
    class EducationalIntegrationTests {

        @Test
        @DisplayName("L0106 Conditionals - Battery check example")
        void testBatteryCheckConditional() {
            // Arrange
            when(mockStatus.getState()).thenReturn(mockState);
            
            // Test low battery scenario
            when(mockState.getBattery()).thenReturn((byte) 25);
            boolean shouldLand = flightController.getBattery() < 30;
            assertTrue(shouldLand, "Should recommend landing when battery is low");
            
            // Test sufficient battery scenario
            when(mockState.getBattery()).thenReturn((byte) 75);
            boolean canTakeOff = flightController.getBattery() > 50;
            assertTrue(canTakeOff, "Should allow takeoff when battery is sufficient");
        }

        @Test
        @DisplayName("L0106 Conditionals - Obstacle avoidance example")
        void testObstacleAvoidanceConditional() {
            // Arrange
            when(mockStatus.getRange()).thenReturn(mockRange);
            
            // Test obstacle detection
            when(mockRange.getFront()).thenReturn((short) 300); // 30cm
            boolean obstacleDetected = flightController.getFrontRange() < 50;
            assertTrue(obstacleDetected, "Should detect obstacle within 50cm");
            
            // Test clear path
            when(mockRange.getFront()).thenReturn((short) 800); // 80cm
            boolean pathClear = flightController.getFrontRange() > 50;
            assertTrue(pathClear, "Should detect clear path beyond 50cm");
        }

        @Test
        @DisplayName("L0105 Variables - Position tracking example")
        void testPositionTrackingVariables() {
            // Arrange
            when(mockStatus.getPosition()).thenReturn(mockPosition);
            when(mockPosition.getX()).thenReturn(0);
            when(mockPosition.getY()).thenReturn(0);
            
            // Simulate takeoff position
            double startX = flightController.getPosX();
            double startY = flightController.getPosY();
            
            // Simulate movement
            when(mockPosition.getX()).thenReturn(1000); // Moved 100cm right
            when(mockPosition.getY()).thenReturn(500);  // Moved 50cm forward
            
            double currentX = flightController.getPosX();
            double currentY = flightController.getPosY();
            
            // Calculate distance traveled
            double distanceTraveled = Math.sqrt(Math.pow(currentX - startX, 2) + Math.pow(currentY - startY, 2));
            
            assertEquals(111.8, distanceTraveled, 1.0, "Should calculate distance traveled correctly");
        }
    }
}
