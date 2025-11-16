package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.util.Scanner;

/**
 * Test: Does y=0 work after another 0x88 command?
 * (Reproduce the Stage 4 scenario where y=0 didn't show but y=32 did)
 */
public class Y0AfterOtherCommandTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            byte[] block = new byte[128];
            for (int i = 0; i < 128; i++) block[i] = (byte) 0xFF;
            
            // Test: Send y=32 first, then y=0
            System.out.println("Stage 1: Send y=32 first");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 32, 128, 8, block);
            Thread.sleep(100);
            System.out.print("Do you see middle line? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 2: Now send y=0 (WITHOUT clearing)");
            drone.controllerDrawImage(0, 0, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see BOTH top line AND middle line? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 3: Clear and repeat in reverse order");
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 0, 128, 8, block);
            Thread.sleep(100);
            System.out.print("Do you see top line? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 4: Now send y=32 (WITHOUT clearing)");
            drone.controllerDrawImage(0, 32, 128, 8, block);
            Thread.sleep(150);
            System.out.print("Do you see BOTH top line AND middle line? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
