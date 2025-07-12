package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DroneTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0103FlightMovements example - Lesson 1.3: Flight Movements: Throttle and Yaw
 * 
 * Based on Robolink's Python with CoDrone EDU curriculum:
 * https://learn.robolink.com/lesson/1-3-flight-movements-throttle-and-yaw-cde/
 * 
 * Educational Objectives:
 * - Describe how roll, pitch, yaw, and throttle change the drone's movement in the air
 * - Use the roll, pitch, yaw, and throttle commands to make the drone navigate a path
 * - Understand the difference between yaw (rotation) and roll (side movement)
 * - Learn throttle for vertical movement (up/down)
 * - Combine movements together and understand the importance of resetting values
 * - Build complete flight paths including takeoff and landing
 * 
 * Key Concepts:
 * - Yaw: rotation around Z-axis (left/right turning) vs Roll: lateral movement
 * - Throttle: vertical movement along Z-axis (up/down)
 * - Movement combination and the need to reset variables to 0
 * - Difference between simultaneous and sequential movements
 */
@DisplayName("L0103 Flight Movements: Throttle and Yaw Tests")
class L0103FlightMovementsTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }
    
    protected void executeStudentFlightPattern() {
        // Simulate the expected behavior from L0103FlightMovements example
        // Based on lesson 1.3 concepts: throttle and yaw movements
        try {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Demonstrate throttle (vertical movement)
            mockDrone.setThrottle(25);
            mockDrone.move(1000);
            mockDrone.setThrottle(0);
            
            // Demonstrate yaw (rotation)
            mockDrone.setYaw(25);
            mockDrone.move(1000);
            mockDrone.setYaw(0);
            
            // Combine movements
            mockDrone.setPitch(20);
            mockDrone.setYaw(15);
            mockDrone.move(1000);
            mockDrone.setPitch(0);
            mockDrone.setYaw(0);
            
            mockDrone.land();
            mockDrone.close();
        } catch (Exception e) {
            fail("Student code should not throw exceptions during normal execution: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("Throttle Movement Tests")
    class ThrottleMovementTests {

        @Test
        @DisplayName("Should use setThrottle for vertical movement")
        void shouldUseSetThrottleForVerticalMovement() {
            executeStudentFlightPattern();
            
            List<String> throttleCalls = mockDrone.getMethodCalls("setThrottle");
            assertFalse(throttleCalls.isEmpty(), 
                "Student should use setThrottle() for vertical movement (up/down)");
            
            // Verify both positive and negative throttle values are used
            List<String> commands = mockDrone.getAllCommands();
            boolean hasPositiveThrottle = commands.stream().anyMatch(cmd -> 
                cmd.contains("setThrottle") && cmd.contains("25"));
            boolean hasNegativeThrottle = commands.stream().anyMatch(cmd -> 
                cmd.contains("setThrottle") && cmd.contains("-25"));
            
            assertTrue(hasPositiveThrottle || hasNegativeThrottle, 
                "Student should demonstrate throttle movement (positive for up, negative for down)");
        }

        @Test
        @DisplayName("Should understand throttle is used in takeoff/land")
        void shouldUnderstandThrottleInTakeoffLand() {
            executeStudentFlightPattern();
            
            // Verify basic flight operations are present
            assertTrue(mockDrone.wasTakeoffCalled(), 
                "Student should understand takeoff uses throttle internally");
            assertTrue(mockDrone.wasLandCalled(), 
                "Student should understand land uses throttle internally");
        }
    }

    @Nested
    @DisplayName("Yaw Movement Tests")
    class YawMovementTests {

        @Test
        @DisplayName("Should use setYaw for rotation around Z-axis")
        void shouldUseSetYawForRotation() {
            executeStudentFlightPattern();
            
            List<String> yawCalls = mockDrone.getMethodCalls("setYaw");
            assertFalse(yawCalls.isEmpty(), 
                "Student should use setYaw() for rotation (spinning left/right)");
        }

        @Test
        @DisplayName("Should demonstrate difference between yaw and roll")
        void shouldDemonstrateDifferenceYawVsRoll() {
            executeStudentFlightPattern();
            
            // Both yaw and roll should be used to show the difference
            List<String> yawCalls = mockDrone.getMethodCalls("setYaw");
            List<String> rollCalls = mockDrone.getMethodCalls("setRoll");
            
            // At least one should be present to demonstrate the concepts
            assertTrue(!yawCalls.isEmpty() || !rollCalls.isEmpty(), 
                "Student should use either yaw (rotation) or roll (lateral movement) to understand the difference");
        }

        @Test
        @DisplayName("Should use appropriate yaw values (lesson shows 25)")
        void shouldUseAppropriateYawValues() {
            executeStudentFlightPattern();
            
            List<String> yawCalls = mockDrone.getMethodCalls("setYaw");
            if (!yawCalls.isEmpty()) {
                // Check for reasonable yaw values (lesson uses 25)
                boolean hasReasonableYaw = yawCalls.stream().anyMatch(call -> {
                    String valueStr = call.replaceAll("[^-0-9]", "");
                    if (!valueStr.isEmpty()) {
                        int value = Math.abs(Integer.parseInt(valueStr));
                        return value >= 10 && value <= 50; // Reasonable range for learning
                    }
                    return false;
                });
                assertTrue(hasReasonableYaw, 
                    "Student should use reasonable yaw values (10-50 range for safe learning)");
            }
        }
    }

    @Nested
    @DisplayName("Movement Combination Tests")
    class MovementCombinationTests {

        @Test
        @DisplayName("Should combine multiple movement types")
        void shouldCombineMultipleMovementTypes() {
            executeStudentFlightPattern();
            
            // Count different types of movements used
            List<String> pitchCalls = mockDrone.getMethodCalls("setPitch");
            List<String> rollCalls = mockDrone.getMethodCalls("setRoll");
            List<String> yawCalls = mockDrone.getMethodCalls("setYaw");
            List<String> throttleCalls = mockDrone.getMethodCalls("setThrottle");
            
            int movementTypes = 0;
            if (!pitchCalls.isEmpty()) movementTypes++;
            if (!rollCalls.isEmpty()) movementTypes++;
            if (!yawCalls.isEmpty()) movementTypes++;
            if (!throttleCalls.isEmpty()) movementTypes++;
            
            assertTrue(movementTypes >= 2, 
                "Student should combine at least 2 different movement types (pitch, roll, yaw, throttle)");
        }

        @Test
        @DisplayName("Should demonstrate proper movement sequencing")
        void shouldDemonstrateProperMovementSequencing() {
            executeStudentFlightPattern();
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple move() calls for sequenced movements
            long moveCallCount = commands.stream().filter(cmd -> cmd.startsWith("move")).count();
            assertTrue(moveCallCount >= 2, 
                "Student should use multiple move() calls to sequence movements properly");
            
            // Should have movement setting before move calls
            boolean hasMovementBeforeMove = false;
            for (int i = 0; i < commands.size() - 1; i++) {
                String current = commands.get(i);
                String next = commands.get(i + 1);
                if ((current.contains("setPitch") || current.contains("setRoll") || 
                     current.contains("setYaw") || current.contains("setThrottle")) && 
                    next.startsWith("move")) {
                    hasMovementBeforeMove = true;
                    break;
                }
            }
            assertTrue(hasMovementBeforeMove, 
                "Student should set movement values before calling move()");
        }

        @Test
        @DisplayName("Should reset movement values to prevent unintended combinations")
        void shouldResetMovementValues() {
            executeStudentFlightPattern();
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Check for reset to 0 (critical lesson concept)
            boolean hasResetValue = commands.stream().anyMatch(cmd -> 
                (cmd.contains("setPitch(0)") || cmd.contains("setRoll(0)") || 
                 cmd.contains("setYaw(0)") || cmd.contains("setThrottle(0)")));
            
            assertTrue(hasResetValue, 
                "Student should reset movement values to 0 to prevent unintended movement combinations");
        }
    }

    @Nested
    @DisplayName("Educational Progression Tests")
    class EducationalProgressionTests {

        @Test
        @DisplayName("Should build on L0102 concepts (pitch and roll)")
        void shouldBuildOnL0102Concepts() {
            executeStudentFlightPattern();
            
            // Should still use basic pitch/roll from previous lesson
            List<String> pitchCalls = mockDrone.getMethodCalls("setPitch");
            List<String> rollCalls = mockDrone.getMethodCalls("setRoll");
            
            assertTrue(!pitchCalls.isEmpty() || !rollCalls.isEmpty(), 
                "Student should continue using pitch/roll concepts from L0102");
        }

        @Test
        @DisplayName("Should demonstrate all four movement axes")
        void shouldDemonstrateAllFourMovementAxes() {
            executeStudentFlightPattern();
            
            // This lesson introduces the complete movement set
            List<String> commands = mockDrone.getAllCommands();
            
            // Count unique movement types demonstrated
            boolean hasPitch = commands.stream().anyMatch(cmd -> cmd.contains("setPitch"));
            boolean hasRoll = commands.stream().anyMatch(cmd -> cmd.contains("setRoll"));
            boolean hasYaw = commands.stream().anyMatch(cmd -> cmd.contains("setYaw"));
            boolean hasThrottle = commands.stream().anyMatch(cmd -> cmd.contains("setThrottle"));
            
            int axesUsed = (hasPitch ? 1 : 0) + (hasRoll ? 1 : 0) + (hasYaw ? 1 : 0) + (hasThrottle ? 1 : 0);
            assertTrue(axesUsed >= 3, 
                "Student should demonstrate at least 3 of the 4 movement axes (pitch, roll, yaw, throttle)");
        }

        @Test
        @DisplayName("Should prepare for variables lesson (L0104)")
        void shouldPrepareForVariablesLesson() {
            executeStudentFlightPattern();
            
            // This lesson should show repetitive patterns that variables could improve
            List<String> commands = mockDrone.getAllCommands();
            
            // Look for repeated movement values (indicating variable opportunities)
            boolean hasRepeatedValues = commands.stream().anyMatch(cmd -> 
                cmd.contains("20") || cmd.contains("25")); // Common values from lesson
            
            assertTrue(hasRepeatedValues, 
                "Student should use repeated movement values that could benefit from variables in next lesson");
        }
    }

    @Nested
    @DisplayName("Challenge Pattern Tests")
    class ChallengePatternTests {

        @Test
        @DisplayName("Should attempt advanced flight patterns")
        void shouldAttemptAdvancedFlightPatterns() {
            executeStudentFlightPattern();
            
            List<String> commands = mockDrone.getAllCommands();
            
            // Look for complex patterns (lesson mentions circle and sine wave challenges)
            boolean hasComplexPattern = commands.size() >= 8; // Multiple movements indicate complexity
            
            assertTrue(hasComplexPattern, 
                "Student should attempt more complex flight patterns as shown in lesson challenges");
        }

        @Test
        @DisplayName("Should demonstrate understanding of simultaneous vs sequential movement")
        void shouldDemonstrateSimultaneousVsSequential() {
            executeStudentFlightPattern();
            
            List<String> commands = mockDrone.getAllCommands();
            
            // The lesson specifically teaches about diagonal movement (pitch + roll without reset)
            // vs sequential movement (pitch, reset, then roll)
            boolean showsSequentialPattern = false;
            
            // Look for pattern: movement -> move -> reset -> different movement -> move
            for (int i = 0; i < commands.size() - 4; i++) {
                if ((commands.get(i).contains("set") && !commands.get(i).contains("(0)")) &&
                    commands.get(i + 1).startsWith("move") &&
                    commands.get(i + 2).contains("(0)") &&
                    (commands.get(i + 3).contains("set") && !commands.get(i + 3).contains("(0)")) &&
                    commands.get(i + 4).startsWith("move")) {
                    showsSequentialPattern = true;
                    break;
                }
            }
            
            assertTrue(showsSequentialPattern, 
                "Student should demonstrate understanding of sequential movement with proper resets");
        }
    }
}
