package com.otabi.jcodroneedu.examples.tests;

import java.util.Scanner;

import com.otabi.jcodroneedu.Drone;

public class TestHeight {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Drone drone = new Drone()) {
            System.out.println("Before pair: " + drone.getAltitude());
            drone.pair();
            System.out.println("After pair: " + drone.getAltitude());

            System.out.println("Move drone to table height...");
            scanner.nextLine();
            System.out.println("Table height: " + drone.getAltitude());

            System.out.println("Move drone to eye level...");
            scanner.nextLine();
            System.out.println("Eye level: " + drone.getAltitude());
        }

        System.out.println("Disconnected.");
    }
}
