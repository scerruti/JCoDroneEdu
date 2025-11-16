---
layout: guide
title: Hardware Guide
category: Teacher Guide
permalink: /guides/teacher/hardware.html
---

## Drone Hardware Selection & Setup

This guide helps you select, configure, and maintain drones for your classroom.

---

## Drone Selection Criteria

### What Makes a Good Classroom Drone?

**Essential Features:**
- ✓ Stable flight (beginner-friendly)
- ✓ Durable (survives crashes)
- ✓ Affordable ($200-400)
- ✓ Replaceable parts (props, batteries)
- ✓ USB connectivity (no proprietary protocols)
- ✓ Good sensor suite (battery, height, range)
- ✓ ~20-30 minute flight time per battery

**Avoid:**
- ✗ Extreme speed (unforgiving for beginners)
- ✗ Fragile designs (expensive to maintain)
- ✗ Proprietary software (limits learning)
- ✗ Complex setup procedures
- ✗ Limited sensor options

### Recommended Drones

**Primary Recommendation: Tello by DJI**

| Spec | Value |
|------|-------|
| Price | $99-129 |
| Flight Time | ~13 minutes |
| Stability | Excellent |
| Durability | Good (plastic frame) |
| Sensors | 5MP camera, IMU, barometer |
| Connectivity | USB, WiFi |
| Pros | Affordable, stable, fun |
| Cons | Shorter flight time, basic sensors |

**Alternative: CoDrone Lite (Robolinks)**

| Spec | Value |
|------|-------|
| Price | $200-300 |
| Flight Time | ~20 minutes |
| Stability | Very Stable |
| Durability | Excellent (prop guards) |
| Sensors | Range, temperature, IMU, barometer |
| Connectivity | USB, extensive sensor suite |
| Pros | Perfect for learning, rich sensors, durable |
| Cons | Higher cost, less "cool factor" |

**Budget Option: Protocol X (Silverlit)**

| Spec | Value |
|------|-------|
| Price | $50-70 |
| Flight Time | ~8 minutes |
| Stability | Good |
| Durability | Fair |
| Sensors | Basic (IMU, barometer) |
| Connectivity | USB, simple API |
| Pros | Very affordable, fun |
| Cons | Short flight time, limited sensors |

---

## Hardware Inventory

### Minimum Setup (Classroom of 20)

```
Item                          Quantity    Unit Cost    Total
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Drones (CoDrone Lite)         4           $250         $1,000
Batteries (per drone)         2           $30          $240
USB Charging Cable            6           $15          $90
Propeller Set (24 props)      2           $40          $80
Drone Protective Case         4           $50          $200
Flight Area Cones             6           $20          $120
USB Extender Cables           3           $10          $30
Battery Charger/Monitor       1           $100         $100
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
TOTAL                                                  $1,860
```

**Note:** Assumes sharing computers (4 drones → 8 students at a time)

### Per Drone Checklist

- [ ] Drone unit (with propellers installed)
- [ ] 2 Batteries (replacements for flight rotation)
- [ ] USB charging cable
- [ ] Prop guards (if applicable)
- [ ] Spare propeller set (4-pack minimum)
- [ ] Storage case (foam or hard case)
- [ ] User manual (printed or digital)
- [ ] Firmware version documented

---

## Initial Setup

### Before First Use

**1. Inspect Hardware**
```
□ Propellers attached securely
□ No visible damage or cracks
□ Gimbal/camera moves freely
□ Battery contacts clean
□ USB port not obstructed
□ All cables intact
```

**2. Charge Batteries (4-6 hours first time)**
- Use recommended charger
- Don't leave unattended
- Store in cool, dry place

**3. Update Firmware**
- Connect to computer
- Check manufacturer updates
- Follow update procedure
- Document firmware version

**4. Calibration**
```java
// Test program to verify calibration
try (Drone drone = new Drone(true)) {
    System.out.println("Gimbal calibration: " + drone.isGimbalCalibrated());
    System.out.println("IMU ready: " + drone.isReady());
}
```

---

## Flight Area Setup

### Indoor Flight Space

**Requirements:**
- Minimum: 15ft × 15ft × 10ft ceiling
- Flat, obstacle-free floor
- No hanging fixtures or ceiling fans
- Good lighting (LED preferred)
- Ventilation adequate

**Setup:**
```
┌─────────────────────────────────────┐
│ 10ft ceiling minimum                │
│ ┌───────────────────────────────┐   │
│ │                               │   │
│ │   Flying Area (10ft × 10ft)   │   │
│ │   Marked with cones or tape   │   │
│ │                               │   │
│ └───────────────────────────────┘   │
│                                     │
│  Testing Station    Battery Charging│
│  (Computers)        (Safe spot)     │
│                                     │
└─────────────────────────────────────┘
```

### Outdoor Flight Space

**Advantages:**
- More space for complex patterns
- No echo interference (ultrasonic sensors)
- More "real world" testing

**Requirements:**
- Clear of obstacles (trees, buildings, people)
- Flat ground
- Calm weather (low wind)
- Away from airports (regulations)
- Adequate lighting

**Weather Constraints:**
- Wind: < 10 mph ideal, < 15 mph maximum
- Rain: Never (water damage)
- Sun: Bright is good (cameras work better)
- Temperature: 32-104°F typical range

---

## Maintenance Schedule

### Daily (After Each Flight)

```
□ Inspect propellers for damage (cracks, warping)
□ Check frame for cracks or stress marks
□ Verify battery wasn't over-discharged
□ Wipe dust from sensors
□ Store in protective case
□ Record any issues
```

**Maintenance Checklist Card:**
```
Date: ________  Drone ID: _______

Flight Duration: ______  Battery Final: ______%

Propeller Damage:     ✓ None    □ Minor    □ Replace
Frame Damage:         ✓ None    □ Minor    □ Needs Repair
Sensors Working:      ✓ Yes     □ No
USB Connection:       ✓ Stable  □ Flaky

Issues Noted:
_________________________________

Maintenance Needed:
□ Propeller replacement
□ Gimbal calibration
□ Firmware update
□ Repair needed
```

### Weekly

```
□ Test each drone in flight simulator
□ Verify all sensors calibrated
□ Check firmware versions match
□ Test USB cables for wear
□ Inspect battery contacts for corrosion
□ Verify all props are balanced
```

### Monthly

```
□ Deep clean (compressed air)
□ Full battery test (charge/discharge cycle)
□ Gimbal calibration if needed
□ Flight test of each drone
□ Replacement part inventory audit
□ Document any usage patterns/failures
```

### End of Semester

```
□ Full inspection of all drones
□ Battery testing (aging assessment)
□ Firmware updates
□ Deep cleaning and maintenance
□ Secure storage
□ Archive usage logs
```

---

## Common Hardware Issues

### Issue: Propeller won't stay on

**Cause:** Loose motor shaft or damaged prop

**Solution:**
1. Replace propeller with spare
2. If still loose, motor may be damaged
3. Document motor number for replacement

### Issue: Drone tilts to one side

**Cause:** Motor speed mismatch or bent frame

**Solution:**
1. Land immediately
2. Check for visible frame damage
3. Calibrate motors (if available)
4. If persistent, may need motor replacement

### Issue: Battery not charging

**Cause:** Corroded contacts or faulty charger

**Solution:**
1. Clean battery contacts with pencil eraser
2. Try different USB cable
3. Try different charger
4. If still fails, replace battery

### Issue: Intermittent USB connection

**Cause:** Worn USB cable or port damage

**Solution:**
1. Try different USB cable
2. Try different computer port
3. Try USB hub (adds buffer)
4. If drone side: warranty replacement

### Issue: Sensor readings seem wrong

**Cause:** Calibration drift or environment

**Solution:**
1. Verify environment (sensor limitations)
2. Recalibrate sensor (pressure, compass)
3. Check for interference (RF, magnetic)
4. Update firmware if available

---

## Parts Inventory Management

### Replacement Parts Kit

**Stock these items:**
- Propellers: 24-pack (lasts ~4 weeks)
- Batteries: 2-3 extra (for rotation)
- USB cables: 2-3 spares
- Motor brushes: 4-pack (if applicable)
- Props guards: 2 sets
- Gimbal parts: 1 set
- Frame brackets: Spare hardware kit

### Reordering Strategy

**Monitor usage:**
- Propeller replacements/week
- Battery degradation rate
- USB cable failures
- Motor issues

**Order when:**
- Consumables (props, batteries) at 25% remaining
- Structural parts at first sign of wear
- Always have 2 weeks extra in stock

---

## Safety Equipment

**For classroom:**
- First aid kit (minor cuts from props)
- Fire extinguisher (battery-related)
- Safety goggles (for all students during flights)
- Padded mat under flight area (reduces bounce on crashes)
- Emergency manual for each drone type

---

## Budget Timeline

### Year 1 (Initial Investment)

```
Summer (Setup):
- Drones: $1,000
- Batteries & cables: $300
- Flight area setup: $150
- Initial spare parts: $200
━━━━━━━━━━━━━━━━━━━━━
Total Year 1: $1,650
```

### Year 2+ (Maintenance)

```
Annual Costs:
- Replacement propellers: $100
- Battery replacement: $150
- USB cables & misc: $50
- Occasional repairs: $100
- New parts as needed: $200
━━━━━━━━━━━━━━━━━━━━━
Typical Annual: $600
```

---

## Warranty and Support

### Manufacturer Support

- **DJI Tello:** 1-year warranty, good customer service
- **CoDrone Lite:** 1-year warranty, educational discount available
- **Protocol X:** Limited support, check before purchasing

### Documentation

Keep records:
- Purchase receipts
- Warranty cards
- Firmware versions
- Maintenance logs
- Crash incidents
- Repair attempts

---

## Sustainability

**Extend hardware life:**
- Proper storage (dry, cool place)
- Regular maintenance (cleaning, calibration)
- Gentle handling (training students in safety)
- Use protective cases
- Prop guards for beginners
- Rotate drones to prevent overuse

**Plan replacement:**
- Budget for 20-25% replacement annually
- Phase in new models as technology improves
- Keep old drones as spares/backup
- Consider trade-in programs

---

Next: [Lesson Plans]({{ '/guides/teacher/lesson-plans.html' | relative_url }})
