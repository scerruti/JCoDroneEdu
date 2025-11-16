---
layout: guide
title: Next Steps
category: Student Guide
permalink: /guides/student/next-steps.html
---

## Beyond the Basics

You've learned the fundamentals! Here are exciting directions to explore:

### Advanced Flight Patterns

**Figure-8 Flight:**
```java
try (Drone drone = new Drone(true)) {
    drone.takeoff();
    
    // First circle
    for (int i = 0; i < 8; i++) {
        drone.moveForward(0.5);
        drone.turnLeft(0.5);
        Thread.sleep(200);
    }
    
    // Second circle (opposite direction)
    for (int i = 0; i < 8; i++) {
        drone.moveForward(0.5);
        drone.turnRight(0.5);
        Thread.sleep(200);
    }
    
    drone.land();
}
```

**Altitude Waves:**
```java
try (Drone drone = new Drone(true)) {
    drone.takeoff();
    
    for (int i = 0; i < 10; i++) {
        drone.moveUp(0.3);
        Thread.sleep(500);
        drone.moveDown(0.3);
        Thread.sleep(500);
    }
    
    drone.land();
}
```

### Sensor-Guided Flight

**Hover at Fixed Height:**
```java
try (Drone drone = new Drone(true)) {
    drone.takeoff();
    
    for (int second = 0; second < 30; second++) {
        int currentHeight = drone.getHeight();
        int targetHeight = 100;  // 100cm
        
        if (currentHeight < targetHeight - 5) {
            drone.moveUp(0.2);
        } else if (currentHeight > targetHeight + 5) {
            drone.moveDown(0.2);
        } else {
            drone.hover();
        }
        
        Thread.sleep(1000);
    }
    
    drone.land();
}
```

**Obstacle Avoidance:**
```java
try (Drone drone = new Drone(true)) {
    drone.takeoff();
    
    while (drone.getBattery() > 30) {
        int distance = drone.getRange();
        
        if (distance < 50) {
            // Too close, turn and back up
            drone.turnLeft(0.5);
            drone.moveBackward(0.3);
        } else if (distance < 100) {
            // Getting close, turn slowly
            drone.turnLeft(0.2);
        } else {
            // Clear ahead
            drone.moveForward(0.3);
        }
        
        Thread.sleep(200);
    }
    
    drone.land();
}
```

### LED Animations

**Status Indicator:**
```java
try (Drone drone = new Drone(true)) {
    drone.takeoff();
    
    // Green for flying
    drone.setLED(0, 255, 0);
    Thread.sleep(5000);
    
    // Yellow for low battery
    if (drone.getBattery() < 50) {
        drone.setLED(255, 255, 0);
    }
    
    // Red for landing
    drone.setLED(255, 0, 0);
    drone.land();
    drone.setLED(0, 0, 0);
}
```

### Sound Effects

**Audio Feedback:**
```java
try (Drone drone = new Drone(true)) {
    // Play startup sound
    drone.playFrequency(1000, 200);  // 1000Hz for 200ms
    Thread.sleep(300);
    
    drone.takeoff();
    
    // Play success sound
    drone.playFrequency(1000, 100);
    Thread.sleep(100);
    drone.playFrequency(1500, 100);
    
    drone.land();
}
```

### Multi-Drone Coordination

**Synchronized Flight (with 2 drones):**
```java
try (Drone drone1 = new Drone(true);
     Drone drone2 = new Drone(true)) {
    
    drone1.takeoff();
    drone2.takeoff();
    
    for (int i = 0; i < 5; i++) {
        drone1.moveForward(0.5);
        drone2.moveBackward(0.5);
        Thread.sleep(500);
    }
    
    drone1.land();
    drone2.land();
}
```

### Project Ideas

**Weather Station:**
- Mount temperature sensor
- Log readings while flying
- Create heat map visualization

**Delivery System:**
- Attach small container
- Autonomously navigate waypoints
- Release on command

**Surveillance Patrol:**
- Fly repeating perimeter
- Take photos at each corner
- Alert if motion detected

**Art Installation:**
- Choreograph fleet of drones
- Synchronized LED patterns
- Timed music soundtrack

**Physics Simulation:**
- Measure gravity with accelerometer
- Calculate lift requirements
- Model aerodynamic forces

### Performance Optimization

**Reduce Latency:**
```java
// Bad: Multiple separate commands
drone.moveForward(0.5);
Thread.sleep(100);
drone.moveLeft(0.3);

// Better: Combined movement
drone.move(0.5, -0.3, 0, 0);  // forward, left, vertical, rotation
```

**Batch Operations:**
```java
// Instead of individual LED changes, prepare commands
drone.setLED(255, 0, 0);
drone.playFrequency(1000, 100);
// These execute together
```

### Advanced Topics to Research

1. **Kalman Filters** - Sensor fusion for better position estimates
2. **PID Controllers** - Smooth autonomous hovering
3. **OpenCV** - Visual processing for object tracking
4. **ROS Integration** - Robot Operating System connectivity
5. **Machine Learning** - Neural networks for flight pattern recognition

---

## Resources

- **Full API Documentation** - [Javadoc]({{ '/javadoc/index.html' | relative_url }})
- **GitHub Examples** - Working code for all patterns
- **Teacher Guide** - [For educators]({{ '/guides/teacher/index.html' | relative_url }})
- **Architecture Documentation** - [Design and implementation]({{ '/architecture/design-guide/index.html' | relative_url }})

---

## Keep Learning!

The best part about this project is that you can keep building on it. Start simple, add complexity gradually, and don't hesitate to experiment. Every great programmer started with "Hello World"—you're now flying drones!

**What will you build next?**
