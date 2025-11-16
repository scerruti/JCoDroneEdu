---
layout: guide
title: Adding Methods
category: Architecture
permalink: /architecture/design-guide/adding-methods.html
---

## Adding New Flight Commands

Step-by-step guide for extending JCoDroneEdu with new movement commands.

---

## Architecture Overview

### Flight Command Pipeline

```
User Code:
drone.newCommand(params)
    ↓
Drone class method (in API)
    ↓
Validation layer
    ↓
CommandService.execute()
    ↓
Protocol builder (0x88 command format)
    ↓
USB transmission
    ↓
Drone firmware executes
    ↓
Echo received
    ↓
Return/update state
```

---

## Step 1: Define the Method

### In `Drone.java` class:

```java
/**
 * Move in a circular pattern
 * @param radius     Circle radius in meters (0.1-5.0)
 * @param speed      Movement speed (0.1-1.0)
 * @param direction  1 for clockwise, -1 for counter-clockwise
 */
public void moveCircle(double radius, double speed, int direction) 
    throws IOException, InterruptedException {
    
    // Validate parameters
    if (radius < 0.1 || radius > 5.0) {
        throw new IllegalArgumentException("Radius must be 0.1-5.0 meters");
    }
    if (speed < 0.1 || speed > 1.0) {
        throw new IllegalArgumentException("Speed must be 0.1-1.0");
    }
    if (direction != 1 && direction != -1) {
        throw new IllegalArgumentException("Direction must be 1 or -1");
    }
    
    // Delegate to command service
    this.commandService.moveCircle(radius, speed, direction);
}
```

### Design Considerations:

1. **Parameter Ranges**
   - Document valid ranges
   - Throw exceptions for invalid values
   - Use constants for limits

2. **Documentation**
   - Explain what the command does
   - Document all parameters
   - Note any side effects

3. **Consistency**
   - Follow naming conventions (verb + descriptor)
   - Match parameter types with similar methods
   - Return void for commands, value for queries

---

## Step 2: Implement in CommandService

### In `CommandService.java`:

```java
public void moveCircle(double radius, double speed, int direction) 
    throws IOException {
    
    // Build the protocol message
    byte[] command = buildCircleCommand(radius, speed, direction);
    
    // Send to drone
    this.protocolHandler.sendCommand(command);
    
    // Wait for echo (0x88 protocol)
    byte[] echo = this.protocolHandler.waitForEcho(COMMAND_TIMEOUT);
    
    // Verify success
    if (!verifyEcho(echo)) {
        throw new IOException("Circle command failed");
    }
    
    // Update state
    this.lastCommand = "moveCircle";
    this.isMoving = true;
}

private byte[] buildCircleCommand(double radius, double speed, int direction) {
    ByteBuffer buffer = ByteBuffer.allocate(COMMAND_SIZE);
    
    // Protocol format (example):
    buffer.put((byte) 0x88);           // Command type
    buffer.put((byte) 0x32);           // Circle subcommand
    
    // Encode radius (0-500 → 0x00-0xFF)
    int radiusEncoded = (int) (radius * 255 / 5.0);
    buffer.put((byte) radiusEncoded);
    
    // Encode speed (0-1 → 0x00-0xFF)
    int speedEncoded = (int) (speed * 255);
    buffer.put((byte) speedEncoded);
    
    // Direction (0x01 or 0xFF)
    buffer.put((byte) (direction > 0 ? 0x01 : 0xFF));
    
    // Add checksum
    byte[] data = buffer.array();
    byte checksum = calculateChecksum(data);
    buffer.put(checksum);
    
    return data;
}
```

### Key Points:

1. **Protocol Encoding**
   - Follow drone's protocol specification (0x88 format)
   - Map floating-point values to bytes
   - Include checksums

2. **Error Handling**
   - Check echo for success
   - Throw appropriate exceptions
   - Clean up on failure

3. **State Management**
   - Update internal state
   - Track command history
   - Maintain isMoving/isFlying flags

---

## Step 3: Add Protocol Handler Logic

### In `ProtocolHandler.java`:

```java
private static final byte CIRCLE_SUBCOMMAND = 0x32;

public void handleCircleCommand(double radius, double speed, int direction) {
    // Protocol-specific implementation
    // This is where drone hardware details are handled
}
```

---

## Step 4: Write Tests

### Unit Test:

```java
public class CommandServiceTest {
    
    @Test
    public void testMoveCircle_ValidParameters() throws Exception {
        CommandService service = new CommandService(mockProtocol);
        
        // Act
        service.moveCircle(2.0, 0.5, 1);
        
        // Assert
        verify(mockProtocol).sendCommand(any(byte[].class));
        assertEquals("moveCircle", service.getLastCommand());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMoveCircle_InvalidRadius() throws Exception {
        CommandService service = new CommandService(mockProtocol);
        
        // Act - radius > 5.0
        service.moveCircle(6.0, 0.5, 1);
    }
    
    @Test(expected = IOException.class)
    public void testMoveCircle_CommandFails() throws Exception {
        CommandService service = new CommandService(mockProtocol);
        mockProtocol.setEchoResponse(ECHO_FAILURE);
        
        // Act
        service.moveCircle(2.0, 0.5, 1);
    }
}
```

### Integration Test:

```java
@Test
public void testMoveCircle_RealDrone() throws Exception {
    try (Drone drone = new Drone(true)) {
        // Arrange
        drone.takeoff();
        
        // Act
        drone.moveCircle(1.0, 0.5, 1);  // Circle for demo
        Thread.sleep(5000);
        
        // Assert - drone moved (hard to verify without position tracking)
        assertTrue(drone.isFlying());
        
        drone.land();
    }
}
```

---

## Step 5: Documentation

### In Code Comments:

```java
/**
 * Moves drone in a circular pattern at specified radius and speed.
 * 
 * The drone will trace a circle while maintaining altitude and heading.
 * Useful for perimeter inspection or patrol patterns.
 * 
 * @param radius Radius of circle in meters. Range: 0.1-5.0 m
 *               Larger radius = wider circle
 * @param speed  Relative speed as fraction of max. Range: 0.1-1.0
 *               1.0 = maximum speed, 0.1 = minimum speed
 * @param direction Circle direction. 1 = clockwise, -1 = counter-clockwise
 *                   (when viewed from above)
 * 
 * @throws IllegalArgumentException if parameters out of range
 * @throws IOException if command fails or drone disconnected
 * @throws InterruptedException if thread interrupted during execution
 * 
 * @example
 * <pre>
 * try (Drone drone = new Drone(true)) {
 *     drone.takeoff();
 *     drone.moveCircle(2.0, 0.5, 1);  // 2m radius, half speed, clockwise
 *     Thread.sleep(5000);
 *     drone.land();
 * }
 * </pre>
 * 
 * @see #moveForward(double)
 * @see #turnLeft(double)
 */
public void moveCircle(double radius, double speed, int direction) { ... }
```

### In User Guide:

Add to [Student API Reference]({{ '/guides/student/api-reference.html' | relative_url }}) once stable:

```markdown
### moveCircle(radius, speed, direction)
Fly in a circular pattern

**Parameters:**
- radius: Circle radius (0.1-5.0 meters)
- speed: Movement speed (0.1-1.0, 1.0 = full speed)
- direction: 1 = clockwise, -1 = counter-clockwise

**Example:**
```java
drone.moveCircle(1.5, 0.5, 1);  // 1.5m circle at half speed
```
```

---

## Step 6: Code Review Checklist

Before committing, verify:

- [ ] Method has clear documentation
- [ ] Parameters have reasonable ranges
- [ ] Invalid parameters throw exceptions
- [ ] Command properly encoded in protocol
- [ ] Echo verification implemented
- [ ] State updated after success
- [ ] Tests cover normal and error cases
- [ ] No side effects on other commands
- [ ] Performance acceptable
- [ ] Thread-safe (if needed)
- [ ] Backwards compatible (no breaking changes)

---

## Common Patterns

### Pattern 1: Simple Movement

```java
public void moveDirection(double speed) {
    this.commandService.move(speed, 0, 0, 0);  // x-axis only
}
```

### Pattern 2: Synchronized Multi-Command

```java
public void sprayAndFly(double speed) {
    this.commandService.spray(true);  // Start spray
    this.moveForward(speed);          // Then fly
}
```

### Pattern 3: Parameterized Pattern

```java
public void flyPolygon(int sides, double speed) {
    double turnAngle = 360.0 / sides;
    double moveDistance = 2.0;  // Fixed distance per side
    
    for (int i = 0; i < sides; i++) {
        this.moveForward(speed);
        Thread.sleep((long) (moveDistance * 1000 / speed));
        this.turnLeft(turnAngle / 45);  // Normalize to 0-1 speed
    }
}
```

---

## Troubleshooting

### Issue: Command doesn't work

**Debugging steps:**
1. Verify protocol encoding (add debug logging)
2. Check echo response
3. Verify parameter ranges
4. Test with simpler command first

### Issue: Drone crashes after command

**Possible causes:**
- State not updated correctly
- Another command still executing
- Buffer overflow in protocol
- Parameter out of range not caught

---

Next: [Adding Sensors]({{ '/architecture/design-guide/adding-sensors.html' | relative_url }})
