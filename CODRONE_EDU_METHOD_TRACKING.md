# CoDrone EDU Method Implementation Tracking

## Project Overview

This document tracks the implementation status of CoDrone EDU Python library methods in our Java test framework. The goal is to ensure our `MockDrone` class provides comprehensive testing coverage for all drone functionality that students might use in their assignments.

## Documentation Reference

**Primary Source**: [CoDrone EDU Python Function Documentation](https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation)

**Last Updated**: July 11, 2025  
**Python Library Version**: 2.3 ✅ **CONFIRMED** (pip show verified)  
**Framework Location**: `src/test/java/com/otabi/jcodroneedu/DroneTest.java`  
**Reference Code**: `reference/codrone_edu/` (7617 lines in drone.py v2.3, from site-packages)

## Recent Implementation Progress

🎉 **PHASE 1 COMPLETE** - Critical methods implemented July 11, 2025:
- ✅ `emergency_stop()` - Safety critical method
- ✅ `go(direction, power, duration)` - **NEW v2.3** beginner-friendly method
- ✅ `get_front_range(unit)` - Obstacle avoidance sensor
- ✅ `square(speed, seconds, direction)` - Educational flight pattern
- ✅ `get_move_values()` - Debugging support method

**Test Coverage**: Comprehensive unit tests added in `CriticalMethodsTest.java`

## Implementation Status Legend

- ✅ **Implemented**: Method exists in MockDrone and has unit tests
- 🚧 **Partial**: Method exists but needs enhancement or additional testing
- ❌ **Missing**: Method not implemented in MockDrone
- 🧪 **Unit Tested**: Has comprehensive unit test coverage
- 🚁 **Drone Tested**: Tested with actual drone hardware
- 📚 **Student Ready**: Safe for student assignments

## Connection Methods

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `pair()` | ✅ 🧪 | ✅ | 🚁 | Supports L0101 pattern 1 |
| `connect()` | ✅ 🧪 | ✅ | 🚁 | Supports L0101 pattern 2, throws DroneNotFoundException |
| `close()` | ✅ 🧪 | ✅ | 🚁 | Supports try-with-resources pattern |
| `disconnect()` | ✅ 🧪 | ✅ | 🚁 | Alternative cleanup method |

## Basic Flight Operations

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `takeoff()` | ✅ 🧪 | ✅ | 🚁 | Basic flight operation |
| `land()` | ✅ 🧪 | ✅ | 🚁 | Basic flight operation |
| `hover(duration)` | ✅ 🧪 | ✅ | 🚁 | Accepts duration in milliseconds |
| `emergency_stop()` | ✅ 🧪 📚 | ✅ | ❌ | **IMPLEMENTED** - Safety method for immediate motor stop |

## Movement Control Methods

### Flight Variables
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `setPitch(int)` | ✅ 🧪 | ✅ | 🚁 | Forward/backward movement |
| `setRoll(int)` | ✅ 🧪 | ✅ | 🚁 | Left/right movement |
| `setYaw(int)` | ✅ 🧪 | ✅ | 🚁 | Rotation control |
| `setThrottle(int)` | ✅ 🧪 | ✅ | 🚁 | Up/down movement |
| `move()` | ✅ 🧪 | ✅ | 🚁 | Execute movement indefinitely |
| `move(duration)` | ✅ 🧪 | ✅ | 🚁 | Execute movement for duration |
| `resetMoveValues()` | ✅ 🧪 | ✅ | 🚁 | Reset all flight variables to 0 |
| `get_move_values()` | ✅ 🧪 📚 | ✅ | ❌ | **IMPLEMENTED** - Returns [roll, pitch, yaw, throttle] |
| `print_move_values()` | ❌ | ❌ | ❌ | Deprecated - use get_move_values() |

### Beginner-Friendly Movement
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `go(direction, power, duration)` | ✅ 🧪 📚 | ✅ | ❌ | **IMPLEMENTED** - **NEW v2.3** beginner method |
| `move_forward(distance, units, speed)` | ❌ | ❌ | ❌ | Distance-based movement |
| `move_backward(distance, units, speed)` | ❌ | ❌ | ❌ | Distance-based movement |
| `move_left(distance, units, speed)` | ❌ | ❌ | ❌ | Distance-based movement |
| `move_right(distance, units, speed)` | ❌ | ❌ | ❌ | Distance-based movement |

### Advanced Movement
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `move_distance(x, y, z, velocity)` | ❌ | ❌ | ❌ | 3D simultaneous movement |
| `send_absolute_position(...)` | ❌ | ❌ | ❌ | Absolute positioning |
| `turn(power, seconds)` | ❌ | ❌ | ❌ | Power-based turning |
| `turn_degree(degree)` | ❌ | ❌ | ❌ | Degree-based turning |
| `turn_left(degree, timeout)` | ❌ | ❌ | ❌ | Left turn with timeout |
| `turn_right(degree, timeout)` | ❌ | ❌ | ❌ | Right turn with timeout |

## Flight Sequences

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `circle(speed, direction)` | ❌ | ❌ | ❌ | **Medium Priority** - common pattern |
| `square(speed, seconds, direction)` | ✅ 🧪 📚 | ✅ | ❌ | **IMPLEMENTED** - L0102 educational pattern |
| `triangle(speed, seconds, direction)` | ❌ | ❌ | ❌ | Geometric pattern |
| `spiral(speed, seconds, direction)` | ❌ | ❌ | ❌ | Advanced pattern |
| `sway(power, seconds, direction)` | ❌ | ❌ | ❌ | Side-to-side movement |
| `flip(direction)` | ❌ | ❌ | ❌ | Requires >50% battery |

## Sensor Methods

### Position Sensors
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `get_pos_x(unit)` | ❌ | ❌ | ❌ | X position (forward/backward) |
| `get_pos_y(unit)` | ❌ | ❌ | ❌ | Y position (left/right) |
| `get_pos_z(unit)` | ❌ | ❌ | ❌ | Z position (up/down) |
| `get_position_data(delay)` | ❌ | ❌ | ❌ | Combined position data |

### Range Sensors
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `detect_wall()` | ❌ | ❌ | ❌ | Boolean wall detection |
| `get_front_range(unit)` | ✅ 🧪 📚 | ✅ | ❌ | **IMPLEMENTED** - Obstacle avoidance sensor |
| `get_bottom_range(unit)` | ❌ | ❌ | ❌ | Height measurement |
| `get_height(unit)` | ❌ | ❌ | ❌ | Alias for get_bottom_range |
| `avoid_wall(timeout, distance)` | ❌ | ❌ | ❌ | Autonomous behavior |
| `keep_distance(timeout, distance)` | ❌ | ❌ | ❌ | Autonomous behavior |

### Gyroscope/Accelerometer
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `get_accel_x()` | ❌ | ❌ | ❌ | X-axis acceleration |
| `get_accel_y()` | ❌ | ❌ | ❌ | Y-axis acceleration |
| `get_accel_z()` | ❌ | ❌ | ❌ | Z-axis acceleration |
| `get_angle_x()` | ❌ | ❌ | ❌ | Roll angle |
| `get_angle_y()` | ❌ | ❌ | ❌ | Pitch angle |
| `get_angle_z()` | ❌ | ❌ | ❌ | Yaw angle |
| `get_angular_speed_x()` | ❌ | ❌ | ❌ | Roll angular velocity |
| `get_angular_speed_y()` | ❌ | ❌ | ❌ | Pitch angular velocity |
| `get_angular_speed_z()` | ❌ | ❌ | ❌ | Yaw angular velocity |
| `reset_gyro()` | ❌ | ❌ | ❌ | Reset angle values |

### Optical Flow Sensor
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `get_flow_velocity_x()` | ❌ | ❌ | ❌ | X-direction velocity |
| `get_flow_velocity_y()` | ❌ | ❌ | ❌ | Y-direction velocity |

### State/Environment Sensors
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `getBattery()` | ✅ 🧪 | ✅ | 🚁 | Returns mock 85% |
| `getTemperature()` | ✅ 🧪 | ✅ | 🚁 | Returns mock 22.5°C |
| `get_drone_temperature(unit)` | ❌ | ❌ | ❌ | With unit conversion |
| `get_flight_state()` | ❌ | ❌ | ❌ | Current flight state |
| `get_movement_state()` | ❌ | ❌ | ❌ | Movement state enum |
| `get_sensor_data(delay)` | ❌ | ❌ | ❌ | Combined sensor data (31 values) |

### Pressure/Altitude
| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `height_from_pressure()` | ❌ | ❌ | ❌ | Barometric height |
| `get_elevation()` | ❌ | ❌ | ❌ | Elevation from barometer |

## LED Control Methods

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `set_drone_LED(r, g, b, brightness)` | ❌ | ❌ | ❌ | **Medium Priority** - visual feedback |
| `set_controller_LED(r, g, b, brightness)` | ❌ | ❌ | ❌ | Controller LED control |
| `drone_LED_off()` | ❌ | ❌ | ❌ | Turn off drone LEDs |
| `controller_LED_off()` | ❌ | ❌ | ❌ | Turn off controller LEDs |
| `set_drone_LED_mode(r, g, b, mode, speed)` | ❌ | ❌ | ❌ | Pattern-based LED control |
| `set_controller_LED_mode(r, g, b, mode, speed)` | ❌ | ❌ | ❌ | Pattern-based LED control |

## Sound Control Methods

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `drone_buzzer(hz, duration)` | ❌ | ❌ | ❌ | **Low Priority** - audio feedback |
| `controller_buzzer(hz, duration)` | ❌ | ❌ | ❌ | Controller buzzer |
| `start_drone_buzzer(hz)` | ❌ | ❌ | ❌ | Start continuous buzzer |
| `stop_drone_buzzer()` | ❌ | ❌ | ❌ | Stop continuous buzzer |
| `start_controller_buzzer(hz)` | ❌ | ❌ | ❌ | Start continuous controller buzzer |
| `stop_controller_buzzer()` | ❌ | ❌ | ❌ | Stop continuous controller buzzer |

## Trim Control Methods

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `get_trim()` | ❌ | ❌ | ❌ | Get current trim values |
| `set_trim(roll, pitch)` | ❌ | ❌ | ❌ | Set trim adjustments |
| `reset_trim()` | ❌ | ❌ | ❌ | Reset trim to defaults |

## Controller Input Methods

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `get_button_data()` | ❌ | ❌ | ❌ | **Low Priority** - manual control |
| `get_joystick_data()` | ❌ | ❌ | ❌ | Joystick state data |
| `get_left_joystick_x()` | ❌ | ❌ | ❌ | Left stick X position |
| `get_left_joystick_y()` | ❌ | ❌ | ❌ | Left stick Y position |
| `get_right_joystick_x()` | ❌ | ❌ | ❌ | Right stick X position |
| `get_right_joystick_y()` | ❌ | ❌ | ❌ | Right stick Y position |
| Various button methods | ❌ | ❌ | ❌ | up/down/left/right_arrow_pressed(), etc. |

## Color Sensor Methods

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| `get_color_data()` | ❌ | ❌ | ❌ | **Low Priority** - specialized use |
| `get_front_color()` | ❌ | ❌ | ❌ | Front color sensor |
| `get_back_color()` | ❌ | ❌ | ❌ | Back color sensor |
| `get_colors()` | ❌ | ❌ | ❌ | Combined color data |
| Color ML methods | ❌ | ❌ | ❌ | Machine learning features |

## Screen Control Methods (Controller)

| Method | Status | Unit Test | Drone Test | Notes |
|--------|--------|-----------|------------|-------|
| Various screen methods | ❌ | ❌ | ❌ | **Low Priority** - display control |

## Implementation Priorities

### Phase 1: Critical Missing Methods (Immediate)
1. `emergency_stop()` - Safety critical
2. `go(direction, power, duration)` - Beginner-friendly
3. `square()` - Used in L0102 assignment
4. `get_front_range()` - Obstacle detection

### Phase 2: Common Student Methods (Short-term)
1. `move_forward/backward/left/right()` - Intuitive movement
2. `get_pos_x/y/z()` - Position tracking
3. `set_drone_LED()` - Visual feedback
4. `circle()` - Common flight pattern

### Phase 3: Advanced Features (Medium-term)
1. Gyroscope/accelerometer methods
2. Advanced movement methods
3. Autonomous behaviors (avoid_wall, keep_distance)

### Phase 4: Specialized Features (Long-term)
1. Color sensor methods
2. Controller input methods
3. Screen control methods
4. Sound control methods

## Testing Strategy

### Unit Testing
- Each method should have at least one unit test
- Test parameter validation and edge cases
- Verify command history tracking
- Test return value accuracy

### Integration Testing
- Test method combinations (e.g., takeoff → move → land)
- Verify state management between calls
- Test exception handling scenarios

### System Testing with Drone
- Validate mock behavior matches real drone
- Test safety features with actual hardware
- Verify educational value in real assignments

## Maintenance Notes

### When to Update This Document
- New Python library release
- New methods added to CoDrone EDU
- Changes to existing method signatures
- New educational assignments requiring different methods
- Student feedback indicating missing functionality

### Tracking Changes
- Document Python library version changes
- Note deprecated methods and replacements
- Track breaking changes that affect tests
- Monitor student usage patterns for priority adjustments

## Educational Considerations

### Student Assignment Coverage
- L0101: Connection patterns ✅
- L0102: Flight movements and square patterns 🚧 (missing square())
- Future assignments: Will need sensor and LED methods

### Learning Progression
- Basic connection and safety ✅
- Simple movement commands ✅
- Advanced flight patterns 🚧
- Sensor integration ❌
- Autonomous behaviors ❌

---

**Last Updated**: July 11, 2025  
**Next Review**: Next Python library release or major assignment change  
**Maintainer**: Course Development Team


---

## Python Library Changelog

# CoDrone EDU Python Library Changelog\n\n**Last Updated**: July 11, 2025\n**Source**: https://docs.robolink.com/docs/CoDroneEDU/Python/Python-Changelog\n\nPythonPython ChangelogOn this pagePython ChangelogCoDrone EDU Python Library Changelog​
Version 2.3​
April 28, 2025​
New Features ✨

Added LED pattern functions for the drone and controller: set_drone_LED_mode() and set_controller_LED_mode().
Added go() function in the Python library. This new function provides a simpler way to transition from block-based programming to Python. It is an alternative to the traditional roll, pitch, yaw, and throttle commands.

Improvements ⬆️

Improved error-handling for color training functions.
Reduced the minimum number of colors to have a valid color dataset from 3 colors to 2 colors.
Improved send_absolute_position() functionality when taking off/landing multiple times in one run


Version 2.2​
March 10, 2025​
Improvements ⬆️

Improved error-handling for controller screen functions.
Improved performance of send_absolute_position(), move_forward(), move_backward(), move_left(), and move_right().
get_colors(), get_front_color(), and get_back_color() return values in all lowercase.

Bug Fixes 🐛

Fixed unexpected movement of send_absolute_position() after turning the drone.


Version 2.1​
January 6, 2025​
New Features ✨

New function get_raw_motion_data() implemented

Improvements ⬆️

Reversed the parameters in the turn() function so that power (direction) comes first before duration (in seconds)
Addressed cases where get_control_speed() did not return updated values
Added library functionality to improve color calibration
Added library functionality to properly detect disconnections of the programming software to the drone or controller
Addressed cases where controller_draw_image() missed pixels while drawing

Bug Fixes 🐛

Corrected the turning direction in the turn() function to handle positive and negative values correctly.


Version 2.0​
November 7, 2024​
New Features ✨

added get_movement_state()
added get_count_data()
added get_address_data()
added get_information_data()
get_error_data() now includes state data in addition to sensor data
controller_preview_canvas() no longer contains image parameter

Improvements ⬆️

improved error handling messages for the user
print_move_values() was renamed to get_move_values() which can be printed as needed
renamed reset_sensor() -> reset_gyro() and increased delay to ensure reset
renamed reset_move() -> reset_move_values()
renamed get_temperature() -> get_drone_temperature()
renamed get_flow_x() -> get_flow_velocity_x()
renamed get_flow_y() -> get_flow_velocity_y()
renamed get_x_accel() -> get_accel_x()
renamed get_x_accel() -> get_accel_y()
renamed get_x_accel() -> get_accel_z()
renamed get_x_angle() -> get_angle_x()
renamed get_x_angle() -> get_angle_y()
renamed get_x_angle() -> get_angle_z()
renamed load_classifier() -> load_color_data()
all controller screen draw functions have an image parameter (functions not compatible with JROTC ed.)
send_absolute_position(), move_forward(), move_backward(), move_left(), and move_right() have been improved for testing
get_image_data() is now a Drone class method (ex. drone.get_image_data())

Bug Fixes 🐛

fixed the pkg_resources error for versions of Python greater than 3.11
fixed set_trim() and reset_trim() delays to work with CoDrone EDU (JROTC ed.)
fixed an issue that appeared when calibrating only 3 colors using the KNN model
corrected the index value for get_flow_velocity_y()


Version 1.9​
October 8, 2023​
Bug Fixes 🐛

Fixed a bug in returning controller button press data for the custom controller lesson


Version 1.8​
April 15, 2023​
New Features ✨

added get_sensor_data()

Improvements ⬆️

Removed pynput dependency

Bug Fixes 🐛

Fixed bug where sensor requests would return 0 right after takeoff


Version 1.7​
February 16, 2023​
New Features ✨

added start_controller_buzzer()
added stop_controller_buzzer()
added get_error_data()

Improvements ⬆️

renamed get_x_gyro() -> get_angular_speed_x()
renamed get_y_gyro() -> get_angular_speed_y()
renamed get_z_gyro() -> get_angular_speed_z()
codrone-edu library version prints to console


Version 1.6​
December 28, 2022​
New Features ✨

added start_drone_buzzer()
added stop_drone_buzzer()
added get_temperature()
added move_forward()
added move_left()
added move_right()
added move_backward()

Improvements ⬆️

updated send_absolute_position()


Version 1.5​
November 14, 2022​
New Features ✨

added stop_motors()
added reset_sensor()

Improvements ⬆️

avoid wall default unit changed from mm to cm
get_pressure() and get_drone_temp() have been modified


Version 1.4​
August 17, 2022​
New Features ✨

added error checking to load_classifier() method
added height_from_pressure()
Virtual ceiling method integrated

Improvements ⬆️

Changed yaw directional values (positive yaw now turns left)


Version 1.3​
June 27, 2022​
New Features ✨

added go()
added 'ESC' key kill switch
waypoints now support multiple takeoffs
Motor diagnostic integrated as motor_test()
Added virtual ceiling to the background of the drone class
added turn()

Improvements ⬆️

turn_degree() method improved


Version 1.2​
June 10, 2022​
New Features ✨

Added move() command with input parameters
Added hyperlink to "drone may not be paired" message.
Added floor test as a method test_floor()
Added a motor test that uses all 4 motors individually to determine if one is faulty. motor_test()
Added waypoint system
Added joystick and button functions


Version 1.1​
May 17, 2022​
Improvements ⬆️

removed serial library

Bug Fixes 🐛

Fixed screen error on controller


Version 1.0​
May 17, 2022​
New Features ✨

Speed defaults to 2 when starting a program
Added speed_change() and get_control_speed() functions
Pillow library added as dependency
Added error message if serial library is not detected
Added error handling when calling load_classifier()


Version 0.9​
May 17, 2022​
New Features ✨

Added Swarm class


Version 0.8​
Mar 1, 2022​
New Features ✨

Added movement as a state in the state list
Added temperature_convert()


Version 0.7​
Feb 18, 2022​
New Features ✨

Added luminosity to knn.fit for a fourth data point
Added controller screen drawing functions

Bug Fixes 🐛

Fixed turn_degree() to be absolute and smoother and turn 180 degrees
Fixed turn_right()
Fixed turn_left()


Version 0.6​
Feb 4, 2022​
New Features ✨

Added and fixed sendMotor
Added and fixed sendMotorSingle
Added buzzer flip warning when battery is less than 50%
convert_meter can now return meter
Added drone.append_color_data() - appends data to an existing text file
Added error handling when load_classifier is empty

Improvements ⬆️

drone.get_flow_x, y converted from m to cm
All distance sensors now return in centimeter by default
drone.turn_degree() is now absolute and division by 0 is fixed
drone.get_height() now uses time of flight instead of barometer
Updated set_trim() to only change roll and pitch
Updated get_trim_data() to return only roll and pitch
Changed dir variable in add_color()

Bug Fixes 🐛

Fixed all functions effected by centimeter being returned by default
Fixed buzzer functions
Fixed drone.avoid_wall()
Fixed issue when adding to a dataset that already exists


Version 0.5​
Jan 10, 2022​
New Features ✨

Added drone.flip()
Added convert_millimeter() and convert_meter() to return centimeter by default for all positional functions
Added error handling in the color classifier
Added docstrings


Version 0.4​
Dec 15, 2021​
New Features ✨

Added drone.get_drone_temp()
Added drone.get_pressure()
Added drone.drone_buzzer()
Added drone.controller_buzzer()
Added drone.set_trim()
Added drone.get_height()
Added drone.get_pressure()
Added the flight sequences square, triangle, spiral, and sway.

Improvements ⬆️

Improved takeoff command
Improved port connection


Version 0.3​
Nov 23, 2021​
New Features ✨

Added drone.avoid_wall() - Obstacle avoidance command. is similar to zumi.forward_avoid_collision()
Added drone.detect_wall() - Uses front range sensor to detect a wall
Added drone.keep_distance() - Keep distance command makes the drone maintain a distance to an obstacle
Added drone.turn_left() - Can be given a degree and the drone will turn to the left
Added drone.turn_right() - Can be given a degree and the drone will turn to the right
Added drone.turn_degrees() - Can take an absolute degree command and will turn to that degree.
Added drone.hover() - Will make the drone hover.
Added reset_YPRT() - Resets the yaw pitch roll and throttle and sends the command to the drone.
Added drone.keep_height() - Keep height command. A single command that is looped.
Added get_colors() - returns a list [1, 2]
Added get_front_color() - gets first color in get_colors() list
Added get_back_color() - gets second color in get_colors() list
Added predict_color() - predicts a color if you have trained the model and there is an existing file
Finished basic LEDs


Version 0.1 - 0.2​
Oct 10, 2021​
New Features ✨

Added Drone.acceleration_x, y, z
Added Drone.angle_roll, yaw, pitch
Added Drone.range_front()
Added Drone.range_bottom()
Added Drone.get_battery()
Added Drone.open()
Added Drone.takeoff()
Added Drone.land()

Edit this pagePreviousSwarm Function DocumentationNextPython for Robolink ChangelogCoDrone EDU Python Library ChangelogVersion 2.3Version 2.2Version 2.1Version 2.0Version 1.9Version 1.8Version 1.7Version 1.6Version 1.5Version 1.4Version 1.3Version 1.2Version 1.1Version 1.0Version 0.9Version 0.8Version 0.7Version 0.6Version 0.5Version 0.4Version 0.3Version 0.1 - 0.2


---
**Auto-updated**: 2025-07-11T18:53:02.280007  
**Source**: Gradle updateChangelog task