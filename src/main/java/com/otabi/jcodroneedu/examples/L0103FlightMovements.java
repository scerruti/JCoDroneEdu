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

            drone.setPitch(20);     // Moves the drone forward at 20% power
            drone.move(1);  // Moves for 1 second

            /* Uncomment this line */
//            drone.setPitch(0);      // Resets the pitch back to 0
            drone.setRoll(-20);     // Moves the drone to the left at 20% power
            drone.move(2);  // Moves for 2 seconds

            drone.land();
        } catch (DroneNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
