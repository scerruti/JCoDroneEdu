---
layout: guide
title: Flight Patterns
category: Student Guide
permalink: /guides/student/flight-patterns.html
---

## Flying Shapes with Loops

Use for loops to repeat commands and create patterns.

### Flying a Square

```java
drone.takeoff();

// Fly a square: 4 sides, 90-degree turns
for (int side = 0; side < 4; side++) {
    drone.moveForward(50, "cm", 1.0);
    drone.turnRight(90);
}

drone.land();
```

### Flying a Triangle

```java
drone.takeoff();

// Fly a triangle: 3 sides, 120-degree turns
for (int side = 0; side < 3; side++) {
    drone.moveForward(60, "cm", 1.0);
    drone.turnRight(120);
}

drone.land();
```

### Flying a Hexagon

```java
drone.takeoff();

// Fly a hexagon: 6 sides, 60-degree turns
for (int side = 0; side < 6; side++) {
    drone.moveForward(40, "cm", 1.0);
    drone.turnRight(60);
}

drone.land();
```

## While Loops and Sensors

Fly until a condition is met.

### Fly Until Obstacle Detected

```java
drone.takeoff();

while (drone.getFrontRange() > 40) {
    drone.moveForward(10, "cm", 0.5);
    System.out.println("Distance: " + drone.getFrontRange());
}

System.out.println("Obstacle detected!");
drone.land();
```

### Fly Until Battery Low

```java
drone.takeoff();

while (drone.getBattery() > 30) {
    drone.moveForward(20, "cm", 1.0);
    drone.turnRight(45);
    System.out.println("Battery: " + drone.getBattery() + "%");
}

System.out.println("Battery low - landing");
drone.land();
```

## Nested Loops

Create complex patterns with loops inside loops.

### 3x3 Grid Pattern

```java
drone.takeoff();

// Fly a 3x3 grid
for (int row = 0; row < 3; row++) {
    for (int col = 0; col < 3; col++) {
        drone.moveForward(40, "cm", 1.0);
        drone.hover(1);  // Pause at each point
    }
    
    // Move to next row
    drone.turnLeft(90);
    drone.moveForward(40, "cm", 1.0);
    drone.turnRight(90);
}

drone.land();
```

## Functions for Code Organization

Break your programs into reusable functions.

### Reusable Flight Function

```java
public class FlightPatterns {
    
    public static void flySquare(Drone drone, int sideLength) {
        for (int i = 0; i < 4; i++) {
            drone.moveForward(sideLength, "cm", 1.0);
            drone.turnRight(90);
        }
    }
    
    public static boolean isSafeToFly(Drone drone) {
        return drone.getBattery() > 30;
    }
    
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        if (!isSafeToFly(drone)) {
            System.out.println("Battery too low!");
            drone.close();
            return;
        }
        
        drone.takeoff();
        
        flySquare(drone, 40);
        flySquare(drone, 60);
        flySquare(drone, 80);
        
        drone.land();
        drone.close();
    }
}
```

---

**Next:** Check the [API Reference]({{ '/guides/student/api-reference.html' | relative_url }}) for all available methods
