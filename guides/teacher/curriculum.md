---
layout: guide
title: Curriculum Overview
category: Teacher Guide
permalink: /guides/teacher/curriculum.html
---

## Suggested 14-Week Curriculum

A complete semester of drone programming, building from basics to autonomous systems.

---

## Unit 1: Java Fundamentals (Weeks 1-3)

### Week 1: Getting Started

**Topics:**
- Course overview and expectations
- Java basics (variables, data types, output)
- IDE setup and first program
- Safety briefing and classroom norms

**Assignment: Hello Drone**
```java
public class HelloDrone {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            System.out.println("✓ I'm connected!");
            System.out.println("Battery: " + drone.getBattery() + "%");
            drone.takeoff();
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Write and run a Java program
- ✓ Understand try-with-resources
- ✓ Read sensor values (battery)
- ✓ Execute basic drone commands

---

### Week 2: Variables and Data Types

**Topics:**
- Primitive data types (int, double, boolean)
- Variable declaration and assignment
- Type conversion and casting
- Basic arithmetic and string concatenation

**Assignment: Flight Status**

```java
public class FlightStatus {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            int battery = drone.getBattery();
            int height = drone.getHeight();
            double altimeter = drone.getCorrectedElevation();
            boolean isFlying = battery > 20;
            
            System.out.println("Battery: " + battery + "%");
            System.out.println("Height: " + height + "cm");
            System.out.println("Altitude: " + String.format("%.2f", altimeter) + "m");
            System.out.println("Ready to fly: " + isFlying);
        }
    }
}
```

**Learning objectives:**
- ✓ Declare and use different data types
- ✓ Read multiple sensors
- ✓ Format output appropriately

---

### Week 3: Control Flow (If/Else)

**Topics:**
- Boolean expressions and logical operators
- If, else-if, else statements
- Comparison operators (==, <, >, <=, >=, !=)
- Nested conditionals

**Assignment: Safe Flight**

```java
public class SafeFlight {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            int battery = drone.getBattery();
            
            if (battery >= 80) {
                System.out.println("✓ Good to fly!");
                drone.takeoff();
                drone.land();
            } else if (battery >= 50) {
                System.out.println("⚠ Low battery, short flight only");
            } else {
                System.out.println("✗ Battery too low, charging required");
            }
        }
    }
}
```

**Learning objectives:**
- ✓ Make decisions based on sensor data
- ✓ Implement safety checks
- ✓ Use logical operators (&&, ||, !)

---

## Unit 2: Loops and Patterns (Weeks 4-5)

### Week 4: For Loops

**Topics:**
- For loop syntax and initialization
- Loop counters and iteration
- Breaking loops and continue
- Nested loops

**Assignment: Geometric Patterns**

```java
public class Patterns {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            drone.takeoff();
            
            // Square pattern
            for (int side = 0; side < 4; side++) {
                drone.moveForward(0.5);
                Thread.sleep(2000);
                drone.turnLeft(0.5);
                Thread.sleep(1000);
            }
            
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Write and understand for loops
- ✓ Create repeating patterns
- ✓ Combine loops with movement commands

**Extension:** Hexagon, triangle, star patterns

---

### Week 5: While Loops and Sensor-Based Loops

**Topics:**
- While loop syntax
- Loop conditions and termination
- Sensor-based loops
- Infinite loop prevention

**Assignment: Battery Monitor Flight**

```java
public class BatteryFlight {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            drone.takeoff();
            
            while (drone.getBattery() > 30) {
                drone.moveForward(0.3);
                Thread.sleep(500);
                System.out.println("Battery: " + drone.getBattery() + "%");
            }
            
            System.out.println("Low battery, landing!");
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Write while loops
- ✓ Terminate based on sensor conditions
- ✓ Monitor system state during flight

---

## Unit 3: Functions and Organization (Weeks 6-7)

### Week 6: Functions and Decomposition

**Topics:**
- Function definition and calling
- Parameters and return values
- Function scope and local variables
- Code reuse and DRY principle

**Assignment: Flight Patterns as Functions**

```java
public class PatternLibrary {
    static void flySquare(Drone drone) throws Exception {
        for (int i = 0; i < 4; i++) {
            drone.moveForward(0.5);
            Thread.sleep(2000);
            drone.turnLeft(0.5);
            Thread.sleep(1000);
        }
    }
    
    static void flyHexagon(Drone drone) throws Exception {
        for (int i = 0; i < 6; i++) {
            drone.moveForward(0.5);
            Thread.sleep(2000);
            drone.turnLeft(0.333);  // 60 degrees
            Thread.sleep(1000);
        }
    }
    
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            drone.takeoff();
            flySquare(drone);
            flyHexagon(drone);
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Write reusable functions
- ✓ Decompose complex tasks
- ✓ Organize code into modules

---

### Week 7: Advanced Functions and Error Handling

**Topics:**
- Exception handling (try-catch)
- Multiple return statements
- Function overloading
- Debugging functions

**Assignment: Safe Pattern Library**

```java
public class SafePatterns {
    static boolean checkBattery(Drone drone, int minimum) {
        return drone.getBattery() >= minimum;
    }
    
    static void flyPattern(Drone drone, String pattern) throws Exception {
        if (!checkBattery(drone, 40)) {
            throw new Exception("Battery too low!");
        }
        
        try {
            if (pattern.equals("square")) {
                // Fly square
            } else if (pattern.equals("hexagon")) {
                // Fly hexagon
            } else {
                throw new Exception("Unknown pattern: " + pattern);
            }
        } catch (Exception e) {
            System.out.println("Error flying pattern: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            drone.takeoff();
            flyPattern(drone, "square");
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Implement robust error handling
- ✓ Validate inputs before executing
- ✓ Debug function interactions

---

## Unit 4: Sensors and Data (Weeks 8-9)

### Week 8: Reading Multiple Sensors

**Topics:**
- Sensor APIs (battery, height, range, temperature)
- Sensor limitations and accuracy
- Data logging and recording
- Sensor fusion basics

**Assignment: Environmental Logger**

```java
public class SensorLogger {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            System.out.println("timestamp,battery,height,range,temp");
            
            drone.takeoff();
            
            for (int second = 0; second < 30; second++) {
                int battery = drone.getBattery();
                int height = drone.getHeight();
                int range = drone.getRange();
                double temp = drone.getTemperature();
                
                System.out.println(second + "," + battery + "," + 
                                 height + "," + range + "," + temp);
                
                Thread.sleep(1000);
            }
            
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Read and interpret sensor data
- ✓ Log data for analysis
- ✓ Understand sensor limitations

---

### Week 9: Sensor-Based Control

**Topics:**
- Feedback loops
- PID concepts (basic)
- State machines
- Real-time decision making

**Assignment: Altitude Maintenance**

```java
public class AltitudeControl {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            int targetHeight = 100;  // 100cm
            
            drone.takeoff();
            
            for (int second = 0; second < 20; second++) {
                int currentHeight = drone.getHeight();
                
                if (currentHeight < targetHeight - 10) {
                    drone.moveUp(0.3);
                } else if (currentHeight > targetHeight + 10) {
                    drone.moveDown(0.3);
                } else {
                    drone.hover();
                }
                
                Thread.sleep(1000);
            }
            
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Implement feedback control
- ✓ Maintain system state
- ✓ React to environmental changes

---

## Unit 5: Autonomous Systems (Weeks 10-12)

### Week 10: Path Planning

**Topics:**
- Waypoint-based navigation
- Distance and heading calculations
- Navigation algorithms
- GPS concepts (without actual GPS)

**Assignment: Waypoint Flight**

```java
public class WaypointFlight {
    static class Waypoint {
        double x, y, z;
        Waypoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    public static void main(String[] args) throws Exception {
        Waypoint[] path = {
            new Waypoint(0, 0, 1),
            new Waypoint(1, 0, 1),
            new Waypoint(1, 1, 1),
            new Waypoint(0, 1, 1)
        };
        
        try (Drone drone = new Drone(true)) {
            drone.takeoff();
            
            for (Waypoint wp : path) {
                System.out.println("Flying to (" + wp.x + ", " + wp.y + ")");
                // Navigate to waypoint
                Thread.sleep(2000);
            }
            
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Represent positions and paths
- ✓ Navigate between waypoints
- ✓ Plan autonomous routes

---

### Week 11: Obstacle Avoidance

**Topics:**
- Range sensor usage
- Collision detection
- Reactive behaviors
- State-based navigation

**Assignment: Autonomous Explorer**

```java
public class AutoExplorer {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            drone.takeoff();
            
            while (drone.getBattery() > 25) {
                int range = drone.getRange();
                
                if (range < 50) {
                    // Obstacle detected!
                    drone.turnLeft(0.5);
                    Thread.sleep(1500);
                } else if (range < 100) {
                    // Approaching obstacle
                    drone.moveForward(0.2);
                } else {
                    // Clear path
                    drone.moveForward(0.5);
                }
                
                Thread.sleep(200);
            }
            
            drone.land();
        }
    }
}
```

**Learning objectives:**
- ✓ Implement reactive behavior
- ✓ Handle collision detection
- ✓ Build autonomous systems

---

### Week 12: Multi-Sensor Integration

**Topics:**
- Fusing multiple sensors
- State estimation
- Complex autonomous behaviors
- Logging and analysis

**Assignment: Smart Explorer**

Combines sensors for intelligent behavior (temperature, altitude, range, battery)

---

## Unit 6: Capstone Project (Weeks 13-14)

### Week 13: Project Planning

**Student chooses from:**

1. **Delivery System**
   - Navigate to target location
   - Land and "deliver"
   - Return to start

2. **Environmental Monitor**
   - Collect data at multiple locations
   - Log temperature and altitude
   - Create visualization

3. **Art Installation**
   - Choreographed light show
   - Synchronized multi-drone flight
   - LED animations

4. **Physics Experiment**
   - Measure gravity
   - Calculate aerodynamic properties
   - Model flight characteristics

5. **Custom Project**
   - Student-designed application
   - Must use at least 3 sensors
   - Demonstrates learned concepts

**Deliverables:**
- Design document
- Implementation plan
- Working code
- Demonstration video
- Reflection paper

---

### Week 14: Presentations and Reflection

**Each student/group:**
- Demonstrates working project
- Explains design decisions
- Discusses challenges and solutions
- Reflects on learning

---

## Standards Alignment

This curriculum aligns with:

- **CSTA K-12:** Variables, data structures, algorithms, testing
- **AP CSP:** Algorithms, data, internet, impact
- **Next Generation Science:** Engineering design, systems, data analysis

See [Standards Alignment]({{ '/guides/teacher/standards.html' | relative_url }}) for detailed mapping.

---

## Customization

**Accelerated track:**
- Compress units 1-2 to weeks 1-2
- Focus weeks 3-10 on autonomous systems
- Weeks 11-14 for advanced projects

**Extended track:**
- Add unit on computer vision (OpenCV)
- Introduce ROS (Robot Operating System)
- Multi-drone coordination systems

---

Next: [Standards Alignment]({{ '/guides/teacher/standards.html' | relative_url }})
