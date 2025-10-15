package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.cardreader.CardColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for color sensor functionality in the CoDrone EDU Java API.
 * Tests Python API compatibility for color detection methods.
 */
@DisplayName("Color Sensor Tests")
class ColorSensorTest {

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
    @DisplayName("Color Data Tests")
    class ColorDataTests {

        @Test
        @DisplayName("get_color_data() should return zeros when initialized")
        void testGetColorDataNoData() {
            runWithDrone(drone -> {
                // CardColor is now initialized with NONE values (matches Python behavior)
                int[][] result = drone.getColorData();

                // Should return zero HSVL values (initialized state)
                assertNotNull(result, "getColorData() should return initialized data");
                assertEquals(2, result.length, "Should have 2 sensors");
                assertArrayEquals(new int[]{0, 0, 0, 0}, result[0], "Front sensor should be all zeros");
                assertArrayEquals(new int[]{0, 0, 0, 0}, result[1], "Back sensor should be all zeros");
            });
        }

        @Test
        @DisplayName("get_color_data() should return HSV data when available")
        void testGetColorDataWithData() {
            // Create mock color data
            byte[][] hsvl = new byte[][]{
                {(byte) 120, (byte) 255, (byte) 200, (byte) 128}, // Front sensor: H=120, S=255, V=200, L=128
                {(byte) 240, (byte) 128, (byte) 100, (byte) 64}   // Back sensor: H=240, S=128, V=100, L=64
            };
            byte[] colors = {2, 4}; // RED front, GREEN back
            byte card = 1;
            
        runWithDrone(drone -> {
            CardColor cardColor = new CardColor(hsvl, colors, card);
            droneStatus.setCardColor(cardColor);

            // Test the method
            int[][] result = drone.getColorData();

            // Should return converted HSV data
            assertNotNull(result, "getColorData() should not return null when data is available");
            assertEquals(2, result.length, "Should return data for 2 sensors");

            // Check front sensor data (index 0)
            assertArrayEquals(new int[]{120, 255, 200, 128}, result[0], 
                "Front sensor HSV data should match");
                
            // Check back sensor data (index 1)
            assertArrayEquals(new int[]{240, 128, 100, 64}, result[1], 
                "Back sensor HSV data should match");
        });
        }

        @Test
        @DisplayName("get_color_data() should handle unsigned byte conversion correctly")
        void testGetColorDataUnsignedByteConversion() {
            // Create color data with values that would be negative as signed bytes
            byte[][] hsvl = new byte[][]{
                {(byte) 200, (byte) 255, (byte) 128, (byte) 64}, // 200 = -56 as signed byte
                {(byte) 180, (byte) 220, (byte) 160, (byte) 32}
            };
            byte[] colors = {1, 3};
            byte card = 2;
            
        runWithDrone(drone -> {
            CardColor cardColor = new CardColor(hsvl, colors, card);
            droneStatus.setCardColor(cardColor);

            // Test the method
            int[][] result = drone.getColorData();

            // Should properly convert unsigned bytes to positive integers
            assertNotNull(result);
            assertEquals(200, result[0][0], "Should convert 200 (unsigned byte) correctly");
            assertEquals(255, result[0][1], "Should convert 255 (unsigned byte) correctly");
        });
        }
    }

    @Nested
    @DisplayName("Color Detection Tests")
    class ColorDetectionTests {

        @Test
        @DisplayName("get_colors() should return NONE (0) when initialized")
        void testGetColorsNoData() {
            runWithDrone(drone -> {
                // CardColor is now initialized with NONE values (matches Python behavior)
                int[] result = drone.getColors();

                // Should return NONE (0) for both sensors
                assertNotNull(result, "getColors() should return initialized data");
                assertEquals(2, result.length, "Should return 2 color values");
                assertEquals(0, result[0], "Front sensor should be NONE (0)");
                assertEquals(0, result[1], "Back sensor should be NONE (0)");
            });
        }

        @Test
        @DisplayName("get_colors() should return front and back color values")
        void testGetColorsWithData() {
            // Create mock color data
            byte[][] hsvl = new byte[2][4]; // Doesn't matter for this test
            byte[] colors = {2, 4}; // RED front, GREEN back
            byte card = 1;
            
            runWithDrone(drone -> {
                CardColor cardColor = new CardColor(hsvl, colors, card);
                droneStatus.setCardColor(cardColor);

                // Test the method
                int[] result = drone.getColors();

                // Should return color values
                assertNotNull(result, "get_colors() should not return null when data is available");
                assertEquals(2, result.length, "Should return 2 color values");
                assertEquals(2, result[0], "Front sensor should detect RED (2)");
                assertEquals(4, result[1], "Back sensor should detect GREEN (4)");
            });
        }

        @Test
        @DisplayName("get_colors() should handle unsigned byte conversion for color values")
        void testGetColorsUnsignedConversion() {
            // Create color data with high color values
            byte[][] hsvl = new byte[2][4];
            byte[] colors = {(byte) 200, (byte) 255}; // High values that would be negative as signed
            byte card = 1;
            
            runWithDrone(drone -> {
                CardColor cardColor = new CardColor(hsvl, colors, card);
                droneStatus.setCardColor(cardColor);

                // Test the method
                int[] result = drone.getColors();

                // Should properly convert unsigned bytes
                assertNotNull(result);
                assertEquals(200, result[0], "Should convert 200 correctly");
                assertEquals(255, result[1], "Should convert 255 correctly");
            });
        }
    }

    @Nested
    @DisplayName("Individual Color Sensor Tests")
    class IndividualColorSensorTests {

        @Test
        @DisplayName("get_front_color() should return NONE (0) when initialized")
        void testGetFrontColorNoData() {
            runWithDrone(drone -> {
                // CardColor is now initialized with NONE values (matches Python behavior)
                int result = drone.getFrontColor();

                // Should return NONE (0)
                assertEquals(0, result, "get_front_color() should return NONE (0) when initialized");
            });
        }

        @Test
        @DisplayName("get_front_color() should return front sensor color value")
        void testGetFrontColorWithData() {
            // Create mock color data
            byte[][] hsvl = new byte[2][4];
            byte[] colors = {6, 2}; // BLUE front, RED back
            byte card = 1;
            
            runWithDrone(drone -> {
                CardColor cardColor = new CardColor(hsvl, colors, card);
                droneStatus.setCardColor(cardColor);

                // Test the method
                int result = drone.getFrontColor();

                // Should return front color
                assertEquals(6, result, "get_front_color() should return BLUE (6)");
            });
        }

        @Test
        @DisplayName("get_back_color() should return NONE (0) when initialized")
        void testGetBackColorNoData() {
            runWithDrone(drone -> {
                // CardColor is now initialized with NONE values (matches Python behavior)
                int result = drone.getBackColor();

                // Should return NONE (0)
                assertEquals(0, result, "get_back_color() should return NONE (0) when initialized");
            });
        }

        @Test
        @DisplayName("get_back_color() should return back sensor color value")
        void testGetBackColorWithData() {
            // Create mock color data
            byte[][] hsvl = new byte[2][4];
            byte[] colors = {6, 2}; // BLUE front, RED back
            byte card = 1;
            
            runWithDrone(drone -> {
                CardColor cardColor = new CardColor(hsvl, colors, card);
                droneStatus.setCardColor(cardColor);

                // Test the method
                int result = drone.getBackColor();

                // Should return back color
                assertEquals(2, result, "get_back_color() should return RED (2)");
            });
        }
    }

    @Nested
    @DisplayName("Educational Color Value Tests")
    class EducationalColorValueTests {

        @Test
        @DisplayName("Color values should match DroneSystem.CardColorIndex constants")
        void testColorValueConstants() {
            // Test all standard color values match expected constants
            assertEquals(0, DroneSystem.CardColorIndex.UNKNOWN.getValue());
            assertEquals(1, DroneSystem.CardColorIndex.WHITE.getValue());
            assertEquals(2, DroneSystem.CardColorIndex.RED.getValue());
            assertEquals(3, DroneSystem.CardColorIndex.YELLOW.getValue());
            assertEquals(4, DroneSystem.CardColorIndex.GREEN.getValue());
            assertEquals(5, DroneSystem.CardColorIndex.CYAN.getValue());
            assertEquals(6, DroneSystem.CardColorIndex.BLUE.getValue());
            assertEquals(7, DroneSystem.CardColorIndex.MAGENTA.getValue());
            assertEquals(8, DroneSystem.CardColorIndex.BLACK.getValue());
        }

        @Test
        @DisplayName("Should handle all standard color values correctly")
        void testAllStandardColorValues() {
            // Test each standard color value
            for (DroneSystem.CardColorIndex colorIndex : DroneSystem.CardColorIndex.values()) {
                if (colorIndex == DroneSystem.CardColorIndex.END_OF_TYPE) continue;
                
                // Create color data with this color on front sensor
                byte[][] hsvl = new byte[2][4];
                byte[] colors = {(byte) colorIndex.getValue(), 0};
                byte card = 1;
                
                runWithDrone(drone -> {
                    CardColor cardColor = new CardColor(hsvl, colors, card);
                    droneStatus.setCardColor(cardColor);

                    // Test the method
                    int result = drone.getFrontColor();

                    // Should return the correct color value
                    assertEquals(colorIndex.getValue(), result,
                        "Color sensor should correctly detect " + colorIndex.name());
                });
            }
        }
    }

    @Nested
    @DisplayName("API Compatibility Tests")
    class APICompatibilityTests {

        @Test
        @DisplayName("Methods should match Python API naming conventions")
        void testPythonAPICompatibility() {
            runWithDrone(drone -> {
                // These methods should exist and follow Python naming (snake_case)
                assertDoesNotThrow(() -> drone.getColorData(),
                    "get_color_data() should exist for Python compatibility");
                assertDoesNotThrow(() -> drone.getColors(),
                    "get_colors() should exist for Python compatibility");
                assertDoesNotThrow(() -> drone.getFrontColor(),
                    "get_front_color() should exist for Python compatibility");
                assertDoesNotThrow(() -> drone.getBackColor(),
                    "get_back_color() should exist for Python compatibility");
            });
        }

        @Test
        @DisplayName("Return types should be appropriate for educational use")
        void testEducationalReturnTypes() {
            // Methods should return int arrays for easier student use
            // (as opposed to byte arrays which are more complex)
            
            // Create test data
            byte[][] hsvl = new byte[][]{
                {(byte) 100, (byte) 200, (byte) 150, (byte) 75},
                {(byte) 50, (byte) 100, (byte) 250, (byte) 25}
            };
            byte[] colors = {3, 7}; // YELLOW, MAGENTA
            runWithDrone(drone -> {
                CardColor cardColor = new CardColor(hsvl, colors, (byte) 1);
                droneStatus.setCardColor(cardColor);

                // Test return types
                int[][] colorData = drone.getColorData();
                assertNotNull(colorData);
                assertTrue(colorData[0] instanceof int[], "HSV data should be int arrays for educational use");

                int[] colors_result = drone.getColors();
                assertNotNull(colors_result);
                assertTrue(colors_result instanceof int[], "Color values should be int array for educational use");

                int frontColor = drone.getFrontColor();
                assertTrue(frontColor >= -1, "Individual color should be int value for educational use");
            });
        }
    }
}
