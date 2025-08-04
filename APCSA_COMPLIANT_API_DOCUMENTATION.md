# CoDrone EDU Java API: AP CSA Compliant Features

## Overview

This document highlights the features in the CoDrone EDU Java API that are fully compliant with the AP Computer Science A (AP CSA) Java Quick Reference. These are the features that students will primarily use and that teachers can confidently teach as part of the AP CSA curriculum.

## AP CSA Compliant Student-Facing API

### 1. Basic Data Types and Variables
**What Students Use**: All primitive types from AP CSA Quick Reference
```java
// From Drone class methods
int power = 50;           // int
double distance = 30.5;   // double  
boolean isConnected;      // boolean
String direction = "forward"; // String
```

### 2. Classes and Objects
**What Students Use**: Standard class instantiation and method calls
```java
// Basic object creation and method calls
Drone drone = new Drone();
drone.pair();
drone.takeoff();
drone.go("forward", 50, 2);
drone.land();
drone.close();
```

### 3. Methods and Parameters
**What Students Use**: Method calls with various parameter types
```java
// Primary movement API - all AP CSA compliant
public void go(String direction, int power, int duration)
public void moveForward(double distance, String units, double power)
public void turn(int power, Double duration)
public void hover(int durationInSeconds)
```

### 4. Conditional Statements
**What Students Use**: Standard if/else logic
```java
// Example student code
if (drone.getFrontRange() < 30) {
    drone.emergencyStop();
} else {
    drone.go("forward", 50, 1);
}
```

### 5. Loops
**What Students Use**: Standard for and while loops
```java
// Example student code patterns
for (int i = 0; i < 4; i++) {
    drone.go("forward", 50, 1);
    drone.turn(90);
}

while (drone.getFrontRange() > 50) {
    drone.go("forward", 30, 1);
}
```

### 6. Arrays (Basic Usage)
**What Students Use**: Simple array access in sensor methods
```java
// Sensor data returns - AP CSA compliant
int[] gyroscope = drone.get_gyro();
double roll = gyroscope[0];
double pitch = gyroscope[1];
double yaw = gyroscope[2];
```

### 7. Exception Handling
**What Students Use**: Basic try-catch with standard exceptions
```java
try {
    Drone drone = new Drone();
    drone.pair();
    // flight code
} catch (DroneNotFoundException e) {
    System.out.println("Could not connect to drone: " + e.getMessage());
}
```

### 8. String Methods
**What Students Use**: Standard String methods from AP CSA Quick Reference
```java
// Direction parameter processing (internal, but visible pattern)
direction.toLowerCase();
direction.equals("forward");
direction.substring(0, 4);
```

### 9. Math Class
**What Students Use**: Standard Math methods
```java
// In movement calculations and student algorithms
Math.abs(distance);
Math.sqrt(x*x + y*y);
Math.max(duration, 1);
```

### 10. Basic Inheritance (with BasicPatternDrone)
**What Students Use**: Extending classes and method overriding
```java
// Educational inheritance example
public class BasicPatternDrone extends Drone {
    // Inherits all Drone methods
    // Adds pattern-specific methods
    
    public void square(int speed, int seconds, int direction) {
        // Implementation using inherited methods
        sendControlWhile(0, speed, 0, 0, seconds * 1000);
        // ...
    }
}
```

## Educational Method Categories

### Core Connection Methods (L0101)
```java
// All AP CSA compliant
public void pair()
public void connect() throws DroneNotFoundException  
public void close()
public void disconnect()
```

### Basic Flight Control (L0101-L0102)
```java
// All AP CSA compliant
public void takeoff()
public void land()
public void emergencyStop()
public void hover(int durationInSeconds)
```

### Movement Methods (L0102-L0103)
```java
// Primary educational API - AP CSA compliant
public void go(String direction, int power, int duration)

// Distance-based movement - AP CSA compliant
public void moveForward(double distance, String units, double power)
public void moveBackward(double distance, String units, double power)
public void moveLeft(double distance, String units, double power)
public void moveRight(double distance, String units, double power)

// Turning methods - AP CSA compliant
public void turn(int power, Double duration)
public void turnLeft(int degrees)
public void turnRight(int degrees)
public void turnDegree(int degrees)
```

### Sensor Methods (L0104+)
```java
// All return basic data types - AP CSA compliant
public int getFrontRange()
public int getBackRange()
public int getTopRange()
public int getBottomRange()
public int getLeftRange()
public int getRightRange()

public int[] get_gyro()          // Returns int array
public int[] get_accel()         // Returns int array
public int[] get_angle()         // Returns int array
public String get_move_values()  // Returns String
```

### Pattern Methods (BasicPatternDrone - Inheritance Focus)
```java
// All AP CSA compliant - demonstrates inheritance
public void square(int speed, int seconds, int direction)
public void triangle(int speed, int seconds, int direction)  
public void sway(int speed, int seconds, int direction)
public void stairs(int stepHeight, int numberOfSteps, int speed)
```

## AP CSA Curriculum Alignment

### Unit 1: Primitive Types
- ✅ `int`, `double`, `boolean` used throughout API
- ✅ String operations in direction parameters
- ✅ Mathematical operations in movement calculations

### Unit 2: Using Objects  
- ✅ Creating Drone objects
- ✅ Calling methods on objects
- ✅ Method parameters and return values

### Unit 3: Boolean Expressions and if Statements
- ✅ Conditional logic in student flight programs
- ✅ Sensor-based decision making

### Unit 4: Iteration
- ✅ For loops for repeated movements
- ✅ While loops for sensor-based navigation

### Unit 5: Writing Classes
- ✅ Understanding method signatures
- ✅ Public vs private concepts (observed in API design)
- ✅ Method overloading examples

### Unit 6: Array
- ✅ Sensor data arrays
- ✅ Array indexing for coordinate access

### Unit 7: ArrayList
- ⚠️ Not used in student-facing API
- ✅ Alternative: Students can create ArrayList of commands

### Unit 8: 2D Array  
- ⚠️ Not directly used in core API
- ✅ Potential extension: Grid-based navigation

### Unit 9: Inheritance
- ✅ BasicPatternDrone extends Drone
- ✅ Method inheritance and overriding concepts
- ✅ "is-a" relationship demonstration

### Unit 10: Recursion
- ⚠️ Not directly used in core API
- ✅ Potential extension: Recursive flight patterns

## Teaching Strategies

### Progressive Complexity
1. **L0101**: Basic object creation and method calls
2. **L0102**: Parameter passing and return values  
3. **L0103**: Conditional statements with sensors
4. **L0104+**: Arrays and more complex logic
5. **Advanced**: Inheritance with pattern classes

### AP CSA Exam Preparation
- **Object-Oriented Design**: Drone class as real-world example
- **Method Design**: Clear parameter patterns and return types
- **Inheritance**: BasicPatternDrone demonstrates "is-a" relationships
- **Exception Handling**: DroneNotFoundException shows proper error handling

### Best Practices Demonstrated
1. **Clear Method Names**: `takeoff()`, `land()`, `emergencyStop()`
2. **Consistent Parameters**: Power (int 0-100), Duration (int seconds)
3. **Defensive Programming**: Parameter validation and error handling
4. **Resource Management**: `close()` method and try-with-resources support

## Conclusion

The CoDrone EDU Java API is carefully designed to provide rich educational experiences while maintaining strict AP CSA compliance in the student-facing interface. Students can confidently use the API with only AP CSA knowledge, while the implementation demonstrates professional development practices they'll encounter in advanced courses.

**Key Benefits**:
- ✅ Full AP CSA compliance in educational interface
- ✅ Real-world programming experience
- ✅ Smooth transition from AP CSA to professional development
- ✅ Inheritance concepts clearly demonstrated
- ✅ Preparation for AP CSA exam question patterns
