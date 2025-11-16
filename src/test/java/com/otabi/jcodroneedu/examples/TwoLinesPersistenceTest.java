package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.util.Scanner;

/**
 * Test: Reproduce the working Stage 2 from Y0AfterOtherCommandTest.
 * Send y=32 and y=0 separately, verify both stay visible.
 */
public class TwoLinesPersistenceTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            byte[] block = new byte[128];
            for (int i = 0; i < 128; i++) block[i] = (byte) 0xFF;
            
            System.out.println("Stage 1: Clear and send y=32");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 32, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see middle line? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 2: Send y=0 (without clearing)");
            drone.controllerDrawImage(0, 0, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see BOTH lines (top AND middle) visible together? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 3: Wait and verify they stay visible");
            Thread.sleep(2000);
            System.out.print("Are both lines STILL visible? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("SUCCESS! Both lines persisted. This proves 0x88 CAN accumulate.");
            System.out.println("Now let's test if we can build up from here...\n");
            
            System.out.println("Stage 4: Add y=16 (without clearing)");
            drone.controllerDrawImage(0, 16, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see THREE lines? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 5: Add y=48 (without clearing)");
            drone.controllerDrawImage(0, 48, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see FOUR lines? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
