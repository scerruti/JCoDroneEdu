package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;
import com.otabi.jcodroneedu.DroneSystem;
import com.otabi.jcodroneedu.protocol.dronestatus.Motion;
import com.otabi.jcodroneedu.protocol.DataType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AccelTest v2 - orchestrated measurement script.
 * Sequence:
 *  - 5 s baseline on the ground
 *  - six user-guided positions (5 s each):
 *    1) Right side down
 *    2) Left side down
 *    3) Front down (nose down)
 *    4) Back down (tail down)
 *    5) Top down (upside-down)
 *    6) Bottom down (upright/top up)
 * Samples at ~10 Hz. Computes mean and standard deviation for accel X/Y/Z and angle R/P/Y.
 */
public class AccelTest {
    private static final int SAMPLE_RATE_HZ = 10;
    private static final int SAMPLE_INTERVAL_MS = 1000 / SAMPLE_RATE_HZ;

    private static class Stats {
        List<Double> ax = new ArrayList<>();
        List<Double> ay = new ArrayList<>();
        List<Double> az = new ArrayList<>();
        List<Double> r = new ArrayList<>();
        List<Double> p = new ArrayList<>();
        List<Double> y = new ArrayList<>();

        void add(Motion m) {
            // Raw accel values are in milli-g (raw units); convert to g then to m/s^2 when printing
            double rawAx = m.getAccelX();
            double rawAy = m.getAccelY();
            double rawAz = m.getAccelZ();
            // Convert raw accel -> m/s^2 using canonical scale (raw units = m/s^2 * 10)
            double ax_ms2 = rawAx * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            double ay_ms2 = rawAy * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            double az_ms2 = rawAz * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            // store as G for statistics
            ax.add(ax_ms2 / 9.80665);
            ay.add(ay_ms2 / 9.80665);
            az.add(az_ms2 / 9.80665);

            // Angles are raw degrees per reference (no /100 scaling)
            r.add((double) m.getAngleRoll() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG);
            p.add((double) m.getAnglePitch() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG);
            y.add((double) m.getAngleYaw() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG);
        }

        static double mean(List<Double> v) {
            if (v.isEmpty()) return Double.NaN;
            double s = 0;
            for (double x : v) s += x;
            return s / v.size();
        }

        static double stddev(List<Double> v) {
            if (v.size() < 2) return Double.NaN;
            double mu = mean(v);
            double s = 0;
            for (double x : v) s += (x - mu) * (x - mu);
            return Math.sqrt(s / (v.size() - 1));
        }

        void print(String label) {
            System.out.printf(Locale.US, "\n=== %s ===\n", label);
            // Compute means/stddevs in G units, convert to m/s^2 for human-readable output
            double meanAxG = mean(ax);
            double meanAyG = mean(ay);
            double meanAzG = mean(az);
            double sdAxG = stddev(ax);
            double sdAyG = stddev(ay);
            double sdAzG = stddev(az);
            double meanAx = meanAxG * 9.80665;
            double meanAy = meanAyG * 9.80665;
            double meanAz = meanAzG * 9.80665;
            double sdAx = sdAxG * 9.80665;
            double sdAy = sdAyG * 9.80665;
            double sdAz = sdAzG * 9.80665;

            System.out.printf(Locale.US, "Accel X mean=%.3f sd=%.3f m/s^2 (%.3f g)%n", meanAx, sdAx, meanAxG);
            System.out.printf(Locale.US, "Accel Y mean=%.3f sd=%.3f m/s^2 (%.3f g)%n", meanAy, sdAy, meanAyG);
            System.out.printf(Locale.US, "Accel Z mean=%.3f sd=%.3f m/s^2 (%.3f g)%n", meanAz, sdAz, meanAzG);
            System.out.printf(Locale.US, "Angle R mean=%.2f sd=%.2f deg%n", mean(r), stddev(r));
            System.out.printf(Locale.US, "Angle P mean=%.2f sd=%.2f deg%n", mean(p), stddev(p));
            System.out.printf(Locale.US, "Angle Y mean=%.2f sd=%.2f deg%n", mean(y), stddev(y));

            // Magnitude check (G)
            double magG = Math.sqrt(meanAxG * meanAxG + meanAyG * meanAyG + meanAzG * meanAzG);
            System.out.printf(Locale.US, "Magnitude mean=%.3f g (%.3f m/s^2)%n", magG, magG * 9.80665);
            if (Double.isFinite(magG) && (magG < 0.5 || magG > 2.0)) {
                System.out.println("WARNING: magnitude out of expected ~1g range - possible parsing/scale issue or noisy data");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("AccelTest v2 - guided orientation capture");
        String port = null;
        boolean auto = false;
        if (args != null) {
            for (String a : args) {
                if ("--auto".equals(a) || "-a".equals(a)) auto = true;
                else if (!a.startsWith("--")) port = a;
            }
        }

        Drone drone = new Drone();
        try {
            try {
                if (port != null) {
                    System.out.println("Connecting to port: " + port);
                    drone.connect(port);
                } else {
                    System.out.println("Auto-detecting controller port...");
                    drone.connect();
                }
            } catch (DroneNotFoundException e) {
                System.err.println("Could not connect to controller: " + e.getMessage());
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            // Warm-up baseline capture on the ground for 5 seconds
            Stats baseline = captureWindow(drone, "Baseline (5s) - drone on the ground, stationary", 5);
            baseline.print("Baseline");

        String[] positions = new String[]{
            "Right side down (right face on table)",
            "Left side down (left face on table)",
            "Front down (nose down)",
            "Back down (tail down)",
            "Top down (upside-down)",
            "Bottom down (upright)",
            // Diagonal positions to improve classifier coverage
            "Front-right down (nose+right)",
            "Back-right down (tail+right)",
            "Back-left down (tail+left)"
        };

            Stats[] results = new Stats[positions.length];

            for (int i = 0; i < positions.length; i++) {
                System.out.println();
                System.out.println("Position " + (i + 1) + "/" + positions.length + ": " + positions[i]);
                System.out.println("Place the drone in this orientation and press Enter to start a 5s capture...");
                if (!auto) in.readLine();
                results[i] = captureWindow(drone, "Capture: " + positions[i], 5);
                results[i].print(positions[i]);
                System.out.println("Capture complete. Take drone back to neutral and press Enter when ready for next position.");
                if (!auto) in.readLine();
            }

            System.out.println("All captures complete. Summary:");
            for (int i = 0; i < positions.length; i++) {
                results[i].print("Summary - " + positions[i]);
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (drone != null) drone.disconnect();
            } catch (Exception ignore) {
            }
            System.out.println("Disconnected.");
        }
    }

    private static Stats captureWindow(Drone drone, String label, int seconds) {
        System.out.println(label + ": capturing for " + seconds + " seconds at ~" + SAMPLE_RATE_HZ + "Hz");
        Stats s = new Stats();
        long end = System.currentTimeMillis() + seconds * 1000L;
        while (System.currentTimeMillis() < end) {
            try {
                drone.sendRequest(DataType.Motion);
                Thread.sleep(80); // give receiver time
                Motion m = drone.getDroneStatus().getMotion();
                if (m != null) s.add(m);
                Thread.sleep(Math.max(1, SAMPLE_INTERVAL_MS - 80));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Capture error: " + e.getMessage());
            }
        }
        System.out.println("Collected " + s.ax.size() + " samples.");
        return s;
    }
}

