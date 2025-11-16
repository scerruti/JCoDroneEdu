---
layout: guide
title: Flight Concepts
category: Student Guide
permalink: /guides/student/flight-concepts.html
---

## Movement Commands

Once your drone is in the air, you can move it in six directions.

### Six Directions of Movement

```java
drone.moveForward(distance, unit, speed);
drone.moveBackward(distance, unit, speed);
drone.moveLeft(distance, unit, speed);
drone.moveRight(distance, unit, speed);
drone.moveUp(distance, unit);
drone.moveDown(distance, unit);
```

### Parameters

- **distance**: How far to move (number)
- **unit**: Measurement unit - "cm", "m", "in", or "ft"
- **speed**: 0.0 (stopped) to 1.0 (full speed) - optional, defaults to 1.0

### Example Flight Pattern

```java
drone.takeoff();
drone.moveForward(100, "cm", 0.5);   // Move forward slowly
drone.moveRight(50, "cm", 0.5);      // Strafe right
drone.moveBackward(100, "cm", 0.5);  // Back to start
drone.moveLeft(50, "cm", 0.5);       // Back to original position
drone.land();
```

## Turning and Rotation

Make your drone face different directions.

```java
drone.turnRight(90);    // Quarter turn right
drone.turnLeft(45);     // Turn left 45 degrees
drone.turn(180);        // Face backward
```

**Understanding angles:**
- 0° = Original direction
- 90° = Quarter turn right
- 180° = Facing backward
- 270° = Quarter turn left
- 360° = Full circle

## Using Variables

Store flight parameters in variables for flexibility:

```java
int forwardDistance = 150;  // centimeters
int sideDistance = 75;
double speed = 0.7;

drone.takeoff();
drone.moveForward(forwardDistance, "cm", speed);
drone.moveRight(sideDistance, "cm", speed);
drone.moveBackward(forwardDistance, "cm", speed);
drone.moveLeft(sideDistance, "cm", speed);
drone.land();
```

**Why use variables?**
- Easy to adjust your flight plan
- Reuse values without typing them again
- Make code easier to understand

## Conditionals (if/else)

Make decisions based on sensor data:

```java
drone.takeoff();

int battery = drone.getBattery();

if (battery > 50) {
    drone.moveUp(100, "cm");
    drone.hover(3);
} else if (battery > 30) {
    drone.moveUp(50, "cm");
    drone.hover(1);
} else {
    drone.land();
    return;
}

drone.land();
```

---

**Next:** Learn to [read sensor data]({{ '/guides/student/sensors.html' | relative_url }})
