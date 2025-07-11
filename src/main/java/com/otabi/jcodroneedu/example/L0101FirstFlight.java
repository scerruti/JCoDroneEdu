package com.otabi.jcodroneedu.example;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

public class L0101FirstFlight
{
    public static void pythonMain(String[] args)
    {
        Drone drone = new Drone();
        drone.pair();
        System.out.println("Paired!");
        drone.takeoff();
        System.out.println("In the air!");
        System.out.println("Hovering for 5 seconds");
        drone.hover(5);
        System.out.println("Landing");
        drone.land();
        drone.close();
        System.out.println("Program complete");
    }

    public static void simpleMain(String[] args)
    {
        Drone drone = new Drone();
        try
        {
            drone.connect();
            System.out.println("Paired!");
            drone.takeoff();
            System.out.println("In the air!");
            System.out.println("Hovering for 5 seconds");
            drone.hover(5);
            System.out.println("Landing");
            drone.land();
            drone.close(); // Could use disconnect
            System.out.println("Program complete");
        } catch (DroneNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args)
    {
        try (Drone drone = new Drone(true))
        {
            System.out.println("Paired!");
            drone.takeoff();
            System.out.println("In the air!");
            System.out.println("Hovering for 5 seconds");
            drone.hover(5);
            System.out.println("Landing");
            drone.land();
        } catch (DroneNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        System.out.println("Program complete");
    }
}
