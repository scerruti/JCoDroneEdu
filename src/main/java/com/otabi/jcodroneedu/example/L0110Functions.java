package com.otabi.jcodroneedu.example;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0110Functions {
    public static void main(String[] args) {
        try (Drone drone = new Drone(true)) {
            drone.takeoff();

            flySquare(drone, 30, 2);
            flyTriangle(drone, 30, 2);

            drone.land();
        } catch (DroneNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void flySquare(Drone drone, int power, int duration) {
        for (int i = 0; i < 4; i++) {
            moveForward(drone, power, duration);
            turnRight(drone, 90, 1);
        }
    }

    private static void flyTriangle(Drone drone, int power, int duration) {
        for (int i = 0; i < 3; i++) {
            moveForward(drone, power, duration);
            turnRight(drone, 120, 1);
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
