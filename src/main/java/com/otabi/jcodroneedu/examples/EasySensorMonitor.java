package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.SensorMonitor;

/**
 * Ultra-simple example showing how students can monitor sensors with ONE line of code.
 * No GUI knowledge required!
 * 
 * @educational This is the easiest way to see your drone's sensors while your code runs.
 * 
 * @author CoDrone EDU Development Team
 * @since 1.0
 */
public class EasySensorMonitor {
    
    public static void main(String[] args) {
        // Connect to drone
        Drone drone = new Drone();
        drone.pair();
        
        // Open sensor monitor with ONE line - that's it!
        @SuppressWarnings("unused") // Monitor runs in background thread
        SensorMonitor monitor = new SensorMonitor(drone);
        
        // Now do your drone programming...
        // The sensor monitor window shows live updates in the background
        
        System.out.println("Sensor monitor is running!");
        System.out.println("Watch the window while the drone flies...");
        
        // Example flight
        drone.takeoff();
        drone.hover(3);
        
        // Fly around and watch the sensors update
        drone.setYaw(30);
        drone.move(2);
        
        drone.turn(180);
        drone.move(2);
        
        drone.land();
        
        System.out.println("Flight complete!");
        System.out.println("You can close the monitor window or it will close automatically.");
        
        // Optional: explicitly close the monitor
        // monitor.close();
        
        // Clean up
        drone.close();
    }
}
