package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.ControllerMonitor;

/**
 * Ultra-simple example showing how students can monitor controller input with ONE line of code.
 * No GUI knowledge required!
 * 
 * @educational This is the easiest way to see controller input while testing your code.
 * 
 * @author CoDrone EDU Development Team
 * @since 1.0
 */
public class EasyControllerMonitor {
    
    public static void main(String[] args) {
        // Connect to drone
        Drone drone = new Drone();
        drone.pair();
        
        // Open controller monitor with ONE line - that's it!
        @SuppressWarnings("unused") // Monitor is used for its side effects (runs in background)
        ControllerMonitor monitor = new ControllerMonitor(drone);
        
        // Now you can see what the controller is doing while you code...
        
        System.out.println("Controller monitor is running!");
        System.out.println("Try moving the joysticks and pressing buttons.");
        System.out.println();
        
        // Example: Use controller input in your code
        System.out.println("Testing joystick input...");
        System.out.println("Move the left joystick (watch the monitor!)");
        System.out.println("The program will continue when you move it...");
        
        // Wait for joystick movement
        while (drone.getLeftJoystickX() == 0 && drone.getLeftJoystickY() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        System.out.println("Joystick moved! Taking off...");
        drone.takeoff();
        drone.hover(3);
        
        System.out.println("Move the right joystick to land (watch the monitor!)");
        
        // Wait for right joystick movement
        while (drone.getRightJoystickX() == 0 && drone.getRightJoystickY() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        System.out.println("Right joystick moved! Landing...");
        drone.land();
        
        System.out.println("Done! You can close the monitor window.");
        
        // Optional: explicitly close the monitor
        // monitor.close();
        
        // Clean up
        drone.close();
    }
}
