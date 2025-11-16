package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.util.Scanner;

/**
 * Test: Verify that 0x88 replaces the entire display.
 * Try sending full 1024 bytes at once (the entire canvas).
 */
public class FullCanvasTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            System.out.println("Stage 1: Clear screen");
            drone.controllerClearScreen();
            Thread.sleep(500);
            System.out.print("Screen blank? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Create a full 1024-byte canvas with black at top and middle
            System.out.println("Stage 2: Create full 1024-byte canvas with black lines at top and middle");
            byte[] fullCanvas = new byte[1024];
            
            // First 128 bytes (y=0-7): all 0xFF (black)
            for (int i = 0; i < 128; i++) fullCanvas[i] = (byte) 0xFF;
            
            // Bytes 128-383 (y=8-31): all 0x00 (white)
            // Already 0 from initialization
            
            // Bytes 384-511 (y=32-39): all 0xFF (black) 
            for (int i = 384; i < 512; i++) fullCanvas[i] = (byte) 0xFF;
            
            // Remaining (y=40-63): all 0x00 (white)
            
            System.out.println("Sending full 1024-byte canvas via 0x88");
            drone.controllerDrawImage(0, 0, 128, 64, fullCanvas);
            Thread.sleep(150);
            System.out.print("Do you see TWO black lines (top and middle)? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 3: Send again, but with circle pattern");
            // Create canvas with circle in middle
            byte[] circleCanvas = new byte[1024];
            // Draw a simple circle pattern (approx)
            for (int y = 24; y < 40; y++) {
                for (int x = 56; x < 72; x++) {
                    int dx = x - 64;
                    int dy = y - 32;
                    if (dx*dx + dy*dy < 64) {  // Rough circle
                        int byteIdx = y / 8 * 128 + x;
                        circleCanvas[byteIdx] |= (1 << (y % 8));
                    }
                }
            }
            
            drone.controllerDrawImage(0, 0, 128, 64, circleCanvas);
            Thread.sleep(150);
            System.out.print("Do you see circle in middle (no lines)? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
