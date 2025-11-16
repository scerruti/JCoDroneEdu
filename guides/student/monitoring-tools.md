---
layout: guide
title: Monitoring Tools - Sensors and Controller
category: Student Guide
permalink: /guides/student/monitoring-tools.html
---

# Monitoring Tools: Real-Time Sensor and Controller Visibility

**Want to see what your drone is actually doing?**

When programming your CoDrone EDU, it's incredibly helpful to monitor sensor data and controller input as your code runs. This guide covers powerful monitoring tools that make debugging and learning much easier.

## Overview

The JCoDroneEdu library provides two complementary monitoring tools:

| Tool | What It Shows | Use Case |
|------|---------------|----------|
| **SensorDisplayGui** | All drone sensors in a real-time GUI | Debugging flight behavior, learning about sensors |
| **ControllerInputGui** | Joystick and button states | Testing controller input, understanding button layout |
| **BothMonitors** | Everything at once! | Complete visibility, interactive demos |
| **Easy monitors** | One-line monitoring | Quick checks while coding |

---

## SensorDisplayGui - Monitor Drone Sensors

### What It Does

SensorDisplayGui displays real-time sensor data from your CoDrone EDU:

- **Battery level** - Keep track of remaining power
- **Motion sensors** - Acceleration, gyroscope, and orientation angles  
- **Altitude** - Height above ground from pressure sensor
- **Range sensors** - Front and bottom distance sensors
- **Position** - X, Y, Z position from optical flow
- **Card color** - Color detection from the color sensor
- **Visual indicators** - Roll/pitch bars and heading arrow

The GUI updates automatically at 5 Hz (5 times per second).

### How to Run It

**Using Gradle:**
```bash
./gradlew runSensorDisplayGui
```

**From your IDE:**
- Run the main class: `com.otabi.jcodroneedu.examples.SensorDisplayGui`
- Location: `src/test/java/com/otabi/jcodroneedu/examples/SensorDisplayGui.java`

### Example: Using While Flying

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.examples.SensorDisplayGui;
import javax.swing.SwingUtilities;

public class FlightWithMonitor {
    public static void main(String[] args) {
        // Start sensor monitor in background
        SwingUtilities.invokeLater(() -> {
            SensorDisplayGui gui = new SensorDisplayGui();
            gui.start();
        });
        
        // Give GUI time to connect
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        
        // Now do your flight - sensor monitor shows live updates!
        Drone drone = new Drone();
        drone.pair();
        drone.takeoff();
        drone.hover(3);
        drone.land();
        drone.close();
    }
}
```

### Key Features

- **Non-intrusive** - Doesn't interfere with your flight code
- **Real-time updates** - 5 Hz refresh rate
- **Visual indicators** - Roll/pitch bars and heading arrow
- **Safe** - Pure monitoring only, no flight commands
- **Automatic reconnection** - Handles connection issues gracefully

---

## ControllerInputGui - Monitor Controller Input

### What It Does

ControllerInputGui provides a comprehensive display of your controller:

- **Left and right joysticks** - Position displayed on 2D panels with visual indicators
- **Shoulder buttons** - L1, L2, R1, R2 with color feedback
- **Center buttons** - H, S, P
- **D-pad arrows** - Up, Down, Left, Right
- **Connection status** - Visual indicator when connected
- **Joystick values** - Numeric X,Y coordinates (-100 to 100)

### How to Run It

**Using Gradle:**
```bash
./gradlew runControllerInputGui
```

**From your IDE:**
- Run the main class: `com.otabi.jcodroneedu.tools.ControllerInputGui`
- Location: `src/main/java/com/otabi/jcodroneedu/tools/ControllerInputGui.java`

### Example: Testing Controller Input

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.examples.SimpleControllerMonitor;

public class TestControllerInput {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        try {
            drone.pair();
            System.out.println("Connected!");
            
            System.out.println("Move joysticks and press buttons!");
            System.out.println("Watch the GUI for real-time feedback.");
            
            // Wait for left joystick movement
            while (drone.getLeftJoystickX() == 0 && drone.getLeftJoystickY() == 0) {
                try { Thread.sleep(50); } catch (InterruptedException e) { break; }
            }
            
            System.out.println("Left joystick moved!");
            System.out.println("X: " + drone.getLeftJoystickX());
            System.out.println("Y: " + drone.getLeftJoystickY());
            
            drone.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            drone.close();
        }
    }
}
```

### Understanding Joystick Values

Joysticks return values from **-100 to 100**:

- **Center position**: X=0, Y=0
- **Full up**: Y=100
- **Full down**: Y=-100
- **Full left**: X=-100
- **Full right**: X=100

### Key Features

- **Complete coverage** - All buttons and joysticks
- **Visual display** - See joystick position in 2D
- **Button debouncing** - See every press clearly
- **Real-time updates** - 20 Hz polling rate
- **Color feedback** - Green for pressed, gray for released
- **Safe operation** - Monitoring only

---

## Using Both Together

### The BothMonitors Example

Want to see **everything** at once?

```bash
./gradlew runBothMonitors
```

This opens two windows side-by-side:
1. **Left**: SensorDisplayGui showing all sensor data
2. **Right**: ControllerInputGui showing controller input

Plus a console interface with interactive controls!

### BothMonitors Interactive Features

**L1 Button Hold-to-Takeoff:**
- Hold L1 for 3 seconds to make the drone take off
- Buzzer warning with increasing urgency
- Console countdown shows: "Takeoff in 3... 2... 1..."
- Watch both monitors update as the drone flies!

**Monitor Integration:**
- See battery drain in real-time during flight
- Watch altitude increase after takeoff
- Track controller input as you move joysticks
- Visual feedback for all button presses

---

## Easy One-Line Monitors

Don't want to deal with GUI code? Add monitoring with just **one line**!

### EasySensorMonitor

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.SensorMonitor;

public class QuickTest {
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        // ONE LINE - opens sensor monitor window!
        new SensorMonitor(drone);
        
        // Program normally - sensors update in background
        drone.takeoff();
        drone.hover(3);
        drone.land();
        drone.close();
    }
}
```

Run it:
```bash
./gradlew runEasySensorMonitor
```

### EasyControllerMonitor

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.ControllerMonitor;

public class TestInput {
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        // ONE LINE - opens controller monitor window!
        new ControllerMonitor(drone);
        
        System.out.println("Move joysticks and press buttons!");
        
        // Wait for input
        while (drone.getLeftJoystickX() == 0) {
            try { Thread.sleep(50); } catch (InterruptedException e) { break; }
        }
        
        System.out.println("Joystick moved!");
        drone.close();
    }
}
```

Run it:
```bash
./gradlew runEasyControllerMonitor
```

---

## Which Tool Should I Use?

**Problem: "My sensor code isn't working!"**
→ Use `EasySensorMonitor` - add one line, see all sensors

**Problem: "Is my controller responding?"**
→ Use `EasyControllerMonitor` - add one line, see all inputs

**Want to understand sensors?**
→ Run `SensorDisplayGui` and watch values change as you move the drone

**Want to understand controller input?**
→ Run `ControllerInputGui` and explore the button layout

**Need sensors and controller together?**
→ Run `BothMonitors` for complete visibility

**Building your own custom GUI?**
→ Use `SensorMonitorPanel` and `ControllerInputPanel` components

---

## Custom GUI with Reusable Panels

For advanced projects, use the reusable panel components:

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.tools.ControllerInputPanel;
import javax.swing.*;

public class MyCustomGUI {
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        // Create the controller input panel
        ControllerInputPanel controllerPanel = new ControllerInputPanel(drone);
        
        // Add to your custom window
        JFrame frame = new JFrame("My Custom Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(controllerPanel);
        frame.pack();
        frame.setVisible(true);
        
        // Start monitoring
        controllerPanel.startMonitoring();
        
        System.out.println("Custom controller monitor is running!");
    }
}
```

Run it:
```bash
./gradlew runSimpleControllerMonitor
```

---

## Quick Reference

### Gradle Commands

```bash
# Sensor monitoring
./gradlew runSensorDisplayGui
./gradlew runEasySensorMonitor

# Controller monitoring  
./gradlew runControllerInputGui
./gradlew runEasyControllerMonitor

# Both together
./gradlew runBothMonitors

# Custom GUI example
./gradlew runSimpleControllerMonitor
```

### Example File Locations

All examples are in `src/test/java/com/otabi/jcodroneedu/examples/`:

- `SensorDisplayGui.java` - Full sensor monitoring GUI
- `ControllerInputGui.java` - Full controller monitoring GUI
- `EasySensorMonitor.java` - One-line sensor monitor
- `EasyControllerMonitor.java` - One-line controller monitor
- `SimpleControllerMonitor.java` - Custom GUI with panel
- `BothMonitors.java` - Interactive demo with both monitors

---

## Tips for Effective Monitoring

1. **Start every debugging session** by opening a monitor - seeing what your drone is actually doing makes fixing problems much faster!

2. **Use easy monitors for quick checks** - Add one line of code and keep programming

3. **Use full GUIs for learning** - Run them to understand what sensors and buttons do

4. **Combine with other tools** - Use monitoring alongside the API reference and examples

5. **Watch real-time updates** - See how values change as you move the drone or press buttons

---

**Next:** Continue to [API Reference]({{ '/guides/student/api-reference.html' | relative_url }}) or [Debugging]({{ '/guides/student/debugging.html' | relative_url }})
