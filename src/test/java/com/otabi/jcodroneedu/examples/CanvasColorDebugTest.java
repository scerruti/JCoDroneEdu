package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DisplayController;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Test: Debug what color values are actually being drawn on the canvas.
 */
public class CanvasColorDebugTest {
    public static void main(String[] args) {
        System.out.println("Testing canvas color values...\n");
        
        DisplayController canvas = new DisplayController();
        Graphics2D g = canvas.getGraphics();
        
        // Check what Color.BLACK actually is
        System.out.println("Color.BLACK RGB values:");
        System.out.println("  Color.BLACK: " + Color.BLACK);
        System.out.println("  .getRGB(): 0x" + Integer.toHexString(Color.BLACK.getRGB()));
        System.out.println("  Integer value: " + Color.BLACK.getRGB());
        
        System.out.println("\nColor.WHITE RGB values:");
        System.out.println("  Color.WHITE: " + Color.WHITE);
        System.out.println("  .getRGB(): 0x" + Integer.toHexString(Color.WHITE.getRGB()));
        System.out.println("  Integer value: " + Color.WHITE.getRGB());
        
        System.out.println("\n--- Stage 1: Fresh canvas (should be all white) ---");
        byte[] beforeDraw = canvas.toByteArray();
        System.out.println("Canvas bytes [0-7]: " + formatBytes(beforeDraw, 0, 8));
        System.out.println("All bytes white? " + allBytesAre(beforeDraw, (byte)0xFF));
        
        System.out.println("\n--- Stage 2: Draw black 8x8 at (0,0) using setColor + fillRect ---");
        canvas.setColor(Color.BLACK);
        g.fillRect(0, 0, 8, 8);
        
        byte[] afterDraw = canvas.toByteArray();
        System.out.println("Canvas bytes [0-7]: " + formatBytes(afterDraw, 0, 8));
        System.out.println("Expected pattern:   " + formatBytes(new byte[]{(byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0x0F, (byte)0x0F, (byte)0x0F, (byte)0x0F}, 0, 8));
        System.out.println("Pattern matches? " + bytesMatch(afterDraw, new byte[]{(byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0x0F, (byte)0x0F, (byte)0x0F, (byte)0x0F}));
        
        System.out.println("\n--- Stage 3: Check actual pixel values at (0,0) to (7,7) ---");
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int rgb = canvas.getImage().getRGB(x, y);
                System.out.print(String.format("(%d,%d)=0x%08X ", x, y, rgb));
            }
            System.out.println();
        }
        
        System.out.println("\n--- Stage 4: Draw black 8x8 using Graphics2D directly ---");
        DisplayController canvas2 = new DisplayController();
        Graphics2D g2 = canvas2.getGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 8, 8);
        
        byte[] afterDraw2 = canvas2.toByteArray();
        System.out.println("Canvas bytes [0-7]: " + formatBytes(afterDraw2, 0, 8));
        System.out.println("Matches expected? " + bytesMatch(afterDraw2, new byte[]{(byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0x0F, (byte)0x0F, (byte)0x0F, (byte)0x0F}));
    }
    
    static String formatBytes(byte[] data, int start, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + length && i < data.length; i++) {
            sb.append(String.format("%02X ", data[i]));
        }
        return sb.toString();
    }
    
    static boolean allBytesAre(byte[] data, byte value) {
        for (byte b : data) {
            if (b != value) return false;
        }
        return true;
    }
    
    static boolean bytesMatch(byte[] data, byte[] expected) {
        if (data.length < expected.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (data[i] != expected[i]) return false;
        }
        return true;
    }
}
