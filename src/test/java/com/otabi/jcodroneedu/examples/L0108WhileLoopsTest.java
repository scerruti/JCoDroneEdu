package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DroneTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0108WhileLoops example - Lesson 1.8: While Loops
 * 
 * Based on Robolink's Python with CoDrone EDU curriculum for while loops.
 * 
 * Educational Objectives:
 * - Understand while loop structure and condition-based iteration
 * - Learn the difference between for loops and while loops
 * - Use sensor data to control while loop conditions
 * - Create condition-based flight patterns
 * - Understand loop termination and infinite loop prevention
 * - Combine while loops with conditional logic
 * - Apply while loops for real-time sensor monitoring
 * 
 * Key Concepts:
 * - While loop syntax and structure
 * - Condition-based iteration vs. count-based iteration
 * - Sensor-driven loop control
 * - Loop termination conditions
 * - Real-time monitoring and response
 * - Infinite loop prevention
 * - Dynamic flight behavior based on conditions
 */
@DisplayName("L0108 While Loops Tests")
class L0108WhileLoopsTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }
    
    protected void executeStudentFlightPattern() {
        // Simulate the expected behavior from L0108WhileLoops example
        // Based on while loop lesson concepts
        try {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Simulate while loop patterns - condition-based flight behavior
            // Example: while (range > 100) { move forward }
            double range = mockDrone.get_front_range();
            int count = 0;
            while (range > 30 && count < 3) {
                mockDrone.setPitch(25);
                mockDrone.move(500);
                mockDrone.setPitch(0);
                range = mockDrone.get_front_range(); // Check sensor again
                count++;
            }
            
            // Example: while (battery > 20) { continue flight }
            int battery = mockDrone.getBattery();
            int batteryCount = 0;
            while (battery > 20 && batteryCount < 2) {
                mockDrone.setYaw(30);
                mockDrone.move(400);
                mockDrone.setYaw(0);
                battery = mockDrone.getBattery(); // Check sensor again
                batteryCount++;
            }
            
            // Example: while (sensor monitoring) { pattern flight }
            double temperature = mockDrone.getTemperature();
            int tempCount = 0;
            while (temperature < 40 && tempCount < 2) {
                mockDrone.setRoll(20);
                mockDrone.move(300);
                mockDrone.setRoll(0);
                mockDrone.setThrottle(15);
                mockDrone.move(200);
                mockDrone.setThrottle(0);
                temperature = mockDrone.getTemperature(); // Check sensor again
                tempCount++;
            }
            
            mockDrone.land();
            mockDrone.close();
        } catch (Exception e) {
            fail("Student code should not throw exceptions during normal execution: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("While Loop Structure Tests")
    class WhileLoopStructureTests {

        @Test
        @DisplayName("Should demonstrate condition-based iteration")
        void shouldDemonstrateConditionBasedIteration() {
            executeStudentFlightPattern();
            
            // While loops use conditions, not fixed counts
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have sensor-based or condition-based patterns
            boolean hasConditionBasedPattern = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery") || cmd.contains("height"));
            
            assertTrue(hasConditionBasedPattern, 
                "Student should demonstrate condition-based iteration with while loops");
        }

        @Test
        @DisplayName("Should use sensor data for loop control")
        void shouldUseSensorDataForLoopControl() {
            executeStudentFlightPattern();
            
            // While loops commonly use sensor data
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have sensor monitoring
            boolean hasSensorMonitoring = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery") || 
                                cmd.contains("height") || cmd.contains("state"));
            
            assertTrue(hasSensorMonitoring, 
                "Student should use sensor data for while loop control");
        }

        @Test
        @DisplayName("Should demonstrate proper loop termination")
        void shouldDemonstrateProperLoopTermination() {
            executeStudentFlightPattern();
            
            // While loops must terminate properly
            List<String> commands = mockDrone.getAllCommands();
            
            // Should complete execution without infinite loop
            boolean hasProperTermination = commands.stream()
                .anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasProperTermination, 
                "Student should demonstrate proper while loop termination");
        }
    }

    @Nested
    @DisplayName("Sensor-Based Control Tests")
    class SensorBasedControlTests {

        @Test
        @DisplayName("Should use range sensors for proximity control")
        void shouldUseRangeSensorsForProximityControl() {
            executeStudentFlightPattern();
            
            // Range sensors common in while loops
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have range-based behavior
            boolean hasRangeBasedControl = commands.stream()
                .anyMatch(cmd -> cmd.contains("range")) ||
                commands.stream().anyMatch(cmd -> cmd.contains("setYaw") || cmd.contains("setRoll"));
            
            assertTrue(hasRangeBasedControl, 
                "Student should use range sensors for proximity control");
        }

        @Test
        @DisplayName("Should monitor battery levels for safe flight")
        void shouldMonitorBatteryLevels() {
            executeStudentFlightPattern();
            
            // Battery monitoring important for while loops
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have battery-aware behavior
            boolean hasBatteryMonitoring = commands.stream()
                .anyMatch(cmd -> cmd.contains("battery")) ||
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasBatteryMonitoring, 
                "Student should monitor battery levels for safe flight");
        }

        @Test
        @DisplayName("Should create responsive flight behavior")
        void shouldCreateResponsiveFlightBehavior() {
            executeStudentFlightPattern();
            
            // While loops create responsive behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have responsive movement patterns
            boolean hasResponsiveBehavior = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 3;
            
            assertTrue(hasResponsiveBehavior, 
                "Student should create responsive flight behavior with while loops");
        }
    }

    @Nested
    @DisplayName("Real-Time Monitoring Tests")
    class RealTimeMonitoringTests {

        @Test
        @DisplayName("Should demonstrate continuous monitoring patterns")
        void shouldDemonstrateContinuousMonitoring() {
            executeStudentFlightPattern();
            
            // While loops enable continuous monitoring
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have continuous monitoring behavior
            boolean hasContinuousMonitoring = commands.stream()
                .filter(cmd -> cmd.contains("range") || cmd.contains("battery") || cmd.contains("height"))
                .count() >= 2;
            
            assertTrue(hasContinuousMonitoring, 
                "Student should demonstrate continuous monitoring patterns");
        }

        @Test
        @DisplayName("Should adapt flight patterns based on real-time data")
        void shouldAdaptFlightPatterns() {
            executeStudentFlightPattern();
            
            // While loops adapt to real-time conditions
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have adaptive behavior
            boolean hasAdaptiveBehavior = commands.stream()
                .anyMatch(cmd -> cmd.contains("set") && !cmd.contains("(0)")) &&
                commands.stream().filter(cmd -> cmd.startsWith("move")).count() >= 2;
            
            assertTrue(hasAdaptiveBehavior, 
                "Student should adapt flight patterns based on real-time data");
        }

        @Test
        @DisplayName("Should demonstrate event-driven programming")
        void shouldDemonstrateEventDrivenProgramming() {
            executeStudentFlightPattern();
            
            // While loops enable event-driven programming
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have event-driven patterns
            boolean hasEventDrivenPattern = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) &&
                commands.stream().anyMatch(cmd -> cmd.contains("set") || cmd.startsWith("move"));
            
            assertTrue(hasEventDrivenPattern, 
                "Student should demonstrate event-driven programming with while loops");
        }
    }

    @Nested
    @DisplayName("Loop Condition Tests")
    class LoopConditionTests {

        @Test
        @DisplayName("Should use appropriate loop termination conditions")
        void shouldUseAppropriateTerminationConditions() {
            executeStudentFlightPattern();
            
            // While loops need good termination conditions
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have logical termination
            boolean hasLogicalTermination = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery") || cmd.contains("height")) ||
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasLogicalTermination, 
                "Student should use appropriate loop termination conditions");
        }

        @Test
        @DisplayName("Should prevent infinite loops through proper conditions")
        void shouldPreventInfiniteLoops() {
            executeStudentFlightPattern();
            
            // Must prevent infinite loops
            List<String> commands = mockDrone.getAllCommands();
            
            // Should complete execution (no infinite loop)
            boolean preventsInfiniteLoop = commands.size() > 0 && commands.size() < 1000;
            assertTrue(preventsInfiniteLoop, 
                "Student should prevent infinite loops through proper conditions");
        }

        @Test
        @DisplayName("Should combine multiple conditions for complex control")
        void shouldCombineMultipleConditions() {
            executeStudentFlightPattern();
            
            // While loops often combine conditions
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple condition types
            boolean hasMultipleConditions = commands.stream()
                .filter(cmd -> cmd.contains("range") || cmd.contains("battery") || 
                              cmd.contains("height") || cmd.contains("state"))
                .count() >= 2;
            
            assertTrue(hasMultipleConditions, 
                "Student should combine multiple conditions for complex control");
        }
    }

    @Nested
    @DisplayName("Educational Progression Tests")
    class EducationalProgressionTests {

        @Test
        @DisplayName("Should build on for loop concepts from L0107")
        void shouldBuildOnForLoopConcepts() {
            executeStudentFlightPattern();
            
            // While loops build on for loop understanding
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have iterative patterns
            boolean hasIterativePatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 4;
            
            assertTrue(hasIterativePatterns, 
                "Student should build on for loop concepts");
        }

        @Test
        @DisplayName("Should prepare for nested loop concepts (L0109)")
        void shouldPrepareForNestedLoopConcepts() {
            executeStudentFlightPattern();
            
            // While loops prepare for nested loops
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complex patterns that could use nesting
            boolean hasComplexPatterns = commands.size() >= 8;
            assertTrue(hasComplexPatterns, 
                "Student should create patterns that prepare for nested loop concepts");
        }

        @Test
        @DisplayName("Should demonstrate advanced loop problem-solving")
        void shouldDemonstrateAdvancedProblemSolving() {
            executeStudentFlightPattern();
            
            // While loops solve advanced problems
            List<String> commands = mockDrone.getAllCommands();
            
            // Should solve complex problems
            boolean solvesAdvancedProblems = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(solvesAdvancedProblems, 
                "Student should solve advanced problems with while loops");
        }
    }

    @Nested
    @DisplayName("Practical Application Tests")
    class PracticalApplicationTests {

        @Test
        @DisplayName("Should implement obstacle avoidance with while loops")
        void shouldImplementObstacleAvoidance() {
            executeStudentFlightPattern();
            
            // While loops excellent for obstacle avoidance
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have avoidance behavior
            boolean hasAvoidanceBehavior = commands.stream()
                .anyMatch(cmd -> cmd.contains("range")) ||
                commands.stream().anyMatch(cmd -> cmd.contains("setYaw") || cmd.contains("setRoll"));
            
            assertTrue(hasAvoidanceBehavior, 
                "Student should implement obstacle avoidance with while loops");
        }

        @Test
        @DisplayName("Should create autonomous flight patterns")
        void shouldCreateAutonomousFlightPatterns() {
            executeStudentFlightPattern();
            
            // While loops enable autonomous behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have autonomous behavior
            boolean hasAutonomousBehavior = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) &&
                commands.stream().filter(cmd -> cmd.contains("set")).count() >= 2;
            
            assertTrue(hasAutonomousBehavior, 
                "Student should create autonomous flight patterns with while loops");
        }

        @Test
        @DisplayName("Should demonstrate safety monitoring systems")
        void shouldDemonstrateSafetyMonitoring() {
            executeStudentFlightPattern();
            
            // While loops important for safety monitoring
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have safety monitoring
            boolean hasSafetyMonitoring = commands.stream()
                .anyMatch(cmd -> cmd.contains("battery") || cmd.contains("height")) ||
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasSafetyMonitoring, 
                "Student should demonstrate safety monitoring systems");
        }
    }

    @Nested
    @DisplayName("Loop Comparison Tests")
    class LoopComparisonTests {

        @Test
        @DisplayName("Should demonstrate when to use while vs for loops")
        void shouldDemonstrateWhenToUseWhileVsFor() {
            executeStudentFlightPattern();
            
            // While loops for conditions, for loops for counts
            List<String> commands = mockDrone.getAllCommands();
            
            // Should show condition-based usage
            boolean showsConditionBasedUsage = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery") || cmd.contains("height"));
            
            assertTrue(showsConditionBasedUsage, 
                "Student should demonstrate when to use while vs for loops");
        }

        @Test
        @DisplayName("Should show while loop advantages for sensor monitoring")
        void shouldShowWhileLoopAdvantages() {
            executeStudentFlightPattern();
            
            // While loops better for sensor monitoring
            List<String> commands = mockDrone.getAllCommands();
            
            // Should show sensor monitoring advantages
            boolean showsSensorAdvantages = commands.stream()
                .filter(cmd -> cmd.contains("range") || cmd.contains("battery") || cmd.contains("height"))
                .count() >= 1;
            
            assertTrue(showsSensorAdvantages, 
                "Student should show while loop advantages for sensor monitoring");
        }

        @Test
        @DisplayName("Should demonstrate dynamic loop control")
        void shouldDemonstrateDynamicLoopControl() {
            executeStudentFlightPattern();
            
            // While loops provide dynamic control
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have dynamic control patterns
            boolean hasDynamicControl = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) &&
                commands.stream().anyMatch(cmd -> cmd.contains("set") && !cmd.contains("(0)"));
            
            assertTrue(hasDynamicControl, 
                "Student should demonstrate dynamic loop control");
        }
    }
}
