package com.otabi.jcodroneedu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Manages graphics and drawing operations on the controller display screen.
 * 
 * <p>This class provides two levels of API:</p>
 * <ul>
 *   <li><strong>Simple API:</strong> Wrapper methods for common shapes (rectangles, circles, lines)</li>
 *   <li><strong>Advanced API:</strong> Direct access to Java's Graphics2D for complex graphics</li>
 * </ul>
 * 
 * <p><strong>Typical Usage:</strong></p>
 * <pre>{@code
 * // Create a canvas
 * DisplayController canvas = drone.controllerCreateCanvas();
 * 
 * // Draw using simple API
 * canvas.setColor(Color.BLACK);
 * canvas.drawRectangle(20, 30, 40, 10);
 * canvas.drawCircle(80, 40, 15);
 * canvas.drawLine(10, 10, 50, 50);
 * 
 * // Send to display
 * drone.controllerDrawCanvas(canvas);
 * }</pre>
 * 
 * <p><strong>Advanced Usage (Graphics2D):</strong></p>
 * <pre>{@code
 * DisplayController canvas = drone.controllerCreateCanvas();
 * Graphics2D g = canvas.getGraphics();
 * 
 * // Use standard Java Graphics2D operations
 * g.setColor(Color.BLACK);
 * g.fillPolygon(xpoints, ypoints, 3);  // Triangle
 * g.drawArc(50, 50, 30, 30, 0, 180);
 * 
 * drone.controllerDrawCanvas(canvas);
 * }</pre>
 * 
 * <p><strong>Display Specifications:</strong></p>
 * <ul>
 *   <li>Resolution: 128×64 pixels</li>
 *   <li>Color: Monochrome (black and white only)</li>
 *   <li>Coordinate system: (0,0) at top-left, x increases right, y increases down</li>
 * </ul>
 * 
 * @see java.awt.Graphics2D
 * @see java.awt.image.BufferedImage
 * @educational
 */
public class DisplayController {
    
    /** Display width in pixels */
    public static final int DISPLAY_WIDTH = 128;
    
    /** Display height in pixels */
    public static final int DISPLAY_HEIGHT = 64;
    
    private BufferedImage image;
    private Graphics2D graphics;
    private Color currentColor;

    /**
     * Creates a new display canvas with the correct dimensions for the controller display.
     * The canvas is initialized with a white background.
     */
    public DisplayController() {
        this.image = new BufferedImage(DISPLAY_WIDTH, DISPLAY_HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        this.graphics = image.createGraphics();
        this.currentColor = Color.BLACK;
        
        // Set up graphics for better drawing quality
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        
        // Initialize with white background
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        graphics.setColor(currentColor);
    }

    /**
     * Sets the current drawing color for subsequent drawing operations.
     * Only black and white are supported on the monochrome display.
     * 
     * @param color The color to use for drawing (typically Color.BLACK or Color.WHITE)
     * @educational
     */
    public void setColor(Color color) {
        this.currentColor = color;
        graphics.setColor(color);
    }

    /**
     * Gets the current drawing color.
     * 
     * @return The current color
     */
    public Color getColor() {
        return currentColor;
    }

    /**
     * Draws a rectangle on the canvas.
     * 
     * @param x X coordinate of top-left corner
     * @param y Y coordinate of top-left corner
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @educational
     */
    public void drawRectangle(int x, int y, int width, int height) {
        graphics.drawRect(x, y, width, height);
    }

    /**
     * Draws a filled rectangle on the canvas.
     * 
     * @param x X coordinate of top-left corner
     * @param y Y coordinate of top-left corner
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @educational
     */
    public void fillRectangle(int x, int y, int width, int height) {
        graphics.fillRect(x, y, width, height);
    }

    /**
     * Draws a circle (oval) on the canvas.
     * 
     * @param x X coordinate of center
     * @param y Y coordinate of center
     * @param radius Radius of circle
     * @educational
     */
    public void drawCircle(int x, int y, int radius) {
        int diameter = radius * 2;
        graphics.drawOval(x - radius, y - radius, diameter, diameter);
    }

    /**
     * Draws a filled circle (oval) on the canvas.
     * 
     * @param x X coordinate of center
     * @param y Y coordinate of center
     * @param radius Radius of circle
     * @educational
     */
    public void fillCircle(int x, int y, int radius) {
        int diameter = radius * 2;
        graphics.fillOval(x - radius, y - radius, diameter, diameter);
    }

    /**
     * Draws a line on the canvas.
     * 
     * @param x1 Starting X coordinate
     * @param y1 Starting Y coordinate
     * @param x2 Ending X coordinate
     * @param y2 Ending Y coordinate
     * @educational
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x1, y1, x2, y2);
    }

    /**
     * Clears the canvas, filling it with white.
     * 
     * @educational
     */
    public void clear() {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        graphics.setColor(currentColor);
    }

    /**
     * Gets the underlying Graphics2D object for advanced drawing operations.
     * This allows access to the full Java Graphics2D API for complex graphics.
     * 
     * <p><strong>Advanced Usage:</strong></p>
     * <pre>{@code
     * Graphics2D g = canvas.getGraphics();
     * g.setStroke(new BasicStroke(2));
     * g.drawArc(50, 50, 30, 30, 0, 180);
     * }</pre>
     * 
     * @return The Graphics2D object for this canvas
     * @see java.awt.Graphics2D
     */
    public Graphics2D getGraphics() {
        return graphics;
    }

    /**
     * Gets the underlying BufferedImage containing the canvas bitmap.
     * 
     * @return The BufferedImage used for rendering
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Gets the width of the display canvas.
     * 
     * @return Width in pixels (128)
     */
    public int getWidth() {
        return DISPLAY_WIDTH;
    }

    /**
     * Gets the height of the display canvas.
     * 
     * @return Height in pixels (64)
     */
    public int getHeight() {
        return DISPLAY_HEIGHT;
    }

    /**
     * Converts the canvas bitmap to a byte array in bit-packed format.
     * This is used internally to send the canvas to the drone display.
     * Format: Column-by-column, with each byte representing 8 vertical pixels.
     * Byte layout: bit 0 = top pixel, bit 7 = bottom pixel of 8-pixel column.
     * 
     * @return Byte array containing the canvas pixel data
     */
    public byte[] toByteArray() {
        byte[] data = new byte[(DISPLAY_WIDTH * DISPLAY_HEIGHT) / 8];
        int dataIndex = 0;
        
        // Pack row-by-row (not column-by-column):
        // Bytes 0-15: pixels for rows 0-7, columns 0-127 (16 bytes per row group)
        // Bytes 16-31: pixels for rows 8-15, columns 0-127
        // etc.
        // Each byte represents 8 vertical pixels in a single column within that row group.
        //
        // For TYPE_BYTE_BINARY with Graphics2D:
        //   - Black (drawn): RGB value is 0xFF000000 (-16777216 in signed int)
        //   - White (background): RGB value is 0xFFFFFFFF (-1 in signed int)
        
        for (int y = 0; y < DISPLAY_HEIGHT; y += 8) {
            for (int x = 0; x < DISPLAY_WIDTH; x++) {
                byte pixelByte = 0;
                for (int bit = 0; bit < 8 && (y + bit) < DISPLAY_HEIGHT; bit++) {
                    // Get pixel value from image
                    int rgb = image.getRGB(x, y + bit);
                    // Check if pixel is black: 0xFF000000 = -16777216
                    if (rgb == 0xFF000000 || rgb == -16777216) {
                        pixelByte |= (1 << bit);
                    }
                }
                data[dataIndex++] = pixelByte;
            }
        }
        
        return data;
    }
}
