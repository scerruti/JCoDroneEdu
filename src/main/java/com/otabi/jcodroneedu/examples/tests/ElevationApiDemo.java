package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;

/**
 * Demonstrates the elevation API and Python compatibility.
 * 
 * Shows three ways to get altitude:
 * 1. getUncorrectedElevation() - explicit raw firmware value
 * 2. getCorrectedElevation() - explicit corrected value
 * 3. getElevation() - Python-compatible convenience method (state-based)
 */
public class ElevationApiDemo {
    
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        try {
            // Request altitude data and wait for it to arrive
            System.out.println("Reading sensor data...");
            for (int i = 0; i < 10; i++) {
                drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.Altitude);
                Thread.sleep(100);
            }
            
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              Elevation API Demonstration                   ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            
            // Example 1: Explicit methods (RECOMMENDED)
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("METHOD 1: Explicit Methods (Recommended for clarity)");
            System.out.println("═══════════════════════════════════════════════════════════");
            double uncorrected = drone.getUncorrectedElevation();
            double corrected = drone.getCorrectedElevation();
            System.out.printf("  Uncorrected: %.2f m  (raw firmware - has offset)\n", uncorrected);
            System.out.printf("  Corrected:   %.2f m  (accurate - use this!)\n", corrected);
            System.out.printf("  Difference:  %.2f m  (firmware offset)\n", uncorrected - corrected);
            System.out.println();
            
            // Example 2: Python-compatible default behavior
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("METHOD 2: Python-Compatible (matches Python default)");
            System.out.println("═══════════════════════════════════════════════════════════");
            double elevation = drone.getElevation();
            System.out.printf("  getElevation():  %.2f m  (returns uncorrected by default)\n", elevation);
            System.out.println();
            
            // Example 3: State-based switching
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("METHOD 3: State-Based Switching");
            System.out.println("═══════════════════════════════════════════════════════════");
            drone.useCorrectedElevation(true);
            elevation = drone.getElevation();
            System.out.println("  After useCorrectedElevation(true):");
            System.out.printf("  getElevation():  %.2f m  (now returns corrected!)\n", elevation);
            System.out.println();
            
            drone.useCorrectedElevation(false);
            elevation = drone.getElevation();
            System.out.println("  After useCorrectedElevation(false):");
            System.out.printf("  getElevation():  %.2f m  (back to uncorrected)\n", elevation);
            System.out.println();
            
            // Example 4: Custom sea-level pressure calibration
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("METHOD 4: Calibrated with Local Sea-Level Pressure");
            System.out.println("═══════════════════════════════════════════════════════════");
            double pressure = drone.getPressure();
            System.out.printf("  Current pressure: %.2f hPa\n", pressure / 100.0);
            
            // Example: If local weather reports 1015 hPa sea-level pressure
            double localSeaLevelPressure = 101500.0; // 1015 hPa in Pascals
            double calibratedElevation = drone.getCorrectedElevation(localSeaLevelPressure);
            System.out.printf("  With standard pressure (1013.25 hPa): %.2f m\n", corrected);
            System.out.printf("  With local pressure (1015 hPa):       %.2f m\n", calibratedElevation);
            System.out.println();
            
            // Example 5: Automatic weather-based calibration
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("METHOD 5: Automatic Weather-Based Calibration");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("  // Fetches current weather automatically from coordinates:");
            System.out.println("  double elevation = drone.getCorrectedElevation(37.7749, -122.4194);");
            System.out.println();
            System.out.println("  ✨ See CalibratedElevationDemo for live weather example!");
            System.out.println("     Run: ./gradlew runCalibratedElevationDemo");
            System.out.println();
            
            // Summary
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("RECOMMENDATION:");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("  ✓ For accurate altitude: use getCorrectedElevation()");
            System.out.println("  ✓ For Python compatibility: use getElevation()");
            System.out.println("  ✓ For best accuracy: use getCorrectedElevation(lat, lon)");
            System.out.println("  ✗ Avoid getUncorrectedElevation() unless debugging");
            System.out.println("═══════════════════════════════════════════════════════════");
            
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        } finally {
            drone.close();
        }
    }
}
