package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Test: Send drone commands and observe response packets.
 * This determines if the echo-based responses are universal or specific to controller commands.
 */
public class DroneCommandResponseTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            
            // Test 1: Drone buzzer command (sends to drone, not controller)
            System.out.println("\n=== TEST 1: Drone Buzzer (Drone Command) ===");
            drone.droneBuzzer(440, 100);
            Thread.sleep(500);
            
            // Test 2: Multiple drone buzzer commands
            System.out.println("\n=== TEST 2: Three Drone Buzzer Commands ===");
            for (int i = 0; i < 3; i++) {
                drone.droneBuzzer(440, 50);
                Thread.sleep(100);
            }
            Thread.sleep(500);
            
            System.out.println("\nTest complete.");
        }
    }
}
