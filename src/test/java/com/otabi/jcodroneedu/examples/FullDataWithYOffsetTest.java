package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.util.Scanner;

/**
 * Test: Send full 1024-byte canvas data but at different y positions.
 * Theory: Maybe the firmware expects full canvas data regardless of y position.
 */
public class FullDataWithYOffsetTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // Create full 1024-byte canvas with patterns
            byte[] fullCanvas = new byte[1024];
            
            // Black line at rows 0-7
            for (int i = 0; i < 128; i++) fullCanvas[i] = (byte) 0xFF;
            
            // Black line at rows 32-39
            for (int i = 384; i < 512; i++) fullCanvas[i] = (byte) 0xFF;
            
            System.out.println("Stage 1: Clear screen");
            drone.controllerClearScreen();
            Thread.sleep(500);
            System.out.print("Blank? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 2: Send full 1024 bytes at y=0, width=128, height=64");
            drone.controllerDrawImage(0, 0, 128, 64, fullCanvas);
            Thread.sleep(150);
            System.out.print("Do you see two lines (top and middle)? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 3: Create canvas with only black at rows 0-7, rest white");
            byte[] onlyTop = new byte[1024];
            for (int i = 0; i < 128; i++) onlyTop[i] = (byte) 0xFF;
            
            System.out.println("Send same 1024 bytes at y=0");
            drone.controllerDrawImage(0, 0, 128, 64, onlyTop);
            Thread.sleep(150);
            System.out.print("Do you see only top line (middle line gone)? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Stage 4: Now try sending 247 bytes (first chunk) at y=0, width=128, height=8");
            byte[] chunk = new byte[247];
            System.arraycopy(fullCanvas, 0, chunk, 0, 247);
            
            drone.controllerClearScreen();
            Thread.sleep(500);
            drone.controllerDrawImage(0, 0, 128, 8, chunk);
            Thread.sleep(150);
            System.out.print("Does anything appear at top? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
