package com.otabi.jcodroneedu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the detectWall(int distance) method in Drone.
 * Uses a MockDrone to simulate range sensor values.
 */
public class DetectWallTest {
    static class TestDrone extends Drone {
        private double mockFrontRange = 100;
        public void setMockFrontRange(double value) { this.mockFrontRange = value; }
        @Override
        public double getFrontRange() { return mockFrontRange; }
    }

    @Test
    void detectsWallWhenObstacleWithinDistance() {
        TestDrone drone = new TestDrone();
        drone.setMockFrontRange(20);
        assertTrue(drone.detectWall(30), "Should detect wall when obstacle is within distance");
    }

    @Test
    void doesNotDetectWallWhenNoObstacleWithinDistance() {
        TestDrone drone = new TestDrone();
        drone.setMockFrontRange(50);
        assertFalse(drone.detectWall(30), "Should not detect wall when obstacle is farther than distance");
    }

    @Test
    void detectsWallAtExactDistance() {
        TestDrone drone = new TestDrone();
        drone.setMockFrontRange(30);
        assertFalse(drone.detectWall(30), "Should not detect wall when obstacle is exactly at distance (uses <)");
    }
}
