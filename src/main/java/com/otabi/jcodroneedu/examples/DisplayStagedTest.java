package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DisplayController;
import java.awt.Color;

/**
 * Staged display test with clear visual progression.
 * Draws: white -> rectangle -> white -> circle -> white
 */
public class DisplayStagedTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            System.out.println("Connected to drone\n");
            
            // Stage 1: White
            System.out.println("Stage 1: Sending WHITE (blank) screen...");
            DisplayController canvas = drone.controllerCreateCanvas();
            drone.controllerDrawCanvas(canvas);
            System.out.println("Sent. Screen should be WHITE/BLANK.\n");
            Thread.sleep(3000);
            
            // Stage 2: Rectangle
            System.out.println("Stage 2: Sending BLACK RECTANGLE at (10,10) size 30x20...");
            canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(10, 10, 30, 20);
            drone.controllerDrawCanvas(canvas);
            System.out.println("Sent. Screen should show rectangle in upper-left.\n");
            Thread.sleep(3000);
            
            // Stage 3: White
            System.out.println("Stage 3: Sending WHITE (blank) screen again...");
            canvas = drone.controllerCreateCanvas();
            drone.controllerDrawCanvas(canvas);
            System.out.println("Sent. Screen should be WHITE/BLANK again.\n");
            Thread.sleep(3000);
            
            // Stage 4: Circle
            System.out.println("Stage 4: Sending BLACK CIRCLE at center (64,32) radius 15...");
            canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            canvas.fillCircle(64, 32, 15);
            drone.controllerDrawCanvas(canvas);
            System.out.println("Sent. Screen should show circle in center.\n");
            Thread.sleep(3000);
            
            // Stage 5: White
            System.out.println("Stage 5: Sending WHITE (blank) screen final...");
            canvas = drone.controllerCreateCanvas();
            drone.controllerDrawCanvas(canvas);
            System.out.println("Sent. Screen should be WHITE/BLANK.\n");
            
            System.out.println("Test complete!");
        }
    }
}
