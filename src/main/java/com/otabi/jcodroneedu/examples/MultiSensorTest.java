package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;
import com.otabi.jcodroneedu.protocol.dronestatus.Range;
import com.otabi.jcodroneedu.protocol.dronestatus.RawFlow;
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
                if (!auto) {
                    System.out.println("Press Enter to capture sensor snapshot...");
                    in.readLine();
                }

                // Request fresh data
                drone.sendRequest(DataType.Range);
                drone.sendRequest(DataType.RawFlow);
                drone.sendRequest(DataType.Motion);
                drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.CardColor);

                // Small delay to allow receiver to update (matches other examples)
                try { Thread.sleep(120); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }

                // Range
                Range range = drone.getDroneStatus().getRange();
                if (range != null) {
                    System.out.printf(Locale.US, "Range (mm) - front=%d back=%d left=%d right=%d top=%d bottom=%d%n",
                            range.getFront(), range.getRear(), range.getLeft(), range.getRight(), range.getTop(), range.getBottom());
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

                // Temperature
                double tempC = drone.getDroneTemperature();
                System.out.printf(Locale.US, "Temperature: %.2f °C / %.2f °F%n", tempC, (tempC * 9.0/5.0) + 32.0);

                // Color
                com.otabi.jcodroneedu.protocol.cardreader.CardColor cardColor = drone.getDroneStatus().getCardColor();
                if (cardColor != null) {
                    byte[] colors = cardColor.getColor();
                    if (colors != null && colors.length >= 2) {
                        System.out.printf(Locale.US, "Detected colors - front=%d back=%d%n", colors[0] & 0xFF, colors[1] & 0xFF);
                    } else {
                        System.out.println("CardColor: no color values available");
                    }
                } else {
                    System.out.println("CardColor: no data");
                }

                System.out.println("--- snapshot complete ---\n");
                if (auto) {
                    // In auto mode, wait a short interval then continue
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                }
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
