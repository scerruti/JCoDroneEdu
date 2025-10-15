package com.otabi.jcodroneedu.examples.research;

import com.otabi.jcodroneedu.Drone;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Temperature Calibration Research Starter
 * 
 * <p>This is a simple experiment to begin investigating factors that affect
 * the temperature sensor calibration offset. Students can run this to collect
 * initial data for their research projects.</p>
 * 
 * <h3>🔬 Research Question:</h3>
 * <p>How does the temperature offset change during warm-up and flight?</p>
 * 
 * <h3>📋 Experimental Procedure:</h3>
 * <ol>
 *   <li>Power on drone (starting cold)</li>
 *   <li>Record temperature every minute for 5 minutes (warm-up phase)</li>
 *   <li>Takeoff and hover at 1 meter</li>
 *   <li>Record temperature every minute for 5 minutes (flight phase)</li>
 *   <li>Land and record recovery for 3 minutes</li>
 * </ol>
 * 
 * <p><strong>⚠️ Flight Time Constraint:</strong> Drones have ~8 minutes maximum flight time.
 * This experiment uses only 5 minutes of flight to allow safety margin and battery reserve
 * for landing.</p>
 * 
 * <h3>📊 Data Collected:</h3>
 * <ul>
 *   <li>Time since power-on</li>
 *   <li>Flight state (ground/flying)</li>
 *   <li>Sensor temperature</li>
 *   <li>Battery percentage</li>
 *   <li>Manual: Reference temperature from external thermometer</li>
 * </ul>
 * 
 * <h3>🎓 Educational Value:</h3>
 * <ul>
 *   <li>Data collection methodology</li>
 *   <li>Experimental design</li>
 *   <li>Sensor physics</li>
 *   <li>Thermal transient analysis</li>
 * </ul>
 * 
 * @educational
 * @author Temperature Calibration Research Team
 */
public class TemperatureCalibrationExperiment {
    
    private static final String CSV_FILE = "temperature_calibration_data.csv";
    private static final DateTimeFormatter TIME_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║    Temperature Calibration Research Experiment            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("This experiment will collect data about temperature sensor");
        System.out.println("behavior during warm-up and flight.");
        System.out.println();
        System.out.println("⚠️  BEFORE STARTING:");
        System.out.println("   1. Place a reference thermometer near the drone");
        System.out.println("   2. Record the reference temperature: _______°C");
        System.out.println("   3. Note environmental conditions:");
        System.out.println("      - Indoor or outdoor? _______");
        System.out.println("      - Direct sunlight? Yes / No");
        System.out.println("      - Approximate humidity? _______%");
        System.out.println("   4. Ensure drone battery is fully charged");
        System.out.println("   5. Clear flight area for safe takeoff");
        System.out.println();
        System.out.print("Press ENTER when ready to start...");
        
        try {
            System.in.read();
        } catch (Exception e) {
            // Continue
        }
        
        System.out.println("\n🔬 Starting experiment...\n");
        
        try (Drone drone = new Drone();
             PrintWriter csv = new PrintWriter(new FileWriter(CSV_FILE))) {
            
            // Write CSV header
            csv.println("timestamp,phase,runtime_sec,is_flying,battery_pct," +
                       "sensor_temp_c,notes");
            
            // Connect to drone
            System.out.println("📡 Connecting to drone...");
            drone.pair();
            System.out.println("✅ Connected!");
            System.out.println();
            
            long startTime = System.currentTimeMillis();
            
            // Phase 1: Ground warm-up (5 minutes - reduced from 10 to allow flight time)
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("PHASE 1: Ground Warm-Up (5 minutes)");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println();
            
            for (int minute = 0; minute < 5; minute++) {
                collectDataPoint(drone, csv, startTime, "ground_warmup", false);
                
                if (minute < 9) {  // Don't wait after last measurement
                    System.out.println("   Waiting 60 seconds...");
                    Thread.sleep(60000);
                }
            }
            
            System.out.println();
            System.out.println("✅ Warm-up phase complete!");
            System.out.println();
            
            // Phase 2: Flight (5 minutes hovering - max safe flight time)
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("PHASE 2: Flight Test (5 minutes)");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("⚠️  Note: Max drone flight time is ~8 minutes");
            System.out.println("   This test uses 5 minutes for safety margin");
            System.out.println();
            System.out.println("🚁 Taking off...");
            
            drone.takeoff();
            Thread.sleep(3000);  // Stabilize after takeoff
            
            System.out.println("✅ Hovering at 1 meter");
            System.out.println();
            
            for (int minute = 0; minute < 5; minute++) {
                collectDataPoint(drone, csv, startTime, "flight_hover", true);
                
                if (minute < 4) {
                    System.out.println("   Maintaining hover...");
                    Thread.sleep(60000);
                }
            }
            
            System.out.println();
            System.out.println("🛬 Landing...");
            drone.land();
            Thread.sleep(3000);
            System.out.println("✅ Landed safely");
            System.out.println();
            
            // Phase 3: Ground recovery (3 minutes - reduced to keep total experiment under 15 min)
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("PHASE 3: Thermal Recovery (3 minutes)");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println();
            
            for (int minute = 0; minute < 3; minute++) {
                collectDataPoint(drone, csv, startTime, "ground_recovery", false);
                
                if (minute < 4) {
                    System.out.println("   Waiting 60 seconds...");
                    Thread.sleep(60000);
                }
            }
            
            System.out.println();
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("✅ EXPERIMENT COMPLETE!");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("📊 Data saved to: " + CSV_FILE);
            System.out.println();
            System.out.println("⏱️  Total Experiment Time: ~13 minutes");
            System.out.println("   - Phase 1 (Ground warm-up): 5 minutes");
            System.out.println("   - Phase 2 (Flight test): 5 minutes");
            System.out.println("   - Phase 3 (Recovery): 3 minutes");
            System.out.println();
            System.out.println("🔋 Flight time used: 5 minutes (max ~8 minutes available)");
            System.out.println();
            System.out.println("📝 NEXT STEPS:");
            System.out.println("   1. Add your reference temperature to the CSV file");
            System.out.println("      (Add a column: reference_temp_c)");
            System.out.println("   2. Calculate offset for each row:");
            System.out.println("      offset_c = reference_temp_c - sensor_temp_c");
            System.out.println("   3. Plot offset vs runtime_sec");
            System.out.println("   4. Plot offset vs phase (ground/flight/recovery)");
            System.out.println("   5. Analyze the data:");
            System.out.println("      - Does offset change during warm-up?");
            System.out.println("      - Is offset different when flying?");
            System.out.println("      - How long does thermal recovery take?");
            System.out.println();
            System.out.println("🔬 See TEMPERATURE_CALIBRATION_RESEARCH.md for analysis tips!");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("❌ Error during experiment: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Collect and record a single data point.
     */
    private static void collectDataPoint(
        Drone drone,
        PrintWriter csv,
        long startTime,
        String phase,
        boolean isFlying) {
        
        long currentTime = System.currentTimeMillis();
        double runtimeSec = (currentTime - startTime) / 1000.0;
        
        // Request fresh sensor data
        drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.Altitude);
        
        // Small delay to ensure data arrives
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Continue
        }
        
        // Collect data
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        double sensorTemp = drone.getUncalibratedTemperature();
        int battery = drone.getBattery();
        
        // Display to console
        System.out.printf("⏱️  %.0f sec | Phase: %-15s | Flight: %-5s | " +
                         "Sensor: %6.2f°C | Battery: %3d%%\n",
            runtimeSec, phase, isFlying ? "YES" : "NO", sensorTemp, battery);
        
        // Write to CSV
        csv.printf("%s,%s,%.0f,%d,%d,%.2f,%s\n",
            timestamp,
            phase,
            runtimeSec,
            isFlying ? 1 : 0,
            battery,
            sensorTemp,
            ""  // Notes column for manual annotations
        );
        csv.flush();  // Ensure data is written immediately
    }
}
