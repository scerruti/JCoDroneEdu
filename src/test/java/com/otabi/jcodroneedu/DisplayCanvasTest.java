package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.display.DisplayCanvas;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DisplayCanvas functionality.
 * Verifies both the simple drawing API and Graphics2D integration.
 */
public class DisplayCanvasTest {

    @Test
    void testCanvasCreation() {
        DisplayCanvas canvas = new DisplayCanvas();
        
        assertEquals(DisplayCanvas.DISPLAY_WIDTH, canvas.getWidth());
        assertEquals(DisplayCanvas.DISPLAY_HEIGHT, canvas.getHeight());
        assertEquals(128, canvas.getWidth());
        assertEquals(64, canvas.getHeight());
    }

    @Test
    void testCanvasDefaultColor() {
        DisplayCanvas canvas = new DisplayCanvas();
        
        assertEquals(Color.BLACK, canvas.getColor());
    }

    @Test
    void testSetColor() {
        DisplayCanvas canvas = new DisplayCanvas();
        
        canvas.setColor(Color.WHITE);
        assertEquals(Color.WHITE, canvas.getColor());
        
        canvas.setColor(Color.BLACK);
        assertEquals(Color.BLACK, canvas.getColor());
    }

    @Test
    void testDrawRectangle() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        
        // Should not throw exception
        canvas.drawRectangle(10, 10, 20, 20);
    }

    @Test
    void testFillRectangle() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        
        // Should not throw exception
        canvas.fillRectangle(10, 10, 20, 20);
    }

    @Test
    void testDrawCircle() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        
        // Should not throw exception
        canvas.drawCircle(64, 32, 15);
    }

    @Test
    void testFillCircle() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        
        // Should not throw exception
        canvas.fillCircle(64, 32, 15);
    }

    @Test
    void testDrawLine() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        
        // Should not throw exception
        canvas.drawLine(0, 0, 127, 63);
    }

    @Test
    void testClear() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        canvas.fillRectangle(0, 0, 128, 64);
        
        // Clear should reset to white background
        canvas.clear();
        
        // After clear, we should be able to draw again
        canvas.setColor(Color.BLACK);
        canvas.drawLine(0, 0, 10, 10);
    }

    @Test
    void testGetGraphics() {
        DisplayCanvas canvas = new DisplayCanvas();
        Graphics2D g = canvas.getGraphics();
        
        assertNotNull(g);
        
        // Test that we can use Graphics2D
        g.setColor(Color.BLACK);
        g.drawOval(50, 30, 20, 20);
    }

    @Test
    void testGetImage() {
        DisplayCanvas canvas = new DisplayCanvas();
        
        assertNotNull(canvas.getImage());
        assertEquals(DisplayCanvas.DISPLAY_WIDTH, canvas.getImage().getWidth());
        assertEquals(DisplayCanvas.DISPLAY_HEIGHT, canvas.getImage().getHeight());
    }

    @Test
    void testToByteArray() {
        DisplayCanvas canvas = new DisplayCanvas();
        
        byte[] data = canvas.toByteArray();
        
        // Should be (128 * 64) / 8 = 1024 bytes
        assertEquals(1024, data.length);
    }

    @Test
    void testToByteArrayWithDrawing() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        canvas.fillRectangle(0, 0, 128, 8);  // Fill first row of pixels
        
        byte[] data = canvas.toByteArray();
        
        assertEquals(1024, data.length);
        // First row should have non-zero values (pixels were drawn)
        int firstRowSum = 0;
        for (int i = 0; i < 128 / 8; i++) {
            firstRowSum += data[i] & 0xFF;
        }
        assertTrue(firstRowSum > 0, "First row should contain drawn pixels");
    }

    @Test
    void testComplexDrawing() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        
        // Draw multiple shapes
        canvas.fillRectangle(10, 10, 30, 20);
        canvas.drawCircle(80, 30, 10);
        canvas.drawLine(0, 0, 127, 63);
        
        // Should be able to get byte array after complex drawing
        byte[] data = canvas.toByteArray();
        assertEquals(1024, data.length);
    }

    @Test
    void testGraphics2DIntegration() {
        DisplayCanvas canvas = new DisplayCanvas();
        Graphics2D g = canvas.getGraphics();
        
        // Use Graphics2D to draw a filled polygon
        int[] xPoints = {10, 40, 25};
        int[] yPoints = {10, 10, 30};
        
        g.setColor(Color.BLACK);
        g.fillPolygon(xPoints, yPoints, 3);
        
        // Canvas should be drawable after Graphics2D operations
        byte[] data = canvas.toByteArray();
        assertEquals(1024, data.length);
    }

    @Test
    void testMultipleOperations() {
        DisplayCanvas canvas = new DisplayCanvas();
        
        // Simple API
        canvas.setColor(Color.BLACK);
        canvas.drawRectangle(5, 5, 20, 20);
        
        // Graphics2D API
        Graphics2D g = canvas.getGraphics();
        g.setColor(Color.WHITE);
        g.drawOval(60, 30, 15, 15);
        
        // Back to simple API
        canvas.setColor(Color.BLACK);
        canvas.drawLine(50, 50, 100, 60);
        
        // Should have valid byte array
        byte[] data = canvas.toByteArray();
        assertEquals(1024, data.length);
    }

    @Test
    void testClearResetsColor() {
        DisplayCanvas canvas = new DisplayCanvas();
        
        canvas.setColor(Color.WHITE);
        assertEquals(Color.WHITE, canvas.getColor());
        
        canvas.clear();
        
        // Color should be maintained after clear
        assertEquals(Color.WHITE, canvas.getColor());
    }

    @Test
    void testBoundaryDrawing() {
        DisplayCanvas canvas = new DisplayCanvas();
        canvas.setColor(Color.BLACK);
        
        // Draw at boundaries
        canvas.drawLine(0, 0, 127, 63);           // Corner to corner
        canvas.drawRectangle(0, 0, 127, 63);      // Full size rectangle
        canvas.drawCircle(0, 0, 5);               // Circle at origin
        canvas.drawCircle(127, 63, 5);            // Circle at far corner
        
        // Should successfully convert to byte array
        byte[] data = canvas.toByteArray();
        assertEquals(1024, data.length);
    }
}
