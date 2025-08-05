package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.display.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for controller display functionality.
 * Verifies that all display methods can be called without errors
 * and that the basic functionality works correctly.
 */
public class ControllerDisplayTest {
    
    private Drone drone;

    @BeforeEach
    void setUp() {
        drone = new Drone();
    }

    @AfterEach
    void tearDown() {
        if (drone != null) {
            drone.close();
        }
    }

    @Test
    void testControllerClearScreen() {
        // Test clear screen with default white
        assertDoesNotThrow(() -> drone.controller_clear_screen());
        
        // Test clear screen with specific pixel types
        assertDoesNotThrow(() -> drone.controller_clear_screen(DisplayPixel.WHITE));
        assertDoesNotThrow(() -> drone.controller_clear_screen(DisplayPixel.BLACK));
    }

    @Test
    void testControllerDrawPoint() {
        // Test draw point with default black
        assertDoesNotThrow(() -> drone.controller_draw_point(10, 10));
        
        // Test draw point with specific pixel types
        assertDoesNotThrow(() -> drone.controller_draw_point(20, 20, DisplayPixel.BLACK));
        assertDoesNotThrow(() -> drone.controller_draw_point(30, 30, DisplayPixel.WHITE));
        assertDoesNotThrow(() -> drone.controller_draw_point(40, 40, DisplayPixel.INVERSE));
        
        // Test boundary coordinates
        assertDoesNotThrow(() -> drone.controller_draw_point(0, 0));
        assertDoesNotThrow(() -> drone.controller_draw_point(127, 63));
    }

    @Test
    void testControllerDrawLine() {
        // Test draw line with defaults
        assertDoesNotThrow(() -> drone.controller_draw_line(0, 0, 50, 50));
        
        // Test draw line with specific parameters
        assertDoesNotThrow(() -> drone.controller_draw_line(10, 10, 60, 30, DisplayPixel.BLACK, DisplayLine.SOLID));
        assertDoesNotThrow(() -> drone.controller_draw_line(20, 20, 70, 40, DisplayPixel.WHITE, DisplayLine.DOTTED));
        assertDoesNotThrow(() -> drone.controller_draw_line(30, 30, 80, 50, DisplayPixel.INVERSE, DisplayLine.DASHED));
    }

    @Test
    void testControllerDrawRectangle() {
        // Test draw rectangle with defaults
        assertDoesNotThrow(() -> drone.controller_draw_rectangle(10, 10, 30, 20));
        
        // Test draw rectangle with specific parameters
        assertDoesNotThrow(() -> drone.controller_draw_rectangle(20, 15, 40, 25, DisplayPixel.BLACK, false, DisplayLine.SOLID));
        assertDoesNotThrow(() -> drone.controller_draw_rectangle(30, 20, 50, 30, DisplayPixel.WHITE, true, DisplayLine.DOTTED));
    }

    @Test
    void testControllerDrawCircle() {
        // Test draw circle with defaults
        assertDoesNotThrow(() -> drone.controller_draw_circle(50, 30, 15));
        
        // Test draw circle with specific parameters
        assertDoesNotThrow(() -> drone.controller_draw_circle(60, 40, 20, DisplayPixel.BLACK, true));
        assertDoesNotThrow(() -> drone.controller_draw_circle(70, 50, 10, DisplayPixel.WHITE, false));
    }

    @Test
    void testControllerDrawString() {
        // Test draw string with defaults
        assertDoesNotThrow(() -> drone.controller_draw_string(5, 5, "Hello"));
        
        // Test draw string with specific parameters
        assertDoesNotThrow(() -> drone.controller_draw_string(10, 15, "Test", DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.BLACK));
        assertDoesNotThrow(() -> drone.controller_draw_string(20, 25, "CoDrone", DisplayFont.LIBERATION_MONO_10X16, DisplayPixel.WHITE));
        
        // Test empty string
        assertDoesNotThrow(() -> drone.controller_draw_string(30, 35, ""));
        
        // Test null string handling
        assertDoesNotThrow(() -> drone.controller_draw_string(40, 45, null));
    }

    @Test
    void testControllerClearArea() {
        // Test clear area with defaults
        assertDoesNotThrow(() -> drone.controller_clear_area(10, 10, 20, 15));
        
        // Test clear area with specific pixel
        assertDoesNotThrow(() -> drone.controller_clear_area(30, 30, 40, 25, DisplayPixel.WHITE));
        assertDoesNotThrow(() -> drone.controller_clear_area(50, 20, 30, 20, DisplayPixel.BLACK));
    }

    @Test
    void testControllerInvertArea() {
        // Test invert area
        assertDoesNotThrow(() -> drone.controller_invert_area(10, 10, 30, 20));
        assertDoesNotThrow(() -> drone.controller_invert_area(0, 0, 127, 63)); // Full screen
    }

    @Test
    void testDisplayEnums() {
        // Test DisplayPixel enum values
        assertEquals((byte) 0x00, DisplayPixel.BLACK.value());
        assertEquals((byte) 0x01, DisplayPixel.WHITE.value());
        assertEquals((byte) 0x02, DisplayPixel.INVERSE.value());
        assertEquals((byte) 0x03, DisplayPixel.OUTLINE.value());
        
        // Test DisplayFont enum values
        assertEquals((byte) 0x00, DisplayFont.LIBERATION_MONO_5X8.value());
        assertEquals((byte) 0x01, DisplayFont.LIBERATION_MONO_10X16.value());
        
        // Test DisplayLine enum values
        assertEquals((byte) 0x00, DisplayLine.SOLID.value());
        assertEquals((byte) 0x01, DisplayLine.DOTTED.value());
        assertEquals((byte) 0x02, DisplayLine.DASHED.value());
        
        // Test DisplayAlign enum values
        assertEquals((byte) 0x00, DisplayAlign.LEFT.value());
        assertEquals((byte) 0x01, DisplayAlign.CENTER.value());
        assertEquals((byte) 0x02, DisplayAlign.RIGHT.value());
    }

    @Test
    void testDisplayEnumFromByte() {
        // Test DisplayPixel.fromByte()
        assertEquals(DisplayPixel.BLACK, DisplayPixel.fromByte((byte) 0x00));
        assertEquals(DisplayPixel.WHITE, DisplayPixel.fromByte((byte) 0x01));
        
        // Test DisplayFont.fromByte()
        assertEquals(DisplayFont.LIBERATION_MONO_5X8, DisplayFont.fromByte((byte) 0x00));
        assertEquals(DisplayFont.LIBERATION_MONO_10X16, DisplayFont.fromByte((byte) 0x01));
        
        // Test DisplayLine.fromByte()
        assertEquals(DisplayLine.SOLID, DisplayLine.fromByte((byte) 0x00));
        assertEquals(DisplayLine.DOTTED, DisplayLine.fromByte((byte) 0x01));
        
        // Test invalid byte values throw exceptions
        assertThrows(IllegalArgumentException.class, () -> DisplayPixel.fromByte((byte) 0xFF));
        assertThrows(IllegalArgumentException.class, () -> DisplayFont.fromByte((byte) 0xFF));
        assertThrows(IllegalArgumentException.class, () -> DisplayLine.fromByte((byte) 0xFF));
        assertThrows(IllegalArgumentException.class, () -> DisplayAlign.fromByte((byte) 0xFF));
    }
}
