# Controller Input Refactoring

## Overview
Refactored controller input data storage to follow the established architectural pattern used by other data managers (DroneStatus, LinkManager, InventoryManager). Controller input data is now stored in `ControllerInputManager` instead of directly in the `Drone` class.

## Motivation
**Architectural Consistency**: Previously, controller input data was stored as volatile arrays (`buttonData`, `joystickData`) directly in the `Drone` class, violating the manager pattern used throughout the codebase. This refactoring aligns controller input with the existing architecture where:
- Sensor/state data → `DroneStatus`
- Link information → `LinkManager`
- Inventory data → `InventoryManager`
- **Controller input → `ControllerInputManager`** (NEW)

## Implementation Details

### New Classes

#### 1. ControllerInputManager
**Location**: `src/main/java/com/otabi/jcodroneedu/ControllerInputManager.java`

**Purpose**: Central storage and management of controller input state with named fields.

**Named Fields**:
- Button data: `buttonTimestamp` (double), `buttonFlags` (int), `buttonEventName` (String)
- Joystick data: `joystickTimestamp` (int), `leftX`, `leftY`, `leftDirection`, `leftEvent`, `rightX`, `rightY`, `rightDirection`, `rightEvent`

**Thread Safety**: All updates are synchronized, fields are volatile where appropriate.

#### 2. ButtonData
**Location**: `src/main/java/com/otabi/jcodroneedu/composite/ButtonData.java`

**Purpose**: Immutable composite object for type-safe button state access.

**Fields**:
- `timestamp` (double): Time when button event occurred
- `buttonFlags` (int): Bitwise flags (0=none, 1=L1, 2=L2, 4=R1, 8=R2)
- `eventName` (String): Event type ("None_", "Press", "Down", "Up")

#### 3. JoystickData
**Location**: `src/main/java/com/otabi/jcodroneedu/composite/JoystickData.java`

**Purpose**: Immutable composite object for type-safe joystick state access.

**Fields**:
- `timestamp` (int): Time when joystick data was captured
- `leftX`, `leftY` (int): Left joystick position (-100 to 100, 0=neutral)
- `leftDirection`, `leftEvent` (int): Left joystick direction/event codes
- `rightX`, `rightY` (int): Right joystick position (-100 to 100, 0=neutral)
- `rightDirection`, `rightEvent` (int): Right joystick direction/event codes

### Three-Tier Access Pattern

The controller input API follows a three-tier access pattern matching the inventory methods design:

#### Tier 1: Python-Compatible Arrays (Legacy API)
For backward compatibility with existing code and Python API:
```java
Object[] buttonData = drone.getButtonData();  // [timestamp, buttonFlags, eventName]
int[] joystickData = drone.getJoystickData(); // [timestamp, leftX, leftY, ..., rightEvent]
```

**Note**: JavaDoc on these methods guides users to prefer composite objects for Java code.

#### Tier 2: Individual Typed Getters (Simple Access)
For simple, type-safe access to individual values:
```java
int leftX = drone.getLeftJoystickX();
int leftY = drone.getLeftJoystickY();
int rightX = drone.getRightJoystickX();
int rightY = drone.getRightJoystickY();
int flags = drone.getButtonFlags();
String eventName = drone.getButtonEventName();
```

#### Tier 3: Java Composite Objects (Best Practice)
**RECOMMENDED for new Java code** - type-safe, immutable, self-documenting:
```java
ButtonData buttonData = drone.getButtonDataObject();
System.out.println("Button pressed: " + buttonData.getEventName());
System.out.println("Flags: " + buttonData.getButtonFlags());

JoystickData joystickData = drone.getJoystickDataObject();
System.out.println("Left joystick: (" + joystickData.getLeftX() + ", " + joystickData.getLeftY() + ")");
System.out.println("Right joystick: (" + joystickData.getRightX() + ", " + joystickData.getRightY() + ")");
```

## Modified Classes

### Drone.java
**Changes**:
- **Added**: `private final ControllerInputManager controllerInputManager;`
- **Removed**: `private volatile Object[] buttonData;` and `private volatile int[] joystickData;`
- **Updated Constructor**: Initializes `ControllerInputManager` and passes to `Receiver`
- **Removed Method**: `initializeControllerInputData()` (no longer needed)
- **Updated Methods**: All getter/update methods now delegate to `ControllerInputManager`
- **New Methods**: 
  - `getButtonDataObject()` → returns `ButtonData`
  - `getJoystickDataObject()` → returns `JoystickData`
- **Refactored**: `isButtonPressed()` private helper now uses `ButtonData` instead of array unpacking

**Migration**:
- All existing code continues to work (array methods preserved)
- New code should prefer `getButtonDataObject()` and `getJoystickDataObject()`

### Receiver.java
**Changes**:
- **Added**: `private final ControllerInputManager controllerInputManager;` field
- **Updated Constructor**: Added 5th parameter `ControllerInputManager`
- **Updated Handlers**: Button and Joystick handlers now call `controllerInputManager.updateButtonData()` and `controllerInputManager.updateJoystickData()` instead of `drone.updateButtonData()` / `drone.updateJoystickData()`

### ControllerInputPanel.java (GUI)
**Changes**:
- **Added Imports**: `ButtonData` and `JoystickData`
- **Refactored**: `updateButtonStates()` now uses `ButtonData` instead of array with casting
- **Refactored**: Joystick polling now uses `JoystickData` composite object
- **Benefits**: 
  - Eliminated type casting (`instanceof Integer`, etc.)
  - Eliminated null checks
  - Cleaner, more readable code
  - Type-safe at compile time

**Before**:
```java
Object[] buttonData = drone.getButtonData();
int buttonFlags = 0;
String eventName = "None_";
if (buttonData != null && buttonData.length >= 3) {
    if (buttonData[1] instanceof Integer) {
        buttonFlags = (Integer) buttonData[1];
    }
    if (buttonData[2] instanceof String) {
        eventName = (String) buttonData[2];
    }
}
```

**After**:
```java
ButtonData buttonData = drone.getButtonDataObject();
int buttonFlags = buttonData.getButtonFlags();
String eventName = buttonData.getEventName();
```

## Migration Guide

### For Existing Code
**No changes required** - all existing array-based API calls continue to work:
```java
// This still works exactly as before
Object[] buttonData = drone.getButtonData();
int[] joystickData = drone.getJoystickData();
int leftX = drone.getLeftJoystickX();
```

### For New Code
**Use composite objects** for better type safety and readability:

**Before** (legacy approach):
```java
Object[] buttonData = drone.getButtonData();
double timestamp = (double) buttonData[0];
int flags = (int) buttonData[1];
String event = (String) buttonData[2];
```

**After** (recommended approach):
```java
ButtonData buttonData = drone.getButtonDataObject();
double timestamp = buttonData.getTimestamp();
int flags = buttonData.getButtonFlags();
String event = buttonData.getEventName();
```

**Before** (legacy approach):
```java
int[] joystickData = drone.getJoystickData();
int leftX = joystickData[1];
int leftY = joystickData[2];
int rightX = joystickData[5];
int rightY = joystickData[6];
```

**After** (recommended approach):
```java
JoystickData joystickData = drone.getJoystickDataObject();
int leftX = joystickData.getLeftX();
int leftY = joystickData.getLeftY();
int rightX = joystickData.getRightX();
int rightY = joystickData.getRightY();
```

## Best Practices

### When to Use Each Tier

1. **Array Methods** (`getButtonData()`, `getJoystickData()`):
   - Python API compatibility
   - Legacy code maintenance
   - **Not recommended for new Java code**

2. **Individual Getters** (`getLeftJoystickX()`, etc.):
   - When you only need 1-2 specific values
   - Simple, quick access
   - Good for basic checks

3. **Composite Objects** (`getButtonDataObject()`, `getJoystickDataObject()`):
   - **RECOMMENDED for new Java code**
   - When processing multiple related values
   - Type-safe, self-documenting
   - Immutable (thread-safe)
   - Better IDE autocomplete support

### Example: Button Checking

**Simple check** (individual getter):
```java
if (drone.getButtonFlags() == 1) {  // L1 button
    System.out.println("L1 pressed");
}
```

**Complex processing** (composite object):
```java
ButtonData buttonData = drone.getButtonDataObject();
if (buttonData.getEventName().equals("Press")) {
    int flags = buttonData.getButtonFlags();
    if ((flags & 1) != 0) System.out.println("L1 pressed");
    if ((flags & 2) != 0) System.out.println("L2 pressed");
    if ((flags & 4) != 0) System.out.println("R1 pressed");
    if ((flags & 8) != 0) System.out.println("R2 pressed");
}
```

### Example: Joystick Processing

**Simple check** (individual getter):
```java
int leftX = drone.getLeftJoystickX();
if (Math.abs(leftX) > 50) {
    System.out.println("Left joystick pushed horizontally");
}
```

**Complex processing** (composite object):
```java
JoystickData joystickData = drone.getJoystickDataObject();
int magnitude = (int) Math.sqrt(
    joystickData.getLeftX() * joystickData.getLeftX() +
    joystickData.getLeftY() * joystickData.getLeftY()
);
if (magnitude > 50) {
    System.out.println("Left joystick engaged: " + joystickData);
}
```

## Testing

**Compilation**: ✅ Verified with `./gradlew compileJava` (BUILD SUCCESSFUL)

**Backward Compatibility**: All existing API methods preserved and functional.

**Thread Safety**: All updates synchronized in `ControllerInputManager`.

## Future Considerations

1. **Button Flag Constants**: Consider adding constants to `ButtonData` or `Drone`:
   ```java
   public static final int BUTTON_L1 = 1;
   public static final int BUTTON_L2 = 2;
   public static final int BUTTON_R1 = 4;
   public static final int BUTTON_R2 = 8;
   ```

2. **Event Name Constants**: Consider adding constants for event names:
   ```java
   public static final String EVENT_NONE = "None_";
   public static final String EVENT_PRESS = "Press";
   public static final String EVENT_DOWN = "Down";
   public static final String EVENT_UP = "Up";
   ```

3. **Helper Methods**: Consider adding convenience methods to `ButtonData`:
   ```java
   public boolean isButtonPressed(int buttonFlag) {
       return (this.buttonFlags & buttonFlag) != 0;
   }
   public boolean isL1Pressed() { return isButtonPressed(1); }
   public boolean isL2Pressed() { return isButtonPressed(2); }
   // etc.
   ```

4. **Deprecation Path**: Consider deprecating array methods in future releases with:
   ```java
   @Deprecated(since = "2.5", forRemoval = true)
   public Object[] getButtonData() { ... }
   ```

## Related Documentation
- `API_DESIGN_PHILOSOPHY.md` - Overall API design patterns
- `INVENTORY_DATA_ACCESS_PATTERNS.md` - Similar three-tier pattern for inventory
- `APCSA_COMPLIANT_API_DOCUMENTATION.md` - Student-facing API documentation
