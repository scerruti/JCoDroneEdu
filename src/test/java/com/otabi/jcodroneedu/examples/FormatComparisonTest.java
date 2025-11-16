package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.util.Scanner;

/**
 * Test: Compare SingleMessageTest pattern vs canvas bytes format.
 * Determine if they use the same binary encoding.
 */
public class FormatComparisonTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // The pattern that works from SingleMessageTest
            byte[] workingPattern = new byte[8];
            workingPattern[0] = (byte) 0xF0;
            workingPattern[1] = (byte) 0xF0;
            workingPattern[2] = (byte) 0xF0;
            workingPattern[3] = (byte) 0xF0;
            workingPattern[4] = (byte) 0x0F;
            workingPattern[5] = (byte) 0x0F;
            workingPattern[6] = (byte) 0x0F;
            workingPattern[7] = (byte) 0x0F;
            
            System.out.println("Stage 1: Clear screen");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            System.out.println("Stage 2: Send working pattern (0xF0/0x0F) at y=0");
            drone.controllerDrawImage(0, 0, 8, 8, workingPattern);
            Thread.sleep(150);
            System.out.print("Do you see diagonal squares? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 3: Clear and try inverted pattern (0x0F/0xF0)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            byte[] invertedPattern = new byte[8];
            invertedPattern[0] = (byte) 0x0F;
            invertedPattern[1] = (byte) 0x0F;
            invertedPattern[2] = (byte) 0x0F;
            invertedPattern[3] = (byte) 0x0F;
            invertedPattern[4] = (byte) 0xF0;
            invertedPattern[5] = (byte) 0xF0;
            invertedPattern[6] = (byte) 0xF0;
            invertedPattern[7] = (byte) 0xF0;
            
            drone.controllerDrawImage(0, 0, 8, 8, invertedPattern);
            Thread.sleep(150);
            System.out.print("Do you see inverted diagonal squares? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 4: Try all pixels ON (0xFF)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            byte[] allOnPattern = new byte[8];
            for (int i = 0; i < 8; i++) {
                allOnPattern[i] = (byte) 0xFF;
            }
            
            drone.controllerDrawImage(0, 0, 8, 8, allOnPattern);
            Thread.sleep(150);
            System.out.print("Do you see solid 8x8 square? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 5: Try all pixels OFF (0x00)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            byte[] allOffPattern = new byte[8];
            for (int i = 0; i < 8; i++) {
                allOffPattern[i] = (byte) 0x00;
            }
            
            drone.controllerDrawImage(0, 0, 8, 8, allOffPattern);
            Thread.sleep(150);
            System.out.print("Do you see anything (should be empty)? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
