package com.otabi.jcodroneedu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base class for flight-related drone tests. 
 * Includes common tests for takeoff, landing, safety, and basic flight operations.
 */
public abstract class DroneFlightTest extends DroneTest {

    /**
     * Subclasses must implement this method to execute their specific flight pattern.
     */
    protected abstract void executeStudentFlightPattern();

    /**
     * Implementation of the base class method - delegates to flight pattern execution.
     */
    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }

    @Nested
    @DisplayName("Basic Flight Operations")
    class BasicFlightTests {

        @Test
        @DisplayName("Should use takeoff() and land() commands")
        void shouldUseTakeoffAndLandCommands() {
            executeStudentFlightPattern();

            // Verify basic flight lifecycle
            assertTrue(mockDrone.wasTakeoffCalled(), 
                "❌ Missing takeoff() command - drone must take off before flying!");
            assertTrue(mockDrone.wasLandCalled(), 
                "❌ Missing land() command - drone must land after flying!");
            
            // Verify takeoff comes before landing
            int takeoffIndex = mockDrone.getCommandIndex("takeoff");
            int landIndex = mockDrone.getCommandIndex("land");
            assertTrue(takeoffIndex < landIndex, 
                "❌ takeoff() must be called before land()!");
        }
    }

    @Nested
    @DisplayName("Safety and Best Practice Tests")
    class SafetyTests {

        @Test
        @DisplayName("Should not use potentially unsafe commands")
        void shouldNotUseUnsafeCommands() {
            executeStudentFlightPattern();

            // Check for potentially unsafe commands that students might accidentally use
            List<String> allCommands = mockDrone.getAllCommands();
            
            for (String command : allCommands) {
                assertFalse(command.contains("setThrottle"), 
                    "⚠️  Avoid using setThrottle() - use setPitch/setRoll for directional movement!");
                assertFalse(command.contains("setYaw") && !command.contains("0"), 
                    "⚠️  Be careful with setYaw() - focus on pitch/roll for basic movement!");
            }
        }

        @Test
        @DisplayName("Should use reasonable pitch/roll values")
        void shouldUseReasonableValues() {
            executeStudentFlightPattern();

            // Check that pitch and roll values are within reasonable ranges
            for (Integer pitch : mockDrone.getUniquePitchValues()) {
                assertTrue(Math.abs(pitch) <= 100, 
                    "⚠️  Pitch value " + pitch + " might be too extreme! Consider values between -50 and 50.");
            }

            for (Integer roll : mockDrone.getUniqueRollValues()) {
                assertTrue(Math.abs(roll) <= 100, 
                    "⚠️  Roll value " + roll + " might be too extreme! Consider values between -50 and 50.");
            }
        }
    }

    @Nested
    @DisplayName("Movement Command Tests")
    class MovementTests {

        @Test
        @DisplayName("Should use move() command to execute movements")
        void shouldUseMoveCommand() {
            executeStudentFlightPattern();

            List<String> moveCalls = mockDrone.getMethodCalls("move");
            assertFalse(moveCalls.isEmpty(), 
                "❌ Must use move() command to execute the flight movements!");
        }

        @Test
        @DisplayName("Should maintain logical movement sequence")
        void shouldMaintainLogicalSequence() {
            executeStudentFlightPattern();

            // Verify that setPitch/setRoll calls are followed by move() calls
            List<String> allCommands = mockDrone.getAllCommands();
            
            boolean hasProperSequence = false;
            for (int i = 0; i < allCommands.size() - 1; i++) {
                String current = allCommands.get(i);
                String next = allCommands.get(i + 1);
                
                if ((current.startsWith("setPitch") || current.startsWith("setRoll")) && 
                    next.startsWith("move")) {
                    hasProperSequence = true;
                    break;
                }
            }

            assertTrue(hasProperSequence,
                "❌ setPitch() or setRoll() commands should be followed by move() to execute the movement!");
        }
    }
}
