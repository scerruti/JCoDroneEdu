package com.otabi.jcodroneedu.example;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0107ForLoops {
    public static void main(String[] args) {
        int power = 30;
        int duration = 2;

        try (Drone drone = new Drone(true)) {
            drone.takeoff();

            for (int i = 0; i < 4; i++) {
                moveForward(drone, power, duration);
                turnRight(drone, 90, 1); // 90-degree turn, 1 second
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
