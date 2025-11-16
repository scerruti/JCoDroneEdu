---
layout: guide
title: Student Guide
category: Student Guide
permalink: /guides/student/
---

Welcome to the CoDrone EDU Student Guide! This comprehensive resource will teach you how to program a real drone using Java, combining computer science concepts with hands-on robotics.

## What You'll Learn

This guide covers everything from connecting to your first drone through building complex autonomous flight behaviors:

- **Getting Started** - Install software, connect your drone, run your first program
- **Flight Concepts** - Understand movement commands, turning, and flight basics
- **Sensors** - Read battery, distance, altitude, and motion sensors
- **Flight Patterns** - Build shapes and patterns using loops and functions
- **API Reference** - Quick lookup for all major commands
- **Debugging** - Solve common problems and troubleshoot issues
- **Next Steps** - Advanced topics and project ideas

## For Different Skill Levels

**Beginner (No programming experience)**
- Start with [Getting Started]({{ '/guides/student/getting-started.html' | relative_url }})
- Then [Your First Flight]({{ '/guides/student/first-flight.html' | relative_url }})
- Follow through guides sequentially

**Intermediate (Some Java experience)**
- Review [Flight Concepts]({{ '/guides/student/flight-concepts.html' | relative_url }})
- Jump to [Flight Patterns]({{ '/guides/student/flight-patterns.html' | relative_url }})
- Use [API Reference]({{ '/guides/student/api-reference.html' | relative_url }}) as needed

**Advanced (Strong Java background)**
- Focus on [Sensors]({{ '/guides/student/sensors.html' | relative_url }})
- Explore [Next Steps]({{ '/guides/student/next-steps.html' | relative_url }})
- Refer to [Javadoc API]({{ site.url }}/javadoc/index.html) for advanced features

## Getting Help

- **Stuck on a concept?** Check the [Debugging]({{ '/guides/student/debugging.html' | relative_url }}) section
- **Looking for a specific method?** See [API Reference]({{ '/guides/student/api-reference.html' | relative_url }})
- **Want to see examples?** Visit the [GitHub repository](https://github.com/scerruti/JCoDroneEdu)
- **Need complete API details?** Browse the [Javadoc]({{ site.url }}/javadoc/index.html)

## Quick Start

Here's the absolute minimum to get your drone flying:

```java
import com.otabi.jcodroneedu.Drone;

public class FirstFlight {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        drone.pair();      // Connect
        drone.takeoff();   // Fly up
        drone.hover(5);    // Stay for 5 seconds
        drone.land();      // Come down
        drone.close();     // Disconnect
    }
}
```

**Next:** [Learn how to set this up →]({{ '/guides/student/getting-started.html' | relative_url }})

---

**Table of Contents**

1. [Getting Started]({{ '/guides/student/getting-started.html' | relative_url }}) - Installation and first connection
2. [Your First Flight]({{ '/guides/student/first-flight.html' | relative_url }}) - Make your drone fly
3. [Flight Concepts]({{ '/guides/student/flight-concepts.html' | relative_url }}) - Movement, variables, conditionals
4. [Working with Sensors]({{ '/guides/student/sensors.html' | relative_url }}) - Read battery, distance, altitude
5. [Flight Patterns]({{ '/guides/student/flight-patterns.html' | relative_url }}) - Loops, shapes, functions
6. [API Reference]({{ '/guides/student/api-reference.html' | relative_url }}) - Quick lookup by category
7. [Debugging]({{ '/guides/student/debugging.html' | relative_url }}) - Solve common problems
8. [Next Steps]({{ '/guides/student/next-steps.html' | relative_url }}) - Advanced projects and learning

---

**Learning Path:** Beginner → Intermediate → Advanced

Each section builds on previous concepts. Work through them in order for best results, but feel free to jump ahead if you have prior experience.

**Safety First:** Always follow safety procedures. Your drone is powerful and fast. Respect it, and it will teach you amazing things!
