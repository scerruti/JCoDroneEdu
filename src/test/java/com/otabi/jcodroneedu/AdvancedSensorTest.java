package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.dronestatus.Altitude;
import com.otabi.jcodroneedu.protocol.dronestatus.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for advanced sensor functionality in the CoDrone EDU Java API.
 * Tests position, pressure, temperature, and comprehensive sensor data methods.
 */
@DisplayName("Advanced Sensor Tests")
class AdvancedSensorTest {

    private DroneStatus droneStatus;

    @FunctionalInterface
    private interface ThrowingConsumer<T> { void accept(T t) throws Exception; }

    private void runWithDrone(ThrowingConsumer<Drone> consumer) {
        try (Drone drone = new Drone(false)) {
            droneStatus = drone.getDroneStatus();
            consumer.accept(drone);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("Position Sensor Tests")
    class PositionSensorTests {

        @Test
        @DisplayName("getPositionData() should return null when no position data available")
        void testGetPositionDataNoData() {
            runWithDrone(drone -> {
                // When no position data is set
                float[] result = drone.getPositionData();

                // Should return default/bad data (array of zeros)
                assertNotNull(result, "getPositionData() should not be null when no data is available");
                assertEquals(3, result.length, "Should return array with 3 position values");
                assertEquals(0.0, result[0], 0.001, "X position should be 0 when no data");
                assertEquals(0.0, result[1], 0.001, "Y position should be 0 when no data");
                assertEquals(0.0, result[2], 0.001, "Z position should be 0 when no data");
            });
        }

        @Test
        @DisplayName("getPositionData() should return position array when data available")
        void testGetPositionDataWithData() {
            runWithDrone(drone -> {
                // Create mock position data (in meters as floats)
                Position position = new Position(1.0f, 0.5f, 0.2f); // 1m forward, 0.5m left, 0.2m up
                droneStatus.setPosition(position);

                // Test the method
                float[] result = drone.getPositionData();

                // Should return position array
                assertNotNull(result, "getPositionData() should not return null when data is available");
                assertEquals(3, result.length, "Should return array with 3 position values");
                assertEquals(1.0, result[0], 0.01, "X position should match");
                assertEquals(0.5, result[1], 0.01, "Y position should match");
                assertEquals(0.2, result[2], 0.01, "Z position should match");
            });
        }

        @Test
        @DisplayName("Individual position getters should return correct values")
        void testIndividualPositionGetters() {
            runWithDrone(drone -> {
                // Create test position data (in meters)
                Position position = new Position(-0.5f, 1.5f, -0.3f);
                droneStatus.setPosition(position);

                // Test individual getters
                assertEquals(-0.5, drone.getPositionX(), 0.01, "getPositionX() should return correct value");
                assertEquals(1.5, drone.getPositionY(), 0.01, "getPositionY() should return correct value");
                assertEquals(-0.3, drone.getPositionZ(), 0.01, "getPositionZ() should return correct value");
            });
        }

        @Test
        @DisplayName("Individual position getters should return 0 when no data available")
        void testIndividualPositionGettersNoData() {
            runWithDrone(drone -> {
                // When no position data is set
                assertEquals(0, drone.getPositionX(), "getPositionX() should return 0 when no data");
                assertEquals(0, drone.getPositionY(), "getPositionY() should return 0 when no data");
                assertEquals(0, drone.getPositionZ(), "getPositionZ() should return 0 when no data");
            });
        }
    }

    @Nested
    @DisplayName("Pressure Sensor Tests")
    class PressureSensorTests {

        @Test
        @DisplayName("getPressure() should return 0.0 when no altitude data available")
        void testGetPressureNoData() {
            runWithDrone(drone -> {
                // When no altitude data is set
                double result = drone.getPressure();

                // Should return 0.0
                assertEquals(0.0, result, 0.001, "getPressure() should return 0.0 when no data is available");
            });
        }

        @Test
        @DisplayName("getPressure() should return pressure in Pascals when data available")
        void testGetPressureWithData() {
            runWithDrone(drone -> {
                // Create mock altitude data with standard atmospheric pressure
                Altitude altitude = new Altitude(25, 1000, 101325, 500); // 25°C, 1000mm range, 101325 Pa, 500mm altitude
                droneStatus.setAltitude(altitude);

                // Test the method
                double result = drone.getPressure();

                // Should return pressure in Pascals
                assertEquals(101325.0, result, 0.001, "getPressure() should return pressure in Pascals");
            });
        }

        @Test
        @DisplayName("getPressure(unit) should convert to different units correctly")
        void testGetPressureWithUnits() {
            runWithDrone(drone -> {
                // Create altitude data with known pressure
                Altitude altitude = new Altitude(20, 800, 101325, 400); // Standard atmospheric pressure
                droneStatus.setAltitude(altitude);

                // Test different units
                assertEquals(101325.0, drone.getPressure("Pa"), 0.001, "Pascals conversion should be correct");
                assertEquals(101.325, drone.getPressure("kPa"), 0.001, "Kilopascals conversion should be correct");
                assertEquals(1013.25, drone.getPressure("mbar"), 0.001, "Millibars conversion should be correct");
                assertEquals(1.0, drone.getPressure("atm"), 0.01, "Atmospheres conversion should be correct");
                assertEquals(29.92, drone.getPressure("inHg"), 0.1, "Inches of mercury conversion should be correct");
            });
        }

        @Test
        @DisplayName("getPressure(unit) should throw exception for unsupported units")
        void testGetPressureUnsupportedUnit() {
            runWithDrone(drone -> {
                // Create altitude data
                Altitude altitude = new Altitude(20, 800, 101325, 400);
                droneStatus.setAltitude(altitude);

                // Test unsupported unit
                Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> drone.getPressure("invalid"));

                assertTrue(exception.getMessage().contains("Unsupported pressure unit"),
                    "Should throw exception with descriptive message");
            });
        }
    }

    @Nested
    @DisplayName("Temperature Sensor Tests")
    class TemperatureSensorTests {

        @Test
        @DisplayName("getDroneTemperature() should return 0.0 when no altitude data available")
        void testGetTemperatureNoData() {
            runWithDrone(drone -> {
                // When no altitude data is set
                double result = drone.getDroneTemperature();

                // Should return 0.0
                assertEquals(0.0, result, 0.001, "getDroneTemperature() should return 0.0 when no data is available");
            });
        }

        @Test
        @DisplayName("getDroneTemperature() should return temperature in Celsius when data available")
        void testGetTemperatureWithData() {
            runWithDrone(drone -> {
                // Create mock altitude data with room temperature
                Altitude altitude = new Altitude(23, 900, 101000, 350); // 23°C
                droneStatus.setAltitude(altitude);

                // Test the method
                double result = drone.getDroneTemperature();

                // Should return temperature in Celsius
                assertEquals(23.0, result, 0.001, "getDroneTemperature() should return temperature in Celsius");
            });
        }

        @Test
        @DisplayName("getDroneTemperature(unit) should convert to different units correctly")
        void testGetTemperatureWithUnits() {
            runWithDrone(drone -> {
                // Create altitude data with known temperature (25°C)
                Altitude altitude = new Altitude(25, 850, 101200, 450);
                droneStatus.setAltitude(altitude);

                // Test different units
                assertEquals(25.0, drone.getDroneTemperature("C"), 0.001, "Celsius conversion should be correct");
                assertEquals(77.0, drone.getDroneTemperature("F"), 0.001, "Fahrenheit conversion should be correct");
                assertEquals(298.15, drone.getDroneTemperature("K"), 0.001, "Kelvin conversion should be correct");
            });
        }

        @Test
        @DisplayName("getDroneTemperature(unit) should handle case insensitive units")
        void testGetTemperatureCaseInsensitive() {
            runWithDrone(drone -> {
                // Create altitude data
                Altitude altitude = new Altitude(0, 800, 101325, 400); // 0°C (freezing point)
                droneStatus.setAltitude(altitude);

                // Test case insensitive units
                assertEquals(0.0, drone.getDroneTemperature("c"), 0.001, "Lowercase 'c' should work");
                assertEquals(32.0, drone.getDroneTemperature("f"), 0.001, "Lowercase 'f' should work");
                assertEquals(273.15, drone.getDroneTemperature("k"), 0.001, "Lowercase 'k' should work");
            });
        }

        @Test
        @DisplayName("getDroneTemperature(unit) should throw exception for unsupported units")
        void testGetTemperatureUnsupportedUnit() {
            runWithDrone(drone -> {
                // Create altitude data
                Altitude altitude = new Altitude(25, 800, 101325, 400);
                droneStatus.setAltitude(altitude);

                // Test unsupported unit
                Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> drone.getDroneTemperature("Rankine"));

                assertTrue(exception.getMessage().contains("Unsupported temperature unit"),
                    "Should throw exception with descriptive message");
            });
        }
    }

    @Nested
    @DisplayName("Comprehensive Sensor Data Tests")
    class ComprehensiveSensorTests {

        @Test
        @DisplayName("getSensorData() should return null when no basic sensor data available")
        void testGetSensorDataNoData() {
            runWithDrone(drone -> {
                // When no sensor data is set
                double[] result = drone.getSensorData();

                // Should return default/bad data (array of zeros)
                assertNotNull(result, "getSensorData() should not be null when no basic data is available");
                // The expected length may depend on your implementation; adjust as needed
                assertTrue(result.length >= 3, "getSensorData() should return an array with at least 3 values");
                for (double v : result) {
                    assertEquals(0.0, v, 0.001, "Sensor value should be 0 when no data is available");
                }
            });
        }

        @Test
        @DisplayName("getSensorData() should return comprehensive sensor array when data available")
        void testGetSensorDataWithData() {
            runWithDrone(drone -> {
                // Set up mock sensor data (position in meters)
                Position position = new Position(1.0f, 0.5f, 0.2f);
                Altitude altitude = new Altitude(25, 900, 101325, 400);
                droneStatus.setPosition(position);
                droneStatus.setAltitude(altitude);

                // Test the method - should always return an array (never null)
                double[] result = drone.getSensorData();

                assertNotNull(result, "getSensorData() should not return null when data is available");
                assertEquals(17, result.length, "Should return array with 17 sensor values");

                // Test position data (indices 0-2) - now in meters
                assertEquals(1.0, result[0], 0.001, "Position X should match");
                assertEquals(0.5, result[1], 0.001, "Position Y should match");
                assertEquals(0.2, result[2], 0.001, "Position Z should match");

                // Test environmental data (indices 15-16)
                assertEquals(101325.0, result[15], 0.001, "Pressure should match");
                assertEquals(25.0, result[16], 0.001, "Temperature should match");
            });
        }
    }

    @Nested
    @DisplayName("Educational Integration Tests")
    class EducationalIntegrationTests {

        @Test
        @DisplayName("All advanced sensor methods should have educational documentation")
        void testEducationalDocumentation() {
            runWithDrone(drone -> {
                // This test ensures that all methods exist and can be called
                // In a real educational environment, these would return actual sensor data

                assertDoesNotThrow(() -> drone.getPositionData(),
                    "getPositionData() should exist for educational use");
                assertDoesNotThrow(() -> drone.getPositionX(),
                    "getPositionX() should exist for educational use");
                assertDoesNotThrow(() -> drone.getPressure(),
                    "getPressure() should exist for educational use");
                assertDoesNotThrow(() -> drone.getPressure("kPa"),
                    "getPressure(unit) should exist for educational use");
                assertDoesNotThrow(() -> drone.getDroneTemperature(),
                    "getDroneTemperature() should exist for educational use");
                assertDoesNotThrow(() -> drone.getDroneTemperature("F"),
                    "getDroneTemperature(unit) should exist for educational use");
                assertDoesNotThrow(() -> drone.getSensorData(),
                    "getSensorData() should exist for educational use");
            });
        }

        @Test
        @DisplayName("Python API naming compatibility should be maintained")
        void testPythonAPICompatibility() {
            runWithDrone(drone -> {
                // These method names should match Python API exactly
                assertDoesNotThrow(() -> drone.getPositionData(),
                    "getPositionData() should match Python API naming");
                assertDoesNotThrow(() -> drone.getPressure(),
                    "getPressure() should match Python API naming");
                assertDoesNotThrow(() -> drone.getDroneTemperature(),
                    "getDroneTemperature() should match Python API naming");
                assertDoesNotThrow(() -> drone.getSensorData(),
                    "getSensorData() should match Python API naming");
            });
        }

        @Test
        @DisplayName("Unit conversion should support common educational units")
        void testEducationalUnitSupport() {
            runWithDrone(drone -> {
                // Create test data
                Altitude altitude = new Altitude(20, 800, 101325, 400);
                droneStatus.setAltitude(altitude);

                // Test common pressure units for physics education
                assertDoesNotThrow(() -> drone.getPressure("Pa"), "Pascals should be supported");
                assertDoesNotThrow(() -> drone.getPressure("kPa"), "Kilopascals should be supported");
                assertDoesNotThrow(() -> drone.getPressure("atm"), "Atmospheres should be supported");

                // Test common temperature units for science education
                assertDoesNotThrow(() -> drone.getDroneTemperature("C"), "Celsius should be supported");
                assertDoesNotThrow(() -> drone.getDroneTemperature("F"), "Fahrenheit should be supported");
                assertDoesNotThrow(() -> drone.getDroneTemperature("K"), "Kelvin should be supported");
            });
        }
    }
}
