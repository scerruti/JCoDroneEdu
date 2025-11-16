package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.util.Scanner;

/**
 * Test: Debug why chunked 0x88 messages don't position correctly.
 * Send test patterns at different y positions to isolate the issue.
 */
public class ChunkPositioningDebugTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // Create a simple 128-byte all-0xFF block
            byte[] block = new byte[128];
            for (int i = 0; i < 128; i++) block[i] = (byte) 0xFF;
            
            // Test 1: Send at y=0
            System.out.println("Stage 1: Clear and send 128×8 at y=0");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 0, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see black line at TOP (y=0-7)? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 2: Send at y=8 (next row group)
            System.out.println("Stage 2: Clear and send 128×8 at y=8");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 8, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see black line at y=8-15? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 3: Send at y=16
            System.out.println("Stage 3: Clear and send 128×8 at y=16");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 16, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see black line at y=16-23? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 4: Send multiple non-overlapping chunks
            System.out.println("Stage 4: Clear and send y=0 AND y=32 (two separate chunks)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 0, 128, 8, block);
            Thread.sleep(100);
            drone.controllerDrawImage(0, 32, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see TWO black lines (top and middle)? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 5: Send the 5-chunk pattern like our failing attempt
            System.out.println("Stage 5: Clear and send 5 chunks like the canvas chunking");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            int[] yPositions = {0, 8, 16, 24, 32};
            for (int y : yPositions) {
                drone.controllerDrawImage(0, y, 128, 8, block);
                Thread.sleep(50);
            }
            Thread.sleep(150);
            System.out.print("Do you see 5 black horizontal lines? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
