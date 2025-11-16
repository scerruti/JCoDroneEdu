package com.otabi.jcodroneedu.examples;

/**
 * Educational example showing how to use the CoDrone EDU reset and trim API
 * for gyroscope calibration and flight balance adjustments.
 * 
 * This example demonstrates various reset and trim methods that students can use
 * to maintain accurate flight control and compensate for manufacturing variations.
 */
public class ResetAndTrimExample {
    public static void main(String[] args) {
        System.out.println("CoDrone EDU Reset and Trim API - Educational Example");
        System.out.println("===================================================");
        System.out.println();
        
        try {
            // Note: This example shows method usage but doesn't connect to actual hardware
            // In a real classroom, students would have their drones connected
            
            System.out.println("Example 1: Gyroscope Calibration");
            System.out.println("--------------------------------");
            System.out.println("// Calibrate the gyroscope for accurate angle readings");
            System.out.println("// Important: Place drone on flat surface and keep it stationary!");
            System.out.println("drone.reset_gyro();");
            System.out.println("// This process takes a few seconds and ensures accurate flight control");
            System.out.println();
            
            System.out.println("Example 2: Setting Trim Values");  
            System.out.println("------------------------------");
            System.out.println("// If your drone drifts during hover, adjust trim to compensate");
            System.out.println("drone.set_trim(5, -2);    // Slight right roll, slight back pitch");
            System.out.println("drone.set_trim(-10, 0);   // Left roll correction, no pitch change");
            System.out.println("drone.set_trim(0, 8);     // No roll change, forward pitch correction");
            System.out.println();
            
            System.out.println("Example 3: Reading Current Trim Values");
            System.out.println("--------------------------------------");
            System.out.println("// Check what trim values are currently applied");
            System.out.println("int[] currentTrim = drone.get_trim();");
            System.out.println("// Returns array: [roll_trim, pitch_trim]");
            System.out.println("// Example result: [5, -2] means 5 right roll, 2 back pitch");
            System.out.println();
            
            System.out.println("Example 4: Resetting Trim to Neutral");
            System.out.println("------------------------------------");
            System.out.println("// Reset all trim values back to zero (neutral)");
            System.out.println("drone.reset_trim();");
            System.out.println("// This is equivalent to drone.set_trim(0, 0)");
            System.out.println();
            
            System.out.println("Example 5: Complete Calibration Sequence");
            System.out.println("----------------------------------------");
            System.out.println("// Recommended setup sequence for accurate flight:");
            System.out.println("// 1. Reset trim to neutral");
            System.out.println("drone.reset_trim();");
            System.out.println();
            System.out.println("// 2. Calibrate gyroscope (drone must be still!)");
            System.out.println("drone.reset_gyro();");
            System.out.println();
            System.out.println("// 3. Test flight and adjust trim if needed");
            System.out.println("drone.takeoff();");
            System.out.println("Thread.sleep(3000);  // Observe drift direction");
            System.out.println("drone.land();");
            System.out.println();
            System.out.println("// 4. Adjust trim based on drift observation");
            System.out.println("// If drone drifted right: drone.set_trim(-5, 0);");
            System.out.println("// If drone drifted forward: drone.set_trim(0, -3);");
            System.out.println();
            
            System.out.println("Example 6: Trim Value Guidelines");
            System.out.println("--------------------------------");
            System.out.println("// Trim values range from -100 to +100");
            System.out.println("// Roll trim: positive = tilt right, negative = tilt left");
            System.out.println("// Pitch trim: positive = tilt forward, negative = tilt backward");
            System.out.println("// Start with small adjustments (±5) and increase gradually");
            System.out.println();
            System.out.println("// Small corrections (most common):");
            System.out.println("drone.set_trim(3, -2);    // Slight adjustments");
            System.out.println();
            System.out.println("// Medium corrections:");
            System.out.println("drone.set_trim(-15, 10);  // More noticeable drift");
            System.out.println();
            System.out.println("// Large corrections (rarely needed):");
            System.out.println("drone.set_trim(25, -20);  // Significant manufacturing variation");
            System.out.println();
            
            System.out.println("Example 7: Error Handling");
            System.out.println("-------------------------");
            System.out.println("try {");
            System.out.println("    drone.set_trim(150, 0);  // This will fail - too high!");
            System.out.println("} catch (IllegalArgumentException e) {");
            System.out.println("    System.out.println(\"Trim values must be -100 to +100\");");
            System.out.println("}");
            System.out.println();
            
            System.out.println("Classroom Learning Objectives:");
            System.out.println("==============================");
            System.out.println("• Understanding sensor calibration and why it's important");
            System.out.println("• Learning about manufacturing tolerances and compensation");
            System.out.println("• Practicing systematic troubleshooting (observe → analyze → adjust)");
            System.out.println("• Using arrays to handle multiple return values");
            System.out.println("• Input validation and error handling");
            System.out.println("• Real-world application of coordinate systems (roll/pitch)");
            System.out.println();
            
            System.out.println("Safety Reminders:");
            System.out.println("=================");
            System.out.println("⚠️  Always place drone on flat, stable surface during reset_gyro()");
            System.out.println("⚠️  Start with small trim adjustments (±5) and test before increasing");
            System.out.println("⚠️  Reset trim before lending drone to another student");
            System.out.println("⚠️  Recalibrate if drone was dropped or hit hard");
            System.out.println();
            
            System.out.println("✅ The reset and trim API provides essential tools for maintaining");
            System.out.println("   accurate flight control and teaching important robotics concepts!");
            
        } catch (Exception e) {
            System.err.println("Error in reset and trim example: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
