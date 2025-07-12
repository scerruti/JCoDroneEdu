package com.otabi.jcodroneedu.patterns;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;

/**
 * Basic Pattern Drone - Extends Drone with fundamental flight patterns.
 * 
 * This class demonstrates object-oriented inheritance concepts:
 * - Extending a base class with additional functionality
 * - Building upon existing methods
 * - Creating specialized behavior classes
 * - Course progression through inheritance
 * 
 * Educational Goals:
 * - Understand inheritance and "is-a" relationships
 * - Learn how complex behaviors are built from simple movements
 * - Practice object-oriented design patterns
 * - See how course content builds progressively
 * 
 * Usage:
 * ```java
 * BasicPatternDrone drone = new BasicPatternDrone();
 * drone.pair();
 * drone.takeoff();
 * drone.square(50, 50);  // New pattern method
 * drone.land();
 * drone.close();
 * ```
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 */
public class BasicPatternDrone extends Drone {
    
    /**
     * Creates a new BasicPatternDrone instance.
     */
    public BasicPatternDrone() {
        super();
    }
    
    /**
     * Creates a new BasicPatternDrone instance with optional automatic connection.
     * 
     * @param autoConnect If true, automatically attempts to connect to the drone
     * @throws DroneNotFoundException if autoConnect is true and connection fails
     */
    public BasicPatternDrone(boolean autoConnect) throws DroneNotFoundException {
        super(autoConnect);
    }
    
    /**
     * Creates a new BasicPatternDrone instance with optional automatic connection to a specific port.
     * 
     * @param autoConnect If true, automatically attempts to connect to the drone
     * @param portName The specific serial port name
     * @throws DroneNotFoundException if autoConnect is true and connection fails
     */
    public BasicPatternDrone(boolean autoConnect, String portName) throws DroneNotFoundException {
        super(autoConnect, portName);
    }
    }
    
    /**
     * Creates a new BasicPatternDrone instance with optional automatic connection to a specific port.
     * 
     * @param autoConnect If true, automatically attempts to connect to the drone
     * @param portName The specific serial port name
     * @throws DroneNotFoundException if autoConnect is true and connection fails
     */
    public BasicPatternDrone(boolean autoConnect, String portName) throws DroneNotFoundException {
        super(autoConnect, portName);
    }
    
    /**
     * Flies the drone in a square pattern.
     * 
     * This pattern teaches:
     * - Loop structure (4 sides of a square)
     * - Consistent movement distances
     * - 90-degree turns
     * - Return to starting position
     * 
     * @param sideLength The length of each side in centimeters (10-200 recommended)
     * @param speed Flight speed as percentage (10-100)
     */
    public void square(int sideLength, int speed) {
        if (sideLength < 10 || sideLength > 200) {
            throw new IllegalArgumentException("Side length must be between 10 and 200 cm");
        }
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        
        // Fly 4 sides of a square
        for (int side = 0; side < 4; side++) {
            drone.go("forward", sideLength, speed);
            drone.go("cw", 90, 30); // Slower turn for precision
            drone.hover(0.5); // Brief pause for stability
        }
    }
    
    /**
     * Flies the drone in a triangle pattern.
     * 
     * This pattern teaches:
     * - Different geometric shapes
     * - 120-degree turns (360° ÷ 3 sides)
     * - Mathematical relationships in flight
     * 
     * @param sideLength The length of each side in centimeters
     * @param speed Flight speed as percentage
     */
    public void triangle(int sideLength, int speed) {
        if (sideLength < 10 || sideLength > 200) {
            throw new IllegalArgumentException("Side length must be between 10 and 200 cm");
        }
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        
        // Fly 3 sides of a triangle
        for (int side = 0; side < 3; side++) {
            drone.go("forward", sideLength, speed);
            drone.go("cw", 120, 30); // 360° ÷ 3 = 120°
            drone.hover(0.5);
        }
    }
    
    /**
     * Flies the drone in a simple line back and forth.
     * 
     * This pattern teaches:
     * - Forward and backward movement
     * - Turning around (180-degree turn)
     * - Simple repetition
     * 
     * @param distance Distance to fly in each direction
     * @param cycles Number of back-and-forth cycles
     * @param speed Flight speed as percentage
     */
    public void lineBackAndForth(int distance, int cycles, int speed) {
        if (distance < 10 || distance > 200) {
            throw new IllegalArgumentException("Distance must be between 10 and 200 cm");
        }
        if (cycles < 1 || cycles > 10) {
            throw new IllegalArgumentException("Cycles must be between 1 and 10");
        }
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        
        for (int cycle = 0; cycle < cycles; cycle++) {
            // Forward
            drone.go("forward", distance, speed);
            drone.hover(1.0); // Pause at end
            
            // Turn around
            drone.go("cw", 180, 30);
            drone.hover(0.5);
            
            // Backward (now forward in new direction)
            drone.go("forward", distance, speed);
            drone.hover(1.0);
            
            // Turn around again to face original direction
            if (cycle < cycles - 1) { // Don't turn on last cycle
                drone.go("cw", 180, 30);
                drone.hover(0.5);
            }
        }
    }
    
    /**
     * Demonstrates up and down movement in a step pattern.
     * 
     * This pattern teaches:
     * - Vertical movement control
     * - Altitude management
     * - 3D thinking
     * 
     * @param stepHeight Height of each step in centimeters
     * @param numberOfSteps Number of steps to climb
     * @param speed Movement speed as percentage
     */
    public void stairs(int stepHeight, int numberOfSteps, int speed) {
        if (stepHeight < 10 || stepHeight > 100) {
            throw new IllegalArgumentException("Step height must be between 10 and 100 cm");
        }
        if (numberOfSteps < 2 || numberOfSteps > 5) {
            throw new IllegalArgumentException("Number of steps must be between 2 and 5");
        }
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        
        // Go up the stairs
        for (int step = 0; step < numberOfSteps; step++) {
            drone.go("up", stepHeight, speed);
            drone.go("forward", 30, speed); // Move forward a bit on each step
            drone.hover(0.5);
        }
        
        // Pause at the top
        drone.hover(2.0);
        
        // Come back down
        for (int step = 0; step < numberOfSteps; step++) {
            drone.go("backward", 30, speed);
            drone.go("down", stepHeight, speed);
            drone.hover(0.5);
        }
    }
}
