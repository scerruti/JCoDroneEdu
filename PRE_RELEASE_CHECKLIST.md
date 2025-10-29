# Pre-Release Validation Checklist

**Target:** JCoDroneEdu v1.0 Release  
**Date Started:** October 15, 2025  
**Target Firmware:** 25.2.1 (March 2025)  
**Current Python Library:** 2.3  

---

## Phase 1: Firmware Update & Baseline Testing

### 1.1 Baseline Testing (BEFORE Firmware Update)

Run all tests with **current** firmware to establish baseline:

**📋 Tests to Run:**
- [x] **Smoke Test**: `./gradlew runSmokeTest`
  - Drone Firmware: `24.9.1`
  - Controller Firmware: `24.9.1` (assumed)
  - Battery Level: `100%`
  - Connection Status: `✓ Working perfectly (cu.usbmodem3973337534311)`

- [x] **Altitude Test**: `./gradlew runAltitudePressureTest`
  - Altitude Offset: `93.64` meters (drone reports 125.4m, actual 31.8m)
  - Sea Level Pressure: `1011.28` hPa
  - Temperature: `2.76-3.18` °C
  - Notes: `Consistent offset across multiple samples`

- [x] **Sensor Test**: `./gradlew runMultiSensorTest`
  - Front Range Sensor: `~407mm avg (386-434mm range, stable)`
  - Bottom Range Sensor: `0mm (expected - not flying)`
  - Optical Flow: `x=-684.0, y=635.0 (consistent)`
  - Color Sensors: `Front: RED (2), Back: BLACK (8) (consistent)`

### 1.2 Update Firmware
- [x] Go to https://codrone.robolink.com/edu/updater/
- [x] Update drone to firmware 25.2.1
- [x] Update controller to firmware 25.2.1
- [x] Verify update completed successfully
- [x] Power cycle drone and controller

**Results - Update:**
```
Update Method: Web updater
Drone Updated: ☑ Yes  ☐ No  ☐ Issues: none
Controller Updated: ☑ Yes  ☐ No  ☐ Issues: none
```

### 1.3 Post-Update Testing

Run the same tests with **new** firmware to compare:

**📋 Tests to Run:**
- [x] **Smoke Test**: `./gradlew runSmokeTest`
  - Drone Firmware: `25.2.1`
  - Controller Firmware: `25.2.1` (assumed)
  - Battery Level: `100%`
  - Connection Status: `✓ Working perfectly (cu.usbmodem3973337534311)`

- [x] **Altitude Test**: `./gradlew runAltitudePressureTest`
  - Altitude Offset: `93.64` meters (UNCHANGED from 24.9.1)
  - Sea Level Pressure: `1011.17` hPa
  - Temperature: `7.50-7.76` °C
  - Notes: `Firmware update did NOT fix altitude offset - still exactly 93.64m`

- [x] **Sensor Test**: `./gradlew runMultiSensorTest`
  - Front Range Sensor: `~430mm avg (384-477mm range, more variance)`
  - Bottom Range Sensor: `0mm (expected - not flying)`
  - Optical Flow: `x=0.0, y=0.0 (CHANGED - was -684.0/635.0)`
  - Color Sensors: `Front: BLACK (8), Back: BLACK (8) (CHANGED - was RED/BLACK)`

### 1.4 Compare Before/After
- [x] Connection works: ☑ Same  ☐ Better  ☐ Worse  ☐ Broken
- [x] Altitude accuracy: ☑ Same  ☐ Better  ☐ Worse (Still 93.64m offset - NOT fixed)
- [x] Sensor readings: ☑ Different (Optical flow reset to 0,0; Color changed to BLACK/BLACK)
- [x] New error messages: ☑ None  ☐ Yes (list: _______)
- [x] Overall: ☑ Safe to proceed  ☐ Issues need investigation

**📊 Key Findings:**
- ❌ Altitude offset UNCHANGED at 93.64m despite firmware claims
- ✓ Optical flow reset to 0,0 (was -684.0/635.0) - calibration reset?
- ⚠️ Color sensors changed (environmental - different lighting?)
- ✓ Connection stable, all sensors functioning

---

## Phase 2: Python Reference Update

### 2.1 Update Python Library
- [x] Run: `./gradlew updateReferenceCode`
- [x] Verify task completed successfully
- [x] Check new version: `cat reference/version.txt`
- [ ] Commit Python library changes to git

**Results:**
```
Old Python Version: 2.3
New Python Version: 2.4
Update Date: October 15, 2025
Any Update Errors: None - Updated successfully
```

### 2.2 Python Baseline Test (Optional - if Python installed)
- [x] Create test script: `scripts/test_python_baseline.py`
- [x] Test connection with new firmware
- [x] Test altitude reading
- [x] Test get_information_data()
- [x] Document any differences

**Results:**
```
Python + New Firmware: ☑ Working  ☐ Issues: None
Python Library Version: 2.4
Firmware Versions: Drone 25.2.1, Controller 25.2.1
Battery: 100%

✅ ALL 8 INVENTORY METHODS WORKING IN PYTHON:
  • get_information_data(): Returns [timestamp, drone_model, drone_fw, controller_model, controller_fw]
  • get_cpu_id_data(): Returns [timestamp, drone_cpu_id_base64, controller_cpu_id_base64]
  • get_address_data(): Returns [timestamp, drone_bt_address_base64, controller_bt_address_base64]
  • get_count_data(): Returns [timestamp, flight_time, takeoffs, landings, accidents]
    - Flight Time: 1742 seconds (29 minutes)
    - Takeoff Count: 90
    - Landing Count: 73
    - Accident Count: 3
  • get_flight_time(): 1742 seconds
  • get_takeoff_count(): 90
  • get_landing_count(): 73
  • get_accident_count(): 3

🔍 ALTITUDE SENSOR INVESTIGATION COMPLETE:
  ✓ RESOLVED: No discrepancy exists - we were comparing different sensors!
    • Python get_height() = Range sensor (ToF) = 0.0m ✓ CORRECT (drone on ground)
    • Java getHeight() = Range sensor (ToF) = 0.0m ✓ CORRECT (same sensor)
    • Java getUncorrectedElevation() = Barometric = 126m ⚠️ (has firmware offset)
  
  Two completely different sensors:
    1. Range sensor: Laser/IR, short range (~2-3m), precise landing
    2. Barometric: Air pressure, long range, flight altitude
  
  Python library doesn't expose raw barometric altitude (the one with offset).
  Our Java elevation API (getCorrectedElevation) is MORE capable than Python!

⚠️  Deprecation Warning: get_temperature() → use get_drone_temperature()
   (Confirmed: Python devs likely anticipate future model without temp sensor)

🌡️ TEMPERATURE API IMPLEMENTATION COMPLETE:
  ✅ Added dual-layer temperature API following API_DESIGN_PHILOSOPHY.md
  ✅ Layer 1 (Python-compatible):
    • @Deprecated getTemperature() - matches Python deprecation
    • @Deprecated getTemperature(String unit) - with log warning
    • getDroneTemperature() - raw sensor die temperature
    • getDroneTemperature(String unit) - with C/F/K conversion
  ✅ Layer 2 (Enhanced):
    • DEFAULT_TEMPERATURE_OFFSET_C = 12.0 - calibration constant
    • getCalibratedTemperature() - ambient estimate (+12°C default)
    • getCalibratedTemperature(double offset) - custom calibration
    • getCalibratedTemperature(String unit) - default + unit conversion
    • getCalibratedTemperature(double, String) - full customization
  ✅ Documentation:
    • Comprehensive JavaDoc with educational examples
    • Created TEMPERATURE_SENSOR_INFO.md - sensor physics explained
    • Created TEMPERATURE_API_ENHANCEMENT.md - implementation summary
    • Explains die temp vs ambient air (10-15°C offset)
  ✅ Demo Application:
    • Created TemperatureCalibrationDemo.java
    • Added Gradle task: runTemperatureCalibrationDemo
    • Demonstrates all 8 methods + calibration procedure
    • Educational output with comparison tables
  
  Pattern established for implementing 8 inventory methods next!
```

---

## Phase 3: API Comparison & Analysis

### 3.1 Automated API Comparison
- [x] Create `compareApis` Gradle task (Copilot will do this)
- [x] Run: `./gradlew compareApis`
- [x] Review generated `API_COMPARISON.md`
- [x] Identify missing methods in Java

**Key Findings:**
```
Missing Methods: 151 total (8 critical for inventory management)
  🔴 CRITICAL (Inventory Methods):
    - get_information_data() - Firmware versions, model info
    - get_cpu_id_data() - Unique device IDs
    - get_address_data() - Bluetooth addresses
    - get_count_data() - Flight statistics array
    - get_flight_time() - Total flight seconds
    - get_takeoff_count() - Number of takeoffs
    - get_landing_count() - Number of landings
    - get_accident_count() - Number of crashes
  
Python has 263 methods, Java has 161 methods
See API_COMPARISON.md for full details
```

### 3.2 Manual Code Review
- [ ] Review Python drone.py changes: `git diff reference/codrone_edu/drone.py`
- [ ] Review protocol.py changes: `git diff reference/codrone_edu/protocol.py`
- [ ] Review system.py changes: `git diff reference/codrone_edu/system.py`
- [ ] Check for new DataTypes
- [ ] Check for changed constants or enums

**Key Changes:**
```
New DataTypes: _____________
Changed Enums: _____________
Bug Fixes: _____________
Breaking Changes: _____________
```

### 3.3 Specific Feature Comparison
- [ ] Compare altitude formulas (Python vs Java)
- [ ] Compare height_from_pressure() implementation
- [ ] Check if get_information_data() exists in Python
- [ ] Check if get_cpu_id_data() exists in Python
- [ ] Check if get_count_data() exists in Python
- [ ] Check for any new sensor methods

**Comparison Results:**
```
Altitude formula match: ☐ Yes  ☐ No (difference: ______)
Height calculation match: ☐ Yes  ☐ No (difference: ______)
Information methods: ☐ Exist  ☐ New  ☐ Changed
New sensor features: _____________
```

---

## Phase 4: Implementation of Missing Features

### 4.1 Implement Missing Methods (If Any)
- [ ] Add `getInformationData()` to Drone.java (if missing)
- [ ] Add `getCpuIdData()` to Drone.java (if missing)
- [ ] Add `getAddressData()` to Drone.java (if missing)
- [ ] Add `getCountData()` to Drone.java (if missing)
- [ ] Add `getFlightTime()` helper method
- [ ] Add `getTakeoffCount()` helper method
- [ ] Add `getLandingCount()` helper method
- [ ] Add `getAccidentCount()` helper method

**Implementation Status:**
```
Methods Added: _____________
Methods Skipped: _____________
Reason for Skip: _____________
```

### 4.2 Create Tests for New Methods
- [ ] Create `InventoryDataTest.java`
- [ ] Test getInformationData()
- [ ] Test getCpuIdData()
- [ ] Test getCountData()
- [ ] Add to existing test suite

### 4.3 Create Examples/Demos
- [ ] Create `InventoryDemo.java` - shows all new methods
- [ ] Add Gradle task: `runInventoryDemo`
- [ ] Test with real drone
- [ ] Document in README

---

## Phase 5: Integration Testing

### 5.1 Run All Tests
- [ ] `./gradlew test` - all unit tests pass
- [ ] `./gradlew runSmokeTest` - smoke test passes
- [ ] `./gradlew runAltitudePressureTest` - altitude test passes
- [ ] `./gradlew runAutomaticElevationDemo` - elevation demo works
- [ ] `./gradlew runMultiSensorTest` - sensors work
- [ ] `./gradlew runInventoryDemo` - (if created) inventory demo works
- [ ] All examples run without errors

**Test Results:**
```
Unit Tests: ☐ All Pass  ☐ Failures: __________
Smoke Test: ☐ Pass  ☐ Fail: __________
Altitude Test: ☐ Pass  ☐ Fail: __________
Sensor Test: ☐ Pass  ☐ Fail: __________
Examples: ☐ All Work  ☐ Issues: __________
```

### 5.2 Real-World Testing Checklist
- [ ] Connection/pairing works reliably
- [ ] Takeoff works (no height sensor errors)
- [ ] Landing works safely
- [ ] Altitude readings accurate (within ±5m with calibration)
- [ ] All range sensors return valid data
- [ ] Color sensors work
- [ ] Position/optical flow works
- [ ] Controller buttons respond
- [ ] Controller joysticks respond
- [ ] Drone LEDs controllable
- [ ] Controller LEDs controllable
- [ ] Buzzer works (both drone and controller)
- [ ] Flight commands responsive
- [ ] Emergency stop works immediately
- [ ] No unexpected error codes
- [ ] Battery reporting accurate

**Real-World Results:**
```
Overall Performance: ☐ Excellent  ☐ Good  ☐ Fair  ☐ Poor
Issues Found: _____________
Workarounds Needed: _____________
```

---

## Phase 6: Documentation Updates

### 6.1 Update Core Documentation
- [ ] Update CHANGELOG.md with all changes
- [ ] Update README.md with firmware compatibility notes
- [ ] Update WEATHER_CALIBRATED_ELEVATION.md (if formula changed)
- [ ] Update CODRONE_EDU_METHOD_TRACKING.md (track new methods)
- [ ] Create FIRMWARE_25.2.1_NOTES.md

### 6.2 Create/Update API Documentation
- [ ] Document any new methods in Javadoc
- [ ] Update API_COMPARISON.md (if exists)
- [ ] Update ELEVATION_API_QUICK_REFERENCE.md (if changed)
- [ ] Update TEACHER_API_QUICK_REFERENCE.md (if exists)

### 6.3 Create Release Notes
- [ ] Create RELEASE_NOTES_v1.0.md
- [ ] List all new features
- [ ] List bug fixes
- [ ] List breaking changes (if any)
- [ ] List known issues
- [ ] List firmware compatibility

---

## Phase 7: Final Verification

### 7.1 Pre-Release Checks
- [ ] Create `preReleaseCheck` Gradle task (Copilot will do this)
- [ ] Run: `./gradlew preReleaseCheck`
- [ ] All tests pass
- [ ] All examples work
- [ ] Documentation complete
- [ ] No uncommitted changes

### 7.2 Clean Build Test
- [ ] Delete `build/` directory
- [ ] Run: `./gradlew clean build`
- [ ] Verify successful build
- [ ] Test smoke test from clean build

### 7.3 Version Update
- [ ] Update version number in build.gradle.kts
- [ ] Update version in README.md
- [ ] Tag release in git
- [ ] Create release branch

---

## Phase 8: Release Preparation

### 8.1 Package for Distribution
- [ ] Build JAR: `./gradlew jar`
- [ ] Verify JAR contents
- [ ] Test with minimal external dependencies
- [ ] Create distribution package

### 8.2 Final Checklist
- [ ] All tests pass ✓
- [ ] All examples work ✓
- [ ] Documentation complete ✓
- [ ] Firmware compatibility verified ✓
- [ ] Python reference updated ✓
- [ ] Known issues documented ✓
- [ ] Release notes ready ✓

---

## Summary & Metrics

### Version Information
```
Java Library Version: _____________
Python Library Version: _____________
Tested Firmware Version: 25.2.1
Compatibility: Firmware 25.2.1+
```

### Key Changes
```
New Methods Added: _____________
Bug Fixes: _____________
Performance Improvements: _____________
Breaking Changes: _____________
```

### Testing Results
| Test Category | Status | Notes |
|---------------|--------|-------|
| Connection | ☐ Pass ☐ Fail | |
| Altitude | ☐ Pass ☐ Fail | |
| Sensors | ☐ Pass ☐ Fail | |
| Flight | ☐ Pass ☐ Fail | |
| Controller | ☐ Pass ☐ Fail | |
| LEDs/Buzzer | ☐ Pass ☐ Fail | |

### Sign-Off
- [ ] Technical validation complete
- [ ] Documentation reviewed
- [ ] Ready for release

**Validated by:** _____________  
**Date:** _____________  
**Release Approved:** ☐ Yes  ☐ No  ☐ Conditional: __________

---

## Rollback Plan (If Needed)

### If Critical Issues Found:
- [ ] Document specific issue
- [ ] Revert firmware if necessary
- [ ] Add workarounds to code
- [ ] Update compatibility matrix
- [ ] Communicate to users

**Issue Severity:** ☐ Minor  ☐ Major  ☐ Critical  
**Action Taken:** _____________  
**Resolution:** _____________  
