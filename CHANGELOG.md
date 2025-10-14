# Changelog

All notable changes to this project will be documented in this file.

## Unreleased

- Normalize sensor scaling: centralize scales and update displays, tests, and docs (#1)
  - PR: #1 by @scerruti
  - Merge commit: `06d0b9481c5f90fc78f5e25696d6b87dec1a4feb`
  - Merged: 2025-10-14T01:54:48Z
  - Summary: Centralize sensor conversion constants (`DroneSystem.SensorScales`), fix endianness parsing in the receiver, update examples and GUI to display accelerometer in m/s^2 and g and angles in degrees, update `FlightController` getters and unit tests, and add documentation.

## Previously released

No prior changelog entries.
