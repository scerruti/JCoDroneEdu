package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DisplayController;
import java.awt.Color;
import java.util.Scanner;

/**
 * Test: Send canvas-generated data in the exact same format that blocksizetest used.
 */
public class CanvasVsManualTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            System.out.println("Stage 1: Clear screen");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            // Create a canvas and draw a 16×8 black rectangle at (0,0)
            System.out.println("Stage 2: Create canvas with 16×8 black rectangle at (0,0)");
            DisplayController canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(0, 0, 16, 8);
            
            byte[] canvasData = canvas.toByteArray();
            System.out.println("Canvas created, total bytes: " + canvasData.length);
            System.out.println("First 16 bytes from canvas:");
            for (int i = 0; i < 16; i++) {
                System.out.print(String.format("%02X ", canvasData[i]));
            }
            System.out.println();
            
            // Extract first 16 bytes and send them
            byte[] chunk16 = new byte[16];
            System.arraycopy(canvasData, 0, chunk16, 0, 16);
            
            System.out.println("Sending canvas bytes 0-15 as 16×8 image at (0,0)");
            drone.controllerDrawImage(0, 0, 16, 8, chunk16);
            Thread.sleep(150);
            System.out.print("Do you see 16×8 black rectangle? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Clear and try manual all-0xFF for comparison
            System.out.println("Stage 3: Clear and send manual 0xFF pattern (16 bytes)");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            byte[] manual16 = new byte[16];
            for (int i = 0; i < 16; i++) manual16[i] = (byte) 0xFF;
            
            drone.controllerDrawImage(0, 0, 16, 8, manual16);
            Thread.sleep(150);
            System.out.print("Do you see 16×8 black rectangle? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Compare the bytes
            System.out.println("Byte comparison:");
            boolean match = true;
            for (int i = 0; i < 16; i++) {
                if (chunk16[i] != manual16[i]) {
                    match = false;
                    System.out.println("Byte " + i + ": canvas=" + String.format("%02X", chunk16[i]) + 
                                     " vs manual=" + String.format("%02X", manual16[i]));
                }
            }
            if (match) {
                System.out.println("All bytes match!");
            }
            
            System.out.println("\nTest complete!");
        }
    }
}
