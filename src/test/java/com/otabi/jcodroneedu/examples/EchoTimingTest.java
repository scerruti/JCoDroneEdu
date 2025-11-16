package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Test: Timing analysis of echo responses.
 * Sends a drone command and measures when the echo arrives to determine
 * if it comes from the controller (immediate) or the drone (delayed).
 */
public class EchoTimingTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone\n");
            
            // Test 1: Controller command (should echo fast - controller is immediate recipient)
            System.out.println("=== TEST 1: Controller Command (DisplayClear) ===");
            System.out.println("Time: " + System.currentTimeMillis());
            System.out.println("Sending DisplayClear to Controller...");
            long controllerSendTime = System.currentTimeMillis();
            drone.controllerClearScreen();
            Thread.sleep(300);
            System.out.println("(Echo should arrive immediately)\n");
            
            // Test 2: Drone command that makes sound (can be heard to verify drone is processing)
            System.out.println("=== TEST 2: Drone Command (Buzzer) ===");
            System.out.println("Time: " + System.currentTimeMillis());
            System.out.println("Sending Buzzer to Drone...");
            long droneSendTime = System.currentTimeMillis();
            drone.droneBuzzer(440, 200);
            Thread.sleep(500);
            System.out.println("(Listen for sound - echo timing will show if it came before/after)");
            
            System.out.println("\n=== ANALYSIS ===");
            System.out.println("If controller echoes immediately when it receives:");
            System.out.println("- DisplayClear echo arrives in ~0-5ms");
            System.out.println("- Buzzer echo arrives in ~0-5ms (before drone beeps)");
            System.out.println("\nIf echo comes from drone after processing:");
            System.out.println("- Buzzer echo arrives AFTER you hear the sound (~200ms+ later)");
        }
    }
}
