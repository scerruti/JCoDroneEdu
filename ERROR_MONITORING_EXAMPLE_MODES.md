# Error Monitoring Example - Enhanced with Flight Modes

## Quick Reference

### Gradle Commands
```bash
# Demo mode - Learn API without hardware (recommended for classroom)
./gradlew runErrorMonitoringDemo

# Connect mode - Real error monitoring, no flight (safe testing)
./gradlew runErrorMonitoringConnect

# Flight mode - Full demo with actual flight (CAUTION: requires safe area)
./gradlew runErrorMonitoringFly
```

### Direct Java Commands
```bash
# Demo mode
java -cp build/libs/JCoDroneEdu.jar com.otabi.jcodroneedu.examples.ErrorMonitoringExample

# Connect mode
java -cp build/libs/JCoDroneEdu.jar com.otabi.jcodroneedu.examples.ErrorMonitoringExample --connect

# Flight mode
java -cp build/libs/JCoDroneEdu.jar com.otabi.jcodroneedu.examples.ErrorMonitoringExample --fly

# Help
java -cp build/libs/JCoDroneEdu.jar com.otabi.jcodroneedu.examples.ErrorMonitoringExample --help
```

## Overview
The `ErrorMonitoringExample.java` has been enhanced with three operational modes to support different use cases: demo, connection monitoring, and actual flight operations.

## Motivation for Multi-Mode Design

### Original Issue
The example was purely demonstrative with all flight commands commented out, making it:
- **Not testable** with real hardware
- **Limited educational value** - students couldn't see real error states
- **Unclear** - needed manual code editing to actually fly
- **Risky** - uncommenting all commands could cause unexpected flight

### Solution: Command-Line Switches
Three distinct modes allow safe progression from learning to flying:

## Usage Modes

### 1. Demo Mode (Default)
```bash
java ErrorMonitoringExample
# OR using Gradle:
./gradlew runErrorMonitoringDemo
```

**Purpose**: Learn the API without hardware
- No connection required
- Shows all API method calls and patterns
- Demonstrates error checking logic
- Safe for classroom discussion without drones

**What It Shows**:
- Error data structure (arrays vs objects)
- Type-safe checking methods
- Pre-flight safety check patterns
- Monitoring logic and decision trees

### 2. Connect Mode
```bash
java ErrorMonitoringExample --connect
# OR using Gradle:
./gradlew runErrorMonitoringConnect
```

**Purpose**: Monitor real error data without flying
- Connects to actual hardware
- Reads real error flags from drone
- Shows actual sensor and state errors
- Demonstrates calibration monitoring
- **Safe**: No flight commands executed

**What Students Learn**:
- How to read real sensor data
- Understanding actual error conditions
- Calibration process monitoring
- Connection and data retrieval

**Use Cases**:
- Testing error detection logic
- Monitoring battery levels
- Checking sensor health
- Observing calibration timing
- Debugging connection issues

### 3. Flight Mode
```bash
java ErrorMonitoringExample --fly
# OR using Gradle:
./gradlew runErrorMonitoringFly
```

**Purpose**: Full demonstration with actual flight operations
- Connects to hardware
- Performs pre-flight safety check
- Executes takeoff
- Monitors errors during flight
- Responds to error conditions
- Performs emergency landing if needed
- **Caution**: Actual flight operations

**Safety Features**:
- Pre-flight safety validation
- Continuous error monitoring (every 1 second)
- Automatic emergency landing on:
  - Low battery detection
  - Critical error conditions
- Attitude stabilization on instability
- Controlled landing after demo

## Examples Demonstrated

### Example 1: Array-Based Error Checking (Python-compatible)
- Shows bitwise operations
- Demonstrates array indexing
- Compatible with Python approach

### Example 2: Object-Based Error Checking (Java-idiomatic)
- Type-safe method calls
- Enum set collections
- Convenience methods

### Example 3: Pre-Flight Safety Check
**Demo Mode**: Shows logic flow
**Connect/Fly Mode**: Validates actual drone state
- Battery level check
- Calibration status
- Sensor responsiveness
- Critical error detection

### Example 4: Flight Monitoring
**Demo Mode**: Simulates monitoring pattern
**Fly Mode**: Performs real flight with monitoring
- Periodic error checks
- Emergency response logic
- Sensor diagnostics
- Automatic safety responses

### Example 5: Calibration Monitoring
**Demo Mode**: Shows concept
**Connect/Fly Mode**: Performs actual gyro calibration
- Demonstrates `resetGyro()` method
- Shows enhanced flag-based monitoring
- Educational about calibration process

### Example 6: Detailed Error Report
- Complete error state display
- Raw flag values
- Error counts
- Formatted output

## Educational Progression

### Classroom Workflow

**Phase 1: Theory (No Hardware)**
```bash
java ErrorMonitoringExample
```
- Project example on screen
- Discuss API methods
- Explain error checking logic
- Review safety patterns
- No drones needed

**Phase 2: Observation (With Hardware)**
```bash
java ErrorMonitoringExample --connect
```
- Students connect their drones
- Observe real error data
- Watch calibration process
- Check battery levels
- **Safe**: Drones stay grounded

**Phase 3: Application (Flight Demo)**
```bash
java ErrorMonitoringExample --fly
```
- Teacher demonstrates with one drone
- Shows error monitoring in action
- Demonstrates emergency responses
- **Controlled**: Only authorized flights

**Phase 4: Student Practice**
- Students modify and test
- Add custom error checks
- Implement new safety rules
- Create custom monitoring patterns

## Implementation Details

### Command-Line Parsing
```java
private static boolean shouldConnect = false;
private static boolean shouldFly = false;

// Parse arguments
for (String arg : args) {
    if ("--connect".equals(arg)) shouldConnect = true;
    else if ("--fly".equals(arg)) {
        shouldConnect = true;
        shouldFly = true;
    }
}
```

### Conditional Execution
```java
// Connect only if requested
if (shouldConnect) {
    drone.pair();
}

// Fly only if authorized
if (shouldFly) {
    drone.takeoff();
    demonstrateRealFlightMonitoring(drone);
    drone.land();
} else {
    demonstrateFlightMonitoring(drone); // Demo version
}
```

### Safety Checks
```java
// Pre-flight validation in fly mode
if (preFlightSafetyCheck(drone)) {
    if (shouldFly) {
        drone.takeoff();
    }
} else {
    System.out.println("DO NOT FLY!");
}

// Emergency landing on critical errors
if (errors.isLowBattery() || errors.hasCriticalErrors()) {
    drone.emergencyStop();
    drone.land();
}
```

## Safety Features

### Pre-Flight Safety Check
- ✅ Battery level validation
- ✅ Calibration status check
- ✅ Sensor responsiveness
- ✅ Critical error detection
- ✅ Prevents takeoff if unsafe

### In-Flight Monitoring
- ✅ Continuous error checking (1 Hz)
- ✅ Low battery emergency landing
- ✅ Critical error emergency landing
- ✅ Attitude stabilization
- ✅ Sensor diagnostics logging

### Error Response Actions
```java
// Low Battery
if (errors.isLowBattery()) {
    drone.emergencyStop();
    drone.land();
}

// Critical Errors
if (errors.hasCriticalErrors()) {
    drone.emergencyStop();
    drone.land();
}

// Attitude Instability
if (errors.hasStateError(ATTITUDE_NOT_STABLE)) {
    drone.hover(1.0); // Stabilize
}
```

## Benefits of Multi-Mode Design

### For Teachers
1. **Progressive instruction** - Start simple, add complexity
2. **Risk management** - Control when drones actually fly
3. **Flexible demos** - Can demonstrate without hardware
4. **Safety control** - Explicit authorization required for flight

### For Students
1. **Learn safely** - Master API before flying
2. **Build confidence** - Progress at own pace
3. **Understand consequences** - See what each command does
4. **Test thoroughly** - Validate logic before flight

### For Development
1. **Easier testing** - Test logic without hardware
2. **Debugging** - Isolate connection vs logic issues
3. **Documentation** - Clear examples of each mode
4. **Maintenance** - Modes clearly separated

## Real-World Error Scenarios

### Low Battery Detection
**Connect Mode**: Can force by running until battery depletes
**Result**: Example detects and shows emergency landing logic

### Sensor Failures
**Connect Mode**: Disconnect sensor to trigger error flag
**Result**: Example logs diagnostic information

### Calibration Monitoring
**Connect Mode**: Watch actual calibration flag timing
**Result**: Learn typical calibration duration (3-5 seconds)

### Attitude Instability
**Fly Mode**: May occur during aggressive maneuvers
**Result**: Automatic stabilization response

## Command Reference

### Help
```bash
java ErrorMonitoringExample --help
java ErrorMonitoringExample -h
```

Shows complete usage information and safety notes.

### Multiple Arguments (Invalid)
```bash
java ErrorMonitoringExample --connect --fly  # Second flag overrides
```
Note: `--fly` implies `--connect`, so use `--fly` alone for flight operations.

## Future Enhancements

### Potential Additions
1. **--duration N** - Set flight duration in seconds
2. **--altitude N** - Set target altitude
3. **--aggressive** - Include more dynamic maneuvers
4. **--log FILE** - Write error log to file
5. **--verbose** - Show detailed debug info
6. **--simulate** - Mock error conditions for testing

### Advanced Features
- Error injection for testing
- Custom error response scripts
- Data logging and analysis
- Multiple drone coordination
- Remote monitoring interface

## Testing Recommendations

### Demo Mode Testing
- ✅ No hardware required
- ✅ Test output formatting
- ✅ Verify logic flow
- ✅ Check documentation accuracy

### Connect Mode Testing
- ✅ Verify connection handling
- ✅ Test error data retrieval
- ✅ Validate calibration monitoring
- ✅ Check timeout handling

### Fly Mode Testing
- ✅ Clear flight area (minimum 10ft × 10ft)
- ✅ Fully charged battery
- ✅ Emergency stop ready
- ✅ Observer monitoring
- ✅ First flight at low altitude

## Conclusion

The enhanced multi-mode design provides:
- **Safe learning progression** from theory to practice
- **Flexible demonstration** options for different scenarios
- **Real-world validation** with actual hardware
- **Clear authorization** for flight operations
- **Educational value** at every level

This approach balances educational goals with safety requirements while demonstrating best practices for error monitoring and flight safety.

---
**Version**: 2.5  
**Date**: October 15, 2025  
**Author**: JCoDroneEdu Development Team
