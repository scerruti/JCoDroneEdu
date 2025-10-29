# Elevation API Implementation

## Summary

Implemented a comprehensive elevation API that provides Python compatibility while improving upon the Python implementation with explicit method names and state-based flexibility.

## Problem

The drone firmware reports altitude with a **+100 to +150 meter offset**. At 16m above sea level, the drone reports 126m (error of ~110m). The pressure sensor is accurate and can be used to calculate correct altitude using the standard barometric formula.

## Python API Analysis

Python's `codrone-edu` library has:
- `get_elevation(unit="m")` - Returns **uncorrected** firmware altitude (with offset)
- `height_from_pressure(b=0, m=9.34)` - Returns **relative height** from reference pressure (linear approximation)
- `get_height(unit="cm")` - Returns **range sensor** (ToF), NOT barometric altitude

**Python does NOT apply automatic altitude correction!**

## New Java API

### Methods Added

```java
// Explicit methods (RECOMMENDED)
public double getUncorrectedElevation()           // Raw firmware value (with ~109m offset)
public double getCorrectedElevation()             // Accurate altitude using barometric formula
public double getCorrectedElevation(double seaLevelPressure)  // With calibrated pressure

// Python-compatible convenience method
public double getElevation()                      // State-based: uncorrected by default

// State control
public void useCorrectedElevation(boolean use)   // Switch getElevation() behavior
```

### Backward Compatibility

```java
@Deprecated(since = "1.0", forRemoval = true)
public double getCalculatedAltitude()             // → getCorrectedElevation()
public double getCalculatedAltitude(double)       // → getCorrectedElevation(double)
```

## Design Philosophy

### Naming Convention
- **"Uncorrected"** = Raw firmware value (has offset)
- **"Corrected"** = Fixed using barometric formula (accurate)
- **"Elevation"** = Matches Python's terminology
- **"Calibrated"** = Reserved for adjusting sea-level pressure parameter

### Python Compatibility
- `getElevation()` matches Python's `get_elevation()` behavior (returns uncorrected by default)
- State-based switching allows Python-like usage while providing explicit methods
- Default behavior: uncorrected (matches Python)

### Educational Benefits
1. **Explicit methods prevent mistakes** - Students can't accidentally use wrong value
2. **Demonstrates sensor calibration** - Real-world example of firmware offset correction
3. **Teaches data validation** - Compare raw vs corrected values
4. **Shows formula application** - Barometric formula in action

## Examples

### Example 1: Explicit Methods (Recommended)
```java
Drone drone = new Drone();
drone.pair();

double uncorrected = drone.getUncorrectedElevation();  // ~126m (wrong!)
double corrected = drone.getCorrectedElevation();       // ~17m (accurate!)
System.out.printf("Offset: %.2f m\n", uncorrected - corrected);  // ~109m
```

### Example 2: Python-Compatible
```java
Drone drone = new Drone();
drone.pair();

// Default matches Python (uncorrected)
double elevation = drone.getElevation();  // ~126m (has offset)

// Switch to corrected
drone.useCorrectedElevation(true);
elevation = drone.getElevation();  // ~17m (accurate!)
```

### Example 3: Calibrated with Local Pressure
```java
// Get local sea-level pressure from weather station
double localPressure = 101500.0;  // 1015 hPa in Pascals

double elevation = drone.getCorrectedElevation(localPressure);
// More accurate than using standard pressure (101325 Pa)
```

## Files Modified

### Core Changes
- **`Drone.java`**
  - Added `private boolean useCorrectedElevation = false;`
  - Added `getUncorrectedElevation()` method
  - Added `getElevation()` convenience method
  - Added `useCorrectedElevation(boolean)` setter
  - Renamed `getCalculatedAltitude()` → `getCorrectedElevation()`
  - Renamed `getCalculatedAltitude(double)` → `getCorrectedElevation(double)`
  - Added `@Deprecated` wrappers for backward compatibility
  - Enhanced documentation with offset information

### Examples Updated
- **`AltitudePressureTest.java`**
  - Updated to use `getUncorrectedElevation()` and `getCorrectedElevation()`
  - Removed manual barometric formula calculation (now in API)
  - Updated documentation

### New Examples
- **`ElevationApiDemo.java`**
  - Demonstrates all three API styles (explicit, Python-compatible, calibrated)
  - Shows state-based switching
  - Educational example comparing all methods

### Build Configuration
- **`build.gradle.kts`**
  - Added `runElevationApiDemo` task

## Testing

All tests pass:
```
BUILD SUCCESSFUL in 13s
12 actionable tasks: 4 executed, 8 up-to-date
```

Run the demos:
```bash
./gradlew runElevationApiDemo
./gradlew runAltitudePressureTest
```

## Documentation

All methods include comprehensive Javadoc with:
- ⚠️ Warnings about firmware offset
- 🎯 Educational usage examples
- 📊 Real-world data examples
- ✓ Recommendations for students
- Cross-references to related methods
- `@apiNote` showing Python equivalents

## Recommendations

**For Students:**
- ✅ Use `getCorrectedElevation()` for accurate altitude
- ✅ Use `getElevation()` for Python compatibility
- ✅ Use `getCorrectedElevation(localPressure)` for best accuracy
- ❌ Avoid `getUncorrectedElevation()` unless debugging

**For Python Transition:**
- Java's `getElevation()` = Python's `get_elevation()`
- Both return uncorrected value by default
- Java improves on Python by providing explicit corrected methods

## Future Considerations

Could add (matching Python's `height_from_pressure()`):
```java
public double getHeightFromPressure(double referencePressure)
```
For relative height changes from a reference point (useful for tracking altitude changes during flight).

---

**Implementation Date:** October 15, 2025  
**Branch:** docs/patch-smoke-test  
**Status:** ✅ Complete and tested
