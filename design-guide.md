# CoDrone EDU Java Library - Design Guide

**Technical Reference for Maintainers and Advanced Developers**

This guide explains the architectural decisions, design patterns, and technical implementation details of the CoDroneEdu Java library. It's intended for developers who want to understand, maintain, or extend the library.

---

## Table of Contents

1. [Design Principles](#design-principles)
2. [Architecture Overview](#architecture-overview)
3. [API Patterns](#api-patterns)
4. [Communication Protocol](#communication-protocol)
5. [Adding New Methods](#adding-new-methods)
6. [Adding New Sensors](#adding-new-sensors)
7. [Testing Strategy](#testing-strategy)
8. [Performance Considerations](#performance-considerations)
9. [Contribution Guidelines](#contribution-guidelines)

---

## Design Principles

### 1. Educational First

Every design decision prioritizes educational use:

**Example: Error Messages**
```java
// ❌ Technical error
throw new IllegalStateException("State mismatch");

// ✅ Educational error
throw new IllegalStateException(
    "Cannot takeoff: Drone is already flying. " +
    "Make sure to land() before taking off again."
);
```

**Rationale:** Students need clear guidance, not technical jargon.

---

### 2. Python Compatibility

Maintain behavioral compatibility with Python API:

**Design Rule:** Always implement Python-equivalent method first, then add enhancements.

```java
// Step 1: Python-compatible method
public int getFrontRange() {
    // Matches Python get_front_range() behavior exactly
    return receiver.getFrontDistance();
}

// Step 2: Java enhancement (optional)
public Distance getFrontRangeObject() {
    return new Distance(getFrontRange(), DistanceUnit.CENTIMETERS);
}
```

**Rationale:** Teachers can reuse Python lesson plans with minimal modification.

---

### 3. Type Safety Without Complexity

Provide type-safe APIs while keeping simple use cases simple:

**Three-Tier API Pattern:**

```java
// Tier 1: Python-compatible (array)
public int[] getJoystickData() {
    return new int[]{leftX, leftY, rightX, rightY};
}

// Tier 2: Convenient individual access
public int getLeftJoystickX() {
    return getJoystickData()[0];
}

// Tier 3: Type-safe object (recommended)
public JoystickData getJoystickDataObject() {
    return new JoystickData(leftX, leftY, rightX, rightY);
}
```

**Rationale:** Beginners can use arrays (familiar from Python), advanced students benefit from type safety.

---

### 4. Fail-Safe Defaults

Operations should be safe by default:

```java
public void takeoff() {
    // Safety checks before executing
    if (isFlying()) {
        throw new IllegalStateException("Already flying");
    }
    if (getBattery() < 20) {
        throw new IllegalStateException("Battery too low: " + getBattery() + "%");
    }
    
    // Execute only if safe
    sendCommand(Command.TAKEOFF);
}
```

**Rationale:** Protects students and equipment from common mistakes.

---

### 5. Explicit Over Implicit

Make behavior obvious through naming and documentation:

```java
// ❌ Ambiguous
public double getElevation() {  // Corrected or uncorrected?
}

// ✅ Explicit
public double getUncorrectedElevation() {  // Clear: has firmware offset
}
public double getCorrectedElevation() {     // Clear: offset removed
}
```

**Rationale:** Students (and teachers) shouldn't need to guess behavior.

---

## Architecture Overview

### System Architecture

```
┌─────────────────────────────────────┐
│           Drone (Facade)            │  ← Student-facing API
├─────────────────────────────────────┤
│  Flight │ Sensors │ LED │ Sound     │  ← Functional groups
└────┬────┴────┬────┴──┬──┴───┬───────┘
     │         │       │      │
     ↓         ↓       ↓      ↓
┌─────────┐ ┌──────────┐ ┌─────────┐
│  Flight │ │Telemetry │ │  Link   │  ← Manager layer
│Controller│ │ Service  │ │ Manager │
└────┬────┘ └─────┬────┘ └────┬────┘
     │            │            │
     └────────────┴────────────┘
                  │
                  ↓
            ┌──────────┐
            │ Receiver │           ← Protocol layer
            ├──────────┤
            │  Serial  │           ← Hardware layer
            └──────────┘
```

### Key Components

**Drone (Facade)**
- Single entry point for students
- Delegates to specialized managers
- Provides cohesive API

**Managers (Business Logic)**
- `FlightController` - Movement commands
- `TelemetryService` - Sensor data with caching
- `LinkManager` - Communication handling
- `InventoryManager` - Device information
- `ControllerInputManager` - Input handling

**Receiver (Protocol)**
- Packet parsing
- Data type handling
- Event dispatching

**Serial (Hardware)**
- USB-Serial communication
- Low-level I/O

---

## API Patterns

### Pattern 1: Three-Tier Data Access

Use for all data that comes from drone:

```java
public class Drone {
    // Tier 1: Python-compatible array
    public Object[] getInformationData() {
        return new Object[]{
            getDroneModel(),
            getDroneVersion(),
            // ... more fields
        };
    }
    
    // Tier 2: Individual typed getters
    public String getDroneModel() {
        return inventoryManager.getModel();
    }
    
    public String getDroneVersion() {
        return inventoryManager.getVersion();
    }
    
    // Tier 3: Composite object (recommended)
    public InformationData getInformationDataObject() {
        return inventoryManager.getInformationData();
    }
}
```

**When to Use:** Any method that returns multiple related values.

**Benefits:**
- Python compatibility (Tier 1)
- Convenience (Tier 2)
- Type safety (Tier 3)

---

### Pattern 2: Command Methods

For actions (no return value):

```java
/**
 * Moves drone forward.
 * 
 * @param distance How far to move
 * @param units "cm", "m", "in", "ft"
 * @param power Speed (0.0 to 1.0)
 * @throws IllegalArgumentException if parameters invalid
 * @pythonEquivalent move_forward(distance, units, power)
 */
@educational
public void moveForward(double distance, String units, double power) {
    // 1. Validate parameters
    validateDistance(distance);
    validateUnits(units);
    validatePower(power);
    
    // 2. Convert units if necessary
    int distanceCm = convertToCentimeters(distance, units);
    
    // 3. Delegate to controller
    flightController.moveForward(distanceCm, power);
}
```

**Structure:**
1. Validate inputs
2. Convert/normalize parameters
3. Delegate to specialist
4. (Optional) Wait for completion

---

### Pattern 3: Query Methods

For reading values:

```java
/**
 * Gets current battery percentage.
 * 
 * @return Battery level (0-100)
 * @pythonEquivalent get_battery()
 */
@educational
public int getBattery() {
    return telemetryService.getBattery();
}
```

**Key Point:** Always go through TelemetryService for sensor data (handles caching).

---

### Pattern 4: Enhanced Methods

For Java-specific improvements:

```java
// Python-compatible base
public double getElevation() {
    return getUncorrectedElevation();  // Match Python behavior
}

// Enhanced version
public double getCorrectedElevation() {
    double pressure = getPressure();
    return calculateCorrectedElevation(pressure);
}

// With custom calibration
public double getCorrectedElevation(double seaLevelPressure) {
    double pressure = getPressure();
    return calculateCorrectedElevation(pressure, seaLevelPressure);
}
```

**Naming Convention:**
- `get*()` - Python-compatible
- `get*Uncorrected()` - Explicit about raw data
- `get*Corrected()` - Enhanced accuracy
- `get*Calibrated()` - User-calibrated

---

## Communication Protocol

### Packet Structure

All communication uses this packet format:

```
┌────────┬────────┬───────┬────────────┬─────────┐
│ Header │  Type  │ Length│    Data    │Checksum │
│ (4B)   │  (1B)  │  (1B) │ (variable) │  (1B)   │
└────────┴────────┴───────┴────────────┴─────────┘
```

**Header:** Always `0x0A 0x55 0xAA 0x0F`
**Type:** Command or data type (see `DataType` enum)
**Length:** Number of data bytes
**Data:** Payload (endianness matters!)
**Checksum:** XOR of all previous bytes

### Sending Commands

```java
public void sendCommand(Command command) {
    ByteBuffer buffer = ByteBuffer.allocate(7);
    buffer.order(ByteOrder.LITTLE_ENDIAN);  // Important!
    
    buffer.put(Header.HEADER_BYTES);
    buffer.put((byte) DataType.COMMAND.value);
    buffer.put((byte) 1);  // Length
    buffer.put((byte) command.value);
    buffer.put(calculateChecksum(buffer));
    
    serial.write(buffer.array());
}
```

**Critical:** Use `ByteOrder.LITTLE_ENDIAN` for multi-byte values.

### Receiving Data

```java
public class Receiver implements Runnable {
    @Override
    public void run() {
        while (running) {
            // 1. Wait for header
            if (!findHeader()) continue;
            
            // 2. Read type and length
            int type = serial.readByte();
            int length = serial.readByte();
            
            // 3. Read data
            byte[] data = serial.readBytes(length);
            
            // 4. Verify checksum
            byte checksum = serial.readByte();
            if (!verifyChecksum(type, length, data, checksum)) {
                continue;  // Invalid, skip
            }
            
            // 5. Dispatch to handler
            handleData(type, data);
        }
    }
}
```

### Data Parsing

**Example: Range Sensor Data**

```java
private void handleRangeData(byte[] data) {
    ByteBuffer buffer = ByteBuffer.wrap(data);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    
    int front = buffer.getShort() & 0xFFFF;  // Unsigned short
    int bottom = buffer.getShort() & 0xFFFF;
    int back = buffer.getShort() & 0xFFFF;
    
    droneStatus.setRangeData(front, bottom, back);
}
```

**Important:** Java doesn't have unsigned types, use masking (`& 0xFFFF`).

---

## Adding New Methods

### Step-by-Step Process

**1. Check Python API**

Find equivalent in [Python docs](https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation):

```python
# Python version
drone.move_forward(distance, units, power)
```

**2. Create Java Signature**

Convert to camelCase, maintain parameter order:

```java
public void moveForward(double distance, String units, double power)
```

**3. Add Annotations**

```java
/**
 * Moves drone forward by specified distance.
 * 
 * <p>The drone will move forward at the specified power level
 * for the calculated duration to achieve the requested distance.
 * 
 * @param distance Distance to move
 * @param units Unit of measurement: "cm", "m", "in", or "ft"
 * @param power Speed from 0.0 (stopped) to 1.0 (full speed)
 * @throws IllegalArgumentException if units invalid or power out of range
 * @pythonEquivalent move_forward(distance, units, power)
 * @see #moveBackward(double, String, double)
 */
@educational
public void moveForward(double distance, String units, double power) {
```

**4. Implement Method**

```java
    // Validate
    if (power < 0.0 || power > 1.0) {
        throw new IllegalArgumentException(
            "Power must be between 0.0 and 1.0, got: " + power
        );
    }
    
    // Convert units
    int distanceCm = switch (units.toLowerCase()) {
        case "cm" -> (int) distance;
        case "m" -> (int) (distance * 100);
        case "in" -> (int) (distance * 2.54);
        case "ft" -> (int) (distance * 30.48);
        default -> throw new IllegalArgumentException(
            "Invalid units: " + units + ". Use cm, m, in, or ft"
        );
    };
    
    // Delegate
    flightController.moveForward(distanceCm, power);
}
```

**5. Add Tests**

```java
@Test
public void testMoveForward_CentimeterConversion() {
    drone.moveForward(50, "cm", 1.0);
    verify(flightController).moveForward(50, 1.0);
}

@Test
public void testMoveForward_MeterConversion() {
    drone.moveForward(0.5, "m", 1.0);
    verify(flightController).moveForward(50, 1.0);
}

@Test
public void testMoveForward_InvalidPower() {
    assertThrows(IllegalArgumentException.class, () ->
        drone.moveForward(50, "cm", 1.5)  // Power > 1.0
    );
}
```

**6. Update Documentation**

Add to:
- `API_COMPARISON.md` - Mark as implemented
- `CODRONE_EDU_METHOD_TRACKING.md` - Update progress
- Example programs if appropriate

---

## Adding New Sensors

### Step-by-Step Process

**1. Identify Data Type**

Check protocol documentation:
```java
public enum DataType {
    // Existing...
    RANGE(0x31),
    ALTITUDE(0x32),
    
    // New sensor
    COLOR_SENSOR(0x40),  // Your new sensor
    // ...
}
```

**2. Create Data Packet Class**

```java
public class ColorData {
    private final int red;
    private final int green;
    private final int blue;
    private final int brightness;
    
    public ColorData(int red, int green, int blue, int brightness) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.brightness = brightness;
    }
    
    // Getters
    public int getRed() { return red; }
    public int getGreen() { return green; }
    public int getBlue() { return blue; }
    public int getBrightness() { return brightness; }
}
```

**3. Add to DroneStatus**

```java
public class DroneStatus {
    // Existing fields...
    private volatile ColorData colorData;
    
    public void setColorData(int r, int g, int b, int brightness) {
        this.colorData = new ColorData(r, g, b, brightness);
    }
    
    public ColorData getColorData() {
        return colorData;
    }
}
```

**4. Add Parser to Receiver**

```java
private void handleColorData(byte[] data) {
    ByteBuffer buffer = ByteBuffer.wrap(data);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    
    int red = buffer.get() & 0xFF;    // Unsigned byte
    int green = buffer.get() & 0xFF;
    int blue = buffer.get() & 0xFF;
    int brightness = buffer.get() & 0xFF;
    
    droneStatus.setColorData(red, green, blue, brightness);
}
```

**5. Add to TelemetryService**

```java
public class TelemetryService {
    public ColorData getColorData() {
        return getCachedData(
            DataType.COLOR_SENSOR,
            () -> {
                linkManager.requestData(DataType.COLOR_SENSOR);
                return droneStatus.getColorData();
            }
        );
    }
}
```

**6. Add Public API to Drone**

```java
// Tier 1: Python-compatible array
public int[] getColorData() {
    ColorData data = telemetryService.getColorData();
    return new int[]{
        data.getRed(),
        data.getGreen(),
        data.getBlue(),
        data.getBrightness()
    };
}

// Tier 2: Individual getters
public int getColorRed() {
    return getColorData()[0];
}

public int getColorGreen() {
    return getColorData()[1];
}

// ... more getters

// Tier 3: Object (recommended)
public ColorData getColorDataObject() {
    return telemetryService.getColorData();
}
```

**7. Add Tests**

```java
@Test
public void testColorSensor() {
    // Mock the data
    when(telemetryService.getColorData())
        .thenReturn(new ColorData(255, 128, 64, 200));
    
    // Test array access
    int[] colors = drone.getColorData();
    assertEquals(255, colors[0]);  // Red
    assertEquals(128, colors[1]);  // Green
    assertEquals(64, colors[2]);   // Blue
    assertEquals(200, colors[3]);  // Brightness
    
    // Test object access
    ColorData data = drone.getColorDataObject();
    assertEquals(255, data.getRed());
    assertEquals(128, data.getGreen());
}
```

---

## Testing Strategy

### Test Pyramid

```
        ┌────────┐
        │  E2E   │  ← 5% (with hardware)
        ├────────┤
        │ Integ. │  ← 25% (component interaction)
        ├────────┤
        │  Unit  │  ← 70% (individual methods)
        └────────┘
```

### Unit Tests

Test individual methods in isolation:

```java
@Test
public void testMoveForward_ValidParameters() {
    Drone drone = new Drone();
    FlightController mockController = mock(FlightController.class);
    drone.setFlightController(mockController);  // Inject mock
    
    drone.moveForward(50, "cm", 1.0);
    
    verify(mockController).moveForward(50, 1.0);
}
```

**Coverage Goal:** 80%+ for business logic

### Integration Tests

Test component interaction:

```java
@Test
public void testSensorDataFlow() {
    Drone drone = new Drone();
    Receiver receiver = drone.getReceiver();
    
    // Simulate incoming sensor packet
    byte[] packet = createRangePacket(100, 50, 75);
    receiver.processPacket(packet);
    
    // Verify data accessible through Drone API
    assertEquals(100, drone.getFrontRange());
    assertEquals(50, drone.getHeight());
}
```

### Hardware Tests

Test with real drone (manual or semi-automated):

```java
@Test
@Tag("hardware")
public void testActualFlight() {
    Drone drone = new Drone();
    drone.pair();  // Requires physical drone
    
    try {
        drone.takeoff();
        Thread.sleep(3000);  // Hover
        
        int height = drone.getHeight();
        assertTrue(height > 50, "Should be airborne");
        assertTrue(height < 200, "Should not be too high");
        
        drone.land();
    } finally {
        drone.close();
    }
}
```

**Run separately:** `./gradlew test --tests *Hardware*`

### Smoke Tests

Quick validation before release:

```java
public class SmokeTest {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        // Test 1: Connection
        System.out.print("Connection test... ");
        drone.pair();
        System.out.println("✓");
        
        // Test 2: Sensor reading
        System.out.print("Sensor test... ");
        int battery = drone.getBattery();
        assert battery >= 0 && battery <= 100;
        System.out.println("✓ (" + battery + "%)");
        
        // Test 3: LED control
        System.out.print("LED test... ");
        drone.setDroneLED Red(255);
        Thread.sleep(500);
        drone.droneLEDOff();
        System.out.println("✓");
        
        drone.close();
        System.out.println("\nAll smoke tests passed!");
    }
}
```

---

## Performance Considerations

### Caching Strategy

**Problem:** Requesting sensor data every time is slow.

**Solution:** TelemetryService with freshness threshold.

```java
public class TelemetryService {
    private static final long FRESHNESS_MS = 100;
    
    private Map<DataType, CachedValue> cache = new ConcurrentHashMap<>();
    
    public <T> T getData(DataType type, Supplier<T> fetcher) {
        CachedValue cached = cache.get(type);
        
        long now = System.currentTimeMillis();
        if (cached != null && now - cached.timestamp < FRESHNESS_MS) {
            return (T) cached.value;
        }
        
        T fresh = fetcher.get();
        cache.put(type, new CachedValue(fresh, now));
        return fresh;
    }
}
```

**Impact:**
- Before: 100ms per sensor read (slow)
- After: <1ms for cached (fast), 100ms for fresh (when needed)

### Batch Requests

**Problem:** Students often read multiple sensors.

**Bad:**
```java
int battery = drone.getBattery();  // Request 1
int height = drone.getHeight();    // Request 2
int front = drone.getFrontRange(); // Request 3
// 300ms total!
```

**Good:**
```java
// Request all sensor data once
drone.updateAllSensors();  // Single batch request

// Read from cache
int battery = drone.getBattery();  // From cache
int height = drone.getHeight();    // From cache
int front = drone.getFrontRange(); // From cache
// 100ms total!
```

### Thread Safety

All sensor data uses `volatile` or atomic operations:

```java
public class DroneStatus {
    private volatile int battery;
    private volatile AtomicReference<RangeData> rangeData;
    
    // Safe to call from any thread
    public int getBattery() {
        return battery;
    }
}
```

**Rationale:** Receiver runs in background thread, student code in main thread.

---

## Contribution Guidelines

### Code Style

**Follow Java conventions:**
```java
// ✓ Class names: PascalCase
public class FlightController { }

// ✓ Method names: camelCase
public void moveForward() { }

// ✓ Constants: UPPER_SNAKE_CASE
public static final int MAX_BATTERY = 100;

// ✓ Packages: lowercase
package com.otabi.jcodroneedu;
```

### Documentation Requirements

**Every public method needs:**
1. Brief description
2. Parameter descriptions
3. Return value description
4. Exceptions thrown
5. Python equivalent (if exists)
6. @educational tag (if student-facing)

```java
/**
 * Brief description of what method does.
 * 
 * <p>Longer explanation with examples if needed.
 * 
 * @param param1 Description of first parameter
 * @param param2 Description of second parameter
 * @return What the method returns
 * @throws ExceptionType When this exception is thrown
 * @pythonEquivalent python_method_name
 * @see #relatedMethod()
 */
@educational
public ReturnType methodName(Type1 param1, Type2 param2) {
```

### Testing Requirements

**Every new feature needs:**
1. Unit tests (80%+ coverage)
2. Integration test (if touches multiple components)
3. Hardware test (if possible)
4. Updated smoke test (if significant feature)

### Pull Request Process

1. **Fork** the repository
2. **Create branch** from `main`: `feature/your-feature-name`
3. **Write code** following style guide
4. **Add tests** achieving coverage requirements
5. **Update documentation**:
   - JavaDoc in code
   - API_COMPARISON.md if adding methods
   - CODRONE_EDU_METHOD_TRACKING.md if @educational
6. **Run all tests**: `./gradlew test`
7. **Create PR** with clear description
8. **Respond to review** feedback

### Review Criteria

PRs are evaluated on:
- ✓ Code quality and style
- ✓ Test coverage
- ✓ Documentation completeness
- ✓ Python compatibility (if applicable)
- ✓ Educational suitability (if student-facing)
- ✓ No breaking changes (unless justified)

---

## Summary

The CoDroneEdu Java library follows clear design principles:

1. **Educational First** - Students come before perfection
2. **Python Compatible** - Enable lesson reuse
3. **Type Safe** - But not at the cost of simplicity
4. **Explicit** - Clear naming and documentation
5. **Fail-Safe** - Protect students and equipment

The architecture uses managers for separation of concerns, three-tier APIs for flexibility, and comprehensive testing for reliability.

When contributing, remember: this library exists to help students learn. Make decisions that serve that goal, even if they differ from traditional library design.

---

**Document Info:**
- **Version**: 1.0
- **Audience**: Maintainers, Advanced Developers, Contributors
- **Last Updated**: November 2025
- **Word Count**: ~3,500 words

---

**Related Documentation:**
- Development History (development-history.md) - How we got here
- Student Guide (student-guide.md) - For end users
- Teacher Guide (teacher-guide.md) - For educators
- Knowledge Index (KNOWLEDGE_INDEX.md) - All documentation
