package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.util.Scanner;

/**
 * Test: Send larger blocks of 0xFF to see if block size affects rendering.
 */
public class BlockSizeTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            System.out.println("Stage 1: Clear screen");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            // Test 1: 16 bytes (8 columns × 2 rows of 8 pixels each)
            System.out.println("Stage 2: Send 16×8 block of 0xFF at (0,0)");
            byte[] block16 = new byte[16];
            for (int i = 0; i < 16; i++) block16[i] = (byte) 0xFF;
            
            drone.controllerDrawImage(0, 0, 16, 8, block16);
            Thread.sleep(150);
            System.out.print("Do you see 16×8 solid black rectangle? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 2: 128 bytes (full width, 1 row of 8 pixels)
            System.out.println("Stage 3: Clear and send 128×8 block of 0xFF at (0,0)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            byte[] block128 = new byte[128];
            for (int i = 0; i < 128; i++) block128[i] = (byte) 0xFF;
            
            drone.controllerDrawImage(0, 0, 128, 8, block128);
            Thread.sleep(150);
            System.out.print("Do you see full-width top row solid black? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 3: 256 bytes (full width, 2 rows of 8 pixels each)
            System.out.println("Stage 4: Clear and send 128×16 block of 0xFF at (0,0)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            byte[] block256 = new byte[256];
            for (int i = 0; i < 256; i++) block256[i] = (byte) 0xFF;
            
            drone.controllerDrawImage(0, 0, 128, 16, block256);
            Thread.sleep(150);
            System.out.print("Do you see top two rows solid black? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
