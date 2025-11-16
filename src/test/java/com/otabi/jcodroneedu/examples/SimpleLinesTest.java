package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.display.DisplayPixel;

/**
 * Simple line drawing test - draws horizontal lines.
 * Much faster than checkerboard since it uses fewer points.
 */
public class SimpleLinesTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // Clear screen
            System.out.println("Stage 1: Clearing screen (white)...");
            drone.controllerClearScreen();
            Thread.sleep(500);
            System.out.println("Screen should be blank/white.\n");
            Thread.sleep(2000);
            
            // Draw a few horizontal lines using individual points
            System.out.println("Stage 2: Drawing horizontal lines using controllerDrawPoint...");
            long startTime = System.currentTimeMillis();
            int pointCount = 0;
            
            // Draw 4 horizontal lines
            int[] yPositions = {10, 20, 30, 40};
            for (int y : yPositions) {
                System.out.println("  Drawing line at y=" + y);
                for (int x = 0; x < 128; x++) {
                    drone.controllerDrawPoint(x, y, DisplayPixel.BLACK);
                    pointCount++;
                }
            }
            
            long endTime = System.currentTimeMillis();
            System.out.println("Sent " + pointCount + " points in " + (endTime - startTime) + "ms");
            System.out.println("Screen should now show 4 horizontal black lines.\n");
            Thread.sleep(2000);
            
            // Clear again
            System.out.println("Stage 3: Clearing screen again...");
            drone.controllerClearScreen();
            Thread.sleep(500);
            System.out.println("Screen should be blank/white again.\n");
            
            System.out.println("Test complete!");
        }
    }
}
