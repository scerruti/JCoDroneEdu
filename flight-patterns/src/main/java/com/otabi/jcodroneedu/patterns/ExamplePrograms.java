package com.otabi.jcodroneedu.patterns;

import com.otabi.jcodroneedu.Drone;

/**
 * Example programs demonstrating how to use the flight patterns.
 * 
 * These examples show:
 * - How to import and use dependency libraries
 * - How to combine multiple patterns
 * - How to create complete flight programs
 * - Best practices for educational drone programming
 * 
 * Educational Goals:
 * - Understand library usage and imports
 * - Learn program structure and organization
 * - Practice combining simple patterns into complex behaviors
 * - Develop confidence with external dependencies
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 */
public class ExamplePrograms {
    
    /**
     * Demonstrates basic pattern usage.
     * 
     * This example teaches:
     * - How to import external libraries
     * - Basic pattern execution
     * - Simple program structure
     */
    public static void basicDemo() {
        // Connect to drone
        Drone drone = new Drone();
        drone.pair(); // This would need to be implemented in the core library
        
        // Create pattern controllers
        BasicPatterns basic = new BasicPatterns(drone);
        
        // Take off
        drone.takeoff();
        
        // Fly a simple square
        System.out.println("Flying a square pattern...");
        basic.square(50, 50);
        
        // Brief pause
        drone.hover(2.0);
        
        // Fly a triangle
        System.out.println("Flying a triangle pattern...");
        basic.triangle(60, 40);
        
        // Land
        drone.land();
        
        // Disconnect
        drone.close();
        
        System.out.println("Basic demo complete!");
    }
    
    /**
     * Demonstrates advanced pattern usage and combinations.
     * 
     * This example teaches:
     * - Using multiple pattern classes
     * - Combining different patterns
     * - More complex program flow
     */
    public static void advancedDemo() {
        // Connect to drone
        Drone drone = new Drone();
        drone.pair();
        
        // Create pattern controllers
        BasicPatterns basic = new BasicPatterns(drone);
        AdvancedPatterns advanced = new AdvancedPatterns(drone);
        
        // Take off
        drone.takeoff();
        
        // Start with a basic square
        System.out.println("Starting with a square...");
        basic.square(40, 50);
        drone.hover(2.0);
        
        // Move to a circle
        System.out.println("Now flying a circle...");
        advanced.circle(60, 12, 45);
        drone.hover(2.0);
        
        // Finish with a figure-8
        System.out.println("Finishing with a figure-8...");
        advanced.figure8(50, 40);
        
        // Land
        drone.land();
        drone.close();
        
        System.out.println("Advanced demo complete!");
    }
    
    /**
     * Demonstrates creating custom patterns by combining existing ones.
     * 
     * This example teaches:
     * - Pattern composition
     * - Creating new behaviors from existing ones
     * - Parameter passing between patterns
     */
    public static void customPatternDemo() {
        // Connect to drone
        Drone drone = new Drone();
        drone.pair();
        
        // Create pattern controllers
        BasicPatterns basic = new BasicPatterns(drone);
        AdvancedPatterns advanced = new AdvancedPatterns(drone);
        
        // Take off
        drone.takeoff();
        
        // Custom pattern: "Flower" - circle with triangles at cardinal points
        System.out.println("Flying custom 'flower' pattern...");
        
        // Center circle
        advanced.circle(40, 8, 30);
        drone.hover(1.0);
        
        // Four "petals" - triangles at north, east, south, west
        for (int petal = 0; petal < 4; petal++) {
            // Move to edge of circle
            drone.go("forward", 40, 30);
            
            // Fly a small triangle "petal"
            basic.triangle(25, 35);
            
            // Return to center
            drone.go("backward", 40, 30);
            
            // Rotate 90 degrees for next petal
            drone.go("cw", 90, 30);
            drone.hover(0.5);
        }
        
        // Land
        drone.land();
        drone.close();
        
        System.out.println("Custom pattern demo complete!");
    }
    
    /**
     * Main method for testing the examples.
     * Students can run this to see the patterns in action.
     */
    public static void main(String[] args) {
        System.out.println("JCoDroneEdu Flight Patterns - Example Programs");
        System.out.println("==============================================");
        System.out.println();
        
        // Uncomment the demo you want to run:
        
        // basicDemo();
        // advancedDemo();
        // customPatternDemo();
        
        System.out.println("Uncomment one of the demo methods in main() to run examples.");
        System.out.println("Make sure your drone is connected and in a safe flying area!");
    }
}
