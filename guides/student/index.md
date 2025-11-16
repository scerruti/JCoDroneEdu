# Student Guides

Welcome to the CoDrone EDU Student Guides! These guides help you learn to program your drone effectively.

## Available Guides

### [Monitoring Tools: Sensors and Controller Input](sensor-monitoring.md)

Learn how to monitor your drone's sensors and controller input in real-time. This guide covers:

- **SensorDisplayGui** - Visual display of all drone sensors
- **ControllerInputGui** - Visual display of controller joysticks and buttons  
- **BothMonitors** - Run both monitors together for complete visibility
- **Easy one-line monitors** - Quick debugging with minimal code
- **Reusable panel components** - Build custom GUIs with monitoring

Perfect for debugging, learning, and understanding what your drone is doing!

---

## Main Documentation

For comprehensive API documentation and tutorials, see the main [Student Guide](../../student-guide.md).

---

## Quick Links

### Running the Tools

```bash
# Sensor monitoring
./gradlew runSensorDisplayGui
./gradlew runEasySensorMonitor

# Controller monitoring  
./gradlew runControllerInputGui
./gradlew runEasyControllerMonitor

# Both together
./gradlew runBothMonitors
```

### Example Code Locations

All examples are in `src/main/java/com/otabi/jcodroneedu/examples/`:

- `SensorDisplayGui.java` - Full sensor monitoring GUI
- `ControllerInputGui.java` - Full controller monitoring GUI
- `EasySensorMonitor.java` - One-line sensor monitor
- `EasyControllerMonitor.java` - One-line controller monitor
- `SimpleControllerMonitor.java` - Custom GUI with panel
- `BothMonitors.java` - Interactive demo with both monitors

---

## Contributing

Found an error or want to suggest improvements? Please open an issue or pull request on [GitHub](https://github.com/scerruti/JCoDroneEdu).
