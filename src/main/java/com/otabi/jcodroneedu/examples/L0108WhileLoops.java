package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0108WhileLoops {
    public static void main(String[] args) {
        int count = 0;
        int maxCount = 4;
        int rollPower = 30;
        int duration = 1;

        try (Drone drone = new Drone(true)) {
            drone.takeoff();

            while (count < maxCount) {
                drone.setRoll(rollPower);
                drone.move(duration);
                drone.setRoll(-rollPower);
                drone.move(duration);
                count++;
            }

            drone.setRoll(0);

            // TODO: Tower of Terror
            // Use a while loop to ascend until the drone reaches a target altitude
            // Example:
            // while (drone.getAltitude() < targetAltitude) {
            //     drone.setThrottle(30);
            //     drone.move(1);
            // }
            // drone.setThrottle(0);

            drone.land();
        } catch (DroneNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
