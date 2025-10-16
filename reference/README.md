# Reference Materials

This directory contains reference implementations and documentation used to ensure compatibility and accuracy of our Java test framework.

## Python CoDrone EDU Library

### Purpose
The Python library code serves as the authoritative reference for:
- Method signatures and parameter types
- Expected behavior and return values
- Error handling and exception patterns
- State management and internal logic

### Usage
- **DO NOT** modify files in this directory
- Use as read-only reference for MockDrone implementation
- Compare against when updating test framework
- Reference when investigating behavioral differences

### Updating
- Replace entire directory when new Python library version is released
- Update version tracking in CODRONE_EDU_METHOD_TRACKING.md
- Review changes for impact on Java test framework
- Test compatibility with existing assignments

### Structure
```
python-codrone-edu/
├── version.txt                    # Python library version
├── codrone_edu/
│   ├── drone.py                  # Main Drone class - PRIMARY REFERENCE
│   ├── protocol/                 # Communication protocols
│   ├── __init__.py              # Package initialization
│   └── ...                      # Supporting modules
└── setup.py                     # Package configuration
```

## Adding Python Library Code

1. **Download/Clone** the Python CoDrone EDU library
2. **Copy** the entire library to `reference/python-codrone-edu/`
3. **Create** `version.txt` with the library version number
4. **Update** the method tracking document
5. **Review** for new methods or signature changes

---

## Alternate UI Designs

### `alternate-sensor-dashboard-ui.html`
**Created:** October 15, 2025  
**Purpose:** Reference design for a modern web-based sensor dashboard UI

**Description:**  
HTML snippet from an alternate (possibly web-based) sensor visualization interface. This represents a more modern, graphical approach to displaying drone sensor data compared to our current Swing-based implementation.

**Key Features:**
- **Accelerometer:** Visual SVG icon with X/Y/Z axis values
- **Gyroscope:** 
  - Roll: Semi-circular range indicator (cyan/teal)
  - Yaw: Rotating arrow/compass (blue)
  - Pitch: Semi-circular range indicator (red/pink)
- **Range Sensors:** Horizontal slider visualizations (0-200cm)
- **Color Sensors:** HSV values displayed separately + color name + circular swatch
- **Additional Sensors:** Pressure (kPa) and Temperature (°C)
- **Position Display:** X/Y coordinates in cm

**Design Characteristics:**
- Dark theme with bright accent colors
- CSS modules for scoped styling
- Inline SVG graphics for icons
- Color-coded visualizations (cyan=roll, blue=yaw, red=pitch)
- More graphical/visual than our Swing panels

**Potential Applications:**
- Reference for improving current Swing UI
- Ideas for future JavaFX or web-based version
- Color scheme inspiration
- Layout patterns for sensor grouping

**File Location:** `/reference/alternate-sensor-dashboard-ui.html`
