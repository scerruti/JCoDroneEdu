---
layout: guide
title: Design Guide
category: Architecture
permalink: /architecture/design-guide/index.html
---

## Architecture & Design Guide

This section documents the design principles, architecture patterns, and implementation strategies used in JCoDroneEdu.

---

## Navigation

### Design Principles
- [API Design]({{ '/architecture/design-guide/api-design.html' | relative_url }}) - How the API is structured
- [Adding New Methods]({{ '/architecture/design-guide/adding-methods.html' | relative_url }}) - Extending flight commands
- [Adding Sensors]({{ '/architecture/design-guide/adding-sensors.html' | relative_url }}) - Integrating new sensors
- [Testing Strategy]({{ '/architecture/design-guide/testing.html' | relative_url }}) - Unit and integration testing
- [Contributing]({{ '/architecture/design-guide/contributing.html' | relative_url }}) - How to contribute to the project

---

## Quick Overview

### Project Goals

**Educational Focus:**
- Make drone programming accessible to students
- Build Java programming fundamentals through concrete applications
- Maintain focus on concepts over infrastructure

**Technical Goals:**
- Simple, intuitive API
- Reliable hardware abstraction
- Extensible architecture for new sensors and commands

### Architecture Layers

```
┌─────────────────────────────────────────────┐
│         User Application Code               │
│     (Student drone programs)                │
├─────────────────────────────────────────────┤
│     JCoDroneEdu Public API                  │
│  (Drone, Flight Commands, Sensors)          │
├─────────────────────────────────────────────┤
│     Core Systems                            │
│  (Connection, Commands, Sensors)            │
├─────────────────────────────────────────────┤
│     Hardware Abstraction                    │
│  (USB Protocol, Device Drivers)             │
├─────────────────────────────────────────────┤
│         Physical Hardware                   │
│     (Drone, Sensors, Motors)                │
└─────────────────────────────────────────────┘
```

### Key Design Patterns

**1. Try-With-Resources**
```java
try (Drone drone = new Drone(true)) {
    // drone automatically closes/disconnects
}
```
Ensures drone disconnects even if exception occurs.

**2. Builder Pattern** (where applicable)
Simplifies complex object creation.

**3. Service Layer Pattern**
Separates concerns:
- ConnectionService: USB communication
- CommandService: Flight commands
- SensorService: Data collection

**4. Observer Pattern** (optional features)
Allows listeners to react to drone events.

---

## Core Concepts

### Drone Connection Model

**Initialization:**
```
1. Drone drone = new Drone(true)
   ↓
2. Establish USB connection
   ↓
3. Discover drone capabilities
   ↓
4. Initialize sensors
   ↓
5. Ready for commands
```

**Disconnection:**
```
1. drone.disconnect() or try-with-resources ends
   ↓
2. Land drone if flying
   ↓
3. Close USB connection
   ↓
4. Release resources
```

### Command Execution Model

```
User Code:
drone.moveForward(0.5)
    ↓
Validation:
- Check if connected
- Check if parameters valid
    ↓
Command Creation:
- Build protocol message
- Add parameters
    ↓
Send via USB:
- Transmit to controller
- Wait for echo/ACK
    ↓
Update State:
- Log command
- Update internal state
    ↓
Return to User
```

### Sensor Reading Model

```
Sensor Request:
int battery = drone.getBattery()
    ↓
Query:
- Send sensor request
- Wait for response
    ↓
Receive Data:
- Parse response
- Validate data
    ↓
Return to User
```

---

## API Design Principles

### 1. Simplicity
**Goal:** Students should understand what each method does without documentation lookup.

```java
// Good - clear intent
drone.moveForward(speed);
drone.turnLeft(speed);
drone.setBattery(value);

// Avoid - unclear
drone.move(1, 0, 0, 0);  // What do these numbers mean?
drone.setData(0x88, [...]);  // Too low-level
```

### 2. Safety
**Goal:** Hard to write code that crashes the drone.

```java
// Built-in safety
try (Drone drone = new Drone(true)) {
    // Auto-lands and disconnects
}

// Parameter validation
if (speed < 0 || speed > 1) {
    throw new IllegalArgumentException("Speed must be 0-1");
}
```

### 3. Consistency
**Goal:** Methods follow predictable patterns.

```java
// Consistent naming
takeoff()          // verb
land()
moveForward()      // verb + direction
turnLeft()

// Consistent return types
int battery = drone.getBattery();        // Simple value
Accelerometer acc = drone.getAccelerometer();  // Objects
void moveForward(double speed);          // No return for side-effects
```

### 4. Discoverability
**Goal:** Students can find methods they need.

```java
// IDE autocomplete helps
drone.get...      // Lists all getters
drone.set...      // Lists all setters
drone.move...     // Lists all movement commands

// Related methods grouped
// Flight commands
takeoff(), land(), moveForward(), moveBackward(), ...

// Sensor queries
getBattery(), getHeight(), getTemperature(), ...
```

---

## Error Handling Strategy

### Exception Hierarchy

```
Exception
├── IOException
│   └── DroneConnectionException (connection lost)
├── IllegalArgumentException
│   └── InvalidCommandException (bad parameters)
└── TimeoutException
    └── SensorTimeoutException (sensor read timeout)
```

### Recovery Strategies

**Connection Lost:**
```java
try {
    drone.moveForward(0.5);
} catch (DroneConnectionException e) {
    System.out.println("Connection lost!");
    // Try to reconnect
    drone = new Drone(true);
}
```

**Invalid Parameter:**
```java
try {
    drone.moveForward(1.5);  // Out of range
} catch (IllegalArgumentException e) {
    System.out.println("Speed must be 0-1");
    drone.moveForward(1.0);  // Use valid value
}
```

---

## Performance Considerations

### Latency

**Command to Flight:**
- ~10-50ms typical
- Depends on USB bandwidth and drone load

**Sensor Read:**
- ~5-20ms per sensor
- Multiple sensors: batch requests

**Optimization:**
```java
// BAD: Multiple round-trips
int battery = drone.getBattery();
int height = drone.getHeight();
int range = drone.getRange();

// GOOD: Batch if possible
SensorReading reading = drone.getAllSensors();
int battery = reading.getBattery();
int height = reading.getHeight();
int range = reading.getRange();
```

### Memory

**Per-drone:**
- ~1-2 MB for data structures
- Multiple drones: linear scaling

**For large classes:**
- Consider object pooling
- Share read-only data

---

## Thread Safety

### Current Model

**Single-threaded by design:**
```java
// Recommended: one thread per drone
try (Drone drone = new Drone(true)) {
    drone.takeoff();
    // ...
}
```

**Multiple Drones:**
```java
// Each drone owns its thread
try (Drone drone1 = new Drone(true);
     Drone drone2 = new Drone(true)) {
    
    // Each operates independently
    drone1.moveForward(0.5);
    drone2.moveBackward(0.5);
}
```

### Future: Concurrent Operations

If implementing concurrent operations:
- Use synchronized blocks
- Document thread-safety guarantees
- Provide concurrent examples

---

## Configuration & Extensibility

### Adding Configuration Options

**For new features:**
```java
// Drone configuration
Drone drone = new Drone(true);
drone.setDebugMode(true);      // Enable logging
drone.setMaxSpeed(0.8);        // Limit speed
drone.setTimeout(5000);        // Command timeout
```

**For instructor override:**
```java
// Load from properties file
Properties config = new Properties();
config.load(new FileInputStream("drone.properties"));
drone.setConfiguration(config);
```

---

## Related Documentation

- [Adding Methods]({{ '/architecture/design-guide/adding-methods.html' | relative_url }}) - How to extend flight API
- [Adding Sensors]({{ '/architecture/design-guide/adding-sensors.html' | relative_url }}) - How to integrate new sensors
- [Testing Strategy]({{ '/architecture/design-guide/testing.html' | relative_url }}) - Approaches to testing
- [Full Javadoc]({{ '/javadoc/index.html' | relative_url }}) - Complete API reference

---

Next: [Adding New Methods]({{ '/architecture/design-guide/adding-methods.html' | relative_url }})
