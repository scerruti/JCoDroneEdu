package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.ControllerInputPanel;

import javax.swing.*;

/**
 * Simple example showing how to use the ControllerInputPanel component.
 * This demonstrates the basic 5-step pattern for monitoring controller input.
 * 
 * <p><strong>Steps to use ControllerInputPanel:</strong></p>
 * <ol>
 *   <li>Create and connect your drone</li>
 *   <li>Create a ControllerInputPanel with your drone</li>
 *   <li>Add the panel to your GUI</li>
 *   <li>Start monitoring</li>
 *   <li>Stop monitoring when done</li>
 * </ol>
 * 
 * @educational This example is designed for students learning to work with
 *              the CoDrone EDU controller. The ControllerInputPanel handles
 *              all the complexity of displaying controller input.
 * 
 * @author CoDrone EDU Development Team
 * @since 1.0
 */
public class SimpleControllerMonitor {
    
    public static void main(String[] args) {
        // Step 1: Create and connect the drone
        System.out.println("Connecting to CoDrone EDU...");
        Drone drone = new Drone();
        
        try {
            drone.pair();
            System.out.println("Connected!");
            
            // Step 2: Create the controller input panel
            ControllerInputPanel controllerPanel = new ControllerInputPanel(drone);
            
            // Step 3: Create a window and add the panel
            JFrame frame = new JFrame("My Controller Monitor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(controllerPanel);
            frame.pack();
            frame.setLocationRelativeTo(null); // Center on screen
            
            // Add cleanup when window closes
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.out.println("Cleaning up...");
                    controllerPanel.stopMonitoring();
                    drone.close();
                    System.out.println("Done!");
                }
            });
            
            frame.setVisible(true);
            
            // Step 4: Start monitoring the controller
            controllerPanel.startMonitoring();
            
            System.out.println("Controller monitoring active!");
            System.out.println("Try moving the joysticks and pressing buttons.");
            System.out.println("Close the window when done.");
            
            // The panel will continue updating until the window is closed
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            drone.close();
        }
    }
}
