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
 * this.pair();
 * this.takeoff();
 * this.square(50, 50);  // New pattern method
 * this.land();
 * this.close();
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
    
    /**
     * Flies the drone in a square pattern.
     * 
     * This pattern teaches:
     * - Direct motor control movements
     * - Coordinated roll/pitch control
     * - Precise timing and stabilization
     * - Matching Python implementation exactly
     * 
     * Implementation matches Python's sendControlWhile() calls:
     * - Forward/backward using pitch control
     * - Left/right using roll control
     * - Brief stabilization pauses between movements
     * 
     * @param speed Flight speed as percentage (10-100)
     * @param seconds Duration of each side in seconds
     * @param direction Direction: 1 for right, -1 for left
     */
    public void square(int speed, int seconds, int direction) {
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        if (seconds < 1 || seconds > 10) {
            throw new IllegalArgumentException("Seconds must be between 1 and 10");
        }
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("Direction must be 1 (right) or -1 (left)");
        }
        
        int power = speed;
        int duration = seconds * 1000; // Convert to milliseconds
        
        // Match Python implementation exactly
        sendControlWhile(0, power, 0, 0, duration);        // Forward (pitch)
        sendControlWhile(0, -power, 0, 0, 50);             // Brief stop
        
        sendControlWhile(power * direction, 0, 0, 0, duration);  // Right/left (roll)
        sendControlWhile(-power * direction, 0, 0, 0, 50);       // Brief stop
        
        sendControlWhile(0, -power, 0, 0, duration);       // Backward (pitch)
        sendControlWhile(0, power, 0, 0, 50);              // Brief stop
        
        sendControlWhile(-power * direction, 0, 0, 0, duration); // Left/right (roll)
        sendControlWhile(power * direction, 0, 0, 0, 50);        // Brief stop
    }
    
    /**
     * Overloaded method with simpler parameters for backwards compatibility
     */
    public void square(int sideLength, int speed) {
        // Convert sideLength to duration estimate (rough approximation)
        int seconds = Math.max(1, sideLength / 50); // Approximate timing
        square(speed, seconds, 1); // Default to right direction
    }
    
    /**
     * Flies the drone in a triangle pattern.
     * 
     * This pattern teaches:
     * - Combined roll/pitch movements for diagonal flight
     * - Coordinated motor control
     * - Geometric pattern creation
     * - Matching Python implementation exactly
     * 
     * Implementation matches Python's sendControlWhile() calls:
     * - Diagonal movements using combined roll+pitch
     * - Precise timing and stabilization
     * - Direction control
     * 
     * @param speed Flight speed as percentage (10-100)
     * @param seconds Duration of each side in seconds
     * @param direction Direction: 1 for right, -1 for left
     */
    public void triangle(int speed, int seconds, int direction) {
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        if (seconds < 1 || seconds > 10) {
            throw new IllegalArgumentException("Seconds must be between 1 and 10");
        }
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("Direction must be 1 (right) or -1 (left)");
        }
        
        int power = speed;
        int duration = seconds * 1000; // Convert to milliseconds
        
        // Match Python implementation exactly
        sendControlWhile(power * direction, power, 0, 0, duration);    // Diagonal up-right/left
        sendControlWhile(-power * direction, -power, 0, 0, 50);        // Brief stop
        
        sendControlWhile(power * direction, -power, 0, 0, duration);   // Diagonal down-right/left
        sendControlWhile(-power * direction, power, 0, 0, 50);         // Brief stop
        
        sendControlWhile(-power * direction, 0, 0, 0, duration);       // Straight left/right
        sendControlWhile(power * direction, 0, 0, 0, 50);              // Brief stop
    }
    
    /**
     * Overloaded method with simpler parameters for backwards compatibility
     */
    public void triangle(int sideLength, int speed) {
        // Convert sideLength to duration estimate (rough approximation)
        int seconds = Math.max(1, sideLength / 50); // Approximate timing
        triangle(speed, seconds, 1); // Default to right direction
    }
    
    /**
     * Flies the drone in a simple line back and forth.
     * 
     * This pattern teaches:
     * - Forward and backward movement using direct control
     * - Precise timing control
     * - Simple repetition patterns
     * 
     * Uses sendControlWhile for consistent implementation with Python patterns
     * 
     * @param speed Flight speed as percentage (10-100)
     * @param seconds Duration to fly in each direction
     * @param cycles Number of back-and-forth cycles
     */
    public void lineBackAndForthTimed(int speed, int seconds, int cycles) {
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        if (seconds < 1 || seconds > 10) {
            throw new IllegalArgumentException("Seconds must be between 1 and 10");
        }
        if (cycles < 1 || cycles > 10) {
            throw new IllegalArgumentException("Cycles must be between 1 and 10");
        }
        
        int power = speed;
        int duration = seconds * 1000; // Convert to milliseconds
        
        for (int cycle = 0; cycle < cycles; cycle++) {
            // Forward movement
            sendControlWhile(0, power, 0, 0, duration);
            sendControlWhile(0, 0, 0, 0, 500); // Brief pause
            
            // Backward movement  
            sendControlWhile(0, -power, 0, 0, duration);
            sendControlWhile(0, 0, 0, 0, 500); // Brief pause
        }
    }
    
    /**
     * Overloaded method with simpler parameters for backwards compatibility
     */
    public void lineBackAndForth(int distance, int cycles, int speed) {
        // Convert distance to duration estimate (rough approximation)
        int seconds = Math.max(1, distance / 50); // Approximate timing
        lineBackAndForthTimed(speed, seconds, cycles);
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
            this.go("up", stepHeight, speed);
            this.go("forward", 30, speed); // Move forward a bit on each step
            this.hover(0.5);
        }
        
        // Pause at the top
        this.hover(2.0);
        
        // Come back down
        for (int step = 0; step < numberOfSteps; step++) {
            this.go("backward", 30, speed);
            this.go("down", stepHeight, speed);
            this.hover(0.5);
        }
    }
    
    /**
     * Moves the drone left and right twice, matching Python implementation exactly.
     * 
     * This pattern teaches:
     * - Side-to-side movement using roll control
     * - Repeated movement patterns
     * - Direction control for symmetrical movements
     * 
     * Implementation matches Python's sendControlWhile() calls:
     * - Uses roll control for left/right movement
     * - Repeats the sway pattern twice
     * - Precise timing control
     * 
     * @param speed Flight speed as percentage (10-100)
     * @param seconds Duration of each sway movement
     * @param direction Direction: 1 starts left, -1 starts right
     */
    public void sway(int speed, int seconds, int direction) {
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        if (seconds < 1 || seconds > 10) {
            throw new IllegalArgumentException("Seconds must be between 1 and 10");
        }
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("Direction must be 1 (start left) or -1 (start right)");
        }
        
        int power = speed;
        int duration = seconds * 1000; // Convert to milliseconds
        
        // Match Python implementation exactly - sway twice
        for (int i = 0; i < 2; i++) {
            sendControlWhile(-power * direction, 0, 0, 0, duration);  // First direction
            sendControlWhile(power * direction, 0, 0, 0, duration);   // Opposite direction
        }
    }
    
    /**
     * Overloaded method with simpler parameters for backwards compatibility
     */
    public void sway(int speed, int seconds) {
        sway(speed, seconds, 1); // Default to starting left
    }
}
