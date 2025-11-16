package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0109NestedLoops {
    public static void main(String[] args) {
        int power = 30;
        int duration = 2;

        try (Drone drone = new Drone(true)) {
            drone.takeoff();

            for (int square = 0; square < 3; square++) {
                for (int side = 0; side < 4; side++) {
                    moveForward(drone, power, duration);
                    turnRight(drone, 90, 1);
                }
            }

            drone.land();
        } catch (DroneNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void moveForward(Drone drone, int power, int duration) {
        drone.setPitch(power);
        drone.move(duration);
        drone.setPitch(0);
    }

    private static void turnRight(Drone drone, int yaw, int duration) {
        drone.setYaw(yaw);
        drone.move(duration);
        drone.setYaw(0);
    }
}
