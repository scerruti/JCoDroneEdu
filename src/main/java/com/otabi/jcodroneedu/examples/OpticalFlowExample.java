package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Example demonstrating optical flow sensor usage for advanced navigation.
 * 
 * <p>This example shows how to use the optical flow sensors to measure
 * velocity in the X and Y directions. This is useful for advanced robotics
 * curricula involving navigation algorithms and autonomous movement control.</p>
 * 
 * <p><strong>Educational Concepts:</strong></p>
 * <ul>
 *   <li>Optical flow sensor technology</li>
 *   <li>Velocity measurement and unit conversion</li>
 *   <li>Advanced navigation techniques</li>
 *   <li>Data logging and analysis</li>
 * </ul>
 * 
 * @author Educational Team
 * @since 1.0
 * @educational Advanced robotics and navigation curricula
 */
public class OpticalFlowExample {
    
    public static void main(String[] args) {
        // Initialize the drone
        Drone drone = new Drone();
        
        try {
            System.out.println("=== Optical Flow Sensor Example ===");
            System.out.println("This example demonstrates advanced navigation sensors.");
            System.out.println();
            
            // Connect to the drone (in a real scenario)
            // drone.connect(); // Uncomment for real drone usage
            
            System.out.println("Reading optical flow velocity data...");
            System.out.println();
            
            // Main monitoring loop
            for (int i = 0; i < 10; i++) {
                // Get flow velocity in different units
                double velocityX_cm = drone.get_flow_velocity_x("cm");
                double velocityY_cm = drone.get_flow_velocity_y("cm");
                double velocityX_m = drone.get_flow_velocity_x("m");
                double velocityY_m = drone.get_flow_velocity_y("m");
                
                // Get comprehensive flow data
                double[] flowData = drone.get_flow_data();
                
                // Display velocity information
                System.out.printf("Reading %d:%n", i + 1);
                System.out.printf("  X Velocity: %.2f cm/s (%.4f m/s)%n", velocityX_cm, velocityX_m);
                System.out.printf("  Y Velocity: %.2f cm/s (%.4f m/s)%n", velocityY_cm, velocityY_m);
                
                if (flowData != null) {
                    System.out.printf("  Timestamp: %.3f seconds%n", flowData[0]);
                    System.out.printf("  Raw X: %.4f m/s%n", flowData[1]);
                    System.out.printf("  Raw Y: %.4f m/s%n", flowData[2]);
                } else {
                    System.out.println("  No flow data available");
                }
                
                // Analyze movement direction
                if (Math.abs(velocityX_cm) > 1.0 || Math.abs(velocityY_cm) > 1.0) {
                    String direction = "";
                    if (velocityX_cm > 1.0) direction += "Forward ";
                    else if (velocityX_cm < -1.0) direction += "Backward ";
                    
                    if (velocityY_cm > 1.0) direction += "Right ";
                    else if (velocityY_cm < -1.0) direction += "Left ";
                    
                    System.out.printf("  Movement detected: %s%n", direction.trim());
                } else {
                    System.out.println("  Drone appears stationary");
                }
                
                System.out.println();
                
                // Brief pause between readings
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // Demonstrate deprecated method compatibility
            System.out.println("=== Backward Compatibility Demo ===");
            System.out.println("Testing deprecated methods (still supported):");
            
            @SuppressWarnings("deprecation")
            double oldStyleX = drone.get_flow_x();
            @SuppressWarnings("deprecation")
            double oldStyleY = drone.get_flow_y();
            
            double newStyleX = drone.get_flow_velocity_x();
            double newStyleY = drone.get_flow_velocity_y();
            
            System.out.printf("Old style: X=%.2f, Y=%.2f cm/s%n", oldStyleX, oldStyleY);
            System.out.printf("New style: X=%.2f, Y=%.2f cm/s%n", newStyleX, newStyleY);
            System.out.println("Values should be identical for backward compatibility.");
            System.out.println();
            
            // Educational note about units
            System.out.println("=== Unit Conversion Examples ===");
            double sampleVelocity = 0.1; // 0.1 m/s as example
            System.out.printf("Sample velocity: %.1f m/s%n", sampleVelocity);
            System.out.printf("  In centimeters: %.1f cm/s%n", sampleVelocity * 100);
            System.out.printf("  In millimeters: %.0f mm/s%n", sampleVelocity * 1000);
            System.out.printf("  In inches: %.2f in/s%n", sampleVelocity * 39.3701);
            System.out.println();
            
            System.out.println("=== Educational Applications ===");
            System.out.println("Optical flow sensors can be used for:");
            System.out.println("• Dead reckoning navigation");
            System.out.println("• Obstacle avoidance algorithms");
            System.out.println("• Precision landing systems");
            System.out.println("• Indoor navigation without GPS");
            System.out.println("• Study of optical flow in computer vision");
            
        } catch (Exception e) {
            System.err.println("Error in optical flow example: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up
            System.out.println("Example completed.");
        }
    }
    
    /**
     * Utility method to analyze flow data for educational purposes.
     * 
     * @param velocityX X-axis velocity in cm/s
     * @param velocityY Y-axis velocity in cm/s
     * @return Human-readable movement description
     */
    public static String analyzeMovement(double velocityX, double velocityY) {
        double threshold = 1.0; // cm/s threshold for detecting movement
        
        if (Math.abs(velocityX) < threshold && Math.abs(velocityY) < threshold) {
            return "Stationary";
        }
        
        String movement = "";
        
        // Analyze X-axis movement (forward/backward)
        if (velocityX > threshold) {
            movement += "Forward";
        } else if (velocityX < -threshold) {
            movement += "Backward";
        }
        
        // Analyze Y-axis movement (left/right)
        if (velocityY > threshold) {
            if (!movement.isEmpty()) movement += " + ";
            movement += "Right";
        } else if (velocityY < -threshold) {
            if (!movement.isEmpty()) movement += " + ";
            movement += "Left";
        }
        
        // Calculate total speed
        double totalSpeed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        movement += String.format(" (%.1f cm/s)", totalSpeed);
        
        return movement;
    }
}
