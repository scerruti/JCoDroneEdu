# Python Library Version 2.3 vs 2.4 Detailed Comparison

**Analysis Date**: October 15, 2025  
**Method**: Downloaded wheels from PyPI and performed code diff

---

## Summary of Changes

### Files Modified
- ✅ **NEW FILE**: `errors.py` (4,264 bytes) - Custom exception classes
- 📝 `__init__.py` - Added `errors` to exports
- 📝 `drone.py` - 289KB → 304KB (+15KB, ~5% larger)
- 📝 `swarm.py` - 11KB → 20KB (+9KB, ~87% larger!)
- 📝 `system.py` - 14.3KB → 14.6KB (+265 bytes)
- 📝 `protocol.py` - 85.9KB → 84.8KB (-1.1KB, slight reduction)

---

## Key Findings

### ❌ MISCONCEPTION CORRECTED!

**Initial assumption**: Inventory methods were NEW in 2.4  
**Reality**: Inventory methods ALREADY EXISTED in 2.3!

The following methods were present in BOTH versions:
- ✅ `get_information_data()` - Line 1950 in 2.3
- ✅ `get_cpu_id_data()` - Line 1902 in 2.3
- ✅ `get_address_data()` - Present in 2.3
- ✅ `get_count_data()` - Line 1824 in 2.3
- ✅ `get_flight_time()` - Line 1840 in 2.3
- ✅ `get_takeoff_count()` - Present in 2.3
- ✅ `get_landing_count()` - Present in 2.3
- ✅ `get_accident_count()` - Line 1876 in 2.3

**Conclusion**: These methods existed in April 2025 (2.3) - we just didn't know about them!

---

## Actual Changes in Version 2.4

### 1. ✅ NEW: Exception Classes (`errors.py`)

**Purpose**: Better error handling for color detection, screen drawing, and swarm operations

**Exception Hierarchy**:
```python
# Color Processing Errors
ColorProcessingError (base)
├── LoadColorsetError
├── DetectColorError
├── FolderNotFoundError
├── ColorsetNotFoundError
└── ColorNotFoundError

# Screen Drawing Errors
ScreenDrawError (base)
├── InvalidDrawColorError
├── InvalidPointListError
├── InvalidImageError
├── InvalidAlignmentError
└── InvalidPixelListError

# Swarm Errors
SwarmError (base)
├── InvalidSyncError
└── InvalidOrderError
```

**Impact**: Better error messages, clearer debugging for students

---

### 2. ⚠️ Temperature Deprecation (Already in 2.3!)

**Deprecation warning was ALREADY present in 2.3**:
```python
# Version 2.3 (April 2025):
print("Warning: The 'drone.get_temperature()' function is deprecated 
       and will be removed in a future release.
       Please use 'drone.get_drone_temperature()'")
```

This means the deprecation happened BEFORE April 2025 (possibly in 2.2 or earlier).

---

### 3. 🔄 Default Speed Changes

**Movement methods changed default speed from 1.0 → 0.5**:

```python
# Version 2.3:
def move_forward(self, distance, units="cm", speed=1.0)
def move_backward(self, distance, units="cm", speed=1.0)
def move_left(self, distance, units="cm", speed=1.0)
def move_right(self, distance, units="cm", speed=1.0)

# Version 2.4:
def move_forward(self, distance, units="cm", speed=0.5)  # SLOWER
def move_backward(self, distance, units="cm", speed=0.5)  # SLOWER
def move_left(self, distance, units="cm", speed=0.5)      # SLOWER
def move_right(self, distance, units="cm", speed=0.5)     # SLOWER
```

**Reason**: Slower default speeds are safer for students, more predictable movement

**Impact**: 
- ✅ Safer default behavior
- ⚠️ Existing code will run slower unless speed explicitly set
- Backward compatible (parameter still available)

---

### 4. 🆕 New/Modified Methods

#### `turn_direction()` - NEW unified turn method
```python
# Version 2.4 adds:
def turn_direction(self, degree=90, timeout=3, p_value=10)
```

**Purpose**: Unified method for turning in any direction
- Positive degrees: turn right
- Negative degrees: turn left
- New `p_value` parameter for proportional control tuning

**Impact**: More flexible than separate `turn_left()`/`turn_right()` methods

#### `_hsv_to_rgb()` - NEW helper
```python
def _hsv_to_rgb(self, h, s, v)
```

**Purpose**: Color space conversion (HSV → RGB)  
**Use**: Likely for LED color calculations

#### `_reopen_emscripten()` - NEW async method
```python
async def _reopen_emscripten(self)
```

**Purpose**: Better connection handling for web-based Python (PyScript/Emscripten)  
**Impact**: Improves browser-based drone programming

---

### 5. 📈 Swarm Module Expansion

**Size grew 87%** (11KB → 20KB)

Likely additions:
- Better synchronization
- More error handling (uses new `SwarmError` exceptions)
- Improved multi-drone coordination

**TODO**: Detailed swarm diff needed

---

### 6. 📉 Protocol Module Reduction

**Size decreased 1.3%** (85.9KB → 84.8KB)

- 13 fewer lines (3,368 → 3,355)
- Possible refactoring or dead code removal
- No major functionality changes detected

---

## Timeline Correction

### Previous Understanding ❌
```
Version 2.4 (July 2025):
  - NEW: Inventory methods
  - NEW: get_information_data(), get_cpu_id_data(), etc.
```

### Corrected Understanding ✅
```
Version 2.3 or earlier (≤ April 2025):
  - Inventory methods ALREADY EXISTED
  - Temperature deprecation ALREADY IN PLACE

Version 2.4 (July 2025):
  - NEW: errors.py exception classes
  - CHANGED: Default movement speed (1.0 → 0.5)
  - NEW: turn_direction() unified method
  - NEW: HSV to RGB color conversion
  - IMPROVED: Swarm functionality
  - IMPROVED: Web platform support
```

---

## Implications for Java Implementation

### ✅ Good News
We now know inventory methods have been stable since at least April 2025 (possibly earlier).

### 📋 Implementation Priority

**High Priority** (Python 2.3+):
1. Inventory methods - Stable since April 2025
   - `get_information_data()`
   - `get_cpu_id_data()`
   - `get_address_data()`
   - `get_count_data()` + helpers

**Medium Priority** (Python 2.4+):
2. Custom exception classes
   - Java has better type system, but custom exceptions helpful
3. Default speed adjustment
   - Update our defaults to 0.5 (safer)
4. Turn direction method
   - Unified API for turning

**Low Priority**:
5. HSV color conversion - Internal utility
6. Swarm improvements - Advanced feature
7. Emscripten support - Not applicable to Java

---

## Recommended Actions

### 1. Update Our VERSION_HISTORY.md
- Inventory methods existed since ≤2.3
- Need to check when they were actually added (possibly 2.2 or 2.1)

### 2. Prioritize Inventory Methods for Java v1.1.0
- These have been stable for 6+ months
- Safe to implement now

### 3. Update Default Movement Speeds
- Change Java defaults from 1.0 → 0.5 to match Python 2.4
- Document as safer for educational use

### 4. Document Exception Handling
- While Java has type system, document equivalent Java exceptions
- Create mapping: Python errors → Java exceptions

---

## Further Research Needed

### Version 2.2 Analysis
**Question**: When were inventory methods actually added?
- Download 2.2 (March 2025)
- Check if inventory methods exist
- Possibly added in 2.2 (same month as firmware 25.2.1)

### Version 2.0 → 2.1 Analysis
**Question**: What changed in the major version bump?
- Compare 1.9 vs 2.0
- Identify breaking changes
- Document API restructuring

---

## File Listing Comparison

### Version 2.3 (April 2025)
```
__init__.py      (102 bytes)
__main__.py      (120 bytes)
crc.py           (4,525 bytes)
drone.py         (289,476 bytes) ← Main library
protocol.py      (85,852 bytes)
receiver.py      (6,875 bytes)
storage.py       (2,830 bytes)
swarm.py         (11,018 bytes)
system.py        (14,302 bytes)
data/            (directory with 10 items)
tools/           (directory with 7 items)
```

### Version 2.4 (July 2025)
```
__init__.py      (116 bytes)   ← +14 bytes (added errors import)
__main__.py      (120 bytes)
crc.py           (4,525 bytes)
drone.py         (304,259 bytes) ← +14,783 bytes (main changes)
errors.py        (4,264 bytes)   ← NEW FILE
protocol.py      (84,790 bytes)  ← -1,062 bytes (cleanup)
receiver.py      (6,875 bytes)
storage.py       (2,830 bytes)
swarm.py         (20,635 bytes)  ← +9,617 bytes (major expansion)
system.py        (14,567 bytes)  ← +265 bytes
data/            (directory with 10 items)
tools/           (directory with 7 items)
```

---

## Conclusion

Version 2.4 is an **incremental improvement** over 2.3, not a major feature release:

- ✅ Better error handling (exception classes)
- ✅ Safer defaults (slower movement speeds)
- ✅ Improved swarm functionality
- ✅ Better web platform support

**The big revelation**: Inventory methods existed much earlier than we thought!

**Next Steps**:
1. Analyze 2.2 vs 2.3 to find when inventory was added
2. Implement inventory methods in Java v1.1.0 (now proven stable)
3. Update default speeds to match Python 2.4
4. Consider implementing `turn_direction()` unified API

---

**Analysis performed by**: JCoDroneEdu Development Team  
**Tools used**: pip download, unzip, diff, grep  
**Date**: October 15, 2025
