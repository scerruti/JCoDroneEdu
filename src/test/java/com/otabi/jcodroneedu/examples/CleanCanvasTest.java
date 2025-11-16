package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DisplayController;
import java.awt.Color;
import java.util.Scanner;

/**
 * Test: Send full canvas in properly-sized 0x88 chunks (no error conditions).
 */
public class CleanCanvasTest {
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
            System.out.println("Stage 2: Creating canvas with black rectangle...");
            DisplayController canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(10, 10, 40, 20);
            
            byte[] imageData = canvas.toByteArray();
            System.out.println("Canvas converted to " + imageData.length + " bytes\n");
            
            // Send in proper 5-chunk format
            System.out.println("Stage 3: Sending canvas in 5 proper chunks...");
            final int CHUNK_SIZE = 247;
            int chunkNum = 0;
            int offset = 0;
            int yPos = 0;
            
            while (offset < imageData.length) {
                int bytesToSend = Math.min(imageData.length - offset, CHUNK_SIZE);
                byte[] chunk = new byte[bytesToSend];
                System.arraycopy(imageData, offset, chunk, 0, bytesToSend);
                
                // Calculate height: each byte represents 8 pixels vertically
                // For now, use simple division: 247 bytes = 1976 bits / 128 width = 15.4 rows ≈ 16 rows
                int pixelHeight = (bytesToSend * 8 + 127) / 128;
                
                System.out.println("  Chunk " + (++chunkNum) + ": y=" + yPos + ", height=" + pixelHeight + ", bytes=" + bytesToSend);
                drone.controllerDrawImage(0, yPos, 128, pixelHeight, chunk);
                
                Thread.sleep(150);  // Adequate delay between chunks
                
                offset += bytesToSend;
                yPos += pixelHeight;
            }
            
            System.out.println("\nChunks sent successfully. Port still open.");
            System.out.print("Do you see the rectangle? Press ENTER...");
            scanner.nextLine();
            System.out.println();
            
            System.out.println("Test complete!");
        }
    }
}
