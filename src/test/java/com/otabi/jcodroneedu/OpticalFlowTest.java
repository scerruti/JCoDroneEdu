package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.dronestatus.RawFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for optical flow sensor methods.
 * 
 * <p>This class tests the optical flow API methods to ensure they work correctly
 * and provide accurate data conversion between units.</p>
 * 
 * @author Educational Team
 * @since 1.0
 * @educational Tests advanced navigation sensor APIs
 */
public class OpticalFlowTest {
    
    private Drone drone;
    
    @BeforeEach
    void setUp() {
        drone = new Drone();
    }
    
    /**
     * Test unit conversion with simulated data.
     * This test demonstrates how the optical flow API would work with real data.
     */
    @Test
    void testUnitConversion_WithSimulatedData() {
        // Create a sample RawFlow object with test data
        RawFlow testFlow = new RawFlow(0.1f, 0.2f); // 0.1 m/s in X, 0.2 m/s in Y
        
        // Manually set the flow data in DroneStatus (for testing purposes)
        drone.getDroneStatus().setRawFlow(testFlow);
        
        // Test X velocity conversion
        double xInMeters = drone.get_flow_velocity_x("m");
        double xInCm = drone.get_flow_velocity_x("cm");
        double xInMm = drone.get_flow_velocity_x("mm");
        double xInInches = drone.get_flow_velocity_x("in");
        
        assertEquals(0.1, xInMeters, 0.001, "X velocity should be 0.1 m/s");
        assertEquals(10.0, xInCm, 0.001, "X velocity should be 10 cm/s");
        assertEquals(100.0, xInMm, 0.001, "X velocity should be 100 mm/s");
        assertEquals(3.93701, xInInches, 0.001, "X velocity should be ~3.94 inches/s");
        
        // Test Y velocity conversion
        double yInMeters = drone.get_flow_velocity_y("m");
        double yInCm = drone.get_flow_velocity_y("cm");
        double yInMm = drone.get_flow_velocity_y("mm");
        double yInInches = drone.get_flow_velocity_y("in");
        
        assertEquals(0.2, yInMeters, 0.001, "Y velocity should be 0.2 m/s");
        assertEquals(20.0, yInCm, 0.001, "Y velocity should be 20 cm/s");
        assertEquals(200.0, yInMm, 0.001, "Y velocity should be 200 mm/s");
        assertEquals(7.87402, yInInches, 0.001, "Y velocity should be ~7.87 inches/s");
        
        // Test default unit (cm)
        double xDefaultUnit = drone.get_flow_velocity_x();
        double yDefaultUnit = drone.get_flow_velocity_y();
        assertEquals(10.0, xDefaultUnit, 0.001, "Default X velocity should be 10 cm/s");
        assertEquals(20.0, yDefaultUnit, 0.001, "Default Y velocity should be 20 cm/s");
        
        // Test flow data array
        double[] flowData = drone.get_flow_data();
        assertNotNull(flowData, "Flow data should not be null");
        assertEquals(3, flowData.length, "Flow data should have 3 elements");
        assertEquals(0.1, flowData[1], 0.001, "Flow data X should be 0.1");
        assertEquals(0.2, flowData[2], 0.001, "Flow data Y should be 0.2");
        
        // flowData[0] is timestamp, should be reasonable
        assertTrue(flowData[0] > 0, "Timestamp should be positive");
    }
    
    @Test
    void testDeprecatedMethods_WithSimulatedData() {
        // Create test data
        RawFlow testFlow = new RawFlow(0.05f, 0.15f); // 0.05 m/s in X, 0.15 m/s in Y
        drone.getDroneStatus().setRawFlow(testFlow);
        
        // Test that deprecated methods still function and return correct values
        @SuppressWarnings("deprecation")
        double oldX = drone.get_flow_x();
        double newX = drone.get_flow_velocity_x();
        assertEquals(oldX, newX, 0.001, "New method should return same value as deprecated method");
        
        @SuppressWarnings("deprecation")
        double oldY = drone.get_flow_y();
        double newY = drone.get_flow_velocity_y();
        assertEquals(oldY, newY, 0.001, "New method should return same value as deprecated method");
        
        // Test with units
        @SuppressWarnings("deprecation")
        double oldXWithUnit = drone.get_flow_x("m");
        double newXWithUnit = drone.get_flow_velocity_x("m");
        assertEquals(oldXWithUnit, newXWithUnit, 0.001, "Methods with units should return same values");
        
        @SuppressWarnings("deprecation")
        double oldYWithUnit = drone.get_flow_y("m");
        double newYWithUnit = drone.get_flow_velocity_y("m");
        assertEquals(oldYWithUnit, newYWithUnit, 0.001, "Methods with units should return same values");
    }
    
    @Test
    void testInvalidUnits_ThrowException() {
        // Set some test data first
        RawFlow testFlow = new RawFlow(0.1f, 0.2f);
        drone.getDroneStatus().setRawFlow(testFlow);
        
        // Test that invalid units throw appropriate exceptions
        assertThrows(IllegalArgumentException.class, () -> {
            drone.get_flow_velocity_x("invalid_unit");
        }, "Should throw exception for invalid unit");
        
        assertThrows(IllegalArgumentException.class, () -> {
            drone.get_flow_velocity_y("invalid_unit");
        }, "Should throw exception for invalid unit");
        
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("deprecation")
            double result = drone.get_flow_x("invalid_unit");
        }, "Deprecated method should also throw exception for invalid unit");
        
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("deprecation")
            double result = drone.get_flow_y("invalid_unit");
        }, "Deprecated method should also throw exception for invalid unit");
    }
    
    @Test
    void testNoFlowData_ReturnsZero() {
        // Ensure no flow data is set (default state)
        drone.getDroneStatus().setRawFlow(null);
        
        // Methods should return 0.0 when no data is available
        // Note: This test doesn't call sendRequest, so it won't cause NPE
        // We're testing the case where data is explicitly set to null
        assertEquals(0.0, drone.get_flow_velocity_x("cm"), 0.001, "Should return 0.0 when no flow data available");
        assertEquals(0.0, drone.get_flow_velocity_y("cm"), 0.001, "Should return 0.0 when no flow data available");
        assertNull(drone.get_flow_data(), "Should return null when no flow data available");
    }
    
    @Test
    void testZeroVelocityData() {
        // Test with zero velocity data (drone stationary)
        RawFlow zeroFlow = new RawFlow(0.0f, 0.0f);
        drone.getDroneStatus().setRawFlow(zeroFlow);
        
        assertEquals(0.0, drone.get_flow_velocity_x("cm"), 0.001, "Should return 0.0 for zero X velocity");
        assertEquals(0.0, drone.get_flow_velocity_y("cm"), 0.001, "Should return 0.0 for zero Y velocity");
        assertEquals(0.0, drone.get_flow_velocity_x("m"), 0.001, "Should return 0.0 for zero X velocity in meters");
        assertEquals(0.0, drone.get_flow_velocity_y("m"), 0.001, "Should return 0.0 for zero Y velocity in meters");
        
        double[] flowData = drone.get_flow_data();
        assertNotNull(flowData, "Flow data should not be null even with zero velocities");
        assertEquals(0.0, flowData[1], 0.001, "Flow data X should be 0.0");
        assertEquals(0.0, flowData[2], 0.001, "Flow data Y should be 0.0");
    }
    
    @Test
    void testNegativeVelocityData() {
        // Test with negative velocity data (reverse movement)
        RawFlow negativeFlow = new RawFlow(-0.1f, -0.05f);
        drone.getDroneStatus().setRawFlow(negativeFlow);
        
        assertEquals(-0.1, drone.get_flow_velocity_x("m"), 0.001, "Should handle negative X velocity");
        assertEquals(-0.05, drone.get_flow_velocity_y("m"), 0.001, "Should handle negative Y velocity");
        assertEquals(-10.0, drone.get_flow_velocity_x("cm"), 0.001, "Should convert negative X velocity to cm");
        assertEquals(-5.0, drone.get_flow_velocity_y("cm"), 0.001, "Should convert negative Y velocity to cm");
    }
}
