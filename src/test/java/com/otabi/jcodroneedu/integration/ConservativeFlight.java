package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

/**
 * Conservative flight example: requires explicit --allow-flight flag to run.
 * This will perform a short supervised takeoff, hover, and land.
 */
public class ConservativeFlight {
    public static void main(String[] args) {
        boolean allowFlight = false;
        for (String a : args) {
            if ("--allow-flight".equals(a)) allowFlight = true;
        }

        if (!allowFlight) {
            System.err.println("Refusing to fly: this example requires --allow-flight flag.");
            System.err.println("Usage: ./gradlew runConservativeFlight --args='--allow-flight'");
            System.exit(1);
        }

        // For CLI usage we'll require the flag and call the helper
        try (Drone drone = new Drone()) {
            try {
                if (!drone.connect()) {
                    System.err.println("Could not connect to drone. Aborting flight.");
                    return;
                }
            } catch (DroneNotFoundException e) {
                System.err.println("Connection failed: " + e.getMessage());
                return;
            }

            System.out.println("Connected. Preparing conservative flight...");
            runShortFlight(drone);
            System.out.println("Flight complete. Disconnecting.");
        } catch (Exception e) {
            System.err.println("Error during conservative flight: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Programmatic helper to run a short supervised takeoff/hover/land sequence.
     * Caller is responsible for ensuring the drone is connected and that this is allowed.
     */
    public static void runShortFlight(Drone drone) {
        if (drone == null) throw new IllegalArgumentException("drone is null");
        try {
            // Use conservative timings: quick takeoff, short hover, land
            drone.takeoff();
            // hover accepts seconds; keep it short
            drone.hover(2);
            drone.land();
        } catch (Exception e) {
            throw new RuntimeException("Conservative flight failed: " + e.getMessage(), e);
        }
    }
}
