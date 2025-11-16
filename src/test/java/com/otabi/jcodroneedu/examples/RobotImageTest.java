package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import java.awt.Color;

/**
 * Test: Display the robot emoji-like image on the controller screen.
 */
public class RobotImageTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            
            // Create canvas and draw robot face
            var canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            
            // Head (rounded rectangle-ish)
            canvas.fillRectangle(30, 12, 68, 40);  // Main head
            
            // Left eye (white background for contrast)
            canvas.setColor(Color.WHITE);
            canvas.fillRectangle(42, 18, 12, 12);
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(45, 21, 6, 6);    // Pupil
            
            // Right eye (white background)
            canvas.setColor(Color.WHITE);
            canvas.fillRectangle(74, 18, 12, 12);
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(77, 21, 6, 6);    // Pupil
            
            // Mouth - small rectangle
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(60, 38, 8, 4);
            
            // Left ear/antenna
            canvas.drawLine(32, 20, 20, 5);
            canvas.drawLine(33, 20, 21, 5);
            
            // Right ear/antenna
            canvas.drawLine(96, 20, 108, 5);
            canvas.drawLine(97, 20, 109, 5);
            
            System.out.println("Drawing robot face...");
            drone.controllerDrawCanvas(canvas);
            System.out.println("Robot displayed!");
            
            Thread.sleep(5000);
            
            System.out.println("Clearing screen...");
            canvas = drone.controllerCreateCanvas();
            drone.controllerDrawCanvas(canvas);
            System.out.println("Done!");
        }
    }
}
