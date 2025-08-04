# CoDrone EDU Java API: Teacher Quick Reference Guide

## Quick Feature Classification

### ✅ AP CSA Safe - Teach Freely
These features align perfectly with AP CSA curriculum and can be taught without concern:

**Core API Methods**:
- `new Drone()` - Object instantiation
- `.pair()`, `.takeoff()`, `.land()`, `.close()` - Basic method calls
- `.go(String, int, int)` - Method with multiple parameters
- `.hover(int)` - Method with single parameter
- `.emergencyStop()` - Method with no parameters

**Data Types**:
- `int power = 50` - Primitive types
- `String direction = "forward"` - String usage
- `double distance = 30.5` - Double precision
- `boolean isFlying` - Boolean logic

**Control Structures**:
```java
// Conditionals
if (drone.getFrontRange() < 30) {
    drone.emergencyStop();
}

// Loops
for (int i = 0; i < 4; i++) {
    drone.go("forward", 50, 1);
}
```

**Arrays**:
```java
int[] gyro = drone.get_gyro();
int roll = gyro[0];  // Array indexing
```

**Exception Handling**:
```java
try {
    drone.pair();
} catch (DroneNotFoundException e) {
    System.out.println("Connection failed");
}
```

### ⚠️ Advanced - Introduce Carefully
These features are beyond AP CSA but can be valuable for advanced students:

**Inheritance** (Post-AP CSA Topic):
```java
BasicPatternDrone drone = new BasicPatternDrone();  // Inheritance
drone.square(50, 2, 1);  // Specialized methods
```

**Try-with-resources** (Java 7+ feature):
```java
try (Drone drone = new Drone()) {  // AutoCloseable
    drone.pair();
    // Automatic resource cleanup
}
```

### ❌ Implementation Only - Don't Teach
These features are used internally but are beyond high school scope:

- Lambda expressions (`msg -> handler.accept(msg)`)
- Method references (`Ping::new`)
- Streams API (`.filter()`, `.map()`)
- Concurrent programming (`CompletableFuture`)
- NIO (`ByteBuffer`)
- Generic wildcards (`Class<? extends Serializable>`)
- External libraries (Guava, Log4j)

## Lesson Plan Integration

### Lesson L0101: First Flight
**AP CSA Concepts**: Objects, method calls, sequence
```java
Drone drone = new Drone();     // Object creation
drone.pair();                  // Method call (void)
drone.takeoff();              // Method call (void)
drone.go("forward", 50, 2);   // Method call (parameters)
drone.land();                 // Method call (void)
drone.close();                // Method call (void)
```

### Lesson L0102: Variables and Movement
**AP CSA Concepts**: Variables, data types, method parameters
```java
int power = 30;              // int variable
int duration = 2;            // int variable
String direction = "right";   // String variable
drone.go(direction, power, duration);  // Variable usage
```

### Lesson L0103: Conditionals and Sensors
**AP CSA Concepts**: if statements, boolean expressions, method return values
```java
int frontDistance = drone.getFrontRange();  // Method return value
if (frontDistance < 50) {                   // Boolean expression
    drone.turn(90);                         // Conditional execution
} else {
    drone.go("forward", 50, 1);
}
```

### Lesson L0104: Loops and Patterns
**AP CSA Concepts**: for loops, while loops, iteration
```java
// For loop
for (int side = 0; side < 4; side++) {
    drone.go("forward", 50, 2);
    drone.turn(90);
}

// While loop
while (drone.getFrontRange() > 30) {
    drone.go("forward", 25, 1);
}
```

### Lesson L0105: Arrays and Sensor Data
**AP CSA Concepts**: Arrays, array indexing
```java
int[] gyroscope = drone.get_gyro();  // Array return value
int roll = gyroscope[0];             // Array indexing
int pitch = gyroscope[1];
int yaw = gyroscope[2];
```

### Advanced Lesson: Inheritance (Post-AP CSA)
**Concepts**: Inheritance, method overriding, polymorphism
```java
BasicPatternDrone drone = new BasicPatternDrone();  // Inheritance
drone.pair();              // Inherited method
drone.takeoff();           // Inherited method
drone.square(50, 2, 1);    // Specialized method
```

## Common Student Questions & Answers

### "Why do we need to call .close()?"
**AP CSA Answer**: Resource management - good programming practice to clean up when done.
**Advanced Answer**: Implements AutoCloseable interface for automatic resource management.

### "What's inside the go() method?"
**AP CSA Answer**: Complex code that sends commands to the drone - focus on using the method correctly.
**Advanced Answer**: Protocol handling, byte buffers, and concurrent programming.

### "Can we make our own drone methods?"
**AP CSA Answer**: You can create helper methods that use existing drone methods.
**Advanced Answer**: You can extend the Drone class (inheritance) to add specialized behaviors.

### "Why does getFrontRange() return an int?"
**AP CSA Answer**: Distance measurements are whole numbers (centimeters), so int is appropriate.
**Technical Answer**: Protocol specification uses integer values for sensor data.

## Debugging Student Code

### Common AP CSA Errors

**Forgetting to create object**:
```java
// Wrong
drone.takeoff();  // NullPointerException

// Right  
Drone drone = new Drone();
drone.takeoff();
```

**Wrong parameter types**:
```java
// Wrong
drone.go("forward", "50", 2);  // String instead of int

// Right
drone.go("forward", 50, 2);    // int power parameter
```

**Array index errors**:
```java
// Wrong
int[] gyro = drone.get_gyro();
int value = gyro[3];  // ArrayIndexOutOfBoundsException

// Right
int roll = gyro[0];   // Valid index
```

### Safe Error Messages
When students encounter implementation-level errors, guide them back to AP CSA concepts:

- "Focus on the method call, not the implementation"
- "Check your parameter types and values" 
- "Make sure you created the drone object first"
- "Remember to handle the DroneNotFoundException"

## Assessment Guidelines

### AP CSA Appropriate Questions
- Trace through drone movement sequences
- Debug method call syntax
- Predict sensor-based conditional outcomes
- Design loop-based flight patterns
- Analyze parameter passing and return values

### Avoid These Question Types
- Internal implementation details
- Advanced Java features (lambdas, streams)
- Concurrent programming concepts
- Protocol or communication details
- Generic type parameters

## Summary

The CoDrone EDU API is designed to support AP CSA learning while hiding implementation complexity. Teachers can confidently use the core API methods as real-world examples of AP CSA concepts without worrying about exposing students to advanced features beyond the curriculum scope.

**Teaching Strategy**: Start with the basics, build confidence with AP CSA concepts, then optionally introduce advanced features as enrichment for interested students.
