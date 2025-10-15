# API Comparison Report

**Generated:** 2025-10-15 (Updated against Official Python Documentation)
**Python Library:** 2.4 ([Official Docs](https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation))
**Java Library:** JCoDroneEdu 2.5+

## Summary

**Documentation Source:** Official Robolink Python API Documentation
- **Python Documented Methods:** ~150+ (excluding deprecated methods)
- **Java Methods:** 161+ (with new composite object methods)
- **Missing Core Methods:** ~30 (documented Python methods not in Java)
- **Deprecated/Low-Priority:** ~25 (marked deprecated in docs or internal)

## Progress Update (October 2025)

✅ **Major API Additions:**
- Inventory data access (InformationData, CountData, AddressData, CpuIdData)
- Controller input composite objects (ButtonData, JoystickData)
- Three-tier API pattern for better Java ergonomics while maintaining Python compatibility

🎯 **API Completeness (Based on Official Documentation):**
- **Connection/Setup**: 100% complete (pair, close)
- **Flight Commands**: 100% complete ✅ (avoid_wall, keep_distance NOW IMPLEMENTED)
- **Flight Variables**: 100% complete (move, set_pitch/roll/yaw/throttle)
- **LED Control**: 100% complete
- **Sound/Buzzer**: 100% complete ✅ (controller_buzzer_sequence, ping NOW IMPLEMENTED + extensible!)
- **Range Sensors**: 100% complete
- **Optical Flow**: 100% complete
- **Gyroscope/IMU**: 100% complete (including deprecated getters)
- **Pressure Sensors**: 100% complete ✅ (height_from_pressure NOW VERIFIED!)
- **Color Sensors**: ~40% complete (missing: classifier, calibration methods)
- **State Data**: ~80% complete (missing: get_error_data details)
- **Controller Input**: 100% complete
- **Screen/Display**: ~20% complete (controller canvas methods mostly missing)

📊 **Overall Completion: ~88% of documented Python API** (up from ~87%!)

## Methods in Python Docs but NOT in Java

These are methods from the official Python documentation that don't have Java equivalents.

## Detailed Category Breakdown

### Connection and Setup (100% ✅)
All implemented:
- ✅ `pair()` → `pair()` or `connect()`
- ✅ `close()` → `close()` or `disconnect()`
- ✅ `connect_bluetooth()` → Not needed (Android-specific)

### Flight Commands (100% ✅)
All implemented:
- ✅ `takeoff()` → `takeoff()`
- ✅ `land()` → `land()`
- ✅ `emergency_stop()` → `emergencyStop()`
- ✅ `hover()` → `hover()`
- ✅ `go()` → `go()`
- ✅ `get_trim()` → `getTrim()`
- ✅ `reset_trim()` → `resetTrim()`
- ✅ `set_trim()` → `setTrim()`
- ✅ `move_forward/backward/left/right()` → `moveForward/Backward/Left/Right()`
- ✅ `move_distance()` → `moveDistance()`
- ✅ `send_absolute_position()` → `sendAbsolutePosition()`
- ✅ `turn()` → `turn()`
- ✅ `turn_degree()` → `turnDegree()`
- ✅ `turn_left/right()` → `turnLeft/Right()`
- ✅ `avoid_wall(timeout, distance)` → `avoidWall(timeout, distance)` - Autonomous wall distance maintenance
- ✅ `keep_distance(timeout, distance)` → `keepDistance(timeout, distance)` - Autonomous object distance tracking

### Flight Sequences (100% ✅)
All implemented:
- ✅ `circle()` → `circle()`
- ✅ `flip()` → `flip()`
- ✅ `spiral()` → `spiral()`
- ✅ `square()` → `square()`
- ✅ `sway()` → `sway()`
- ✅ `triangle()` → `triangle()`

### Flight Variables (100% ✅)
All implemented:
- ✅ `get_move_values()` → `getMoveValues()`
- ✅ `move()` → `move()`
- ✅ `reset_move_values()` → `resetMoveValues()`
- ✅ `set_pitch()` → `setPitch()`
- ✅ `set_roll()` → `setRoll()`
- ✅ `set_yaw()` → `setYaw()`
- ✅ `set_throttle()` → `setThrottle()`

### LED Control (100% ✅)
All implemented:
- ✅ `controller_LED_off()` → `controllerLEDOff()`
- ✅ `drone_LED_off()` → `droneLEDOff()`
- ✅ `set_controller_LED()` → `setControllerLED()`
- ✅ `set_drone_LED()` → `setDroneLED()`
- ✅ `set_controller_LED_mode()` → `setControllerLEDMode()`
- ✅ `set_drone_LED_mode()` → `setDroneLEDMode()`

### Sounds/Buzzer (100% ✅)
All implemented:
- ✅ `controller_buzzer()` → `controller_buzzer()`
- ✅ `drone_buzzer()` → `drone_buzzer()`
- ✅ `start_controller_buzzer()` → `start_controller_buzzer()`
- ✅ `start_drone_buzzer()` → `start_drone_buzzer()`
- ✅ `stop_controller_buzzer()` → `stop_controller_buzzer()`
- ✅ `stop_drone_buzzer()` → `stop_drone_buzzer()`
- ✅ `drone_buzzer_sequence(kind)` → `droneBuzzerSequence(String)` + extensible registry
- ✅ `controller_buzzer_sequence(kind)` → `controllerBuzzerSequence(String)` + extensible registry
- ✅ `ping(r, g, b)` → `ping(Integer, Integer, Integer)` + `ping()` overload

**Bonus:** Java adds `registerBuzzerSequence(String, BuzzerSequence)` for custom sequences!

### Range Sensors (100% ✅)
All implemented:
- ✅ `detect_wall()` → `detectWall()`
- ✅ `get_bottom_range()` → `getBottomRange()`
- ✅ `get_front_range()` → `getFrontRange()`
- ✅ `get_height()` → `getHeight()`

### Optical Flow Sensors (100% ✅)
All implemented:
- ✅ `get_pos_x/y/z()` → `getPositionX/Y/Z()` or `getPosX/Y/Z()`
- ✅ `get_flow_velocity_x/y()` → `getFlowVelocityX/Y()`
- ✅ `get_flow_x/y()` → `getFlowX/Y()` (deprecated but supported)
- ✅ `get_position_data()` → `getPositionData()`

### Gyroscope/IMU Sensors (100% ✅)
All implemented (including deprecated methods):
- ✅ `get_accel_x/y/z()` → `getAccelX/Y/Z()`
- ✅ `get_angle_x/y/z()` → `getAngleX/Y/Z()`
- ✅ `get_angular_speed_x/y/z()` → `getAngularSpeedX/Y/Z()`
- ✅ `get_x/y/z_accel()` → Deprecated in Python, supported via `getAccelX/Y/Z()`
- ✅ `get_x/y/z_angle()` → Deprecated in Python, supported via `getAngleX/Y/Z()`
- ✅ `reset_gyro()` → `resetGyro()`
- ✅ `reset_sensor()` → Deprecated in Python, supported via `resetGyro()`

### Pressure Sensors (100% ✅)
All implemented:
- ✅ `get_drone_temperature()` → `getBarometerTemperature()` + calibrated methods
- ✅ `get_temperature()` → Deprecated, use `getBarometerTemperature()`
- ✅ `get_elevation()` → `getElevation()`, `getCorrectedElevation()`, `getUncorrectedElevation()`
- ✅ `get_pressure()` → `getPressure()`
- ✅ `set_initial_pressure()` → `setInitialPressure()`
- ✅ `height_from_pressure(b, m)` → `getHeightFromPressure(double b, double m)` + `getHeightFromPressure()` overload

### Color Sensors (40% - Missing 6 methods)
Implemented:
- ✅ `get_back_color()` → `getBackColor()`
- ✅ `get_color_data()` → `getColorData()`
- ✅ `get_colors()` → `getColors()`
- ✅ `get_front_color()` → `getFrontColor()`

Missing:
- ❌ `append_color_data(color, data, dataset)` - Custom color training (⚠️ unavailable in Python for Robolink)
- ❌ `load_classifier(dataset, show_graph)` - Load custom color set (⚠️ unavailable in Python for Robolink)
- ❌ `load_color_data(dataset)` - Load color data (⚠️ unavailable in Python for Robolink)
- ❌ `new_color_data(color, data, dataset)` - Create new color data
- ❌ `predict_colors(color_data)` - Predict colors from data

Note: Many color sensor training methods are marked as "currently unavailable for Python for Robolink" in official docs.

### State/Status Data (80% - Missing 1 method)
Implemented:
- ✅ `get_battery()` → `getBattery()`
- ✅ `get_flight_state()` → `getFlightState()`
- ✅ `get_movement_state()` → `getMovementState()`
- ✅ `get_sensor_data()` → `getSensorData()` (comprehensive 31-element array)

Missing:
- ❌ `get_error_data()` - Returns error state enum (Motion calibration, low battery, etc.)

### Controller Input (100% ✅)
All implemented with three-tier API:
- ✅ `get_button_data()` → `getButtonData()` (array) + `getButtonDataObject()` (composite)
- ✅ `get_joystick_data()` → `getJoystickData()` (array) + `getJoystickDataObject()` (composite)
- ✅ `get_left_joystick_x/y()` → `getLeftJoystickX/Y()`
- ✅ `get_right_joystick_x/y()` → `getRightJoystickX/Y()`
- ✅ Button pressed methods (all buttons: L1, L2, R1, R2, arrows, H, P, S, power)

### Controller Display/Screen (20% - Missing 11 methods)
Implemented:
- ✅ `controller_clear_screen()` → `controllerClearScreen()` (⚠️ unavailable for JROTC edition)
- ✅ `controller_draw_line()` → `controllerDrawLine()`
- ✅ `controller_draw_rectangle()` → `controllerDrawRectangle()`
- ✅ `controller_draw_string()` → `controllerDrawString()`

Missing (⚠️ Most unavailable for JROTC edition):
- ❌ `controller_create_canvas()` - Create image object for drawing
- ❌ `controller_draw_arc()` - Draw arc on canvas
- ❌ `controller_draw_canvas()` - Render canvas to screen
- ❌ `controller_draw_chord()` - Draw chord on canvas
- ❌ `controller_draw_ellipse()` - Draw ellipse on canvas
- ❌ `controller_draw_image()` - Draw image on screen
- ❌ `controller_draw_polygon()` - Draw polygon on canvas
- ❌ `controller_draw_square()` - Draw square on canvas
- ❌ `controller_draw_string_align()` - Draw aligned text
- ❌ `controller_preview_canvas()` - Preview canvas in window
- ❌ `get_image_data(image_file)` - Load and resize image (⚠️ unavailable for Python for Robolink)

Note: Many controller canvas methods are unavailable for JROTC edition hardware.

## Deprecated Python Methods (Documented but Deprecated)

These methods are documented but marked as deprecated in the official Python docs. Java equivalents use the newer naming:

- `get_temperature()` → Use `get_drone_temperature()` / Java: `getBarometerTemperature()`
- `get_x/y/z_accel()` → Use `get_accel_x/y/z()` / Java: `getAccelX/Y/Z()`
- `get_x/y/z_angle()` → Use `get_angle_x/y/z()` / Java: `getAngleX/Y/Z()`
- `get_flow_x/y()` → Use `get_flow_velocity_x/y()` / Java: `getFlowVelocityX/Y()`
- `reset_sensor()` → Use `reset_gyro()` / Java: `resetGyro()`

## Methods NOT in Official Python Docs (Internal/Low-Level)

These were in the Python source code but are NOT in the official documentation, suggesting they are internal or low-level:

### Protocol/Internal Methods
- `sendCommand*()` methods - Low-level protocol
- `update_*()` methods - Internal updates
- `receive_*()` methods - Internal protocol
- `makeTransferDataArray()` - Internal serialization
- `getData()`, `getHeader()` - Low-level packet methods
- `dummy_function()` - Placeholder
- `j()` - Unknown/debug

### Utility/Conversion Methods
- `convert_meter()`, `convert_millimeter()` - Unit conversion helpers
- `temperature_convert()` - Temperature conversion
- `percent_error()` - Math utility
- `format_firmware_version()` - String formatting

These are expected to be different in Java (better abstractions, different architecture).

## Methods in Java but NOT in Python Docs

## Methods in Java but NOT in Python Docs

ℹ️ Java-specific methods (expected and designed for better Java ergonomics):

### Connection/Setup
- `autoConnect()` - Automatic port detection (Java convenience)
- `autoConnect(String portName)` - Automatic with hint (Java convenience)

### Flight Control
- `clearBias()` - Reset IMU bias (was `sendClearBias()` in Python internals)
- `clearTrim()` - Reset trim (was `sendClearTrim()` in Python internals)
- `changeSpeed()` - Change flight speed (Java-specific)

### LED Control (Convenience Methods)
- `setDroneLEDRed/Green/Blue/Yellow/Orange/Purple/White()` - Color shortcuts

### Data Access (Enhanced Java APIs)
- `getAccel()` - Get all accelerations at once (object vs individual getters)
- `getAngle()` - Get all angles at once (object vs individual getters)
- `getGyro()` - Get all gyro values at once (object vs individual getters)
- `getBackRange()`, `getLeftRange()`, `getRightRange()`, `getTopRange()` - Individual range getters
- `getPositionX/Y/Z()` - Alternative to `getPosX/Y/Z()`

### Elevation/Altitude (Enhanced API)
- `getCalculatedAltitude()` - Computed altitude from barometer + calibration
- `getCorrectedElevation()` - Weather-corrected elevation
- `getUncorrectedElevation()` - Raw barometer elevation
- `getHeightFromPressure()` - Height calculation from pressure (alternative to Python `height_from_pressure()`)
- `useCorrectedElevation()` - Enable/disable weather correction

### Component Access (Java Architecture)
- `getDroneStatus()` - Access DroneStatus manager
- `getFlightController()` - Access FlightController
- `getLinkController()` - Access LinkController
- `getLinkManager()` - Access LinkManager
- `getInventoryManager()` - Access InventoryManager
- `getControllerInputManager()` - Access ControllerInputManager
- `getReceiver()` - Access Receiver
- `getSettingsController()` - Access SettingsController

### Controller Display (Java Extensions)
- `controller_clear_area()` - Clear specific area
- `controller_draw_circle()` - Draw circle (vs ellipse)
- `controller_draw_point()` - Draw single pixel
- `controller_invert_area()` - Invert screen area

### Color Sensor
- `loadColorClassifier()` - Load color classifier (vs Python `load_classifier()`)
- `unloadColorClassifier()` - Unload classifier

### Settings/Configuration
- `setBacklight()` - Set controller backlight
- `setControllerMode()` - Set controller mode (vs Python `sendControlleLinkMode()`)
- `setDefault()` - Reset to defaults (was `sendSetDefault()` in Python)
- `setHeadlessMode()` - Set headless mode (was `sendHeadless()` in Python)
- `setLinkMode()` - Set link mode

### Flight Events
- `triggerFlightEvent()` - Trigger flight event (was `sendFlightEvent()` in Python)

### Data Updates (Java Architecture)
- `updateButtonData()` - Update button state (Java internal)
- `updateJoystickData()` - Update joystick state (Java internal)

### Composite Data Objects (Java Best Practice)
- `getButtonDataObject()` - Type-safe ButtonData composite
- `getJoystickDataObject()` - Type-safe JoystickData composite
- `getInformationDataObject()` - Type-safe InformationData composite
- `getCountDataObject()` - Type-safe CountData composite
- `getCpuIdDataObject()` - Type-safe CpuIdData composite
- `getAddressDataObject()` - Type-safe AddressData composite

## Summary of Key Differences

### What's Missing from Java (Priority to Implement):
1. **High Priority (Core Features):**
   - `avoid_wall()` - Autonomous navigation
   - `keep_distance()` - Autonomous navigation
   - `controller_buzzer_sequence()` - Sound sequences
   - `ping()` - Find drone feature
   - `get_error_data()` - Error state reporting

2. **Medium Priority (Color Sensor):**
   - Color classifier training/prediction methods (if not hardware-limited)

3. **Low Priority (Controller Display):**
   - Canvas/drawing methods (unavailable for JROTC hardware anyway)

### What Java Has That Python Doesn't:
1. **Component Architecture:** Manager objects (DroneStatus, LinkManager, InventoryManager, etc.)
2. **Type-Safe Composites:** ButtonData, JoystickData, InformationData, etc.
3. **Three-Tier API:** Arrays (Python compat) + Individual getters + Composite objects
4. **Enhanced Elevation:** Weather-corrected altitude calculations
5. **Convenience Methods:** Color LED shortcuts, auto-connect, etc.

### Deprecated Methods:
Java implements both old and new versions where Python deprecated methods:
- `getFlowX/Y()` and `getFlowVelocityX/Y()` ✅
- `getAccelX/Y/Z()` (replaces `getXAccel()` etc.) ✅
- `resetGyro()` (replaces `resetSensor()`) ✅

## ⚠️ Important: Inventory Management (✅ Complete)

All inventory methods IMPLEMENTED with three-tier API:
- ✅ `get_information_data()` → `getInformationData()` (array) + `getInformationDataObject()` (object)
- ✅ `get_cpu_id_data()` → `getCpuIdData()` (array) + `getCpuIdDataObject()` (object)
- ✅ `get_address_data()` → `getAddressData()` (array) + `getAddressDataObject()` (object)
- ✅ `get_count_data()` → `getCountData()` (array) + `getCountDataObject()` (object)
- ✅ Individual getters: `getFlightTime()`, `getTakeoffCount()`, etc.

## ⚠️ Important: Controller Input (✅ Complete)

All controller input methods IMPLEMENTED with three-tier API:
- ✅ `get_button_data()` → `getButtonData()` (array) + `getButtonDataObject()` (ButtonData)
- ✅ `get_joystick_data()` → `getJoystickData()` (array) + `getJoystickDataObject()` (JoystickData)
- ✅ Individual getters: `getLeftJoystickX/Y()`, `getRightJoystickX/Y()`, `getButtonFlags()`, etc.
- ✅ All button pressed methods: `l1Pressed()`, `l2Pressed()`, etc.

## Recent Implementations (October 2025)

### InventoryManager with Three-Tier API
Implemented comprehensive inventory data access following the three-tier pattern:
1. **Python-compatible arrays**: `getInformationData()`, `getCpuIdData()`, etc.
2. **Individual typed getters**: `getDroneModel()`, `getFlightTime()`, etc.
3. **Java composite objects**: `getInformationDataObject()`, `getCountDataObject()`, etc.

**Benefits:**
- Python API compatibility maintained
- Type-safe Java objects for better IDE support
- Cleaner code (no array indexing)
- Full JavaDoc documentation

### ControllerInputManager with Three-Tier API
Refactored controller input to manager pattern with named fields:
1. **Python-compatible arrays**: `getButtonData()`, `getJoystickData()`
2. **Individual typed getters**: `getLeftJoystickX()`, `getRightJoystickY()`, `getButtonFlags()`, etc.
3. **Java composite objects**: `getButtonDataObject()`, `getJoystickDataObject()`

**Benefits:**
- Eliminated type casting and null checks in GUI code
- ButtonData and JoystickData immutable composites
- Hardware limitation documented (multiplexed button matrix)

**Documentation:**
- `INVENTORY_METHODS_IMPLEMENTATION.md` - Inventory API guide
- `INVENTORY_DATA_ACCESS_PATTERNS.md` - Access pattern reference
- `CONTROLLER_INPUT_REFACTORING.md` - Controller input migration guide
- `BEST_PRACTICE_GUIDANCE.md` - Overall API best practices

## Recommendations

### For Students/Teachers:
1. **Use composite objects** when available (`getButtonDataObject()`, `getInformationDataObject()`)
2. **Avoid array indexing** - use individual getters instead
3. **Check documentation** - many "missing" Python methods have Java alternatives

### For Development:
1. **Implement high-priority missing methods** first: `avoid_wall()`, `keep_distance()`, `ping()`
2. **Document hardware limitations** (e.g., JROTC controller display unavailable)
3. **Maintain three-tier pattern** for new data access methods
4. **Keep Python compatibility** where reasonable

