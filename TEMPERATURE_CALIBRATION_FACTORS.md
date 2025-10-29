# Quick Reference: Temperature Calibration Factors

## ⚠️ CRITICAL: 8-Minute Flight Time Limit!

**CoDrone EDU max flight**: ~8 minutes per battery charge  
**Recommended test flights**: ≤5 minutes (safety margin)  
**Good news**: Most experiments work ON THE GROUND (no flight needed!)

---

## TL;DR - What Impacts Temperature Readings?

| Factor | Impact | Magnitude | Easy to Measure? |
|--------|--------|-----------|------------------|
| **Flight State** 🚁 | Airflow cools sensor | +1.5°C offset | ✅ Yes - drone knows |
| **Runtime** ⏱️ | Sensor warms up | -2°C (first 5 min) | ✅ Yes - track time |
| **Battery Thermal State** 🔥 | Fresh vs cool battery | ±1-2°C | ⚠️ Need to track charging time |
| **Sunlight** ☀️ | Heats drone case | -4°C (direct sun) | ⚠️ Need light sensor |
| **Humidity** 💧 | Affects heat transfer | ±0.5°C | ⚠️ Need hygrometer |
| **Battery Level** 🔋 | Power dissipation | ±0.3°C | ✅ Yes - drone knows |
| **Ambient Temp** 🌡️ | Offset varies with temp | +0.5°C per 10°C | ✅ Yes - calculate it |
| **Pressure** 🌪️ | Altitude effects | Minimal | ✅ Yes - drone knows |

---

## Current vs Potential Accuracy

```
Simple Model (Current):
  ambient = sensor + 12.0
  Accuracy: ±2-3°C

Advanced Model (After Research):
  ambient = sensor + f(flight, runtime, sun, humidity, battery, temp)
  Accuracy: ±0.5-1°C
```

---

## Quick Experiments You Can Do Now

### 1. Ground vs Flight (10 minutes total, 3 minutes flight)
```java
// Does flight change temperature?
double ground = drone.getUncalibratedTemperature();
drone.takeoff();
Thread.sleep(180000);  // Hover 3 minutes (max safe time)
double flight = drone.getUncalibratedTemperature();
drone.land();  // LAND IMMEDIATELY!
System.out.printf("Ground: %.2f°C, Flight: %.2f°C, Diff: %.2f°C\n", 
    ground, flight, flight - ground);
```
**⚠️ Battery warning? Land immediately!**

### 2. Warm-Up Test (20 minutes - NO FLIGHT REQUIRED!)
```java
// How long to stabilize? (on ground - safe!)
drone.pair();
for (int i = 0; i < 20; i++) {
    double temp = drone.getUncalibratedTemperature();
    System.out.printf("%d min: %.2f°C\n", i, temp);
    Thread.sleep(60000);
}
// No battery drain - drone just sitting there!
```

### 3. Sun vs Shade (15 minutes - NO FLIGHT REQUIRED!)
```java
// Does sunlight matter? (on ground - safe!)
double shade = drone.getUncalibratedTemperature();
// Move drone to direct sunlight (WALK IT OVER - no flight!)
Thread.sleep(300000);  // Wait 5 minutes
double sun = drone.getUncalibratedTemperature();
System.out.printf("Shade: %.2f°C, Sun: %.2f°C, Diff: %.2f°C\n",
    shade, sun, sun - shade);
// Battery still at 100% - no flight!
```

**💡 Pro Tip**: Most calibration research can be done with drone ON THE GROUND!
Only flight state testing requires actual flight (and that's just 3-5 minutes).

---

## Student Project Ideas

### 🥉 Bronze: Single Factor
"Does hovering change temperature?"
- 1 hour of experimentation
- Simple before/after comparison
- Great for middle school

### 🥈 Silver: Multiple Factors  
"What affects drone temperature readings?"
- Test 3-4 factors systematically
- Build simple linear model
- Perfect for high school science fair

### 🥇 Gold: Full Research Project
"Machine learning for temperature calibration"
- Comprehensive data collection
- Advanced statistical analysis
- Publication-quality research
- AP Research / Undergraduate thesis

---

## See TEMPERATURE_CALIBRATION_RESEARCH.md for Full Details

- Complete experimental procedures
- Data collection templates
- Statistical analysis methods
- Implementation strategies
- Expected outcomes

**Bottom Line**: We can make this sensor 3-5x more accurate with systematic research! 🎯
