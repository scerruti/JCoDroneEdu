# CoDrone EDU Flight Time Constraints

## Critical Specification

**Maximum Flight Time**: ~8 minutes per battery charge

**Recommended Safe Flight**: ≤5 minutes (leaves safety margin for landing)

**Battery Warning**: Land immediately when low battery warning occurs

---

## Implications for Programming & Research

### ✅ What You CAN Do (Unlimited Time)

**Ground-Based Operations** - No battery drain while sitting still:
- Temperature monitoring (hours if needed)
- Sensor data collection
- Color sensor readings
- Range sensor testing
- Display programming
- Controller input monitoring
- LED programming
- Buzzer testing

**Example**: Warm-up study for 20 minutes
```java
drone.pair();  // Drone on ground
for (int i = 0; i < 20; i++) {
    double temp = drone.getUncalibratedTemperature();
    System.out.printf("%d min: %.2f°C\n", i, temp);
    Thread.sleep(60000);
}
// Battery still ~100% - no flight!
```

---

### ⚠️ What You CANNOT Do (Flight Time Limited)

**Flight Operations** - ~8 minute battery life:
- Extended hovering (>5 minutes risky)
- Long autonomous missions
- Multiple flight patterns in one session
- Continuous flight data collection

**Solution**: Multiple short flights with battery swaps!

**Example**: Flight temperature study
```java
// Session 1: Warm-up phase (ground - unlimited)
recordGroundTemperature(5);  // 5 minutes

// Session 2: Flight phase (MUST BE SHORT!)
drone.takeoff();
recordFlightTemperature(3);  // 3 minutes ONLY
drone.land();  // Battery ~70%

// Session 3: Recovery phase (ground - unlimited)
recordRecoveryTemperature(5);  // 5 minutes
```

---

## Best Practices for Research

### 🎯 Design Principle: Maximize Ground Time, Minimize Flight Time

**Good Research Design** ✅:
```
Total experiment: 20 minutes
├── Ground warm-up: 5 min (no battery drain)
├── Flight test: 3 min (battery to 75%)
└── Ground recovery: 5 min (no battery drain)
Battery remaining: 75% - SAFE!
```

**Poor Research Design** ❌:
```
Total experiment: 20 minutes
├── Ground warm-up: 2 min
├── Flight test: 10 min (battery to 20% - DANGEROUS!)
└── Ground recovery: 2 min
Battery remaining: 20% - UNSAFE! May not land properly!
```

---

## Programming Patterns

### Pattern 1: Short Flight Test
```java
// Always check battery first
if (drone.getBattery() < 50) {
    System.out.println("Battery too low for flight test!");
    return;
}

// Short, focused flight
drone.takeoff();
collectData(180000);  // 3 minutes max
drone.land();

// Battery check
System.out.println("Battery after flight: " + drone.getBattery() + "%");
```

### Pattern 2: Multiple Sessions
```java
// Session 1: Flight test
drone.takeoff();
collectFlightData(180000);  // 3 minutes
drone.land();

System.out.println("Recharge battery and press ENTER...");
System.in.read();

// Session 2: Different flight pattern
drone.takeoff();
collectDifferentFlightData(180000);  // Another 3 minutes
drone.land();
```

### Pattern 3: Ground-Heavy Research
```java
// MOST of the research on ground (no battery limit!)
collectGroundData(600000);  // 10 minutes - no problem!

// Brief flight comparison
drone.takeoff();
collectFlightData(180000);  // 3 minutes
drone.land();

// More ground analysis
analyzeGroundData(600000);  // Another 10 minutes - fine!
```

---

## Temperature Calibration Experiment Timeline

**Adjusted for 8-minute flight constraint**:

```
Phase 1: Ground Warm-Up (5 minutes)
├── Minute 0: Power on, start recording
├── Minutes 1-4: Temperature stabilization
└── Minute 5: Warm-up complete
   Battery: 100% (no flight yet)

Phase 2: Flight Test (5 minutes) ⚠️
├── Minute 0: Takeoff
├── Minutes 1-4: Hover at 1m, record temperature
└── Minute 5: LAND IMMEDIATELY
   Battery: ~65-70% (5 min flight)

Phase 3: Thermal Recovery (3 minutes)
├── Minutes 1-2: Temperature recovery on ground
└── Minute 3: Experiment complete
   Battery: ~65% (stable - no more flight)

Total Time: 13 minutes
Flight Time: 5 minutes (safe!)
Battery Remaining: ~65% (safe margin for emergency)
```

---

## Battery Management Tips

### Before Each Flight
- ✅ Charge to 100%
- ✅ Check battery: `drone.getBattery()`
- ✅ Plan flight duration (≤5 minutes)
- ✅ Have landing area ready

### During Flight
- ⚠️ Monitor battery if possible
- ⚠️ Listen for low battery beep
- ⚠️ Watch for LED warning
- ⚠️ Land if ANY battery warning!

### After Flight
- ✅ Check final battery percentage
- ✅ Allow battery to cool (10-15 minutes)
- ✅ Recharge before next session
- ✅ Never fly below 30% battery

---

## Research Experiment Modifications

### Original Plan (UNREALISTIC ❌)
- Warm-up: 10 minutes ground
- Flight: 5 minutes hover
- Recovery: 5 minutes ground
- **Problem**: Total 20 min, but battery only lasts 8 min flight!

### Revised Plan (REALISTIC ✅)
- Warm-up: 5 minutes ground (battery: 100%)
- Flight: 5 minutes hover (battery: 70%)
- Recovery: 3 minutes ground (battery: 70%)
- **Success**: Total 13 min, only 5 min flight, safe battery margin!

---

## Why This Matters

### Educational Projects
Students learn:
- **Resource constraints**: Real engineering limitations
- **Experimental design**: Work within constraints
- **Safety margins**: Never push limits
- **Battery management**: Critical for robotics
- **Creative solutions**: Maximize ground-based testing

### Research Value
- Most temperature factors can be studied on ground!
- Flight state is just ONE variable
- Ground-based = unlimited time for data collection
- Better data quality from longer observation periods

---

## Advanced: Battery Modeling

Estimated battery drain rates:
```
Idle on ground: ~0% per minute (negligible)
Hovering: ~6-8% per minute
Forward flight: ~8-10% per minute
Aggressive maneuvers: ~10-12% per minute

Therefore:
8 minutes hover = 100% → 36% (64% drain)
5 minutes hover = 100% → 65% (35% drain) ← SAFE!
3 minutes hover = 100% → 76% (24% drain) ← VERY SAFE!
```

**Recommendation**: Plan for 3-minute flights in research to allow:
- Multiple flights per battery
- Safety margin for emergencies
- Repeat measurements for reliability

---

## Summary

✅ **Ground testing**: Unlimited time, no battery concerns  
⚠️ **Flight testing**: 5 minutes max recommended (8 minutes absolute max)  
🔋 **Battery safety**: Always land with >30% remaining  
🔬 **Research design**: Favor ground measurements, brief flight comparisons  
📊 **Data quality**: Longer ground observation periods = better data!  

**Bottom Line**: The 8-minute flight limit is NOT a problem for most research - it's an opportunity to design smarter experiments! 🎯
