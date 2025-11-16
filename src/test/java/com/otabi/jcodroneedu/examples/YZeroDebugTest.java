package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.util.Scanner;

/**
 * Test: Isolate the y=0 positioning issue.
 */
public class YZeroDebugTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            byte[] block = new byte[128];
            for (int i = 0; i < 128; i++) block[i] = (byte) 0xFF;
            
            // Test 1: y=0 alone
            System.out.println("Stage 1: Clear and send ONLY y=0");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 0, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see black line at top? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 2: y=1 to verify non-zero y works
            System.out.println("Stage 2: Clear and send ONLY y=1");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 1, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see black line offset by 1 pixel? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 3: Small block at y=0 (like SingleMessageTest)
            System.out.println("Stage 3: Clear and send 8×8 at y=0 (like SingleMessageTest)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            byte[] small = new byte[8];
            for (int i = 0; i < 8; i++) small[i] = (byte) 0xFF;
            drone.controllerDrawImage(0, 0, 8, 8, small);
            Thread.sleep(150);
            System.out.print("Do you see 8×8 black square at top-left? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Test 4: y=0 with different width
            System.out.println("Stage 4: Clear and send 64×8 at y=0 (half width)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            byte[] half = new byte[64];
            for (int i = 0; i < 64; i++) half[i] = (byte) 0xFF;
            drone.controllerDrawImage(0, 0, 64, 8, half);
            Thread.sleep(150);
            System.out.print("Do you see black rectangle (half width) at top? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
