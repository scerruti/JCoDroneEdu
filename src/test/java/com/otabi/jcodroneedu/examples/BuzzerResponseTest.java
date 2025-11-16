package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Test: Send buzzer commands and observe response packets.
 * This determines if non-display commands also get the echo-based responses.
 */
public class BuzzerResponseTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            
            // Test 1: Single buzzer
            System.out.println("\n=== TEST 1: Single Buzzer ===");
            drone.controllerBuzzer(440, 100);
            Thread.sleep(500);
            
            // Test 2: Multiple buzzers
            System.out.println("\n=== TEST 2: Three Rapid Buzzers ===");
            for (int i = 0; i < 3; i++) {
                drone.controllerBuzzer(440, 50);
                Thread.sleep(50);
            }
            Thread.sleep(500);
            
            System.out.println("\nTest complete.");
        }
    }
}
