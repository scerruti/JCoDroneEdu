---
layout: guide
category: Architecture
title: API Patterns
---

# Common API Patterns

This guide documents the patterns and conventions used throughout the CoDrone EDU API.

## Pattern: Temporal Commands

Many flight commands include duration parameters:

```java
drone.moveForward(distance, time);
drone.hover(seconds);
```

## Pattern: Sensor Reading

Sensor data follows a consistent read pattern:

```java
int altitude = drone.getAltitude();
int battery = drone.getBatteryLevel();
```

## Pattern: Configuration

Device configuration methods are grouped in controller classes:

```java
SettingsController settings = new SettingsController(drone);
settings.setTrim(pitch, roll, yaw, throttle);
```

## Pattern: Event Handling

Asynchronous events use callbacks and futures for notification.

## See Also

- [Design Principles](/architecture/design-guide/principles.html)
- [Adding Methods](/architecture/design-guide/adding-methods.html)
