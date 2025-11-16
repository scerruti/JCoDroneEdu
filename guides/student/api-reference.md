---
layout: guide
title: API Reference
category: Student Guide
permalink: /guides/student/api-reference.html
---

Quick reference for the most commonly used methods, organized by category.

## Connection & Setup

```java
Drone drone = new Drone();          // Create drone object
drone.pair();                       // Connect to drone
drone.close();                      // Disconnect when done
```

## Flight Control

```java
// Basic flight
drone.takeoff();                    // Ascend to hover height
drone.land();                       // Descend and land
drone.hover(seconds);               // Hold position
drone.emergencyStop();              // STOP ALL MOTORS NOW

// Movement
drone.moveForward(dist, unit, speed);
drone.moveBackward(dist, unit, speed);
drone.moveLeft(dist, unit, speed);
drone.moveRight(dist, unit, speed);
drone.moveUp(dist, unit);
drone.moveDown(dist, unit);

// Turning
drone.turnRight(degrees);
drone.turnLeft(degrees);
drone.turn(degrees);                // Absolute angle
```

## Sensors

```java
// Battery
int battery = drone.getBattery();   // 0-100%

// Height/Distance
int height = drone.getHeight();     // cm, range sensor
double elevation = drone.getCorrectedElevation();  // m, pressure
int front = drone.getFrontRange();  // cm, front sensor
int bottom = drone.getBottomRange(); // cm, bottom sensor

// Motion (IMU)
double angleX = drone.getAngleX();   // degrees
double angleY = drone.getAngleY();
double angleZ = drone.getAngleZ();
double accelX = drone.getAccelX();   // m/s²
double accelY = drone.getAccelY();
double accelZ = drone.getAccelZ();

// Environment
double temp = drone.getBarometerTemperature();  // °C
double tempF = drone.getBarometerTemperature("F"); // °F
double pressure = drone.getPressure();           // Pa
```

## LEDs

```java
// Drone LEDs (brightness 0-255)
drone.setDroneLEDRed(brightness);
drone.setDroneLEDGreen(brightness);
drone.setDroneLEDBlue(brightness);
drone.droneLEDOff();

// Controller LEDs
drone.setControllerLED(red, green, blue, brightness);
drone.controllerLEDOff();
```

## Sound

```java
// Play a tone
drone.droneBuzzer(frequency, duration);  // frequency in Hz, duration in ms

// Play sequences
drone.droneBuzzerSequence("success");    // or "fail", "warning"
```

## Parameters

**Distance Units:**
- "cm" - Centimeters
- "m" - Meters
- "in" - Inches
- "ft" - Feet

**Speed Values:**
- 0.0 = Stopped
- 0.5 = Half speed
- 1.0 = Full speed

**Angles:**
- 0-360 degrees
- 90° = Quarter turn
- 180° = Half turn
- 270° = Three-quarter turn

---

For complete API documentation, see the [Javadoc Reference]({{ site.url }}/javadoc/index.html)

**Next:** [Debugging common problems]({{ '/guides/student/debugging.html' | relative_url }})
