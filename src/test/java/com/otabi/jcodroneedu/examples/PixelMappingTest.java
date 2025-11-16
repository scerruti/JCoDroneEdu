package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.awt.*;

/**
 * Test: Verify pixel mapping - draw a specific pattern to understand the byte layout.
 */
public class PixelMappingTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            
            // Test 1: Single black pixel at (0,0)
            System.out.println("Test 1: Single pixel at (0,0)");
            testSinglePixel(drone, 0, 0);
            Thread.sleep(3000);
            
            // Test 2: Column of pixels
            System.out.println("Test 2: Vertical line at x=10");
            testVerticalLine(drone, 10);
            Thread.sleep(3000);
            
            // Test 3: Horizontal line
            System.out.println("Test 3: Horizontal line at y=32");
            testHorizontalLine(drone, 32);
            Thread.sleep(3000);
            
            System.out.println("Done!");
        }
    }
    
    private static void testSinglePixel(Drone drone, int x, int y) throws Exception {
        var canvas = drone.controllerCreateCanvas();
        var graphics = canvas.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(x, y, 1, 1);
        
        drone.controllerDrawCanvas(canvas);
    }
    
    private static void testVerticalLine(Drone drone, int x) throws Exception {
        var canvas = drone.controllerCreateCanvas();
        var graphics = canvas.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.drawLine(x, 0, x, 63);
        
        drone.controllerDrawCanvas(canvas);
    }
    
    private static void testHorizontalLine(Drone drone, int y) throws Exception {
        var canvas = drone.controllerCreateCanvas();
        var graphics = canvas.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, y, 127, y);
        
        drone.controllerDrawCanvas(canvas);
    }
}
