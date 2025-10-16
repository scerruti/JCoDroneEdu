# Error Data API Summary

## Overview
Implemented error state monitoring for CoDrone EDU with a two-tier API approach that maintains Python compatibility while providing a cleaner Java-idiomatic interface.

## API Methods

### Tier 1: Python-Compatible (Array-Based)

#### `double[] getErrorData()`
```java
double[] errorData = drone.getErrorData();
if (errorData != null) {
    double timestamp = errorData[0];           // seconds since epoch
    int sensorErrors = (int) errorData[1];     // bitmask
    int stateErrors = (int) errorData[2];      // bitmask
    
    // Check for low battery using bitwise operations
    if ((stateErrors & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0) {
        System.out.println("Low battery!");
        drone.land();
    }
}
```

#### `double[] getErrorData(double delay)`
Same as above but with custom delay for data collection.

**Purpose:** 100% Python API compatibility
**Return:** `[timestamp_seconds, sensorFlags, stateFlags]`
**Matches:** Python's `drone.get_error_data()`

### Tier 3: Java-Idiomatic (Type-Safe Objects)

#### `ErrorData getErrors()`
```java
ErrorData errors = drone.getErrors();
if (errors != null) {
    // Simple, readable error checks
    if (errors.isLowBattery()) {
        System.out.println("Low battery!");
        drone.land();
    }
    
    if (errors.isCalibrating()) {
        System.out.println("Calibrating... please wait");
    }
    
    // Type-safe error detection
    if (errors.hasStateError(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE)) {
        System.out.println("Attitude unstable!");
    }
    
    // Get all errors as enum sets
    Set<DroneSystem.ErrorFlagsForSensor> sensorErrors = errors.getSensorErrors();
    Set<DroneSystem.ErrorFlagsForState> stateErrors = errors.getStateErrors();
    
    // Check for critical issues
    if (errors.hasCriticalErrors()) {
        drone.emergencyStop();
    }
    
    // Display all errors
    System.out.println(errors.toDetailedString());
}
```

#### `ErrorData getErrors(double delay)`
Same as above but with custom delay for data collection.

**Purpose:** Best practice for Java developers
**Return:** Immutable `ErrorData` object
**Benefits:**
- ✅ No array indexing (`[0]`, `[1]`, `[2]`)
- ✅ No bitwise operations (`& 0x08`)
- ✅ No type casting (`(int)`)
- ✅ IDE auto-completion for all errors
- ✅ Compile-time type safety
- ✅ Self-documenting code

## ErrorData Class Features

### Type-Safe Error Checking
```java
// Sensor errors
boolean hasError = errors.hasSensorError(ErrorFlagsForSensor.MOTION_CALIBRATING);
Set<ErrorFlagsForSensor> allSensorErrors = errors.getSensorErrors();

// State errors  
boolean hasError = errors.hasStateError(ErrorFlagsForState.LOW_BATTERY);
Set<ErrorFlagsForState> allStateErrors = errors.getStateErrors();
```

### Convenience Methods
```java
errors.isCalibrating()       // MOTION_CALIBRATING check
errors.isLowBattery()        // LOW_BATTERY check
errors.hasCriticalErrors()   // Any critical error
errors.hasAnyErrors()        // Any error at all
errors.hasAnySensorErrors()  // Any sensor error
errors.hasAnyStateErrors()   // Any state error
```

### Timestamp Access
```java
Instant timestamp = errors.getTimestamp();           // Java Instant
double seconds = errors.getTimestampSeconds();       // Python-compatible
```

### Display Methods
```java
String simple = errors.toString();          // Concise: ErrorData{timestamp=..., stateErrors=[...]}
String detailed = errors.toDetailedString(); // Multi-line with all error names
```

### Raw Access (for compatibility)
```java
int sensorFlags = errors.getSensorErrorFlags();  // Raw bitmask
int stateFlags = errors.getStateErrorFlags();    // Raw bitmask
```

## Error Flags

### Sensor Errors (`ErrorFlagsForSensor`)
1. `MOTION_CALIBRATING` - Gyroscope/accelerometer calibrating
2. `MOTION_NO_ANSWER` - Motion sensor unresponsive
3. `MOTION_WRONG_VALUE` - Motion sensor incorrect data
4. `MOTION_NOT_CALIBRATED` - Motion sensor not calibrated
5. `PRESSURE_NO_ANSWER` - Barometer unresponsive
6. `PRESSURE_WRONG_VALUE` - Barometer incorrect data
7. `RANGE_GROUND_NO_ANSWER` - Bottom range sensor unresponsive
8. `RANGE_GROUND_WRONG_VALUE` - Bottom range sensor incorrect
9. `FLOW_NO_ANSWER` - Optical flow sensor unresponsive
10. `FLOW_WRONG_VALUE` - Optical flow sensor incorrect
11. `FLOW_CANNOT_RECOGNIZE_GROUND_IMAGE` - Cannot recognize ground pattern

### State Errors (`ErrorFlagsForState`)
1. `NOT_REGISTERED` - Device not registered
2. `FLASH_READ_LOCK_UNLOCKED` - Flash memory read lock not engaged
3. `BOOTLOADER_WRITE_LOCK_UNLOCKED` - Bootloader write lock not engaged
4. `LOW_BATTERY` - Battery level is low ⚠️
5. `TAKEOFF_FAILURE_CHECK_PROPELLER_AND_MOTOR` - Takeoff failed ⚠️
6. `CHECK_PROPELLER_VIBRATION` - Propeller vibration detected ⚠️
7. `ATTITUDE_NOT_STABLE` - Drone attitude too tilted ⚠️
8. `CANNOT_FLIP_LOW_BATTERY` - Battery below 50% for flip
9. `CANNOT_FLIP_TOO_HEAVY` - Drone too heavy for flip

⚠️ = Critical error (checked by `hasCriticalErrors()`)

## Design Philosophy

### Why Two Tiers (Not Three)?

**Tier 1: Python-Compatible Arrays**
- Required for Python API parity (89% coverage goal)
- Method name: `getErrorData()` - matches Python's `get_error_data()`
- Cannot be renamed or removed without breaking compatibility

**Tier 3: Java Composite Objects**
- Method name: `getErrors()` - clean, simple, Java-idiomatic
- New functionality, not tied to Python API
- Provides superior developer experience

**No Tier 2 (Individual Getters)**
- Would be: `getErrorTimestamp()`, `getSensorErrorFlags()`, `getStateErrorFlags()`
- Adds method surface area without clear benefit
- Tier 1 provides raw access, Tier 3 provides convenience
- Middle ground adds complexity without solving a problem

### Naming Rationale

#### ❌ Why Not `getErrorDataObject()`?
- Redundant: "Object" adds no information
- Inconsistent with Java conventions (`LocalDateTime.now()`, not `nowObject()`)
- Return type already documents it's an object
- Only made sense in contrast to array method

#### ✅ Why `getErrors()`?
- Clean and simple
- Semantically accurate: returns the errors
- Follows Java conventions (getter-like but for composite data)
- Distinct from `getErrorData()` (array version)
- Similar patterns: `getInfo()`, `getData()`, `getState()`

### Example: Comparing with ButtonData

**ButtonData Pattern (existing):**
```java
double[] data = drone.getButtonData();        // Array (Python-compatible)
ButtonData data = drone.getButtonDataObject(); // Object (Java-idiomatic) ⚠️
```

**ErrorData Pattern (new, improved):**
```java
double[] data = drone.getErrorData();  // Array (Python-compatible)
ErrorData errors = drone.getErrors();  // Object (Java-idiomatic) ✅
```

The error data API improves on the button data pattern by using cleaner naming that doesn't append "Object" unnecessarily.

## Refactoring Plan for Other APIs

To apply this improved pattern to `ButtonData` and `JoystickData`:

### Current (Awkward):
```java
ButtonData buttons = drone.getButtonDataObject();
JoystickData joystick = drone.getJoystickDataObject();
```

### Proposed (Clean):
```java
ButtonData buttons = drone.getButtons();
JoystickData joystick = drone.getJoystick();
```

**Migration Strategy:**
1. Add new clean methods (`getButtons()`, `getJoystick()`)
2. Keep old methods for backward compatibility
3. Mark old methods as `@Deprecated`
4. Update documentation to recommend new methods
5. Future release: Remove deprecated methods

## Testing

### ErrorDataTest.java
Tests Python-compatible array methods:
- Array format verification (`[timestamp, sensorFlags, stateFlags]`)
- Null handling
- Bitwise flag detection
- Multiple simultaneous errors
- Python API format compatibility

### ErrorDataObjectTest.java
Tests Java-idiomatic object methods:
- ErrorData creation and conversion
- Type-safe error detection
- Enum set collections
- Convenience methods (`isLowBattery()`, `isCalibrating()`)
- Immutability
- Critical error detection
- Display methods

**All tests passing ✅**

## Documentation

### JavaDoc Coverage
- ✅ Comprehensive method documentation
- ✅ Usage examples for both tiers
- ✅ All 20 error flags documented
- ✅ Educational context (APCSA lessons)
- ✅ Safety programming guidance
- ✅ Comparison examples (Tier 1 vs Tier 3)

### API Comparison
- ✅ Updated API_COMPARISON.md
- ✅ State/Status Data: 80% → 100%
- ✅ Overall completion: 88% → 89%
- ✅ Added to "Major API Additions"

## Implementation Files

### Core Implementation
- `src/main/java/com/otabi/jcodroneedu/Drone.java`
  - `getErrorData()` - Tier 1 (Python-compatible)
  - `getErrorData(double delay)` - Tier 1 with custom delay
  - `getErrors()` - Tier 3 (Java-idiomatic)
  - `getErrors(double delay)` - Tier 3 with custom delay

- `src/main/java/com/otabi/jcodroneedu/ErrorData.java`
  - Immutable composite object
  - Type-safe error checking
  - Convenience methods
  - Display utilities

- `src/main/java/com/otabi/jcodroneedu/protocol/linkmanager/Error.java`
  - Added getters: `getSystemTime()`, `getErrorFlagsForSensor()`, `getErrorFlagsForState()`

### Testing
- `src/test/java/com/otabi/jcodroneedu/ErrorDataTest.java` (8 tests)
- `src/test/java/com/otabi/jcodroneedu/ErrorDataObjectTest.java` (14 tests)

### Documentation
- `API_COMPARISON.md` - Updated progress
- `ERROR_DATA_API_SUMMARY.md` - This document

## Best Practices

### For Java Developers (Recommended)
```java
// ✅ Use getErrors() for clean, type-safe code
ErrorData errors = drone.getErrors();
if (errors != null && errors.isLowBattery()) {
    drone.land();
}
```

### For Python Compatibility
```java
// ✅ Use getErrorData() when porting from Python
double[] errorData = drone.getErrorData();
if (errorData != null) {
    int stateErrors = (int) errorData[2];
    if ((stateErrors & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0) {
        drone.land();
    }
}
```

### Educational Context
- **L0106 Conditionals:** Error checking with if statements
- **L0108 Loops:** Continuous error monitoring
- **L0109 Methods:** Encapsulation of error data
- **L0111 OOP:** Immutable data objects, type safety
- **Safety Programming:** Proactive error handling

## Summary

✅ **Python API Compatibility:** `getErrorData()` provides 100% format match  
✅ **Java Best Practices:** `getErrors()` provides clean, type-safe interface  
✅ **Comprehensive Testing:** 22 tests covering both tiers  
✅ **Full Documentation:** JavaDoc, examples, educational context  
✅ **Clean Naming:** No "Object" suffix - just `getErrors()`  
✅ **Ready for Production:** All tests passing, builds successfully  

The error data API demonstrates how to successfully balance Python compatibility with Java idioms, providing the best of both worlds without compromise.
