package com.otabi.jcodroneedu.example;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0110FlyShapes {
    public static void main(String[] args) {
        int power = 30;
        int duration = 2;

        try (Drone drone = new Drone(true)) {
            drone.takeoff();

            System.out.println("Flying a triangle...");
            flyTriangle(drone, power, duration);

            System.out.println("Flying a square...");
            flySquare(drone, power, duration);

            System.out.println("Flying a hexagon...");
            flyShape(drone, 6, power, duration);  // Generic shape

            drone.land();
        } catch (DroneNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void flyTriangle(Drone drone, int power, int duration) {
        flyShape(drone, 3, power, duration);
    }

    private static void flySquare(Drone drone, int power, int duration) {
        flyShape(drone, 4, power, duration);
    }

    private static void flyShape(Drone drone, int sides, int power, int duration) {
        if (sides < 3) {
            System.out.println("A shape must have at least 3 sides.");
            return;
        }

        int turnAngle = 360 / sides;

        for (int i = 0; i < sides; i++) {
            moveForward(drone, power, duration);
            turnRight(drone, turnAngle, 1);
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
