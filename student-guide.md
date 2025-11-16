# CoDrone EDU Student Guide

**Welcome to Programming with Drones!**

This guide will help you learn Java programming while flying the CoDrone EDU. Whether you're completely new to programming or have some experience, you'll learn by writing code that makes a real drone take flight, navigate obstacles, and respond to sensors.

**Who is this for?** Students in grades 9-12, especially those taking AP Computer Science A or similar introductory programming courses.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Your First Flight](#your-first-flight)
3. [Core Flight Concepts](#core-flight-concepts)
4. [Working with Sensors](#working-with-sensors)
5. [Building Flight Patterns](#building-flight-patterns)
6. [API Reference by Category](#api-reference-by-category)
7. [Common Problems & Debugging](#common-problems--debugging)
8. [Next Steps & Advanced Topics](#next-steps--advanced-topics)

---

## Getting Started

### What You'll Need

**Hardware:**
- CoDrone EDU drone and controller
- USB cable (for controller connection)
- Charged batteries (drone and controller)

**Software:**
- Java Development Kit (JDK) 11 or newer
- An IDE (IntelliJ IDEA, VS Code, or BlueJ)
- CoDrone EDU Java library (this project!)

### Installation

1. **Install the JDK** if you haven't already
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [AdoptOpenJDK](https://adoptopenjdk.net/)
   - Verify installation: Open terminal/command prompt and type `java -version`

2. **Set up your IDE**
   - **VS Code** (Recommended for beginners): Install the Java Extension Pack
   - **IntelliJ IDEA**: Community Edition is free and excellent for Java
   - **BlueJ**: Designed for education, very beginner-friendly

3. **Get the CoDrone EDU Java library**
   - Download from [GitHub Releases](https://github.com/scerruti/JCoDroneEdu/releases)
   - Add the JAR file to your project's dependencies
   - Your teacher may have already set this up for you!

### Connecting Your Drone

Before you can program your drone, you need to physically connect it:

1. **Turn on the controller** (the handheld remote)
2. **Turn on the drone** (the flying part)
3. **Connect controller to computer** via USB cable
4. **Wait for pairing** - The controller's LEDs will show connection status

**Status Lights:**
- Solid blue: Connected and ready
- Blinking: Searching for drone
- Red: Error or low battery

### Testing the Connection

Let's make sure everything works! Create a new file called `ConnectionTest.java`:

```java
import com.otabi.jcodroneedu.Drone;

public class ConnectionTest {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        try {
            drone.pair();
            System.out.println("Connected successfully!");
            
            int battery = drone.getBattery();
            System.out.println("Battery level: " + battery + "%");
            
            drone.close();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
```

**What this does:**
- Creates a `Drone` object
- Connects to your drone with `pair()`
- Reads the battery level
- Disconnects properly with `close()`

If you see "Connected successfully!" and your battery level, you're ready to fly!

**Learn More:** [CoDrone EDU Getting Started](https://docs.robolink.com/docs/CoDroneEDU/Python/getting-started)

---

## Your First Flight

Now for the exciting part - making your drone fly! We'll start with the simplest possible flight program.

### Hello, Drone! (Lesson L0101)

This program makes your drone take off, hover for 5 seconds, and land:

```java
import com.otabi.jcodroneedu.Drone;

public class FirstFlight {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        drone.pair();              // Connect to drone
        System.out.println("Connected!");
        
        drone.takeoff();           // Start flying!
        System.out.println("In the air!");
        
        drone.hover(5);            // Stay in place for 5 seconds
        System.out.println("Hovering...");
        
        drone.land();              // Come back down
        System.out.println("Landed!");
        
        drone.close();             // Disconnect
        System.out.println("Done!");
    }
}
```

**Understanding Each Line:**

- `Drone drone = new Drone()` - Creates your drone object (like getting a remote control)
- `drone.pair()` - Establishes connection (like pairing Bluetooth)
- `drone.takeoff()` - Makes the drone ascend to a safe hovering height (~1 meter)
- `drone.hover(5)` - Holds position for 5 seconds
- `drone.land()` - Safely descends and lands
- `drone.close()` - Disconnects and cleans up

**Safety First!** Always make sure you:
- Have clear space around the drone (at least 3 meters)
- Know where the emergency stop is (press any button on controller)
- Keep the drone below 2 meters indoors
- Land before battery gets below 20%

**Learn More:** [First Flight Tutorial](https://learn.robolink.com/lesson/codrone-edu/intro-python-lesson1/)

---

## Core Flight Concepts

### Basic Movement Commands

Once your drone is in the air, you can move it in six directions:

```java
// Move forward 50 cm at normal speed (1.0)
drone.moveForward(50, "cm", 1.0);

// Move backward
drone.moveBackward(50, "cm", 1.0);

// Move left (strafe)
drone.moveLeft(50, "cm", 1.0);

// Move right (strafe)
drone.moveRight(50, "cm", 1.0);

// Move up
drone.moveUp(50, "cm");

// Move down
drone.moveDown(50, "cm");
```

**Parameters Explained:**
- **Distance**: How far to move (number)
- **Unit**: "cm" for centimeters, "m" for meters, "in" for inches
- **Speed**: 0.0 (stopped) to 1.0 (full speed) - optional, defaults to 1.0

**Example Flight Pattern:**
```java
drone.takeoff();
drone.moveForward(100, "cm", 0.5);   // Move forward slowly
drone.moveRight(50, "cm", 0.5);      // Strafe right
drone.moveBackward(100, "cm", 0.5);  // Back to start
drone.land();
```

### Turning and Rotation

Make your drone face different directions:

```java
// Turn right 90 degrees
drone.turnRight(90);

// Turn left 45 degrees  
drone.turnLeft(45);

// Turn to a specific angle (0-360 degrees)
drone.turn(180);  // Turn around completely
```

**Understanding Angles:**
- 0° = Facing original direction
- 90° = Quarter turn to the right
- 180° = Facing backward
- 270° = Quarter turn to the left
- 360° = Full circle (back to start)

**Learn More:** [Movement API Documentation](https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move)

### Flight with Variables (Lesson L0104)

Use variables to make your flight programs flexible:

```java
// Define flight parameters
int flightHeight = 100;  // centimeters
int forwardDistance = 150;
int sideDistance = 75;

drone.takeoff();

// Fly a pattern using variables
drone.moveUp(flightHeight, "cm");
drone.moveForward(forwardDistance, "cm", 1.0);
drone.moveRight(sideDistance, "cm", 1.0);
drone.moveBackward(forwardDistance, "cm", 1.0);
drone.moveLeft(sideDistance, "cm", 1.0);

drone.land();
```

**Why use variables?**
- Easy to adjust your flight plan
- Reuse values in multiple places
- Make your code easier to understand
- Practice AP CSA concepts!

### Using Conditionals (Lesson L0106)

Make decisions in your code based on sensor data:

```java
drone.takeoff();

// Check battery level before continuing
int battery = drone.getBattery();

if (battery > 50) {
    System.out.println("Battery good - flying high!");
    drone.moveUp(100, "cm");
    drone.hover(3);
} else if (battery > 30) {
    System.out.println("Battery medium - flying low");
    drone.moveUp(50, "cm");
    drone.hover(2);
} else {
    System.out.println("Battery low - landing immediately");
    drone.land();
    return;  // Exit program
}

drone.land();
```

**Learn More:** [Conditionals Lesson](https://learn.robolink.com/lesson/codrone-edu/intro-python-lesson6/)

---

## Working with Sensors

Your drone has multiple sensors that tell you about its environment. Let's learn to read them!

### Battery Level

Always check your battery before flying:

```java
int batteryPercent = drone.getBattery();
System.out.println("Battery: " + batteryPercent + "%");

if (batteryPercent < 20) {
    System.out.println("WARNING: Low battery!");
}
```

**Battery Tips:**
- 100% = Fully charged
- 50% = Half capacity
- 20% = Land soon
- 10% or below = Emergency land immediately

### Height and Distance Sensors

The drone uses two different sensors for measuring height:

**Bottom Range Sensor** (for low altitudes):
```java
// Get height above ground (works up to 150 cm)
int height = drone.getHeight();  // Returns cm
System.out.println("Height: " + height + " cm");

// Check if too high
if (height > 150) {
    System.out.println("Using range sensor limit!");
    // Sensor returns 999 when out of range
}
```

**Barometric Pressure Sensor** (for high altitudes):
```java
// Get altitude from air pressure
double elevation = drone.getCorrectedElevation();  // Returns meters
System.out.println("Elevation: " + elevation + " m");
```

**Which sensor should you use?**
- **Short flights (under 150cm)**: Use `getHeight()` - it's more accurate
- **High flights**: Use `getCorrectedElevation()` - it works at any height
- **For comparison**: The elevation sensor has an offset of about 110 meters in the raw firmware value, but `getCorrectedElevation()` fixes this automatically!

**Learn More:** [Sensor Data Documentation](https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#sensors)

### Front Range Sensor

Detect obstacles in front of your drone:

```java
drone.takeoff();

while (true) {
    int frontDistance = drone.getFrontRange();  // cm
    System.out.println("Object " + frontDistance + " cm ahead");
    
    if (frontDistance < 30) {
        System.out.println("Obstacle detected! Stopping.");
        drone.hover(1);
        break;
    }
    
    drone.moveForward(10, "cm", 0.3);  // Move slowly
}

drone.land();
```

**Range Sensor Values:**
- 0-200 cm: Detects obstacles accurately
- 999: Nothing detected or out of range

### Motion Sensors (IMU)

Read the drone's orientation and acceleration:

```java
// Get drone's tilt angles
double angleX = drone.getAngleX();  // Roll (left/right tilt)
double angleY = drone.getAngleY();  // Pitch (forward/back tilt)
double angleZ = drone.getAngleZ();  // Yaw (rotation)

System.out.println("Roll: " + angleX + "°");
System.out.println("Pitch: " + angleY + "°");
System.out.println("Yaw: " + angleZ + "°");

// Get acceleration forces
double accelX = drone.getAccelX();  // m/s²
double accelY = drone.getAccelY();
double accelZ = drone.getAccelZ();
```

**Understanding IMU Data:**
- **Angles**: Measured in degrees, tells you drone's orientation
- **Acceleration**: Measured in m/s², tells you forces acting on drone
- **Gyroscope**: Tells you rotation speed (angular velocity)

**Learn More:** [IMU Sensor Guide](https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#gyroscope)

### Temperature Sensor

Your drone can measure temperature, but there's a catch:

```java
// Get temperature from barometer sensor
double tempC = drone.getBarometerTemperature();
System.out.println("Sensor temperature: " + tempC + "°C");

// Convert to Fahrenheit
double tempF = drone.getBarometerTemperature("F");
System.out.println("Sensor temperature: " + tempF + "°F");
```

**Important Note:** The temperature sensor measures the sensor chip's temperature, which is typically 10-15°C cooler than the actual air temperature. This is a physical characteristic of the sensor, not a bug! For more accurate readings, you could add a calibration offset:

```java
double sensorTemp = drone.getBarometerTemperature();
double actualTemp = sensorTemp + 12.0;  // Add typical offset
System.out.println("Estimated air temperature: " + actualTemp + "°C");
```

This is a great opportunity to learn about sensor calibration - real sensors always have imperfections!

---

## Building Flight Patterns

Now let's combine what you've learned to create interesting flight patterns.

### Flying Shapes with Loops (Lesson L0107)

**Square Pattern:**
```java
drone.takeoff();

// Fly a square using a for loop
for (int side = 0; side < 4; side++) {
    drone.moveForward(50, "cm", 1.0);
    drone.turnRight(90);
}

drone.land();
```

**Triangle Pattern:**
```java
drone.takeoff();

// Fly a triangle (3 sides, 120° turns)
for (int side = 0; side < 3; side++) {
    drone.moveForward(60, "cm", 1.0);
    drone.turnRight(120);
}

drone.land();
```

**Learn More:** [For Loops Lesson](https://learn.robolink.com/lesson/codrone-edu/intro-python-lesson7/)

### While Loops for Sensor-Based Flight (Lesson L0108)

Fly until you detect an obstacle:

```java
drone.takeoff();

// Keep flying forward until obstacle is close
while (drone.getFrontRange() > 40) {
    drone.moveForward(10, "cm", 0.5);
    System.out.println("Distance: " + drone.getFrontRange() + " cm");
}

System.out.println("Obstacle detected!");
drone.hover(2);
drone.land();
```

Fly until battery gets low:

```java
drone.takeoff();

int flightCycles = 0;
while (drone.getBattery() > 30) {
    // Fly in a small circle
    drone.moveForward(30, "cm", 1.0);
    drone.turnRight(45);
    
    flightCycles++;
    System.out.println("Completed " + flightCycles + " cycles");
    System.out.println("Battery: " + drone.getBattery() + "%");
}

System.out.println("Low battery - landing");
drone.land();
```

**Learn More:** [While Loops Lesson](https://learn.robolink.com/lesson/codrone-edu/intro-python-lesson8/)

### Nested Loops for Complex Patterns (Lesson L0109)

Create a grid search pattern:

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

### Functions for Code Organization (Lesson L0110)

Break your flight program into reusable pieces:

```java
public class FlightFunctions {
    
    // Function to fly a square
    public static void flySquare(Drone drone, int sideLength) {
        for (int i = 0; i < 4; i++) {
            drone.moveForward(sideLength, "cm", 1.0);
            drone.turnRight(90);
        }
    }
    
    // Function to check safety before flying
    public static boolean isSafeToFly(Drone drone) {
        int battery = drone.getBattery();
        if (battery < 30) {
            System.out.println("Battery too low: " + battery + "%");
            return false;
        }
        return true;
    }
    
    // Main flight program
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        if (!isSafeToFly(drone)) {
            drone.close();
            return;
        }
        
        drone.takeoff();
        
        // Fly multiple squares of different sizes
        flySquare(drone, 40);
        flySquare(drone, 60);
        flySquare(drone, 80);
        
        drone.land();
        drone.close();
    }
}
```

**Why use functions?**
- Makes code easier to read
- Can reuse code in multiple places
- Easier to test and debug
- Essential for AP CSA!

**Learn More:** [Functions Lesson](https://learn.robolink.com/lesson/codrone-edu/intro-python-lesson10/)

---

## API Reference by Category

Here's a quick reference of the most commonly used methods, organized by what they do.

### Connection & Setup

```java
Drone drone = new Drone();        // Create drone object
drone.pair();                     // Connect to drone
drone.close();                    // Disconnect when done
```

### Flight Control

```java
// Basic flight
drone.takeoff();                  // Ascend to hover height
drone.land();                     // Descend and land
drone.hover(seconds);             // Hold position
drone.emergencyStop();            // STOP ALL MOTORS NOW

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
drone.turn(degrees);              // Absolute angle
```

### Sensors

```java
// Battery
int battery = drone.getBattery();  // 0-100%

// Height/Distance
int height = drone.getHeight();              // cm, range sensor
double elevation = drone.getCorrectedElevation();  // m, pressure sensor
int frontDist = drone.getFrontRange();       // cm, front sensor
int bottomDist = drone.getBottomRange();     // cm, bottom sensor

// Motion (IMU)
double angleX = drone.getAngleX();    // degrees
double angleY = drone.getAngleY();
double angleZ = drone.getAngleZ();
double accelX = drone.getAccelX();    // m/s²
double accelY = drone.getAccelY();
double accelZ = drone.getAccelZ();

// Environment
double temp = drone.getBarometerTemperature();    // °C
double tempF = drone.getBarometerTemperature("F"); // °F
double pressure = drone.getPressure();             // Pa
```

### LEDs and Visual Feedback

```java
// Drone LEDs (the colored lights on the drone)
drone.setDroneLEDRed(brightness);      // brightness: 0-255
drone.setDroneLEDGreen(brightness);
drone.setDroneLEDBlue(brightness);
drone.droneLEDOff();                   // Turn off all LEDs

// Controller LEDs (lights on the remote)
drone.setControllerLED(r, g, b, brightness);
drone.controllerLEDOff();
```

### Sound

```java
// Play a tone on the drone's buzzer
drone.drone_buzzer(frequency, duration);  // frequency in Hz, duration in ms

// Play predefined sound sequences
drone.droneBuzzerSequence("success");     // Available: success, fail, warning
```

**Full API Documentation:** [CoDrone EDU Python API](https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation) (Java methods use camelCase instead of snake_case)

---

## Common Problems & Debugging

### Drone Won't Connect

**Problem:** `pair()` fails or times out

**Solutions:**
1. Check USB connection between controller and computer
2. Make sure controller and drone are both powered on
3. Try turning controller off and on again
4. Check that controller's LEDs show it's paired with the drone
5. Make sure no other program is using the serial port

**Code to debug connection:**
```java
System.out.println("Attempting to connect...");
try {
    drone.pair();
    System.out.println("Connected successfully!");
} catch (Exception e) {
    System.out.println("Connection failed:");
    System.out.println(e.getMessage());
    // Try to help identify the problem
    System.out.println("Check:");
    System.out.println("- USB cable connected?");
    System.out.println("- Controller powered on?");
    System.out.println("- Drone powered on?");
}
```

### Drone Drifts During Flight

**Problem:** Drone doesn't stay in place or moves in wrong direction

**Solution:** Calibrate the trim settings

```java
// Get current trim values
int[] trim = drone.getTrim();
System.out.println("Current trim: " + Arrays.toString(trim));

// If drone drifts, adjust trim
// Positive values: adjust if drifting forward/right
// Negative values: adjust if drifting backward/left
drone.setTrim(0, 0, 0, 0);  // Reset to zero first
```

You can also use the controller buttons to adjust trim during flight.

### Height Sensor Returns 999

**Problem:** `getHeight()` returns 999 instead of actual height

**Explanation:** The range sensor maxes out at 150cm. When you fly higher, it can't detect the ground.

**Solution:** Use pressure-based elevation for high flights

```java
int height = drone.getHeight();

if (height >= 999) {
    // Too high for range sensor, use pressure instead
    double elevation = drone.getCorrectedElevation();
    System.out.println("High altitude: " + elevation + " m");
} else {
    System.out.println("Height: " + height + " cm");
}
```

### Battery Drains Quickly

**Problem:** Drone battery doesn't last long

**Solutions:**
1. Fully charge battery before each session
2. Avoid aggressive maneuvers (high speeds, quick direction changes)
3. Land when battery reaches 20-30%
4. Give battery time to cool between flights
5. Replace battery if it's old (batteries wear out over time)

**Code to monitor battery:**
```java
public static void safeFlight(Drone drone) {
    drone.takeoff();
    
    while (true) {
        int battery = drone.getBattery();
        System.out.println("Battery: " + battery + "%");
        
        if (battery < 30) {
            System.out.println("Low battery - landing");
            break;
        }
        
        // Your flight code here
        drone.hover(1);
    }
    
    drone.land();
}
```

### Program Crashes Mid-Flight

**Problem:** Program stops unexpectedly, drone keeps flying

**Solution:** Always use proper error handling and cleanup

```java
public static void main(String[] args) {
    Drone drone = new Drone();
    
    try {
        drone.pair();
        drone.takeoff();
        
        // Your flight code here
        
        drone.land();
    } catch (Exception e) {
        System.out.println("Error occurred: " + e.getMessage());
        try {
            drone.emergencyStop();  // Stop motors immediately
        } catch (Exception stopError) {
            // Even stopping failed - manually press controller button
        }
    } finally {
        // Always disconnect, even if something went wrong
        drone.close();
    }
}
```

**Best Practice:** Use try-with-resources (Java 7+):

```java
try (Drone drone = new Drone(true)) {  // Auto-connects
    drone.takeoff();
    // Your code here
    drone.land();
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}
// Drone automatically disconnects, even if error occurs
```

### Movement Is Jerky or Imprecise

**Problem:** Drone movements aren't smooth

**Solutions:**
1. Reduce speed parameter (use 0.3-0.7 instead of 1.0)
2. Add hover pauses between movements
3. Use smaller distance increments
4. Make sure batteries are charged (low battery causes jerky flight)

```java
// Instead of this (jerky):
drone.moveForward(200, "cm", 1.0);

// Try this (smooth):
for (int i = 0; i < 4; i++) {
    drone.moveForward(50, "cm", 0.5);
    drone.hover(0.5);  // Brief pause
}
```

---

## Next Steps & Advanced Topics

### Learning Path

Now that you've mastered the basics, here's what to explore next:

**Level 1: Beginner** (You are here!)
- ✓ Basic connection and flight
- ✓ Movement commands
- ✓ Reading sensors
- ✓ Simple loops and conditionals

**Level 2: Intermediate**
- Combine sensors with flight decisions
- Create functions for complex behaviors
- Build autonomous flight patterns
- Use arrays to store flight plans

**Level 3: Advanced**
- Multi-drone coordination
- Computer vision integration
- Custom autonomous behaviors
- Advanced sensor fusion

**Learn More:** [Advanced CoDrone EDU Lessons](https://learn.robolink.com/codrone-edu)

### Project Ideas

Try building these projects to practice your skills:

**Beginner Projects:**
1. **Obstacle Avoider**: Fly forward until front sensor detects wall, then turn and continue
2. **Battery Monitor**: Display battery level with different colored LEDs
3. **Pattern Flyer**: Create your own custom shape or pattern
4. **Dance Routine**: Choreograph a sequence of movements with music

**Intermediate Projects:**
5. **Room Mapper**: Fly in a grid pattern and record front sensor distances
6. **Altitude Challenge**: Fly as high as possible while monitoring sensors
7. **Follow the Light**: Use color sensor to follow a colored object
8. **Stabilization System**: Use IMU data to keep drone level

**Advanced Projects:**
9. **Autonomous Navigation**: Fly through a course without human input
10. **Multi-Drone Swarm**: Coordinate multiple drones to fly in formation
11. **Search and Rescue**: Find a target object using sensors
12. **Delivery System**: Pick up and transport small objects

### Connecting to Other Learning

**AP Computer Science A Topics:**
- **Objects and Classes**: The `Drone` class is your main object
- **Methods and Parameters**: Every flight command uses methods with parameters
- **Control Structures**: Loops and conditionals control flight paths
- **Variables and Data Types**: Store sensor readings and flight parameters
- **Arrays**: Store sequences of movements or sensor data
- **Error Handling**: Try-catch blocks for robust programs

**Physics and Math:**
- **Forces and Motion**: Understanding acceleration and velocity
- **Angles and Geometry**: Planning precise movements
- **Pressure and Temperature**: Working with environmental sensors
- **Calibration**: Dealing with sensor offsets and errors

**Real-World Applications:**
- **Robotics**: Autonomous systems and control theory
- **Aerospace**: Flight dynamics and navigation
- **IoT (Internet of Things)**: Sensor data and communication
- **Problem Solving**: Debugging and optimization

### Getting Help

**Resources:**
- [Official CoDrone EDU Documentation](https://docs.robolink.com/docs/CoDroneEDU/Python/getting-started)
- [Robolink Learn Platform](https://learn.robolink.com/)
- [GitHub Repository](https://github.com/scerruti/JCoDroneEdu) (Issues and discussions)
- Ask your teacher or classmates!

**Troubleshooting:**
- Check the [Common Problems](#common-problems--debugging) section above
- Look at example programs in `src/main/java/com/otabi/jcodroneedu/examples/`
- Read error messages carefully - they often tell you exactly what's wrong
- Test small pieces of code before combining them

### Safety Reminders

Always remember:
- ✓ Fly in open areas away from people and obstacles
- ✓ Keep drone below 2 meters indoors
- ✓ Monitor battery level constantly
- ✓ Land if anything seems wrong
- ✓ Know how to emergency stop (any controller button)
- ✓ Never fly near windows or fragile objects
- ✓ Give batteries time to cool between flights

---

## Summary

Congratulations! You've learned how to:
- Set up and connect your CoDrone EDU
- Write programs that make your drone fly
- Read sensors to understand your drone's environment
- Create complex flight patterns with loops and functions
- Debug common problems
- Build projects that combine programming and robotics

**The most important thing:** Have fun and keep experimenting! Every programmer makes mistakes and every drone crashes occasionally. The key is to learn from each experience and keep improving.

**Your Next Step:** Pick one of the project ideas above and start building. Start simple, test often, and gradually make it more complex. Happy flying!

---

**Additional Resources:**
- Full API documentation in this repository's Javadoc
- Python API Reference (Java uses similar methods): [docs.robolink.com](https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation)
- Interactive lessons: [learn.robolink.com](https://learn.robolink.com/)
- Example code: [GitHub examples folder](https://github.com/scerruti/JCoDroneEdu/tree/main/src/main/java/com/otabi/jcodroneedu/examples)

---

*This guide was created for students learning Java programming with CoDrone EDU. For questions, corrections, or suggestions, please open an issue on the GitHub repository.*

**Document Info:**
- **Version**: 1.0
- **Target Audience**: Students grades 9-12, AP Computer Science A
- **Last Updated**: November 2025
- **Word Count**: ~4,000 words
