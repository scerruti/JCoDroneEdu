---
layout: guide
title: Standards Alignment
category: Teacher Guide
permalink: /guides/teacher/standards.html
---

## Computer Science Standards Coverage

JCoDroneEdu aligns with major CS education standards. Use this guide to plan curriculum and document standards compliance.

---

## CSTA K-12 Computer Science Standards

### Level 3A (Grades 9-10)

**Algorithms & Programming**

| Standard | Unit | Evidence |
|----------|------|----------|
| AP-A.1.3: Create procedures with parameters | Unit 3 | PatternLibrary.java functions |
| AP-A.2.1: Represent algorithms using flowcharts/pseudocode | Week 6 | Flight pattern decomposition |
| AP-A.2.2: Implement algorithms using selection/iteration | Unit 2-3 | Loops and conditionals |
| AP-A.3.1: Describe how artificial intelligence drives decision-making | Week 11 | Obstacle avoidance algorithms |
| DA-1.1: Represent data using multiple formats | Week 8 | Sensor logging |
| DA-2.1: Recommend data collection for questions | Week 9 | Environmental logging |
| DA-2.2: Test and validate data | Week 8-9 | Sensor verification |
| AP-A.1.1: Understand variables and data types | Unit 1 | HelloDrone, FlightStatus |

**Computing Systems**

| Standard | Unit | Evidence |
|----------|------|----------|
| CS-2.1: Describe physical hardware layers | Week 1 | Drone hardware overview |
| CS-2.2: Explain how hardware abstraction layers work | Unit 1 | Java and JCoDroneEdu library |
| NI-3.1: Implement communication protocols | Week 1 | USB connection to drone |

---

### Level 3B (Grades 11-12)

**Algorithms & Programming**

| Standard | Unit | Evidence |
|----------|------|----------|
| AP-A.2.1: Trace complex algorithms | Unit 5-6 | Multi-sensor integration |
| AP-A.2.3: Debug programs using techniques | Unit 3 | SafePatterns error handling |
| AP-B.1.1: Compare data structures and efficiency | Unit 6 | Project data structures |
| AP-B.2.1: Analyze feedback loops and systems | Week 11-12 | Autonomous control systems |
| DA-3.1: Create computational models | Unit 5 | Path planning and navigation |

**Cybersecurity & Privacy**

| Standard | Unit | Evidence |
|----------|------|----------|
| NI-4.1: Explain privacy and secure data transmission | Teacher Guide | GitHub privacy, data protection |

---

## AP Computer Science Principles

### Big Idea 1: Creativity (CRD)

**Learning Objective CRD-1.A:**
> Create a new computational artifact for creative expression.

**Evidence:** Week 13-14 Capstone Project
- Students design and implement custom applications
- Examples: Art installations, environmental monitors
- Demonstrates creative use of drone technology

### Big Idea 2: Abstraction (AAP)

**Learning Objective AAP-1.A:**
> Represent information using multiple levels of abstraction.

**Evidence:** Unit 1-3
- Variables represent drone state
- Functions abstract complex behaviors
- Libraries abstract hardware details

**Learning Objective AAP-2.A:**
> Implement an algorithm that includes sequencing, selection, and iteration.

**Evidence:** Unit 2-5
- Sequencing: Flight commands in order
- Selection: Battery checks, obstacle detection
- Iteration: Pattern loops, sensor polling

### Big Idea 3: Data (DAT)

**Learning Objective DAT-1.A:**
> Represent information using multiple data structures.

**Evidence:** Week 8-9
- Integers: Battery level, height
- Doubles: Temperature, altitude
- Booleans: Flight status
- Arrays: Sensor arrays, waypoints

### Big Idea 4: Systems (SYS)

**Learning Objective SYS-1.A:**
> Describe the purpose of computing systems.

**Evidence:** Unit 5-6
- Autonomous flight systems
- Multi-sensor integration
- Real-time feedback control

### Big Idea 5: Impact (FRQ)

**Learning Objective FRQ-1.A:**
> Explain how computing impacts society.

**Evidence:** Throughout course
- Drones used in delivery, surveillance, agriculture
- Environmental monitoring applications
- Real-world relevance of programming

---

## Next Generation Science Standards (NGSS)

### Engineering Design

**ETS1.A: Defining and Delimiting Engineering Problems**

| Standard | Implementation |
|----------|-----------------|
| Analyze defining features of design problems | Week 13-14: Capstone project design |
| Develop criteria and constraints for solutions | Project planning document |

**ETS1.B: Developing Possible Solutions**

| Standard | Implementation |
|----------|-----------------|
| Plan and conduct investigations for data | Week 8-9: Sensor experiments |
| Analyze data to identify patterns | Environmental Logger assignment |
| Develop models to represent systems | Waypoint Flight, Altitude Control |

**ETS1.C: Optimizing Design Solutions**

| Standard | Implementation |
|----------|-----------------|
| Evaluate competing design solutions | Week 13: Project comparison |
| Use tradeoffs analysis (cost vs accuracy) | Sensor selection discussion |

### Physical Science

**PS2.A: Forces and Motion**

| Standard | Implementation |
|----------|-----------------|
| Understand forces and acceleration | Drone flight mechanics |
| Apply Newton's laws to drone motion | Unit 5: Path planning considers speed/acceleration |

**PS4.C: Information Technologies**

| Standard | Implementation |
|----------|-----------------|
| Understand data encoding and transmission | Week 1: USB protocol |
| Understand feedback systems | Week 11-12: Autonomous control |

---

## State Standards Examples

### Texas Essential Knowledge and Skills (TEKS)

**§130.421(c)(1): Computer Science**

**Strand 1: Computational Thinking**
- ✓ Algorithm analysis and design (Unit 2-3)
- ✓ Decomposition (Unit 3)
- ✓ Abstraction (Unit 3-4)

**Strand 2: Digital Citizenship**
- ✓ Cybersecurity and privacy (Teacher Guide)
- ✓ Ethical considerations (Week 13+)

**Strand 3: Computing Systems**
- ✓ Hardware and software interaction (Unit 1)
- ✓ Sensors and data (Unit 4)

---

### California Computer Science Standards Framework

**Grade 9-10: Programming & Data**

| Concept | Standard | JCoDroneEdu Unit |
|---------|----------|-----------------|
| Variables & Data Types | CS.DA.6.9-10.a | Unit 1, Week 2 |
| Control Structures | CS.DA.6.9-10.b | Unit 2-3 |
| Functions & Procedures | CS.DA.6.9-10.c | Unit 3 |
| Data Structures | CS.DA.6.9-10.d | Week 8, Unit 6 |
| Algorithms | CS.AL.1.9-10.a | Unit 2-5 |
| Program Design | CS.PD.7.9-10.a | Unit 3, Week 13 |

---

## Standards Coverage Summary

### Total Coverage

- **CSTA Standards:** 25+ aligned standards across levels 3A & 3B
- **AP CSP Big Ideas:** All 5 big ideas covered
- **NGSS:** Engineering design and physical science integration
- **State Standards:** Aligned with major state frameworks

### By Course Unit

| Unit | Standards Count | Key Focus |
|------|-----------------|-----------|
| Unit 1-2 | 8+ | Algorithms, data types, control flow |
| Unit 3 | 6+ | Functions, abstraction, decomposition |
| Unit 4 | 8+ | Data representation, systems |
| Unit 5-6 | 9+ | Complex algorithms, system design |
| **Total** | **31+** | Comprehensive CS foundations |

---

## Assessment & Documentation

### For Standards-Based Grading

**Rubric aligned to standards:**

```
Algorithms & Programming (40%)
- Use variables and data types: _____/10
- Use control structures: _____/10
- Write functions and procedures: _____/10
- Design algorithms: _____/10

Data & Systems (30%)
- Collect and analyze data: _____/10
- Understand computing systems: _____/10
- Solve problems systematically: _____/10

Computational Thinking (20%)
- Decompose problems: _____/10
- Abstract concepts: _____/10

Impact & Ethics (10%)
- Understand societal impact: _____/10
```

### Documentation for Accreditation

**For school reports and accreditation:**

1. **Standards Coverage Report**
   - Lists all standards taught
   - Evidence from assignments
   - Assessment data

2. **Learning Outcomes**
   - Student competencies by standard
   - Pre/post assessment results
   - Portfolio evidence

3. **Connection to Real-World**
   - How standards prepare for careers
   - Industry relevance of drone technology
   - STEM career pathways

---

## Extending to Advanced Standards

### For AP Computer Science A (Advanced)

Would require additional units on:
- Object-oriented programming (classes, inheritance)
- Algorithms and complexity analysis
- Data structures (lists, maps, trees)

Current JCoDroneEdu curriculum provides solid foundation.

### For AP Computer Science Principles

JCoDroneEdu covers approximately 60% of content:
- Create computational artifacts ✓
- Algorithm design ✓
- Data representation ✓
- Computing systems ✓
- Cybersecurity (partial)

---

## Standards Quick Reference

**For lesson planning, this matrix shows which units cover which standards:**

| Standard | Week | Unit | Assignment |
|----------|------|------|-----------|
| Variables | 2 | 1 | FlightStatus |
| Control Flow | 3 | 1 | SafeFlight |
| For Loops | 4 | 2 | Patterns |
| While Loops | 5 | 2 | BatteryFlight |
| Functions | 6 | 3 | PatternLibrary |
| Error Handling | 7 | 3 | SafePatterns |
| Data Logging | 8 | 4 | SensorLogger |
| Feedback Control | 9 | 4 | AltitudeControl |
| Path Planning | 10 | 5 | WaypointFlight |
| Autonomous Systems | 11-12 | 5 | AutoExplorer |
| System Design | 13-14 | 6 | Capstone Project |

---

Next: [Hardware Guide]({{ '/guides/teacher/hardware.html' | relative_url }})
