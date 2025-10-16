package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.util.WeatherService;

/**
 * Demonstrates weather-calibrated altitude measurement.
 * 
 * <p>This example shows how to use real-time weather data to improve
 * altitude accuracy. Compares three methods:
 * <ul>
 *   <li>Uncorrected firmware altitude (has ~109m offset)</li>
 *   <li>Standard barometric formula (good baseline)</li>
 *   <li>Weather-calibrated formula (best accuracy!)</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> Update the latitude and longitude constants below
 * with your actual location for accurate results!</p>
 */
public class CalibratedElevationDemo {
    
    // ⚠️ UPDATE THESE WITH YOUR LOCATION! ⚠️
    // Find your coordinates:
    // - Google Maps: Right-click → "What's here?"
    // - iPhone Compass app: Shows coordinates at bottom
    // - Command line: curl ipinfo.io
    private static final double LATITUDE = 37.7749;   // Example: San Francisco
    private static final double LONGITUDE = -122.4194; // Example: San Francisco
    
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        try {
            // Request altitude data
            System.out.println("Reading sensor data...");
            for (int i = 0; i < 10; i++) {
                drone.sendRequest(DataType.Altitude);
                Thread.sleep(100);
            }
            
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║        Weather-Calibrated Elevation Demonstration          ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            
            // Check internet connectivity
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("NETWORK CHECK:");
            System.out.println("═══════════════════════════════════════════════════════════");
            boolean online = WeatherService.isInternetAvailable();
            System.out.println("  Internet: " + (online ? "✓ Connected" : "✗ Offline"));
            System.out.println();
            
            if (!online) {
                System.out.println("  ⚠️  Weather calibration unavailable (no internet)");
                System.out.println("  Will use standard atmosphere pressure instead.");
                System.out.println();
            }
            
            // Get current weather
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("WEATHER DATA:");
            System.out.println("═══════════════════════════════════════════════════════════");
            if (online) {
                String report = WeatherService.getWeatherReport(LATITUDE, LONGITUDE);
                System.out.println("  " + report.replace("\n", "\n  "));
            } else {
                System.out.printf("  Location: %.4f°, %.4f°\n", LATITUDE, LONGITUDE);
                System.out.println("  Sea-Level Pressure: 1013.25 hPa (standard atmosphere)");
            }
            System.out.println();
            
            // Get drone pressure sensor reading
            double dronePressure = drone.getPressure();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("DRONE PRESSURE SENSOR:");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.printf("  Measured Pressure: %.2f hPa (%.0f Pa)\n", 
                dronePressure / 100.0, dronePressure);
            System.out.println();
            
            // Compare all three methods
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("ELEVATION COMPARISON:");
            System.out.println("═══════════════════════════════════════════════════════════");
            
            double uncorrected = drone.getUncorrectedElevation();
            System.out.printf("  1. Uncorrected (firmware):    %7.2f m  ⚠️  (has offset)\n", uncorrected);
            
            double standard = drone.getCorrectedElevation();
            System.out.printf("  2. Standard correction:        %7.2f m  ✓  (good baseline)\n", standard);
            
            if (online) {
                double calibrated = drone.getCorrectedElevation(LATITUDE, LONGITUDE);
                System.out.printf("  3. Weather-calibrated:         %7.2f m  ✓✓ (best accuracy!)\n", calibrated);
                
                double improvement = Math.abs(calibrated - standard);
                System.out.println();
                System.out.printf("  Weather calibration improved accuracy by: %.2f m\n", improvement);
            } else {
                System.out.println("  3. Weather-calibrated:         N/A (no internet)");
            }
            
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("RECOMMENDATION:");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("  For BEST accuracy:");
            System.out.println("  • Use getCorrectedElevation(lat, lon) with internet");
            System.out.println("  • Updates automatically with local weather conditions");
            System.out.println("  • Accounts for pressure changes (weather fronts, etc.)");
            System.out.println();
            System.out.println("  Without internet:");
            System.out.println("  • Use getCorrectedElevation() for good baseline");
            System.out.println("  • Assumes standard atmosphere (1013.25 hPa)");
            System.out.println("  • Still much better than uncorrected firmware value");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println();
            
            if (online) {
                System.out.println("💡 TIP: Update LATITUDE and LONGITUDE constants in code");
                System.out.println("    for accurate weather calibration at your location!");
            } else {
                System.out.println("💡 TIP: Connect to internet for weather-calibrated altitude!");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            drone.close();
        }
    }
}
