---
layout: guide
title: Getting Started
category: Student Guide
permalink: /guides/student/getting-started.html
---

## Installation

This section covers installing everything you need to program your CoDrone EDU.

### What You'll Need

**Hardware:**
- CoDrone EDU drone and controller
- USB cable for connecting controller to computer
- Fully charged batteries

**Software:**
- Java Development Kit (JDK) 11 or newer
- An IDE: VS Code (recommended), IntelliJ IDEA, or BlueJ
- CoDrone EDU Java library

### Step 1: Install Java

Download and install the JDK:
1. Go to [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/) or [AdoptOpenJDK](https://adoptopenjdk.net/)
2. Choose your operating system
3. Run the installer
4. Accept defaults

**Verify installation** - Open a terminal and type:
```
java -version
```

You should see version 11 or higher.

### Step 2: Choose and Install an IDE

**Option A: VS Code (Recommended for beginners)**
- Download from [code.visualstudio.com](https://code.visualstudio.com/)
- Install Java Extension Pack from the Extensions marketplace
- Lightweight and professional

**Option B: IntelliJ IDEA (Best all-around)**
- Download Community Edition from [jetbrains.com](https://www.jetbrains.com/idea/)
- Professional-grade features
- More heavy-weight

**Option C: BlueJ (Simplest)**
- Download from [bluej.org](https://www.bluej.org/)
- Designed for teaching
- Great for visualization

### Step 3: Get the CoDrone EDU Library

1. Visit [GitHub Releases](https://github.com/scerruti/JCoDroneEdu/releases)
2. Download the latest JAR file
3. Save it to a known location (e.g., `C:\CoDroneLibrary\` on Windows)

### Step 4: Set Up Your First Project

**In VS Code:**
1. Create a new folder for your project
2. Open folder in VS Code
3. Create new file: `ConnectionTest.java`
4. Create `lib` folder and copy JAR file into it
5. Right-click JAR → "Add to Java Source Path"

## Connecting Your Drone

Before you can program your drone, you need to establish a connection.

### Physical Setup

1. **Turn on the controller** - Press the power button
2. **Turn on the drone** - Press the power button (watch for LED indicators)
3. **Wait for pairing** - LEDs will show blue when ready
4. **Connect controller to computer** - Use USB cable

### Connection Test Program

Create a file called `ConnectionTest.java`:

```java
import com.otabi.jcodroneedu.Drone;

public class ConnectionTest {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        try {
            drone.pair();
            System.out.println("✓ Connected successfully!");
            
            int battery = drone.getBattery();
            System.out.println("Battery level: " + battery + "%");
            
            drone.close();
        } catch (Exception e) {
            System.out.println("✗ Connection failed: " + e.getMessage());
            System.out.println("\nChecklist:");
            System.out.println("- Is the USB cable connected?");
            System.out.println("- Is the controller powered on?");
            System.out.println("- Is the drone powered on?");
            System.out.println("- Is the library JAR in your classpath?");
        }
    }
}
```

Run this program. If you see "Connected successfully!" and your battery level, you're ready to fly!

## Next Steps

Once you can connect successfully, you're ready to:
- [Your First Flight]({{ '/guides/student/first-flight.html' | relative_url }}) - Make your drone take off and land
- [Flight Concepts]({{ '/guides/student/flight-concepts.html' | relative_url }}) - Learn basic movement commands

---

**Having trouble?** Check the [Debugging section]({{ '/guides/student/debugging.html' | relative_url }}) for common issues.
