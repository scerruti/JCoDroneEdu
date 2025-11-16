---
layout: guide
title: Lesson Plans
category: Teacher Guide
permalink: /guides/teacher/lesson-plans.html
---

## Week-by-Week Lesson Plans

Detailed lesson plans for the 14-week curriculum. Adapt to your schedule and student needs.

---

## Week 1: Getting Started

**Theme:** Welcome to drone programming!

### Day 1-2: Introduction & Safety (Classroom)

**Objectives:**
- Understand course goals and expectations
- Learn safety protocols
- Understand Java basics

**Lesson Flow:**
1. Welcome and course overview (5 min)
2. Demonstration flight (2 min) - Show what's possible
3. Safety briefing - mandatory (10 min)
4. Java refresher (15 min)
   - Variables, data types, output
   - Compile and run simple program
5. Homework: Download and install Java

**Materials:**
- Safety briefing slides
- Hello World starter code
- IDE installation guide

### Day 3-4: IDE Setup & First Program (Lab)

**Objectives:**
- Install and configure development environment
- Write and run first drone program
- Connect to actual drone

**Lab Procedure:**
1. IDE installation (20 min)
2. Create new project (5 min)
3. Add JCoDroneEdu library (10 min)
4. Type and compile HelloDrone (15 min)
5. Connection test (10 min) - Just battery reading

**Success Criteria:**
- Program compiles without errors
- IDE correctly recognizes library
- Drone connects and responds to battery query

**Troubleshooting:**
- See [IDE Setup Guide]({{ '/guides/teacher/ide-setup.html' | relative_url }})
- Common: PATH issues with Java
- Common: Library not added to project

### Day 5: First Flight! (Lab)

**Objectives:**
- Successfully take off and land
- Understand basic drone commands
- Build confidence with equipment

**Lab Procedure:**
1. Safety briefing refresh (5 min)
2. Pre-flight checklist (5 min)
3. Connect drone and verify (5 min)
4. Run HelloDrone program (1-2 min)
   - Drone takes off
   - Hover for 3 seconds
   - Land
5. Repeat with different groups (5 min each)

**Assessment:**
- Did drone take off? ✓/✗
- Did it land safely? ✓/✗
- Did you understand the code? ✓/✗

**Reflection:**
- "How does the code relate to what you saw?"
- "What commands do you want to try next?"

---

## Week 2: Variables and Data Types

**Theme:** Controlling drone behavior with data

### Day 1-2: Classroom - Variables (Lecture/Lab)

**Objectives:**
- Understand variables and types
- Read multiple sensors
- Format output

**Lesson:**
```java
// Variables example
int battery = drone.getBattery();  // Type: int, Value: 95
double altitude = drone.getCorrectedElevation();
boolean isFlying = battery > 20;

System.out.println("Battery: " + battery + "%");
System.out.println("Altitude: " + String.format("%.2f", altitude) + "m");
```

**Practice Exercises:**
1. Modify HelloDrone to print battery
2. Add height reading
3. Add temperature reading
4. Format output with String.format()

**Common Mistakes:**
- Integer division (5/2 = 2, not 2.5)
- Forgetting "new" for objects
- Type mismatches in concatenation

### Day 3-4: Lab - Flight Status (Hands-On)

**Objectives:**
- Apply variables to real problems
- Display drone information
- Prepare for next week's conditionals

**Lab Assignment:**
Create FlightStatus.java that displays:
```
Battery: 95%
Height: 0cm
Altitude: 0.00m
Temperature: 32.5C
Status: Ready to fly!
```

**Progression:**
1. Print one sensor (easy)
2. Print all sensors (medium)
3. Format numbers nicely (harder)

**Extension:**
- Use variables to calculate (battery * 0.5)
- Show sensor ranges

### Day 5: Assessment & Reflection

**Assignment:** FlightStatus Program

**Rubric:**
- Correct data types (20%)
- All sensors read (40%)
- Formatted output (30%)
- Code comments (10%)

**Reflection Questions:**
- What type would you use for each sensor?
- Why do we use different types?
- What sensors would be useful for controlling flight?

---

## Week 3: Control Flow (If/Else)

**Theme:** Making smart decisions with code

### Day 1-2: Classroom - Conditionals

**Objectives:**
- Understand boolean expressions
- Use if/else statements
- Apply to flight safety

**Lesson Topics:**
```java
// Comparison operators
int battery = 50;
if (battery > 80) { ... }
if (battery >= 50) { ... }
if (battery == 25) { ... }
if (battery != 0) { ... }

// Logical operators
if (battery > 50 && battery < 80) { ... }
if (battery < 25 || charging == true) { ... }
if (!isFlying) { ... }
```

**In-Class Practice:**
- Write SafeFlight program
- Discuss each condition
- Predict program flow

### Day 3-4: Lab - Safe Flight

**Objectives:**
- Implement flight safety checks
- Use nested conditionals
- Prevent equipment damage

**Lab Assignment:**
```java
// Students complete this
public class SafeFlight {
    public static void main(String[] args) {
        try (Drone drone = new Drone(true)) {
            int battery = drone.getBattery();
            
            if (battery >= 75) {
                // Good to fly
                drone.takeoff();
                Thread.sleep(2000);
                drone.land();
            } else if (battery >= 50) {
                // Marginal
                System.out.println("Low battery warning!");
            } else {
                // Too low
                System.out.println("Charge drone first!");
            }
        }
    }
}
```

**Variations:**
- Add height check
- Add temperature check
- Combine multiple conditions

### Day 5: Quiz & Lab Assessment

**Quiz:** (5 min, oral or written)
- What's the difference between == and >=?
- What's the outcome of: `if (5 > 3 && 2 < 1)`?

**Lab Assessment:** SafeFlight Program

**Rubric:**
- Logic correct (40%)
- Handles all cases (40%)
- Safety enforced (20%)

---

## Week 4: For Loops

**Theme:** Repeat commands to create patterns

### Day 1-2: Classroom - For Loop Basics

**Objectives:**
- Understand loop structure and flow
- Use loop variable
- Create repeating patterns

**Lesson:**
```java
// Simple loop
for (int i = 0; i < 4; i++) {
    System.out.println("Step: " + i);
}

// Loop with drone commands
for (int i = 0; i < 4; i++) {
    drone.moveForward(0.5);
    Thread.sleep(2000);
    drone.turnLeft(0.5);
}
```

**Trace Through:**
- Draw loop execution table
- Show i values: 0, 1, 2, 3
- Show which commands execute

### Day 3-4: Lab - Geometric Patterns

**Objectives:**
- Create square, triangle, hexagon patterns
- Modify angle for different shapes
- Verify pattern by observation

**Lab Assignment:**
Create and fly three patterns:
```java
// Square: 4 sides, 90° turns
// Triangle: 3 sides, 120° turns
// Hexagon: 6 sides, 60° turns
```

**Progression:**
1. Square (provided with hints)
2. Triangle (modify square code)
3. Hexagon (extend pattern)
4. Extension: Pentagon, octagon, custom

**Success Metric:**
- Drone flies recognizable shape
- Returns approximately to start
- Code is clean and commented

**Video/Photo:** Record pattern for portfolio

### Day 5: Pattern Competition

**Activity:** Flying competition
- Best square (straightest sides)
- Tightest turn
- Closest return to start
- Most creative pattern

**Assessment:** Patterns Assignment

---

## Week 5: While Loops

**Theme:** Fly based on sensor conditions

### Day 1-2: Classroom - Sensor-Based Loops

**Objectives:**
- Understand loop termination conditions
- Use sensors to control flight
- Prevent infinite loops

**Lesson:**
```java
// While loop pattern
while (condition is true) {
    // do something
}

// Drone example
while (drone.getBattery() > 30) {
    drone.moveForward(0.3);
    System.out.println("Battery: " + drone.getBattery());
}
System.out.println("Low battery, landing!");
```

**Key Discussion:**
- How do we know when to stop?
- What if condition never becomes false?
- How is this different from for loop?

### Day 3-4: Lab - Battery Monitor Flight

**Objectives:**
- Implement sensor-based control
- Monitor system state
- React to changing conditions

**Lab Assignment:**
```java
// Students complete:
try (Drone drone = new Drone(true)) {
    drone.takeoff();
    
    while (drone.getBattery() > 30) {
        // Keep moving while battery is good
        drone.moveForward(0.3);
        
        // Display status
        System.out.println("Battery: " + drone.getBattery() + "%");
        
        Thread.sleep(500);
    }
    
    // Exit loop, land drone
    System.out.println("Low battery!");
    drone.land();
}
```

**Variations:**
- Loop based on height (hover at fixed altitude)
- Loop based on range (keep moving until obstacle)
- Combined conditions (battery AND not at boundary)

### Day 5: Lab Assessment

**Rubric:**
- Loop condition correct (40%)
- Sensor read and acted upon (40%)
- Graceful exit (land safely) (20%)

---

## Weeks 6-7: Functions & Error Handling

*(Abbreviated - follow same pattern: Classroom lecture → Lab assignment → Assessment)*

**Week 6 Focus:** Functions
- Pattern libraries with reusable functions
- Parameters and return values
- Code organization

**Week 7 Focus:** Error Handling
- Try-catch blocks
- Safe pattern execution
- Input validation

---

## Weeks 8-9: Sensors

**Week 8:** Reading Multiple Sensors
- Environmental Logger assignment
- Data collection and CSV output
- Sensor accuracy discussion

**Week 9:** Sensor-Based Control
- Altitude maintenance
- Feedback loops
- PID concepts (simplified)

---

## Weeks 10-12: Autonomous Systems

**Week 10:** Path Planning
- Waypoint navigation
- Distance calculations
- Movement sequencing

**Week 11:** Obstacle Avoidance
- Range sensor usage
- Reactive behavior
- State-based navigation

**Week 12:** Multi-Sensor Integration
- Combining data sources
- Complex decision making
- System architecture

---

## Weeks 13-14: Capstone Project

### Week 13: Project Planning

**Students choose project type:**

**Option 1: Delivery System**
- Day 1-2: Design and plan navigation
- Day 3-4: Implement path following
- Day 5: Integration and testing

**Option 2: Environmental Monitor**
- Day 1-2: Design sampling locations
- Day 3-4: Implement data collection
- Day 5: Data analysis and visualization

**Option 3: Art Installation**
- Day 1-2: Choreograph flight patterns
- Day 3-4: Implement LED synchronization
- Day 5: Performance and recording

**Option 4: Physics Experiment**
- Day 1-2: Design experiment
- Day 3-4: Collect and analyze data
- Day 5: Calculate results and document

### Week 14: Final Presentations

**Each group presents:**
1. Project overview and goals (2 min)
2. Live demonstration or video (2-3 min)
3. Technical implementation (2 min)
4. Lessons learned (1 min)
5. Q&A (2 min)

**Reflection Paper:**
- What was challenging?
- What would you do differently?
- What did you learn about programming?
- What about drone systems?
- Next steps for your project?

---

## Assessment & Grading

### Assignment Grades

| Assignment | Week | Points |
|-----------|------|--------|
| HelloDrone | 1 | 10 |
| FlightStatus | 2 | 10 |
| SafeFlight | 3 | 15 |
| Patterns | 4 | 15 |
| BatteryFlight | 5 | 15 |
| PatternLibrary | 6 | 15 |
| SafePatterns | 7 | 15 |
| SensorLogger | 8 | 20 |
| AltitudeControl | 9 | 20 |
| WaypointFlight | 10 | 20 |
| AutoExplorer | 11-12 | 25 |
| Capstone Project | 13-14 | 50 |
| Participation | Throughout | 20 |
| **Total** | | **250** |

### Grading Scale

- A: 225-250 (90-100%)
- B: 200-224 (80-89%)
- C: 175-199 (70-79%)
- D: 150-174 (60-69%)
- F: < 150 (< 60%)

---

## Materials & Resources

**For each week:**
- Slide deck (provided in teacher materials)
- Lab assignment handout
- Solution code (for reference)
- Assessment rubric

**For students:**
- [Student Guide]({{ '/guides/student/index.html' | relative_url }})
- [API Reference]({{ '/guides/student/api-reference.html' | relative_url }})
- Javadoc documentation

---

Next: [Glossary]({{ '/guides/teacher/glossary.html' | relative_url }})
