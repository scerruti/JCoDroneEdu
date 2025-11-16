# Monitoring Tools: Sensors and Controller Input

**Learn how to see what your drone is doing in real-time!**

When programming your CoDrone EDU, it's incredibly helpful to see live sensor data and controller input. This guide shows you two powerful monitoring tools that make debugging and learning much easier.

---

## Table of Contents

1. [Overview](#overview)
2. [SensorDisplayGui - Monitor Drone Sensors](#sensordisplaygui---monitor-drone-sensors)
3. [ControllerInputGui - Monitor Controller Input](#controllerinputgui---monitor-controller-input)
4. [Using Both Together](#using-both-together)
5. [Easy One-Line Monitors](#easy-one-line-monitors)
6. [Building Custom GUIs with Panels](#building-custom-guis-with-panels)
7. [Which Tool Should I Use?](#which-tool-should-i-use)

---

## Overview

The JCoDroneEdu library provides several tools to help you see what's happening with your drone:

| Tool | Purpose | When to Use |
|------|---------|-------------|
| **SensorDisplayGui** | Shows all drone sensor data (battery, altitude, angles, etc.) | When debugging flight behavior or learning about sensors |
| **ControllerInputGui** | Shows joystick and button states from the controller | When testing controller input or building manual controls |
| **BothMonitors** | Runs both GUIs together with interactive demo | When you want to see everything at once |
| **EasySensorMonitor** | One-line sensor monitoring | Quick sensor checks while coding |
| **EasyControllerMonitor** | One-line controller monitoring | Quick controller checks while coding |

All these tools are **safe by default** - they won't make your drone fly unless you explicitly tell them to!

---

## SensorDisplayGui - Monitor Drone Sensors

### What It Does

SensorDisplayGui is a Swing-based application that displays real-time sensor data from your CoDrone EDU. It shows:

- **Battery level** - Keep track of remaining power
- **Motion sensors** - Acceleration, gyroscope, and orientation angles
- **Altitude** - Height above ground from pressure sensor
- **Range sensors** - Front and bottom distance sensors
- **Position** - X, Y, Z position from optical flow
- **Card color** - Color detection from the color sensor
- **Visual indicators** - Roll/pitch bars and heading arrow

The GUI updates automatically at 5 Hz (5 times per second) and provides an intuitive visual display of your drone's state.

### How to Run It

**Method 1: Using Gradle (Recommended)**
```bash
./gradlew runSensorDisplayGui
```

**Method 2: From Your IDE**
- Run the `SensorDisplayGui.java` main class
- Location: `src/main/java/com/otabi/jcodroneedu/examples/SensorDisplayGui.java`

**Method 3: Using ExampleMenu**
```bash
./gradlew runExampleMenu
```
Then select "Sensor Display GUI" from the menu.

### What You'll See

When you run SensorDisplayGui, a window opens showing:

```
┌─────────────────────────────────────────────────────────┐
│          CoDrone EDU Sensor Monitor                     │
├─────────────────────────────────────────────────────────┤
│  Battery: 85%              Card Color: [color swatch]   │
├─────────────────────────────────────────────────────────┤
│  Accel (g)    │  Gyro (deg/s)  │  Angle (deg)          │
│  0.02, 0.01,  │  0.5, -0.2,    │  -1.2, 0.8,           │
│  0.98         │  0.1           │  45.3                 │
├─────────────────────────────────────────────────────────┤
│  Roll (deg)   │  Heading       │  Pitch (deg)          │
│  [bar ----]   │  [arrow ↑]     │  [bar ----]           │
├─────────────────────────────────────────────────────────┤
│  Front Range Sensor       │  Bottom Range Sensor       │
│  [progress bar: 125 mm]   │  [progress bar: 45 mm]     │
├─────────────────────────────────────────────────────────┤
│  Altitude / Pressure / Temp   │  Position (m)          │
│  Alt: 0.52m                   │  X: 0.05m              │
│  Press: 1013.2hPa             │  Y: -0.02m             │
│  Temp: 22.5°C                 │  Z: 0.48m              │
└─────────────────────────────────────────────────────────┘
```

### Example Code

The simplest way to use it is to just run the application:

```java
import com.otabi.jcodroneedu.examples.SensorDisplayGui;

public class MySensorMonitor {
    public static void main(String[] args) {
        // Just run it!
        SensorDisplayGui.main(args);
    }
}
```

Or use it while your drone flies:

```java
import com.otabi.jcodroneedu.Drone;
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
        
        // Now do your flight programming
        // The sensor monitor shows live updates!
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

- **Non-intrusive**: Doesn't interfere with your flight code
- **Real-time updates**: 5 Hz refresh rate
- **Visual indicators**: Roll/pitch bars and heading arrow for easy understanding
- **Range sensors**: Progress bars show distance to obstacles
- **Safe**: No flight commands - pure monitoring only
- **Automatic reconnection**: Handles connection issues gracefully

---

## ControllerInputGui - Monitor Controller Input

### What It Does

ControllerInputGui is a comprehensive tool for monitoring CoDrone EDU controller input. It provides a visual representation of:

- **Left and right joysticks** - Position displayed on 2D panels
- **Shoulder buttons** - L1, L2, R1, R2
- **Center buttons** - H, S, P
- **D-pad arrows** - Up, Down, Left, Right
- **Button states** - Green when pressed, gray when released
- **Joystick values** - Numeric X,Y coordinates (-100 to 100)
- **Connection status** - Visual indicator

The GUI features button debouncing (shows presses for 200ms minimum) and joystick visualization with crosshairs and range circles.

### How to Run It

**Method 1: Using Gradle (Recommended)**
```bash
./gradlew runControllerInputGui
```

**Method 2: From Your IDE**
- Run the `ControllerInputGui.java` main class
- Location: `src/main/java/com/otabi/jcodroneedu/examples/ControllerInputGui.java`

**Method 3: Using ExampleMenu**
```bash
./gradlew runExampleMenu
```
Then select "Controller Input GUI" from the menu.

### What You'll See

When you run ControllerInputGui, a window opens showing:

```
┌────────────────────────────────────────────────────────────────────┐
│               CoDrone EDU Controller                               │
├────────────────────────────────────────────────────────────────────┤
│  Shoulder L     │        Status             │    Shoulder R        │
│  [L1]  [L2]     │  ✓ Connected              │    [R1]  [R2]        │
│                 │  L1 R1 ↑                  │                      │
│   [H]           │  Last input: Button       │           [Power]    │
│  ┌─────────┐    │                           │    ┌─────────┐      │
│  │    ●    │    │     D-Pad (Center)        │    │    ●    │      │
│  │    ↑    │    │        [↑]                │    │         │      │
│  │  ← ● →  │ J1 │    [←]   [→]              │ J2 │         │      │
│  │    ↓    │    │        [↓]                │    │         │      │
│  └─────────┘    │                           │    └─────────┘      │
│   [S]           │                           │           [P]        │
├────────────────────────────────────────────────────────────────────┤
│  Left: X= 45, Y= -30           Right: X=  0, Y=  0                │
└────────────────────────────────────────────────────────────────────┘
```

The joystick panels show:
- **Red dot** - Current joystick position
- **Gray crosshairs** - Center position (0, 0)
- **Range circles** - 50% and 100% deflection indicators
- **Live coordinates** - Numeric values updated in real-time

### Example Code

**Simple standalone monitor:**
```java
import com.otabi.jcodroneedu.examples.ControllerInputGui;

public class MyControllerMonitor {
    public static void main(String[] args) {
        // Just run it!
        ControllerInputGui.main(args);
    }
}
```

**Monitor while testing your flight code:**
```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.examples.ControllerInputGui;
import javax.swing.SwingUtilities;

public class TestControllerInput {
    public static void main(String[] args) {
        // Open controller monitor
        SwingUtilities.invokeLater(() -> {
            ControllerInputGui gui = new ControllerInputGui();
            gui.setVisible(true);
        });
        
        // Give GUI time to connect
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        
        // Now test your controller input code
        Drone drone = new Drone();
        drone.pair();
        
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
    }
}
```

### Key Features

- **Complete controller coverage**: All buttons and joysticks
- **Visual joystick display**: See exact position with 2D representation
- **Button debouncing**: Ensures you see every button press
- **Real-time updates**: 20 Hz polling rate
- **Color-coded feedback**: Green for pressed, gray for released
- **Coordinate display**: Shows numeric X,Y values
- **Connection indicator**: Know when controller is ready
- **Safe operation**: Monitoring only, no flight commands

### Understanding Joystick Values

The joysticks return values from **-100 to 100**:

- **Center position**: X=0, Y=0
- **Full up**: Y=100
- **Full down**: Y=-100
- **Full left**: X=-100
- **Full right**: X=100
- **Diagonal**: Combination of X and Y values

### Understanding Button States

Buttons have three states reported by the controller:

- **Press** - Button just pressed down (triggers action)
- **Down** - Button is being held (continuous state)
- **Up** - Button just released

The GUI uses debouncing to show button presses for at least 200ms, making it easier to see quick button taps.

---

## Using Both Together

### The BothMonitors Example

Want to see **everything** at once? The `BothMonitors` example runs both the sensor monitor and controller monitor together, plus adds an interactive demo!

### How to Run It

```bash
./gradlew runBothMonitors
```

Or run `BothMonitors.java` from your IDE.

### What You'll See

Two windows open side-by-side:
1. **Left window**: SensorDisplayGui showing all sensor data
2. **Right window**: ControllerInputGui showing controller input

Plus a console interface with interactive controls!

### Interactive Features

The BothMonitors demo includes special features:

**L1 Button Hold-to-Takeoff:**
- Hold L1 for 3 seconds to make the drone take off
- Buzzer warning with increasing urgency (beeps get faster/higher)
- Console countdown shows: "Takeoff in 3... 2... 1..."
- Watch both monitors update as the drone flies!

**Console Controls:**
```
Press 'Q' and ENTER to quit (warns if drone is flying)
```

**Monitor Integration:**
- See battery drain in real-time during flight
- Watch altitude increase after takeoff
- Monitor all sensor values throughout flight
- Track controller input as you move joysticks
- Visual feedback for all button presses

### Example Output

```
╔════════════════════════════════════════════════════════════╗
║     CoDrone EDU - Dual Monitor Interactive Demo           ║
╚════════════════════════════════════════════════════════════╝

✓ Both monitors are running!
  - Left window: Sensor Monitor (with LAND and STOP buttons)
  - Right window: Controller Monitor (watch input in real-time)

═══════════════════════════════════════════════════════════
INTERACTIVE CONTROLS:
═══════════════════════════════════════════════════════════
  L1 Button:  Hold for 3 seconds to TAKEOFF
              (buzzer warning with increasing urgency)

  LAND Button (blue):  Click in sensor monitor to land
  STOP Button (red):   Click in sensor monitor for emergency stop

  Controller: Move joysticks to see real-time display updates
═══════════════════════════════════════════════════════════

  Press 'Q' and ENTER to quit (will warn if drone is flying)

>>> L1 DETECTED! Starting takeoff sequence...
    Takeoff in 3...
    Takeoff in 2...
    Takeoff in 1...
    🚁 TAKING OFF! Watch the sensors update!

✓ Drone is flying!
  - Watch the altitude sensor in the Sensor Monitor
  - Try the LAND button (blue) to land safely
  - Try the STOP button (red) for emergency stop
  - Move the joysticks to see controller response
```

### Code Example

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.SensorMonitor;
import com.otabi.jcodroneedu.gui.ControllerMonitor;

public class MyBothMonitors {
    public static void main(String[] args) {
        // Connect to drone
        Drone drone = new Drone();
        drone.pair();
        
        // Open BOTH monitors with just two lines!
        new SensorMonitor(drone);
        new ControllerMonitor(drone);
        
        System.out.println("Both monitors are running!");
        
        // Now do your programming...
        // Both monitors update in background
        
        drone.takeoff();
        drone.hover(3);
        drone.land();
        drone.close();
    }
}
```

---

## Easy One-Line Monitors

Don't want to deal with GUI code? Use the "Easy" monitors for instant visibility!

### EasySensorMonitor

Monitor sensors with **ONE line of code**:

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.SensorMonitor;

public class QuickTest {
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        // ONE LINE - opens sensor monitor window!
        new SensorMonitor(drone);
        
        // Now program normally - sensors update in background
        drone.takeoff();
        drone.hover(3);
        drone.land();
        drone.close();
    }
}
```

**How to run:**
```bash
./gradlew runEasySensorMonitor
```

### EasyControllerMonitor

Monitor controller input with **ONE line of code**:

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.ControllerMonitor;

public class TestInput {
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        // ONE LINE - opens controller monitor window!
        new ControllerMonitor(drone);
        
        // Now test controller input
        System.out.println("Move joysticks and press buttons!");
        
        // Wait for input...
        while (drone.getLeftJoystickX() == 0) {
            try { Thread.sleep(50); } catch (InterruptedException e) { break; }
        }
        
        System.out.println("Left joystick moved!");
        drone.close();
    }
}
```

**How to run:**
```bash
./gradlew runEasyControllerMonitor
```

### When to Use Easy Monitors

Perfect for:
- **Quick debugging**: "Why isn't my sensor reading working?"
- **Learning**: "What does the accelerometer do?"
- **Testing**: "Is my controller connected?"
- **Demonstrations**: Show students live sensor/controller data
- **Minimal code**: Focus on logic, not GUI

Just add one line, and keep programming!

---

## Building Custom GUIs with Panels

Want to integrate monitoring into your own GUI? Use the reusable panel components!

### Using ControllerInputPanel

The `ControllerInputPanel` is a Swing component you can add to any JFrame:

```java
import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.ControllerInputPanel;
import javax.swing.*;

public class MyCustomGUI {
    public static void main(String[] args) {
        // Step 1: Create and connect your drone
        Drone drone = new Drone();
        drone.pair();
        
        // Step 2: Create the controller input panel
        ControllerInputPanel controllerPanel = new ControllerInputPanel(drone);
        
        // Step 3: Create your custom window
        JFrame frame = new JFrame("My Custom Controller Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Step 4: Add the panel to your window
        frame.add(controllerPanel);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center on screen
        
        // Step 5: Add cleanup when window closes
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                controllerPanel.stopMonitoring();
                drone.close();
            }
        });
        
        frame.setVisible(true);
        
        // Step 6: Start monitoring
        controllerPanel.startMonitoring();
        
        System.out.println("Custom controller monitor is running!");
    }
}
```

**How to run:**
```bash
./gradlew runSimpleControllerMonitor
```

### The 5-Step Pattern

All reusable panels follow the same pattern:

1. **Create and connect** your drone
2. **Create the panel** with your drone object
3. **Create your GUI** (JFrame, JPanel, etc.)
4. **Add the panel** to your GUI
5. **Start monitoring** when ready

### Combining Multiple Panels

You can build complex GUIs by combining panels:

```java
import javax.swing.*;
import java.awt.*;

public class MegaMonitor {
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.pair();
        
        // Create both panels
        SensorPanel sensorPanel = new SensorPanel(drone);
        ControllerInputPanel controllerPanel = new ControllerInputPanel(drone);
        
        // Build custom layout
        JFrame frame = new JFrame("Mega Monitor");
        frame.setLayout(new GridLayout(1, 2)); // Side-by-side
        
        frame.add(sensorPanel);
        frame.add(controllerPanel);
        
        frame.pack();
        frame.setVisible(true);
        
        // Start both monitors
        sensorPanel.startMonitoring();
        controllerPanel.startMonitoring();
    }
}
```

---

## Which Tool Should I Use?

Choose based on what you need to see:

### Quick Debugging

**Problem**: "My sensor code isn't working!"
- **Solution**: Use `EasySensorMonitor` - add one line, see all sensors

**Problem**: "Is my controller responding?"
- **Solution**: Use `EasyControllerMonitor` - add one line, see all inputs

### Learning and Exploration

**Want to understand sensors?**
- Run `SensorDisplayGui` and watch how values change
- Try moving the drone around
- See real-time altitude, angles, and ranges

**Want to understand controller input?**
- Run `ControllerInputGui` and move joysticks
- Press all the buttons
- Learn the value ranges and button states

### Building Your Own Project

**Need just sensors in your GUI?**
- Use `SensorPanel` component in your custom JFrame

**Need just controller input in your GUI?**
- Use `ControllerInputPanel` component in your custom JFrame

**Need both?**
- Use both panels in your custom layout

### Teaching and Demonstrations

**Showing how drones work?**
- Run `BothMonitors` for impressive dual display
- Hold L1 to demonstrate takeoff sequence
- Show all sensors updating in real-time

**Step-by-step tutorial?**
- Start with `EasySensorMonitor` (simplest)
- Progress to `SensorDisplayGui` (full featured)
- Show `ControllerInputGui` for input understanding
- End with `BothMonitors` for complete picture

---

## Summary

The JCoDroneEdu monitoring tools make learning and debugging much easier:

| Tool | Lines of Code | What It Shows | Best For |
|------|---------------|---------------|----------|
| EasySensorMonitor | 1 | All sensors | Quick checks |
| EasyControllerMonitor | 1 | All controller input | Quick checks |
| SensorDisplayGui | Run command | All sensors with GUI | Learning sensors |
| ControllerInputGui | Run command | All controller with GUI | Learning controller |
| BothMonitors | Run command | Everything + demo | Teaching & debugging |
| SensorPanel | 5 steps | Sensors in your GUI | Custom projects |
| ControllerInputPanel | 5 steps | Controller in your GUI | Custom projects |

**Pro Tip**: Start every debugging session by opening a monitor. Seeing what your drone is actually doing makes fixing problems much faster!

---

## Next Steps

- **Try each tool**: Run all the examples to see what they do
- **Read the source**: The example files are well-commented
- **Build your own**: Combine panels into a custom GUI
- **Explore the API**: Check the main [Student Guide](../../student-guide.md)

Happy monitoring! 🚁✨
