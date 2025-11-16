package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;

public class TestHeight {
    public static void main(String[] args) {
        try (Drone drone = new Drone()) {
            drone.pair();
            drone.takeoff();
            drone.setThrottle(20);

            System.out.println("Requesting initial altitude...");
            float altitude = 0;
            altitude = drone.getAltitude().getRangeHeight();
            drone.getHeight();
            System.out.println("Received altitude: " + altitude + " cm");

            while (altitude < 2) {
                altitude = drone.getAltitude().getRangeHeight();
                System.out.println("Received altitude: " + altitude + " cm");
                drone.move(0.01);
            }
            drone.land();
        }

        System.out.println("Disconnected.");
    }
}
