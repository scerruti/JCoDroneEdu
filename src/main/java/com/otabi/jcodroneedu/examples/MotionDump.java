package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;
import com.otabi.jcodroneedu.DroneSystem;
import com.otabi.jcodroneedu.protocol.dronestatus.Motion;
import com.otabi.jcodroneedu.protocol.DataType;

/**
 * Small diagnostic to dump raw Motion shorts and scaled values.
 * Usage: ./gradlew runMotionDump
 */
public class MotionDump {
    public static void main(String[] args) {
        String port = (args != null && args.length > 0) ? args[0] : null;
        try (Drone drone = new Drone()) {
            try {
                if (port != null) drone.connect(port);
                else drone.connect();
            } catch (DroneNotFoundException e) {
                System.err.println("Could not connect: " + e.getMessage());
                return;
            }

            System.out.println("Connected. Dumping Motion samples for 5s...");
            long end = System.currentTimeMillis() + 5000L;
            while (System.currentTimeMillis() < end) {
                try {
                    drone.sendRequest(DataType.Motion);
                    Thread.sleep(100);
                    Motion m = drone.getDroneStatus().getMotion();
                    if (m != null) {
                        short ax = m.getAccelX();
                        short ay = m.getAccelY();
                        short az = m.getAccelZ();
                        short ar = m.getAngleRoll();
                        short ap = m.getAnglePitch();
                        short ayaw = m.getAngleYaw();

                        // Convert accel raw -> m/s^2 using canonical scale (raw units = m/s^2 * 10)
                        double ax_ms2 = ax * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                        double ay_ms2 = ay * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                        double az_ms2 = az * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                        double roll_deg = ar * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG;
                        double pitch_deg = ap * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG;
                        double yaw_deg = ayaw * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG;

                        System.out.printf("RAW ax=%d ay=%d az=%d | a_ms2=%.2f,%.2f,%.2f | angles (deg)=%.2f,%.2f,%.2f\n",
                                (int) ax, (int) ay, (int) az,
                                ax_ms2, ay_ms2, az_ms2,
                                roll_deg, pitch_deg, yaw_deg);
                    } else {
                        System.out.println("No motion sample yet");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }

            drone.disconnect();
            System.out.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
