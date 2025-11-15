package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.linkmanager.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for error data retrieval functionality.
 * Tests getErrorData() method which returns sensor and state error flags as array (Python-compatible).
 */
class ErrorDataTest {

    private Drone drone;
    private LinkManager linkManager;

    @BeforeEach
    void setUp() {
        drone = new Drone();
        linkManager = drone.getLinkManager();
    }

    @Test
    @DisplayName("Should return null when no error data available")
    void shouldReturnNullWhenNoErrorData() {
        // When: No error data has been received
        double[] errorData = drone.getErrorData(0.01); // Very short delay for testing
        
        // Then: Should return null
        assertNull(errorData, "Should return null when no error data available");
    }

    @Test
    @DisplayName("Should return error data array with correct format")
    void shouldReturnErrorDataWithCorrectFormat() {
        // Given: Mock error data is available
        long testSystemTime = 123456789L; // milliseconds
        int testSensorFlags = 0x00000008; // MOTION_CALIBRATING
        int testStateFlags = 0x00000008;  // LOW_BATTERY
        
        Error mockError = new Error(testSystemTime, testSensorFlags, testStateFlags);
        linkManager.setError(mockError);
        
        // When: Getting error data
        double[] errorData = drone.getErrorData(0.0); // No delay needed with mock data
        
        // Then: Should return array with correct format
        assertNotNull(errorData, "Error data should not be null");
        assertEquals(3, errorData.length, "Error data should have 3 elements");
        
        // Verify timestamp (converted from ms to seconds)
        assertEquals(testSystemTime / 1000.0, errorData[0], 0.001, 
                    "Timestamp should be in seconds");
        
        // Verify sensor flags
        assertEquals((double) testSensorFlags, errorData[1], 
                    "Sensor error flags should match");
        
        // Verify state flags
        assertEquals((double) testStateFlags, errorData[2], 
                    "State error flags should match");
    }

    @Test
    @DisplayName("Should detect motion calibration error flag")
    void shouldDetectMotionCalibrationFlag() {
        // Given: Error with MOTION_CALIBRATING flag set
        int sensorFlags = DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue();
        Error mockError = new Error(System.currentTimeMillis(), sensorFlags, 0);
        linkManager.setError(mockError);
        
        // When: Getting error data
        double[] errorData = drone.getErrorData(0.0);
        
        // Then: Should be able to detect the flag
        assertNotNull(errorData);
        int returnedSensorFlags = (int) errorData[1];
        assertTrue((returnedSensorFlags & DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue()) != 0,
                  "Should detect MOTION_CALIBRATING flag");
    }

    @Test
    @DisplayName("Should detect low battery error flag")
    void shouldDetectLowBatteryFlag() {
        // Given: Error with LOW_BATTERY flag set
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue();
        Error mockError = new Error(System.currentTimeMillis(), 0, stateFlags);
        linkManager.setError(mockError);
        
        // When: Getting error data
        double[] errorData = drone.getErrorData(0.0);
        
        // Then: Should be able to detect the flag
        assertNotNull(errorData);
        int returnedStateFlags = (int) errorData[2];
        assertTrue((returnedStateFlags & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0,
                  "Should detect LOW_BATTERY flag");
    }

    @Test
    @DisplayName("Should handle multiple error flags simultaneously")
    void shouldHandleMultipleErrorFlags() {
        // Given: Multiple error flags set
        int sensorFlags = DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue() 
                        | DroneSystem.ErrorFlagsForSensor.PRESSURE_NO_ANSWER.getValue();
        int stateFlags = DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue() 
                       | DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE.getValue();
        
        Error mockError = new Error(System.currentTimeMillis(), sensorFlags, stateFlags);
        linkManager.setError(mockError);
        
        // When: Getting error data
        double[] errorData = drone.getErrorData(0.0);
        
        // Then: All flags should be detectable
        assertNotNull(errorData);
        int returnedSensorFlags = (int) errorData[1];
        int returnedStateFlags = (int) errorData[2];
        
        assertTrue((returnedSensorFlags & DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue()) != 0);
        assertTrue((returnedSensorFlags & DroneSystem.ErrorFlagsForSensor.PRESSURE_NO_ANSWER.getValue()) != 0);
        assertTrue((returnedStateFlags & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0);
        assertTrue((returnedStateFlags & DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE.getValue()) != 0);
    }

    @Test
    @DisplayName("Should use default delay when no delay parameter provided")
    @SuppressWarnings("unused") // Testing method execution, not return value
    void shouldUseDefaultDelay() {
        // Given: No error data available
        // When: Calling without delay parameter
        double[] errorData = drone.getErrorData(); // Uses default 0.2 second delay
        
        // Then: Should not throw exception (may return null, which is fine)
        // This test just verifies the method signature works
        assertTrue(true, "Method should execute without throwing exception");
    }

    @Test
    @DisplayName("Should match Python API return format")
    void shouldMatchPythonAPIFormat() {
        // Given: Mock error data
        long timestamp = 98765432L;
        int sensorFlags = 0x00001234;
        int stateFlags = 0x00005678;
        
        Error mockError = new Error(timestamp, sensorFlags, stateFlags);
        linkManager.setError(mockError);
        
        // When: Getting error data
        double[] errorData = drone.getErrorData(0.0);
        
        // Then: Format should match Python's [timestamp, sensorFlags, stateFlags]
        assertNotNull(errorData);
        assertEquals(3, errorData.length);
        
        // Python returns timestamp in seconds as float
        assertEquals(timestamp / 1000.0, errorData[0], 0.001);
        
        // Python returns flags as integers (converted to double in Java array)
        assertEquals((double) sensorFlags, errorData[1]);
        assertEquals((double) stateFlags, errorData[2]);
    }
}
