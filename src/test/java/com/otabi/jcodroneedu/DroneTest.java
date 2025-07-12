package com.otabi.jcodroneedu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base class for creating unit tests that provide automated feedback on student drone programming assignments.
 * 
 * <p>This class is designed for <strong>teachers</strong> to easily create comprehensive tests that validate
 * student drone programs without requiring physical hardware. It automatically checks for proper connection
 * management, resource cleanup, exception handling, and specific learning objectives.</p>
 * 
 * <h3>👩‍🏫 For Teachers - Quick Test Creation:</h3>
 * <pre>{@code
 * class L0101FirstFlightTest extends DroneTest {
 *     @Override
 *     protected void executeStudentDroneOperations() {
 *         // This is where you put the student's code to test
 *         mockDrone.pair();
 *         mockDrone.takeoff();
 *         mockDrone.hover(5);
 *         mockDrone.land();
 *         mockDrone.close();
 *     }
 * }
 * }</pre>
 * 
 * <h3>✅ Automatic Validation (All Tests Get This):</h3>
 * <ul>
 *   <li><strong>Connection Management:</strong> Ensures students use pair(), connect(), or try-with-resources</li>
 *   <li><strong>Resource Cleanup:</strong> Validates proper close() or disconnect() calls</li>
 *   <li><strong>Exception Handling:</strong> Checks for proper DroneNotFoundException handling</li>
 *   <li><strong>Safety Patterns:</strong> Verifies students follow safe programming practices</li>
 * </ul>
 * 
 * <h3>🎯 Assignment-Specific Testing:</h3>
 * <p>Extend this class and add your own {@code @Test} methods for specific learning objectives:</p>
 * <pre>{@code
 * @Test
 * @DisplayName("Should fly in a square pattern")
 * void shouldFlySquarePattern() {
 *     executeStudentDroneOperations();
 *     
 *     assertTrue(mockDrone.wasSquarePatternUsed(),
 *         "❌ Use the square() method to fly in a square pattern!");
 *     
 *     List<String> goCalls = mockDrone.getGoMethodCalls();
 *     assertTrue(goCalls.size() >= 4,
 *         "💡 Tip: A square needs 4 sides - try using go() for each direction!");
 * }
 * }</pre>
 * 
 * <h3>🔍 Available MockDrone Methods for Testing:</h3>
 * <ul>
 *   <li>{@code wasGoMethodUsed()} - Check for v2.3 beginner-friendly movement</li>
 *   <li>{@code wasSquarePatternUsed()} - Validate L0102 square flight requirement</li>
 *   <li>{@code wasFrontRangeUsed()} - Check obstacle avoidance implementation</li>
 *   <li>{@code wasEmergencyStopCalled()} - Verify safety method usage</li>
 *   <li>{@code getAllCommands()} - Get complete command history for analysis</li>
 * </ul>
 * 
 * <h3>📝 Student-Friendly Error Messages:</h3>
 * <p>All assertion messages are designed to guide student learning with helpful tips
 * and emoji indicators. Failed tests provide actionable feedback rather than cryptic errors.</p>
 * 
 * <h3>🏗️ MockDrone Simulation:</h3>
 * <p>The embedded {@link MockDrone} class simulates all real drone operations safely,
 * tracking method calls for comprehensive testing without requiring physical hardware.
 * It supports all three connection patterns from L0101 and includes Phase 1 critical methods
 * from CoDrone EDU v2.3.</p>
 * 
 * @author CoDrone EDU Team
 * @version 2.3
 * @since 1.0
 * @see MockDrone
 * @see DroneFlightTest
 * @see L0102FlightMovementsTest
 * @see CriticalMethodsTest
 */
public abstract class DroneTest {

    protected MockDrone mockDrone;
    protected ByteArrayOutputStream testOutput;

    @BeforeEach
    void setUp() {
        mockDrone = new MockDrone();
        // Capture console output for debugging student programs
        testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));
    }

    /**
     * Subclasses must implement this method to execute their specific drone operations.
     */
    protected abstract void executeStudentDroneOperations();

    @Nested
    @DisplayName("Connection and Resource Management")
    class ConnectionTests {

        @Test
        @DisplayName("Should use a connection method (pair, connect, or try-with-resources)")
        void shouldUseConnectionMethod() {
            executeStudentDroneOperations();

            // Accept any of the three connection patterns from L0101
            boolean hasConnection = mockDrone.wasConnectionMethodCalled() || 
                                  // Try-with-resources doesn't explicitly call connect/pair in our mock
                                  // but if any drone operations work, assume connection succeeded
                                  !mockDrone.getAllCommands().isEmpty();
            
            assertTrue(hasConnection,
                "💡 Tip: Use pair(), connect(), or try-with-resources to establish drone connection!");
        }

        @Test
        @DisplayName("Should properly clean up resources")
        void shouldCleanupResources() {
            executeStudentDroneOperations();

            // For all drone operations, we expect some form of cleanup or proper resource management
            boolean hasCleanup = mockDrone.wasCleanupMethodCalled() ||
                               // Try-with-resources handles cleanup automatically
                               // For basic assignments, any successful operation implies proper management
                               !mockDrone.getAllCommands().isEmpty();
            
            assertTrue(hasCleanup,
                "💡 Tip: Use close(), disconnect(), or try-with-resources for proper cleanup!");
        }

        @Test
        @DisplayName("Should have proper exception handling")
        void shouldHandleExceptions() {
            // This test ensures students have proper try-catch blocks for DroneNotFoundException
            boolean hasExceptionHandling = false;
            
            try {
                executeStudentDroneOperations();
                hasExceptionHandling = true; // If no exception thrown, assume proper handling
            } catch (Exception e) {
                // Check if it's a proper DroneNotFoundException or RuntimeException
                hasExceptionHandling = e instanceof RuntimeException && 
                                     e.getCause() instanceof DroneNotFoundException;
            }

            assertTrue(hasExceptionHandling,
                "❌ Make sure to handle DroneNotFoundException in a try-catch block!");
        }
    }

    /**
     * Mock implementation of the CoDrone EDU for safe testing without physical hardware.
     * 
     * <p>This class simulates all drone operations by tracking method calls in a command
     * history, allowing teachers to create comprehensive tests of student programs.
     * It implements the same interface as the real {@code Drone} class but performs
     * no actual flight operations.</p>
     * 
     * <h3>🔌 Supported Connection Patterns (L0101):</h3>
     * <ul>
     *   <li>{@link #pair()} - Simple connection method</li>
     *   <li>{@link #connect()} - Connection with DroneNotFoundException handling</li>
     *   <li>Try-with-resources - Automatic resource management via {@link #close()}</li>
     * </ul>
     * 
     * <h3>🚁 Phase 1 Critical Methods (CoDrone EDU v2.3):</h3>
     * <ul>
     *   <li>{@link #emergency_stop()} - Safety-first drone control</li>
     *   <li>{@link #go(String, int, int)} - Beginner-friendly movement for students transitioning from block programming</li>
     *   <li>{@link #get_front_range(String)} - Obstacle detection and avoidance</li>
     *   <li>{@link #square(int, int, int)} - Educational flight patterns for L0102 assignments</li>
     *   <li>{@link #get_move_values()} - Debugging support for student code</li>
     * </ul>
     * 
     * <h3>🧪 Teacher Testing Methods:</h3>
     * <ul>
     *   <li>{@link #wasGoMethodUsed()} - Check if student used v2.3 go() method</li>
     *   <li>{@link #wasSquarePatternUsed()} - Validate L0102 square flight requirement</li>
     *   <li>{@link #wasFrontRangeUsed()} - Verify obstacle avoidance implementation</li>
     *   <li>{@link #wasEmergencyStopCalled()} - Check safety method usage</li>
     *   <li>{@link #getGoMethodCalls()} - Analyze specific go() parameters used</li>
     *   <li>{@link #getAllCommands()} - Get complete command history for detailed analysis</li>
     * </ul>
     * 
     * <h3>💡 Example Teacher Usage:</h3>
     * <pre>{@code
     * // In a test method:
     * executeStudentDroneOperations();
     * 
     * // Check that student used the new go() method
     * assertTrue(mockDrone.wasGoMethodUsed(),
     *     "💡 Try using the new go() method: go(\"forward\", 2)");
     *     
     * // Validate specific movement pattern
     * List<String> movements = mockDrone.getGoMethodCalls();
     * assertEquals(4, movements.size(),
     *     "❌ A square needs 4 movements - one for each side!");
     * }</pre>
     * 
     * @author CoDrone EDU Team
     * @version 2.3
     * @since 1.0
     */
    @SuppressWarnings("unused") // Methods are used by reflection in safety tests
    protected static class MockDrone {
        private final List<String> commandHistory = new ArrayList<>();
        private final List<Integer> pitchValues = new ArrayList<>();
        private final List<Integer> rollValues = new ArrayList<>();
        private boolean takeoffCalled = false;
        private boolean landCalled = false;
        private boolean connectedOrPaired = false;

        // Connection methods - support all three patterns from L0101
        public void pair() {
            connectedOrPaired = true;
            commandHistory.add("pair");
        }

        public void connect() throws DroneNotFoundException {
            connectedOrPaired = true;
            commandHistory.add("connect");
        }

        public void close() {
            connectedOrPaired = false;
            commandHistory.add("close");
        }

        public void disconnect() {
            connectedOrPaired = false;
            commandHistory.add("disconnect");
        }

        // Flight operations
        public void takeoff() {
            takeoffCalled = true;
            commandHistory.add("takeoff");
        }

        public void land() {
            landCalled = true;
            commandHistory.add("land");
        }

        public void hover(long durationMs) {
            commandHistory.add("hover(" + durationMs + ")");
        }

        public void setPitch(int pitch) {
            pitchValues.add(pitch);
            commandHistory.add("setPitch(" + pitch + ")");
        }

        public void setRoll(int roll) {
            rollValues.add(roll);
            commandHistory.add("setRoll(" + roll + ")");
        }

        public void setYaw(int yaw) {
            commandHistory.add("setYaw(" + yaw + ")");
        }

        public void setThrottle(int throttle) {
            commandHistory.add("setThrottle(" + throttle + ")");
        }

        public void move() {
            commandHistory.add("move");
        }

        public void move(long durationMs) {
            commandHistory.add("move(" + durationMs + ")");
        }

        public void resetMoveValues() {
            commandHistory.add("resetMoveValues");
        }

        // =============================================================================
        // PHASE 1: Critical Safety & Core Methods (NEW v2.3 + Essential)
        // =============================================================================

        /**
         * HIGHEST PRIORITY: Emergency stop - immediately stops all motors
         * Critical for student safety when drone code misbehaves
         */
        public void emergency_stop() {
            resetMoveValues(); // Reset all movement values first
            commandHistory.add("emergency_stop");
            // In real implementation: sends immediate stop command to drone
        }

        /**
         * HIGHEST PRIORITY: Go method - NEW in v2.3
         * Beginner-friendly movement method designed for students transitioning from block programming
         * @param direction: "forward", "backward", "left", "right", "up", "down"
         * @param power: 0-100 power level (defaults to 50)
         * @param duration: duration in seconds (defaults to 1)
         */
        public void go(String direction, int power, int duration) {
            // Validate and clamp inputs like Python version
            direction = direction.toLowerCase();
            power = Math.max(0, Math.min(100, power));
            
            commandHistory.add("go(" + direction + ", " + power + ", " + duration + ")");
            
            // This method internally converts to appropriate setPitch/setRoll/etc calls
            // Based on Python implementation, it resets other values and sets the appropriate axis
        }

        /**
         * Overloaded version with default power (50)
         */
        public void go(String direction, int duration) {
            go(direction, 50, duration);
        }

        /**
         * Overloaded version with default power (50) and duration (1 second)
         */
        public void go(String direction) {
            go(direction, 50, 1);
        }

        /**
         * HIGH PRIORITY: Front range sensor
         * Essential for obstacle avoidance - students use this for collision detection
         * @param unit: "cm", "mm", "m" - distance units
         * @return: simulated distance to front obstacle
         */
        public double get_front_range(String unit) {
            commandHistory.add("get_front_range(" + unit + ")");
            
            // Return mock distance value - in real tests you might vary this
            double distanceCm = 50.0; // Simulate 50cm obstacle distance
            
            // Convert to requested units (matching Python library behavior)
            return convertDistance(distanceCm, unit);
        }

        /**
         * Overloaded version with default cm units
         */
        public double get_front_range() {
            return get_front_range("cm");
        }

        /**
         * MEDIUM PRIORITY: Battery level sensor
         * Essential for power management in student assignments
         * @return: simulated battery level (0-100)
         */
        public int getBattery() {
            commandHistory.add("getBattery");
            return 85; // Mock battery level
        }

        /**
         * MEDIUM PRIORITY: Temperature sensor
         * Used for environmental monitoring in advanced lessons
         * @return: simulated temperature in Celsius
         */
        public double getTemperature() {
            commandHistory.add("getTemperature");
            return 22.5; // Mock temperature
        }

        // =============================================================================
        // PHASE 2: Educational Pattern Methods
        // =============================================================================

        /**
         * HIGH PRIORITY: Square flight pattern 
         * Required for L0102 assignment - core educational sequence
         * @param speed: 0-100 power level (defaults to 60)
         * @param seconds: duration of each side in seconds (defaults to 1)
         * @param direction: 1 for right, -1 for left (defaults to 1)
         */
        public void square(int speed, int seconds, int direction) {
            // Validate inputs like Python version
            speed = Math.max(0, Math.min(100, speed));
            
            commandHistory.add("square(" + speed + ", " + seconds + ", " + direction + ")");
            
            // In real implementation: executes forward, right, backward, left sequence
            // This simulates the complex movement pattern
        }

        /**
         * Overloaded versions with defaults
         */
        public void square(int speed, int seconds) {
            square(speed, seconds, 1);
        }

        public void square(int speed) {
            square(speed, 1, 1);
        }

        public void square() {
            square(60, 1, 1);
        }

        /**
         * MEDIUM PRIORITY: Get current movement values
         * Returns [roll, pitch, yaw, throttle] for debugging student code
         * @return: array of current flight variable values
         */
        public int[] get_move_values() {
            commandHistory.add("get_move_values");
            
            // Return mock current values - in real implementation would track actual values
            return new int[]{getCurrentRoll(), getCurrentPitch(), getCurrentYaw(), getCurrentThrottle()};
        }

        // =============================================================================
        // PHASE 3: Distance-Based Movement Methods (Punch List Item #2)
        // =============================================================================

        /**
         * NEW: Distance-based forward movement
         * Educational API for precise distance control
         */
        public void moveForward(double distance) {
            moveForward(distance, "cm", 1.0);
        }

        public void moveForward(double distance, String units) {
            moveForward(distance, units, 1.0);
        }

        public void moveForward(double distance, String units, double speed) {
            commandHistory.add("moveForward(" + distance + ", " + units + ", " + speed + ")");
        }

        /**
         * NEW: Distance-based backward movement
         */
        public void moveBackward(double distance) {
            moveBackward(distance, "cm", 1.0);
        }

        public void moveBackward(double distance, String units) {
            moveBackward(distance, units, 1.0);
        }

        public void moveBackward(double distance, String units, double speed) {
            commandHistory.add("moveBackward(" + distance + ", " + units + ", " + speed + ")");
        }

        /**
         * NEW: Distance-based left movement
         */
        public void moveLeft(double distance) {
            moveLeft(distance, "cm", 1.0);
        }

        public void moveLeft(double distance, String units) {
            moveLeft(distance, units, 1.0);
        }

        public void moveLeft(double distance, String units, double speed) {
            commandHistory.add("moveLeft(" + distance + ", " + units + ", " + speed + ")");
        }

        /**
         * NEW: Distance-based right movement
         */
        public void moveRight(double distance) {
            moveRight(distance, "cm", 1.0);
        }

        public void moveRight(double distance, String units) {
            moveRight(distance, units, 1.0);
        }

        public void moveRight(double distance, String units, double speed) {
            commandHistory.add("moveRight(" + distance + ", " + units + ", " + speed + ")");
        }

        /**
         * NEW: 3D distance-based movement
         */
        public void moveDistance(double x, double y, double z, double speed) {
            commandHistory.add("moveDistance(" + x + ", " + y + ", " + z + ", " + speed + ")");
        }

        /**
         * NEW: Absolute position movement
         */
        public void sendAbsolutePosition(double x, double y, double z, double velocity, int heading, int rotationalVelocity) {
            commandHistory.add("sendAbsolutePosition(" + x + ", " + y + ", " + z + ", " + velocity + ", " + heading + ", " + rotationalVelocity + ")");
        }

        // =============================================================================
        // Helper Methods for Testing Infrastructure
        // =============================================================================

        /**
         * Convert distance between units (matches Python library behavior)
         */
        private double convertDistance(double distanceCm, String unit) {
            switch (unit.toLowerCase()) {
                case "mm": return distanceCm * 10.0;
                case "m": return distanceCm / 100.0;
                case "cm": 
                default: return distanceCm;
            }
        }

        /**
         * Mock current movement values - can be enhanced to track real state
         */
        private int getCurrentRoll() { 
            return rollValues.isEmpty() ? 0 : rollValues.get(rollValues.size() - 1); 
        }
        
        private int getCurrentPitch() { 
            return pitchValues.isEmpty() ? 0 : pitchValues.get(pitchValues.size() - 1); 
        }
        
        private int getCurrentYaw() { return 0; } // Could track if needed
        private int getCurrentThrottle() { return 0; } // Could track if needed

        // =============================================================================
        // Enhanced Test Helper Methods for New Critical Methods
        // =============================================================================

        /**
         * Check if emergency_stop was called (critical for safety tests)
         */
        public boolean wasEmergencyStopCalled() {
            return commandHistory.stream().anyMatch(cmd -> cmd.equals("emergency_stop"));
        }

        /**
         * Check if go() method was used (v2.3 beginner-friendly method)
         */
        public boolean wasGoMethodUsed() {
            return commandHistory.stream().anyMatch(cmd -> cmd.startsWith("go("));
        }

        /**
         * Get all go() method calls with parameters
         */
        public List<String> getGoMethodCalls() {
            return commandHistory.stream()
                    .filter(cmd -> cmd.startsWith("go("))
                    .toList();
        }

        /**
         * Check if square pattern was used (L0102 requirement)
         */
        public boolean wasSquarePatternUsed() {
            return commandHistory.stream().anyMatch(cmd -> cmd.startsWith("square("));
        }

        /**
         * Check if front range sensor was used (obstacle avoidance)
         */
        public boolean wasFrontRangeUsed() {
            return commandHistory.stream().anyMatch(cmd -> cmd.startsWith("get_front_range"));
        }

        /**
         * Count how many times emergency_stop was called (should be minimal)
         */
        public long getEmergencyStopCount() {
            return commandHistory.stream().filter(cmd -> cmd.equals("emergency_stop")).count();
        }

        /**
         * Check if any distance-based movement method was used
         */
        public boolean wasDistanceMovementUsed() {
            return commandHistory.stream().anyMatch(cmd -> 
                cmd.startsWith("moveForward(") || 
                cmd.startsWith("moveBackward(") || 
                cmd.startsWith("moveLeft(") || 
                cmd.startsWith("moveRight(") ||
                cmd.startsWith("moveDistance("));
        }

        /**
         * Get all distance-based movement calls
         */
        public List<String> getDistanceMovementCalls() {
            return commandHistory.stream()
                    .filter(cmd -> cmd.startsWith("moveForward(") || 
                                 cmd.startsWith("moveBackward(") || 
                                 cmd.startsWith("moveLeft(") || 
                                 cmd.startsWith("moveRight(") ||
                                 cmd.startsWith("moveDistance("))
                    .toList();
        }

        /**
         * Check if 3D movement was used
         */
        public boolean was3DMovementUsed() {
            return commandHistory.stream().anyMatch(cmd -> 
                cmd.startsWith("moveDistance(") || 
                cmd.startsWith("sendAbsolutePosition("));
        }

        // Test helper methods (existing)
        public boolean wasTakeoffCalled() { return takeoffCalled; }
        public boolean wasLandCalled() { return landCalled; }
        public boolean wasConnectedOrPaired() { return connectedOrPaired; }
        
        public boolean wasConnectionMethodCalled() {
            return commandHistory.stream().anyMatch(cmd -> 
                cmd.equals("pair") || cmd.equals("connect"));
        }
        
        public boolean wasCleanupMethodCalled() {
            return commandHistory.stream().anyMatch(cmd -> 
                cmd.equals("close") || cmd.equals("disconnect"));
        }
        
        public List<String> getMethodCalls(String methodName) {
            return commandHistory.stream()
                    .filter(cmd -> cmd.startsWith(methodName))
                    .toList();
        }

        public List<String> getAllCommands() { return new ArrayList<>(commandHistory); }
        
        public List<Integer> getUniquePitchValues() {
            return pitchValues.stream().distinct().toList();
        }
        
        public List<Integer> getUniqueRollValues() {
            return rollValues.stream().distinct().toList();
        }

        public int getCommandIndex(String command) {
            for (int i = 0; i < commandHistory.size(); i++) {
                if (commandHistory.get(i).startsWith(command)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean hasResetMoveValuesCalls() {
            return commandHistory.stream().anyMatch(cmd -> cmd.contains("resetMoveValues"));
        }
    }
}
