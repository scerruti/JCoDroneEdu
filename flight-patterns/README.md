# JCoDroneEdu Flight Patterns

Educational flight patterns library wi## 🎯 Implementation Notes

### **Python-Matching Implementation**

The flight patterns in this library **exactly match the Python implementation** using the same `sendControl()` and `sendControlWhile()` methods for direct motor control.

**Python Implementation:**
```python
def square(self, speed=60, seconds=1, direction=1):
    power = int(speed)
    duration = int(seconds * 1000)
    self.sendControlWhile(0, power, 0, 0, duration)        # Forward
    self.sendControlWhile(0, -power, 0, 0, 50)             # Brief stop
    self.sendControlWhile(power * direction, 0, 0, 0, duration)  # Right/left
    # ... continues with exact same pattern

def triangle(self, speed=60, seconds=1, direction=1):
    power = int(speed)
    duration = int(seconds * 1000)
    self.sendControlWhile(power * direction, power, 0, 0, duration)    # Diagonal
    self.sendControlWhile(-power * direction, -power, 0, 0, 50)        # Brief stop
    # ... continues with exact same pattern

def sway(self, speed=30, seconds=2, direction=1):
    power = int(speed)
    duration = int(seconds * 1000)
    for i in range(2):
        self.sendControlWhile(-power * direction, 0, 0, 0, duration)
        self.sendControlWhile(power * direction, 0, 0, 0, duration)

def spiral(self, speed=50, seconds=5, direction=1):
    power = int(speed)
    self.sendControl(0, power, 100 * -direction, -power)
    time.sleep(seconds)

def circle(self, speed=75, direction=1):
    self.sendControl(0, speed, direction * speed, 0)
    time.sleep(5)
```

**Java Implementation (Direct Match):**
```java
public void square(int speed, int seconds, int direction) {
    int power = speed;
    int duration = seconds * 1000;
    sendControlWhile(0, power, 0, 0, duration);        // Forward
    sendControlWhile(0, -power, 0, 0, 50);             // Brief stop
    sendControlWhile(power * direction, 0, 0, 0, duration);  // Right/left
    // ... continues with exact same pattern

public void triangle(int speed, int seconds, int direction) {
    int power = speed;
    int duration = seconds * 1000;
    sendControlWhile(power * direction, power, 0, 0, duration);    // Diagonal
    sendControlWhile(-power * direction, -power, 0, 0, 50);        // Brief stop
    // ... continues with exact same pattern

public void sway(int speed, int seconds, int direction) {
    int power = speed;
    int duration = seconds * 1000;
    for (int i = 0; i < 2; i++) {
        sendControlWhile(-power * direction, 0, 0, 0, duration);
        sendControlWhile(power * direction, 0, 0, 0, duration);
    }
}

public void spiral(int speed, int seconds, int direction) {
    int power = speed;
    sendControl(0, power, 100 * -direction, -power);
    Thread.sleep(seconds * 1000);
}

public void circle(int speed, int direction, double duration) {
    sendControl(0, speed, direction * speed, 0);
    Thread.sleep((long)(duration * 1000));
}
```ode** for CoDrone EDU programming.

## 🎯 Educational Purpose

This library is designed to teach students about:
- **Dependency Management**: How to include external libraries in Java projects
- **API Usage**: How to use well-designed class interfaces
- **Source Code Study**: Understanding implementation details by reading actual code
- **Pattern Composition**: Building complex behaviors from simple components

## 📦 Using as a Dependency

### Gradle (build.gradle or build.gradle.kts)

```kotlin
dependencies {
    implementation("com.otabi:jcodroneedu-flight-patterns:1.0.0")
}
```

### Maven (pom.xml)

```xml
<dependency>
    <groupId>com.otabi</groupId>
    <artifactId>jcodroneedu-flight-patterns</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🚀 Quick Start

```java
import com.otabi.jcodroneedu.patterns.BasicPatternDrone;
import com.otabi.jcodroneedu.patterns.AdvancedPatternDrone;

public class MyFlightProgram {
    public static void main(String[] args) {
        // Option 1: Use BasicPatternDrone (inherits from Drone)
        BasicPatternDrone drone = new BasicPatternDrone();
        drone.pair();
        drone.takeoff();
        
        // Python-matching method calls
        drone.square(60, 1, 1);         // speed=60%, seconds=1, direction=right
        drone.triangle(50, 2, -1);      // speed=50%, seconds=2, direction=left
        drone.sway(30, 2, 1);           // speed=30%, seconds=2, starts left
        drone.hover(2.0);               // Inherited method
        
        drone.land();
        drone.close();
        
        // Option 2: Use AdvancedPatternDrone (inherits from BasicPatternDrone)
        AdvancedPatternDrone advancedDrone = new AdvancedPatternDrone();
        advancedDrone.pair();
        advancedDrone.takeoff();
        
        // Can use basic patterns (inherited)
        advancedDrone.square(60, 1, 1);
        advancedDrone.sway(30, 2, 1);
        
        // Can use advanced patterns (new) - exact Python matches
        advancedDrone.circle(75, 1, 5.0);      // speed=75%, clockwise, 5 seconds
        advancedDrone.spiral(50, 5, 1);        // speed=50%, 5 seconds, right
        
        // Educational discrete methods (good for beginners)
        advancedDrone.pentagon(50, 60);        // 50cm sides, 60% speed
        advancedDrone.hexagon(40, 70);         // 40cm sides, 70% speed
        
        // Python-style smooth methods (advanced students)  
        advancedDrone.pentagonSmooth(60, 2, 1);  // 60% speed, 2 sec/side, clockwise
        advancedDrone.hexagonSmooth(50, 1, -1);  // 50% speed, 1 sec/side, counter-clockwise
        
        advancedDrone.land();
        advancedDrone.close();
    }
}
```

## � Important Implementation Notes

### **Circle Implementation**

The flight patterns in this library use **direct motor control** for true circles, leveraging the Java Drone class's `sendControl()` method to match Python functionality.

**Python Implementation (True Circles):**
```python
def circle(self, speed=75, direction=1):
    self.sendControl(0, speed, direction * speed, 0)  # Direct motor control
    time.sleep(5)
```

**Java Implementation (Direct Control):**
```java
// Direct motor control for true circles (similar to Python)
sendControl(0, speed, clockwise * speed, 0);
```

### **Educational Progression**

The library provides **two approaches** for geometric patterns to support different learning levels:

1. **Educational Discrete Methods** (`pentagon()`, `hexagon()`, `circleApproximation()`):
   - Use high-level `go()` commands  
   - Show students how to break complex shapes into simple movements
   - Teach geometric concepts and angle calculations
   - Good for introductory programming courses

2. **Python-Style Smooth Methods** (`pentagonSmooth()`, `hexagonSmooth()`, `circle()`, `spiral()`):
   - Use direct motor control with `sendControl()` and `sendControlWhile()`
   - Match Python implementation exactly
   - Provide smooth, continuous movement
   - Good for advanced courses and cross-language consistency

This dual approach lets instructors choose the right method for their course level and learning objectives.

### **Additional Features to Consider**

While the basic circle implementation is now available, we can expand with:
- Polygon approximation methods for comparison
- Variable-speed circles (accelerating/decelerating)
- Both direct control and approximation options for educational comparison

## �📚 Available Patterns

### BasicPatternDrone
- `square(speed, seconds, direction)` - Fly a square pattern using direct motor control
- `triangle(speed, seconds, direction)` - Fly a triangle pattern using combined roll/pitch  
- `lineBackAndForthTimed(speed, seconds, cycles)` - Fly back and forth in a line
- `sway(speed, seconds, direction)` - Move left and right twice (matches Python exactly)
- `stairs(stepHeight, numberOfSteps, speed)` - Climb/descend in steps (educational pattern)

### AdvancedPatternDrone  
- `circle(speed, direction, duration)` - Fly true circles using direct motor control (matches Python exactly)
- `spiral(speed, seconds, direction)` - Fly expanding spiral using direct motor control (matches Python exactly)
- `pentagon(sideLength, speed)` - Fly pentagon using discrete movements (educational)
- `hexagon(sideLength, speed)` - Fly hexagon using discrete movements (educational)
- `pentagonSmooth(speed, seconds, direction)` - Fly pentagon using direct motor control (Python-style)
- `hexagonSmooth(speed, seconds, direction)` - Fly hexagon using direct motor control (Python-style)
- `circleApproximation(radius, segments, speed)` - Approximate circles using polygon segments (educational comparison)
- `pentagon(sideLength, speed)` - Five-sided polygon
- `hexagon(sideLength, speed)` - Six-sided polygon

## 🧑‍🎓 Learning Exercises

### Exercise 1: Understanding Dependencies
1. Create a new Java project
2. Add this library as a dependency using Gradle or Maven
3. Notice how your IDE downloads the library automatically
4. Import and use the `BasicPatternDrone` class

### Exercise 2: Reading Source Code
1. Navigate to the source code of `BasicPatternDrone.java`
2. Study how the `square()` method works
3. Understand why it uses a for loop
4. See how parameter validation works

### Exercise 3: Creating Custom Patterns
1. Use existing patterns to create new combinations
2. Try the "flower" pattern from `ExamplePrograms.java`
3. Create your own pattern by combining squares and circles

### Exercise 4: Understanding Geometry
1. Study the `circle()` method in `AdvancedPatterns`
2. Understand how circles are approximated with polygons
3. Experiment with different segment counts
4. Calculate the relationship between radius and circumference

## 🔍 Source Code Study Guide

The source code is extensively commented to help you learn:

### Key Concepts Demonstrated:
- **Constructor Patterns**: How classes accept dependencies
- **Parameter Validation**: Checking inputs before use
- **Loop Structures**: Using for loops for repetitive movements
- **Mathematical Calculations**: Converting geometry to flight commands
- **Method Design**: Clear naming and parameter organization

### Look For:
- How error checking prevents crashes
- Why certain movements have timing delays
- How mathematical formulas translate to flight paths
- How complex patterns build on simple movements

## 🔧 Building from Source

If you want to modify or contribute:

```bash
# Clone this repository
git clone https://github.com/scerruti/JCoDroneEdu-FlightPatterns.git
cd JCoDroneEdu-FlightPatterns

# Build and test
./gradlew build

# Publish to local Maven repository for testing
./gradlew publishToMavenLocal
```

## 🤝 Educational Philosophy

This library demonstrates:
- **Progressive Complexity**: Start simple, build up gradually
- **Real-World Practices**: Industry-standard dependency management
- **Open Source Learning**: Full source access for understanding
- **Safety First**: Built-in parameter validation and safe defaults

## 📖 Next Steps

1. Master the basic patterns first
2. Study the source code to understand implementation
3. Create your own custom pattern combinations
4. Try building your own pattern library using similar techniques

## ⚠️ Safety Reminders

- Always fly in a safe, open area
- Keep drone within line of sight
- Test patterns with small parameters first
- Have a manual override ready (emergency stop)
- Follow your local drone flying regulations

---

**Happy Flying and Learning!** 🛸

*This library teaches you both drone programming AND modern Java development practices.*
