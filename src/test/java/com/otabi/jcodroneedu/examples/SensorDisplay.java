package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneStatus;
import com.otabi.jcodroneedu.DroneSystem;
import com.otabi.jcodroneedu.protocol.linkmanager.Information;

import java.util.Arrays;

/**
 * Simple non-flying sensor display example modeled after the user's screenshot.
 * Connects (auto-detect) and prints a snapshot of sensors to stdout then disconnects.
 */
public class SensorDisplay {
    public static void main(String[] args) {
        try (Drone drone = new Drone()) {
            try {
                System.out.println("Auto-detecting and connecting to CoDrone EDU...");
                boolean ok = drone.pair();
                if (!ok) {
                    System.err.println("Could not connect to drone. Exiting.");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Connection failed: " + e.getMessage());
                return;
            }

            System.out.println("Connected. Gathering sensor snapshot...");

            // Information from LinkManager
            Information info = drone.getLinkManager().getInformation();
            String model = info != null && info.getModelNumber() != null ? info.getModelNumber().toString() : "UNKNOWN";
            String fw = info != null && info.getVersion() != null ? info.getVersion().toString() : "UNKNOWN";
            System.out.printf("Model: %s    Firmware: %s\n", model, fw);

            // DroneStatus snapshot
            DroneStatus status = drone.getDroneStatus();

            // Battery
            try {
                System.out.printf("Battery: %d%%%n", drone.getBattery());
            } catch (Exception ignored) {
                System.out.println("Battery: N/A");
            }

            // Accelerometer / Gyro (use DroneStatus Motion object)
            System.out.println("--- Motion Sensors ---");
            try {
                com.otabi.jcodroneedu.protocol.dronestatus.Motion motion = status.getMotion();
                if (motion != null) {
                    double ax_ms2 = motion.getAccelX() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                    double ay_ms2 = motion.getAccelY() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                    double az_ms2 = motion.getAccelZ() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                    double ax_g = ax_ms2 * DroneSystem.SensorScales.ACCEL_RAW_TO_G;
                    double ay_g = ay_ms2 * DroneSystem.SensorScales.ACCEL_RAW_TO_G;
                    double az_g = az_ms2 * DroneSystem.SensorScales.ACCEL_RAW_TO_G;

                    System.out.printf("Accelerometer (m/s^2): %.3f  %.3f  %.3f\n", ax_ms2, ay_ms2, az_ms2);
                    System.out.printf("Accelerometer (g):     %.3f  %.3f  %.3f\n", ax_g, ay_g, az_g);
                    System.out.printf("Gyro (deg/s):          %d  %d  %d\n", motion.getGyroRoll(), motion.getGyroPitch(), motion.getGyroYaw());
                    System.out.printf("Angles (deg):          %.1f  %.1f  %.1f\n",
                            motion.getAngleRoll() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG,
                            motion.getAnglePitch() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG,
                            motion.getAngleYaw() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG);
                } else {
                    System.out.println("Motion: N/A");
                }
            } catch (Exception e) {
                System.out.println("Motion: N/A");
            }

            // Ranges
            System.out.println("--- Ranges (cm) ---");
            try {
                System.out.printf("Front: %.1f   Bottom: %.1f\n", status.getRange().getFront(), status.getRange().getBottom());
            } catch (Exception e) {
                System.out.println("Ranges: N/A");
            }

            // Pressure / Temperature
            System.out.println("--- Environment ---");
            try {
                System.out.printf("Pressure: %.3f kPa\n", status.getAltitude().getPressure() / 1000.0);
            } catch (Exception e) {
                System.out.println("Pressure: N/A");
            }
            try {
                System.out.printf("Temperature: %.2f °C\n", status.getAltitude().getTemperature());
            } catch (Exception e) {
                System.out.println("Temperature: N/A");
            }

            // Colors (front/back)
            System.out.println("--- Colors ---");
            try {
                int[] colors = drone.getColors();
                System.out.println("Colors (array): " + Arrays.toString(colors));
            } catch (Exception e) {
                System.out.println("Colors: N/A");
            }

            // Position
            System.out.println("--- Position ---");
                try {
                // position values are returned in millimeters by the protocol — convert to centimeters for display
                System.out.printf("X: %.2f cm   Y: %.2f cm   Z: %.2f cm\n",
                        status.getPosition().getX() / 10.0, status.getPosition().getY() / 10.0, status.getPosition().getZ() / 10.0);
            } catch (Exception e) {
                System.out.println("Position: N/A");
            }

            System.out.println("--- End of snapshot. Disconnecting.");

        } catch (Exception e) {
            System.err.println("Unhandled error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
