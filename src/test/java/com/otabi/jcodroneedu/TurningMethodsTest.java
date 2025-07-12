package com.otabi.jcodroneedu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for turning methods (Punch List Item #3).
 * 
 * These tests verify that the new turning methods work correctly
 * and provide proper Python API compatibility for educational use.
 */
@DisplayName("Turning Methods Tests (Punch List Item #3)")
class TurningMethodsTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        // Default test: basic turning pattern
        mockDrone.turn(50, 2.0);        // Basic turn
        mockDrone.turnLeft(90);         // Left quarter turn
        mockDrone.turnRight(45);        // Right eighth turn
        mockDrone.turnDegree(180);      // Half turn
    }

    @Nested
    @DisplayName("Basic Turn Method Tests")
    class BasicTurnMethodTests {

        @Test
        @DisplayName("Should turn with power and duration")
        void shouldTurnWithPowerAndDuration() {
            mockDrone.turn(50, 2.0); // 50% power for 2 seconds
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turn(50, 2.0)")),
                "Should call turn method with power and duration");
        }

        @Test
        @DisplayName("Should turn with default duration")
        void shouldTurnWithDefaultDuration() {
            mockDrone.turn(30); // 30% power with default duration
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turn(30, 1.0)")),
                "Should call turn with default 1 second duration");
        }

        @Test
        @DisplayName("Should turn with null duration for manual control")
        void shouldTurnWithNullDuration() {
            mockDrone.turn(40, null); // Manual control mode
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turn(40)")),
                "Should call turn with null duration for manual control");
        }

        @Test
        @DisplayName("Should handle positive and negative power values")
        void shouldHandlePositiveAndNegativePower() {
            mockDrone.turn(50, 1.0);   // Turn left
            mockDrone.turn(-50, 1.0);  // Turn right
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turn(50, 1.0)")),
                "Should handle positive power (left turn)");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turn(-50, 1.0)")),
                "Should handle negative power (right turn)");
        }
    }

    @Nested
    @DisplayName("Degree-Based Turn Tests")
    class DegreeTurnTests {

        @Test
        @DisplayName("Should turn to specific degree with default parameters")
        void shouldTurnToSpecificDegree() {
            mockDrone.turnDegree(90); // 90 degrees with defaults
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnDegree(90, 3.0, 10.0)")),
                "Should call turnDegree with default timeout and pValue");
        }

        @Test
        @DisplayName("Should turn to specific degree with custom timeout")
        void shouldTurnWithCustomTimeout() {
            mockDrone.turnDegree(180, 5.0); // 180 degrees with 5 second timeout
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnDegree(180, 5.0, 10.0)")),
                "Should call turnDegree with custom timeout");
        }

        @Test
        @DisplayName("Should turn with all custom parameters")
        void shouldTurnWithAllCustomParameters() {
            mockDrone.turnDegree(45, 4.0, 15.0); // Custom degree, timeout, and pValue
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnDegree(45, 4.0, 15.0)")),
                "Should call turnDegree with all custom parameters");
        }

        @Test
        @DisplayName("Should handle positive and negative degrees")
        void shouldHandlePositiveAndNegativeDegrees() {
            mockDrone.turnDegree(90);   // Left turn
            mockDrone.turnDegree(-90);  // Right turn
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnDegree(90")),
                "Should handle positive degrees (left turn)");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnDegree(-90")),
                "Should handle negative degrees (right turn)");
        }
    }

    @Nested
    @DisplayName("Directional Turn Tests")
    class DirectionalTurnTests {

        @Test
        @DisplayName("Should turn left with default timeout")
        void shouldTurnLeftWithDefaults() {
            mockDrone.turnLeft(90); // 90 degrees left
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnLeft(90, 3.0)")),
                "Should call turnLeft with default timeout");
        }

        @Test
        @DisplayName("Should turn left with custom timeout")
        void shouldTurnLeftWithCustomTimeout() {
            mockDrone.turnLeft(45, 5.0); // 45 degrees left with 5 second timeout
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnLeft(45, 5.0)")),
                "Should call turnLeft with custom timeout");
        }

        @Test
        @DisplayName("Should turn right with default timeout")
        void shouldTurnRightWithDefaults() {
            mockDrone.turnRight(90); // 90 degrees right
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnRight(90, 3.0)")),
                "Should call turnRight with default timeout");
        }

        @Test
        @DisplayName("Should turn right with custom timeout")
        void shouldTurnRightWithCustomTimeout() {
            mockDrone.turnRight(135, 4.0); // 135 degrees right with 4 second timeout
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnRight(135, 4.0)")),
                "Should call turnRight with custom timeout");
        }
    }

    @Nested
    @DisplayName("Educational API Compatibility Tests")
    class EducationalCompatibilityTests {

        @Test
        @DisplayName("Should provide Python-like turning API")
        void shouldProvidePythonLikeTurningAPI() {
            // Test pattern similar to Python tutorials
            mockDrone.turn(50, 1.0);        // Basic turning
            mockDrone.turnLeft(90);         // Intuitive left turn
            mockDrone.turnRight(90);        // Intuitive right turn
            mockDrone.turnDegree(180);      // Precise angle control
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Verify all turning types were called
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turn(")),
                "Should support Python-style turn()");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnLeft(")),
                "Should support Python-style turnLeft()");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnRight(")),
                "Should support Python-style turnRight()");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnDegree(")),
                "Should support Python-style turnDegree()");
        }

        @Test
        @DisplayName("Should support common educational turning patterns")
        void shouldSupportEducationalPatterns() {
            // Common patterns from educational curriculum
            mockDrone.turnLeft(90);    // Quarter turn left
            mockDrone.turnRight(90);   // Quarter turn right
            mockDrone.turnDegree(180); // Half turn (about face)
            mockDrone.turnLeft(45);    // Eighth turn left
            mockDrone.turnRight(45);   // Eighth turn right
            
            List<String> commands = mockDrone.getAllCommands();
            assertEquals(5, commands.stream().mapToInt(cmd -> 
                (cmd.contains("turnLeft(") || cmd.contains("turnRight(") || cmd.contains("turnDegree(")) ? 1 : 0
            ).sum(), "Should support all common educational turning patterns");
        }

        @Test
        @DisplayName("Should work with movement methods for navigation patterns")
        void shouldWorkWithMovementMethods() {
            // Test navigation pattern: move forward, turn, repeat
            mockDrone.moveForward(100.0, "cm");  // Move forward
            mockDrone.turnRight(90);             // Turn right
            mockDrone.moveForward(100.0, "cm");  // Move forward again
            mockDrone.turnLeft(90);              // Turn left
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Verify mixed movement and turning
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveForward")),
                "Should work with distance movement methods");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnRight")),
                "Should work with turning methods");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnLeft")),
                "Should support complex navigation patterns");
        }
    }

    @Nested
    @DisplayName("Safety and Validation Tests")
    class SafetyValidationTests {

        @Test
        @DisplayName("Should not crash with extreme parameters")
        void shouldNotCrashWithExtremeParameters() {
            assertDoesNotThrow(() -> {
                mockDrone.turn(150, 10.0);        // Power too high
                mockDrone.turnDegree(270);        // Degree too high
                mockDrone.turnLeft(-45);          // Negative degrees (should be made positive)
                mockDrone.turnRight(200, 0.1);   // Very short timeout
            }, "Should handle extreme parameters safely");
        }

        @Test
        @DisplayName("Should maintain flight safety during turns")
        void shouldMaintainFlightSafety() {
            executeStudentDroneOperations();
            
            // Verify no emergency stops were triggered during normal turning
            assertFalse(mockDrone.wasEmergencyStopCalled(),
                "Should not trigger emergency stop during normal turning");
        }

        @Test
        @DisplayName("Should work with existing safety methods")
        void shouldWorkWithSafetyMethods() {
            mockDrone.takeoff();
            mockDrone.turn(50, 1.0);
            mockDrone.hover(1);
            mockDrone.turnLeft(90);
            mockDrone.land();
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Verify turning integrates safely with flight sequence
            assertTrue(commands.stream().anyMatch(cmd -> cmd.equals("takeoff")),
                "Should work with takeoff");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turn(")),
                "Should include turning operations");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.equals("land")),
                "Should work with landing");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should work with existing flight control methods")
        void shouldWorkWithExistingMethods() {
            // Mix turning with existing methods
            mockDrone.takeoff();
            mockDrone.go("forward", 50, 2);      // Existing go method
            mockDrone.turnRight(90);             // New turning method
            mockDrone.moveLeft(50.0, "cm");      // Distance movement
            mockDrone.turnDegree(-45);           // Precise turning
            mockDrone.hover(2);
            mockDrone.land();
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Verify mixed usage works
            assertTrue(commands.size() >= 7, "Should have all commands recorded");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("go(")),
                "Should work with existing go method");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("turnRight(")),
                "Should work with new turning methods");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.contains("moveLeft(")),
                "Should work with distance movement");
        }

        @Test
        @DisplayName("Should create complex flight patterns with turns")
        void shouldCreateComplexFlightPatterns() {
            // Create a square pattern using turning methods
            mockDrone.takeoff();
            
            // Square pattern using turns
            for (int i = 0; i < 4; i++) {
                mockDrone.moveForward(1.0, "m");
                mockDrone.turnRight(90);
            }
            
            // Navigation with obstacles (simulated)
            mockDrone.moveForward(0.5, "m");
            mockDrone.turnLeft(45);       // Avoid obstacle
            mockDrone.moveForward(0.5, "m");
            mockDrone.turnRight(45);      // Resume course
            
            mockDrone.land();
            
            List<String> commands = mockDrone.getAllCommands();
            assertTrue(commands.size() >= 12, "Should have complex pattern recorded");
            
            // Verify pattern includes turning
            long turnCount = commands.stream().filter(cmd -> 
                cmd.contains("turnRight(") || cmd.contains("turnLeft(")
            ).count();
            assertTrue(turnCount >= 6, "Should have multiple turns in pattern");
        }

        @Test
        @DisplayName("Should demonstrate educational progression")
        void shouldDemonstrateEducationalProgression() {
            // Progression from basic to advanced turning
            
            // Level 1: Basic power-based turning
            mockDrone.turn(30);
            
            // Level 2: Directional turning
            mockDrone.turnLeft(90);
            mockDrone.turnRight(90);
            
            // Level 3: Precise angle control
            mockDrone.turnDegree(45);
            mockDrone.turnDegree(-135, 5.0);
            
            // Level 4: Advanced control with custom parameters
            mockDrone.turnDegree(60, 4.0, 15.0);
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Verify progression is captured
            assertTrue(commands.stream().anyMatch(cmd -> cmd.startsWith("turn(")),
                "Should support basic turning");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.startsWith("turnLeft(")),
                "Should support directional turning");
            assertTrue(commands.stream().anyMatch(cmd -> cmd.startsWith("turnDegree(")),
                "Should support precise angle control");
        }
    }

    @Nested
    @DisplayName("Test Helper Validation")
    class TestHelperValidationTests {

        @Test
        @DisplayName("Should detect turning usage")
        void shouldDetectTurningUsage() {
            mockDrone.turnLeft(90);
            
            assertTrue(mockDrone.wasTurningUsed(),
                "Should detect when turning methods are used");
        }

        @Test
        @DisplayName("Should detect precise turning usage")
        void shouldDetectPreciseTurningUsage() {
            mockDrone.turnDegree(45);
            
            assertTrue(mockDrone.wasPreciseTurningUsed(),
                "Should detect when precise turning methods are used");
        }

        @Test
        @DisplayName("Should get all turning calls")
        void shouldGetAllTurningCalls() {
            mockDrone.turn(50);
            mockDrone.turnLeft(90);
            mockDrone.turnRight(45);
            mockDrone.turnDegree(180);
            
            List<String> turningCalls = mockDrone.getTurningCalls();
            assertEquals(4, turningCalls.size(),
                "Should capture all turning method calls");
        }

        @Test
        @DisplayName("Should not detect turning when not used")
        void shouldNotDetectTurningWhenNotUsed() {
            mockDrone.takeoff();
            mockDrone.moveForward(100.0);
            mockDrone.land();
            
            assertFalse(mockDrone.wasTurningUsed(),
                "Should not detect turning when only using other methods");
        }
    }
}
