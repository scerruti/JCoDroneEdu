# AGENT INSTRUCTIONS: Add @pythonEquivalent and @pythonReference Annotations

**Task**: Add `@pythonEquivalent` and `@pythonReference` annotations to all methods in `Drone.java` that have Python equivalents.

**Reference Documents**:
- `PYTHON_EQUIVALENT_AUDIT.md` - Contains URL mappings and implementation format
- `API_COMPARISON.md` - Master list of all Python ↔ Java method mappings
- Target file: `src/main/java/com/otabi/jcodroneedu/Drone.java`

---

## PHASE 1: Enhancement of Existing Annotations

**Objective**: Add `@pythonReference` tags to the 15 methods that already have `@pythonEquivalent`

### Existing Annotations to Enhance (15 total)

All located in `Drone.java`. For each, add a `@pythonReference` tag with the provided URL.

#### Movement Methods (8)

1. **Method**: `moveForward(double distance)`
   - Current: `@pythonEquivalent move_forward(distance)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move_forward`

2. **Method**: `moveForward(double distance, String units)`
   - Current: `@pythonEquivalent move_forward(distance, units)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move_forward`

3. **Method**: `moveBackward(double distance)`
   - Current: `@pythonEquivalent move_backward(distance)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move_backward`

4. **Method**: `moveBackward(double distance, String units)`
   - Current: `@pythonEquivalent move_backward(distance, units)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move_backward`

5. **Method**: `moveLeft(double distance)`
   - Current: `@pythonEquivalent move_left(distance)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move_left`

6. **Method**: `moveLeft(double distance, String units)`
   - Current: `@pythonEquivalent move_left(distance, units)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move_left`

7. **Method**: `moveRight(double distance)`
   - Current: `@pythonEquivalent move_right(distance)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move_right`

8. **Method**: `moveRight(double distance, String units)`
   - Current: `@pythonEquivalent move_right(distance, units)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#move_right`

#### Autonomous Methods (2)

9. **Method**: `avoidWall(int timeout, int distance)`
   - Current: `@pythonEquivalent avoid_wall(timeout, distance)`
   - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#avoid_wall`

10. **Method**: `keepDistance(int timeout, int distance)`
    - Current: `@pythonEquivalent keep_distance(timeout, distance)`
    - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#keep_distance`

#### Sound/Buzzer Methods (3)

11. **Method**: `ping(Integer red, Integer green, Integer blue)`
    - Current: `@pythonEquivalent ping(r, g, b)`
    - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#ping`

12. **Method**: `ping()`
    - Current: `@pythonEquivalent ping()`
    - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#ping`

13. **Method**: `droneBuzzerSequence(String kind)`
    - Current: `@pythonEquivalent drone_buzzer_sequence(kind)`
    - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#drone_buzzer_sequence`

14. **Method**: `controllerBuzzerSequence(String kind)`
    - Current: `@pythonEquivalent controller_buzzer_sequence(kind)`
    - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#controller_buzzer_sequence`

#### Class Documentation (1)

15. **File**: `src/main/java/com/otabi/jcodroneedu/buzzer/BuzzerSequence.java`
    - Current: `@pythonEquivalent controller_buzzer_sequence(kind), drone_buzzer_sequence(kind)`
    - Add: `@pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#drone_buzzer_sequence`

### Format for Phase 1 Updates

Add the `@pythonReference` tag on a new line after `@pythonEquivalent`:

```java
/**
 * [Existing description]
 * 
 * @param ... [existing params]
 * @see ... [existing cross-refs]
 * @educational [if present]
 * @pythonEquivalent method_name(args)
 * @pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#anchor
 */
public void methodName(...)
```

---

## PHASE 2: Add New Annotations to All Aligned Methods

**Objective**: Add both `@pythonEquivalent` AND `@pythonReference` annotations to all other methods in Drone.java that have Python equivalents but lack annotations.

### Mapping: Complete List of Methods Requiring Annotations

**Base URL for all**: `https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation`

#### Connection & Setup (4 methods)
```
pair()                              → pair                       (#pair)
pair(String portName)               → pair                       (#pair)
connect()                           → connect_bluetooth          (#connect_bluetooth)
connect(String portName)            → connect_bluetooth          (#connect_bluetooth)
close()                             → close                      (#close)
disconnect()                        → close                      (#close)
```

#### Flight Commands - Basic (12 methods)
```
takeoff()                           → takeoff                    (#takeoff)
land()                              → land                       (#land)
emergencyStop()                     → emergency_stop             (#emergency_stop)
hover()                             → hover                      (#hover)
hover(double durationSeconds)       → hover                      (#hover)
resetMoveValues()                   → reset_move_values          (#reset_move_values)
go(String direction, int power, double duration)     → go        (#go)
go(int pitch, int roll, int yaw, int throttle, double duration) → go (#go)
```

#### Flight Variables (7 methods)
```
getMoveValues()                     → get_move_values            (#get_move_values)
get_move_values()                   → get_move_values            (#get_move_values)  [deprecated alias]
move()                              → move                       (#move)
move(double duration)               → move                       (#move)
setPitch(int pitch)                 → set_pitch                  (#set_pitch)
setRoll(int roll)                   → set_roll                   (#set_roll)
setYaw(int yaw)                     → set_yaw                    (#set_yaw)
setThrottle(int throttle)           → set_throttle              (#set_throttle)
```

#### Flight Sequences (6 methods)
```
circle()                            → circle                     (#circle)
flip()                              → flip                       (#flip)
spiral()                            → spiral                     (#spiral)
square()                            → square                     (#square)
sway()                              → sway                       (#sway)
triangle()                          → triangle                   (#triangle)
```

#### Turning (4 methods)
```
turn(int power, double seconds)     → turn                       (#turn)
turnLeft(int power, double seconds) → turn_left                  (#turn_left)
turnRight(int power, double seconds) → turn_right               (#turn_right)
turnDegree(int yaw)                 → turn_degree               (#turn_degree)
```

#### LED Control - Core (6 methods)
```
droneLEDOff()                       → drone_LED_off              (#drone_led_off)
controllerLEDOff()                  → controller_LED_off         (#controller_led_off)
setDroneLED(int r, int g, int b)   → set_drone_LED              (#set_drone_led)
setControllerLED(int r, int g, int b) → set_controller_LED      (#set_controller_led)
setDroneLEDMode(int r, int g, int b, String mode, int speed) → set_drone_LED_mode (#set_drone_led_mode)
setControllerLEDMode(int r, int g, int b, String mode, int speed) → set_controller_LED_mode (#set_controller_led_mode)
```

#### LED Control - Convenience Color Methods (14 methods)
```
setDroneLEDRed()                    → set_drone_LED              (#set_drone_led)
setDroneLEDGreen()                  → set_drone_LED              (#set_drone_led)
setDroneLEDBlue()                   → set_drone_LED              (#set_drone_led)
setDroneLEDYellow()                 → set_drone_LED              (#set_drone_led)
setDroneLEDOrange()                 → set_drone_LED              (#set_drone_led)
setDroneLEDPurple()                 → set_drone_LED              (#set_drone_led)
setDroneLEDWhite()                  → set_drone_LED              (#set_drone_led)
setControllerLEDRed()               → set_controller_LED         (#set_controller_led)
setControllerLEDGreen()             → set_controller_LED         (#set_controller_led)
setControllerLEDBlue()              → set_controller_LED         (#set_controller_led)
setControllerLEDYellow()            → set_controller_LED         (#set_controller_led)
setControllerLEDOrange()            → set_controller_LED         (#set_controller_led)
setControllerLEDPurple()            → set_controller_LED         (#set_controller_led)
setControllerLEDWhite()             → set_controller_LED         (#set_controller_led)
```

#### Buzzer/Sound - Core (4 methods)
```
droneBuzzer(Object note, int duration) → drone_buzzer           (#drone_buzzer)
controllerBuzzer(Object note, int duration) → controller_buzzer (#controller_buzzer)
droneBuzzerMute()                   → drone_buzzer_mute          (#drone_buzzer_mute)
controllerBuzzerMute()              → controller_buzzer_mute     (#controller_buzzer_mute)
```

#### Buzzer - Pattern Methods (2 methods)
```
droneBuzzerSequence(String kind)    → drone_buzzer_sequence     (#drone_buzzer_sequence)
controllerBuzzerSequence(String kind) → controller_buzzer_sequence (#controller_buzzer_sequence)
```

#### Range Sensors (6 methods)
```
getFrontRange()                     → get_front_range            (#get_front_range)
getBackRange()                      → get_back_range             (#get_back_range)
getLeftRange()                      → get_left_range             (#get_left_range)
getRightRange()                     → get_right_range            (#get_right_range)
getTopRange()                       → get_top_range              (#get_top_range)
getBottomRange()                    → get_bottom_range / get_height (#get_bottom_range)
```

#### IMU/Gyro (6 methods)
```
getAccelX()                         → get_accel_x                (#get_accel_x)
getAccelY()                         → get_accel_y                (#get_accel_y)
getAccelZ()                         → get_accel_z                (#get_accel_z)
getGyroX()                          → get_angular_speed_x        (#get_angular_speed_x)
getGyroY()                          → get_angular_speed_y        (#get_angular_speed_y)
getGyroZ()                          → get_angular_speed_z        (#get_angular_speed_z)
```

#### Angle/Orientation (3 methods)
```
getAngleX()                         → get_angle_x                (#get_angle_x)
getAngleY()                         → get_angle_y                (#get_angle_y)
getAngleZ()                         → get_angle_z                (#get_angle_z)
```

#### Pressure/Altitude (8 methods)
```
getPressure()                       → get_pressure               (#get_pressure)
getPressure(String unit)            → get_pressure               (#get_pressure)
getHeight()                         → get_height                 (#get_height)
getHeight(String unit)              → get_height                 (#get_height)
getElevation()                      → get_elevation              (#get_elevation)
getElevation(String unit)           → get_elevation              (#get_elevation)
setInitialPressure()                → set_initial_pressure       (#set_initial_pressure)
getHeightFromPressure()             → height_from_pressure       (#height_from_pressure)
getHeightFromPressure(double b, double m) → height_from_pressure (#height_from_pressure)
```

#### Temperature (2 methods)
```
getBarometerTemperature()           → get_drone_temperature      (#get_drone_temperature)
getBarometerTemperature(String unit) → get_drone_temperature    (#get_drone_temperature)
```

#### IMU Calibration (2 methods)
```
resetGyro()                         → reset_gyro                 (#reset_gyro)
clearBias()                         → clear_bias                 (#clear_bias)
```

#### Trim Methods (3 methods)
```
getTrim()                           → get_trim                   (#get_trim)
setTrim(int roll, int pitch)        → set_trim                   (#set_trim)
resetTrim()                         → reset_trim                 (#reset_trim)
```

#### Trim Deprecated Aliases (1 method)
```
clearTrim()                         → reset_trim                 (#reset_trim)  [Java convenience; maps to Python reset_trim]
```

#### Motor/Speed Control (1 method)
```
changeSpeed(int speedLevel)         → (internal motor control - no direct Python equivalent, but Python capable) [SKIP ANNOTATION]
```

#### Color Sensor (4 methods)
```
getFrontColor()                     → get_front_color            (#get_front_color)
getBackColor()                      → get_back_color             (#get_back_color)
getColorValues()                    → get_colors                 (#get_colors)
getColorData()                      → get_color_data             (#get_color_data)
```

#### Optical Flow (2 methods)
```
getFlowVelocityX()                  → get_flow_velocity_x        (#get_flow_velocity_x)
getFlowVelocityY()                  → get_flow_velocity_y        (#get_flow_velocity_y)
```

#### State/Status (3 methods)
```
isConnected()                       → (connection status check - maps to state monitoring)    [Use: #get_state or connection_status - VERIFY]
isOpen()                            → (connection state check - same as isConnected)         [Use: connection_status - VERIFY]
getElapsedTime()                    → get_elapsed_time           (#get_elapsed_time)  [if exists in Python]
```

#### Inventory/Info Methods (5 methods)
```
getInformationData()                → get_information_data       (#get_information_data)
getCountData()                      → get_count_data             (#get_count_data)
getAddressData()                    → get_address_data           (#get_address_data)
getCpuIdData()                      → get_cpu_id_data            (#get_cpu_id_data)
getButtonData()                     → get_button_data            (#get_button_data)
```

#### Error/State Monitoring (1 method)
```
getErrorData()                      → get_error_data             (#get_error_data)
```

#### Display/Screen (5 methods - if implemented)
```
displayText(int x, int y, String text) → controller_display_text  (#controller_display_text)  [if available]
clearDisplay()                      → controller_clear_display   (#controller_clear_display)  [if available]
```

---

## PHASE 3: Handle Deprecated Methods

Some methods are deprecated in Python. For these, add both tags PLUS note the deprecation:

```java
/**
 * [Description]
 * 
 * @deprecated Use getTemperature() instead - this matches deprecated Python method
 * @see #getBarometerTemperature()
 * @pythonEquivalent get_temperature()
 * @pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#get_temperature
 */
@Deprecated
public double getTemperature()
```

**Deprecated Python methods to handle**:
```
getTemperature()                    → get_temperature [deprecated, use getDroneTemperature]
getTemperature(String unit)         → get_temperature [deprecated, use getDroneTemperature]  
get_move_values()                   → get_move_values [legacy, use getMoveValues]
```

---

## CRITICAL NOTES FOR AGENT

### 1. **URL Consistency**
- All URLs use this base: `https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation`
- Anchors are the Python method name in snake_case preceded by `#`
- Example: `#move_forward`, `#drone_buzzer_sequence`, `#get_accel_x`

### 2. **Overloaded Methods**
- Methods with multiple overloads (e.g., `moveForward(distance)` and `moveForward(distance, units)`) typically point to **the same Python anchor**
- Example: Both `setPitch(int)` overloads point to `#set_pitch`
- See existing 15 annotations for examples

### 3. **Convenience Methods**
- Color helper methods (`setDroneLEDRed()`, etc.) map to base method (`set_drone_LED`)
- Range getters (individual directions) map to their respective base methods (`get_front_range`, etc.)
- These are Java conveniences with Python equivalents

### 4. **Skip These Methods** (No Python Equivalent)
- `loadColorClassifier()`, `unloadColorClassifier()` - Java-specific implementation
- `autoConnect()` - Java convenience, no Python equivalent
- `sendRequest()`, `sendRequestWait()` - Internal protocol methods
- `printMoveValues()` - Debug helper
- Constructor overloads - Covered by `pair()` / `connect()`
- Getters like `getFlightController()`, `getDroneStatus()` - Return Java objects, no Python equivalent
- `setLinkMode()`, `setControllerMode()`, `triggerFlightEvent()` - Internal/specialized
- `setHeadlessMode()`, `setBacklight()` - Controller-specific configs
- `changeSpeed()` - Internal motor control
- `clearCounter()` - Utility

### 5. **Verification Checklist**
Before completing, verify:
- ✅ All 15 Phase 1 annotations enhanced with `@pythonReference`
- ✅ All Phase 2 methods have BOTH `@pythonEquivalent` AND `@pythonReference`
- ✅ No broken URLs (all follow pattern with valid anchors)
- ✅ Javadoc still generates without warnings
- ✅ Format is consistent: tag on its own line, URL complete

### 6. **Testing**
```bash
# Verify annotations added correctly
grep -c "@pythonEquivalent" src/main/java/com/otabi/jcodroneedu/Drone.java
grep -c "@pythonReference" src/main/java/com/otabi/jcodroneedu/Drone.java

# Generate Javadoc to check for issues
./gradlew javadoc

# Look for any broken links (manual check in generated docs)
grep -r "pythonReference" build/docs/javadoc/
```

### 7. **Commit Strategy**
- Phase 1: Commit separately - "Enhance existing @pythonEquivalent annotations with robolink references"
- Phase 2: Commit separately - "Add @pythonEquivalent and @pythonReference to all aligned methods"
- Phase 3: Commit separately - "Handle deprecated method annotations"

---

## SUMMARY

| Phase | Task | Count | Status |
|-------|------|-------|--------|
| 1 | Add @pythonReference to existing | 15 | Ready |
| 2 | Add both tags to aligned methods | ~90 | Ready |
| 3 | Handle deprecated methods | ~3 | Ready |
| **TOTAL** | **All aligned methods documented** | **~108** | **Ready for Agent** |

**Expected Outcome**: Every method in Drone.java with a Python equivalent will have clickable documentation links to the official Robolink Python API reference.
