---
layout: guide
title: Testing Strategy
category: Architecture
permalink: /architecture/design-guide/testing.html
---

## Testing JCoDroneEdu

Comprehensive testing approach for drone software development.

---

## Test Types

### Unit Tests

**Target:** Individual methods and classes in isolation

**Example:**
```java
@Test
public void testMoveForward_Speed05() throws Exception {
    CommandService service = new CommandService(mockProtocol);
    service.moveForward(0.5);
    
    verify(mockProtocol).sendCommand(argThat(cmd -> {
        // Verify speed byte is correct
        return cmd[2] == 127;  // 0.5 * 255
    }));
}
```

**Coverage Goals:**
- 80%+ code coverage
- All error paths tested
- All parameter combinations validated

### Integration Tests

**Target:** Multiple components working together

**Example:**
```java
@Test
public void testTakeoffAndLand() throws Exception {
    MockDrone drone = new MockDrone();
    
    drone.takeoff();
    assertTrue(drone.isFlying());
    assertEquals(100, drone.getThrottle());
    
    drone.land();
    assertFalse(drone.isFlying());
    assertEquals(0, drone.getThrottle());
}
```

### System Tests

**Target:** Full drone application with hardware

**Example:**
```java
@Test
public void testFlightPattern_Square() throws Exception {
    try (Drone drone = new Drone(true)) {
        drone.takeoff();
        
        for (int i = 0; i < 4; i++) {
            drone.moveForward(0.5);
            Thread.sleep(2000);
            drone.turnLeft(0.5);
            Thread.sleep(1000);
        }
        
        drone.land();
        // Observe: Did drone fly square?
    }
}
```

---

## Mock Objects Strategy

### Mock Protocol

```java
public class MockProtocol implements Protocol {
    private Queue<byte[]> responses = new LinkedList<>();
    private int echoMode = ECHO_SUCCESS;
    
    @Override
    public void send(byte[] command) {
        // Record command
        lastCommand = command;
    }
    
    @Override
    public byte[] query(byte[] request, int timeout) {
        // Return pre-configured response
        return responses.poll();
    }
    
    public void setResponse(byte[] response) {
        responses.add(response);
    }
    
    public byte[] getLastCommand() {
        return lastCommand;
    }
}

// Usage in test
@Test
public void testGetBattery() throws Exception {
    MockProtocol mock = new MockProtocol();
    mock.setResponse(new byte[] { 0x01, 95 });  // Battery: 95%
    
    try (Drone drone = new Drone(mock)) {
        assertEquals(95, drone.getBattery());
    }
}
```

### Mock Drone

```java
public class MockDrone {
    private boolean flying = false;
    private int battery = 100;
    private double position = 0;
    
    public void takeoff() {
        flying = true;
        battery -= 1;
    }
    
    public void moveForward(double speed) {
        if (!flying) throw new IllegalStateException();
        position += speed;
        battery -= 1;
    }
    
    public boolean isFlying() {
        return flying;
    }
    
    // Getters for state verification
}

// Usage
@Test
public void testMoveForward_ConsistentBattery() throws Exception {
    MockDrone drone = new MockDrone();
    drone.takeoff();
    
    int batteryBefore = drone.getBattery();
    drone.moveForward(0.5);
    int batteryAfter = drone.getBattery();
    
    // Battery decreases with activity
    assertTrue(batteryAfter < batteryBefore);
}
```

---

## Test Organization

### Package Structure

```
src/test/java/com/otabi/jcodroneedu/
├── connection/
│   ├── DroneConnectionTest.java
│   └── ProtocolTest.java
├── command/
│   ├── FlightCommandTest.java
│   └── CommandServiceTest.java
├── sensor/
│   ├── BatterySensorTest.java
│   ├── HeightSensorTest.java
│   └── SensorServiceTest.java
├── integration/
│   ├── TakeoffLandTest.java
│   └── PatternFlightTest.java
└── mock/
    ├── MockProtocol.java
    ├── MockDrone.java
    └── TestFixtures.java
```

### Test Naming Convention

```
test[MethodName]_[Scenario]_[Expected]

Examples:
testMoveForward_ValidSpeed_CommandSent
testGetBattery_Timeout_ThrowsIOException
testTakeoff_AlreadyFlying_ThrowsIllegalState
```

---

## Test Fixtures

### Common Setup

```java
public abstract class DroneTest {
    protected MockProtocol protocol;
    protected Drone drone;
    
    @Before
    public void setup() throws Exception {
        protocol = new MockProtocol();
        drone = new Drone(protocol);
    }
    
    @After
    public void cleanup() {
        if (drone != null) {
            drone.close();
        }
    }
    
    // Utility methods
    protected void assertCommandSent(byte type, byte subcommand) {
        byte[] cmd = protocol.getLastCommand();
        assertEquals(type, cmd[0]);
        assertEquals(subcommand, cmd[1]);
    }
}

@Test
public class MoveCommandTest extends DroneTest {
    @Test
    public void testMoveForward_Sends0x88Command() throws Exception {
        drone.moveForward(0.5);
        assertCommandSent(0x88, 0x41);  // Move forward subcommand
    }
}
```

---

## Coverage Goals

### By Component

| Component | Min Coverage | Why |
|-----------|------------|-----|
| Command execution | 90% | Critical for flight |
| Sensor reading | 85% | Data accuracy matters |
| Error handling | 100% | All failure paths |
| Utilities | 80% | Less critical |

### Coverage Commands

```bash
# Generate coverage report
mvn clean test jacoco:report

# View in browser
open target/site/jacoco/index.html

# Enforce minimum coverage
mvn clean verify -Dcoverage.minimum=0.80
```

---

## Continuous Testing

### Pre-Commit

```bash
#!/bin/bash
# Run before committing
mvn clean test -DskipITs  # Skip integration tests
if [ $? -ne 0 ]; then
    echo "Tests failed!"
    exit 1
fi
```

### CI/CD Pipeline

```yaml
# .github/workflows/test.yml
name: Test
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK
        uses: actions/setup-java@v2
      - name: Run tests
        run: mvn clean test
      - name: Upload coverage
        uses: codecov/codecov-action@v1
```

---

## Hardware Testing Checklist

**Before releasing new version:**

- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] Code coverage > 80%
- [ ] No memory leaks detected
- [ ] Tested on real hardware
- [ ] Drone recovers from errors
- [ ] Performance acceptable
- [ ] Documentation updated
- [ ] Examples still work
- [ ] Backwards compatible

---

## Debugging Techniques

### Add Logging

```java
private static final Logger log = Logger.getLogger(Drone.class.getName());

public void moveForward(double speed) throws IOException {
    log.fine("moveForward called with speed=" + speed);
    
    // Validate
    if (speed < 0 || speed > 1) {
        log.warning("Invalid speed, clamping to 0-1");
        speed = Math.max(0, Math.min(1, speed));
    }
    
    // Execute
    log.info("Sending move command");
    this.commandService.moveForward(speed);
    log.fine("Move command completed");
}
```

### Enable Debug Mode

```java
drone.setDebugMode(true);  // Enables detailed logging
```

---

## Test Data

### Sensor Response Examples

```java
// Battery responses
new byte[] { 0x01, 100 }  // 100%
new byte[] { 0x01, 50 }   // 50%
new byte[] { 0x01, 10 }   // 10% (low battery)

// Height responses
new byte[] { 0x09, 0, 100 }  // 100 cm
new byte[] { 0x09, 0, 250 }  // 250 cm

// Temperature responses
new byte[] { 0x0A, 25 }   // 25°C
new byte[] { 0x0A, 0 }    // 0°C
```

---

## Known Test Challenges

### Challenge: Timing Tests

Problem: Flight commands with `Thread.sleep()` are fragile

Solution:
```java
// Use FakeClock instead of real time
public interface Clock {
    long currentTimeMillis();
}

// Mock clock in tests
Clock fakeClock = () -> 0;

// Production uses System.currentTimeMillis()
```

### Challenge: USB/Hardware Mocking

Problem: Real USB operations are slow and hardware-dependent

Solution:
- Mock entire Protocol layer
- Have separate system tests for real hardware
- Use CI system with drone hardware for regression testing

---

Next: [Contributing]({{ '/architecture/design-guide/contributing.html' | relative_url }})
