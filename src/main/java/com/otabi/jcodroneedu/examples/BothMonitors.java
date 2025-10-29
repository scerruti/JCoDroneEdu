package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.SensorMonitor;
import com.otabi.jcodroneedu.gui.ControllerMonitor;
import com.otabi.jcodroneedu.protocol.DataType;

/**
 * Interactive demo for testing both monitors together.
 * 
 * Features:
 * - Hold L1 for 3 seconds to takeoff (with buzzer warning)
 * - Test LAND and EMERGENCY STOP buttons in sensor monitor
 * - See real-time sensor and controller updates
 * 
 * @educational Use both monitors together to see everything your drone is doing!
 * 
 * @author CoDrone EDU Development Team
 * @since 1.0
 */
public class BothMonitors {
    
    public static void main(String[] args) {
        // Connect to drone
        Drone drone = new Drone();
        drone.pair();
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     CoDrone EDU - Dual Monitor Interactive Demo           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Open BOTH monitors with just two lines!
        new SensorMonitor(drone);
        new ControllerMonitor(drone);
        
        System.out.println("✓ Both monitors are running!");
        System.out.println("  - Left window: Sensor Monitor (with LAND and STOP buttons)");
        System.out.println("  - Right window: Controller Monitor (watch input in real-time)");
        System.out.println();
        
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("INTERACTIVE CONTROLS:");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  L1 Button:  Hold for 3 seconds to TAKEOFF");
        System.out.println("              (buzzer warning with increasing urgency)");
        System.out.println();
        System.out.println("  LAND Button (blue):  Click in sensor monitor to land");
        System.out.println("  STOP Button (red):   Click in sensor monitor for emergency stop");
        System.out.println();
        System.out.println("  Controller: Move joysticks to see real-time display updates");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  Press 'Q' and ENTER to quit (will warn if drone is flying)");
        System.out.println();
        
        // Keyboard input buffer for non-blocking reads
        final StringBuilder inputBuffer = new StringBuilder();
        
        // Main loop - wait for L1 button to be held
        boolean flying = false;
        boolean wasFlying = false;
        
        while (true) {
            try {
                // Non-blocking keyboard check
                if (System.in.available() > 0) {
                    int ch = System.in.read();
                    if (ch == '\n' || ch == '\r') {
                        // Check if buffer contains 'Q'
                        String input = inputBuffer.toString().trim();
                        if (input.equalsIgnoreCase("Q")) {
                            if (flying) {
                                System.out.println();
                                System.out.println("⚠️  WARNING: Drone is still flying!");
                                System.out.println("   Please land the drone before exiting.");
                                System.out.println("   Use the LAND button (blue) in the Sensor Monitor.");
                                System.out.println();
                                System.out.println("   Press 'Q' again after landing to exit safely.");
                            } else {
                                System.out.println();
                                System.out.println("✓ Exiting safely...");
                                break;
                            }
                        }
                        inputBuffer.setLength(0); // Clear buffer
                    } else if (ch != -1) {
                        inputBuffer.append((char) ch);
                    }
                }
                
                // Check flight state to reset flying flag when drone lands
                drone.sendRequest(DataType.State);
                Thread.sleep(10);
                String flightMode = drone.getFlightState();
                flying = "FLIGHT".equalsIgnoreCase(flightMode);
                
                // Notify when drone has landed
                if (wasFlying && !flying) {
                    System.out.println();
                    System.out.println("✓ Drone has landed. Ready for next takeoff!");
                    System.out.println("  Press and hold L1 for 3 seconds to takeoff again.");
                    System.out.println();
                }
                wasFlying = flying;
                
                // Request button data
                drone.sendRequest(DataType.Button);
                Thread.sleep(50);
                
                // Get button data
                Object[] buttonData = drone.getButtonData();
                if (buttonData != null && buttonData.length >= 3) {
                    int buttonFlags = 0;
                    String eventName = "None_";
                    
                    if (buttonData[1] instanceof Integer) {
                        buttonFlags = (Integer) buttonData[1];
                    }
                    if (buttonData[2] instanceof String) {
                        eventName = (String) buttonData[2];
                    }
                    
                    // Check if L1 is pressed (bit 0x0001)
                    boolean l1Pressed = (buttonFlags & 0x0001) != 0 && 
                                       ("Press".equals(eventName) || "Down".equals(eventName));
                    
                    if (l1Pressed && !flying) {
                        System.out.println();
                        System.out.println(">>> L1 DETECTED! Starting takeoff sequence...");
                        
                        // 3-second countdown with buzzer warning
                        for (int i = 3; i > 0; i--) {
                            System.out.println("    Takeoff in " + i + "...");
                            
                            // Buzzer warning with increasing urgency
                            if (i == 3) {
                                // Low urgency - single beep
                                drone.droneBuzzer(500, 150);
                                Thread.sleep(850);
                            } else if (i == 2) {
                                // Medium urgency - double beep
                                drone.droneBuzzer(750, 100);
                                Thread.sleep(100);
                                drone.droneBuzzer(750, 100);
                                Thread.sleep(700);
                            } else {
                                // High urgency - triple beep (faster and higher pitch)
                                drone.droneBuzzer(1000, 80);
                                Thread.sleep(70);
                                drone.droneBuzzer(1000, 80);
                                Thread.sleep(70);
                                drone.droneBuzzer(1200, 100);
                                Thread.sleep(600);
                            }
                        }
                        
                        // Final warning beep
                        drone.droneBuzzer(1500, 200);
                        Thread.sleep(200);
                        
                        System.out.println("    🚁 TAKING OFF! Watch the sensors update!");
                        drone.takeoff();
                        flying = true;
                        
                        System.out.println();
                        System.out.println("✓ Drone is flying!");
                        System.out.println("  - Watch the altitude sensor in the Sensor Monitor");
                        System.out.println("  - Try the LAND button (blue) to land safely");
                        System.out.println("  - Try the STOP button (red) for emergency stop");
                        System.out.println("  - Move the joysticks to see controller response");
                        System.out.println();
                    }
                }
                
                Thread.sleep(50);
                
            } catch (InterruptedException e) {
                System.out.println("\nDemo interrupted. Closing...");
                break;
            } catch (Exception e) {
                System.err.println("Error in main loop: " + e.getMessage());
                e.printStackTrace();
                // Don't break - keep going unless it's critical
            }
        }
        
        // Clean shutdown
        System.out.println("Closing drone connection...");
        drone.close();
    }
}
