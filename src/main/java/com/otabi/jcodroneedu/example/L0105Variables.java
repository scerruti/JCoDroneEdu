package com.otabi.jcodroneedu.example;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0105Variables
{
    public static void main(String[] args)
    {
        int power = 25;
        int turn = 50;
        int duration = 3;

        try (Drone drone = new Drone(true))
        {
            drone.takeoff();

            moveForward(drone, power, duration);
            turn(drone, turn, duration);

            // Fill in the rest! For example, you might want to:
            // - Move backward
            // - Turn in the opposite direction
            // - Land the drone

            // Example continuation:
            moveForward(drone, -power, duration);
            turn(drone, -turn, duration);

            drone.land();
        } catch (DroneNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void turn(Drone drone, int turn, int duration)
    {
        drone.setYaw(turn);
        drone.move(duration);
        drone.setYaw(0);
    }

    private static void moveForward(Drone drone, int power, int duration)
    {
        drone.setPitch(power);
        drone.move(duration);
        drone.setPitch(0);
    }
}
