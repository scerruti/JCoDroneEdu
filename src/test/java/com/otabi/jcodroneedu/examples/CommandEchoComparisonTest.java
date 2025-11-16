package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Test: Compare echo responses for different command types and routing.
 * Demonstrates that echo responses are universal for all commands.
 */
public class CommandEchoComparisonTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // Controller command
            System.out.println("=== CONTROLLER COMMAND ===");
            System.out.println("Sending: DisplayClear (To=Controller)");
            drone.controllerClearScreen();
            Thread.sleep(300);
            
            // Drone command
            System.out.println("\n=== DRONE COMMAND ===");
            System.out.println("Sending: Buzzer (To=Drone)");
            drone.droneBuzzer(440, 100);
            Thread.sleep(300);
            
            System.out.println("\nNote: Both types of commands receive 11-byte echo packets");
            System.out.println("containing the command DataType in byte 8.");
        }
    }
}
