package com.otabi.jcodroneedu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Example unit tests for a basic sensor reading assignment (non-flight).
 * Demonstrates how non-flight assignments also get connection validation automatically.
 */
class BasicSensorTest extends DroneTest {

    @Nested
    @DisplayName("Sensor Reading Tests")
    class SensorTests {

        @Test
        @DisplayName("Should read battery level")
        void shouldReadBatteryLevel() {
            executeStudentDroneOperations();

            List<String> batteryCalls = mockDrone.getMethodCalls("getBattery");
            assertFalse(batteryCalls.isEmpty(), 
                "❌ Should read battery level using getBattery()!");
        }

        @Test
        @DisplayName("Should not use flight commands for sensor reading")
        void shouldNotUseFlightCommands() {
            executeStudentDroneOperations();

            List<String> allCommands = mockDrone.getAllCommands();
            
            for (String command : allCommands) {
                assertFalse(command.startsWith("takeoff"), 
                    "⚠️  This is a sensor reading assignment - no takeoff needed!");
                assertFalse(command.startsWith("land"), 
                    "⚠️  This is a sensor reading assignment - no landing needed!");
                assertFalse(command.startsWith("move"), 
                    "⚠️  This is a sensor reading assignment - no movement needed!");
            }
        }
    }

    /**
     * Example implementation of a basic sensor reading pattern.
     * This demonstrates non-flight drone operations.
     */
    @Override
    protected void executeStudentDroneOperations() {
        try {
            // Simulate basic sensor reading assignment
            mockDrone.connect();           // Connect to drone
            mockDrone.getBattery();        // Read battery level
            mockDrone.getTemperature();    // Read temperature
            mockDrone.close();             // Clean up
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
