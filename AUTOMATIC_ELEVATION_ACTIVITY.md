# `getAutomaticElevation()` – Classroom Activity Design

## Concept
A convenience method that **automatically chooses the best elevation sensor** without requiring students to understand the hardware trade-offs upfront. Over time, this teaches sensor selection and calibration through experience.

## Behavior Specification

### Method Signature
```java
public double getAutomaticElevation() {
    return getAutomaticElevation(null);  // default unit: cm
}

public double getAutomaticElevation(String unit) {
    // Implementation logic below
}
```

### Logic Flow
1. **Check bottom range sensor first** (faster, no calibration needed)
   - Read bottom range via `telemetryService.getUncorrectedElevation("cm")`
   - If valid reading (< 999), return in requested unit
   
2. **Fall back to pressure-based elevation** if range sensor fails
   - If bottom range returns 999 (out of range / ceiling hit):
     - Check if pressure was calibrated via `elevationService.isPressureCalibrated()`
     - If **YES**: use `elevationService.getCorrectedElevation(unit)`
     - If **NO**: return 999 (signal: "not calibrated for pressure")

### Calibration Behavior
- **First time called with invalid bottom range:** 
  - Automatically calls `elevationService.setPressureAsInitialReference()`
  - Logs: `"Pressure calibrated at elevation: X meters"`
  - Returns the newly calibrated elevation
  
- **Subsequent calls:** Uses the stored initial pressure reference

### Return Values
| Scenario | Return |
|----------|--------|
| Bottom range valid | Range sensor value (cm/m/ft/km) |
| Bottom range 999, pressure calibrated | Pressure-based elevation (cm/m/ft/km) |
| Bottom range 999, pressure NOT calibrated | 999 (requires manual calibration) |

---

## Classroom Value

### Level 1 (AP CSA Intro)
**Student perspective:** "I can fly high and just call `getAutomaticElevation()` without worrying about sensors."
```java
drone.takeoff();
while (drone.getAutomaticElevation("cm") < 200) {
    drone.moveUp(20);  // ascend 20cm per step
}
```
✓ Simple, works for most flights
✓ No calibration burden for beginners

### Level 2 (Debugging Lesson)
When students hit 150cm ceiling and get 999:
- They discover the limitation: "Why did it stop?"
- Teacher explains: range sensor maxes out
- Students learn: pressure provides unlimited height
- They call `setInitialPressure()` and retry
- **Insight:** Different sensors for different scenarios

### Level 3 (Advanced - Sensor Selection)
Once comfortable, students consciously choose:
```java
// For low-altitude precise landing:
drone.getHeight();  // range sensor, accurate to 1-2cm

// For high-altitude exploration:
drone.setInitialPressure();
drone.getHeightFromPressure();  // barometer, limited accuracy but unlimited range

// For "I don't know how high I need to go":
drone.getAutomaticElevation();  // adaptive, learns the drone's limits
```

---

## Implementation Notes

### Advantages
1. **Zero friction for beginners** – students don't need sensor knowledge upfront
2. **Automatic discovery** – limitations become apparent organically
3. **Backward compatible** – exists alongside explicit methods
4. **Supports units** – consistent with Java API design

### Edge Cases & Considerations

#### Case 1: Rapid Pressure Recalibration
If student accidentally triggers pressure calibration at 150cm (thinking they're higher), what happens?
- **Current Design:** Calibrates and returns 150cm as "elevation"
- **Issue:** Pressure offset is now wrong; subsequent readings will be wrong
- **Solution Option A:** Add a `recalibratePressure()` method (explicit, safe)
- **Solution Option B:** Log a warning but allow override on next calibration within 5 seconds
- **Recommendation:** Option A (explicit control, no magic)

#### Case 2: Unit Conversion During Calibration
If first call uses feet (`getAutomaticElevation("ft")`), should calibration happen in feet or meters?
- **Recommendation:** Calibrate internally in pascals (pressure sensor native), convert on return
- **Benefit:** Unit choice doesn't affect calibration validity

#### Case 3: Multiple Drones / Different Altitudes
If two drones in the same classroom both auto-calibrate:
- Each drone stores its own initial pressure reference ✓
- No cross-interference
- Sea-level pressure might differ between drones (weather-dependent)
- **Teaching moment:** Discuss weather systems, pressure changes

#### Case 4: Pressure Calibration Fails
What if `elevationService` can't read initial pressure?
- Retry up to 3 times with backoff (already in `TelemetryService`)
- If still fails: return 999, log error, don't cache failed calibration
- User must call `setInitialPressure()` manually to retry

---

## Classroom Implementation

### Student Activity: Extend Drone → SmartElevationDrone

**Goal:** Students implement their own `getAutomaticElevation()` by extending the `Drone` class.

**What students learn:**
- Inheritance (extending a library class)
- Sensor fallback logic (conditional branching)
- Calibration patterns (state management)
- API design (when to auto-do vs. require explicit calls)

### Student Deliverable: `SmartElevationDrone.java`

```java
import edu.robolink.codrone.drone.Drone;

public class SmartElevationDrone extends Drone {
    private boolean pressureCalibratedAutomatically = false;

    /**
     * Smart elevation: uses range sensor when available,
     * falls back to pressure-based elevation when range sensor fails.
     * 
     * @return elevation in centimeters, or 999 if uncalibrated
     */
    public double getAutomaticElevation() {
        return getAutomaticElevation("cm");
    }

    public double getAutomaticElevation(String unit) {
        double rangeElevation = getHeight(unit);
        
        // If range sensor works, use it
        if (rangeElevation < 999) {
            return rangeElevation;
        }
        
        // Range sensor failed; try pressure
        // But only if we've calibrated pressure first
        if (!pressureCalibratedAutomatically) {
            // First time hitting ceiling: auto-calibrate pressure
            setInitialPressure();
            pressureCalibratedAutomatically = true;
            System.out.println("Auto-calibrated pressure-based elevation.");
        }
        
        // Now use pressure-based elevation
        return getCorrectedElevation(unit);
    }
}
```

### Teacher Preparation

**Before class:**
1. Students have flown with `getHeight()` in previous lessons
2. They've hit the 150cm ceiling (discovered 999 return)
3. They've learned about `setInitialPressure()` + `getHeightFromPressure()`

**In class:**
1. Introduce the problem: "Your drone stops at 150cm. Can we make it smarter?"
2. Guide students through inheritance syntax (extends Drone)
3. Walk through the logic: check range → if 999, use pressure
4. Students write `SmartElevationDrone` (~20 lines of code)
5. Students test with `L0108WhileLoops` (modified to use `SmartElevationDrone`)

### Modified Example: `L0108WhileLoops_SmartElevation.java`

```java
import edu.robolink.codrone.drone.Drone;
import java.util.Random;

public class L0108WhileLoops_SmartElevation {
    public static void main(String[] args) {
        SmartElevationDrone drone = new SmartElevationDrone();
        drone.pair();
        
        Random random = new Random();
        int cycles = 0;
        
        drone.takeoff();
        System.out.println("=== Tower of Terror: Automatic Elevation Version ===");
        
        while (cycles < 3) {
            int upSpeed = 50 + random.nextInt(30);  // 50-79% power
            int downDuration = 2 + random.nextInt(3);  // 2-4 seconds
            
            System.out.println("\nCycle " + (cycles + 1) + ":");
            System.out.println("  Flying up at " + upSpeed + "% power...");
            
            drone.setThrottle(upSpeed);
            drone.move(3);  // 3 seconds up
            
            double height = drone.getAutomaticElevation("cm");
            System.out.println("  Height: " + height + " cm");
            
            if (height > 250) {
                System.out.println("  Hit safety ceiling! Descending.");
                break;
            }
            
            System.out.println("  Descending for " + downDuration + " seconds...");
            drone.setThrottle(-50);
            drone.move(downDuration);
            
            cycles++;
        }
        
        drone.land();
        drone.close();
        System.out.println("\n=== Activity Complete ===");
    }
}
```

### Assessment Criteria

✅ Student successfully:
- [ ] Extends `Drone` class correctly
- [ ] Implements both `getAutomaticElevation()` overloads
- [ ] Calls `getHeight()` first and checks for 999
- [ ] Auto-calibrates pressure on first 999
- [ ] Returns pressure-based elevation on fallback
- [ ] Flies higher than 150cm without error
- [ ] Explains to partner: "Why does it switch sensors?"

---

## Questions for Teacher (You)

1. **Auto-calibration on first 999 – good or bad?**
   - Pro: Transparent, no student action needed
   - Con: Magic, students might not notice
   - **Your preference?**

2. **Recalibration safety:**
   - Should `getAutomaticElevation()` allow re-calibration if pressure reference drifts?
   - Or leave manual `setInitialPressure()` for deliberate recalibration?

3. **Classroom workflow:**
   - Should students be taught `getAutomaticElevation()` first (simple), then explicit methods?
   - Or explicit methods first (control), then convenience method as shorthand?

4. **Unit design:**
   - Should `getAutomaticElevation()` always return **centimeters** by default (matching `getHeight()`)?
   - Or **meters** (matching barometer standard)?
   - **Current suggestion:** centimeters for consistency with range sensor API

---

## Summary
`getAutomaticElevation()` is a **pedagogically intentional API** that:
- Lowers barrier to entry (no calibration knowledge needed)
- Teaches sensor trade-offs through discovery (ceiling hit → learn pressure)
- Supports progression from "comfortable flying" → "understanding hardware"
- Bridges `getHeight()` (range) and `getHeightFromPressure()` (pressure) into one thoughtful method

**This is the classroom equivalent of "train wheels before taking them off a bike."**

