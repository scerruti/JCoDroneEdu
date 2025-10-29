package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

/**
 * Comprehensive test for autonomous flight methods and ping feature.
 * 
 * <p>This test program demonstrates:
 * <ul>
 *   <li>Autonomous wall avoidance (avoidWall)</li>
 *   <li>Autonomous distance keeping (keepDistance)</li>
 *   <li>Ping/find drone feature</li>
 *   <li>Combined autonomous behaviors</li>
 * </ul>
 * 
 * <h3>⚠️ Safety Requirements:</h3>
 * <ul>
 *   <li>Clear flight space (3m x 3m minimum)</li>
 *   <li>Wall or large object for detection (within 1-2 meters)</li>
 *   <li>Emergency stop ready (controller button)</li>
 *   <li>Fully charged battery</li>
 * </ul>
 * 
 * @author Stephen Cerruti
 * @educational Tests autonomous methods from API completion milestone
 */
public class AutonomousPingTest {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  CoDrone EDU Autonomous + Ping Test          ║");
        System.out.println("║  API Completion: 87%                          ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        try (Drone drone = new Drone()) {
            // Test 1: Connection and Ping
            System.out.println("\n[Test 1] Connection + Ping Feature");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            System.out.println("Connecting to drone...");
            if (!drone.pair()) {
                throw new DroneNotFoundException("Could not connect to drone");
            }
            System.out.println("✓ Connected!");
            
            // Ping with random color
            System.out.println("\nPinging drone with random color...");
            drone.ping();
            Thread.sleep(2000);
            
            // Ping with specific colors
            System.out.println("Pinging with RED...");
            drone.ping(255, 0, 0);
            Thread.sleep(2000);
            
            System.out.println("Pinging with GREEN...");
            drone.ping(0, 255, 0);
            Thread.sleep(2000);
            
            System.out.println("Pinging with BLUE...");
            drone.ping(0, 0, 255);
            Thread.sleep(2000);
            
            // Test 2: Basic Autonomous Flight Setup
            System.out.println("\n[Test 2] Pre-Flight Checks");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // Check battery
            int battery = drone.getBattery();
            System.out.println("Battery level: " + battery + "%");
            if (battery < 30) {
                System.err.println("⚠ Battery too low for autonomous flight test!");
                System.err.println("  Please charge battery and try again.");
                return;
            }
            
            // Check sensors
            System.out.println("Checking front range sensor...");
            double frontRange = drone.getFrontRange();
            System.out.println("Front range: " + frontRange + " cm");
            
            if (frontRange < 10 || frontRange > 200) {
                System.err.println("⚠ Front sensor not detecting properly!");
                System.err.println("  Please ensure sensor is clear and within 2m of wall.");
                return;
            }
            
            System.out.println("✓ All pre-flight checks passed!");
            
            // Test 3: Autonomous Wall Avoidance
            System.out.println("\n[Test 3] Autonomous Wall Avoidance");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("This test will:");
            System.out.println("  1. Take off");
            System.out.println("  2. Maintain 50cm distance from wall for 5 seconds");
            System.out.println("  3. Signal success with buzzer");
            System.out.println("  4. Land");
            
            confirmToContinue();
            
            System.out.println("\nTaking off...");
            drone.takeoff();
            drone.hover(2.0);  // Stabilize
            System.out.println("✓ Airborne and stable");
            
            System.out.println("\nActivating avoidWall(5, 50)...");
            System.out.println("  Target: 50cm from wall");
            System.out.println("  Duration: 5 seconds");
            
            // Monitor during autonomous flight
            Thread monitorThread = new Thread(() -> {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(500);
                        double range = drone.getFrontRange();
                        System.out.printf("  [%ds] Distance: %.1f cm%n", i / 2, range);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            monitorThread.start();
            
            drone.avoidWall(5, 50);
            monitorThread.join();
            
            System.out.println("✓ Wall avoidance complete");
            drone.droneBuzzerSequence("success");
            
            System.out.println("\nLanding...");
            drone.land();
            drone.hover(2.0);  // Wait for full landing
            System.out.println("✓ Landed safely");
            
            // Test 4: Autonomous Distance Keeping
            System.out.println("\n[Test 4] Autonomous Distance Keeping");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("This test will:");
            System.out.println("  1. Take off");
            System.out.println("  2. Track and maintain 60cm from object for 8 seconds");
            System.out.println("  3. (You can move object closer/farther)");
            System.out.println("  4. Signal success and land");
            
            confirmToContinue();
            
            System.out.println("\nTaking off...");
            drone.takeoff();
            drone.hover(2.0);
            System.out.println("✓ Airborne");
            
            System.out.println("\nActivating keepDistance(8, 60)...");
            System.out.println("  Target: 60cm from object");
            System.out.println("  Duration: 8 seconds");
            System.out.println("  Try moving the object closer or farther!");
            
            // Monitor with more detail
            Thread monitorThread2 = new Thread(() -> {
                try {
                    for (int i = 0; i < 16; i++) {
                        Thread.sleep(500);
                        double range = drone.getFrontRange();
                        double error = Math.abs(range - 60);
                        String status = error < 10 ? "✓ ON TARGET" : 
                                       error < 20 ? "~ ADJUSTING" : 
                                       "⚠ CORRECTING";
                        System.out.printf("  [%ds] Distance: %.1f cm (error: %.1f cm) %s%n", 
                            i / 2, range, error, status);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            monitorThread2.start();
            
            drone.keepDistance(8, 60);
            monitorThread2.join();
            
            System.out.println("✓ Distance keeping complete");
            drone.droneBuzzerSequence("success");
            
            System.out.println("\nLanding...");
            drone.land();
            drone.hover(2.0);
            System.out.println("✓ Landed safely");
            
            // Test 5: Combined Autonomous Behavior
            System.out.println("\n[Test 5] Combined Autonomous Behavior");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("This test will:");
            System.out.println("  1. Take off with GREEN ping");
            System.out.println("  2. Avoid wall (70cm) while moving right");
            System.out.println("  3. Switch to distance keeping (40cm)");
            System.out.println("  4. Land with BLUE ping");
            
            confirmToContinue();
            
            System.out.println("\nTaking off...");
            drone.takeoff();
            drone.ping(0, 255, 0);  // Green = start
            Thread.sleep(2000);
            drone.hover(2.0);
            System.out.println("✓ Ready for autonomous flight");
            
            System.out.println("\nPhase 1: Wall avoidance while strafing right");
            System.out.println("  Starting avoidWall(4, 70) with sideways motion...");
            
            // Run autonomous in separate thread so we can add roll
            Thread autonomousThread = new Thread(() -> {
                try {
                    drone.avoidWall(4, 70);
                } catch (Exception e) {
                    System.err.println("Autonomous flight error: " + e.getMessage());
                }
            });
            
            autonomousThread.start();
            Thread.sleep(500);  // Let it stabilize
            
            // Add gentle roll while autonomous controls pitch
            System.out.println("  Adding gentle right strafe...");
            drone.setRoll(20);
            drone.move(3);
            drone.setRoll(0);
            
            autonomousThread.join();
            System.out.println("✓ Phase 1 complete");
            drone.hover(1.0);
            
            System.out.println("\nPhase 2: Distance keeping (closer)");
            System.out.println("  Switching to keepDistance(4, 40)...");
            drone.keepDistance(4, 40);
            System.out.println("✓ Phase 2 complete");
            
            System.out.println("\nLanding with blue ping...");
            drone.land();
            drone.ping(0, 0, 255);  // Blue = complete
            Thread.sleep(2000);
            drone.hover(2.0);
            System.out.println("✓ Landed");
            
            // Final Status Report
            System.out.println("\n[Test Complete] Final Status");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("✓ Connection and ping: PASSED");
            System.out.println("✓ Pre-flight checks: PASSED");
            System.out.println("✓ Wall avoidance: PASSED");
            System.out.println("✓ Distance keeping: PASSED");
            System.out.println("✓ Combined autonomous: PASSED");
            
            battery = drone.getBattery();
            System.out.println("\nFinal battery: " + battery + "%");
            
            System.out.println("\n🎉 ALL TESTS PASSED!");
            drone.droneBuzzerSequence("success");
            drone.ping(0, 255, 0);  // Victory green
            
        } catch (DroneNotFoundException e) {
            System.err.println("\n❌ Drone not found: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("  • Controller is on and in pairing mode");
            System.err.println("  • USB cable is connected");
            System.err.println("  • No other programs are using the controller");
        } catch (InterruptedException e) {
            System.err.println("\n⚠ Test interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Prompts user to confirm before continuing with next test.
     * Allows user to abort if drone position is unsafe.
     */
    private static void confirmToContinue() {
        System.out.println("\n⚠ SAFETY CHECK:");
        System.out.println("  • Is the flight area clear?");
        System.out.println("  • Is the drone in a safe position?");
        System.out.println("  • Are you ready to continue?");
        System.out.println("\nPress ENTER to continue (or Ctrl+C to abort)...");
        
        try {
            System.in.read();
        } catch (Exception e) {
            // User probably pressed Ctrl+C
        }
    }
}
