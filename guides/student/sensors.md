---
layout: guide
title: Working with Sensors
category: Student Guide
permalink: /guides/student/sensors.html
---

## Reading Sensor Data

Your drone has multiple sensors that provide information about its state and environment.

### Battery Level

Always check battery before flying:

```java
int batteryPercent = drone.getBattery();
System.out.println("Battery: " + batteryPercent + "%");

if (batteryPercent < 20) {
    System.out.println("WARNING: Land soon!");
}
```

**Battery guidelines:**
- 100% = Fully charged
- 50% = Half capacity
- 20% = Time to land
- <10% = Emergency land now

### Height Sensors

The drone has two different sensors for measuring height:

**Range Sensor (Bottom) - Best for low altitudes:**
```java
int height = drone.getHeight();  // Returns centimeters
```
- Works up to 150 cm
- Very accurate for low flights
- Returns 999 if out of range

**Pressure Sensor (Barometer) - Best for high altitudes:**
```java
double elevation = drone.getCorrectedElevation();  // Returns meters
```
- Works at any altitude
- Less accurate than range sensor at low heights
- Automatically corrected for firmware offset

### Front Range Sensor

Detect obstacles ahead:

```java
int distance = drone.getFrontRange();  // Returns centimeters

if (distance < 30) {
    System.out.println("Obstacle nearby!");
}
```

### Motion Sensors (IMU)

Read the drone's orientation and acceleration:

```java
double angleX = drone.getAngleX();    // Roll (left/right tilt)
double angleY = drone.getAngleY();    // Pitch (forward/back tilt)
double angleZ = drone.getAngleZ();    // Yaw (rotation)

double accelX = drone.getAccelX();    // Acceleration (m/s²)
double accelY = drone.getAccelY();
double accelZ = drone.getAccelZ();
```

### Temperature

Read the barometer temperature (note: measures sensor chip temperature, not air):

```java
double tempC = drone.getBarometerTemperature();
System.out.println("Sensor temp: " + tempC + "°C");

// Typically 10-15°C cooler than actual air
double actualTemp = tempC + 12.0;  // Add typical offset
```

---

**Next:** Build [flight patterns with loops]({{ '/guides/student/flight-patterns.html' | relative_url }})
