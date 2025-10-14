package com.otabi.jcodroneedu.test;

import com.otabi.jcodroneedu.Drone;

public class TestEducationalException {
    public static void main(String[] args) {
        try {
            try (Drone drone = new Drone()) {
                drone.square(60, 2, 1);  // This should throw the educational exception
            }
        } catch (Exception e) {
            System.out.println("Exception caught:");
            System.out.println(e.getMessage());
        }
    }
}
