package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0102FlightMovements
{
    public static void main(String[] args)
    {
        try (Drone drone = new Drone(true))
        {
            drone.takeoff();

            drone.setPitch(30);     // setting forward pitch
            drone.move(1);  // moving forward!

            drone.setPitch(0);      // resetting pitch to 0
            drone.setRoll(30);      // setting roll to the right
            drone.move(1);  // moving right!

            drone.setRoll(0);       // resetting roll to 0
            drone.setPitch(-30);    // setting backwards pitch
            drone.move(1);  // moving backwards!

            drone.setPitch(0);      // resetting pitch to 0
            drone.setRoll(-30);     // setting roll to the left
            drone.move(1);  // moving left!

            drone.land();
        } catch (DroneNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
