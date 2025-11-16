---
layout: guide
title: Your First Flight
category: Student Guide
permalink: /guides/student/first-flight.html
---

## Hello, Drone!

Now that you're connected, let's make your drone fly!

### The Simplest Program

This program will take off, hover for 5 seconds, and land:

```java
import com.otabi.jcodroneedu.Drone;

public class FirstFlight {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        drone.pair();           // Connect to drone
        drone.takeoff();        // Start flying
        drone.hover(5);         // Stay in place for 5 seconds
        drone.land();           // Come back down
        drone.close();          // Disconnect
    }
}
```

### Safety First

Before you run any program:
- ✓ Clear flight zone (at least 3 meters around)
- ✓ Know where the emergency stop is (any button on controller)
- ✓ Keep drone below 2 meters indoors
- ✓ Check battery level (should be above 50%)

### Understanding Each Line

**`drone.pair()`** - Establishes connection to your drone

**`drone.takeoff()`** - Makes the drone ascend to safe hovering height (~1 meter above ground)

**`drone.hover(5)`** - Holds position for specified number of seconds

**`drone.land()`** - Safely descends to ground

**`drone.close()`** - Disconnects properly (always do this!)

### What Happens

1. Run the program
2. Drone will ascend to about 1 meter
3. It will stay there for 5 seconds
4. It will descend and land
5. Program ends

### Customizing Your Flight

Try these variations:

**Hover longer:**
```java
drone.hover(10);  // Hover for 10 seconds instead
```

**Add movement to hovering flight:**
```java
drone.takeoff();
drone.moveForward(50, "cm", 1.0);  // Move forward 50cm
drone.moveBackward(50, "cm", 1.0); // Move back to start
drone.land();
```

### Common Issues

**"Drone won't takeoff"**
- Check battery (must be above 20%)
- Make sure controller is paired
- Try again - sometimes needs second attempt

**"Drone takes off but lands immediately"**
- Battery might be low
- Something might be blocking sensors
- Try power cycling drone and controller

**"Program crashes"**
- Make sure `drone.close()` is included
- Check that drone is actually connected before flying

---

**Next:** Learn about [movement commands and flight concepts]({{ '/guides/student/flight-concepts.html' | relative_url }})
