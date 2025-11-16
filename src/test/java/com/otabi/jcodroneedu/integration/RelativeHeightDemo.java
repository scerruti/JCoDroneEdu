package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.DataType;

/**
 * Demonstrates relative height measurement using pressure reference.
 * 
 * <p>This example shows how to use {@code setInitialPressure()} and
 * {@code getHeightFromPressure()} to measure height changes relative
 * to a starting position. This is perfect for:</p>
 * <ul>
 *   <li>Indoor flying (no need for GPS or sea-level pressure)</li>
 *   <li>Measuring climb/descent during flight</li>
 *   <li>Simple altitude control loops</li>
 * </ul>
 * 
 * <p><strong>⚠️ Safety:</strong> This is a non-flying demo that simulates
 * height changes by moving the drone up and down by hand.</p>
 */
public class RelativeHeightDemo {
    
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        try {
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║          Relative Height Measurement Demo                  ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("This demo shows how to measure height changes relative to");
            System.out.println("a starting position using barometric pressure.");
            System.out.println();
            
            // Stabilize sensors
            System.out.println("Initializing sensors...");
            for (int i = 0; i < 10; i++) {
                drone.sendRequest(DataType.Altitude);
                Thread.sleep(100);
            }
            
            // Set initial pressure at current position
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("STEP 1: Set Reference Point");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("Setting current position as reference (height = 0)...");
            drone.setInitialPressure();
            Thread.sleep(500);
            
            double initialPressure = drone.getPressure();
            System.out.printf("  Reference pressure: %.2f hPa\n", initialPressure / 100.0);
            System.out.printf("  Initial height: %.2f mm\n", drone.getHeightFromPressure());
            System.out.println();
            
            // Demonstrate height measurement
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("STEP 2: Measure Height Changes");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("Monitoring height changes for 30 seconds...");
            System.out.println();
            System.out.println("📋 TRY THIS:");
            System.out.println("  • Lift the drone up → see positive height");
            System.out.println("  • Lower the drone → see negative height");
            System.out.println("  • Return to start → height returns to ~0");
            System.out.println();
            System.out.println("Height readings (update every 0.5s):");
            System.out.println("─────────────────────────────────────────────────────────");
            
            long startTime = System.currentTimeMillis();
            long duration = 30000; // 30 seconds
            
            int reading = 0;
            while (System.currentTimeMillis() - startTime < duration) {
                drone.sendRequest(DataType.Altitude);
                Thread.sleep(100);
                
                double heightMm = drone.getHeightFromPressure();
                double heightCm = heightMm / 10.0;
                double heightM = heightMm / 1000.0;
                double currentPressure = drone.getPressure();
                
                // Print every 5th reading (every 0.5 seconds)
                if (reading % 5 == 0) {
                    long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                    System.out.printf("[%2ds] Height: %7.2f mm = %6.2f cm = %5.3f m  |  Pressure: %.2f hPa\n",
                        elapsed, heightMm, heightCm, heightM, currentPressure / 100.0);
                }
                reading++;
            }
            
            System.out.println("─────────────────────────────────────────────────────────");
            System.out.println();
            
            // Compare with absolute elevation
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("STEP 3: Compare Measurement Methods");
            System.out.println("═══════════════════════════════════════════════════════════");
            
            double relativeHeightMm = drone.getHeightFromPressure();
            double relativeHeightM = relativeHeightMm / 1000.0;
            double absoluteElevation = drone.getCorrectedElevation();
            double uncorrectedElevation = drone.getUncorrectedElevation();
            
            System.out.println("Current measurements:");
            System.out.printf("  1. Relative Height:    %7.2f mm (%6.3f m)\n", 
                relativeHeightMm, relativeHeightM);
            System.out.printf("  2. Absolute Elevation: %7.2f m (corrected)\n", 
                absoluteElevation);
            System.out.printf("  3. Firmware Elevation: %7.2f m (uncorrected)\n", 
                uncorrectedElevation);
            System.out.println();
            
            // Demonstrate custom calibration
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("STEP 4: Custom Calibration (Advanced)");
            System.out.println("═══════════════════════════════════════════════════════════");
            
            double defaultHeight = drone.getHeightFromPressure();
            double adjusted1 = drone.getHeightFromPressure(0, 10.0);  // More sensitive
            double adjusted2 = drone.getHeightFromPressure(0, 8.0);   // Less sensitive
            double offsetted = drone.getHeightFromPressure(5.0, 9.34); // +5 Pa offset
            
            System.out.println("Height with different calibration:");
            System.out.printf("  Default (b=0, m=9.34):  %7.2f mm\n", defaultHeight);
            System.out.printf("  Sensitive (b=0, m=10.0): %7.2f mm  (+%.0f%%)\n", 
                adjusted1, ((adjusted1 - defaultHeight) / defaultHeight * 100));
            System.out.printf("  Conservative (b=0, m=8.0): %7.2f mm  (%.0f%%)\n", 
                adjusted2, ((adjusted2 - defaultHeight) / defaultHeight * 100));
            System.out.printf("  Offset (b=5, m=9.34):   %7.2f mm  (%.0f mm shift)\n", 
                offsetted, offsetted - defaultHeight);
            System.out.println();
            
            // Summary
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("SUMMARY:");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("✅ Relative Height Measurement:");
            System.out.println("   • Use setInitialPressure() at starting position");
            System.out.println("   • Call getHeightFromPressure() to get height change");
            System.out.println("   • Returns millimeters above/below reference point");
            System.out.println();
            System.out.println("📊 When to Use:");
            System.out.println("   ✓ Indoor flying (no GPS/weather needed)");
            System.out.println("   ✓ Measuring climb/descent during flight");
            System.out.println("   ✓ Simple altitude control loops");
            System.out.println("   ✓ Quick calibration without internet");
            System.out.println();
            System.out.println("🎯 Accuracy:");
            System.out.println("   • ±5-10mm in stable conditions");
            System.out.println("   • Better for relative changes than absolute position");
            System.out.println("   • Affected by temperature and weather changes");
            System.out.println("═══════════════════════════════════════════════════════════");
            
        } catch (InterruptedException e) {
            System.out.println("\nDemo interrupted!");
        } finally {
            drone.close();
        }
    }
}
