# Temperature Calibration Research Guide

## ⚠️ CRITICAL CONSTRAINT: 8-Minute Flight Time Limit

**CoDrone EDU flight time**: ~8 minutes maximum per battery charge

**Implications for research**:
- Flight tests must be SHORT (≤5 minutes recommended)
- Ground-based tests are unlimited (drone can sit powered on)
- Multiple battery charges needed for comprehensive flight data
- Plan experiments to maximize ground time, minimize flight time
- Most calibration factors can be studied on the ground!

**Good news**: Most factors (warm-up, sunlight, humidity, temperature) can be measured while drone sits on ground - no flight required! 🎉

---

## Executive Summary

The current default calibration offset (+12°C) is a **simplified approximation**. A sophisticated calibration model could account for multiple environmental and operational factors to provide significantly more accurate ambient temperature readings.

**Research Question**: Can we build a predictive model that accounts for flight state, runtime, humidity, sunlight, and other factors to improve temperature accuracy from ±3°C to ±0.5°C?

---

## Current Calibration Model

### Simple Offset Model (Current Implementation)
```java
ambient_temp = sensor_temp + 12.0  // Fixed offset
```

**Assumptions:**
- Die temperature offset is constant
- Environmental conditions don't matter
- Flight state doesn't affect sensor
- Thermal equilibrium is instant

**Observed Accuracy**: ±2-3°C (sufficient for educational demos, inadequate for weather stations)

---

## Factors That Impact Sensor Temperature

### 1. **Flight State** 🚁

#### Ground vs Flight
**Hypothesis**: Sensor temperature changes during flight due to:
- Air circulation around sensor
- Motor heat generation
- Electronic component power consumption
- Forced convection from propellers

**Expected Impact**: 
- Ground (idle): Sensor closer to ambient (minimal heat dissipation)
- Hovering: Moderate cooling from propeller downwash
- Aggressive flight: Higher electronics temperature, more cooling from airflow
- Motor heat may conduct to sensor housing

**Experimental Design**:
```java
// Collect temperature samples at different flight states
double temp_ground = drone.getUncalibratedTemperature();
drone.takeoff();
Thread.sleep(30000);  // Hover for 30 seconds
double temp_hover = drone.getUncalibratedTemperature();
drone.move(0, 0, 0, 50);  // Fast forward flight
Thread.sleep(30000);
double temp_flight = drone.getUncalibratedTemperature();
```

**Data to Record**:
- Reference ambient temperature (external thermometer)
- Sensor temperature (ground)
- Sensor temperature (hovering at 1m for 30s, 60s, 90s)
- Sensor temperature (forward flight)
- Sensor temperature (after landing, thermal recovery)
- Motor duty cycle / throttle level

**Expected Relationship**:
```
offset = base_offset + flight_factor * is_flying + airspeed_factor * velocity
```

---

### 2. **Runtime / Thermal Stabilization** ⏱️

#### Transient vs Steady-State Thermal Behavior
**Hypothesis**: The BMP280 sensor has thermal mass and takes time to reach equilibrium
- Power-on: Sensor self-heating as chip becomes active
- First 5 minutes: Transient thermal state
- After 10+ minutes: Steady-state temperature

**Expected Impact**:
- 0-2 minutes: Offset may be +8°C (sensor warming up)
- 2-5 minutes: Offset transitions to +10°C
- 5+ minutes: Stable offset of +12°C

**Experimental Design**:
```java
// Record temperature over time after power-on
drone.pair();
double reference = getExternalThermometer();

for (int min = 0; min < 20; min++) {
    Thread.sleep(60000);  // Wait 1 minute
    double sensor = drone.getUncalibratedTemperature();
    double offset = reference - sensor;
    System.out.printf("Time: %d min, Offset: %.2f°C\n", min, offset);
}
```

**Data to Record**:
- Time since power-on (seconds)
- Sensor temperature
- Reference temperature (constant environment)
- Battery voltage (affects power dissipation)

**Expected Relationship**:
```
offset = base_offset + thermal_transient * exp(-time / time_constant)
```
Where `time_constant` ≈ 120-300 seconds (to be determined experimentally)

---

### 3. **Humidity** 💧

#### Water Vapor Thermal Effects
**Hypothesis**: Humidity affects heat transfer characteristics
- Humid air has different thermal conductivity than dry air
- Water vapor can affect evaporative cooling
- Sensor housing may have condensation effects

**Expected Impact**: 
- Low humidity (20%): Offset might be +13°C (less thermal coupling)
- Medium humidity (50%): Offset +12°C (baseline)
- High humidity (80%): Offset might be +11°C (better thermal coupling)

**Challenge**: Drone doesn't measure humidity (would need external sensor)

**Experimental Design**:
```java
// Test in controlled environments with known humidity
// Option 1: Test in different seasons/weather
// Option 2: Use humidifier in enclosed room
// Option 3: Test in bathroom after shower (high humidity)

// Record conditions
String location = "Controlled room";
double reference_temp = 22.0;  // External thermometer
double reference_humidity = 65.0;  // External hygrometer
double sensor_temp = drone.getUncalibratedTemperature();
double offset = reference_temp - sensor_temp;
```

**Data to Record**:
- Ambient humidity (external hygrometer)
- Reference temperature
- Sensor temperature
- Time of day / weather conditions

**Expected Relationship**:
```
offset = base_offset + humidity_factor * (humidity - 50) / 50
```

---

### 4. **Direct Sunlight** ☀️

#### Radiative Heating
**Hypothesis**: Direct sunlight heats the drone housing (black plastic)
- Solar radiation absorbed by case
- Case conducts heat to sensor
- Sensor reads HIGHER than ambient in sun
- Effect reversed in shade

**Expected Impact**:
- Shade: Offset +12°C (baseline)
- Partial sun: Offset +10°C (case warming, sensor reads higher)
- Direct sun: Offset +6 to +8°C (significant case heating)
- After moving to shade: 5-10 minute thermal recovery

**Experimental Design**:
```java
// Test in controlled sunlight exposure
// 1. Measure in shade (baseline)
double temp_shade = drone.getUncalibratedTemperature();

// 2. Place drone in direct sunlight for 5 minutes
System.out.println("Moving to direct sunlight...");
Thread.sleep(300000);  // 5 minutes
double temp_sun = drone.getUncalibratedTemperature();

// 3. Return to shade, monitor recovery
System.out.println("Moving back to shade...");
for (int i = 0; i < 10; i++) {
    Thread.sleep(60000);  // 1 minute intervals
    double temp = drone.getUncalibratedTemperature();
    System.out.printf("Recovery %d min: %.2f°C\n", i+1, temp);
}
```

**Data to Record**:
- Solar irradiance (use pyranometer or estimate: full sun ≈ 1000 W/m²)
- Time in sun/shade
- Reference ambient temperature (shaded thermometer)
- Sensor temperature
- Drone case color (black absorbs more)
- Wind speed (affects convective cooling)

**Expected Relationship**:
```
offset = base_offset - solar_factor * solar_irradiance / 1000
```
Where `solar_irradiance` in W/m² (0 in shade, ~1000 in direct sun)

---

### 5. **Battery Level** 🔋

#### Electronic Power Dissipation
**Hypothesis**: Battery voltage affects power consumption and heat generation
- Full battery (4.2V): More power, more heat
- Low battery (3.5V): Less power, less heat
- Voltage regulators dissipate power as heat
- Could affect sensor die temperature

**Expected Impact**: Likely minor (±0.5°C)

**Experimental Design**:
```java
// Long-term test: Monitor as battery drains (ground-based only!)
drone.pair();  // On ground - no flight
while (drone.getBattery() > 20) {
    double voltage = estimateBatteryVoltage();  // 4.2V to 3.5V
    double sensor = drone.getUncalibratedTemperature();
    double reference = getExternalThermometer();
    double offset = reference - sensor;
    
    System.out.printf("Battery: %d%%, Offset: %.2f°C\n", 
        drone.getBattery(), offset);
    
    Thread.sleep(300000);  // Check every 5 minutes (ground - safe!)
}
```

**Data to Record**:
- Battery percentage
- Battery voltage (if available)
- Reference temperature
- Sensor temperature
- Time

**Expected Relationship**:
```
offset = base_offset + battery_factor * (voltage - 3.85) / (4.2 - 3.5)
```

---

### 6. **Battery Thermal State** 🔥 (NEW! Important for Li-ion!)

#### Fresh-off-Charger vs Room Temperature Battery
**Hypothesis**: Battery temperature from charging affects nearby sensor
- Fresh battery (just charged): Warmer (30-35°C)
- Room temp battery (sat for 30 min): Cooler (~20-25°C)
- Battery is physically close to sensor in small drone
- Small Li-ion batteries: Minimal self-heating but NOT zero
- Heat from charging can persist 15-30 minutes

**Expected Impact**: Could be significant! (±1-2°C)
- Warm battery → Sensor reads higher → Less offset needed
- Cool battery → Sensor reads lower → More offset needed

**Experimental Design**:
```java
// Test 1: Fresh battery (just off charger)
System.out.println("Install battery fresh from charger");
System.out.println("Press ENTER when battery installed...");
System.in.read();

drone.pair();
Thread.sleep(120000);  // 2 minutes for system stabilization

double freshBatteryTemp = drone.getUncalibratedTemperature();
double reference = getExternalThermometer();
double freshOffset = reference - freshBatteryTemp;

System.out.printf("Fresh battery offset: %.2f°C\n", freshOffset);

// Let system cool down
drone.close();
System.out.println("Waiting 30 minutes for battery to cool...");
System.out.println("Leave battery in drone, let it sit at room temp");
Thread.sleep(1800000);  // 30 minutes

// Test 2: Room temperature battery
System.out.println("Testing with room temperature battery...");
drone.pair();
Thread.sleep(120000);  // 2 minutes for system stabilization

double coolBatteryTemp = drone.getUncalibratedTemperature();
double coolOffset = reference - coolBatteryTemp;

System.out.printf("Cool battery offset: %.2f°C\n", coolOffset);
System.out.printf("Difference: %.2f°C\n", coolOffset - freshOffset);
```

**Data to Record**:
- Time since battery removed from charger
- Ambient temperature (constant reference)
- Sensor temperature
- Calculated offset
- Battery percentage (should be 100% for both tests)

**Expected Relationship**:
```
offset = base_offset - battery_temp_factor * (battery_temp - 25)
```

Where `battery_temp` would need to be measured externally or estimated from:
- Time since charging: 0 min = warm, 30+ min = room temp
- Battery self-heating during operation (flight adds heat)

**Why This Matters**:
- Student charges battery, immediately runs experiment → Warmer baseline
- Student lets battery sit 30 min → Cooler baseline
- Inconsistent results unless controlled!
- **Best Practice**: Always wait 15-30 minutes after charging before experiments

**Teaching Opportunity**:
- Thermal mass and heat dissipation
- Battery chemistry basics (Li-ion characteristics)
- Importance of standardized procedures
- Real-world experimental challenges

---

### 7. **Ambient Temperature** 🌡️

#### Temperature-Dependent Offset
**Hypothesis**: The offset itself may be temperature-dependent
- At 0°C: Offset might be +10°C
- At 20°C: Offset +12°C (baseline)
- At 40°C: Offset might be +14°C

**Reasoning**: Thermal conductivity, heat dissipation, and sensor characteristics all vary with temperature

**Experimental Design**:
```java
// Test in different ambient conditions
// Cold: Refrigerator (careful with condensation!)
// Normal: Room temperature (20-25°C)
// Warm: Summer day or heated room (30-35°C)

String condition = "Cold environment (refrigerator)";
double reference = 5.0;  // Refrigerator thermometer
Thread.sleep(600000);  // Wait 10 minutes for thermal equilibrium
double sensor = drone.getUncalibratedTemperature();
double offset = reference - sensor;
```

**Data to Record**:
- Reference ambient temperature (wide range: 0-40°C)
- Sensor temperature
- Calculated offset
- Time to thermal equilibrium

**Expected Relationship**:
```
offset = base_offset + temp_coefficient * (reference_temp - 20)
```

---

### 7. **Barometric Pressure** 🌪️

#### Altitude and Weather Effects
**Hypothesis**: Air pressure affects thermal characteristics
- Lower pressure → Less convective cooling
- Weather systems affect local pressure
- High altitude has different thermal properties

**Expected Impact**: Likely minimal for typical educational use (sea level to 1000m)

**Experimental Design**:
```java
// Test at different elevations
// Sea level: Baseline
// 500m: Mid elevation
// 1000m: High elevation

double pressure = drone.getPressure();  // Pa
double sensor = drone.getUncalibratedTemperature();
double reference = getExternalThermometer();
double offset = reference - sensor;

System.out.printf("Pressure: %.0f Pa, Offset: %.2f°C\n", pressure, offset);
```

**Data to Record**:
- Barometric pressure (Pa)
- Altitude (meters)
- Reference temperature
- Sensor temperature

---

## Proposed Advanced Calibration Model

### Multi-Factor Linear Model
```java
/**
 * Advanced temperature calibration model accounting for multiple factors.
 * 
 * @param sensorTemp Raw sensor die temperature (°C)
 * @param isFlying True if drone is airborne
 * @param runtimeSeconds Time since power-on
 * @param solarExposure 0.0 (shade) to 1.0 (direct sun)
 * @param humidity Relative humidity (0-100%), use 50 if unknown
 * @param batteryVoltage Current battery voltage (3.5-4.2V)
 * @return Estimated ambient temperature (°C)
 */
public double getAdvancedCalibratedTemperature(
    double sensorTemp,
    boolean isFlying,
    double runtimeSeconds,
    double solarExposure,
    double humidity,
    double batteryVoltage) {
    
    // Base offset (empirically determined)
    double offset = 12.0;
    
    // Flight state correction
    // Hypothesis: Airflow cools sensor by ~1-2°C
    if (isFlying) {
        offset += 1.5;  // Sensor reads cooler during flight
    }
    
    // Thermal transient correction (exponential decay)
    // Sensor warms up over first 5 minutes
    double timeConstant = 180.0;  // 3 minutes to 63% of final value
    double transientFactor = 2.0 * Math.exp(-runtimeSeconds / timeConstant);
    offset -= transientFactor;  // Less offset when first powered on
    
    // Solar heating correction
    // Direct sun heats case, sensor reads higher, need less offset
    offset -= solarExposure * 4.0;  // Up to -4°C in direct sun
    
    // Humidity correction (minor effect)
    double humidityFactor = (humidity - 50.0) / 50.0;  // Normalized -1 to +1
    offset += humidityFactor * 0.5;  // ±0.5°C for extreme humidity
    
    // Battery voltage correction (minor effect)
    double voltageFactor = (batteryVoltage - 3.85) / 0.7;  // Normalized -1 to +1
    offset -= voltageFactor * 0.3;  // ±0.3°C for battery extremes
    
    // Temperature-dependent offset
    double tempCoefficient = 0.05;  // 0.05°C offset per °C ambient
    offset += tempCoefficient * (sensorTemp - 20.0);
    
    return sensorTemp + offset;
}
```

### Model Parameters to Determine Experimentally

```java
// Constants to tune through experimentation
private static final double BASE_OFFSET_C = 12.0;           // To be measured
private static final double FLIGHT_OFFSET_C = 1.5;          // To be measured
private static final double THERMAL_TIME_CONSTANT_S = 180.0; // To be measured
private static final double THERMAL_TRANSIENT_MAX_C = 2.0;  // To be measured
private static final double SOLAR_FACTOR_C = 4.0;           // To be measured
private static final double HUMIDITY_FACTOR_C = 0.5;        // To be measured
private static final double VOLTAGE_FACTOR_C = 0.3;         // To be measured
private static final double TEMP_COEFFICIENT_C_PER_C = 0.05; // To be measured
```

---

## Experimental Research Plan

### Phase 1: Baseline Characterization (2-3 hours)
**Goal**: Determine base offset and thermal time constant

**Setup**:
- Controlled indoor environment (20-25°C, minimal air movement)
- Reference thermometer (±0.1°C accuracy)
- Drone powered on ground (not flying)

**Procedure**:
1. Power on drone, record temperature every minute for 20 minutes
2. Calculate offset vs time
3. Fit exponential curve to determine time constant
4. Determine steady-state offset

**Expected Data**:
```
Time(min)  Sensor(°C)  Reference(°C)  Offset(°C)
0          10.5        22.0           11.5
1          11.2        22.0           10.8
2          11.8        22.0           10.2
5          13.0        22.0           9.0
10         14.5        22.0           7.5
15         15.0        22.0           7.0
20         15.2        22.0           6.8
```

---

### Phase 2: Flight State Testing (8 minutes MAX! ⚠️)
**Goal**: Determine flight vs ground offset difference

**CRITICAL CONSTRAINT**: Drones have ~8 minutes maximum flight time!

**Setup**:
- Same controlled environment
- Fully charged battery (100%)
- Safety area for flight
- Plan for SHORT flight tests (≤5 minutes each)

**Procedure**:
1. Measure temperature on ground for 2 minutes (equilibrium)
2. Takeoff, hover at 1m for 3 minutes (not longer!)
3. Measure temperature
4. Land immediately, measure recovery
5. **Recharge battery** before next flight test
6. Optional: Repeat with different flight patterns (1-2 minute flights only)

**Expected Data**:
```
State          Time   Sensor(°C)  Reference(°C)  Offset(°C)  Battery
Ground         0min   15.0        22.0           7.0         100%
Hover 1min     1min   14.2        22.0           7.8         85%
Hover 3min     3min   13.8        22.0           8.2         70%
Land           4min   14.5        22.0           7.5         65%
```

**⚠️ SAFETY NOTES**:
- Never fly with battery < 30%
- Land immediately if battery warning occurs
- 5-minute flight maximum recommended
- Allow battery to cool between flights
- Multiple short flights better than one long flight

---

### Phase 3: Environmental Conditions (Multiple sessions)
**Goal**: Measure impact of sunlight, humidity, temperature range

#### 3A: Solar Exposure Test
**Procedure**:
1. Start in shade, measure baseline
2. Move to direct sunlight
3. Record temperature every minute for 10 minutes
4. Return to shade, monitor recovery

**Expected**: Sensor reads 2-5°C higher in sun (needs less offset)

#### 3B: Humidity Test
**Procedure**:
1. Test on dry day (humidity < 30%)
2. Test on humid day (humidity > 70%)
3. Compare offsets

**Expected**: ±0.5-1°C variation with humidity

#### 3C: Temperature Range Test
**Procedure**:
1. Test in cold environment (5-10°C): refrigerator or winter morning
2. Test in normal environment (20-25°C): room temperature
3. Test in warm environment (30-35°C): summer day or heated room

**Expected**: Offset increases with ambient temperature (maybe +0.5°C per 10°C)

---

### Phase 4: Long-Duration Testing (Multiple Sessions! ⚠️)
**Goal**: Battery and long-term stability

**CRITICAL**: Cannot do this in one session due to 8-minute flight limit!

**Procedure** (Ground-based testing only):
1. Fully charge battery
2. Power on drone (on ground - NO FLIGHT)
3. Record every 5 minutes until battery low
4. Track battery voltage, temperature, offset

**Alternative Procedure** (Multiple flight sessions):
1. Fully charge battery
2. Fly for 4 minutes, land, record
3. Wait 10 minutes (cool down)
4. Fly again for 4 minutes, land, record
5. Repeat 3-4 times to see battery depletion effects

**Expected**: Minor variation with battery (±0.3°C)

**⚠️ NOTE**: This is why battery effect is expected to be minor - we can't observe
full battery depletion during a single flight!

---

## Data Collection Template

### CSV Format for Experiments
```csv
timestamp,test_id,runtime_sec,is_flying,battery_pct,battery_voltage,reference_temp_c,sensor_temp_c,offset_c,humidity_pct,solar_exposure,pressure_pa,notes
2025-10-15 10:00:00,baseline_001,0,0,100,4.2,22.0,10.5,11.5,45,0.0,101325,indoor_controlled
2025-10-15 10:01:00,baseline_001,60,0,100,4.2,22.0,11.2,10.8,45,0.0,101325,warming_up
...
```

### Required Equipment
- **Reference thermometer**: ±0.1°C accuracy (digital indoor/outdoor thermometer)
- **Hygrometer**: For humidity measurements
- **Light meter** (optional): Measure solar irradiance
- **Stopwatch**: Track runtime
- **Battery voltage meter** (optional): Track voltage if drone doesn't report it

---

## Machine Learning Approach (Advanced)

### Could We Use ML to Build the Model?

**Yes!** Once sufficient data is collected:

```python
# Pseudocode for ML calibration model
import pandas as pd
from sklearn.ensemble import RandomForestRegressor

# Load experimental data
data = pd.read_csv('temperature_calibration_experiments.csv')

# Features
X = data[['sensor_temp_c', 'runtime_sec', 'is_flying', 
          'battery_voltage', 'humidity_pct', 'solar_exposure']]

# Target (actual offset)
y = data['offset_c']

# Train model
model = RandomForestRegressor(n_estimators=100)
model.fit(X, y)

# Feature importance
print(model.feature_importances_)
# Shows which factors matter most!

# Use in Java
# Convert trained model to formula or lookup table
```

---

## Implementation Strategy

### Simple Implementation (Short Term)
```java
// Add optional parameters to existing method
public double getCalibratedTemperature(
    boolean isFlying,
    double runtimeSeconds) {
    
    double base = getUncalibratedTemperature();
    double offset = DEFAULT_TEMPERATURE_OFFSET_C;
    
    // Apply simple corrections
    if (isFlying) {
        offset += 1.5;
    }
    
    if (runtimeSeconds < 300) {  // First 5 minutes
        offset -= 2.0 * Math.exp(-runtimeSeconds / 180.0);
    }
    
    return base + offset;
}
```

### Advanced Implementation (After Research)
```java
// Full multi-factor model
public double getCalibratedTemperature(
    CalibrationContext context) {
    
    // Context contains: isFlying, runtime, solarExposure, 
    // humidity, batteryVoltage, etc.
    
    return applyAdvancedCalibrationModel(
        getUncalibratedTemperature(), 
        context);
}
```

---

## Student Research Projects

### Beginner Level: Single Factor Studies
**Project**: "How does flight affect temperature readings?"
- Compare ground vs hover vs flight
- Simple before/after measurements
- Basic statistical analysis

### Intermediate Level: Multi-Factor Experiment
**Project**: "Building a better temperature calibration"
- Test 3-4 factors (flight, runtime, sunlight)
- Collect structured data
- Build linear regression model

### Advanced Level: Comprehensive Study
**Project**: "Machine learning for sensor calibration"
- Full factorial experiment design
- Large dataset collection (100+ measurements)
- Train ML model, validate accuracy
- Compare simple vs advanced calibration

---

## Expected Outcomes

### Accuracy Improvements
- **Current (simple offset)**: ±2-3°C accuracy
- **With flight correction**: ±1.5-2°C accuracy
- **With thermal transient**: ±1-1.5°C accuracy
- **Full multi-factor model**: ±0.5-1°C accuracy (goal)

### Educational Value
Students learn about:
- Sensor physics and thermal effects
- Experimental design and data collection
- Statistical analysis and curve fitting
- Feature importance and model building
- Real-world calibration challenges
- Trade-offs between model complexity and accuracy

### Publications/Presentations
- Science fair project: "Advanced Drone Temperature Calibration"
- IEEE paper: "Multi-Factor Calibration Model for BMP280 Sensor"
- Educational case study: "Teaching Sensor Physics Through Drone Experiments"

---

## Open Questions for Future Investigation

1. **Does altitude affect offset?** (Test at different elevations)
2. **Does wind speed matter?** (Test on windy vs calm days)
3. **Indoor vs outdoor?** (HVAC airflow effects)
4. **Case color?** (Black vs white drone case)
5. **Sensor location?** (Top vs bottom of drone housing)
6. **Age/wear effects?** (Does offset drift over months/years?)
7. **Individual calibration?** (Does each drone need unique coefficients?)

---

## Conclusion

**Short Answer**: Yes, we can build a much better calibration model!

**The Path Forward**:
1. ✅ Current implementation: Simple +12°C offset (good enough for demos)
2. 🔬 Research phase: Systematic experiments to measure each factor
3. 📊 Model building: Linear regression or ML to combine factors
4. 🎓 Educational integration: Turn research into student projects
5. 🚀 Advanced implementation: Multi-factor calibration in production code

**Estimated Research Timeline**:
- Baseline experiments: 1 week
- Multi-factor testing: 2-3 weeks
- Model development: 1 week
- Validation: 1 week
- **Total**: 1-2 months of intermittent testing

**Potential Accuracy**: From ±2-3°C → ±0.5-1°C

This would transform the CoDrone EDU from "educational toy" to "legitimate weather station platform"! 🌡️🚁

---

## References for Further Reading

1. **BMP280 Datasheet**: Bosch Sensortec - thermal characteristics
2. **Heat Transfer**: Fundamentals of convection, radiation, conduction
3. **Sensor Calibration**: NIST guidelines for temperature sensors
4. **Machine Learning**: Scikit-learn regression documentation
5. **Experimental Design**: Factorial experiments, DOE principles

---

## Contact for Collaboration

If students want to pursue this as a research project, this could be:
- Science fair competition entry
- AP Research/Capstone project
- Undergraduate research thesis
- Publication in educational technology journal

**This is serious science, accessible to high school students!** 🔬
