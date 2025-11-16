package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.display.DisplayPixel;
import java.util.Scanner;

/**
 * Minimal test: Send a single DisplayDrawImage (0x88) message manually.
 * This helps us understand exactly how the protocol works and where it fails.
 */
public class SingleMessageTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // Clear screen first
            System.out.println("Stage 1: Clearing screen...");
            drone.controllerClearScreen();
            Thread.sleep(1000);
            System.out.println("Screen should be white.");
            System.out.print("Press ENTER to continue to Stage 2...");
            scanner.nextLine();
            System.out.println();
            
            // Create minimal image data: a simple 8x8 pattern at top-left
            // We'll create 8 bytes of data (8 rows × 1 byte wide)
            byte[] imageData = new byte[8];
            // Row 0: 11110000 = 0xF0 (draw 4 pixels)
            imageData[0] = (byte) 0xF0;
            // Row 1: 11110000 = 0xF0
            imageData[1] = (byte) 0xF0;
            // Row 2: 11110000 = 0xF0
            imageData[2] = (byte) 0xF0;
            // Row 3: 11110000 = 0xF0
            imageData[3] = (byte) 0xF0;
            // Row 4: 00001111 = 0x0F (draw 4 pixels at bottom)
            imageData[4] = (byte) 0x0F;
            // Row 5: 00001111 = 0x0F
            imageData[5] = (byte) 0x0F;
            // Row 6: 00001111 = 0x0F
            imageData[6] = (byte) 0x0F;
            // Row 7: 00001111 = 0x0F
            imageData[7] = (byte) 0x0F;
            
            System.out.println("Stage 2: Sending single DisplayDrawImage message...");
            System.out.println("  Position: x=0, y=0");
            System.out.println("  Size: width=8, height=8");
            System.out.println("  Data: 8 bytes");
            System.out.println("  Expected: Small 8x8 square pattern in top-left corner");
            
            long startTime = System.currentTimeMillis();
            drone.controllerDrawImage(0, 0, 8, 8, imageData);
            long endTime = System.currentTimeMillis();
            
            System.out.println("Message sent in " + (endTime - startTime) + "ms");
            System.out.print("Do you see the 8x8 pattern in the top-left? Press ENTER to continue...");
            scanner.nextLine();
            System.out.println();
            
            // Try sending another command to verify port is still open
            System.out.println("Stage 3: Verifying port still open by drawing a point...");
            try {
                drone.controllerDrawPoint(50, 50, DisplayPixel.BLACK);
                System.out.println("Port is open! Point command succeeded.");
            } catch (Exception e) {
                System.out.println("Port error: " + e.getMessage());
            }
            System.out.print("Did you see a point at (50,50)? Press ENTER to continue...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 4: Clearing screen...");
            drone.controllerClearScreen();
            Thread.sleep(500);
            System.out.print("Screen should be white again. Press ENTER to finish...");
            scanner.nextLine();
            
            System.out.println("\nTest complete!");
        }
    }
}
