---
layout: guide
title: Adding Sensors
category: Architecture
permalink: /architecture/design-guide/adding-sensors.html
---

## Integrating New Sensors

Guide for adding new sensor types to JCoDroneEdu.

---

## Sensor Architecture

### Sensor Types

**Built-in Sensors:**
- Battery level (millivolts → percentage)
- Height/Altitude (barometric pressure)
- Range (ultrasonic distance)
- IMU (accelerometer, gyroscope)
- Temperature (thermistor)

**Query Model:**
```java
// Synchronous - query on demand
int battery = drone.getBattery();       // Blocks until response
double[] accel = drone.getAcceleration();
```

---

## Adding a New Sensor: Humidity

### Step 1: Define Sensor Class

```java
/**
 * Represents humidity sensor readings
 */
public class HumiditySensor {
    private final int rawValue;        // 0-255
    private final double percentage;   // 0-100%
    private final long timestamp;
    
    public HumiditySensor(int rawValue) {
        this.rawValue = rawValue;
        this.percentage = (rawValue / 255.0) * 100;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getRawValue() { return rawValue; }
    public double getHumidityPercent() { return percentage; }
    public long getTimestamp() { return timestamp; }
}
```

### Step 2: Add to Drone API

```java
public class Drone implements AutoCloseable {
    // ...existing code...
    
    /**
     * Get current humidity reading
     * @return Humidity percentage (0-100%)
     */
    public double getHumidity() throws IOException {
        return this.sensorService.readHumidity();
    }
    
    /**
     * Get raw humidity sensor value
     * @return Raw ADC value (0-255)
     */
    public int getHumidityRaw() throws IOException {
        return this.sensorService.readHumidityRaw();
    }
}
```

### Step 3: Implement in SensorService

```java
public class SensorService {
    private static final byte HUMIDITY_REQUEST = 0x42;
    private static final int HUMIDITY_TIMEOUT = 200;  // ms
    
    /**
     * Read humidity sensor via protocol
     */
    public double readHumidity() throws IOException {
        byte[] request = new byte[] { HUMIDITY_REQUEST };
        byte[] response = this.protocol.query(request, HUMIDITY_TIMEOUT);
        
        // Verify response
        if (response == null || response.length < 2) {
            throw new IOException("Humidity sensor timeout");
        }
        
        // Parse response
        int rawValue = response[1] & 0xFF;
        return (rawValue / 255.0) * 100.0;
    }
    
    public int readHumidityRaw() throws IOException {
        byte[] request = new byte[] { HUMIDITY_REQUEST };
        byte[] response = this.protocol.query(request, HUMIDITY_TIMEOUT);
        
        if (response == null || response.length < 2) {
            throw new IOException("Humidity sensor timeout");
        }
        
        return response[1] & 0xFF;
    }
}
```

### Step 4: Update Protocol Handler

Add humidity query support:

```java
public class ProtocolHandler {
    public static final byte QUERY_HUMIDITY = 0x42;
    
    public byte[] handleHumidityQuery() {
        // Request humidity from drone
        // Wait for response
        // Return response bytes
    }
}
```

### Step 5: Test the Sensor

```java
@Test
public void testGetHumidity() throws Exception {
    // Mock protocol to return humidity data
    mockProtocol.setQueryResponse(0x42, new byte[] { 0x42, 150 });
    
    try (Drone drone = new Drone(mockProtocol)) {
        double humidity = drone.getHumidity();
        
        // 150/255 * 100 = 58.8%
        assertEquals(58.8, humidity, 0.1);
    }
}

@Test
public void testGetHumidity_SensorTimeout() throws Exception {
    mockProtocol.setQueryTimeout(0x42);
    
    try (Drone drone = new Drone(mockProtocol)) {
        assertThrows(IOException.class, () -> {
            drone.getHumidity();
        });
    }
}
```

### Step 6: Integration Test

```java
@Test
public void testHumiditySensor_RealHardware() throws Exception {
    try (Drone drone = new Drone(true)) {
        // Read humidity multiple times
        for (int i = 0; i < 10; i++) {
            double humidity = drone.getHumidity();
            System.out.println("Humidity: " + humidity + "%");
            
            // Verify reasonable range
            assertTrue(humidity >= 0 && humidity <= 100);
            
            Thread.sleep(100);
        }
    }
}
```

---

## Sensor Reading Patterns

### Pattern 1: Simple Value

```java
// Returns single numeric value
public int getBattery() throws IOException {
    return this.sensorService.readBattery();
}
```

### Pattern 2: Multiple Values

```java
// Returns object with multiple readings
public class MotionData {
    public double[] acceleration;  // x, y, z
    public double[] rotation;      // pitch, roll, yaw
}

public MotionData getMotion() throws IOException {
    return this.sensorService.readMotion();
}
```

### Pattern 3: Streaming Data

```java
// For continuous monitoring (future feature)
public void subscribeToSensor(String sensorName, SensorListener listener) {
    // Listen for sensor updates as they arrive
}

public interface SensorListener {
    void onUpdate(SensorReading reading);
}
```

---

## Sensor Accuracy Considerations

### Document Limitations

```java
/**
 * Get temperature in Celsius
 * 
 * NOTE: Sensor accuracy is ±2°C and reads 10-15°C low due to 
 * heat generated by drone electronics. Calibration curve provided
 * in {@link #getCalibratedTemperature()}.
 * 
 * @return Raw temperature reading in Celsius
 * @see #getCalibratedTemperature()
 */
public double getTemperature() throws IOException { ... }
```

### Provide Calibration

```java
public class TemperatureCalibration {
    // Lookup table for temperature correction
    private static final Map<Integer, Double> CALIBRATION = 
        Map.ofEntries(
            Map.entry(0, 10),
            Map.entry(50, 40),
            Map.entry(100, 65)
        );
    
    public static double calibrate(int rawValue) {
        // Interpolate from lookup table
        // Or apply formula: corrected = raw + offset
    }
}
```

---

## Performance Optimization

### Batch Sensor Reads

```java
// INEFFICIENT: Three separate USB queries
int battery = drone.getBattery();        // Query 1
int height = drone.getHeight();          // Query 2
double temp = drone.getTemperature();    // Query 3

// EFFICIENT: Single query returns all
public class AllSensors {
    public int battery;
    public int height;
    public double temperature;
}

AllSensors all = drone.getAllSensors();  // Single query
int battery = all.battery;
int height = all.height;
```

### Caching

```java
private long lastBatteryRead = 0;
private int cachedBattery = -1;
private static final long CACHE_TIME = 100;  // ms

public int getBattery() throws IOException {
    long now = System.currentTimeMillis();
    
    if (now - lastBatteryRead > CACHE_TIME) {
        // Fresh read needed
        cachedBattery = this.sensorService.readBattery();
        lastBatteryRead = now;
    }
    
    return cachedBattery;
}
```

---

## Common Sensor Issues

### Issue: Sensor always returns 0

**Check:**
1. Is sensor connected to drone?
2. Is protocol query reaching drone?
3. Is response parsing correct?

### Issue: Sensor values fluctuate wildly

**Solutions:**
- Add smoothing filter (moving average)
- Verify sensor calibration
- Check for electromagnetic interference
- Increase sample count

### Issue: Sensor read times out

**Possible causes:**
- USB bus congestion
- Drone unresponsive
- Sensor hardware failure

---

## Sensor Documentation Template

When adding sensor, document:

```
Sensor Name: [Name]
Type: [Analog/Digital/Computed]
Range: [Min-Max with units]
Accuracy: [±X%]
Update Rate: [Hz or ms]
Calibration: [Formula or notes]
Limitations: [Known issues]
Protocol Byte: [0x##]
Example Usage: [Code snippet]
```

---

Next: [Testing Strategy]({{ '/architecture/design-guide/testing.html' | relative_url }})
