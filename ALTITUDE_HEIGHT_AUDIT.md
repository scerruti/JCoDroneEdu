# Python vs Java Altitude/Height/Pressure API Audit

## Executive Summary

This audit compares the Python `codrone_edu` library's altitude/height/pressure methods with the Java `JCoDroneEdu` implementation, focusing on behavioral differences, documentation quality, and educational suitability for 9th grade students.

## Key Findings

### 1. Critical Behavioral Differences

| Aspect | Python | Java | Issue |
|--------|--------|------|-------|
| **Pressure retry** | Calls `get_altitude_data()` up to 3 times if `pascals == 0` | Calls `getPressure()` once, returns 0.0 if null | Python: wasteful retries; Java: no retry |
| **Elevation source** | Always returns raw firmware altitude from `altitude_data[3]` | Can return raw OR corrected via state toggle | Java adds hidden state |
| **Data freshness** | Always requests + sleeps (10ms default) | Mixed: `getAltitude()` waits, `getPressure()` sleeps 100ms, `getUncorrectedElevation()` reuses stale | Inconsistent |
| **Temperature access** | `get_drone_temperature()` requests altitude packet | No public `getDroneTemperature()` wrapper | Java discovery issue |
| **Zero handling** | Returns 0, retries pressure | Returns 0.0 | Ambiguous: error vs sea level |

### 2. Method-by-Method Comparison

#### Altitude Data Retrieval

**Python: `get_altitude_data(delay=0.01)`**
```python
def _get_altitude_data_desktop(self, delay=0.01):
    self.sendRequest(DeviceType.Drone, DataType.Altitude)
    time.sleep(delay)
    return self.altitude_data
```
- **Returns**: List `[timestamp, temperature, pressure, altitude, rangeHeight]`
- **Documentation**: Minimal docstring showing packet structure
- **Freshness**: Always requests; sleeps 10ms
- **Grade 9 friendly**: ❌ No explanation of what altitude vs height means

**Java: `getAltitude()` / `getAltitudeData()`**
```java
public Altitude getAltitude() {
    sendRequestWait(DataType.Altitude);
    return droneStatus.getAltitude();
}
```
- **Returns**: `Altitude` object with getters for temperature, pressure, altitude, rangeHeight
- **Documentation**: Brief but includes Python compatibility note
- **Freshness**: Always requests; blocks until received
- **Grade 9 friendly**: ❌ "replaces getAltitudeData for Java idiomatic usage" is jargon

**Assessment**: 
- ✅ Java blocking wait is more reliable than fixed sleep
- ❌ Java returns object instead of array (breaks parity)
- ❌ Neither explains what the values represent for students

---

#### Pressure Reading

**Python: `get_pressure(unit="Pa")`**
```python
def _get_pressure_desktop(self, unit="Pa"):
    pascals = self.get_altitude_data()[2]
    for i in range(3):
        if pascals == 0:  # check if the value is an error which would be 0
            pascals = self.get_altitude_data()[2]
        else:  # if not 0 then it is an ok value
            break
    return self._calculate_value_from_pressure(unit, pascals)
```
- **Documentation**: "requests from the drone the pressure in pascals... :return: Returns pressure reading in set unit rounded by 2 decimal places"
- **Units supported**: Pa, KPa/kPa/kpa, mB, inHg, torr, atm
- **Zero handling**: Retries up to 3 times (requesting altitude packet each time)
- **Error behavior**: Prints error message, returns pascals on invalid unit
- **Grade 9 friendly**: ⚠️ Docstring lists units but doesn't explain what pressure IS

**Java: `getPressure()` / `getPressure(String unit)`**
```java
public double getPressure() {
    sendRequest(DataType.Altitude);
     try {
        Thread.sleep(100); // Brief delay for data request
    } catch (InterruptedException e) {
    } 
    var altitude = droneStatus.getAltitude();
    return (altitude != null) ? altitude.getPressure() : 0.0;
}
```
- **Documentation**: 
  - First javadoc: "Returns the current atmospheric pressure reading from the drone's barometric sensor. This data is useful for altitude calculations and environmental monitoring projects." + educational usage bullets
  - Duplicate shorter javadoc below it (redundant)
- **Units supported**: pa, kpa, mbar, inhg, atm (lowercase only)
- **Zero handling**: None - returns 0.0 if altitude is null
- **Error behavior**: Throws `IllegalArgumentException` on invalid unit
- **Grade 9 friendly**: ⚠️ Educational usage bullets mention "Physics Learning: Understand atmospheric pressure" but don't explain it

**Assessment**:
- ❌ Python: Wasteful retry loop (up to 3 full altitude packet requests = 30ms delay minimum)
- ❌ Java: Fixed 100ms sleep is arbitrary and blocking
- ❌ Java: Case-sensitive units (Python accepts "KPa", "kPa", "kpa"; Java only "kpa")
- ❌ Java: Throws exception vs Python's graceful fallback
- ✅ Java: Better structured documentation (but still too advanced)
- ❌ Neither: Explains pressure in 9th grade terms ("how hard air pushes")

---

#### Elevation (Altitude above sea level)

**Python: `get_elevation(unit="m")`**
```python
def _get_elevation_desktop(self, unit="m"):
    value = self.get_altitude_data()[3]
    return self._calculate_value_from_elevation(unit, value)
```
- **Returns**: Raw firmware altitude value from `altitude_data[3]` 
- **Documentation**: None - no docstring
- **Units supported**: m, km, ft, mi
- **Data source**: Firmware-calculated barometric altitude (has ~100-150m offset)
- **Error handling**: Prints error, returns meters on invalid unit
- **Grade 9 friendly**: ❌ No documentation at all

**Java: `getElevation()`**
```java
public double getElevation() {
    return useCorrectedElevation ? getCorrectedElevation() : getUncorrectedElevation();
}
```
- **Returns**: Either raw firmware value OR corrected calculation depending on `useCorrectedElevation` flag
- **Documentation**: 
  - Comprehensive (9 lines) explaining state-based behavior
  - Includes usage examples
  - Warns about state-based approach: "prefer using getUncorrectedElevation() or getCorrectedElevation() directly"
- **Units supported**: Only meters returned; no unit parameter
- **Data source**: Conditional based on flag
- **Error handling**: Returns 0.0 if altitude data null
- **Grade 9 friendly**: ⚠️ Text is clear but uses advanced terms ("state-based behavior", "Python compatibility")

**Python: No explicit `get_uncorrected_elevation()` or `get_corrected_elevation()`**

**Java: `getUncorrectedElevation()`**
```java
public double getUncorrectedElevation() {
    var altitude = droneStatus.getAltitude();
    return altitude != null ? altitude.getAltitude() : 0.0;
}
```
- **Returns**: Raw firmware altitude (no request - reuses last altitude packet)
- **Documentation**: 
  - Excellent warning about firmware offset (+100 to +150m)
  - Explains use cases (Python parity, education, debugging)
  - Includes concrete example with actual numbers
- **Grade 9 friendly**: ✅ Clear warnings with specific numbers; uses ⚠️ emoji
- **Issue**: Does NOT request fresh data - relies on stale `droneStatus`

**Java: `getCorrectedElevation()` (multiple overloads)**
- **Default**: Automatically fetches sea-level pressure via WeatherService
- **With seaLevelPressure**: Manual calibration
- **With lat/long**: Fetches weather for location
- **Documentation**: Very comprehensive (20+ lines per overload)
- **Grade 9 friendly**: ⚠️ Thorough but overwhelming (formula, API details, location instructions all mixed)

**Assessment**:
- ❌ Python: Zero documentation on `get_elevation()`
- ❌ Python: No way to get corrected altitude
- ❌ Java: Hidden state toggle (`useCorrectedElevation`) changes semantics
- ❌ Java: No unit parameter on `getElevation()` (Python has it)
- ✅ Java: Provides explicit uncorrected/corrected methods
- ❌ Java: `getUncorrectedElevation()` doesn't request fresh data
- ⚠️ Java: Documentation too verbose for target audience

---

#### Height from Pressure (Relative)

**Python: `set_initial_pressure()` + `height_from_pressure(b=0, m=9.34)`**
```python
def _set_initial_pressure_desktop(self):
    self.init_press = 0
    while self.init_press < 1:
        self.init_press = self.get_pressure()

def _height_from_pressure_desktop(self, b=0, m=9.34):
    current_pressure = self.get_pressure()
    height_pressure = (self.init_press - current_pressure + b) * m
    height_pressure = round(height_pressure, 2)
    return height_pressure
```
- **Documentation**: 
  - `set_initial_pressure()`: None
  - `height_from_pressure()`: "initial_pressure: the initial pressure taken at the 'floor level'... :return: the height in millimeters"
- **Formula**: `(init_press - current_press + b) * m`
- **Default constants**: b=0, m=9.34
- **Units**: Returns millimeters
- **Grade 9 friendly**: ⚠️ Explains concept but formula details are advanced

**Java: `setInitialPressure()` + `getHeightFromPressure()` (2 overloads)**
```java
public void setInitialPressure() {
    this.initialPressure = getPressure();
}

public double getHeightFromPressure(double b, double m) {
    if (initialPressure == 0.0) {
        return 0.0;
    }
    double currentPressure = getPressure();
    double heightMm = (initialPressure - currentPressure + b) * m;
    return Math.round(heightMm * 100.0) / 100.0;
}
```
- **Documentation**: 
  - `setInitialPressure()`: Comprehensive (15 lines) with use cases and usage pattern
  - `getHeightFromPressure()`: Very detailed (20+ lines) explaining parameters, calibration, formula
- **Formula**: Identical to Python
- **Default constants**: Matching (b=0, m=9.34)
- **Units**: Returns millimeters (same as Python)
- **Grade 9 friendly**: ⚠️ Well-structured but uses advanced vocabulary ("systematic errors", "empirically-derived constant")

**Assessment**:
- ✅ Perfect parity in behavior and formula
- ❌ Python: Minimal documentation
- ✅ Java: Excellent structure (use cases, calibration advice, formula explanation)
- ❌ Java: Too verbose and technical for 9th graders
- ⚠️ Neither: Explains WHY 9.34 mm/Pa in accessible terms

---

#### Height (Time-of-Flight Sensor)

**Python: `get_height(unit="cm")`**
```python
def get_height(self, unit="cm"):
    """
    height in centimeters
    :param unit: "cm", "in", "mm", or "m"
    :return: height in chosen unit (centimeter by default).
    """
    return self.get_bottom_range(unit)
```
- **Returns**: Alias of `get_bottom_range()` (range sensor reading)
- **Documentation**: Single line
- **Units**: cm (default), in, mm, m
- **Data source**: Time-of-flight bottom range sensor
- **Grade 9 friendly**: ⚠️ Doesn't explain difference from elevation

**Java: `getHeight()` / `getHeight(String unit)`**
```java
public double getHeight() {
    return flightController.getHeight();
}
```
(FlightController delegates to `getBottomRange()`)
- **Returns**: Alias of `getBottomRange()` in FlightController
- **Documentation**: "Gets the current height from the ground using the bottom range sensor." + Python compatibility note
- **Units**: cm (default), mm, m, in
- **Data source**: Time-of-flight bottom range sensor (requests fresh via `sendRequestWait`)
- **Grade 9 friendly**: ✅ Brief but clear ("from the ground using... sensor")

**Assessment**:
- ✅ Perfect behavioral parity
- ✅ Java: Clearer documentation (mentions "from the ground" and sensor type)
- ❌ Neither: Explicitly contrasts height (ground distance) vs elevation (sea level)

---

#### Range Sensors

**Python: `get_range_data()`, `get_front_range(unit="cm")`, `get_bottom_range(unit="cm")`**
- All follow same pattern: request, sleep, convert from millimeters
- Documentation: Minimal ("the unit that the distance will be in")
- Note in `update_range_data()`: "Bottom range sensor will not update unless flying. Will display -1000 by default"
- **Grade 9 friendly**: ❌ No explanation of time-of-flight sensors

**Java: `getFrontRange()`, `getBottomRange()` (in FlightController)**
- Pattern: request via `sendRequestWait`, convert from millimeters
- Documentation: "Gets the distance measured by the [front/bottom] range sensor."
- No mention of -1000 default for bottom sensor when not flying
- **Grade 9 friendly**: ⚠️ Brief but lacks context about sensor type

**Assessment**:
- ✅ Behavioral parity
- ❌ Python: Better note about bottom sensor behavior when grounded
- ❌ Neither: Explains what time-of-flight sensors are or how they work

---

#### Temperature

**Python: `get_drone_temperature(unit="C")` (and deprecated `get_temperature()`)**
```python
def _get_drone_temperature_desktop(self, unit="C"):
    data = self.get_altitude_data()[1]
    return self._calculate_value_from_temperature(unit, data)
```
- **Documentation**: "In celsius by default... :return: the temperature in the desired units rounded 2 decimal places"
- **Units supported**: C/c/Celsius, F/f/Fahrenheit, K/k/Kelvin (case-insensitive)
- **Data source**: `altitude_data[1]` (temperature from altitude packet)
- **Deprecation**: `get_temperature()` prints yellow warning directing to `get_drone_temperature()`
- **Grade 9 friendly**: ⚠️ Lists units but doesn't explain what's being measured

**Java: No public `getDroneTemperature()` method**
- Temperature accessible via `getAltitude().getTemperature()` (requires knowing Altitude object structure)
- No wrapper method for Python parity
- **Grade 9 friendly**: ❌ Discoverability issue for students

**Assessment**:
- ❌ Java: Missing public wrapper - breaks parity
- ✅ Python: Clear deprecation pattern
- ❌ Neither: Explains temperature source (barometer sensor, not ambient air)

---

## 3. Documentation Quality Analysis

### Python Documentation Issues

| Method | Documentation | Grade 9 Issues |
|--------|---------------|----------------|
| `get_altitude_data()` | Packet structure only | No explanation of values |
| `get_pressure()` | Unit list | Doesn't explain what pressure is |
| `get_elevation()` | **NONE** | Critical gap |
| `set_initial_pressure()` | **NONE** | Critical gap |
| `height_from_pressure()` | Technical formula | Formula details too advanced |
| `get_height()` | One line | No context |
| `get_drone_temperature()` | Unit list | No source explanation |

**Overall**: Minimal, inconsistent, missing key explanations

### Java Documentation Issues

| Method | Documentation | Grade 9 Issues |
|--------|---------------|----------------|
| `getAltitude()` | Brief, clear | Uses jargon ("idiomatic") |
| `getPressure()` | Comprehensive structure | "Physics Learning" mentioned but not explained |
| `getUncorrectedElevation()` | Excellent warnings | Clear! Uses concrete numbers |
| `getElevation()` | Very thorough | Too much advanced vocabulary |
| `getCorrectedElevation()` | Extremely detailed (20+ lines) | Overwhelming; mixes concepts |
| `setInitialPressure()` | Well-structured | "Systematic errors", technical terms |
| `getHeightFromPressure()` | Very detailed | "Empirically-derived constant" too advanced |
| `getHeight()` | Brief, clear | Good! Mentions sensor |

**Overall**: Thorough but too technical; needs vocabulary simplification

### Common Documentation Gaps (Both)

1. **No conceptual primer**: Neither explains:
   - Height = distance from ground (range sensor)
   - Elevation = height above sea level (barometric)
   - Pressure = atmospheric pressure (from barometer)
   
2. **Sensor explanation missing**: What is a:
   - Barometer / barometric sensor?
   - Time-of-flight sensor?
   - Why does bottom sensor show -1000 when grounded?

3. **Units context**: Why millimeters for range but meters for elevation?

4. **Zero ambiguity**: Does 0 mean "at sea level", "on ground", or "no data"?

5. **Firmware offset**: Python never mentions the ~110m firmware bug; Java explains it well but only in advanced method

---

## 4. Parity Violations

### Method Naming

| Python | Java | Match? |
|--------|------|--------|
| `get_altitude_data()` | `getAltitudeData()` ✅ (deprecated), `getAltitude()` | ⚠️ Return type differs |
| `get_pressure(unit)` | `getPressure()` / `getPressure(String unit)` | ✅ |
| `get_elevation(unit)` | `getElevation()` | ❌ No unit parameter |
| `set_initial_pressure()` | `setInitialPressure()` | ✅ |
| `height_from_pressure(b, m)` | `getHeightFromPressure()` / `getHeightFromPressure(double b, double m)` | ✅ |
| `get_drone_temperature(unit)` | **MISSING** | ❌ |
| `get_height(unit)` | `getHeight()` / `getHeight(String unit)` | ✅ |
| `get_front_range(unit)` | `getFrontRange()` / `getFrontRange(String unit)` | ✅ |
| `get_bottom_range(unit)` | `getBottomRange()` / `getBottomRange(String unit)` | ✅ |
| N/A | `getUncorrectedElevation()` | Java addition |
| N/A | `getCorrectedElevation()` (3 overloads) | Java addition |
| N/A | `useCorrectedElevation(boolean)` | Java addition (problematic) |

### Behavior Differences

1. **Elevation state**: Python always returns raw; Java conditional based on flag
2. **Pressure retry**: Python retries 3x; Java returns immediately
3. **Unit flexibility**: Python accepts case variations; Java lowercase only
4. **Error handling**: Python prints + continues; Java throws exceptions
5. **Data freshness**: Python always requests; Java mixed (some reuse stale)
6. **Return types**: Python returns lists; Java returns objects
7. **Temperature access**: Python has method; Java requires object navigation

---

## 5. Specific Problems

### Python Issues

1. **Wasteful retry loop**: `get_pressure()` can request altitude packet 3 times (30+ms delay)
2. **No corrected elevation**: Students can't get accurate altitude without external calculation
3. **Missing documentation**: `get_elevation()` and `set_initial_pressure()` have no docstrings
4. **Fixed sleep timing**: All methods use fixed delays regardless of actual packet arrival
5. **Zero ambiguity**: 0 could mean error, sea level, or ground - no way to distinguish

### Java Issues

1. **Hidden state toggle**: `useCorrectedElevation(boolean)` creates action-at-a-distance
2. **Inconsistent freshness**: Some methods request, some reuse, some sleep
3. **Missing unit parameter**: `getElevation()` only returns meters (Python has unit param)
4. **Missing temperature wrapper**: No `getDroneTemperature()` method
5. **Case-sensitive units**: Python accepts "KPa"/"kPa"/"kpa"; Java only "kpa"
6. **Exception on bad unit**: Throws vs Python's graceful fallback
7. **Duplicate docs**: `getPressure()` has two javadoc blocks (editing artifact?)
8. **Verbose documentation**: Many methods have 15-25 line javadocs mixing concepts
9. **No unit tests**: Cannot verify retry behavior, freshness, or unit conversions
10. **Blocking sleep**: `getPressure()` has hardcoded `Thread.sleep(100)`

---

## 6. Recommendations

### Immediate Fixes (Parity)

1. **Add `getDroneTemperature(String unit)` to Java**
   - Wrapper for `getAltitude().getTemperature()` with unit conversion
   - Match Python's C/F/K support with case-insensitivity

2. **Add unit parameter to Java `getElevation(String unit)`**
   - Match Python's m/km/ft/mi support
   - Apply to `getUncorrectedElevation()` and `getCorrectedElevation()` too

3. **Remove or deprecate `useCorrectedElevation(boolean)`**
   - State toggle violates principle of least surprise
   - Students should use explicit `getUncorrectedElevation()` or `getCorrectedElevation()`

4. **Make Java units case-insensitive**
   - Accept "Pa"/"pa"/"PA", "KPa"/"kPa"/"kpa", etc.
   - Match Python's forgiving approach

5. **Standardize error handling**
   - Decision: throw exceptions or print + fallback?
   - Document which approach and why

### Data Freshness Strategy

1. **Implement snapshot caching**
   ```java
   private static class AltitudeSnapshot {
       final long timestampMs;
       final double pressure;
       final double temperature;
       final double altitude;
       final double rangeHeight;
   }
   ```

2. **Centralize freshness logic**
   ```java
   private AltitudeSnapshot ensureFreshAltitude(long maxAgeMs) {
       long now = System.currentTimeMillis();
       if (altitudeSnapshot == null || now - altitudeSnapshot.timestampMs > maxAgeMs) {
           sendRequestWait(DataType.Altitude);
           // update snapshot
       }
       return altitudeSnapshot;
   }
   ```

3. **Reuse snapshot across getters**
   - `getPressure()`, `getUncorrectedElevation()`, `getDroneTemperature()` all pull from same snapshot
   - Eliminates redundant requests and inconsistent delays
   - Student calls like:
     ```java
     double p = drone.getPressure();
     double e = drone.getElevation();
     double t = drone.getDroneTemperature();
     ```
     Would trigger only ONE altitude packet request (if within freshness window)

4. **Remove Python retry loop**
   - Zero-checking loop is a code smell (hiding sensor errors)
   - Better: return NaN or Optional, check error flags

### Documentation Rewrite (Grade 9 Target)

**Structure for each method:**

1. **One-sentence summary** (what it does)
2. **Simple explanation** (what the value represents, in plain English)
3. **Code example** (2-3 lines max)
4. **Units note** (if applicable)
5. **Advanced details** (formula, calibration) under collapsible section or separate guide

**Vocabulary simplification:**

| Current | Grade 9 Alternative |
|---------|---------------------|
| "atmospheric pressure" | "how hard the air pushes" |
| "barometric sensor" | "air pressure sensor" |
| "time-of-flight sensor" | "distance sensor that uses light" |
| "firmware offset" | "the drone's built-in math has an error" |
| "calibration" | "adjusting for better accuracy" |
| "systematic errors" | "regular differences" |
| "empirically-derived constant" | "number found by testing" |
| "standard atmosphere formula" | "science formula for altitude" |

**Example rewrite:**

**Before (Java `getCorrectedElevation()`):**
> "Calculates accurate altitude from barometric pressure using the standard barometric formula. This method provides accurate altitude calculation, correcting the drone's built-in altitude reading which has a firmware offset of +100 to +150 meters..."

**After:**
> "Gets your altitude above sea level by measuring air pressure. The drone's sensor has a built-in error of about 110 meters, so this method fixes that. Returns your actual height in meters."

### Testing Requirements

1. **Unit conversion tests**: Verify Pa → kPa, m → ft, C → F, etc.
2. **Freshness tests**: Rapid successive calls should reuse data
3. **Stale tests**: Old data should trigger refresh
4. **Zero-value tests**: Distinguish between error and valid zero
5. **Retry behavior**: Python's 3-attempt loop vs Java's immediate return
6. **State isolation**: `useCorrectedElevation()` shouldn't affect explicit methods

---

## 7. Summary Table

| Feature | Python | Java | Winner | Fix Priority |
|---------|--------|------|--------|--------------|
| Method names | ✅ | ✅ | Tie | - |
| Behavioral parity | ⚠️ | ⚠️ | Tie | High (state toggle) |
| Data freshness | ❌ Fixed delays | ❌ Mixed | Neither | **Critical** |
| Temperature access | ✅ | ❌ Missing | Python | High |
| Elevation units | ✅ | ❌ No param | Python | Medium |
| Unit flexibility | ✅ | ❌ Case-sensitive | Python | Low |
| Corrected altitude | ❌ Missing | ✅ | Java | Medium (add to Python upstream) |
| Error handling | ⚠️ | ⚠️ | Tie | Medium |
| Documentation | ❌ Minimal | ⚠️ Too technical | Neither | **Critical** |
| Code quality | ❌ Retry loop | ❌ Sleep/state | Neither | High |

**Critical Path**: 
1. Implement altitude snapshot caching (fixes freshness)
2. Remove/deprecate state toggle (fixes surprise)
3. Add missing wrappers (fixes parity)
4. Rewrite docs for grade 9 (fixes learning)
5. Add unit tests (validates fixes)

---

## 8. Current Status (Nov 15, 2025)

- Centralized telemetry: Introduced `TelemetryService` to request, cache, and convert sensor data (Range/Position/Altitude/Motion/etc.).
    - Freshness-aware caching, single-flight coalescing, RSSI-aware scaling, classroom jitter, and bounded pressure retries implemented.
    - Configuration lives at `DroneSystem.CommunicationConstants.TelemetryConfig` (not `DroneSystem.TelemetryConfig`).
- API surface alignment:
    - `Drone` exposes student-facing overloads and delegates to `TelemetryService` for height, front/bottom range, and position.
    - `FlightController` retains no-arg overloads for height/range/position that default to centimeters (to preserve educational parity and tests), but also delegates to `TelemetryService`.
- Pressure parity: Java now performs bounded retries on zero pressure values to match Python behavior without arbitrary sleeps.
- Height vs Elevation: `getHeight[()]` remains an alias of bottom range (ground distance). `getElevation()` returns un/ corrected altitude (sea-level reference) depending on `useCorrectedElevation` toggle; explicit `getUncorrectedElevation()`/`getCorrectedElevation()` are recommended.
- Developer note: Documentation and examples should avoid explicit `sendRequest`/sleep patterns; `TelemetryService` handles freshness automatically.

Action items remaining (tracked):
- Consider removing or deprecating `useCorrectedElevation(boolean)` and prefer explicit methods everywhere.
- Optionally route elevation reads through `TelemetryService` to unify freshness for barometer frames.
- Update quick references and teacher docs to clarify “height (ground distance) vs elevation (sea-level)” with 9th-grade vocabulary.
