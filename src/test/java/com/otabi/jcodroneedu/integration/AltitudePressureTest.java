package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.dronestatus.Altitude;
import com.otabi.jcodroneedu.protocol.dronestatus.Range;

/**
 * Test to display altitude, pressure, and height values.
 * 
 * This test demonstrates the new elevation API:
 * - getUncorrectedElevation() - raw firmware value (has ~100-150m offset)
 * - getCorrectedElevation() - accurate altitude from barometric formula
 * - Atmospheric pressure (accurate)
 * - Range height (bottom sensor distance)
 * - Temperature
 * 
 * Known Issue: Drone firmware reports altitude with +100 to +150m offset.
 * Always use getCorrectedElevation() for accurate altitude measurements.
 */
public class AltitudePressureTest {
    
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         Altitude, Pressure, and Height Test                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Reading sensor data...");
        System.out.println("Press Ctrl+C to exit");
        System.out.println();
        
        try {
            while (true) {
                // Request altitude and range data
                drone.sendRequest(DataType.Altitude);
                drone.sendRequest(DataType.Range);
                Thread.sleep(100);
                
                // Get the data
                Altitude alt = drone.getDroneStatus().getAltitude();
                Range range = drone.getDroneStatus().getRange();
                
                if (alt != null) {
                    // Get raw values
                    float pressure = alt.getPressure(); // in Pascals
                    float temperature = alt.getTemperature(); // in Celsius
                    float rangeHeight = alt.getRangeHeight(); // in meters
                    
                    // Get altitude values using new API
                    double droneAltitude = drone.getUncorrectedElevation(); // in meters (with firmware offset)
                    double calculatedAltitude = drone.getCorrectedElevation(); // corrected using barometric formula
                    
                    // Calculate the offset
                    double offset = droneAltitude - calculatedAltitude;
                    
                    // Get bottom range sensor value
                    int bottomRange = (range != null) ? range.getBottom() : 0;
                    
                    // Clear screen (ANSI escape code)
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    
                    System.out.println("╔════════════════════════════════════════════════════════════╗");
                    System.out.println("║         Altitude, Pressure, and Height Test                ║");
                    System.out.println("╚════════════════════════════════════════════════════════════╝");
                    System.out.println();
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.println("PRESSURE SENSOR:");
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.printf("  Pressure:      %.2f hPa  (%.3f kPa)\n", pressure / 100.0, pressure / 1000.0);
                    System.out.printf("  Temperature:   %.2f °C\n", temperature);
                    System.out.println();
                    
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.println("ALTITUDE READINGS:");
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.printf("  Drone Reports:     %.2f m  ⚠️  (has offset)\n", droneAltitude);
                    System.out.printf("  Calculated Actual: %.2f m  ✓  (from pressure)\n", calculatedAltitude);
                    System.out.printf("  Offset Error:      %.2f m\n", offset);
                    System.out.println();
                    
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.println("HEIGHT SENSORS:");
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.printf("  Range Height:      %.2f m  (from Altitude packet)\n", rangeHeight);
                    System.out.printf("  Bottom Range:      %d mm  (from Range packet)\n", bottomRange);
                    System.out.printf("  Bottom Range:      %.2f m\n", bottomRange / 1000.0);
                    System.out.println();
                    
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.println("EXPLANATION:");
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.println("  • Pressure is accurate");
                    System.out.println("  • Drone altitude has ~100-150m offset (firmware issue)");
                    System.out.println("  • Calculated altitude uses standard barometric formula");
                    System.out.println("  • Range height is distance to ground (when flying)");
                    System.out.println("═══════════════════════════════════════════════════════════");
                    System.out.println();
                    System.out.println("Press Ctrl+C to exit");
                }
                
                Thread.sleep(900); // Update every 1 second total
            }
            
        } catch (InterruptedException e) {
            System.out.println("\nTest interrupted.");
        } finally {
            drone.close();
        }
    }
}
