package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DisplayController;
import java.awt.Color;
import java.util.Scanner;

/**
 * Test to send a completely white (empty) display to see if it renders correctly.
 * Uses keyboard input to control timing between tests.
 */
public class DisplayWhiteTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            System.out.println("Connected to drone");
            Scanner scanner = new Scanner(System.in);
            
            // Send completely white canvas (nothing drawn)
            System.out.println("\n=== Test 1: Sending white (empty) display ===");
            DisplayController canvas = drone.controllerCreateCanvas();
            // Don't draw anything - it should be all white
            
            System.out.println("About to send white canvas...");
            System.out.println("Press ENTER to send...");
            scanner.nextLine();
            
            System.out.println("Sending white canvas...");
            drone.controllerDrawCanvas(canvas);
            System.out.println("White display sent!");
            System.out.println("Look at the display - it should be COMPLETELY WHITE/BLANK");
            System.out.println("Press ENTER when ready for next test...");
            scanner.nextLine();
            
            // Now draw a single black rectangle to verify drawing works
            System.out.println("\n=== Test 2: Drawing single black rectangle (10,10,30,20) ===");
            canvas = drone.controllerCreateCanvas();
            canvas.setColor(Color.BLACK);
            canvas.fillRectangle(10, 10, 30, 20);
            
            System.out.println("About to send black rectangle...");
            System.out.println("Press ENTER to send...");
            scanner.nextLine();
            
            System.out.println("Sending rectangle...");
            drone.controllerDrawCanvas(canvas);
            System.out.println("Rectangle sent!");
            System.out.println("Look at the display - you should see a BLACK RECTANGLE in the upper-left area");
            System.out.println("Rectangle position: upper-left at (10,10), size 30 wide x 20 tall");
            System.out.println("Press ENTER when ready for next test...");
            scanner.nextLine();
            
            // Send white again
            System.out.println("\n=== Test 3: Sending white again ===");
            canvas = drone.controllerCreateCanvas();
            
            System.out.println("About to send white canvas again...");
            System.out.println("Press ENTER to send...");
            scanner.nextLine();
            
            drone.controllerDrawCanvas(canvas);
            System.out.println("White display sent!");
            System.out.println("Look at the display - it should go back to COMPLETELY WHITE/BLANK");
            System.out.println("Press ENTER to finish...");
            scanner.nextLine();
            
            System.out.println("\nTest complete!");
            scanner.close();
        }
    }
}

