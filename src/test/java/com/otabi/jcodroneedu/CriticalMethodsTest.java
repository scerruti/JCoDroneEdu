package com.otabi.jcodroneedu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for critical Phase 1 methods implemented in MockDrone:
 * - emergency_stop() (safety)
 * - go() (v2.3 beginner-friendly method)
 * - get_front_range() (obstacle avoidance)
 * - square() (educational patterns)
 * - get_move_values() (debugging)
 */
@DisplayName("Critical Methods Tests (Phase 1 Implementation)")
public class CriticalMethodsTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        // Base implementation - individual tests will override
        mockDrone.pair();
        mockDrone.takeoff();
        mockDrone.land();
        mockDrone.close();
    }

    @Nested
    @DisplayName("Emergency Stop - Safety Critical")
    class EmergencyStopTests {

        @Test
        @DisplayName("emergency_stop() should be callable and tracked")
        void emergencyStopShouldBeTracked() {
            mockDrone.pair();
            mockDrone.takeoff();
            mockDrone.emergency_stop();
            
            assertTrue(mockDrone.wasEmergencyStopCalled(), 
                "emergency_stop() call should be tracked");
            
            assertTrue(mockDrone.getAllCommands().contains("emergency_stop"),
                "Command history should contain emergency_stop");
        }

        @Test
        @DisplayName("emergency_stop() should reset movement values")
        void emergencyStopShouldResetMovement() {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Set some movement values
            mockDrone.setPitch(50);
            mockDrone.setRoll(30);
            
            // Emergency stop should reset values
            mockDrone.emergency_stop();
            
            assertTrue(mockDrone.hasResetMoveValuesCalls(),
                "emergency_stop() should call resetMoveValues()");
        }

        @Test
        @DisplayName("Should count multiple emergency_stop calls")
        void shouldCountMultipleEmergencyStops() {
            mockDrone.pair();
            mockDrone.emergency_stop();
            mockDrone.emergency_stop();
            mockDrone.emergency_stop();
            
            assertEquals(3, mockDrone.getEmergencyStopCount(),
                "Should track multiple emergency_stop calls");
        }
    }

    @Nested
    @DisplayName("Go Method - v2.3 Beginner-Friendly Movement")
    class GoMethodTests {

        @Test
        @DisplayName("go() with all parameters should be tracked")
        void goWithAllParametersShouldWork() {
            mockDrone.pair();
            mockDrone.takeoff();
            mockDrone.go("forward", 75, 2);
            
            assertTrue(mockDrone.wasGoMethodUsed(),
                "go() method usage should be tracked");
            
            assertTrue(mockDrone.getAllCommands().contains("go(forward, 75, 2)"),
                "Command history should contain complete go() call");
        }

        @Test
        @DisplayName("go() should handle different directions")
        void goShouldHandleAllDirections() {
            mockDrone.pair();
            mockDrone.takeoff();
            
            String[] directions = {"forward", "backward", "left", "right", "up", "down"};
            
            for (String direction : directions) {
                mockDrone.go(direction, 50, 1);
            }
            
            assertEquals(6, mockDrone.getGoMethodCalls().size(),
                "Should track all direction calls");
            
            for (String direction : directions) {
                assertTrue(mockDrone.getAllCommands().stream()
                    .anyMatch(cmd -> cmd.contains("go(" + direction)),
                    "Should contain go() call for direction: " + direction);
            }
        }

        @Test
        @DisplayName("go() should clamp power values to 0-100 range")
        void goShouldClampPowerValues() {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Test power clamping (this tests the validation logic)
            mockDrone.go("forward", 150, 1); // Should clamp to 100
            mockDrone.go("backward", -25, 1); // Should clamp to 0
            
            // In mock, we verify the parameters are processed
            assertTrue(mockDrone.getAllCommands().contains("go(forward, 100, 1)"),
                "Power should be clamped to maximum 100");
            
            assertTrue(mockDrone.getAllCommands().contains("go(backward, 0, 1)"),
                "Power should be clamped to minimum 0");
        }

        @Test
        @DisplayName("go() overloaded methods should use defaults")
        void goOverloadedMethodsShouldUseDefaults() {
            mockDrone.pair();
            mockDrone.takeoff();
            
            mockDrone.go("forward", 2); // Should default power to 50
            mockDrone.go("left");        // Should default power to 50, duration to 1
            
            assertTrue(mockDrone.getAllCommands().contains("go(forward, 50, 2)"),
                "go(direction, duration) should default power to 50");
            
            assertTrue(mockDrone.getAllCommands().contains("go(left, 50, 1)"),
                "go(direction) should default power to 50 and duration to 1");
        }

        @Test
        @DisplayName("go() should handle case insensitive directions")
        void goShouldBeCaseInsensitive() {
            mockDrone.pair();
            mockDrone.takeoff();
            
            mockDrone.go("FORWARD", 50, 1);
            mockDrone.go("Right", 50, 1);
            mockDrone.go("bAcKwArD", 50, 1);
            
            // All should be converted to lowercase
            assertTrue(mockDrone.getAllCommands().contains("go(forward, 50, 1)"),
                "FORWARD should be converted to lowercase");
            
            assertTrue(mockDrone.getAllCommands().contains("go(right, 50, 1)"),
                "Right should be converted to lowercase");
            
            assertTrue(mockDrone.getAllCommands().contains("go(backward, 50, 1)"),
                "bAcKwArD should be converted to lowercase");
        }
    }

    @Nested
    @DisplayName("Front Range Sensor - Obstacle Avoidance")
    class FrontRangeTests {

        @Test
        @DisplayName("get_front_range() should return distance and be tracked")
        void getFrontRangeShouldReturnDistance() {
            mockDrone.pair();
            
            double distance = mockDrone.get_front_range("cm");
            
            assertTrue(distance > 0, "Should return positive distance");
            assertTrue(mockDrone.wasFrontRangeUsed(),
                "Front range usage should be tracked");
            
            assertTrue(mockDrone.getAllCommands().contains("get_front_range(cm)"),
                "Command history should contain front range call");
        }

        @Test
        @DisplayName("get_front_range() should handle different units")
        void getFrontRangeShouldHandleUnits() {
            mockDrone.pair();
            
            double distanceCm = mockDrone.get_front_range("cm");
            double distanceMm = mockDrone.get_front_range("mm");
            double distanceM = mockDrone.get_front_range("m");
            
            // Basic unit conversion validation (50cm = 500mm = 0.5m)
            assertEquals(distanceCm * 10, distanceMm, 0.01,
                "mm should be 10x cm");
            
            assertEquals(distanceCm / 100, distanceM, 0.01,
                "m should be cm/100");
        }

        @Test
        @DisplayName("get_front_range() with no parameters should default to cm")
        void getFrontRangeDefaultShouldBeCm() {
            mockDrone.pair();
            
            double distance = mockDrone.get_front_range();
            
            assertTrue(distance > 0, "Should return positive distance");
            assertTrue(mockDrone.getAllCommands().contains("get_front_range(cm)"),
                "Default call should use cm units");
        }
    }

    @Nested
    @DisplayName("Square Pattern - Educational Flight Sequences")
    class SquarePatternTests {

        @Test
        @DisplayName("square() with all parameters should be tracked")
        void squareWithAllParametersShouldWork() {
            mockDrone.pair();
            mockDrone.takeoff();
            mockDrone.square(80, 2, -1);
            
            assertTrue(mockDrone.wasSquarePatternUsed(),
                "Square pattern usage should be tracked");
            
            assertTrue(mockDrone.getAllCommands().contains("square(80, 2, -1)"),
                "Command history should contain complete square call");
        }

        @Test
        @DisplayName("square() should clamp speed to 0-100 range")
        void squareShouldClampSpeed() {
            mockDrone.pair();
            mockDrone.takeoff();
            
            mockDrone.square(150, 1, 1); // Should clamp to 100
            mockDrone.square(-25, 1, 1);  // Should clamp to 0
            
            assertTrue(mockDrone.getAllCommands().contains("square(100, 1, 1)"),
                "Speed should be clamped to maximum 100");
            
            assertTrue(mockDrone.getAllCommands().contains("square(0, 1, 1)"),
                "Speed should be clamped to minimum 0");
        }

        @Test
        @DisplayName("square() overloaded methods should use defaults")
        void squareOverloadedMethodsShouldUseDefaults() {
            mockDrone.pair();
            mockDrone.takeoff();
            
            mockDrone.square(70, 3);  // Should default direction to 1
            mockDrone.square(80);     // Should default seconds to 1, direction to 1
            mockDrone.square();       // Should use all defaults: 60, 1, 1
            
            assertTrue(mockDrone.getAllCommands().contains("square(70, 3, 1)"),
                "square(speed, seconds) should default direction to 1");
            
            assertTrue(mockDrone.getAllCommands().contains("square(80, 1, 1)"),
                "square(speed) should default seconds to 1 and direction to 1");
            
            assertTrue(mockDrone.getAllCommands().contains("square(60, 1, 1)"),
                "square() should use all defaults");
        }
    }

    @Nested
    @DisplayName("Get Move Values - Debugging Support")
    class GetMoveValuesTests {

        @Test
        @DisplayName("get_move_values() should return array and be tracked")
        void getMoveValuesShouldReturnArray() {
            mockDrone.pair();
            mockDrone.setPitch(25);
            mockDrone.setRoll(15);
            
            int[] values = mockDrone.get_move_values();
            
            assertNotNull(values, "Should return non-null array");
            assertEquals(4, values.length, "Should return 4-element array [roll, pitch, yaw, throttle]");
            
            assertTrue(mockDrone.getAllCommands().contains("get_move_values"),
                "Command history should contain get_move_values call");
        }

        @Test
        @DisplayName("get_move_values() should reflect recent movement settings")
        void getMoveValuesShouldReflectSettings() {
            mockDrone.pair();
            
            // Set some specific values
            mockDrone.setPitch(30);
            mockDrone.setRoll(45);
            
            int[] values = mockDrone.get_move_values();
            
            // Should reflect the most recent values set
            assertEquals(45, values[0], "Roll should be 45 (index 0)");
            assertEquals(30, values[1], "Pitch should be 30 (index 1)");
            // Yaw and throttle should be 0 (not set)
            assertEquals(0, values[2], "Yaw should be 0 (index 2)");
            assertEquals(0, values[3], "Throttle should be 0 (index 3)");
        }
    }

    @Nested
    @DisplayName("Integration Tests - Multiple Critical Methods")
    class IntegrationTests {

        @Test
        @DisplayName("Student should be able to use go() for simple obstacle avoidance")
        void studentObstacleAvoidanceWithGo() {
            // Simulate student code using v2.3 go() method with obstacle avoidance
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Check for obstacle
            double frontDistance = mockDrone.get_front_range();
            
            if (frontDistance < 30) { // If obstacle closer than 30cm
                mockDrone.go("up", 50, 1);      // Go up to avoid
                mockDrone.go("forward", 50, 2); // Then forward
                mockDrone.go("down", 50, 1);    // Then down
            } else {
                mockDrone.go("forward", 60, 3); // Safe to go forward
            }
            
            mockDrone.land();
            mockDrone.close();
            
            // Verify the sequence
            assertTrue(mockDrone.wasGoMethodUsed(), "Should use go() method");
            assertTrue(mockDrone.wasFrontRangeUsed(), "Should check front range");
            assertTrue(mockDrone.wasTakeoffCalled(), "Should takeoff");
            assertTrue(mockDrone.wasLandCalled(), "Should land");
            assertTrue(mockDrone.wasCleanupMethodCalled(), "Should cleanup");
        }

        @Test
        @DisplayName("Student should be able to combine square pattern with safety checks")
        void squarePatternWithSafety() {
            // Simulate educational assignment: square with emergency handling
            mockDrone.pair();
            mockDrone.takeoff();
            
            try {
                // Check area is clear
                double frontDistance = mockDrone.get_front_range();
                
                if (frontDistance > 100) { // Safe distance for square pattern
                    mockDrone.square(70, 2, 1); // Execute square pattern
                } else {
                    mockDrone.emergency_stop(); // Emergency stop if not safe
                }
                
            } catch (Exception e) {
                mockDrone.emergency_stop(); // Emergency stop on any error
            }
            
            mockDrone.land();
            mockDrone.close();
            
            // Verify comprehensive usage
            assertTrue(mockDrone.wasSquarePatternUsed() || mockDrone.wasEmergencyStopCalled(),
                "Should either execute square or emergency stop");
            
            assertTrue(mockDrone.wasFrontRangeUsed(), "Should check safety first");
            assertTrue(mockDrone.wasConnectionMethodCalled(), "Should connect properly");
            assertTrue(mockDrone.wasCleanupMethodCalled(), "Should cleanup properly");
        }
    }
}
