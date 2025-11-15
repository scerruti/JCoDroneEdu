# ElevationService Refactor Summary

**Date:** November 15, 2025  
**Branch:** altitude-height-api-refactor/issue-18

## What Changed

### New Architecture

Separated elevation calculation from raw sensor telemetry by introducing `ElevationService`:

```
Drone (student API)
  ↓
ElevationService (domain logic: formulas, weather, calibration)
  ↓
TelemetryService (sensor reads: pressure, temperature, uncorrected elevation)
  ↓
DroneStatus (raw protocol data)
```

### Responsibilities

**TelemetryService** (unchanged role):
- Request fresh sensor frames (Altitude, Range, Position, Motion, etc.)
- Cache and coalesce sensor reads
- Provide unit conversions for raw sensor data
- Handle freshness windows, RSSI scaling, backoff

**ElevationService** (new):
- Calculate corrected elevation using barometric formula
- Integrate with WeatherService for sea-level pressure
- Support multiple calibration modes (automatic, manual, coordinates)
- Provide unit conversions for elevation results

**Drone** (simplified):
- Delegate all elevation methods to `ElevationService`
- Maintain simple student-facing API
- No more inline formulas or WeatherService calls

## Code Changes

### New File
- `src/main/java/com/otabi/jcodroneedu/ElevationService.java`

### Modified Files
- `src/main/java/com/otabi/jcodroneedu/Drone.java`
  - Added `ElevationService` field
  - Initialized in constructor: `new ElevationService(telemetryService)`
  - Updated all elevation methods to delegate:
    - `getUncorrectedElevation()` → `elevationService.getUncorrectedElevation("m")`
    - `getUncorrectedElevation(unit)` → `elevationService.getUncorrectedElevation(unit)`
    - `getCorrectedElevation()` → `elevationService.getCorrectedElevation()`
    - `getCorrectedElevation(unit)` → `elevationService.getCorrectedElevation(unit)`
    - `getCorrectedElevation(pressure)` → `elevationService.getCorrectedElevation(pressure)`
    - `getCorrectedElevation(lat, lon)` → `elevationService.getCorrectedElevation(lat, lon)`
  - Removed inline barometric formula
  - Removed direct WeatherService calls

## API Surface (Unchanged)

Student-facing API remains identical:

```java
// Uncorrected (raw firmware)
double meters = drone.getUncorrectedElevation();
double feet = drone.getUncorrectedElevation("ft");

// Corrected (automatic calibration)
double meters = drone.getCorrectedElevation();
double feet = drone.getCorrectedElevation("ft");

// Corrected (manual pressure)
double meters = drone.getCorrectedElevation(101500.0);

// Corrected (weather API by coordinates)
double meters = drone.getCorrectedElevation(37.7749, -122.4194);

// Toggle-based (Python compatibility)
drone.useCorrectedElevation(true);
double meters = drone.getElevation();
double feet = drone.getElevation("ft");
```

## Benefits

### Separation of Concerns
- **TelemetryService**: pure sensor reads (no weather APIs, no formulas)
- **ElevationService**: domain calculations (no direct sensor access)
- **Drone**: thin delegation layer (student-friendly)

### Testability
- Can mock `TelemetryService` and `WeatherService` independently
- Can test elevation formulas without real drone hardware
- Can test sensor reads without weather API calls

### Maintainability
- Barometric formula lives in one place
- Weather integration centralized
- Future features (rate of climb, altitude history) have a natural home

### Code Quality
- Drone.java simplified (removed 20+ lines of calculation logic)
- Single Responsibility Principle: each service has one job
- Clear dependency graph

## Testing

- All existing tests pass ✅
- No test changes required (API surface unchanged)
- Build successful

## Next Steps

1. ✅ ElevationService created
2. ✅ Drone refactored to delegate
3. ✅ Tests passing
4. 🔲 Consider adding ElevationService-specific unit tests
5. 🔲 Update teacher documentation to mention ElevationService architecture
6. 🔲 Add JavaDoc examples showing the separation of concerns

## Related Documents

- `ALTITUDE_HEIGHT_AUDIT.md` - Parity analysis and current status
- `ELEVATION_API_QUICK_REFERENCE.md` - Student-facing elevation API guide
- `WEATHER_CALIBRATED_ELEVATION.md` - Calibration strategies and examples

---

**Note:** This refactor maintains 100% backward compatibility while improving code organization and separation of concerns.
