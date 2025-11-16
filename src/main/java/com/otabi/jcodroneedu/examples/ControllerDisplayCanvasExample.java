package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DisplayController;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Example demonstrating the DisplayController API for drawing on the controller display.
 * 
 * <p>This example shows two approaches:</p>
 * <ul>
 *   <li><strong>Simple API:</strong> Using wrapper methods for basic shapes</li>
 *   <li><strong>Advanced API:</strong> Using Graphics2D for complex graphics</li>
 * </ul>
 * 
 * <p><strong>Performance Benefit:</strong> Drawing on a canvas and sending once is much more
 * efficient than calling individual draw methods, as it reduces network communication to a
 * single batch update.</p>
 * 
 * @author Educational Platform
 * @version 1.0
 */
public class ControllerDisplayCanvasExample {

    /**
     * Demonstrates the simple drawing API using wrapper methods.
     * This approach is ideal for beginners learning basic programming concepts.
     */
    private static void demonstrateSimpleAPI(Drone drone) {
        System.out.println("=== Simple Drawing API ===");
        
        // Create a canvas
        DisplayController canvas = drone.controllerCreateCanvas();
        
        // Set color to black
        canvas.setColor(Color.BLACK);
        
        // Draw some shapes using simple API
        canvas.fillRectangle(10, 10, 30, 20);      // Filled rectangle
        canvas.drawRectangle(50, 10, 30, 20);      // Rectangle outline
        
        canvas.fillCircle(20, 50, 8);              // Filled circle
        canvas.drawCircle(50, 50, 8);              // Circle outline
        
        canvas.drawLine(70, 10, 120, 55);          // Diagonal line
        
        // Send to display as a single batch
        drone.controllerDrawCanvas(canvas);
        
        System.out.println("Simple drawing complete!");
    }

    /**
     * Demonstrates the advanced Graphics2D API for complex graphics.
     * This approach teaches students about the real Java graphics library.
     */
    private static void demonstrateAdvancedAPI(Drone drone) {
        System.out.println("=== Advanced Graphics2D API ===");
        
        // Create a canvas
        DisplayController canvas = drone.controllerCreateCanvas();
        
        // Get the underlying Graphics2D object
        Graphics2D g = canvas.getGraphics();
        
        // Draw complex shapes using Graphics2D
        g.setColor(Color.BLACK);
        
        // Draw a triangle using a polygon
        int[] xPoints = {40, 60, 50};
        int[] yPoints = {10, 10, 30};
        g.fillPolygon(xPoints, yPoints, 3);
        
        // Draw an arc
        g.drawArc(80, 10, 30, 30, 0, 180);
        
        // Draw multiple rectangles (nested)
        for (int i = 0; i < 3; i++) {
            int x = 20 + (i * 10);
            int y = 50 + (i * 5);
            int size = 30 - (i * 8);
            g.drawRect(x, y, size, size);
        }
        
        // Send to display
        drone.controllerDrawCanvas(canvas);
        
        System.out.println("Advanced drawing complete!");
    }

    /**
     * Demonstrates mixing simple and advanced APIs on the same canvas.
     * This shows the flexibility of the hybrid approach.
     */
    private static void demonstrateMixedAPI(Drone drone) {
        System.out.println("=== Mixed Simple and Advanced API ===");
        
        // Create a canvas
        DisplayController canvas = drone.controllerCreateCanvas();
        
        // Use simple API
        canvas.setColor(Color.BLACK);
        canvas.drawLine(0, 32, 128, 32);  // Horizontal line across middle
        
        // Switch to Graphics2D for complex operation
        Graphics2D g = canvas.getGraphics();
        g.setColor(Color.BLACK);
        
        // Draw grid pattern using Graphics2D
        for (int x = 0; x < 128; x += 16) {
            g.drawLine(x, 0, x, 64);
        }
        for (int y = 0; y < 64; y += 16) {
            g.drawLine(0, y, 128, y);
        }
        
        // Back to simple API for clarity
        canvas.setColor(Color.BLACK);
        canvas.fillCircle(64, 32, 5);  // Center dot
        
        // Send to display
        drone.controllerDrawCanvas(canvas);
        
        System.out.println("Mixed drawing complete!");
    }

    /**
     * Demonstrates clearing and redrawing the canvas.
     * Shows how to update the display with new content.
     */
    private static void demonstrateUpdate(Drone drone) {
        System.out.println("=== Canvas Update Example ===");
        
        // First drawing
        DisplayController canvas = drone.controllerCreateCanvas();
        canvas.setColor(Color.BLACK);
        canvas.fillRectangle(0, 0, 64, 64);
        drone.controllerDrawCanvas(canvas);
        
        sleep(2000);  // Wait 2 seconds
        
        // Create new canvas for second drawing
        canvas = drone.controllerCreateCanvas();
        canvas.setColor(Color.BLACK);
        canvas.drawRectangle(20, 10, 40, 30);
        canvas.drawCircle(100, 30, 10);
        drone.controllerDrawCanvas(canvas);
        
        System.out.println("Update complete!");
    }

    /**
     * Demonstrates performance benefits of canvas batching.
     * Shows the difference between individual calls and canvas batching.
     */
    private static void demonstratePerformance(Drone drone) {
        System.out.println("=== Performance Comparison ===");
        
        // Method 1: Individual draw calls (slower - multiple network messages)
        System.out.println("Method 1: Individual draw calls");
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            drone.controllerDrawPoint(i * 10, i * 5);
            drone.controllerDrawLine(i * 10, 0, i * 10, 64);
        }
        long duration1 = System.currentTimeMillis() - start1;
        System.out.println("  Time: " + duration1 + "ms");
        
        sleep(1000);
        
        // Method 2: Canvas batching (faster - single network message)
        System.out.println("Method 2: Canvas batching");
        long start2 = System.currentTimeMillis();
        DisplayController canvas = drone.controllerCreateCanvas();
        canvas.setColor(Color.BLACK);
        for (int i = 0; i < 10; i++) {
            canvas.drawLine(i * 10, 0, i * 10, 64);
        }
        drone.controllerDrawCanvas(canvas);
        long duration2 = System.currentTimeMillis() - start2;
        System.out.println("  Time: " + duration2 + "ms");
        
        System.out.println("Canvas batching is faster for complex drawings!");
    }

    /**
     * Main method to run the examples.
     */
    public static void main(String[] args) {
        try (Drone drone = new Drone()) {
            drone.pair();
            
            // Run examples
            demonstrateSimpleAPI(drone);
            sleep(2000);
            
            demonstrateAdvancedAPI(drone);
            sleep(2000);
            
            demonstrateMixedAPI(drone);
            sleep(2000);
            
            demonstrateUpdate(drone);
            sleep(2000);
            
            demonstratePerformance(drone);
            
            System.out.println("\nAll examples complete!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to sleep for a given number of milliseconds.
     */
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
