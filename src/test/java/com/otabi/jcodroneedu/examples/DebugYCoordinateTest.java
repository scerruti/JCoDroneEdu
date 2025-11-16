package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DisplayController;
import java.awt.Color;
import java.util.Scanner;

/**
 * Test: Start with the working 8x8 pattern, then expand.
 */
public class DebugYCoordinateTest {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // Clear screen
            System.out.println("Stage 1: Clearing screen...");
            drone.controllerClearScreen();
            Thread.sleep(500);
            System.out.print("Screen should be white. Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Stage 2: Send the working 8x8 pattern (we know this works)
            System.out.println("Stage 2: Sending the 8x8 pattern that we know works...");
            byte[] workingPattern = new byte[8];
            workingPattern[0] = (byte) 0xF0;
            workingPattern[1] = (byte) 0xF0;
            workingPattern[2] = (byte) 0xF0;
            workingPattern[3] = (byte) 0xF0;
            workingPattern[4] = (byte) 0x0F;
            workingPattern[5] = (byte) 0x0F;
            workingPattern[6] = (byte) 0x0F;
            workingPattern[7] = (byte) 0x0F;
            
            System.out.println("  Sending at y=0, width=8, height=8, bytes=8");
            drone.controllerDrawImage(0, 0, 8, 8, workingPattern);
            Thread.sleep(150);
            
            System.out.print("Do you see the diagonal squares at top-left? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Stage 3: Now try with the canvas data (first 8 bytes, same as pattern)
            System.out.println("Stage 3: Creating canvas and comparing first 8 bytes...");
            DisplayController canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            // Draw using Graphics2D directly instead of fillRectangle
            canvas.getGraphics().fillRect(0, 0, 8, 8);
            
            byte[] imageData = canvas.toByteArray();
            System.out.println("Canvas created, checking first 8 bytes:");
            System.out.print("  Working pattern: ");
            for (int i = 0; i < 8; i++) System.out.print(String.format("%02X ", workingPattern[i]));
            System.out.println();
            System.out.print("  Canvas bytes:    ");
            for (int i = 0; i < 8; i++) System.out.print(String.format("%02X ", imageData[i]));
            System.out.println();
            
            // Clear and send canvas data
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            System.out.println("  Sending canvas first 8 bytes at y=0, width=8, height=8");
            byte[] canvasChunk = new byte[8];
            System.arraycopy(imageData, 0, canvasChunk, 0, 8);
            drone.controllerDrawImage(0, 0, 8, 8, canvasChunk);
            Thread.sleep(150);
            
            System.out.print("Do you see the same diagonal squares? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Stage 4: Try with 128 bytes (one full row group)
            System.out.println("Stage 4: Clearing and trying with 128 bytes (full row group)...");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            System.out.println("  Sending canvas first 128 bytes at y=0, width=128, height=8");
            byte[] oneRowGroup = new byte[128];
            System.arraycopy(imageData, 0, oneRowGroup, 0, 128);
            drone.controllerDrawImage(0, 0, 128, 8, oneRowGroup);
            Thread.sleep(150);
            
            System.out.print("Do you see anything at top? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}

