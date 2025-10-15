package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneSystem;
import com.otabi.jcodroneedu.ErrorData;

import java.util.Set;

/**
 * Demonstrates error monitoring and safety checks using both Python-compatible
 * and Java-idiomatic approaches.
 * 
 * <p>This example shows:
 * <ul>
 *   <li>Basic error checking for critical conditions</li>
 *   <li>Comparison between array-based and object-based APIs</li>
 *   <li>Monitoring errors during flight operations</li>
 *   <li>Responding to specific error conditions</li>
 * </ul>
 * 
 * <p><b>Usage:</b>
 * <ul>
 *   <li>{@code java ErrorMonitoringExample} - Demo mode (no connection, shows API usage)</li>
 *   <li>{@code java ErrorMonitoringExample --connect} - Connect to drone, read real error data</li>
 *   <li>{@code java ErrorMonitoringExample --fly} - Full demo with actual flight operations</li>
 * </ul>
 * 
 * @author JCoDroneEdu Development Team
 */
public class ErrorMonitoringExample {

    private static boolean shouldConnect = false;
    private static boolean shouldFly = false;

    public static void main(String[] args) {
        // Parse command line arguments
        for (String arg : args) {
            if ("--connect".equals(arg)) {
                shouldConnect = true;
                System.out.println("Mode: CONNECT - Will connect to drone and read real error data");
            } else if ("--fly".equals(arg)) {
                shouldConnect = true;
                shouldFly = true;
                System.out.println("Mode: FLY - Will connect to drone and perform flight operations");
                System.out.println("⚠️  WARNING: Ensure drone is in a safe flying area!");
            } else if ("--help".equals(arg) || "-h".equals(arg)) {
                printUsage();
                return;
            }
        }
        
        if (!shouldConnect) {
            System.out.println("Mode: DEMO - Showing API usage without hardware connection");
            System.out.println("Use --connect to read real error data, or --fly for full demo");
            System.out.println("Use --help for more information\n");
        }
        
        Drone drone = new Drone();
        
        System.out.println("\n=== CoDrone EDU Error Monitoring Example ===\n");
        
        // Connect to drone if requested
        if (shouldConnect) {
            System.out.println("Connecting to drone...");
            drone.pair();
            System.out.println("✅ Connected!\n");
        }
        
        try {
            // Example 1: Basic error checking (Python-compatible array approach)
            System.out.println("\n--- Example 1: Array-Based Error Checking (Python-compatible) ---");
            demonstrateArrayBasedErrorChecking(drone);
            
            // Example 2: Type-safe error checking (Java-idiomatic approach)
            System.out.println("\n--- Example 2: Object-Based Error Checking (Java-idiomatic) ---");
            demonstrateObjectBasedErrorChecking(drone);
            
            // Example 3: Pre-flight safety check
            System.out.println("\n--- Example 3: Pre-Flight Safety Check ---");
            if (preFlightSafetyCheck(drone)) {
                System.out.println("✅ Drone passed pre-flight safety check!");
                if (shouldFly) {
                    System.out.println("Taking off...");
                    drone.takeoff();
                    Thread.sleep(3000);
                }
            } else {
                System.out.println("❌ Drone failed pre-flight safety check - DO NOT FLY!");
            }
            
            // Example 4: Continuous monitoring during flight
            System.out.println("\n--- Example 4: Error Monitoring During Flight ---");
            if (shouldFly) {
                demonstrateRealFlightMonitoring(drone);
            } else {
                demonstrateFlightMonitoring(drone);
            }
            
            // Example 5: Wait for calibration completion
            System.out.println("\n--- Example 5: Waiting for Calibration ---");
            if (shouldConnect) {
                System.out.println("Note: Calibration requires drone to be stationary on flat surface");
                System.out.println("This may take 3-10 seconds...");
                try {
                    waitForCalibrationComplete(drone);
                } catch (RuntimeException e) {
                    System.out.println("⚠️  Calibration warning: " + e.getMessage());
                    System.out.println("This is normal if drone was recently powered on or moved");
                    System.out.println("Continuing with remaining examples...");
                }
            } else {
                System.out.println("(Demo mode - showing concept only)");
                System.out.println("In real mode, this would monitor actual calibration status");
            }
            
            // Example 6: Detailed error reporting
            System.out.println("\n--- Example 6: Detailed Error Report ---");
            printDetailedErrorReport(drone);
            
            // Land if we took off
            if (shouldFly) {
                System.out.println("\nLanding drone...");
                drone.land();
                System.out.println("✅ Landed safely");
            }
            
        } catch (InterruptedException e) {
            System.err.println("Example interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("\nClosing connection...");
            drone.close();
        }
    }
    
    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.out.println("CoDrone EDU Error Monitoring Example");
        System.out.println("====================================\n");
        System.out.println("Usage: java ErrorMonitoringExample [OPTIONS]\n");
        System.out.println("Options:");
        System.out.println("  (no args)   Demo mode - Shows API usage without hardware connection");
        System.out.println("  --connect   Connect to drone and read real error data");
        System.out.println("  --fly       Full demo with actual flight operations (CAUTION!)");
        System.out.println("  --help, -h  Show this help message\n");
        System.out.println("Examples:");
        System.out.println("  java ErrorMonitoringExample              # Demo mode");
        System.out.println("  java ErrorMonitoringExample --connect    # Real error monitoring");
        System.out.println("  java ErrorMonitoringExample --fly        # Full flight demo\n");
        System.out.println("Safety Notes:");
        System.out.println("  • --fly mode will perform actual takeoff and flight maneuvers");
        System.out.println("  • Ensure drone is in a safe, open area before using --fly");
        System.out.println("  • Battery should be sufficiently charged for flight operations");
        System.out.println("  • Keep emergency stop ready during flight operations");
    }
    
    /**
     * Example 1: Python-compatible array-based error checking.
     * Uses bitwise operations on integer flags.
     */
    private static void demonstrateArrayBasedErrorChecking(Drone drone) {
        double[] errorData = drone.getErrorData();
        
        if (errorData == null) {
            System.out.println("No error data available yet");
            return;
        }
        
        // Extract values from array
        double timestamp = errorData[0];
        int sensorErrors = (int) errorData[1];
        int stateErrors = (int) errorData[2];
        
        System.out.println("Timestamp: " + timestamp + " seconds");
        System.out.println("Sensor error flags: 0x" + Integer.toHexString(sensorErrors));
        System.out.println("State error flags: 0x" + Integer.toHexString(stateErrors));
        
        // Check for specific errors using bitwise operations
        if ((stateErrors & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0) {
            System.out.println("⚠️  LOW BATTERY detected!");
        }
        
        if ((sensorErrors & DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue()) != 0) {
            System.out.println("🔄 Motion sensors calibrating...");
        }
        
        if ((stateErrors & DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE.getValue()) != 0) {
            System.out.println("⚠️  ATTITUDE NOT STABLE!");
        }
    }
    
    /**
     * Example 2: Java-idiomatic type-safe error checking.
     * Uses convenience methods and enum sets.
     */
    private static void demonstrateObjectBasedErrorChecking(Drone drone) {
        ErrorData errors = drone.getErrors();
        
        if (errors == null) {
            System.out.println("No error data available yet");
            return;
        }
        
        System.out.println("Timestamp: " + errors.getTimestamp());
        
        // Simple, readable convenience methods
        if (errors.isLowBattery()) {
            System.out.println("⚠️  LOW BATTERY detected!");
        }
        
        if (errors.isCalibrating()) {
            System.out.println("🔄 Motion sensors calibrating...");
        }
        
        if (errors.hasStateError(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE)) {
            System.out.println("⚠️  ATTITUDE NOT STABLE!");
        }
        
        // Check for any critical errors
        if (errors.hasCriticalErrors()) {
            System.out.println("🚨 CRITICAL ERROR DETECTED!");
        }
        
        // Get all active errors as sets
        Set<DroneSystem.ErrorFlagsForSensor> sensorErrors = errors.getSensorErrors();
        Set<DroneSystem.ErrorFlagsForState> stateErrors = errors.getStateErrors();
        
        if (!sensorErrors.isEmpty()) {
            System.out.println("Active sensor errors: " + sensorErrors.size());
        }
        
        if (!stateErrors.isEmpty()) {
            System.out.println("Active state errors: " + stateErrors.size());
        }
        
        if (!errors.hasAnyErrors()) {
            System.out.println("✅ All systems normal!");
        }
    }
    
    /**
     * Example 3: Pre-flight safety check.
     * Returns true if drone is safe to fly, false otherwise.
     */
    private static boolean preFlightSafetyCheck(Drone drone) {
        ErrorData errors = drone.getErrors();
        
        if (errors == null) {
            System.out.println("⚠️  Cannot verify drone status - no error data");
            return false;
        }
        
        // Check for critical errors
        if (errors.hasCriticalErrors()) {
            System.out.println("❌ Critical errors detected:");
            
            if (errors.isLowBattery()) {
                System.out.println("   - Low battery");
            }
            
            if (errors.hasStateError(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE)) {
                System.out.println("   - Attitude not stable");
            }
            
            if (errors.hasStateError(DroneSystem.ErrorFlagsForState.TAKEOFF_FAILURE_CHECK_PROPELLER_AND_MOTOR)) {
                System.out.println("   - Takeoff failure - check propeller and motor");
            }
            
            if (errors.hasStateError(DroneSystem.ErrorFlagsForState.CHECK_PROPELLER_VIBRATION)) {
                System.out.println("   - Propeller vibration detected");
            }
            
            return false;
        }
        
        // Check if still calibrating
        if (errors.isCalibrating()) {
            System.out.println("⏳ Waiting for calibration to complete...");
            return false;
        }
        
        // Check for sensor issues
        if (errors.hasSensorError(DroneSystem.ErrorFlagsForSensor.MOTION_NO_ANSWER)) {
            System.out.println("❌ Motion sensor not responding");
            return false;
        }
        
        if (errors.hasSensorError(DroneSystem.ErrorFlagsForSensor.PRESSURE_NO_ANSWER)) {
            System.out.println("❌ Barometer not responding");
            return false;
        }
        
        return true;
    }
    
    /**
     * Example 4a: Demonstrate flight monitoring concept (demo mode).
     * Shows the API without actual flight.
     */
    private static void demonstrateFlightMonitoring(Drone drone) {
        System.out.println("(Demo mode - simulating flight monitoring pattern)");
        System.out.println("Showing how to monitor errors during flight...\n");
        
        // Simulate periodic error checks
        for (int i = 0; i < 5; i++) {
            ErrorData errors = drone.getErrors();
            
            System.out.print("Check " + (i + 1) + ": ");
            
            if (errors != null) {
                if (errors.hasAnyErrors()) {
                    System.out.println("Errors detected!");
                    
                    // Show what we would do for critical errors
                    if (errors.isLowBattery()) {
                        System.out.println("   🔋 Low battery - would initiate emergency landing!");
                    }
                    
                    if (errors.hasStateError(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE)) {
                        System.out.println("   ⚠️  Attitude unstable - would reduce maneuvers");
                    }
                    
                    if (errors.hasSensorError(DroneSystem.ErrorFlagsForSensor.FLOW_NO_ANSWER)) {
                        System.out.println("   📷 Optical flow sensor not responding");
                    }
                } else {
                    System.out.println("✅ All systems normal (no real hardware connected)");
                }
            } else {
                System.out.println("No error data (demo mode)");
            }
            
            // Wait before next check
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Example 4b: Monitor errors during actual flight operations.
     * Performs real flight with continuous error monitoring and safety responses.
     */
    private static void demonstrateRealFlightMonitoring(Drone drone) throws InterruptedException {
        System.out.println("Monitoring errors during real flight operations...");
        System.out.println("Performing hover and maneuvers with continuous monitoring\n");
        
        boolean emergencyLandingTriggered = false;
        
        // Perform flight maneuvers with periodic error checks
        for (int i = 0; i < 10 && !emergencyLandingTriggered; i++) {
            ErrorData errors = drone.getErrors();
            
            System.out.print("Check " + (i + 1) + ": ");
            
            if (errors != null) {
                if (errors.hasAnyErrors()) {
                    System.out.println("Errors detected!");
                    
                    // Handle critical errors immediately
                    if (errors.isLowBattery()) {
                        System.out.println("   🔋 LOW BATTERY - EMERGENCY LANDING!");
                        drone.emergencyStop();
                        drone.land();
                        emergencyLandingTriggered = true;
                        break;
                    }
                    
                    if (errors.hasCriticalErrors()) {
                        System.out.println("   🚨 CRITICAL ERROR - EMERGENCY LANDING!");
                        drone.emergencyStop();
                        drone.land();
                        emergencyLandingTriggered = true;
                        break;
                    }
                    
                    if (errors.hasStateError(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE)) {
                        System.out.println("   ⚠️  Attitude unstable - hovering to stabilize");
                        drone.hover(1.0);
                    }
                    
                    // Log sensor errors for diagnostics
                    if (errors.hasSensorError(DroneSystem.ErrorFlagsForSensor.FLOW_NO_ANSWER)) {
                        System.out.println("   📷 Optical flow sensor not responding");
                    }
                    
                    if (errors.hasSensorError(DroneSystem.ErrorFlagsForSensor.PRESSURE_NO_ANSWER)) {
                        System.out.println("   🌡️  Barometer not responding");
                    }
                } else {
                    System.out.println("✅ All systems normal");
                    
                    // Perform gentle maneuver if everything is ok
                    if (i % 3 == 0) {
                        System.out.println("   → Performing gentle test maneuver");
                        drone.setRoll(10);
                        Thread.sleep(500);
                        drone.hover(0.5);
                    }
                }
            } else {
                System.out.println("⚠️  No error data available");
            }
            
            // Wait before next check
            Thread.sleep(1000);
        }
        
        if (!emergencyLandingTriggered) {
            System.out.println("\n✅ Flight monitoring complete - all checks passed");
        } else {
            System.out.println("\n⚠️  Emergency landing executed due to error condition");
        }
    }
    
    /**
     * Example 5: Wait for calibration to complete.
     * Demonstrates monitoring a specific error condition.
     */
    private static void waitForCalibrationComplete(Drone drone) throws InterruptedException {
        System.out.println("Initiating gyro reset...");
        drone.resetGyro(); // This now uses the enhanced flag-monitoring internally!
        System.out.println("✅ Calibration complete!");
        System.out.println("Note: resetGyro() now uses the same error flag monitoring we demonstrated!");
    }
    
    /**
     * Example 6: Print detailed error report.
     * Shows all available error information.
     */
    private static void printDetailedErrorReport(Drone drone) {
        ErrorData errors = drone.getErrors();
        
        if (errors == null) {
            System.out.println("No error data available");
            return;
        }
        
        System.out.println("\n=== Detailed Error Report ===");
        System.out.println("Timestamp: " + errors.getTimestamp());
        System.out.println();
        
        // Use the built-in detailed string method
        System.out.println(errors.toDetailedString());
        System.out.println();
        
        // Show individual checks
        System.out.println("Quick Checks:");
        System.out.println("  Has any errors: " + errors.hasAnyErrors());
        System.out.println("  Has critical errors: " + errors.hasCriticalErrors());
        System.out.println("  Is calibrating: " + errors.isCalibrating());
        System.out.println("  Is low battery: " + errors.isLowBattery());
        System.out.println();
        
        // Show counts
        System.out.println("Error Counts:");
        System.out.println("  Sensor errors: " + errors.getSensorErrors().size());
        System.out.println("  State errors: " + errors.getStateErrors().size());
        System.out.println();
        
        // Show raw flags for debugging
        System.out.println("Raw Flags (for debugging):");
        System.out.println("  Sensor flags: 0x" + Integer.toHexString(errors.getSensorErrorFlags()));
        System.out.println("  State flags: 0x" + Integer.toHexString(errors.getStateErrorFlags()));
    }
    
    /**
     * Bonus: Comparison of both approaches for the same task.
     */
    @SuppressWarnings("unused")
    private static void compareApproaches(Drone drone) {
        System.out.println("\n=== Approach Comparison ===");
        
        // Approach 1: Array-based (Python-compatible)
        System.out.println("\nArray-based approach:");
        double[] errorArray = drone.getErrorData();
        if (errorArray != null) {
            int stateErrors = (int) errorArray[2];
            if ((stateErrors & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0) {
                System.out.println("Low battery detected using array approach");
                // drone.land();
            }
        }
        
        // Approach 2: Object-based (Java-idiomatic)
        System.out.println("\nObject-based approach:");
        ErrorData errors = drone.getErrors();
        if (errors != null && errors.isLowBattery()) {
            System.out.println("Low battery detected using object approach");
            // drone.land();
        }
        
        System.out.println("\nNote: Both approaches produce the same result,");
        System.out.println("but the object-based approach is more readable!");
    }
}
