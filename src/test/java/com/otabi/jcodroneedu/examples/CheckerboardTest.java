package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.display.DisplayPixel;

/**
 * Simple checkerboard test using individual controllerDrawPoint commands.
 * This helps diagnose whether basic display communication is working.
 */
public class CheckerboardTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // Clear screen first
            System.out.println("Clearing screen...");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            // Draw a checkerboard pattern using individual points
            System.out.println("Drawing checkerboard pattern using controllerDrawPoint()...");
            System.out.println("This uses individual draw commands (like Python implementation)");
            
            long startTime = System.currentTimeMillis();
            int pointCount = 0;
            
            // Draw checkerboard: alternate black/white in 8-pixel squares
            for (int y = 0; y < 64; y += 8) {
                for (int x = 0; x < 128; x += 8) {
                    // Calculate which square this is
                    int squareX = x / 8;
                    int squareY = y / 8;
                    
                    // Checkerboard pattern: alternating black/white
                    if ((squareX + squareY) % 2 == 0) {
                        // Draw 8x8 square of black pixels
                        for (int py = y; py < y + 8; py++) {
                            for (int px = x; px < x + 8; px++) {
                                drone.controllerDrawPoint(px, py, DisplayPixel.BLACK);
                                pointCount++;
                                
                                // Progress indicator every 100 points
                                if (pointCount % 100 == 0) {
                                    System.out.print(".");
                                }
                            }
                        }
                    }
                    // White squares: skip (already white from clear)
                }
            }
            
            long endTime = System.currentTimeMillis();
            long elapsedMs = endTime - startTime;
            
            System.out.println();
            System.out.println("Sent " + pointCount + " points in " + elapsedMs + "ms");
            System.out.println("Average: " + (elapsedMs / (double)pointCount) + "ms per point");
            System.out.println();
            System.out.println("Screen should now show a checkerboard pattern (black and white squares)");
            System.out.println("If you see nothing or just white, communication may have an issue.");
            System.out.println("\nTest complete!");
        }
    }
}
