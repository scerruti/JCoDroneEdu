package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0103FlightMovements
{
    public static void main(String[] args)
    {
        try (Drone drone = new Drone(true))
        {
            drone.takeoff();

            drone.setThrottle(20);   // Moves the drone up at 20% power
            drone.move(1);  // Moves for 1 second

            drone.setThrottle(0);    // Resets the throttle back to 0
            drone.setYaw(30);        // Turns the drone clockwise at 30% power
            drone.move(2);  // Turns for 2 seconds

            drone.land();
        } catch (DroneNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
