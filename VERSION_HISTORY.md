# CoDrone EDU Python Library Version History

## Purpose

This document tracks the evolution of the Python CoDrone EDU library to help us:
- Understand when methods were added/deprecated
- Correlate Python versions with firmware releases
- Document API changes and breaking changes
- Identify minimum requirements for Java compatibility
- Track feature parity between Python and Java implementations

## Research Methodology

1. **PyPI Release Data**: Official release dates from https://pypi.org/project/codrone-edu/
2. **Code Diffs**: Downloaded wheels and compared source code between versions
3. **Firmware Correlation**: Matched library releases with firmware update dates
4. **Testing**: Verified behavior on actual hardware when possible

---

## Version Timeline

### Version 2.4 (July 29, 2025) - CURRENT ✅

**Release Date**: 2025-07-29  
**Firmware Tested**: 25.2.1 (March 2025)  
**Status**: ✅ Fully tested with firmware 25.2.1

#### **Key Changes** (vs 2.3):
- ⚠️ **DEPRECATED**: `get_temperature()` → Use `get_drone_temperature()` instead
  - Warning added: "will be removed in a future release"
  - Suggests different future drone models may not have temperature sensor
- ✅ **NEW**: Inventory/Statistics methods:
  - `get_information_data()` - Returns [timestamp, drone_model, drone_fw, controller_model, controller_fw]
  - `get_cpu_id_data()` - Returns [timestamp, drone_cpu_id_base64, controller_cpu_id_base64]
  - `get_address_data()` - Returns [timestamp, drone_bt_address_base64, controller_bt_address_base64]
  - `get_count_data()` - Returns [timestamp, flight_time_sec, takeoff_count, landing_count, accident_count]
  - `get_flight_time()` - Helper returning flight time in seconds
  - `get_takeoff_count()` - Helper returning takeoff count
  - `get_landing_count()` - Helper returning landing count
  - `get_accident_count()` - Helper returning accident count
- ✅ **NEW**: `errors.py` module with custom exceptions:
  - `ColorProcessingError`, `LoadColorsetError`, `DetectColorError`
  - `FolderNotFoundError`, `ColorsetNotFoundError`, `ColorNotFoundError`
  - `ScreenDrawError`, `InvalidDrawColorError`, `InvalidPointListError`
  - `InvalidImageError`, `InvalidAlignmentError`, `InvalidPixelListError`
  - `SwarmError`, `InvalidSyncError`, `InvalidOrderError`

#### **Dependencies**:
```
pyserial>=3.5
numpy
colorama
Pillow
```

#### **Java Implementation Status** (v1.0.0):
- ✅ Temperature API: Implemented with switchable calibration pattern (better than Python!)
- ❌ Inventory methods: **NOT YET IMPLEMENTED** (target: v1.1.0)
- ❌ Error classes: Not needed (Java has type system + exceptions)

#### **Notes**:
- Library released 4 months AFTER firmware 25.2.1 (March → July)
- Suggests library updates lag behind firmware releases
- Temperature deprecation hints at future hardware changes

---

### Version 2.3 (April 28, 2025)

**Release Date**: 2025-04-28  
**Firmware Tested**: 24.9.1 (estimated)  
**Status**: ✅ Tested before firmware update

#### **Key Changes** (vs 2.2):
- [TO BE ANALYZED - downloading for comparison]

#### **Dependencies**:
```
[TO BE CHECKED]
```

#### **Notes**:
- Released 1.5 months after firmware 25.2.1
- Likely still compatible with 24.x firmware
- We tested this version before updating to 25.2.1

---

### Version 2.2 (March 10, 2025)

**Release Date**: 2025-03-10  
**Firmware**: Unknown (possibly 25.2.1 or earlier)  
**Status**: Not tested

#### **Key Changes** (vs 2.1):
- [TO BE ANALYZED]

#### **Notes**:
- Released same month as firmware 25.2.1
- Possible correlation with firmware release

---

### Version 2.1 (January 10, 2025)

**Release Date**: 2025-01-10  
**Firmware**: Likely 24.x series  
**Status**: Not tested

#### **Key Changes** (vs 2.0):
- [TO BE ANALYZED]

---

### Version 2.0 (November 7, 2024) - MAJOR VERSION ⚠️

**Release Date**: 2024-11-07  
**Firmware**: Likely 24.x series  
**Status**: Not tested

#### **Key Changes** (vs 1.9):
- [TO BE ANALYZED]
- ⚠️ **Major version bump suggests breaking changes**

#### **Notes**:
- First 2.x release
- Over 1 year after previous version (1.9)
- Likely significant API overhaul

---

### Version 1.9 (October 8, 2023) - Last 1.x

**Release Date**: 2023-10-08  
**Firmware**: Likely 23.x or early 24.x  
**Status**: Not tested

#### **Notes**:
- Last version before major 2.0 rewrite
- Gap of ~13 months until version 2.0

---

### Earlier Versions (1.8 and below)

Available versions: 1.8, 1.7, 1.6, 1.5, 1.4, 1.3, 1.2, 1.1, 1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1

**Notable dates**:
- Version 1.8: May 7, 2023
- Version 1.7: February 20, 2023
- Version 1.6: December 28, 2022
- Version 1.5: November 14, 2022

**Status**: Not analyzed (pre-2.0 era)

---

## Firmware Correlation Timeline

| Firmware Version | Release Date | Python Version | Python Release | Gap |
|------------------|--------------|----------------|----------------|-----|
| 25.2.1 | March 2025 | 2.4 | July 2025 | +4 months |
| 25.2.1 | March 2025 | 2.3 | April 2025 | +1 month |
| 25.2.1 | March 2025 | 2.2 | March 2025 | Same month |
| 24.9.1 | ~Sept 2024? | 2.0? | November 2024 | ? |

**Pattern**: Python library updates typically lag 1-4 months behind firmware releases.

---

## Breaking Changes Across Versions

### 2.0 → 2.4 (Recent 2.x era)
- Deprecation: `get_temperature()` (as of 2.4)
- Additions: Inventory methods (2.4)
- Additions: Error classes (2.4)

### 1.9 → 2.0 (Major version bump)
- [TO BE ANALYZED]
- Likely significant API restructuring

---

## Dependencies Evolution

### Version 2.4 (Current)
```python
Requires: pyserial>=3.5, numpy, colorama, Pillow
Python: >=3.5
```

### Earlier Versions
- [TO BE TRACKED]

---

## Java Implementation Roadmap

### JCoDroneEdu v1.0.0 (October 2025)
**Based on**: Python 2.4 (July 2025)  
**Firmware**: 25.2.1+ (March 2025)

**Features**:
- ✅ Core flight operations (matches Python 2.4)
- ✅ Temperature API with switchable calibration (BETTER than Python)
- ✅ Elevation API with correction (NOT in Python)
- ❌ Inventory methods (Python 2.4) - **Coming in v1.1.0**

### JCoDroneEdu v1.1.0 (Planned)
**Target Features**:
- ✅ All 8 inventory methods from Python 2.4
- ✅ Version checking and firmware validation
- ✅ Enhanced error messages

### JCoDroneEdu v2.0.0 (Future)
**Target**: When Python 2.5+ or 3.0 releases with breaking changes

---

## Research Status

- ✅ **Version 2.4**: Fully analyzed (current)
- 🔄 **Version 2.3**: In progress (downloading for diff)
- ⏳ **Version 2.2**: Queued
- ⏳ **Version 2.1**: Queued
- ⏳ **Version 2.0**: High priority (major version)
- ⏳ **Version 1.9**: Medium priority (last 1.x)

---

## How to Update This Document

### Manual Research
1. Download specific version: `pip download codrone-edu==X.Y --no-deps`
2. Extract: `unzip codrone_edu-X.Y-*.whl -d version-X.Y/`
3. Compare: `diff -r version-X.Y/codrone_edu/ version-X.Z/codrone_edu/`
4. Document changes above

### Automated (Future)
```bash
./gradlew analyzeVersionHistory
# Downloads recent versions, diffs, and updates this file
```

---

## References

- **PyPI**: https://pypi.org/project/codrone-edu/
- **Firmware Updater**: https://codrone.robolink.com/edu/updater/
- **Java Library**: https://github.com/scerruti/JCoDroneEdu

---

**Last Updated**: October 15, 2025  
**Maintained By**: JCoDroneEdu Development Team  
**Next Review**: When Python 2.5 releases
