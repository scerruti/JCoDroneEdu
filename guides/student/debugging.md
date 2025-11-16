---
layout: guide
title: Debugging
category: Student Guide
permalink: /guides/student/debugging.html
---

## Common Problems & Solutions

### Connection Issues

**Problem: Drone won't connect**

Solutions:
1. Check USB cable is connected
2. Power on controller and drone
3. Try turning controller off and on
4. Restart your IDE
5. Check Device Manager for USB device

**Code to diagnose:**
```java
try {
    drone.pair();
    System.out.println("✓ Connected");
} catch (Exception e) {
    System.out.println("✗ Failed: " + e.getMessage());
}
```

### Flight Issues

**Problem: Drone drifts sideways**

Solution: Calibrate the trim settings
```java
drone.setTrim(0, 0, 0, 0);  // Reset first
```

**Problem: Height sensor returns 999**

Explanation: Range sensor maxes out at 150cm

Solution: Use pressure-based elevation for high flights
```java
if (height >= 999) {
    double elevation = drone.getCorrectedElevation();
    System.out.println("High: " + elevation + "m");
} else {
    System.out.println("Height: " + height + "cm");
}
```

**Problem: Battery drains too fast**

Solutions:
- Fully charge before each flight
- Avoid aggressive maneuvers
- Land at 20-30% battery
- Let battery cool between flights

### Program Issues

**Problem: Program crashes mid-flight**

Solution: Always use try-catch and cleanup:
```java
try (Drone drone = new Drone(true)) {
    drone.takeoff();
    // Your code here
    drone.land();
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}
// Drone auto-disconnects
```

**Problem: Movements are jerky**

Solutions:
- Reduce speed (use 0.3-0.7 instead of 1.0)
- Add hover pauses between moves
- Use smaller distance increments
- Ensure batteries are charged

### Sensor Issues

**Problem: Sensor readings seem wrong**

Solution: Some offsets are normal and documented
- Temperature is 10-15°C low (sensor die effect)
- Elevation has ~110m firmware offset (we correct this)

---

## General Debugging Strategy

1. **Break into small pieces** - Test one movement at a time
2. **Add print statements** - Show what's happening
3. **Check battery** - Low battery causes all sorts of issues
4. **Verify connection** - Make sure drone is actually connected
5. **Read error messages** - They often tell you exactly what's wrong

## Getting Help

- **Check API Reference** - See what methods exist
- **Review examples** - GitHub has working code
- **Ask your teacher** - Debugging together is a great learning opportunity
- **Visit Javadoc** - Full documentation of all methods

---

**Next:** [Learn advanced topics and projects]({{ '/guides/student/next-steps.html' | relative_url }})
