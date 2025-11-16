---
layout: guide
category: Architecture
title: Design Principles
---

# API Design Principles

This page documents the core design principles that guide the CoDrone EDU API architecture.

## Core Principles

### 1. Simplicity First
The API prioritizes ease of learning over advanced features. Beginners should understand what a method does from its name.

### 2. Consistency
Similar operations use similar naming conventions and parameter patterns.

### 3. Feedback
Methods provide immediate feedback through return values or exceptions.

### 4. Educational Value
All public methods are tagged with `@educational` metadata for curriculum integration.

## Method Naming Conventions

- Verbs for actions: `takeoff()`, `hover()`, `land()`
- Questions for queries: `getAltitude()`, `getBatteryLevel()`
- Clear, non-abbreviated names: `moveForward()` not `mvFwd()`

## See Also

- [Adding Methods](/architecture/design-guide/adding-methods.html)
- [Adding Sensors](/architecture/design-guide/adding-sensors.html)
- [Testing Strategy](/architecture/design-guide/testing.html)
