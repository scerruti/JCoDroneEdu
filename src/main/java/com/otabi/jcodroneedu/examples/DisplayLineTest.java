package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DisplayController;
import java.awt.Color;

/**
 * Test to understand the display byte packing by drawing a simple horizontal line.
 */
public class DisplayLineTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            System.out.println("Connected to drone");
            
            // Test 1: Horizontal line at y=0 (top row)
            System.out.println("\n=== Test 1: Horizontal line at y=0 ===");
            testHorizontalLine(drone, 0, "Top row (y=0)");
            
            // Test 2: Horizontal line at y=8 (second row of bytes)
            System.out.println("\n=== Test 2: Horizontal line at y=8 ===");
            testHorizontalLine(drone, 8, "Second byte row (y=8)");
            
            // Test 3: Single pixel at (0,0)
            System.out.println("\n=== Test 3: Single pixel at (0,0) ===");
            testSinglePixel(drone, 0, 0, "Pixel at (0,0)");
            
            // Test 4: Single pixel at (10,0)
            System.out.println("\n=== Test 4: Single pixel at (10,0) ===");
            testSinglePixel(drone, 10, 0, "Pixel at (10,0)");
            
            System.out.println("\nAll tests complete!");
        }
    }
    
    private static void testHorizontalLine(Drone drone, int y, String description) throws InterruptedException {
        DisplayController canvas = drone.controllerCreateCanvas();
        canvas.setColor(Color.BLACK);
        canvas.drawLine(0, y, 127, y);  // Draw across entire width
        
        System.out.println("Drawing: " + description);
        drone.controllerDrawCanvas(canvas);
        Thread.sleep(1500);
    }
    
    private static void testSinglePixel(Drone drone, int x, int y, String description) throws InterruptedException {
        DisplayController canvas = drone.controllerCreateCanvas();
        canvas.setColor(Color.BLACK);
        // Draw two pixels to make it visible
        int[] xpoints = {x, x+1};
        int[] ypoints = {y, y};
        
        System.out.println("Drawing: " + description);
        canvas.getGraphics().drawPolyline(xpoints, ypoints, 2);
        drone.controllerDrawCanvas(canvas);
        Thread.sleep(1500);
    }
}
