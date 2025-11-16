package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.awt.Color;

/**
 * Simple test: Draw a rectangle and circle using the 0x88 protocol.
 */
public class Simple0x88Test {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            
            System.out.println("\n1. Drawing rectangle...");
            var canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(10, 10, 30, 20);
            drone.controllerDrawCanvas(canvas);
            System.out.println("Rectangle drawn (should take ~1 second)");
            
            Thread.sleep(2000);
            
            System.out.println("\n2. Drawing circle...");
            canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            canvas.drawCircle(64, 32, 15);
            drone.controllerDrawCanvas(canvas);
            System.out.println("Circle drawn");
            
            Thread.sleep(2000);
            
            System.out.println("\n3. Clearing screen...");
            canvas = drone.controllerCreateCanvas();
            drone.controllerDrawCanvas(canvas);
            System.out.println("Screen cleared");
            
            System.out.println("\nTest complete!");
        }
    }
}
