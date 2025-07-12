package com.otabi.jcodroneedu.patterns;

import com.otabi.jcodroneedu.Drone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for BasicPatterns class.
 * 
 * These tests demonstrate:
 * - How to test code that controls external devices
 * - Using mocks to simulate drone behavior
 * - Parameter validation testing
 * - Educational test structure
 * 
 * Educational Goals:
 * - Learn about unit testing
 * - Understand mocking for external dependencies
 * - See how to validate method calls
 * - Practice test-driven thinking
 */
class BasicPatternsTest {
    
    @Mock
    private Drone mockDrone;
    
    private BasicPatterns patterns;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patterns = new BasicPatterns(mockDrone);
    }
    
    @Test
    @DisplayName("Square pattern should make 4 forward movements and 4 turns")
    void testSquarePattern() {
        // Given: valid parameters
        int sideLength = 50;
        int speed = 50;
        
        // When: flying a square pattern
        patterns.square(sideLength, speed);
        
        // Then: verify 4 forward movements and 4 clockwise turns
        verify(mockDrone, times(4)).go("forward", sideLength, speed);
        verify(mockDrone, times(4)).go("cw", 90, 30);
        verify(mockDrone, times(4)).hover(0.5);
    }
    
    @Test
    @DisplayName("Square pattern should reject invalid side lengths")
    void testSquarePatternInvalidSideLength() {
        // Test too small
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            patterns.square(5, 50); // Too small
        });
        assertTrue(exception1.getMessage().contains("Side length must be between 10 and 200 cm"));
        
        // Test too large
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            patterns.square(250, 50); // Too large
        });
        assertTrue(exception2.getMessage().contains("Side length must be between 10 and 200 cm"));
    }
    
    @Test
    @DisplayName("Square pattern should reject invalid speeds")
    void testSquarePatternInvalidSpeed() {
        // Test too slow
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            patterns.square(50, 5); // Too slow
        });
        assertTrue(exception1.getMessage().contains("Speed must be between 10 and 100 percent"));
        
        // Test too fast
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            patterns.square(50, 150); // Too fast
        });
        assertTrue(exception2.getMessage().contains("Speed must be between 10 and 100 percent"));
    }
    
    @Test
    @DisplayName("Triangle pattern should make 3 forward movements and 3 turns of 120 degrees")
    void testTrianglePattern() {
        // Given: valid parameters
        int sideLength = 60;
        int speed = 40;
        
        // When: flying a triangle pattern
        patterns.triangle(sideLength, speed);
        
        // Then: verify 3 forward movements and 3 turns of 120 degrees
        verify(mockDrone, times(3)).go("forward", sideLength, speed);
        verify(mockDrone, times(3)).go("cw", 120, 30);
        verify(mockDrone, times(3)).hover(0.5);
    }
    
    @Test
    @DisplayName("Line back and forth should alternate directions correctly")
    void testLineBackAndForth() {
        // Given: valid parameters
        int distance = 100;
        int cycles = 2;
        int speed = 60;
        
        // When: flying back and forth
        patterns.lineBackAndForth(distance, cycles, speed);
        
        // Then: verify correct sequence of movements
        // Each cycle should have: forward, turn 180, forward, turn 180 (except last cycle)
        verify(mockDrone, times(4)).go("forward", distance, speed); // 2 per cycle × 2 cycles
        verify(mockDrone, times(3)).go("cw", 180, 30); // 2 per cycle except last turn
        
        // Verify hovering at appropriate times
        verify(mockDrone, times(4)).hover(1.0); // At each end
        verify(mockDrone, times(3)).hover(0.5); // After each turn
    }
    
    @Test
    @DisplayName("Stairs pattern should alternate up/forward and backward/down movements")
    void testStairsPattern() {
        // Given: valid parameters
        int stepHeight = 30;
        int numberOfSteps = 3;
        int speed = 50;
        
        // When: flying stairs pattern
        patterns.stairs(stepHeight, numberOfSteps, speed);
        
        // Then: verify going up stairs
        verify(mockDrone, times(numberOfSteps)).go("up", stepHeight, speed);
        verify(mockDrone, times(numberOfSteps)).go("forward", 30, speed);
        
        // Then: verify coming down stairs
        verify(mockDrone, times(numberOfSteps)).go("backward", 30, speed);
        verify(mockDrone, times(numberOfSteps)).go("down", stepHeight, speed);
        
        // Verify appropriate pauses
        verify(mockDrone, times(2 * numberOfSteps)).hover(0.5); // At each step
        verify(mockDrone, times(1)).hover(2.0); // At the top
    }
    
    @Test
    @DisplayName("Constructor should accept drone dependency")
    void testConstructor() {
        // When: creating a new BasicPatterns instance
        BasicPatterns newPatterns = new BasicPatterns(mockDrone);
        
        // Then: it should be created successfully
        assertNotNull(newPatterns);
    }
    
    @Test
    @DisplayName("Constructor should handle null drone gracefully")
    void testConstructorWithNullDrone() {
        // When/Then: creating with null should not immediately fail
        // (The failure would occur when trying to use the patterns)
        assertDoesNotThrow(() -> {
            new BasicPatterns(null);
        });
    }
}
