## Elevation API - Quick Reference Card

## All Available Methods

Units supported for elevation methods: `m`, `cm`, `km`, `ft`, `mi`.
# Elevation API - Quick Reference Card

## All Available Methods

### 📊 Absolute Elevation Methods

```java
// 1. AUTOMATIC (RECOMMENDED) ✨
// Smart detection: OS location → IP location → Standard atmosphere
double elevation = drone.getCorrectedElevation();

// 2. MANUAL PRESSURE
// Specify exact sea-level pressure in Pascals
double elevation = drone.getCorrectedElevation(101325.0);

// 3. COORDINATES (WEATHER API)
// Uses real-time weather data at specified location
double elevation = drone.getCorrectedElevation(latitude, longitude);

// 4. UNCORRECTED (RAW FIRMWARE)
// Warning: Has ~100-150m offset, use for testing only
double rawElevation = drone.getUncorrectedElevation();

// 5. STATE-BASED (PYTHON COMPATIBILITY)
// Returns corrected or uncorrected based on flag (explicit methods preferred)
drone.useCorrectedElevation(true);
double elevation = drone.getElevation();
```

### 📏 Relative Height Methods (Change from Reference)

```java
// Set initial pressure reference (captures current pressure)
drone.setInitialPressure();

// Get height change in millimeters (default calibration)
double heightMm = drone.getHeightFromPressure();

// Get height change with custom calibration (b=bias, m=scale)
double heightMm = drone.getHeightFromPressure(bias, scale);
```

## Method Selection Guide

| Use Case | Best Method | Notes |
|----------|-------------|-------|
| **General classroom use** | `getCorrectedElevation()` | Just works! Auto-detects everything |
| **Known pressure** | `getCorrectedElevation(pressure)` | Manual override |
| **Known location** | `getCorrectedElevation(lat, lon)` | Uses weather API |
| **Change from start** | `setInitialPressure()` + `getHeightFromPressure()` | Relative measurement |
| **Firmware testing** | `getUncorrectedElevation()` | Raw data |
| **Python compatibility** | `useCorrectedElevation()` + `getElevation()` | Matches Python API |

## Accuracy Reference

| Method | Typical Error | Internet Required |
|--------|--------------|-------------------|
| Uncorrected | ±100-150m | ❌ No |
| Standard (101325 Pa) | ±5-10m | ❌ No |
| Automatic (IP location) | ±1-5m | ✅ Yes (fallback to standard) |
| Automatic (OS location) | ±0.5-3m | ✅ Yes (when JNI available) |
| Manual pressure | ±1-3m | ❌ No (if pressure accurate) |
| Weather API | ±1-3m | ✅ Yes |
| Relative height | ±0.1m | ❌ No |

## Automatic Detection Fallback

When you call `getCorrectedElevation()` without arguments:

```
1️⃣ Try OS location (JNI) → Weather API
   ↓ (if JNI not configured)
2️⃣ Try IP geolocation → Weather API
   ↓ (if offline)
3️⃣ Use standard atmosphere (101325 Pa)
   ✓ Always works!
```

## Example: Simple Altitude Display

```java
Drone drone = new Drone();
drone.pair();

// Get elevation - automatically calibrated!
// Freshness and pacing are handled internally by TelemetryService.
double elevation = drone.getCorrectedElevation();
System.out.printf("Elevation: %.2f meters\n", elevation);

drone.close();
```

## Example: Relative Height Tracking

```java
Drone drone = new Drone();
drone.pair();

// Set starting reference
drone.setInitialPressure();
System.out.println("Reference set - lift drone to see change");

while (true) {
    // TelemetryService handles requesting fresh altitude frames as needed
    double heightMm = drone.getHeightFromPressure();
    System.out.printf("Height change: %.1f mm (%.3f m)\n", heightMm, heightMm / 1000.0);
    Thread.sleep(100);
}
```

## Python API Compatibility

| Python Method | Java Equivalent |
|--------------|-----------------|
| `drone.get_height(unit)` | `drone.getHeight(unit)` (alias of bottom range; defaults to cm) |
| `drone.set_initial_pressure()` | `drone.setInitialPressure()` |
| `drone.height_from_pressure()` | `drone.getHeightFromPressure()` |
| `drone.height_from_pressure(b, m)` | `drone.getHeightFromPressure(b, m)` |
| `drone.get_elevation(unit)` | `drone.getElevation(unit)` (respects corrected toggle) |

## Configuration Examples

### Enable Python-style State Toggle
```java
drone.useCorrectedElevation(true);
double h1 = drone.getElevation();  // Returns corrected

drone.useCorrectedElevation(false);
double h2 = drone.getElevation();  // Returns uncorrected
```

### Set Custom OS Location Provider (JNI)
```java
WeatherService.setOSLocationProvider(new LocationProvider() {
    @Override
    public double[] getLocation() {
        // Your JNI implementation
        return NativeLib.getCurrentLocation();
    }
});

// Now getCorrectedElevation() will use OS location!
```

## Demo Programs

| Demo | Command | What It Shows |
|------|---------|---------------|
| Elevation API | `./gradlew runElevationApiDemo` | All 5 elevation methods |
| Weather Calibration | `./gradlew runCalibratedElevationDemo` | Before/after calibration |
| Automatic Detection | `./gradlew runAutomaticElevationDemo` | Fallback strategy |
| Relative Height | `./gradlew runRelativeHeightDemo` | Height change tracking |
| Weather Service | `./gradlew runWeatherServiceTest` | API testing (no drone) |

## Troubleshooting

**Problem:** Always getting standard atmosphere pressure  
**Solution:** Check internet connection, verify ipinfo.io not blocked

**Problem:** Want more accurate location  
**Solution:** Find JNI library, implement `LocationProvider` interface

**Problem:** Getting zero elevation  
**Solution:** Ensure the drone is connected; TelemetryService will request altitude frames automatically when needed.

**Problem:** Need exact pressure at my location  
**Solution:** Use `getCorrectedElevation(lat, lon)` with your coordinates

## Further Reading

- **WEATHER_CALIBRATED_ELEVATION.md** - Comprehensive documentation
- **AUTOMATIC_ELEVATION_SUMMARY.md** - Implementation details
- **ELEVATION_API_IMPLEMENTATION.md** - Original implementation notes

---

**💡 Pro Tip:** For most classroom use, just call `getCorrectedElevation()`—freshness and pacing are handled for you.

Note: Telemetry configuration constants are located at `DroneSystem.CommunicationConstants.TelemetryConfig`.
