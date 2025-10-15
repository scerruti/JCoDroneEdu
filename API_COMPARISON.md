# API Comparison Report

**Generated:** 2025-10-15 (Updated after InventoryManager & ControllerInputManager implementation)
**Python Library:** 2.4
**Java Library:** JCoDroneEdu 2.5+

## Summary

- **Python Methods:** 263
- **Java Methods:** 161+ (with new composite object methods)
- **Critical Missing Methods:** ~25 (down from 151)
- **Low-Priority/Internal Methods:** ~125 (alternative APIs exist or low-level)

## Progress Update (October 2025)

✅ **Major API Additions:**
- Inventory data access (InformationData, CountData, AddressData, CpuIdData)
- Controller input composite objects (ButtonData, JoystickData)
- Three-tier API pattern for better Java ergonomics while maintaining Python compatibility

🎯 **API Completeness:**
- **Core functionality**: ~95% complete
- **Data access**: ~90% complete (with improved Java APIs)
- **Advanced features** (AI, color detection, waypoints): ~30% complete
- **Internal/protocol methods**: Intentionally different (better abstraction in Java)

## Methods in Python but NOT in Java

⚠️ Consider implementing these methods:

**Note:** Several previously missing methods have been implemented (see "Recent Implementations" section below).

### Still Missing - Core Methods
- `avoid_wall()`
- `check()`
- `checkDetail()`
- `detect_colors()`
- `fit()`
- `goto_waypoint()`
- `keep_distance()`
- `load_classifier()`
- `load_color_data()`
- `load_color_data_without_print()`
- `open()`
- `ping()`
- `predict()`
- `predict_colors()`
- `reset()`
- `reset_classifier()`
- `reset_move()`
- `reset_previous_land()`
- `reset_sensor()`
- `set_motor_speed()`
- `set_waypoint()`
- `speed_change()`
- `stop_motors()`
- `turn_direction()`

### Still Missing - Data Access (Lower Priority)
- `get_altitude_data()` - Use `getAltitude()` or DroneStatus methods instead
- `get_angular_speed_x/y/z()` - Use `getGyro()` methods instead  
- `get_error_data()` - Not yet implemented
- `get_image_data()` - Not yet implemented
- `get_lostconnection_data()` - Not yet implemented
- `get_motion_data()` - Use `getMotion()` or DroneStatus methods instead
- `get_range_data()` - Use `getRange()` or individual range getters instead
- `get_raw_motion_data()` - Not yet implemented
- `get_state_data()` - Use `getState()` or DroneStatus methods instead
- `get_temperature()` - **Deprecated**, use `getBarometerTemperature()` or calibrated methods
- `get_trim_data()` - Use DroneStatus methods instead
- `get_x/y/z_accel()` - Use `getAccel()` methods instead
- `get_x/y/z_angle()` - Use `getAngle()` methods instead
- `get_x/y/z_gyro()` - Use `getGyro()` methods instead

### Still Missing - Controller Display Methods
- `controller_buzzer_sequence()`
- `controller_create_canvas()`
- `controller_draw_arc()`
- `controller_draw_canvas()`
- `controller_draw_chord()`
- `controller_draw_ellipse()`
- `controller_draw_image()`
- `controller_draw_polygon()`
- `controller_draw_square()`
- `controller_draw_string_align()`
- `controller_preview_canvas()`

### Still Missing - Protocol/Internal Methods (Low Priority)
- `convertByteArrayToString()`
- `convert_meter()`
- `convert_millimeter()`
- `dummy_function()`
- `format_firmware_version()`
- `getCount()` - Low-level, use `get_count_data()` instead
- `getData()` - Low-level
- `getHeader()` - Low-level
- `height_from_pressure()` - Use `getHeightFromPressure()` instead
- `initialize_data()` - Internal
- `j()` - Internal/debug?
- `makeTransferDataArray()` - Internal
- `new_color_data()` - Internal
- `percent_error()` - Utility
- `print_num_data()` - Debug
- `receive_address_data()` - Internal
- `receive_cpu_id_data()` - Internal
- `sendBacklight()` - Use `setBacklight()` instead
- `sendBuzzer()` - Use `controllerBuzzer()` or `droneBuzzer()` methods
- `sendBuzzerHz/HzReserve()` - Low-level
- `sendBuzzerMute/MuteReserve()` - Low-level
- `sendBuzzerScale/ScaleReserve()` - Low-level
- `sendClearBias()` - Use `clearBias()` instead
- `sendClearTrim()` - Use `clearTrim()` instead
- `sendCommand*()` - Low-level protocol methods
- `sendControlleLinkMode()` - Typo in Python? Use `setControllerMode()`
- `sendDisplay*()` - Low-level display methods
- `sendFlightEvent()` - Use `triggerFlightEvent()` instead
- `sendFlip()` - Use `flip()` instead
- `sendHeadless()` - Use `setHeadlessMode()` instead
- `sendLanding()` - Use `land()` instead
- `sendLight*()` - Use `setDroneLED()` or `setControllerLED()` methods
- `sendLostConnection()` - Low-level
- `sendModeControlFlight()` - Low-level
- `sendMotor*()` - Low-level motor control
- `sendPairing()` - Use `pair()` instead
- `sendPing()` - Low-level
- `sendSetDefault()` - Use `setDefault()` instead
- `sendStop()` - Use `emergencyStop()` instead
- `sendTakeOff()` - Use `takeoff()` instead
- `sendTrim()` - Low-level
- `sendVibrator*()` - Low-level
- `sendWeight()` - Low-level
- `setEventHandler()` - Different paradigm (Java uses different pattern)
- `temperature_convert()` - Utility
- `transfer()` - Internal
- `update_*()` - Internal update methods (Java uses receiver pattern)

## Methods in Java but NOT in Python

ℹ️ Java-specific methods (expected):

- `autoConnect) throws DroneNotFoundException()`
- `autoConnect, String portName) throws DroneNotFoundException()`
- `changeSpeed()`
- `clearBias()`
- `clearCounter()`
- `clearTrim()`
- `controllerLEDOff()`
- `controller_clear_area()`
- `controller_draw_circle()`
- `controller_invert_area()`
- `droneLEDOff()`
- `getAccel()`
- `getAngle()`
- `getBackRange()`
- `getCalculatedAltitude()`
- `getCorrectedElevation()`
- `getDroneStatus()`
- `getFlightController()`
- `getGyro()`
- `getHeightFromPressure()`
- `getLeftRange()`
- `getLinkController()`
- `getLinkManager()`
- `getPositionX()`
- `getPositionY()`
- `getPositionZ()`
- `getReceiver()`
- `getRightRange()`
- `getSettingsController()`
- `getTopRange()`
- `getUncorrectedElevation()`
- `loadColorClassifier()`
- `setBacklight()`
- `setControllerLED()`
- `setControllerLEDMode()`
- `setControllerMode()`
- `setDefault()`
- `setDroneLED()`
- `setDroneLEDBlue()`
- `setDroneLEDGreen()`
- `setDroneLEDMode()`
- `setDroneLEDOrange()`
- `setDroneLEDPurple()`
- `setDroneLEDRed()`
- `setDroneLEDWhite()`
- `setDroneLEDYellow()`
- `setHeadlessMode()`
- `setLinkMode()`
- `triggerFlightEvent()`
- `unloadColorClassifier()`
- `updateButtonData()`
- `useCorrectedElevation()`
- `{()`

## ⚠️ Important Missing Methods for Inventory Management

✅ **IMPLEMENTED:**
- `get_information_data()` - **IMPLEMENTED** (Returns InformationData object or array)
- `get_cpu_id_data()` - **IMPLEMENTED** (Returns CpuIdData object or array)
- `get_address_data()` - **IMPLEMENTED** (Returns AddressData object or array)
- `get_count_data()` - **IMPLEMENTED** (Returns CountData object or array)

ℹ️ **AVAILABLE VIA OTHER METHODS:**
- `get_flight_time()` - Available via `get_count_data().getFlightTime()` or `getFlightTime()`
- `get_takeoff_count()` - Available via `get_count_data().getTakeoffCount()` or `getTakeoffCount()`
- `get_landing_count()` - Available via `get_count_data().getLandingCount()` or `getLandingCount()`
- `get_accident_count()` - Available via `get_count_data().getAccidentCount()` or `getAccidentCount()`

✅ **IMPLEMENTED - Controller Input Methods:**
- `getButtonDataObject()` - **NEW** Type-safe ButtonData composite (recommended for Java)
- `getJoystickDataObject()` - **NEW** Type-safe JoystickData composite (recommended for Java)
- Python `get_button_data()` → Java `getButtonData()` (array, for compatibility)
- Python `get_joystick_data()` → Java `getJoystickData()` (array, for compatibility)

## Recent Implementations (October 2025)

### InventoryManager with Three-Tier API
Implemented comprehensive inventory data access following the three-tier pattern:
1. **Python-compatible arrays**: `getInformationData()`, `getCpuIdData()`, etc.
2. **Individual getters**: `getDroneModel()`, `getFlightTime()`, etc.
3. **Java composite objects**: `getInformationDataObject()`, `getCountDataObject()`, etc.

### ControllerInputManager with Three-Tier API
Refactored controller input to manager pattern with named fields:
1. **Python-compatible arrays**: `getButtonData()`, `getJoystickData()`
2. **Individual getters**: `getLeftJoystickX()`, `getRightJoystickY()`, `getButtonFlags()`, etc.
3. **Java composite objects**: `getButtonDataObject()`, `getJoystickDataObject()`

**Documentation:**
- `INVENTORY_METHODS_IMPLEMENTATION.md` - Inventory API guide
- `INVENTORY_DATA_ACCESS_PATTERNS.md` - Access pattern reference
- `CONTROLLER_INPUT_REFACTORING.md` - Controller input migration guide
- `BEST_PRACTICE_GUIDANCE.md` - Overall API best practices

