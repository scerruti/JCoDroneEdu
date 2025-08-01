package com.otabi.jcodroneedu.patterns;

import com.otabi.jcodroneedu.DroneNotFoundException;

/**
 * Advanced Pattern Drone - Extends BasicPatternDrone with sophisticated flight patterns.
 * 
 * This class demonstrates advanced object-oriented concepts:
 * - Multi-level inheritance (Drone → BasicPatternDrone → AdvancedPatternDrone)
 * - Building upon inherited functionality
 * - Progressive complexity in course design
 * - Specialized behavior through inheritance
 * 
 * Educational Goals:
 * - Understand inheritance hierarchies and method resolution
 * - Apply trigonometry and geometry concepts in programming
 * - Learn about precision vs. complexity tradeoffs
 * - See how advanced features build on basic foundations
 * 
 * Course Progression:
 * 1. Students start with Drone (basic flight)
 * 2. Progress to BasicPatternDrone (simple patterns)
 * 3. Advance to AdvancedPatternDrone (complex mathematics)
 * 
 * Usage:
 * ```java
 * AdvancedPatternDrone drone = new AdvancedPatternDrone();
 * this.pair();
 * this.takeoff();
 * 
 * // Can use basic patterns (inherited)
 * this.square(50, 50);
 * 
 * // Can use advanced patterns (new)
 * this.circle(45, 1, 3.0);
 * this.spiral(20, 100, 3, 40);
 * 
 * this.land();
 * this.close();
 * ```
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 */
public class AdvancedPatternDrone extends BasicPatternDrone {
    
    /**
     * Creates a new AdvancedPatternDrone instance.
     */
    public AdvancedPatternDrone() {
        super();
    }
    
    /**
     * Creates a new AdvancedPatternDrone instance with optional automatic connection.
     * 
     * @param autoConnect If true, automatically attempts to connect to the drone
     * @throws DroneNotFoundException if autoConnect is true and connection fails
     */
    public AdvancedPatternDrone(boolean autoConnect) throws DroneNotFoundException {
        super(autoConnect);
    }
    
    /**
     * Creates a new AdvancedPatternDrone instance with optional automatic connection to a specific port.
     * 
     * @param autoConnect If true, automatically attempts to connect to the drone
     * @param portName The specific serial port name
     * @throws DroneNotFoundException if autoConnect is true and connection fails
     */
    public AdvancedPatternDrone(boolean autoConnect, String portName) throws DroneNotFoundException {
        super(autoConnect, portName);
    }
    
    /**
     * Flies the drone in a circular pattern using direct motor control (Python-style).
     * 
     * This method replicates the Python implementation:
     * def circle(self, speed=75, direction=1):
     *     self.sendControl(0, speed, direction * speed, 0)
     *     time.sleep(5)
     * 
     * This pattern teaches:
     * - Direct motor control vs. high-level commands
     * - Continuous vs. discrete movement
     * - How gyroscope-based flight works
     * - Real circular motion instead of polygon approximation
     * 
     * @param speed Flight speed as percentage (10-100)
     * @param direction Turn direction: 1 for clockwise, -1 for counter-clockwise
     * @param duration How long to fly in circle (seconds)
     */
    public void circle(int speed, int direction, double duration) {
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("Direction must be 1 (clockwise) or -1 (counter-clockwise)");
        }
        if (duration < 0.5 || duration > 10.0) {
            throw new IllegalArgumentException("Duration must be between 0.5 and 10.0 seconds");
        }
        
        // Python-style circle: sendControl(roll=0, pitch=speed, yaw=direction*speed, throttle=0)
        sendControl(0, speed, direction * speed, 0);
        
        // Hold the circle for the specified duration (match Python time.sleep)
        try {
            Thread.sleep((long)(duration * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Stop the movement
        sendControl(0, 0, 0, 0);
    }
    
    /**
     * Flies the drone in a figure-8 pattern.
     * 
     * This pattern teaches:
     * - Complex multi-part patterns
     * - Symmetrical movements
     * - Planning multi-step sequences
     * 
     * @param radius Radius of each loop in the figure-8
     * @param speed Flight speed as percentage
     */
    public void figure8(int radius, int speed) {
        if (radius < 30 || radius > 100) {
            throw new IllegalArgumentException("Radius must be between 30 and 100 cm");
        }
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        
        // First loop (clockwise)
        circle(radius, 12, speed);
        
        // Move to center and prepare for second loop
        this.hover(0.5);
        
        // Second loop (counter-clockwise) - reverse the turn direction
        int segments = 12;
        double circumference = 2 * Math.PI * radius;
        int segmentLength = (int) Math.round(circumference / segments);
        int turnAngle = 360 / segments;
        
        for (int segment = 0; segment < segments; segment++) {
            this.go("forward", segmentLength, speed);
            this.go("ccw", turnAngle, 20); // Counter-clockwise for second loop
            this.hover(0.2);
        }
    }
    
    /**
     * Flies the drone in a spiral pattern, matching Python implementation exactly.
     * 
     * This pattern teaches:
     * - Direct motor control for complex patterns
     * - Coordinated movement with multiple axes
     * - Python-Java implementation parity
     * 
     * Implementation matches Python's sendControl() call:
     * - Forward pitch movement
     * - Yaw rotation for direction
     * - Throttle control for altitude change
     * 
     * @param speed Flight speed as percentage (10-100)
     * @param seconds Duration of spiral movement
     * @param direction Direction: 1 for right, -1 for left
     */
    public void spiral(int speed, int seconds, int direction) {
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
        
        // Match Python implementation exactly:
        // self.sendControl(0, power, 100 * -direction, -power)
        sendControl(0, power, 100 * -direction, -power);
        
        // Hold the spiral for the specified time
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Stop the movement
        sendControl(0, 0, 0, 0);
    }
    
    /**
     * Overloaded method with simpler parameters for backwards compatibility
     */
    public void spiral(int startRadius, int endRadius, int turns, int speed) {
        // Convert to Python-style parameters (rough approximation)
        int seconds = Math.max(2, turns * 2); // Approximate timing
        int direction = 1; // Default to right
        spiral(speed, seconds, direction);
    }
    
    /**
     * Flies the drone in a pentagon pattern.
     * 
     * This pattern teaches:
     * - Regular polygons beyond squares and triangles
     * - Interior angle calculations (108° for pentagon)
     * - More complex geometric shapes
     * 
     * @param sideLength Length of each side in centimeters
     * @param speed Flight speed as percentage
     */
    public void pentagon(int sideLength, int speed) {
        if (sideLength < 15 || sideLength > 150) {
            throw new IllegalArgumentException("Side length must be between 15 and 150 cm");
        }
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        
        // Pentagon has 5 sides, each exterior angle is 360° ÷ 5 = 72°
        for (int side = 0; side < 5; side++) {
            this.go("forward", sideLength, speed);
            this.go("cw", 72, 30);
            this.hover(0.3);
        }
    }
    
    /**
     * Flies the drone in a hexagon pattern.
     * 
     * This pattern teaches:
     * - Six-sided regular polygon
     * - 60-degree exterior angles
     * - Relationship between number of sides and turn angles
     * 
     * @param sideLength Length of each side in centimeters
     * @param speed Flight speed as percentage
     */
    public void hexagon(int sideLength, int speed) {
        if (sideLength < 15 || sideLength > 150) {
            throw new IllegalArgumentException("Side length must be between 15 and 150 cm");
        }
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        
        // Hexagon has 6 sides, each exterior angle is 360° ÷ 6 = 60°
        for (int side = 0; side < 6; side++) {
            this.go("forward", sideLength, speed);
            this.go("cw", 60, 30);
            this.hover(0.3);
        }
    }
    
    /**
     * Flies the drone in a circular pattern using polygon approximation.
     * 
     * This alternative method demonstrates how to approximate circles when you
     * need precise radius control or want to teach geometric concepts.
     * 
     * This pattern teaches:
     * - How curves can be approximated with straight lines
     * - Circle circumference calculation  
     * - Division and modular arithmetic
     * - Precision vs. performance tradeoffs
     * - Mathematical approach to complex movements
     * 
     * @param radius The radius of the circle in centimeters
     * @param segments Number of segments to use (more = smoother circle)
     * @param speed Flight speed as percentage
     */
    public void circleApproximation(int radius, int segments, int speed) {
        if (radius < 20 || radius > 150) {
            throw new IllegalArgumentException("Radius must be between 20 and 150 cm");
        }
        if (segments < 6 || segments > 36) {
            throw new IllegalArgumentException("Segments must be between 6 and 36");
        }
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        
        // Calculate segment length using circle circumference
        double circumference = 2 * Math.PI * radius;
        int segmentLength = (int) Math.round(circumference / segments);
        
        // Calculate turn angle for each segment
        int turnAngle = 360 / segments;
        
        // Fly the circular pattern
        for (int segment = 0; segment < segments; segment++) {
            this.go("forward", segmentLength, speed);
            this.go("cw", turnAngle, 20); // Slower turns for smoother circle
            this.hover(0.2); // Very brief pause
        }
    }
    
    /**
     * Flies the drone in a smooth pentagon pattern using direct motor control.
     * 
     * This demonstrates how to create smooth geometric patterns using the same
     * direct control approach as the Python implementation, rather than discrete
     * go() commands.
     * 
     * This pattern teaches:
     * - Direct motor control for smooth geometric shapes
     * - Continuous movement vs. discrete segments
     * - How to time geometric patterns precisely
     * 
     * @param speed Flight speed as percentage (10-100)
     * @param seconds Duration of each side in seconds
     * @param direction Direction: 1 for clockwise, -1 for counter-clockwise
     */
    public void pentagonSmooth(int speed, int seconds, int direction) {
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        if (seconds < 1 || seconds > 5) {
            throw new IllegalArgumentException("Seconds must be between 1 and 5");
        }
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("Direction must be 1 (clockwise) or -1 (counter-clockwise)");
        }
        
        int power = speed;
        int duration = seconds * 1000; // Convert to milliseconds
        
        // Pentagon: 5 sides, each with smooth direct control
        for (int side = 0; side < 5; side++) {
            // Forward movement for each side
            sendControlWhile(0, power, 0, 0, duration);
            sendControlWhile(0, 0, 0, 0, 100); // Brief pause
            
            // 72-degree turn (360° ÷ 5 = 72°) using yaw control
            // Approximate turn time based on speed
            int turnTime = 72 * 10; // Rough approximation
            sendControlWhile(0, 0, 30 * direction, 0, turnTime);
            sendControlWhile(0, 0, 0, 0, 100); // Brief pause
        }
    }
    
    /**
     * Flies the drone in a smooth hexagon pattern using direct motor control.
     * 
     * This demonstrates how to create smooth geometric patterns using the same
     * direct control approach as the Python implementation, rather than discrete
     * go() commands.
     * 
     * This pattern teaches:
     * - Direct motor control for smooth geometric shapes
     * - Continuous movement vs. discrete segments
     * - How to time geometric patterns precisely
     * 
     * @param speed Flight speed as percentage (10-100)
     * @param seconds Duration of each side in seconds
     * @param direction Direction: 1 for clockwise, -1 for counter-clockwise
     */
    public void hexagonSmooth(int speed, int seconds, int direction) {
        if (speed < 10 || speed > 100) {
            throw new IllegalArgumentException("Speed must be between 10 and 100 percent");
        }
        if (seconds < 1 || seconds > 5) {
            throw new IllegalArgumentException("Seconds must be between 1 and 5");
        }
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("Direction must be 1 (clockwise) or -1 (counter-clockwise)");
        }
        
        int power = speed;
        int duration = seconds * 1000; // Convert to milliseconds
        
        // Hexagon: 6 sides, each with smooth direct control
        for (int side = 0; side < 6; side++) {
            // Forward movement for each side
            sendControlWhile(0, power, 0, 0, duration);
            sendControlWhile(0, 0, 0, 0, 100); // Brief pause
            
            // 60-degree turn (360° ÷ 6 = 60°) using yaw control
            // Approximate turn time based on speed
            int turnTime = 60 * 10; // Rough approximation
            sendControlWhile(0, 0, 30 * direction, 0, turnTime);
            sendControlWhile(0, 0, 0, 0, 100); // Brief pause
        }
    }
}
