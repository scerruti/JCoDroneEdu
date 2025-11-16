package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.DataType;

/**
 * Debug tool to see what controller input data is actually being received.
 */
public class ControllerInputDebug {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        try {
            System.out.println("Connecting to CoDrone EDU...");
            drone.pair();
            Thread.sleep(1000);
            System.out.println("Connected!\n");
            
            System.out.println("Requesting controller data for 30 seconds...");
            System.out.println("Move joysticks and press buttons!\n");
            
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 30000) {
                // Request data
                drone.sendRequest(DataType.Joystick);
                drone.sendRequest(DataType.Button);
                Thread.sleep(100);
                
                // Get joystick data
                int[] joystick = drone.getJoystickData();
                System.out.println("Joystick: " + java.util.Arrays.toString(joystick));
                
                // Get button data  
                Object[] button = drone.getButtonData();
                System.out.println("Button: " + java.util.Arrays.toString(button));
                
                System.out.println("---");
                Thread.sleep(400);
            }
            
            System.out.println("\nDone!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            drone.close();
        }
    }
}
