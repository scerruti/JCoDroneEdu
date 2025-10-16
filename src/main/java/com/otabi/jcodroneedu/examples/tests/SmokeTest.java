package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

/**
 * Minimal smoke test for verifying connection to a CoDrone EDU controller.
 *
 * Usage: run from IDE or via Gradle task added in the repository (runSmokeTest).
 * This program will only connect and print battery + model information.
 * Do NOT run flight commands automatically; use the separate FlightTest example below
 * only after manual verification and safety checks.
 */
public class SmokeTest {
    public static void main(String[] args) {
        // CLI options (all optional)
        // --port=<path>    force serial port (e.g. /dev/cu.usbserial-XXXX)
        // --buzzer         run controller buzzer sequence test
        // --display        run controller display primitive test
        String port = null;
        boolean doBuzzer = false;
        boolean doDisplay = false;

        for (String a : args) {
            if (a.startsWith("--port=")) {
                port = a.substring("--port=".length());
            } else if ("--buzzer".equals(a)) {
                doBuzzer = true;
            } else if ("--display".equals(a)) {
                doDisplay = true;
            }
        }

        try (Drone drone = new Drone()) {
            boolean connected;
            if (port != null) {
                System.out.println("Attempting to connect to port: " + port);
                connected = drone.connect(port);
            } else {
                System.out.println("Attempting auto-detect and pair()...");
                connected = drone.pair();
            }

            if (!connected) {
                System.err.println("Connection failed or could not be verified.");
                return;
            }

            System.out.println("Connection verified: isConnected=" + drone.isConnected());
            try {
                System.out.printf("Battery: %d%%\n", drone.getBattery());
            } catch (Exception e) {
                System.out.println("Could not read battery: " + e.getMessage());
            }

            // Small controller display test (non-destructive)
            if (doDisplay) {
                try {
                    System.out.println("Running controller display smoke test...");
                    drone.controllerClearScreen();
                    Thread.sleep(150);
                    drone.controllerDrawString(10, 10, "SMOKE");
                    Thread.sleep(150);
                    drone.controllerDrawLine(0, 0, 63, 31); // diagonal
                    Thread.sleep(150);
                    drone.controllerDrawRectangle(5, 5, 20, 12);
                    Thread.sleep(150);
                    drone.controllerDrawCircle(32, 16, 6);
                    Thread.sleep(150);
                    drone.controllerInvertArea(0, 0, 63, 31);
                    Thread.sleep(150);
                    drone.controllerClearScreen();
                    Thread.sleep(150);
                    System.out.println("Display test complete.");
                } catch (Exception e) {
                    System.err.println("Controller display test failed: " + e.getMessage());
                }
            }

            // Buzzer test (non-blocking sequences)
            if (doBuzzer) {
                try {
                    System.out.println("Playing controller buzzer sequences...");
                    drone.controllerBuzzerSequence("success");
                    Thread.sleep(500);
                    drone.controllerBuzzerSequence("warning");
                    Thread.sleep(500);
                    drone.controllerBuzzerSequence("alarm");
                    Thread.sleep(500);
                    System.out.println("Buzzer test complete.");
                } catch (Exception e) {
                    System.err.println("Controller buzzer test failed: " + e.getMessage());
                }
            }

            // Keep open briefly so reader thread can process any incoming messages
            Thread.sleep(250);

            System.out.println("Smoke test complete - disconnecting.");

        } catch (DroneNotFoundException e) {
            System.err.println("DroneNotFoundException: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
