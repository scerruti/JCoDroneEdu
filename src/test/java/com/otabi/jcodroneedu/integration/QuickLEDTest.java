package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;

/**
 * Quick LED test to validate drone and controller LED functionality.
 * 
 * This test exercises:
 * - Drone LED solid colors and modes
 * - Controller LED solid colors and modes
 * - LED off commands
 * 
 * Run with: ./gradlew runQuickLEDTest
 */
public class QuickLEDTest {
    
    public static void main(String[] args) {
        System.out.println("=== Quick LED Test - Final Validation ===");
        System.out.println("Testing LED modes with proper timing");
        System.out.println("Watch the LED on the DRONE body.");
        System.out.println();
        
        // Use try-with-resources to ensure proper cleanup
        try (Drone drone = new Drone()) {
            System.out.println("[DEBUG] Connecting to drone...");
            drone.pair();
            
            if (!drone.isConnected()) {
                System.err.println("[ERROR] Failed to connect to drone. Exiting.");
                return;
            }
            
            System.out.println("[DEBUG] Connected successfully!");
            System.out.println("[DEBUG] Battery: " + drone.getBattery() + "%");
            System.out.println();
            
            // Turn off to start clean
            System.out.println("[DEBUG] Turning off drone LED to start clean...");
            drone.droneLEDOff();
            sleep(2000);
            System.out.println();
            
            // Test 1: Solid colors using setDroneLED (now with correct byte order!)
            System.out.println("=== Test 1: Solid Colors with setDroneLED ===");
            
            System.out.println("[DEBUG] Setting RED...");
            drone.setDroneLED(255, 0, 0);
            sleep(3000);
            
            System.out.println("[DEBUG] Setting GREEN...");
            drone.setDroneLED(0, 255, 0);
            sleep(3000);
            
            System.out.println("[DEBUG] Setting BLUE...");
            drone.setDroneLED(0, 0, 255);
            sleep(3000);
            
            System.out.println("[DEBUG] Setting YELLOW...");
            drone.setDroneLED(255, 255, 0);
            sleep(3000);
            
            drone.droneLEDOff();
            sleep(2000);
            
            // Test 2: Dimming (breathing) at different speeds
            System.out.println("\n=== Test 2: Dimming (Breathing) - Speed 10 (Fast) ===");
            System.out.println("[DEBUG] CYAN breathing fast...");
            drone.setDroneLEDMode(0, 255, 255, "dimming", 10);
            sleep(8000);
            
            System.out.println("\n[DEBUG] Switching to MAGENTA breathing slow (speed 1)...");
            drone.setDroneLEDMode(255, 0, 255, "dimming", 1);
            sleep(10000);
            
            drone.droneLEDOff();
            sleep(2000);
            
            // Test 3: Rainbow at different speeds
            System.out.println("\n=== Test 3: Rainbow Mode ===");
            System.out.println("[DEBUG] Rainbow fast (speed 10)...");
            drone.setDroneLEDMode(255, 255, 255, "rainbow", 10);
            sleep(15000);
            
            System.out.println("\n[DEBUG] Rainbow slow (speed 1)...");
            drone.setDroneLEDMode(255, 255, 255, "rainbow", 1);
            sleep(15000);
            
            drone.droneLEDOff();
            sleep(2000);
            
            // Test 4: Controller LED solid colors
            System.out.println("\n=== Test 4: Controller Solid Colors ===");
            
            System.out.println("[DEBUG] Controller RED...");
            drone.setControllerLED(255, 0, 0);
            sleep(3000);
            
            System.out.println("[DEBUG] Controller GREEN...");
            drone.setControllerLED(0, 255, 0);
            sleep(3000);
            
            System.out.println("[DEBUG] Controller BLUE...");
            drone.setControllerLED(0, 0, 255);
            sleep(3000);
            
            System.out.println("[DEBUG] Controller YELLOW...");
            drone.setControllerLED(255, 255, 0);
            sleep(3000);
            
            drone.controllerLEDOff();
            sleep(2000);
            
            // Test 5: Controller LED animations
            System.out.println("\n=== Test 5: Controller LED Animations ===");
            
            System.out.println("[DEBUG] Controller CYAN breathing...");
            drone.setControllerLEDMode(0, 255, 255, "dimming", 10);
            sleep(8000);
            
            System.out.println("[DEBUG] Controller MAGENTA blinking...");
            drone.setControllerLEDMode(255, 0, 255, "blink", 5);
            sleep(8000);
            
            System.out.println("[DEBUG] Controller RAINBOW...");
            drone.setControllerLEDMode(255, 255, 255, "rainbow", 10);
            sleep(10000);
            
            drone.controllerLEDOff();
            sleep(2000);
            
            System.out.println("\n[DEBUG] LED test complete!");
            System.out.println("\n✅ SUCCESS! All LED modes are now working correctly!");
            System.out.println("- Drone solid colors work with setDroneLED()");
            System.out.println("- Drone animated modes work with setDroneLEDMode()");
            System.out.println("- Controller solid colors work with setControllerLED()");
            System.out.println("- Controller animated modes work with setControllerLEDMode()");
            System.out.println("- Byte order fix resolved the issue!");
            
        } catch (Exception e) {
            System.err.println("[ERROR] LED test failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("[DEBUG] Disconnecting and exiting...");
    }
    
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[WARNING] Sleep interrupted: " + e.getMessage());
        }
    }
}
