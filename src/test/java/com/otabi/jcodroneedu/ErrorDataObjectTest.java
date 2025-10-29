package com.otabi.jcodroneedu;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ErrorData immutable object (Tier 3 API).
 * Verifies type-safe error checking and Java-idiomatic patterns.
 */
class ErrorDataObjectTest {
    
    @Test
    void shouldCreateFromValidArray() {
        // Arrange
        double timestamp = 123.456;
        int sensorFlags = DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue();
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue();
        double[] errorArray = {timestamp, sensorFlags, stateFlags};
        
        // Act
        ErrorData errorData = ErrorData.fromArray(errorArray);
        
        // Assert
        assertNotNull(errorData);
        assertEquals(123.456, errorData.getTimestampSeconds(), 0.001);
    }
    
    @Test
    void shouldReturnNullFromInvalidArray() {
        // Assert
        assertNull(ErrorData.fromArray(null));
        assertNull(ErrorData.fromArray(new double[0]));
        assertNull(ErrorData.fromArray(new double[]{1.0, 2.0})); // Too short
        assertNull(ErrorData.fromArray(new double[]{1.0, 2.0, 3.0, 4.0})); // Too long
    }
    
    @Test
    void shouldDetectSensorErrorUsingTypeSafeMethod() {
        // Arrange
        int sensorFlags = DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue();
        ErrorData errorData = new ErrorData(100.0, sensorFlags, 0);
        
        // Act & Assert
        assertTrue(errorData.hasSensorError(DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING));
        assertFalse(errorData.hasSensorError(DroneSystem.ErrorFlagsForSensor.PRESSURE_NO_ANSWER));
        assertTrue(errorData.isCalibrating()); // Convenience method
    }
    
    @Test
    void shouldDetectStateErrorUsingTypeSafeMethod() {
        // Arrange
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue();
        ErrorData errorData = new ErrorData(100.0, 0, stateFlags);
        
        // Act & Assert
        assertTrue(errorData.hasStateError(DroneSystem.ErrorFlagsForState.LOW_BATTERY));
        assertFalse(errorData.hasStateError(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE));
        assertTrue(errorData.isLowBattery()); // Convenience method
    }
    
    @Test
    void shouldReturnErrorsAsEnumSets() {
        // Arrange
        int sensorFlags = DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue()
                         | DroneSystem.ErrorFlagsForSensor.PRESSURE_NO_ANSWER.getValue();
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()
                        | DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE.getValue();
        ErrorData errorData = new ErrorData(100.0, sensorFlags, stateFlags);
        
        // Act
        Set<DroneSystem.ErrorFlagsForSensor> sensorErrors = errorData.getSensorErrors();
        Set<DroneSystem.ErrorFlagsForState> stateErrors = errorData.getStateErrors();
        
        // Assert
        assertEquals(2, sensorErrors.size());
        assertTrue(sensorErrors.contains(DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING));
        assertTrue(sensorErrors.contains(DroneSystem.ErrorFlagsForSensor.PRESSURE_NO_ANSWER));
        
        assertEquals(2, stateErrors.size());
        assertTrue(stateErrors.contains(DroneSystem.ErrorFlagsForState.LOW_BATTERY));
        assertTrue(stateErrors.contains(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE));
    }
    
    @Test
    void shouldReturnEmptySetsWhenNoErrors() {
        // Arrange
        ErrorData errorData = new ErrorData(100.0, 0, 0);
        
        // Act & Assert
        assertTrue(errorData.getSensorErrors().isEmpty());
        assertTrue(errorData.getStateErrors().isEmpty());
        assertFalse(errorData.hasAnyErrors());
        assertFalse(errorData.hasAnySensorErrors());
        assertFalse(errorData.hasAnyStateErrors());
    }
    
    @Test
    void shouldDetectCriticalErrors() {
        // Arrange - Low battery is critical
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue();
        ErrorData errorData = new ErrorData(100.0, 0, stateFlags);
        
        // Act & Assert
        assertTrue(errorData.hasCriticalErrors());
        
        // Arrange - Takeoff failure is critical
        stateFlags = DroneSystem.ErrorFlagsForState.TAKEOFF_FAILURE_CHECK_PROPELLER_AND_MOTOR.getValue();
        errorData = new ErrorData(100.0, 0, stateFlags);
        assertTrue(errorData.hasCriticalErrors());
        
        // Arrange - No critical errors
        errorData = new ErrorData(100.0, 0, 0);
        assertFalse(errorData.hasCriticalErrors());
    }
    
    @Test
    void shouldConvertTimestampCorrectly() {
        // Arrange
        double timestampSeconds = 123.456;
        ErrorData errorData = new ErrorData(timestampSeconds, 0, 0);
        
        // Act
        Instant instant = errorData.getTimestamp();
        double retrievedSeconds = errorData.getTimestampSeconds();
        
        // Assert
        assertEquals(123456, instant.toEpochMilli());
        assertEquals(timestampSeconds, retrievedSeconds, 0.001);
    }
    
    @Test
    void shouldProvideRawFlagsForCompatibility() {
        // Arrange
        int sensorFlags = 0x42;
        int stateFlags = 0x18;
        ErrorData errorData = new ErrorData(100.0, sensorFlags, stateFlags);
        
        // Act & Assert
        assertEquals(sensorFlags, errorData.getSensorErrorFlags());
        assertEquals(stateFlags, errorData.getStateErrorFlags());
    }
    
    @Test
    void shouldProduceReadableToString() {
        // Arrange - No errors
        ErrorData noErrors = new ErrorData(100.0, 0, 0);
        
        // Act & Assert
        String str = noErrors.toString();
        assertTrue(str.contains("no errors"));
        
        // Arrange - With errors
        int sensorFlags = DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue();
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue();
        ErrorData withErrors = new ErrorData(100.0, sensorFlags, stateFlags);
        
        // Act
        str = withErrors.toString();
        
        // Assert
        assertTrue(str.contains("sensorErrors"));
        assertTrue(str.contains("stateErrors"));
    }
    
    @Test
    void shouldProduceDetailedString() {
        // Arrange - No errors
        ErrorData noErrors = new ErrorData(100.0, 0, 0);
        
        // Act
        String detailed = noErrors.toDetailedString();
        
        // Assert
        assertTrue(detailed.contains("No errors"));
        assertTrue(detailed.contains("all systems normal"));
        
        // Arrange - With errors
        int sensorFlags = DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue();
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue();
        ErrorData withErrors = new ErrorData(100.0, sensorFlags, stateFlags);
        
        // Act
        detailed = withErrors.toDetailedString();
        
        // Assert
        assertTrue(detailed.contains("Sensor Errors"));
        assertTrue(detailed.contains("MOTION_CALIBRATING"));
        assertTrue(detailed.contains("State Errors"));
        assertTrue(detailed.contains("LOW_BATTERY"));
    }
    
    @Test
    void shouldBeImmutable() {
        // Arrange
        int sensorFlags = DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue();
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue();
        ErrorData errorData = new ErrorData(100.0, sensorFlags, stateFlags);
        
        // Act - Get sets and try to modify them
        Set<DroneSystem.ErrorFlagsForSensor> sensorErrors = errorData.getSensorErrors();
        Set<DroneSystem.ErrorFlagsForState> stateErrors = errorData.getStateErrors();
        
        // Assert - Sets should be new copies each time
        assertNotSame(sensorErrors, errorData.getSensorErrors());
        assertNotSame(stateErrors, errorData.getStateErrors());
        
        // Verify we can't modify through the original
        assertEquals(1, sensorErrors.size());
        assertEquals(1, stateErrors.size());
    }
    
    @Test
    void shouldWorkWithDroneGetErrorsMethod() {
        // This test verifies integration with Drone class using getErrors()
        // Note: Requires actual Drone instance, so this is more of a documentation test
        
        // Example usage pattern:
        // Drone drone = new Drone();
        // ErrorData errorData = drone.getErrors();
        // if (errorData != null && errorData.isLowBattery()) {
        //     drone.land();
        // }
        
        // We can test the conversion method directly
        double[] errorArray = {123.456, 0x01, 0x02};
        ErrorData errorData = ErrorData.fromArray(errorArray);
        
        assertNotNull(errorData);
        assertEquals(123.456, errorData.getTimestampSeconds(), 0.001);
        assertEquals(0x01, errorData.getSensorErrorFlags());
        assertEquals(0x02, errorData.getStateErrorFlags());
    }
    
    @Test
    void shouldCompareTypeSafeVsBitwiseApproaches() {
        // Arrange
        double[] errorArray = {
            100.0,
            DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue(),
            DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()
        };
        
        // Act - Bitwise approach (Tier 1 - Python-compatible)
        int stateFlags = (int) errorArray[2];
        boolean lowBatteryBitwise = (stateFlags & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0;
        
        // Act - Type-safe approach (Tier 3 - Java-idiomatic)
        ErrorData errorData = ErrorData.fromArray(errorArray);
        boolean lowBatteryTypeSafe = errorData.isLowBattery();
        
        // Assert - Both approaches should give same result
        assertEquals(lowBatteryBitwise, lowBatteryTypeSafe);
        
        // But the type-safe approach is much cleaner:
        // Tier 1: if ((((int)errorData[2]) & ErrorFlagsForState.LOW_BATTERY.getValue()) != 0)
        // Tier 3: if (errorData.isLowBattery())
        assertTrue(lowBatteryTypeSafe, "Type-safe approach should detect low battery");
    }
}
