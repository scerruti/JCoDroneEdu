# Sprint 1: Critical API & Core Logic

**Sprint Goal:** Achieve comprehensive test coverage for all critical API classes that students and educators interact with directly.

**Priority:** 🔴 Critical  
**Duration:** 2 weeks  
**Target Coverage:** 80%+ for all core API classes

---

## Sprint Backlog

### Issue #1: Add unit tests for Drone class core methods
**Component:** `Drone.java`  
**Priority:** Critical  
**Labels:** `sprint-1`, `priority-critical`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `takeoff()`
- `land()`
- `move()`
- `move(int duration)`
- `hover()`
- `hover(int duration)`
- `emergencyStop()`
- `reset()`

**Test Scenarios:**
1. Test successful takeoff initialization
2. Test land command execution
3. Test move command with default duration
4. Test move command with custom duration
5. Test hover command execution
6. Test emergency stop immediate response
7. Test reset command clears all states
8. Test command sequencing (takeoff → move → land)
9. Test error when commanding without connection
10. Test duplicate takeoff/land commands

**Acceptance Criteria:**
- [ ] All core flight methods have unit tests
- [ ] 80%+ coverage for Drone core methods
- [ ] Tests validate command sequencing
- [ ] Edge cases tested (null values, negative durations)
- [ ] Mock drone properly simulates behavior
- [ ] Tests pass on CI/CD

---

### Issue #2: Add unit tests for Drone color LED methods
**Component:** `Drone.java` (LED methods)  
**Priority:** Critical  
**Labels:** `sprint-1`, `priority-critical`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `setDroneColor(int red, int green, int blue)`
- `setDroneColor(int red, int green, int blue, int brightness)`
- `getDroneColor()`
- `resetDroneColor()`
- `droneColorBlink()` variations

**Test Scenarios:**
1. Test setDroneColor with valid RGB values
2. Test setDroneColor with brightness parameter
3. Test getDroneColor returns set values
4. Test resetDroneColor clears to default
5. Test color blink with different patterns
6. Test boundary values (0, 255, out of range)
7. Test negative color values handling
8. Test color persistence across commands

**Acceptance Criteria:**
- [ ] All color LED methods tested
- [ ] RGB value validation tested
- [ ] Brightness parameter properly validated
- [ ] State persistence verified
- [ ] Boundary conditions tested
- [ ] Tests document expected color behavior

---

### Issue #3: Add unit tests for FlightController velocity and movement methods
**Component:** `FlightController.java`  
**Priority:** Critical  
**Labels:** `sprint-1`, `priority-critical`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `setPitch(int value)`
- `setRoll(int value)`
- `setYaw(int value)`
- `setThrottle(int value)`
- `go(int pitch, int roll, int yaw, int throttle)`
- `turn(int degrees)`
- `setFlightSpeed(int speed)`

**Test Scenarios:**
1. Test setPitch with valid range (-100 to 100)
2. Test setRoll with valid range
3. Test setYaw rotation control
4. Test setThrottle altitude control
5. Test go() combines all axes correctly
6. Test turn() rotates specified degrees
7. Test setFlightSpeed affects movement
8. Test value clamping for out-of-range inputs
9. Test velocity reset between commands
10. Test command accumulation vs replacement

**Acceptance Criteria:**
- [ ] All velocity control methods tested
- [ ] Range validation (-100 to 100) verified
- [ ] Combined movement commands tested
- [ ] Speed settings properly applied
- [ ] Edge cases handled correctly
- [ ] Mock accurately tracks control values

---

### Issue #4: Add unit tests for FlightController emergency and reset commands
**Component:** `FlightController.java`  
**Priority:** Critical  
**Labels:** `sprint-1`, `priority-critical`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `emergencyStop()`
- `resetTrim()`
- `resetHeading()`
- `calibrate()`
- `resetAll()`

**Test Scenarios:**
1. Test emergencyStop halts all movement immediately
2. Test resetTrim clears trim adjustments
3. Test resetHeading resets orientation
4. Test calibrate initiates calibration sequence
5. Test resetAll clears all states
6. Test emergency stop overrides other commands
7. Test reset methods clear appropriate values
8. Test calibration timeout handling

**Acceptance Criteria:**
- [ ] Emergency stop properly tested
- [ ] All reset methods verified
- [ ] Calibration flow tested
- [ ] Command priority validated
- [ ] State clearing confirmed
- [ ] Safety features verified

---

### Issue #5: Add unit tests for DisplayController text and drawing methods
**Component:** `DisplayController.java`  
**Priority:** High  
**Labels:** `sprint-1`, `priority-high`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `drawText(String text, int x, int y)`
- `drawLine(int x1, int y1, int x2, int y2)`
- `drawRectangle(int x, int y, int width, int height)`
- `drawCircle(int x, int y, int radius)`
- `clearDisplay()`
- `showDisplay()`

**Test Scenarios:**
1. Test drawText renders at correct position
2. Test drawLine connects two points
3. Test drawRectangle with dimensions
4. Test drawCircle with radius
5. Test clearDisplay removes all content
6. Test showDisplay updates controller screen
7. Test coordinate boundary handling
8. Test text overflow behavior
9. Test overlapping shapes
10. Test display buffer management

**Acceptance Criteria:**
- [ ] All drawing methods tested
- [ ] Coordinate validation verified
- [ ] Display update mechanism tested
- [ ] Buffer management validated
- [ ] Boundary cases handled
- [ ] Clear/show sequence verified

---

### Issue #6: Add unit tests for DisplayController color and canvas operations
**Component:** `DisplayController.java`  
**Priority:** High  
**Labels:** `sprint-1`, `priority-high`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `setColor(int red, int green, int blue)`
- `fillRectangle(int x, int y, int width, int height)`
- `setPixel(int x, int y)`
- `setCanvasColor(int color)`
- `invertDisplay()`

**Test Scenarios:**
1. Test setColor changes drawing color
2. Test fillRectangle fills area with color
3. Test setPixel sets individual pixel
4. Test setCanvasColor changes background
5. Test invertDisplay reverses colors
6. Test color persistence across drawings
7. Test color value validation
8. Test canvas operations on full buffer

**Acceptance Criteria:**
- [ ] Color methods fully tested
- [ ] Canvas operations verified
- [ ] Pixel-level operations tested
- [ ] Color state management validated
- [ ] Display inversion tested
- [ ] RGB validation implemented

---

### Issue #7: Add unit tests for ControllerService input reading methods
**Component:** `ControllerService.java`  
**Priority:** High  
**Labels:** `sprint-1`, `priority-high`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `getButton(String buttonName)`
- `getJoystick(String joystickName)`
- `waitForButtonPress(String buttonName)`
- `isButtonPressed(String buttonName)`

**Test Scenarios:**
1. Test getButton returns button state
2. Test getJoystick returns position values
3. Test waitForButtonPress blocks until pressed
4. Test isButtonPressed returns boolean
5. Test invalid button name handling
6. Test button state changes
7. Test joystick value ranges
8. Test simultaneous button detection

**Acceptance Criteria:**
- [ ] All input methods tested
- [ ] Button state tracking verified
- [ ] Joystick value reading tested
- [ ] Wait functionality validated
- [ ] Error handling for invalid names
- [ ] Multiple input detection tested

---

### Issue #8: Add unit tests for DroneStatus state tracking and reporting
**Component:** `DroneStatus.java`  
**Priority:** High  
**Labels:** `sprint-1`, `priority-high`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `getBatteryPercentage()`
- `isFlying()`
- `getAltitude()`
- `getPosition()`
- `getOrientation()`
- Status update methods

**Test Scenarios:**
1. Test battery percentage reporting
2. Test flying state detection
3. Test altitude value retrieval
4. Test position coordinate reporting
5. Test orientation angles
6. Test status update frequency
7. Test state transitions
8. Test cached vs fresh values

**Acceptance Criteria:**
- [ ] All status methods tested
- [ ] State tracking verified
- [ ] Value ranges validated
- [ ] Update mechanisms tested
- [ ] Cache behavior verified
- [ ] State transitions covered

---

### Issue #9: Add unit tests for DroneSystem information and version methods
**Component:** `DroneSystem.java`  
**Priority:** Medium  
**Labels:** `sprint-1`, `priority-medium`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- `getVersion()`
- `getFirmwareVersion()`
- `getSystemInfo()`
- Device information methods

**Test Scenarios:**
1. Test version string retrieval
2. Test firmware version format
3. Test system information structure
4. Test device identification
5. Test version comparison logic

**Acceptance Criteria:**
- [ ] Version methods tested
- [ ] Information retrieval verified
- [ ] Data format validated
- [ ] Device info methods covered

---

### Issue #10: Add unit tests for exception handling
**Component:** `DroneNotFoundException.java`, `MessageNotSentException.java`, `InvalidMessageException.java`  
**Priority:** Critical  
**Labels:** `sprint-1`, `priority-critical`, `test`, `coverage`, `unit-test`, `component-api`

**Methods to Cover:**
- DroneNotFoundException scenarios
- MessageNotSentException scenarios
- InvalidMessageException scenarios
- Exception message formatting
- Exception context preservation

**Test Scenarios:**
1. Test DroneNotFoundException thrown when no device found
2. Test MessageNotSentException on communication failure
3. Test InvalidMessageException on malformed packets
4. Test exception messages are informative
5. Test exception cause chain preservation
6. Test exception handling in API methods
7. Test recovery after exceptions

**Acceptance Criteria:**
- [ ] All exception types tested
- [ ] Exception throwing scenarios covered
- [ ] Message clarity verified
- [ ] Cause chain preserved
- [ ] Recovery paths tested
- [ ] Educational error messages validated

---

## Sprint Success Metrics

- ✅ All 10 issues completed
- ✅ 80%+ code coverage on core API classes
- ✅ All tests passing on CI/CD
- ✅ Zero critical methods without tests
- ✅ Test suite runs in < 30 seconds
- ✅ Documentation updated with new patterns

## Sprint Review Agenda

1. Demo new test coverage
2. Review coverage report
3. Discuss any blocked items
4. Plan any carryover to Sprint 2
5. Retrospective on process improvements

---

**Sprint Start:** [To be scheduled]  
**Sprint End:** [To be scheduled]  
**Sprint Owner:** [To be assigned]  
**Team Members:** [To be assigned]
