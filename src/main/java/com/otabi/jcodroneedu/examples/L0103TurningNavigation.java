package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

/**
 * L0103 Turning and Navigation - Demonstrates the new turning methods.
 * 
 * <p>This example showcases the turning methods added in Punch List Item #3,
 * providing Python-compatible turning APIs for educational use.</p>
 * 
 * <p><strong>Educational Objectives:</strong></p>
 * <ul>
 *   <li>Learn basic turning with power and duration</li>
 *   <li>Understand directional turning (left/right)</li>
 *   <li>Practice precise angle control</li>
 *   <li>Combine movement and turning for navigation</li>
 * </ul>
 * 
 * @author JCoDroneEdu Team
 * @version 1.0
 * @since 1.0
 */
public class L0103TurningNavigation {

    public static void main(String[] args) {
        System.out.println("=== L0103: Turning and Navigation ===");
        
        try (Drone drone = new Drone(true)) {
            // Auto-connected via constructor
            System.out.println("✅ Connected to drone!");
            
            // Basic flight setup
            drone.takeoff();
            System.out.println("🚁 Drone in the air!");
            
            // Demonstrate basic turning methods
            demonstrateBasicTurning(drone);
            
            // Demonstrate directional turning
            demonstrateDirectionalTurning(drone);
            
            // Demonstrate precise angle control
            demonstratePreciseAngleControl(drone);
            
            // Demonstrate navigation pattern
            demonstrateNavigationPattern(drone);
            
            // Safe landing
            drone.land();
            System.out.println("🛬 Drone landed safely!");
            
        } catch (DroneNotFoundException e) {
            System.err.println("❌ Could not find drone. Make sure it's paired and turned on.");
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.err.println("❌ Error during flight: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("🎯 Turning lesson complete!");
    }

    /**
     * Demonstrates basic turning with power and duration.
     */
    private static void demonstrateBasicTurning(Drone drone) {
        System.out.println("\n--- Basic Turning ---");
        
        System.out.println("💫 Turn left at 50% power for 1 second");
        drone.turn(50, 1.0);  // Positive power = left turn
        
        drone.hover(1);  // Brief hover between maneuvers
        
        System.out.println("💫 Turn right at 40% power for 1.5 seconds");
        drone.turn(-40, 1.5);  // Negative power = right turn
        
        drone.hover(1);
    }

    /**
     * Demonstrates directional turning methods.
     */
    private static void demonstrateDirectionalTurning(Drone drone) {
        System.out.println("\n--- Directional Turning ---");
        
        System.out.println("⬅️ Turn left 90 degrees (quarter turn)");
        drone.turnLeft(90);
        
        drone.hover(1);
        
        System.out.println("➡️ Turn right 90 degrees (quarter turn)");
        drone.turnRight(90);
        
        drone.hover(1);
        
        System.out.println("⬅️ Turn left 45 degrees (eighth turn)");
        drone.turnLeft(45);
        
        drone.hover(1);
    }

    /**
     * Demonstrates precise angle control.
     */
    private static void demonstratePreciseAngleControl(Drone drone) {
        System.out.println("\n--- Precise Angle Control ---");
        
        System.out.println("🎯 Turn to exact 180 degrees (about face)");
        drone.turnDegree(180);
        
        drone.hover(1);
        
        System.out.println("🎯 Turn to -45 degrees with custom timeout");
        drone.turnDegree(-45, 5.0);  // Longer timeout for precision
        
        drone.hover(1);
        
        System.out.println("🎯 Turn to 30 degrees with custom control parameters");
        drone.turnDegree(30, 4.0, 15.0);  // Custom timeout and gain
        
        drone.hover(1);
    }

    /**
     * Demonstrates navigation pattern combining movement and turning.
     */
    private static void demonstrateNavigationPattern(Drone drone) {
        System.out.println("\n--- Navigation Pattern (Square with Turns) ---");
        
        // Fly a square pattern using the new turning methods
        for (int side = 1; side <= 4; side++) {
            System.out.println("📐 Side " + side + " of square");
            
            // Move forward for one side of the square
            drone.moveForward(100, "cm", 1.0);  // 100cm forward
            
            if (side < 4) {  // Don't turn after the last side
                System.out.println("🔄 Turn right 90 degrees for next side");
                drone.turnRight(90);
                drone.hover(0.5);  // Brief pause for stability
            }
        }
        
        System.out.println("✅ Square pattern complete!");
        
        // Demonstrate obstacle avoidance pattern
        System.out.println("\n--- Obstacle Avoidance Pattern ---");
        
        System.out.println("🚀 Moving forward");
        drone.moveForward(50, "cm");
        
        System.out.println("⚠️ Obstacle detected! Turning left to avoid");
        drone.turnLeft(45);
        
        System.out.println("🚀 Moving around obstacle");
        drone.moveForward(50, "cm");
        
        System.out.println("🔄 Turning right to resume original course");
        drone.turnRight(45);
        
        System.out.println("🚀 Continuing forward");
        drone.moveForward(50, "cm");
        
        System.out.println("✅ Obstacle avoidance complete!");
    }
}
