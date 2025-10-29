# Running the Error Monitoring Example

## Quick Start

Three modes are available with convenient Gradle tasks:

### 1. Demo Mode (Recommended for Learning)
```bash
./gradlew runErrorMonitoringDemo
```
- **No hardware required**
- Shows all API patterns and usage
- Perfect for classroom instruction
- Safe for code review and understanding

### 2. Connect Mode (Safe Testing)
```bash
./gradlew runErrorMonitoringConnect
```
- **Connects to drone but doesn't fly**
- Reads real error data from hardware
- Tests error monitoring logic
- Safe for development and debugging

### 3. Flight Mode (Actual Flight - CAUTION!)
```bash
./gradlew runErrorMonitoringFly
```
- **⚠️ WARNING: Performs actual flight operations!**
- Ensure drone is in a safe, open area
- Requires fully charged battery
- Keep emergency stop ready
- Pre-flight safety check performed automatically

## Command Line Arguments (Alternative)

You can also run directly with Java:

```bash
# Build first
./gradlew build

# Demo mode
java -cp build/libs/JCoDroneEdu-1.0-SNAPSHOT.jar \
  com.otabi.jcodroneedu.examples.ErrorMonitoringExample

# Connect mode
java -cp build/libs/JCoDroneEdu-1.0-SNAPSHOT.jar \
  com.otabi.jcodroneedu.examples.ErrorMonitoringExample --connect

# Flight mode
java -cp build/libs/JCoDroneEdu-1.0-SNAPSHOT.jar \
  com.otabi.jcodroneedu.examples.ErrorMonitoringExample --fly

# Help
java -cp build/libs/JCoDroneEdu-1.0-SNAPSHOT.jar \
  com.otabi.jcodroneedu.examples.ErrorMonitoringExample --help
```

## Classroom Workflow

### Phase 1: Theory (Whole Class)
```bash
# Teacher projects on screen
./gradlew runErrorMonitoringDemo
```
- No drones needed
- Discuss error checking patterns
- Review API usage
- Q&A about error handling

### Phase 2: Observation (With Hardware)
```bash
# Students at their desks with drones
./gradlew runErrorMonitoringConnect
```
- Each student connects their drone
- Observe real error states
- See calibration in action
- Drones stay safely on desks

### Phase 3: Demonstration (Teacher Only)
```bash
# Teacher in open area
./gradlew runErrorMonitoringFly
```
- Teacher demonstrates flight monitoring
- Shows emergency landing responses
- Discusses real-world error scenarios
- **Students observe, don't fly yet**

### Phase 4: Student Practice
- Students modify the code
- Test with connect mode first
- Teacher approves flight tests
- Supervised flight operations

## What Each Mode Does

### Demo Mode Output
```
Mode: DEMO - Showing API usage without hardware connection
Use --connect to read real error data, or --fly for full demo

=== CoDrone EDU Error Monitoring Example ===

--- Example 1: Array-Based Error Checking (Python-compatible) ---
No error data available yet

--- Example 2: Object-Based Error Checking (Java-idiomatic) ---
No error data available yet

--- Example 3: Pre-Flight Safety Check ---
⚠️  Cannot verify drone status - no error data

--- Example 4: Error Monitoring During Flight ---
(Demo mode - simulating flight monitoring pattern)
...
```

### Connect Mode Output
```
Mode: CONNECT - Will connect to drone and read real error data
Connecting to drone...
✅ Connected!

=== CoDrone EDU Error Monitoring Example ===

--- Example 1: Array-Based Error Checking (Python-compatible) ---
Timestamp: 1234.567 seconds
Sensor error flags: 0x0
State error flags: 0x0

--- Example 2: Object-Based Error Checking (Java-idiomatic) ---
Timestamp: 2024-10-15T...
✅ All systems normal!

--- Example 3: Pre-Flight Safety Check ---
✅ Drone passed pre-flight safety check!

--- Example 5: Waiting for Calibration ---
Initiating gyro reset...
✅ Calibration complete!
...
```

### Flight Mode Output
```
Mode: FLY - Will connect to drone and perform flight operations
⚠️  WARNING: Ensure drone is in a safe flying area!
Connecting to drone...
✅ Connected!

=== CoDrone EDU Error Monitoring Example ===
...
--- Example 3: Pre-Flight Safety Check ---
✅ Drone passed pre-flight safety check!
Taking off...

--- Example 4: Error Monitoring During Flight ---
Monitoring errors during real flight operations...
Check 1: ✅ All systems normal
   → Performing gentle test maneuver
Check 2: ✅ All systems normal
...
Landing drone...
✅ Landed safely
```

## Safety Features

All modes include:
- ✅ Automatic error detection
- ✅ Pre-flight safety validation
- ✅ Clear mode indication
- ✅ Help text available

Flight mode additionally includes:
- ✅ Pre-flight battery check
- ✅ Calibration status check
- ✅ Continuous error monitoring (1 Hz)
- ✅ Automatic emergency landing on:
  - Low battery
  - Critical errors
  - System failures
- ✅ Attitude stabilization
- ✅ Controlled descent and landing

## Troubleshooting

### "No error data available"
- **Demo mode**: Expected - no hardware connected
- **Connect mode**: Check USB connection, try reconnecting

### "Cannot verify drone status"
- Ensure drone is powered on
- Check Bluetooth/RF connection
- Wait a few seconds for initial data

### "Pre-flight check failed"
- Check battery level (should be > 30%)
- Ensure drone is on flat surface
- Wait for calibration to complete
- Check for propeller obstructions

### "Gyroscope calibration timed out"
This can happen in Example 5 if:
- **Drone was just powered on**: Wait 5-10 seconds after pairing before running
- **No error data received**: Check connection, try reconnecting
- **Drone is moving**: Must be completely stationary on flat surface
- **Connection delay**: Normal on first run, try again

**Workaround**: The example now catches this error and continues with remaining examples. Calibration can be tested separately once drone is fully initialized.

### Gradle task not found
```bash
# Rebuild to register tasks
./gradlew build
# List all tasks
./gradlew tasks --all
```

## Advanced Usage

### Custom Arguments
You can pass additional arguments through Gradle:
```bash
./gradlew runErrorMonitoringDemo --args="--help"
```

### Integration Testing
Use connect mode in CI/CD if hardware is available:
```bash
# In test script
./gradlew runErrorMonitoringConnect
# Check exit code for pass/fail
```

### Debugging
Enable debug output:
```bash
./gradlew runErrorMonitoringConnect --info
```

## Related Documentation

- **ERROR_MONITORING_EXAMPLE_MODES.md** - Complete feature documentation
- **ERROR_DATA_API_SUMMARY.md** - API reference
- **CALIBRATION_ENHANCEMENT.md** - Calibration details
- **ErrorMonitoringExample.java** - Source code with JavaDoc

## Support

For issues or questions:
1. Check the example's `--help` output
2. Review the comprehensive documentation
3. Examine the source code JavaDoc
4. Test in demo mode first to isolate hardware issues

---
**Version**: 2.5  
**Last Updated**: October 15, 2025
