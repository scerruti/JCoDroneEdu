---
layout: guide
title: Classroom Setup
category: Teacher Guide
permalink: /guides/teacher/setup.html
---

## Setting Up Your Classroom

### Step 1: Assess Your Needs

**Question: How many students?**
- 1 drone per 2-3 students (pair programming)
- 3-4 drones for a class of 20

**Question: What's your budget?**
- Drone: $250-400 each
- USB cables and chargers: $50-100
- Replacement parts: $100-200
- Total: ~$3000-5000 per classroom

### Step 2: Software Installation

**All student computers need:**
1. Java JDK 11 or later
2. IntelliJ IDEA (free Community Edition)
3. JCoDroneEdu library

**Server/Setup Computer:**
- Can be instructor machine
- Runs drone charging and battery management
- Hosts reference documentation

### Step 3: Hardware Setup

**For Each Drone:**
- Drone unit
- 2 batteries (rotate during flight)
- USB charging cable
- Protective props
- Emergency replacement parts

**For Each Student Computer:**
- USB Type-A cable (for drone connection)
- IDE installed with library
- Sample programs compiled and tested

### Step 4: Safety Protocols

**Required:**
- Safety briefing before any flight
- "No one enters flight area" rule
- Emergency stop procedure (immediate land command)
- Battery safety (no flying below 25%, over-charging prevention)
- Propeller replacement after crashes

**Setup:**
- Clear 10ft × 10ft flight area
- Students outside flight zone during testing
- Backup location for outdoor testing
- First aid kit nearby

### Step 5: Test Environment

Before students arrive:

```java
// Test program - verify all drones work
try (Drone drone = new Drone(true)) {
    System.out.println("✓ Connected");
    System.out.println("Battery: " + drone.getBattery() + "%");
    System.out.println("✓ Ready to fly");
}
```

All drones should report connected and battery status.

---

## Scheduling Flights

### Daily Flight Schedule (Sample)

**Class: 1 hour, 24 students (8 drones in rotation)**

```
00:00 - 00:05   Welcome & safety check
00:05 - 00:20   Programming (students 1-8 code)
00:20 - 00:30   Testing (students 1-8 fly)
00:30 - 00:45   Programming (students 9-16 code)
00:45 - 00:55   Testing (students 9-16 fly)
00:55 - 01:00   Debrief & wrap-up
```

Students rotate: flying group comes back fresh to code, coding group now tests.

### Charging Strategy

With 8 drones and limited time:
- Use 4 drones per class, keep 4 charging
- 30-minute flight time per battery
- Have 2 batteries per drone (rotate)
- Overnight charging for next day

### Conflict Resolution

**What if a drone crashes during testing?**
- Stop immediately, inspect for damage
- Repair if minor (prop replacement)
- Have backup drone ready
- Continue with next group

---

## Space Considerations

### Minimum Flying Area

**Indoors:**
- 20ft × 20ft room minimum
- 10ft ceiling height
- Away from hanging fixtures

**Outdoors:**
- Preferably calm day (no wind)
- Clear of obstacles
- Away from people not in class
- Backup: parking lot or gym

### Classroom Layout

```
┌─────────────────────────────┐
│ Flying Area (10ft × 10ft)   │
│    (Students out!)          │
│                             │
├─────────────────────────────┤
│ Test Stations   │ Charging  │
│ (Computers)     │ (Batteries)│
│                             │
├─────────────────────────────┤
│ Reference Display / Instructor
└─────────────────────────────┘
```

---

## Materials Checklist

### Per Classroom
- [ ] 4-8 drones
- [ ] 8-16 batteries
- [ ] 8-16 USB charging cables
- [ ] Spare propellers (24-pack)
- [ ] Battery storage box
- [ ] Safety briefing poster
- [ ] Reference guide printed

### Per Student Computer
- [ ] Java JDK installed
- [ ] IntelliJ IDEA installed
- [ ] JCoDroneEdu library configured
- [ ] Sample programs downloaded
- [ ] First-time setup tested

### Instructor Tools
- [ ] Battery charger/monitor
- [ ] Drone diagnostic program
- [ ] Backup USB cables (2-3)
- [ ] Replacement parts kit
- [ ] Student roster

---

## Pre-Course Setup

**2 weeks before:**
- Test all drones (they age, might need updates)
- Verify all computers can compile and run
- Prepare sample programs

**1 week before:**
- Stock supplies (batteries, propellers)
- Verify charging system works
- Test network if using shared resources

**Day before:**
- Charge all batteries
- Compile sample program on all computers
- Review safety briefing

---

## First Day Logistics

1. **Students arrive** - Divide into groups (2-3 students)
2. **Safety briefing** - 5 minutes, mandatory
3. **Environment tour** - Show testing area, safe zones
4. **First program** - `takeoff()` and `land()` only
5. **Supervised flight** - Instructor monitors first flights
6. **Celebration** - First flight is a milestone!

---

## Ongoing Maintenance

**Daily:**
- Inspect propellers for damage
- Charge batteries overnight
- Log any issues in maintenance sheet

**Weekly:**
- Test one drone completely
- Check USB cables for wear
- Verify software still runs

**Monthly:**
- Full inventory audit
- Update library if new version available
- Analyze crash patterns (learning opportunities!)

---

Next: [GitHub Classroom Integration]({{ '/guides/teacher/github-classroom.html' | relative_url }})
