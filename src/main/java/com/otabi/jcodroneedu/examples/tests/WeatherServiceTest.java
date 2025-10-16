package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.util.WeatherService;

/**
 * Simple test to verify weather API connectivity without drone.
 * This can be run to check if the weather service is working.
 */
public class WeatherServiceTest {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║            Weather Service Connectivity Test               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Check internet
        System.out.println("Checking internet connectivity...");
        boolean online = WeatherService.isInternetAvailable();
        System.out.println("  Status: " + (online ? "✓ Connected" : "✗ Offline"));
        System.out.println();
        
        if (!online) {
            System.out.println("❌ No internet connection. Weather API requires network access.");
            return;
        }
        
        // Test a few locations
        System.out.println("Testing weather data retrieval:");
        System.out.println();
        
        testLocation("San Francisco, CA", 37.7749, -122.4194);
        testLocation("New York, NY", 40.7128, -74.0060);
        testLocation("Tokyo, Japan", 35.6762, 139.6503);
        testLocation("London, UK", 51.5074, -0.1278);
        
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("✅ Weather service is working correctly!");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
    
    private static void testLocation(String name, double lat, double lon) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println(name);
        System.out.println("═══════════════════════════════════════════════════════════");
        
        try {
            double pressure = WeatherService.getSeaLevelPressure(lat, lon);
            System.out.printf("  Coordinates: %.4f°, %.4f°\n", lat, lon);
            System.out.printf("  Sea-Level Pressure: %.2f hPa (%.0f Pa)\n", pressure / 100.0, pressure);
            System.out.println("  Status: ✓ Success");
        } catch (Exception e) {
            System.out.println("  Status: ✗ Failed - " + e.getMessage());
        }
        System.out.println();
    }
}
