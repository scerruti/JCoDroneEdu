package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DisplayController;
import java.awt.Color;
import java.util.Scanner;

/**
 * Test: Send full canvas as single 0x88 message (will fail - too large).
 * Then send as multiple 0x88 messages to find the issue.
 */
public class CanvasToProtocolTest {
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
            
            // Create a simple canvas with a rectangle
            System.out.println("Stage 2: Creating canvas with rectangle...");
            DisplayController canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(10, 10, 40, 20);
            
            byte[] imageData = canvas.toByteArray();
            System.out.println("Canvas converted to " + imageData.length + " bytes");
            
            // Try to send as single message (will likely fail - too large)
            System.out.println("\nStage 3: Attempting to send full canvas as single 0x88 message...");
            System.out.println("  (This will fail because 1024 bytes > 247 byte limit)");
            try {
                drone.controllerDrawImage(0, 0, 128, 64, imageData);
                System.out.println("Message sent (or silently truncated)");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.print("Does anything appear? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            // Clear and try chunking into 5 messages like we planned
            System.out.println("Stage 4: Clearing screen...");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            System.out.println("Stage 5: Sending canvas in 5 chunks of 247 bytes each...");
            final int CHUNK_SIZE = 247;
            int chunkNum = 0;
            int offset = 0;
            int yPos = 0;
            
            while (offset < imageData.length) {
                int bytesToSend = Math.min(imageData.length - offset, CHUNK_SIZE);
                byte[] chunk = new byte[bytesToSend];
                System.arraycopy(imageData, offset, chunk, 0, bytesToSend);
                
                // Calculate height: each byte represents 8 pixels vertically
                // Round up: (bytesToSend * 8 + 127) / 128
                int pixelHeight = (bytesToSend * 8 + 127) / 128;
                
                System.out.println("  Chunk " + (++chunkNum) + ": y=" + yPos + ", height=" + pixelHeight + ", bytes=" + bytesToSend);
                drone.controllerDrawImage(0, yPos, 128, pixelHeight, chunk);
                
                Thread.sleep(100);  // Small delay between chunks
                
                offset += bytesToSend;
                yPos += pixelHeight;
            }
            
            System.out.print("Do you see the rectangle? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
