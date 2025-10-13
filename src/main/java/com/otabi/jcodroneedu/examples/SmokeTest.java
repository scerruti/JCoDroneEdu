package com.otabi.jcodroneedu.examples;

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
        // If you want to force a specific port, set via first arg (e.g. /dev/tty.usbserial-XXXX)
        String port = (args.length > 0) ? args[0] : null;

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
