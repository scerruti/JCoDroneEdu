package com.otabi.jcodroneedu.example;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0106Conditionals {
    public static void main(String[] args) {
        int direction = 1; // Try changing this to -1 or 0 to test different branches
        int power = 30;
        int duration = 2;

        try (Drone drone = new Drone(true)) {
            drone.takeoff();

            if (direction == 1) {
                System.out.println("Moving forward");
                drone.setPitch(power);
                drone.move(duration);
                drone.setPitch(0);
            } else if (direction == -1) {
                System.out.println("Moving backward");
                drone.setPitch(-power);
                drone.move(duration);
                drone.setPitch(0);
            } else {
                System.out.println("Hovering in place");
                drone.hover(duration);
            }

            drone.land();
        } catch (DroneNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
