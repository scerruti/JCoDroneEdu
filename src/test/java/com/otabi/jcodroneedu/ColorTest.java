package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.lightcontroller.Color;

public class ColorTest {
    public static void main(String[] args) {
        try {
            // Test with small values (should work)
            Color color1 = new Color((byte) 50, (byte) 100, (byte) 127);
            System.out.println("Color with values 50, 100, 127: SUCCESS");
            
            // Test with larger values (might fail)
            Color color2 = new Color((byte) 255, (byte) 0, (byte) 0);
            System.out.println("Color with values 255, 0, 0: SUCCESS");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
