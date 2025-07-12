package com.otabi.jcodroneedu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for distance-based movement methods (Punch List Item #2).
 * 
 * These tests verify that the new distance-based movement methods work correctly
 * and provide proper Python API compatibility.
 */
@DisplayName("Distance-Based Movement Tests (Punch List Item #2)")
class DistanceMovementTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        // Default test: basic movement pattern
        mockDrone.moveForward(50.0); // 50 cm forward
        mockDrone.moveRight(30.0);   // 30 cm right
        mockDrone.moveBackward(25.0); // 25 cm backward
        mockDrone.moveLeft(40.0);    // 40 cm left
    }

    @Nested
    @DisplayName("Forward Movement Tests")
    class ForwardMovementTests {

        @Test
        @DisplayName("Should move forward with default parameters")
        void shouldMoveForwardWithDefaults() {
            mockDrone.moveForward(50.0); // 50 cm, default speed
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveForward")),
                "Should call moveForward method");
        }

        @Test
        @DisplayName("Should move forward with specified units")
        void shouldMoveForwardWithUnits() {
            mockDrone.moveForward(2.0, "ft"); // 2 feet
            mockDrone.moveForward(12.0, "in"); // 12 inches  
            mockDrone.moveForward(1.0, "m");   // 1 meter
            
            List<String> commands = mockDrone.getAllCommands();
            assertEquals(3, commands.stream().mapToInt(cmd -> cmd.contains("moveForward") ? 1 : 0).sum(),
                "Should have 3 moveForward calls");
        }

        @Test
        @DisplayName("Should move forward with custom speed")
        void shouldMoveForwardWithCustomSpeed() {
            mockDrone.moveForward(100.0, "cm", 1.5); // 100 cm at 1.5 m/s
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveForward")),
                "Should call moveForward with custom speed");
        }
    }

    @Nested
    @DisplayName("Backward Movement Tests")
    class BackwardMovementTests {

        @Test
        @DisplayName("Should move backward with default parameters")
        void shouldMoveBackwardWithDefaults() {
            mockDrone.moveBackward(75.0); // 75 cm, default speed
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveBackward")),
                "Should call moveBackward method");
        }

        @Test
        @DisplayName("Should move backward with specified units and speed")
        void shouldMoveBackwardWithCustomParameters() {
            mockDrone.moveBackward(3.0, "ft", 0.8); // 3 feet at 0.8 m/s
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveBackward")),
                "Should call moveBackward with custom parameters");
        }
    }

    @Nested
    @DisplayName("Left Movement Tests")
    class LeftMovementTests {

        @Test
        @DisplayName("Should move left with default parameters")
        void shouldMoveLeftWithDefaults() {
            mockDrone.moveLeft(60.0); // 60 cm, default speed
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveLeft")),
                "Should call moveLeft method");
        }
    }

    @Nested
    @DisplayName("Right Movement Tests")
    class RightMovementTests {

        @Test
        @DisplayName("Should move right with default parameters")
        void shouldMoveRightWithDefaults() {
            mockDrone.moveRight(45.0); // 45 cm, default speed
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveRight")),
                "Should call moveRight method");
        }
    }

    @Nested
    @DisplayName("3D Movement Tests")
    class ThreeDMovementTests {

        @Test
        @DisplayName("Should move in 3D space")
        void shouldMoveIn3D() {
            mockDrone.moveDistance(1.0, 0.5, 0.25, 1.0); // 3D movement
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveDistance")),
                "Should call moveDistance for 3D movement");
        }

        @Test
        @DisplayName("Should send absolute position commands")
        void shouldSendAbsolutePosition() {
            mockDrone.sendAbsolutePosition(2.0, 1.0, 1.5, 0.8, 90, 30);
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("sendAbsolutePosition")),
                "Should call sendAbsolutePosition for absolute positioning");
        }
    }

    @Nested
    @DisplayName("Educational API Compatibility Tests")
    class EducationalCompatibilityTests {

        @Test
        @DisplayName("Should provide Python-like API for educational use")
        void shouldProvidePythonLikeAPI() {
            // Test pattern similar to Python tutorials
            mockDrone.moveForward(50.0, "cm", 1.0);   // Move forward 50 cm
            mockDrone.moveLeft(30.0, "cm", 1.0);      // Move left 30 cm
            mockDrone.moveBackward(25.0, "cm", 1.0);  // Move backward 25 cm
            mockDrone.moveRight(35.0, "cm", 1.0);     // Move right 35 cm
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Verify all movement types were called
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveForward")),
                "Should support Python-style moveForward");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveLeft")),
                "Should support Python-style moveLeft");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveBackward")),
                "Should support Python-style moveBackward");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveRight")),
                "Should support Python-style moveRight");
        }

        @Test
        @DisplayName("Should support different unit systems for education")
        void shouldSupportDifferentUnits() {
            // Test all supported unit types
            mockDrone.moveForward(50.0, "cm");   // Centimeters (metric, default)
            mockDrone.moveForward(2.0, "ft");    // Feet (imperial)
            mockDrone.moveForward(12.0, "in");   // Inches (imperial)
            mockDrone.moveForward(1.0, "m");     // Meters (metric)
            
            List<String> commands = mockDrone.getAllCommands();
            assertEquals(4, commands.stream().mapToInt(cmd -> cmd.contains("moveForward") ? 1 : 0).sum(),
                "Should support all unit types for educational flexibility");
        }

        @Test
        @DisplayName("Should handle educational error scenarios gracefully")
        void shouldHandleErrorsGracefully() {
            // Test invalid units (should not crash)
            assertDoesNotThrow(() -> {
                mockDrone.moveForward(50.0, "invalid_unit");
            }, "Should handle invalid units gracefully");
            
            // Test extreme values (should be clamped)
            assertDoesNotThrow(() -> {
                mockDrone.moveForward(100.0, "cm", 5.0); // Speed too high, should be clamped
                mockDrone.moveDistance(15.0, 0.0, 0.0, 0.1); // Speed too low, should be clamped
            }, "Should handle extreme values gracefully");
        }
    }

    @Nested
    @DisplayName("Safety and Validation Tests")
    class SafetyValidationTests {

        @Test
        @DisplayName("Should not crash with invalid parameters")
        void shouldNotCrashWithInvalidParameters() {
            assertDoesNotThrow(() -> {
                mockDrone.moveForward(-50.0);       // Negative distance
                mockDrone.moveForward(0.0);         // Zero distance
                mockDrone.moveForward(50.0, null);  // Null units
                mockDrone.moveDistance(Double.NaN, 0.0, 0.0, 1.0); // NaN values
            }, "Should handle invalid parameters safely");
        }

        @Test
        @DisplayName("Should maintain flight safety during movements")
        void shouldMaintainFlightSafety() {
            executeStudentDroneOperations();
            
            // Verify no emergency stops were triggered during normal operations
            assertFalse(mockDrone.wasEmergencyStopCalled(),
                "Should not trigger emergency stop during normal distance movements");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should work with existing flight control methods")
        void shouldWorkWithExistingMethods() {
            // Mix new distance methods with existing methods
            mockDrone.takeoff();
            mockDrone.moveForward(100.0, "cm");
            mockDrone.go("right", 50, 2);
            mockDrone.moveLeft(50.0, "cm");
            mockDrone.hover(2);
            mockDrone.moveBackward(75.0, "cm");
            mockDrone.land();
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Verify mixed usage works
            assertTrue(commands.size() >= 7, "Should have all commands recorded");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.equals("takeoff")),
                "Should work with takeoff");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveForward")),
                "Should work with new move methods");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("go")),
                "Should work with existing go method");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.equals("land")),
                "Should work with landing");
        }

        @Test
        @DisplayName("Should create complex flight patterns")
        void shouldCreateComplexFlightPatterns() {
            // Create a square pattern using distance-based movement
            mockDrone.takeoff();
            
            // Square pattern: 1m x 1m
            mockDrone.moveForward(1.0, "m", 1.0);
            mockDrone.moveRight(1.0, "m", 1.0);
            mockDrone.moveBackward(1.0, "m", 1.0);
            mockDrone.moveLeft(1.0, "m", 1.0);
            
            // 3D movement: move up and forward simultaneously
            mockDrone.moveDistance(0.5, 0.0, 0.5, 0.8);
            
            mockDrone.land();
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.size() >= 7, "Should have complex pattern recorded");
        }
    }
}
