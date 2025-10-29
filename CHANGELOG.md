# Changelog

All notable changes to this project will be documented in this file.

## v1.1.0 - 2025-10-28

### Added
- Renamed `getAltitudeData` to `getAltitude` for Java idiomatic usage.
- Added deprecated `getAltitudeData` method for Python compatibility.
- Updated documentation for altitude methods, clarifying usage and compatibility.
- Bumped project version to 1.1 (minor release).

### Changed
- Renamed sensor and range methods for consistency with Python API:
  - `getBottomDistance` replaced with `getBottomRange`
  - `getAltitudeData` replaced with `getAltitude`
  - Deprecated methods preserved for compatibility
- Converted several method names from `snake_case` to `camelCase` for Java API consistency.
- Improved API consistency between Java and Python versions.

### Notes
- This release introduces a minor version increment due to public API changes and compatibility improvements.

## v1.0.0 - 2025-10-15

- Add expanded smoke tests and examples: non-flying SmokeTest, controller display and buzzer tests, QuickLEDTest, and a guarded FlightSmokeTest (indoor-safe turning-only) with Gradle tasks `runSmokeTest`, `runControllerDisplayExample`, `runBuzzerTest`, `runQuickLEDTest`, and `runFlightSmokeTest`.

## Unreleased

- Normalize sensor scaling: centralize scales and update displays, tests, and docs (#1)
  - PR: #1 by @scerruti
  - Merge commit: `06d0b9481c5f90fc78f5e25696d6b87dec1a4feb`
  - Merged: 2025-10-14T01:54:48Z
  - Summary: Centralize sensor conversion constants (`DroneSystem.SensorScales`), fix endianness parsing in the receiver, update examples and GUI to display accelerometer in m/s^2 and g and angles in degrees, update `FlightController` getters and unit tests, and add documentation.

## Previously released

No prior changelog entries.
