# JCoDroneEdu Unit Test Framework

## Architecture Overview

The test framework uses a three-tier inheritance hierarchy designed to support **all** drone assignments:

```
DroneTest (Base - ALL drone assignments)
├── Connection & Resource Management Tests
├── Exception Handling Tests  
└── MockDrone with all drone methods

DroneFlightTest (Flight-specific assignments)
├── Takeoff/Landing Tests
├── Safety Tests (unsafe commands, values)
├── Movement Tests (move commands, sequences)
└── Inherits all connection tests

L0102FlightMovementsTest (Specific flight patterns)
├── Square-specific pattern tests
├── Pitch/roll usage validation
├── Reset value best practices
└── Inherits all flight + connection tests
```

### **DroneTest** (Base Class for ALL Assignments)
- **Connection Validation**: Supports all three patterns from L0101 (`pair()`, `connect()`, try-with-resources)
- **Resource Management**: Ensures proper cleanup (`close()`, `disconnect()`, or automatic)
- **Exception Handling**: Validates `DroneNotFoundException` handling
- **MockDrone**: Complete drone simulation for testing without hardware
- **Used for**: Sensor reading, LED control, status checks, AND flight assignments

### **DroneFlightTest** (Flight Operations Only)
- Inherits all connection tests from `DroneTest`
- Adds flight-specific tests:
  - ✅ Takeoff and landing verification
  - ⚠️ Safety checks (unsafe commands, reasonable values)  
  - 🚀 Movement command validation
- **Used for**: Any assignment involving takeoff, movement, or landing

### **L0102FlightMovementsTest** (Square Pattern)
- Inherits all flight tests from `DroneFlightTest`
- Adds specific tests for:
  - Square pattern requirements (4 directions)
  - Pitch/roll usage verification
  - Reset value best practices

## Benefits of This Architecture

### ✅ **For Instructors**
- **Easy to add new patterns**: Just extend `DroneFlightTest` and implement `executeStudentFlightPattern()`
- **Consistent safety checks**: All flight tests automatically get safety validation
- **Reusable components**: Common test logic is shared across all flight patterns

### ✅ **For Students**
- **Consistent feedback**: Same safety and basic flight checks across all lessons
- **Progressive complexity**: Basic tests first, then pattern-specific tests
- **Clear error categories**: Basic Flight → Safety → Pattern-specific

## Adding New Flight Pattern Tests

To add a new flight pattern test (e.g., triangle, circle, figure-8):

```java
class NewPatternTest extends DroneFlightTest {
    
    @Nested
    @DisplayName("New Pattern Specific Tests")
    class NewPatternTests {
        @Test
        void shouldDoPatternSpecificThing() {
            executeStudentFlightPattern();
            // Pattern-specific assertions
        }
    }
    
    @Override
    protected void executeStudentFlightPattern() {
        // Implement the expected pattern
        mockDrone.takeoff();
        // ... pattern logic ...
        mockDrone.land();
    }
}
```

**That's it!** The new test automatically gets:
- ✅ Takeoff/landing validation
- ⚠️ Safety checks  
- 🚀 Movement command validation
- 🔄 Exception handling
- Plus your pattern-specific tests

## L0102 Flight Movements Test

This test file helps students verify their square flight pattern code **before** flying with a real drone.

### What the Tests Check

#### ✅ **Square Flight Pattern Tests**
- **Takeoff and Landing**: Ensures your code calls `takeoff()` and `land()` in the correct order
- **Pitch and Roll Usage**: Verifies you're using `setPitch()` and `setRoll()` for directional movement
- **Reset Values**: Checks that you reset pitch/roll to 0 between movements for clean flight patterns
- **Move Commands**: Ensures you use `move()` to execute movements after setting direction
- **Logical Sequence**: Verifies that direction commands are followed by movement commands

#### ⚠️ **Safety Tests**
- **Unsafe Commands**: Warns against using potentially dangerous commands like `setThrottle()`
- **Reasonable Values**: Checks that pitch/roll values are within safe ranges (-100 to 100)
- **Exception Handling**: Ensures proper try-catch blocks for `DroneNotFoundException`

#### 🎯 **Code Quality Tests**
- **Flight Concepts**: Verifies understanding of pitch (forward/backward) vs roll (left/right)
- **Complete Square**: Ensures all 4 directions are used in the square pattern

### How to Run the Tests

```bash
# Run just the L0102FlightMovements tests
./gradlew test --tests L0102FlightMovementsTest

# Run all tests
./gradlew test

# Run with detailed output
./gradlew test --tests L0102FlightMovementsTest --info
```

### Understanding Test Results

#### ✅ **Green (Passing) Tests**
Your code follows good practices and should work safely with a real drone.

#### ❌ **Red (Failing) Tests with Error Messages**
- **Missing takeoff() command**: Add `drone.takeoff()` at the beginning
- **Missing land() command**: Add `drone.land()` at the end
- **Must use setPitch() to move forward/backward**: Use `drone.setPitch(30)` for forward, `drone.setPitch(-30)` for backward
- **Must use setRoll() to move left/right**: Use `drone.setRoll(30)` for right, `drone.setRoll(-30)` for left
- **Must use move() command**: Add `drone.move(1)` after setting direction

#### ⚠️ **Warnings**
- **Consider resetting values**: Set pitch/roll to 0 between movements
- **Avoid setThrottle()**: Use pitch/roll for directional movement instead
- **Values might be too extreme**: Keep pitch/roll values between -50 and 50

### Example of Good Code Pattern

```java
try (Drone drone = new Drone(true)) {
    drone.takeoff();

    // Forward
    drone.setPitch(30);
    drone.move(1);
    
    // Reset and move right
    drone.setPitch(0);
    drone.setRoll(30);
    drone.move(1);
    
    // Reset and move backward
    drone.setRoll(0);
    drone.setPitch(-30);
    drone.move(1);
    
    // Reset and move left
    drone.setPitch(0);
    drone.setRoll(-30);
    drone.move(1);

    drone.land();
} catch (DroneNotFoundException e) {
    throw new RuntimeException(e);
}
```

### Tips for Students

1. **Always test first**: Run these unit tests before trying with a real drone
2. **Read error messages**: They tell you exactly what's missing or wrong
3. **Start simple**: Get basic takeoff/land working, then add movements
4. **Reset between movements**: Set pitch/roll to 0 before changing direction
5. **Use reasonable values**: Start with pitch/roll values around 20-30

### For Instructors

This test framework uses a mock drone object that tracks all method calls without requiring actual hardware. It can be easily extended for other flight patterns and lessons. The tests provide educational feedback to help students understand drone flight concepts and safe programming practices.
