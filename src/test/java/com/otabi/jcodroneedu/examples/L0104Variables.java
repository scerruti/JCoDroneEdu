package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0104Variables
{
    public static void main(String[] args)
    {
        try (Drone drone = new Drone(true))
        {
            int speed = 40;
            int duration = 1;

            drone.takeoff();

            drone.setPitch(speed);
            drone.setThrottle(speed);
            drone.move(duration);

            drone.setThrottle(-speed);
            drone.move(duration);

            drone.setThrottle(speed);
            drone.move(duration);

            drone.setThrottle(-speed);
            drone.move(duration);

            drone.land();
        } catch (DroneNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
