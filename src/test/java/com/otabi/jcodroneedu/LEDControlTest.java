package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.Header;
import com.otabi.jcodroneedu.protocol.Serializable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LED control functionality.
 * 
 * Verifies that all LED methods work correctly and provide proper educational
 * functionality for classroom use including identification, debugging, and engagement.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LED Control Methods")
class LEDControlTest {

    @Mock
    private SerialPortManager mockSerialPortManager;
    
    private Drone drone;

    @BeforeEach
    void setUp() {
        // Create a real Drone instance with mocked serial communication
        drone = new Drone() {
            @Override
            void transfer(Header header, Serializable data) {
                // Mock the transfer method to avoid actual serial communication
                // In real implementation, this would send data to the drone
            }
        };
    }

    @Nested
    @DisplayName("Basic LED Methods")
    class BasicLEDTests {

        @Test
        @DisplayName("Should set drone LED with RGBA values")
        void shouldSetDroneLEDWithRGBA() {
            // Test basic LED setting - should not throw exceptions
            assertDoesNotThrow(() -> drone.setDroneLED(255, 0, 0, 100),
                "Setting drone LED with valid RGBA values should not throw exception");
            
            assertDoesNotThrow(() -> drone.setDroneLED(0, 255, 0, 255),
                "Setting drone LED with full brightness should not throw exception");
        }

        @Test
        @DisplayName("Should set drone LED with RGB values (full brightness)")
        void shouldSetDroneLEDWithRGB() {
            assertDoesNotThrow(() -> drone.setDroneLED(0, 0, 255),
                "Setting drone LED with RGB values should not throw exception");
        }

        @Test
        @DisplayName("Should validate LED parameter ranges")
        void shouldValidateLEDParameterRanges() {
            // Test invalid red value
            assertThrows(IllegalArgumentException.class, () -> drone.setDroneLED(-1, 0, 0, 100),
                "Negative red value should throw IllegalArgumentException");
            
            assertThrows(IllegalArgumentException.class, () -> drone.setDroneLED(256, 0, 0, 100),
                "Red value > 255 should throw IllegalArgumentException");
            
            // Test invalid green value
            assertThrows(IllegalArgumentException.class, () -> drone.setDroneLED(0, -1, 0, 100),
                "Negative green value should throw IllegalArgumentException");
            
            assertThrows(IllegalArgumentException.class, () -> drone.setDroneLED(0, 256, 0, 100),
                "Green value > 255 should throw IllegalArgumentException");
            
            // Test invalid blue value
            assertThrows(IllegalArgumentException.class, () -> drone.setDroneLED(0, 0, -1, 100),
                "Negative blue value should throw IllegalArgumentException");
            
            assertThrows(IllegalArgumentException.class, () -> drone.setDroneLED(0, 0, 256, 100),
                "Blue value > 255 should throw IllegalArgumentException");
            
            // Test invalid brightness value
            assertThrows(IllegalArgumentException.class, () -> drone.setDroneLED(0, 0, 0, -1),
                "Negative brightness should throw IllegalArgumentException");
            
            assertThrows(IllegalArgumentException.class, () -> drone.setDroneLED(0, 0, 0, 256),
                "Brightness > 255 should throw IllegalArgumentException");
        }

        @Test
        @DisplayName("Should turn off drone LED")
        void shouldTurnOffDroneLED() {
            assertDoesNotThrow(() -> drone.droneLEDOff(),
                "Turning off drone LED should not throw exception");
        }
    }

    @Nested
    @DisplayName("LED Mode Methods")
    class LEDModeTests {

        @Test
        @DisplayName("Should set LED mode with valid parameters")
        void shouldSetLEDModeWithValidParameters() {
            // Test all valid modes
            String[] validModes = {"solid", "dimming", "fade_in", "fade_out", "blink", "double_blink", "rainbow"};
            
            for (String mode : validModes) {
                assertDoesNotThrow(() -> drone.setDroneLEDMode(255, 255, 255, mode, 5),
                    "Setting LED mode '" + mode + "' should not throw exception");
            }
        }

        @Test
        @DisplayName("Should validate LED mode parameters")
        void shouldValidateLEDModeParameters() {
            // Test invalid mode
            assertThrows(IllegalArgumentException.class, 
                () -> drone.setDroneLEDMode(255, 0, 0, "invalid_mode", 5),
                "Invalid mode should throw IllegalArgumentException");
            
            // Test null mode
            assertThrows(IllegalArgumentException.class, 
                () -> drone.setDroneLEDMode(255, 0, 0, null, 5),
                "Null mode should throw IllegalArgumentException");
            
            // Test invalid speed values
            assertThrows(IllegalArgumentException.class, 
                () -> drone.setDroneLEDMode(255, 0, 0, "blink", 0),
                "Speed 0 should throw IllegalArgumentException");
            
            assertThrows(IllegalArgumentException.class, 
                () -> drone.setDroneLEDMode(255, 0, 0, "blink", 11),
                "Speed > 10 should throw IllegalArgumentException");
        }

        @Test
        @DisplayName("Should handle case-insensitive mode names")
        void shouldHandleCaseInsensitiveModes() {
            assertDoesNotThrow(() -> drone.setDroneLEDMode(255, 0, 0, "BLINK", 5),
                "Uppercase mode should work");
            
            assertDoesNotThrow(() -> drone.setDroneLEDMode(255, 0, 0, "Fade_In", 5),
                "Mixed case mode should work");
        }
    }

    @Nested
    @DisplayName("Controller LED Methods")
    class ControllerLEDTests {

        @Test
        @DisplayName("Should set controller LED with RGBA values")
        void shouldSetControllerLEDWithRGBA() {
            assertDoesNotThrow(() -> drone.setControllerLED(0, 255, 0, 100),
                "Setting controller LED should not throw exception");
        }

        @Test
        @DisplayName("Should set controller LED with RGB values")
        void shouldSetControllerLEDWithRGB() {
            assertDoesNotThrow(() -> drone.setControllerLED(255, 0, 255),
                "Setting controller LED with RGB should not throw exception");
        }

        @Test
        @DisplayName("Should turn off controller LED")
        void shouldTurnOffControllerLED() {
            assertDoesNotThrow(() -> drone.controllerLEDOff(),
                "Turning off controller LED should not throw exception");
        }
    }

    @Nested
    @DisplayName("Educational Helper Methods")
    class EducationalHelperTests {

        @Test
        @DisplayName("Should provide simple color methods for young students")
        void shouldProvideSimpleColorMethods() {
            // Test all the simple color helper methods
            assertDoesNotThrow(() -> drone.setDroneLEDRed(),
                "Red helper method should work");
            
            assertDoesNotThrow(() -> drone.setDroneLEDGreen(),
                "Green helper method should work");
            
            assertDoesNotThrow(() -> drone.setDroneLEDBlue(),
                "Blue helper method should work");
            
            assertDoesNotThrow(() -> drone.setDroneLEDYellow(),
                "Yellow helper method should work");
            
            assertDoesNotThrow(() -> drone.setDroneLEDPurple(),
                "Purple helper method should work");
            
            assertDoesNotThrow(() -> drone.setDroneLEDWhite(),
                "White helper method should work");
            
            assertDoesNotThrow(() -> drone.setDroneLEDOrange(),
                "Orange helper method should work");
        }
    }

    @Nested
    @DisplayName("Educational Integration")
    class EducationalIntegrationTests {

        @Test
        @DisplayName("Should support classroom identification scenarios")
        void shouldSupportClassroomIdentification() {
            // Simulate different students setting their drone colors
            assertDoesNotThrow(() -> {
                drone.setDroneLEDRed();        // Student 1
                Thread.sleep(10);
                drone.setDroneLEDBlue();       // Student 2  
                Thread.sleep(10);
                drone.setDroneLEDGreen();      // Student 3
            }, "Classroom identification scenario should work");
        }

        @Test
        @DisplayName("Should support debugging with visual feedback")
        void shouldSupportDebuggingWithVisualFeedback() {
            // Simulate debugging scenario with LED feedback
            assertDoesNotThrow(() -> {
                drone.setDroneLED(255, 255, 0);     // Yellow for "searching"
                Thread.sleep(10);
                drone.setDroneLEDMode(0, 255, 0, "blink", 8);  // Green blink for "found"
                Thread.sleep(10);
                drone.setDroneLED(255, 0, 0);       // Red for "error"
                Thread.sleep(10);
                drone.droneLEDOff();                 // Clear state
            }, "Debugging scenario should work");
        }

        @Test
        @DisplayName("Should support creative programming projects")
        void shouldSupportCreativeProgrammingProjects() {
            // Simulate creative light show
            assertDoesNotThrow(() -> {
                drone.setDroneLEDMode(255, 0, 0, "fade_in", 3);     // Red fade in
                Thread.sleep(10);
                drone.setDroneLEDMode(0, 255, 0, "fade_out", 3);    // Green fade out
                Thread.sleep(10);
                drone.setDroneLEDMode(0, 0, 255, "double_blink", 7); // Blue double blink
                Thread.sleep(10);
                drone.setDroneLEDMode(255, 255, 255, "rainbow", 10); // Rainbow
            }, "Creative programming scenario should work");
        }
    }
}
