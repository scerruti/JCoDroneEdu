package com.otabi.jcodroneedu.patterns;

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
        try {
            // Create and connect to drone with pattern capabilities
            BasicPatternDrone drone = new BasicPatternDrone();
            
            // Take off
            drone.takeoff();
            
            // Fly a simple square
            System.out.println("Flying a square pattern...");
            drone.square(50, 50);
            
            // Brief pause
            drone.hover(2.0);
            
            // Fly a triangle
            System.out.println("Flying a triangle pattern...");
            drone.triangle(60, 40);
            
            // Land safely
            drone.land();
            
            // Close connection
            drone.close();
            
        } catch (Exception e) {
            System.err.println("Error in basic demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates intermediate pattern combinations.
     * 
     * This example teaches:
     * - Combining basic and advanced patterns
     * - Managing multiple flight phases
     * - Error handling in complex sequences
     */
    public static void intermediateDemo() {
        try {
            // Create drones with pattern capabilities and ensure resources are closed
            try (BasicPatternDrone basic = new BasicPatternDrone();
                 AdvancedPatternDrone advanced = new AdvancedPatternDrone()) {
                // Take off
                basic.takeoff();

                // Start with basic patterns
                System.out.println("Phase 1: Basic patterns...");
                basic.square(40, 45);
                basic.hover(1.0);
                // Use advanced circle method from AdvancedPatternDrone
                advanced.circle(30, 1, 3.0);  // speed, direction, duration
                basic.hover(1.0);

                // Move to advanced patterns
                System.out.println("Phase 2: Advanced patterns...");
                advanced.figure8(60, 25);
                advanced.hover(1.5);
                advanced.spiral(40, 80, 30);

                // Finish with basic landing sequence
                System.out.println("Phase 3: Landing sequence...");
                basic.hover(2.0);
                basic.land();
            }
            
        } catch (Exception e) {
            System.err.println("Error in intermediate demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates advanced pattern usage and customization.
     * 
     * This example teaches:
     * - Advanced pattern parameters
     * - Custom flight sequences
     * - Performance optimization
     * - Safety considerations
     */
    public static void advancedDemo() {
        try {
            // Create drones with full pattern capabilities and ensure they are closed
            try (BasicPatternDrone basic = new BasicPatternDrone();
                 AdvancedPatternDrone advanced = new AdvancedPatternDrone()) {
                // Take off
                basic.takeoff();
            
            // Complex flight routine
            System.out.println("Starting advanced flight routine...");
            
            // High-speed maneuvers
            advanced.pentagon(80, 60);  // Large, fast pentagon
            basic.hover(0.5);
            
            advanced.hexagon(70, 55);   // Large, fast hexagon  
            basic.hover(0.5);
            
            // Precision maneuvers
            advanced.circleApproximation(25, 12, 15);  // Small, precise circle approximation
            basic.hover(1.0);
            
            // Performance showcase
            advanced.figure8(100, 45);     // Large figure-8
            basic.hover(1.0);
            
            advanced.spiral(40, 80, 3, 40);   // Complex spiral pattern
            basic.hover(2.0);
            
                // Safe landing
                System.out.println("Landing sequence...");
                basic.land();
            }
        } catch (Exception e) {
            System.err.println("Error in advanced demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main method for running examples.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("CoDrone EDU Flight Patterns - Example Programs");
        System.out.println("=============================================");
        
        if (args.length == 0) {
            System.out.println("Available demos:");
            System.out.println("  basic      - Basic pattern demonstration");
            System.out.println("  intermediate - Intermediate pattern combinations");
            System.out.println("  advanced   - Advanced patterns and customization");
            System.out.println();
            System.out.println("Usage: java ExamplePrograms <demo_name>");
            return;
        }
        
        String demo = args[0].toLowerCase();
        switch (demo) {
            case "basic":
                System.out.println("Running basic demo...");
                basicDemo();
                break;
            case "intermediate":
                System.out.println("Running intermediate demo...");
                intermediateDemo();
                break;
            case "advanced":
                System.out.println("Running advanced demo...");
                advancedDemo();
                break;
            default:
                System.err.println("Unknown demo: " + demo);
                System.err.println("Available demos: basic, intermediate, advanced");
                System.exit(1);
        }
        
        System.out.println("Demo completed successfully!");
    }
}
