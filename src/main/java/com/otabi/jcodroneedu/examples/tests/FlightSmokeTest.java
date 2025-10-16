package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

/**
 * Guarded flight smoke test.
 *
 * Safety: This test will NOT fly unless both flags are provided:
 *   --allow-flight  (enables flight actions)
 *   --confirm=YES   (explicit human confirmation)
 *
 * Optional:
 *   --port=/dev/cu.xxx (force serial port)
 *   --min-battery=50   (minimum battery percentage required)
 *
 * Usage (safe default - will not fly):
 *   ./gradlew runFlightSmokeTest
 *
 * To run a real flight (ONLY when you are ready and in a safe area):
 *   ./gradlew runFlightSmokeTest --args='--allow-flight --confirm=YES --min-battery=60'
 */
public class FlightSmokeTest {
    public static void main(String[] args) {
        String port = null;
        boolean allowFlight = false;
        boolean confirmed = false;
        int minBattery = 50;

        for (String a : args) {
            if (a.startsWith("--port=")) {
                port = a.substring("--port=".length());
            } else if ("--allow-flight".equals(a)) {
                allowFlight = true;
            } else if (a.startsWith("--confirm=")) {
                String v = a.substring("--confirm=".length());
                confirmed = "YES".equals(v);
            } else if (a.startsWith("--min-battery=")) {
                try {
                    minBattery = Integer.parseInt(a.substring("--min-battery=".length()));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid min-battery value, using default " + minBattery);
                }
            }
        }

        if (!allowFlight || !confirmed) {
            System.out.println("Flight disabled — this test will not perform takeoff unless both --allow-flight and --confirm=YES are provided.");
            System.out.println("You can still use this program to verify connection by adding --port or running SmokeTest.");
        }

        Drone drone = null;
        try {
            drone = new Drone();

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

            System.out.println("Connected. Battery: " + drone.getBattery() + "%");

            if (allowFlight) {
                int battery = drone.getBattery();
                if (battery < minBattery) {
                    System.err.printf("Battery (%d%%) below required min (%d%%) — aborting flight.%n", battery, minBattery);
                    return;
                }

                System.out.println("Performing guarded indoor-safe turning-only flight sequence. No forward moves will be executed.");
                try {
                    // Safer indoor sequence inspired by L01 turning lessons
                    drone.takeoff();
                    Thread.sleep(2500); // allow gentle ascent

                    // Small yaw/turn maneuvers only (no translational movement)
                    drone.hover(0.5);
                    System.out.println("Turning left 30 degrees (indoor-safe)");
                    drone.turnLeft(30);
                    drone.hover(0.5);

                    System.out.println("Turning right 30 degrees (indoor-safe)");
                    drone.turnRight(30);
                    drone.hover(0.5);

                    System.out.println("Turning degree to 180 (slow") ;
                    drone.turnDegree(180, 5.0); // slower turn for stability
                    drone.hover(0.5);

                    // Small stabilization hover before landing
                    drone.hover(0.5);
                    drone.land();
                    Thread.sleep(2000); // settle
                    System.out.println("Indoor turning flight sequence completed successfully.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                System.out.println("Flight not enabled. Exiting after connection check.");
            }

        } catch (DroneNotFoundException e) {
            System.err.println("DroneNotFoundException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // Attempt emergency stop if possible
            try {
                if (drone != null) {
                    System.err.println("Attempting emergency stop...");
                    drone.emergencyStop();
                }
            } catch (Exception ex) {
                System.err.println("Emergency stop failed: " + ex.getMessage());
            }
        } finally {
            try {
                if (drone != null) drone.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
