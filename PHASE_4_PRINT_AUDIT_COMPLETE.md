# Phase 4: Complete Print Statement Audit
## Python CoDrone EDU Codebase Analysis

**Generated:** November 15, 2025  
**Repository:** JCoDroneEdu  
**Scope:** All active (non-commented) print() statements  
**Total Statements Found:** 257 active print statements across 13 Python files

---

## Executive Summary

### Distribution by Category

| Category | Count | Files | Key Files |
|----------|-------|-------|-----------|
| **Other** | 132 | 4 | drone.py (67), tools/parser.py (47) |
| **Deprecation Warnings** | 26 | 1 | drone.py |
| **Errors/Warnings** | 24 | 3 | tools/parser.py (8), tools/update.py (8) |
| **Unit Conversion Warnings** | 16 | 1 | drone.py |
| **State Errors** | 15 | 2 | drone.py (14), tools/parser.py (1) |
| **Status Messages** | 18 | 3 | drone.py (13), tools/update.py (3) |
| **Sensor Error Codes** | 12 | 1 | drone.py |
| **Calibration/Diagnostics** | 6 | 1 | drone.py |
| **Progress/UI** | 8 | 3 | tools/update.py (2), swarm.py (5) |
| **Other** | 4 | 2 | (edge cases) |

---

## Detailed Category Breakdown

### 1. ERRORS/WARNINGS (24 statements)
**Purpose:** Connection errors, I/O errors, device detection failures  
**Primary File:** tools/parser.py (8 statements), tools/update.py (8 statements)

#### drone.py
- **Line 2127:** `"Serial device not available. Check the USB cable or USB port. . "`
- **Line 2134:** `"Could not connect to CoDrone EDU controller."`
- **Line 2165:** `"Could not connect to CoDrone EDU."`
- **Line 2209:** `"Serial device not available. Check the USB cable or USB port. . "`
- **Line 2216:** `"Could not connect to CoDrone EDU controller."`
- **Line 2247:** `"Could not connect to CoDrone EDU."`

#### tools/parser.py (8 occurrences)
- **Lines 203, 223, 235, 248, 261, 311, 358, 386:** All print `"* Error : Unable to open serial port."`

#### tools/update.py
- **Line 133:** `"* Error : Unable to open serial port."`
- **Line 157:** `"* Error : Could not detect device."`
- **Line 173:** `"Python firmware updater is not available. Use the web updater: https://codrone.robolink.com/edu/updater/"`
- **Line 179:** `"Python firmware updater not available for CoDrone EDU (JROTC ed.)"`
- **Line 197:** `"Python firmware updater not available for this device.)"`
- **Line 209:** `"Python firmware updater not available for this device.)"`
- **Line 249:** `"* Error : No response."`
- **Line 259:** `"* Error : Index Over."`
- **Line 285:** `"* Error : Firmware update is not available. Check that your device is in bootloader state."`

---

### 2. STATUS MESSAGES (18 statements)
**Purpose:** Device detection, connection confirmation, firmware version, battery status

#### drone.py (13 statements)
- **Line 264:** `Running {library_name} library version {library.version}` (colored)
- **Line 266:** `Running {0} library version {1}` (Emscripten version, color="green")
- **Line 2099:** `"Serial library not installed"`
- **Line 2110:** `Detected CoDrone EDU controller at port {portname}`
- **Line 2150:** `"Connected to CoDrone EDU (JROTC ed.)."`
- **Line 2152:** `"Connected to CoDrone EDU."`
- **Line 2155:** `Battery = {battery}%`
- **Line 2181:** `"Serial library not installed"`
- **Line 2192:** `Detected CoDrone EDU controller at port {portname}`
- **Line 2232:** `"Connected to CoDrone EDU (JROTC ed.)."`
- **Line 2234:** `"Connected to CoDrone EDU."`
- **Line 2237:** `Battery = {battery}%`
- **Line 2277:** `"Connected to CoDrone EDU (JROTC ed.)." (color="green")`
- **Line 2279:** `"Connected to CoDrone EDU." (color="green")`
- **Line 2283:** `Battery = {battery}%` (color="green")

#### swarm.py
- **Line 87:** `Running {library_name} library version {library}`

#### tools/update.py
- **Line 81:** `Version : {major}.{minor}.{build}`
- **Line 109:** `Version : {major}.{minor}.{build} ({version_v} / 0x{version_v:08X})`

---

### 3. CALIBRATION/DIAGNOSTICS (6 statements)
**Purpose:** Sensor calibration status, motion calibration states, diagnostics feedback

#### drone.py
- **Line 475:** `"Motion_Calibrating"`
- **Line 481:** `"Motion_NotCalibrated"`
- **Line 5669:** `"Warning: The 'drone.reset_sensor()' function is deprecated and will be removed in a future release.\nPlease use 'drone.reset_"` (Deprecation overlap)
- **Line 5671:** `"Warning: The 'drone.reset_sensor()' function is deprecated and will be removed in a future release.\nPlease use 'drone.reset_gyro()'"` (color="warning")
- **Line 5709:** `"Lay the drone on a flat surface & "`
- **Line 5718:** `"Done calibrating."`

---

### 4. SENSOR ERROR CODES (12 statements)
**Purpose:** Motion, pressure, range, optical flow sensor error codes

#### drone.py
- **Line 477:** `"Motion_NoAnswer"`
- **Line 479:** `"Motion_WrongValue"`
- **Line 483:** `"Pressure_NoAnswer"`
- **Line 485:** `"Pressure_WrongValue"`
- **Line 487:** `"RangeGround_NoAnswer"`
- **Line 489:** `"RangeGround_WrongValue"`
- **Line 491:** `"Flow_NoAnswer"`
- **Line 493:** `"Flow_CannotRecognizeGroundImage"`
- **Line 1053-1055:** `Flow_x() deprecation warning` (dual platform)
- **Line 1079-1081:** `Flow_y() deprecation warning` (dual platform)

---

### 5. STATE ERRORS (15 statements)
**Purpose:** Battery level, takeoff failures, propeller issues, attitude problems, flip validation

#### drone.py (14 statements)
- **Line 498:** `"Device not registered."`
- **Line 500:** `"Flash memory read lock not engaged."`
- **Line 502:** `"Bootloader write lock not engaged."`
- **Line 504:** `"Low battery."`
- **Line 506:** `"Takeoff failure. Check propeller and motor."`
- **Line 508:** `"Propeller vibration detected."`
- **Line 510:** `"Attitude not stable."`
- **Line 512:** `"Cannot flip. Battery below 50%."`
- **Line 514:** `"Cannot flip. Drone too heavy."`
- **Line 4464:** `"Warning: Unable to perform flip; battery level is below 50%."` (YELLOW)
- **Line 4480:** `"Invalid flip direction."`
- **Line 4489:** `"Warning: Unable to perform flip; battery level is below 50%."` (color="warning")
- **Line 4505:** `"Invalid flip direction."`
- **Line 4513:** `"Warning: Unable to perform flip; battery level is below 50%."` (color="warning")
- **Line 4529:** `"Invalid flip direction."`

#### tools/parser.py
- **Line 431:** `"takeoff"` (in command help text)

---

### 6. DEPRECATION WARNINGS (26 statements)
**Purpose:** API function deprecations with migration guidance

#### drone.py (26 statements)

**Temperature Functions:**
- **Line 665-667:** `get_temperature()` → use `get_drone_temperature()`

**Acceleration Functions:**
- **Line 1327-1329:** `get_x_accel()` → use `get_accel_x()`
- **Line 1346-1348:** `get_y_accel()` → use `get_accel_y()`
- **Line 1365-1367:** `get_z_accel()` → use `get_accel_z()`

**Angular Speed Functions:**
- **Line 1372-1374:** `get_x_gyro()` → use `get_angular_speed_x()`
- **Line 1391-1393:** `get_y_gyro()` → use `get_angular_speed_y()`
- **Line 1410-1412:** `get_z_gyro()` → use `get_angular_speed_z()`

**Angle Functions:**
- **Line 1441-1443:** `get_x_angle()` → use `get_angle_x()`
- **Line 1460-1462:** `get_y_angle()` → use `get_angle_y()`
- **Line 1479-1481:** `get_z_angle()` → use `get_angle_z()`

**Movement Functions:**
- **Line 2935-2937:** `reset_move()` → use `reset_move_values()`
- **Line 3039-3041:** `get_move()` → use `get_move_values()`

**Color Functions:**
- **Line 6400-6402:** `load_classifier()` → use `load_color_data()`

**Optical Flow Functions:**
- **Line 1053-1055:** `get_flow_x()` → use `get_flow_velocity_x()`
- **Line 1079-1081:** `get_flow_y()` → use `get_flow_velocity_y()`

---

### 7. UNIT CONVERSION WARNINGS (16 statements)
**Purpose:** Invalid unit parameter fallback notifications

#### drone.py (All dual-platform: Colorama + Emscripten versions)

**Pressure Unit:**
- **Line 573-575:** Invalid unit → fallback to `'Pa' (Pascals)`

**Elevation Unit:**
- **Line 609-611:** Invalid unit → fallback to `'m' (meters)`
- **Line 761-763:** Invalid unit → fallback to `'m' (meters)` (with period)

**Distance Unit:**
- **Line 789-791:** Invalid unit → fallback to `'mm' (millimeters)`

**Heading/Yaw Unit:**
- **Line 3350-3380:** `"Error: Not a valid unit."` (RED)
- **Line 3421-3451:** `"Error: Not a valid unit."` (RED)
- **Line 3492-3521:** `"Error: Not a valid unit."` (RED)
- **Line 3562-3591:** `"Error: Not a valid unit."` (RED)

---

### 8. PROGRESS/UI (8 statements)
**Purpose:** Firmware update progress, command help text, method resolution feedback

#### swarm.py
- **Line 59:** `"Warning: Invalid format. Valid formats are 'raw' and 'readable'."`
- **Line 132:** `"Method ", method_name, " not found"`
- **Line 338:** `"Method ", method_name, " not found"`
- **Line 419:** `"Warning: Invalid format. Valid formats are 'raw' and 'readable'."`
- **Line 513:** `"Warning: Invalid format. Valid formats are 'raw' and 'readable'."`

#### tools/parser.py
- **Line 413:** `"* Command List "`

#### tools/update.py
- **Line 127:** `"* Firmware loading."`
- **Line 266:** `"  Update Complete."`

---

### 9. OTHER (132 statements)
**Purpose:** Miscellaneous messages including command help text, data output, validation errors

#### drone.py (67 statements)
- Data display and logging (Lines 2553, 2556, 2563, 2566, 2572, 2578, 2584)
- Parameter validation errors (Lines 3632-3776, 6300+)
- Input validation (Lines 4937, 5016, 5099, 5115, 5145, 5175)
- Light mode validation (Lines 5313, 5343, 5413, 5443)
- Color classification (Line 6303, 6344)
- Dataset operations (Lines 6524, 6704, 6728)
- Image format validation (Lines 7733, 7777)
- Flight parameter conversion errors (Lines 84, 1718, 1753, 1987, 1989)
- Buzzer sequence validation (Lines 4848, 4869, 4896, 4917)
- Device type error (Line 2258)
- Pairing help links (Lines 2167, 2249)
- Help text and format display (Lines 2553+)

#### swarm.py (5 statements)
- Drone list display (Line 103)
- Swarm data output (Lines 62, 422, 516)
- Blank line formatting (Line 101)

#### tools/parser.py (47 statements)
- Extensive command help text with colored formatting (Lines 412-482)
- Data dump display (Lines 30, 33, 35, 488, 502, 516, 533)

#### tools/update.py (13 statements)
- Firmware header information display (Lines 78-83, 107-111)
- Update progress indicators (Line 283)
- Device information display (Lines 78-111)

---

## File-by-File Summary

### drone.py
**Total: 147 statements**
- Primary hub for print() usage
- Dense concentration of deprecation warnings (26 statements)
- Complete error reporting system
- Multi-platform support (Colorama + Emscripten)
- 67 "Other" statements mostly for parameter validation and help text

### tools/parser.py
**Total: 73 statements**
- Mostly "Other" category (47 statements)
- Command help system with extensive formatting
- 8 serial port error messages
- Data display and formatting

### drone.py (continued)
- **Connection:** 6 connection error messages (Lines 2127-2247)
- **Battery:** 5 battery-related messages (Lines 2099-2283)
- **Calibration:** 6 calibration messages (Lines 475-5718)
- **Sensors:** 8 sensor error codes (Lines 477-495)
- **State:** 14 state error messages (Lines 498-4529)

### swarm.py
**Total: 11 statements**
- Library initialization (Line 87)
- Format validation (Lines 59, 419, 513)
- Method resolution (Lines 132, 338)
- Data display (Lines 62, 103, 422, 516)
- Empty line placeholders (Line 101)

### tools/update.py
**Total: 26 statements**
- Firmware information display
- Serial port errors (2 statements)
- Device detection errors
- Progress indicators
- Update completion message

### Other Files (errors.py, receiver.py, storage.py, system.py, etc.)
**Total: 0 statements**
- No active print() statements found
- Suggest using logging framework instead

---

## Key Findings

### 1. Print Distribution
- **Dominated by drone.py** (147/257 = 57% of all prints)
- **Significant in tools/parser.py** (73/257 = 28%)
- **Minimal in swarm.py** (11/257 = 4%)
- **Zero in core infrastructure files** (errors.py, receiver.py, storage.py, system.py)

### 2. Dual-Platform Implementations
Many prints exist in pairs for platform compatibility:
- **Colorama version** (desktop): Uses `Fore.COLOR + message + Style.RESET_ALL`
- **Emscripten version** (web): Uses `message, color="color_name"` parameter

Examples:
- Lines 264/266 (library initialization)
- Lines 2150-2155, 2232-2237, 2277-2283 (connection status)
- 26 deprecation warning pairs across Lines 665-6402

### 3. Deprecation Wave
**26 total deprecation warnings** indicate planned API migration:
- Old accelerometer: `get_x/y/z_accel()` → `get_accel_x/y/z()`
- Old gyro: `get_x/y/z_gyro()` → `get_angular_speed_x/y/z()`
- Old angles: `get_x/y/z_angle()` → `get_angle_x/y/z()`
- Old flow: `get_flow_x/y()` → `get_flow_velocity_x/y()`
- Old movement: `reset_move()` / `get_move()` → `reset_move_values()` / `get_move_values()`
- Old classifier: `load_classifier()` → `load_color_data()`
- Old temperature: `get_temperature()` → `get_drone_temperature()`
- Old sensor calibration: `reset_sensor()` → `reset_gyro()`

### 4. Error Handling Patterns
- **Serial port errors**: 8 identical messages in parser.py (Lines 203, 223, 235, 248, 261, 311, 358, 386)
- **Connection failures**: 6 variations across Desktop/Web platforms
- **Unit conversion errors**: 16 statements with fallback defaults
- **State validation**: 15 state-specific error messages

### 5. Command Help System
- **tools/parser.py** contains extensive command documentation
- 47 statements dedicated to help text formatting
- Uses colorama for visual formatting
- Lists: Firmware upgrade, data requests, flight events, control, position, buzzer, vibrator, lights

### 6. Platform Support
- **Desktop/Colorama:** Primary implementation with colored output
- **Web/Emscripten:** Secondary implementation with `color` parameter
- **Non-platform code:** errors.py, receiver.py, storage.py, system.py have zero prints (best practice)

---

## Recommendations

### 1. Consolidate Redundant Serial Errors
8 identical "Unable to open serial port" messages in parser.py could be consolidated:
```python
# Instead of 8 separate prints, create a helper function:
def print_serial_error():
    print(Fore.RED + "* Error : Unable to open serial port." + Style.RESET_ALL)
```

### 2. Migrate Deprecation Warnings
26 deprecation warning pairs suggest migration is in progress. Consider:
- Setting deprecation flag in version 2.5
- Complete removal in version 3.0
- Centralize deprecation helper function

### 3. Implement Logging Framework
Files with zero prints (errors.py, receiver.py, storage.py, system.py) are good candidates for logging:
```python
import logging
logger = logging.getLogger(__name__)
logger.error("message")  # Better than print()
```

### 4. Standardize Unit Conversion Messages
16 unit conversion warnings across 4 locations could use a unified format:
```python
def handle_invalid_unit(invalid_unit, valid_units, default):
    print(f"Warning: '{invalid_unit}' is not valid. Valid units: {valid_units}. Using '{default}'.")
```

### 5. Unit Test Coverage
Current print statements in test scenarios should be redirected to logging:
- Lines 3037, 6527, 6704, 6728 suggest test/debug output
- Consider using debug-level logging instead

---

## Summary Statistics

| Metric | Value |
|--------|-------|
| **Total Print Statements** | 257 |
| **Files with Prints** | 4 |
| **Files without Prints** | 9 |
| **Largest File** | drone.py (147 statements) |
| **Most Common Category** | Other (132 statements) |
| **Deprecation Warnings** | 26 (10.1%) |
| **Error Messages** | 24 (9.3%) |
| **Platform Pairs** | ~40 (Colorama + Emscripten) |
| **Duplicate Serial Errors** | 8 instances |
| **Unique Error Types** | ~20 |

---

## Appendix: Category Glossary

1. **Errors/Warnings** - Connection failures, I/O issues, device detection problems
2. **Status Messages** - Device initialization, connection confirmation, battery status
3. **Calibration/Diagnostics** - Sensor calibration feedback, motion calibration state
4. **Sensor Error Codes** - Motion, pressure, range, optical flow sensor errors
5. **State Errors** - Battery level, takeoff, propeller, attitude, flip validation
6. **Deprecation Warnings** - Function/method deprecations with migration guidance
7. **Unit Conversion Warnings** - Invalid unit parameter notifications with fallbacks
8. **Progress/UI** - Firmware updates, command help, method resolution feedback
9. **Other** - Miscellaneous: parameter validation, data display, help text, image format checks

---

**Document Version:** 1.0  
**Last Updated:** November 15, 2025  
**Status:** Complete
