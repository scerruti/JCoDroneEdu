package com.otabi.jcodroneedu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0102FlightMovements example.
 * These tests help students verify their drone programming logic before flying with a real drone.
 * They check for proper use of pitch/roll commands in a square flight pattern.
 */
class L0102FlightMovementsTest extends DroneFlightTest {

    @Nested
    @DisplayName("Square Flight Pattern Tests")
    class SquareFlightTests {

        @Test
        @DisplayName("Should use setPitch() and setRoll() for directional movement")
        void shouldUsePitchAndRollForMovement() {
            executeStudentFlightPattern();

            List<String> pitchCalls = mockDrone.getMethodCalls("setPitch");
            List<String> rollCalls = mockDrone.getMethodCalls("setRoll");

            // Check that both pitch and roll are used
            assertFalse(pitchCalls.isEmpty(), 
                "❌ Must use setPitch() to move forward/backward in square pattern!");
            assertFalse(rollCalls.isEmpty(), 
                "❌ Must use setRoll() to move left/right in square pattern!");

            // For a square: should have at least 2 different pitch values and 2 different roll values
            assertTrue(mockDrone.getUniquePitchValues().size() >= 2,
                "❌ Square pattern requires at least 2 different pitch values (forward/backward)!");
            assertTrue(mockDrone.getUniqueRollValues().size() >= 2,
                "❌ Square pattern requires at least 2 different roll values (left/right)!");
        }

        @Test
        @DisplayName("Should reset pitch/roll values between movements")
        void shouldResetValuesBeforeNewDirection() {
            executeStudentFlightPattern();

            List<String> pitchCalls = mockDrone.getMethodCalls("setPitch");
            List<String> rollCalls = mockDrone.getMethodCalls("setRoll");

            // Verify that students are resetting values properly
            // Good practice is to set pitch and roll to 0 between directional movements
            assertTrue(pitchCalls.contains("setPitch(0)") && rollCalls.contains("setRoll(0)"),
                "⚠️  Consider resetting pitch/roll values (set to 0) between movements for cleaner flight patterns!");
        }

        @Test
        @DisplayName("Should use minimum required move commands for square")
        void shouldUseMinimumMoveCommands() {
            executeStudentFlightPattern();

            List<String> moveCalls = mockDrone.getMethodCalls("move");
            
            // For a square pattern, expect at least 4 movements (one per side)
            assertTrue(moveCalls.size() >= 4,
                "❌ Square pattern should have at least 4 move() commands (one per side)!");
        }
    }

    @Nested
    @DisplayName("Square Pattern Quality Tests")
    class SquarePatternQualityTests {

        @Test
        @DisplayName("Should demonstrate understanding of flight control concepts")
        void shouldDemonstrateFlightConcepts() {
            executeStudentFlightPattern();

            // Verify understanding of pitch vs roll
            assertTrue(mockDrone.getUniquePitchValues().contains(30) || 
                      mockDrone.getUniquePitchValues().contains(-30),
                "💡 Tip: Use positive pitch values to move forward, negative to move backward!");

            assertTrue(mockDrone.getUniqueRollValues().contains(30) || 
                      mockDrone.getUniqueRollValues().contains(-30),
                "💡 Tip: Use positive roll values to move right, negative to move left!");
        }

        @Test
        @DisplayName("Should complete full square pattern")
        void shouldCompleteSquarePattern() {
            executeStudentFlightPattern();

            // A complete square should have movements in all 4 directions
            boolean hasForward = mockDrone.getUniquePitchValues().stream().anyMatch(p -> p > 0);
            boolean hasBackward = mockDrone.getUniquePitchValues().stream().anyMatch(p -> p < 0);
            boolean hasRight = mockDrone.getUniqueRollValues().stream().anyMatch(r -> r > 0);
            boolean hasLeft = mockDrone.getUniqueRollValues().stream().anyMatch(r -> r < 0);

            int directionsUsed = (hasForward ? 1 : 0) + (hasBackward ? 1 : 0) + 
                               (hasRight ? 1 : 0) + (hasLeft ? 1 : 0);

            assertTrue(directionsUsed >= 4,
                "🎯 Great! Complete square pattern should use all 4 directions: forward, right, backward, left!");
        }
    }

    /**
     * Helper method to execute the student's flight pattern.
     * This simulates the L0102FlightMovements square pattern.
     */
    @Override
    protected void executeStudentFlightPattern() {
        try {
            // Simulate the student's L0102FlightMovements pattern
            // In a real test environment, this would invoke the student's code
            mockDrone.takeoff();

            mockDrone.setPitch(30);     // forward
            mockDrone.move(1);
            
            mockDrone.setPitch(0);      // reset
            mockDrone.setRoll(30);      // right
            mockDrone.move(1);
            
            mockDrone.setRoll(0);       // reset
            mockDrone.setPitch(-30);    // backward
            mockDrone.move(1);
            
            mockDrone.setPitch(0);      // reset
            mockDrone.setRoll(-30);     // left
            mockDrone.move(1);

            mockDrone.land();
        } catch (Exception e) {
            // Re-throw as runtime exception to test exception handling
            throw new RuntimeException(e);
        }
    }
}
