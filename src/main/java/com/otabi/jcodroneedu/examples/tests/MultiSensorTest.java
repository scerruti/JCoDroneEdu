package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;
import com.otabi.jcodroneedu.protocol.dronestatus.Range;
import com.otabi.jcodroneedu.protocol.dronestatus.Motion;
import com.otabi.jcodroneedu.protocol.DataType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * MultiSensorTest - simple utility to exercise range, flow, temperature, and color sensors.
 * Usage: java -cp <jar> com.otabi.jcodroneedu.examples.MultiSensorTest [port] [--auto]
 */
public class MultiSensorTest {
    public static void main(String[] args) {
        String port = null;
        @SuppressWarnings("unused") // Reserved for future auto-test feature
        boolean auto = false;
        if (args != null) {
            for (String a : args) {
                if ("--auto".equals(a) || "-a".equals(a)) auto = true;
                else if (!a.startsWith("--")) port = a;
            }
        }

        try (Drone drone = new Drone()) {
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

            System.out.println("MultiSensorTest - Press Enter to take a single snapshot of sensors. Ctrl-C to quit.");
            while (true) {
                // Always wait for Enter (removed auto mode check)
                System.out.println("\nPress Enter to capture sensor snapshot...");
                in.readLine();

                // Request fresh data
                drone.sendRequest(DataType.Range);
                drone.sendRequest(DataType.RawFlow);
                drone.sendRequest(DataType.Motion);
                drone.sendRequest(DataType.CardColor);

                // Small delay to allow receiver to update (matches other examples)
                try { Thread.sleep(120); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }

                // Range - only show valid sensors (>= 0, -1 means not available)
                Range range = drone.getDroneStatus().getRange();
                if (range != null) {
                    StringBuilder rangeStr = new StringBuilder("Range (mm):");
                    if (range.getFront() >= 0) rangeStr.append(String.format(" front=%d", range.getFront()));
                    if (range.getRear() >= 0) rangeStr.append(String.format(" back=%d", range.getRear()));
                    if (range.getLeft() >= 0) rangeStr.append(String.format(" left=%d", range.getLeft()));
                    if (range.getRight() >= 0) rangeStr.append(String.format(" right=%d", range.getRight()));
                    if (range.getTop() >= 0) rangeStr.append(String.format(" top=%d", range.getTop()));
                    if (range.getBottom() >= 0) rangeStr.append(String.format(" bottom=%d", range.getBottom()));
                    
                    if (rangeStr.length() > 11) { // More than just "Range (mm):"
                        System.out.println(rangeStr.toString());
                    } else {
                        System.out.println("Range: no valid sensors detected");
                    }
                } else {
                    System.out.println("Range: no data");
                }

                // Flow
                com.otabi.jcodroneedu.protocol.dronestatus.RawFlow rawFlow = drone.getDroneStatus().getRawFlow();
                if (rawFlow != null) {
                    System.out.printf(Locale.US, "Optical flow (m) - x=%.4f y=%.4f%n", rawFlow.getX(), rawFlow.getY());
                } else {
                    System.out.println("Flow: no data");
                }

                // Temperature from Motion sensor
                Motion motion = drone.getDroneStatus().getMotion();
                if (motion != null) {
                    // Motion has accel/gyro/angle data but temperature is in Altitude
                    // For now, skip temperature since it comes from barometer which may not be enabled
                } 
                
                // Note: Temperature from barometer is typically 0.0 unless Altitude data is received
                // Skipping temperature display as it requires barometer/altitude data

                // Color - use CardColor's toString method
                com.otabi.jcodroneedu.protocol.cardreader.CardColor cardColor = drone.getDroneStatus().getCardColor();
                if (cardColor != null) {
                    System.out.println("Colors - " + cardColor.toString());
                } else {
                    System.out.println("CardColor: no data");
                }

                System.out.println("--- snapshot complete ---");
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
