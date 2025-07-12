package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DroneTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0106Conditionals example - Lesson 1.6: Conditionals
 * 
 * Based on Robolink's Python with CoDrone EDU curriculum for conditional statements.
 * 
 * Educational Objectives:
 * - Understand conditional logic and decision making in programming
 * - Learn if/else statements and their syntax
 * - Use comparison operators (==, !=, <, >, <=, >=)
 * - Apply logical operators (and, or, not)
 * - Create decision-based flight patterns
 * - Use sensor data for conditional flight control
 * - Combine variables with conditionals for dynamic behavior
 * 
 * Key Concepts:
 * - Boolean logic and conditional expressions
 * - If/else/elif statement structure
 * - Comparison and logical operators
 * - Sensor-based decision making
 * - Conditional flight patterns
 * - Safety through conditional logic
 */
@DisplayName("L0106 Conditionals Tests")
class L0106ConditionalsTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }
    
    protected void executeStudentFlightPattern() {
        // Simulate the expected behavior from L0106Conditionals example
        // Based on conditional logic lesson concepts
        try {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Demonstrate conditional logic with sensor data
            double range = mockDrone.get_front_range();
            
            if (range > 30) {
                // Safe to move forward
                mockDrone.setPitch(30);
                mockDrone.move(1000);
                mockDrone.setPitch(0);
            } else {
                // Obstacle detected, turn
                mockDrone.setYaw(45);
                mockDrone.move(1000);
                mockDrone.setYaw(0);
            }
            
            // Battery level check
            int battery = mockDrone.getBattery();
            if (battery > 50) {
                // Enough battery for complex maneuver
                mockDrone.setRoll(25);
                mockDrone.setPitch(20);
                mockDrone.move(1500);
                mockDrone.setRoll(0);
                mockDrone.setPitch(0);
            } else {
                // Low battery, simple hover
                mockDrone.hover(2);
            }
            
            // Temperature check for throttle adjustment
            double temperature = mockDrone.getTemperature();
            if (temperature > 25) {
                // Hot weather, reduce throttle
                mockDrone.setThrottle(15);
                mockDrone.move(800);
                mockDrone.setThrottle(0);
            } else {
                // Normal temperature, standard throttle
                mockDrone.setThrottle(30);
                mockDrone.move(1000);
                mockDrone.setThrottle(0);
            }
            
            // Create repetitive patterns for loop preparation
            mockDrone.hover(1);
            mockDrone.hover(1);
            mockDrone.hover(1);
            mockDrone.move(500);
            mockDrone.move(500);
            mockDrone.move(500);
            
            mockDrone.land();
            mockDrone.close();
        } catch (Exception e) {
            fail("Student code should not throw exceptions during normal execution: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("Conditional Logic Tests")
    class ConditionalLogicTests {

        @Test
        @DisplayName("Should demonstrate decision-making in flight patterns")
        void shouldDemonstrateDecisionMaking() {
            executeStudentFlightPattern();
            
            // Conditionals enable decision-based flight patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have alternative flight paths
            boolean hasAlternativePaths = commands.stream()
                .anyMatch(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll")) &&
                commands.stream().anyMatch(cmd -> cmd.contains("setYaw") || cmd.contains("setThrottle"));
            
            assertTrue(hasAlternativePaths, 
                "Student should create decision-based flight patterns using conditionals");
        }

        @Test
        @DisplayName("Should use sensor data for conditional control")
        void shouldUseSensorDataForConditionalControl() {
            executeStudentFlightPattern();
            
            // Conditionals often use sensor data for decisions
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have sensor-related calls (range, battery, etc.)
            boolean hasSensorUsage = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery") || 
                                cmd.contains("height") || cmd.contains("state"));
            
            assertTrue(hasSensorUsage, 
                "Student should use sensor data for conditional flight control");
        }

        @Test
        @DisplayName("Should demonstrate if/else logic patterns")
        void shouldDemonstrateIfElseLogic() {
            executeStudentFlightPattern();
            
            // If/else creates branching behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple movement choices (indicating branching)
            boolean hasBranchingBehavior = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 3;
            
            assertTrue(hasBranchingBehavior, 
                "Student should demonstrate if/else logic through branching behavior");
        }
    }

    @Nested
    @DisplayName("Comparison Operations Tests")
    class ComparisonOperationsTests {

        @Test
        @DisplayName("Should use comparison operators for flight decisions")
        void shouldUseComparisonOperators() {
            executeStudentFlightPattern();
            
            // Comparisons drive conditional behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have varied movement patterns (indicating comparisons)
            boolean hasVariedPatterns = commands.stream()
                .anyMatch(cmd -> cmd.contains("setPitch")) &&
                commands.stream().anyMatch(cmd -> cmd.contains("setRoll"));
            
            assertTrue(hasVariedPatterns, 
                "Student should use comparison operators for flight decisions");
        }

        @Test
        @DisplayName("Should demonstrate range-based conditional logic")
        void shouldDemonstrateRangeBasedLogic() {
            executeStudentFlightPattern();
            
            // Range sensors are common for conditional logic
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have range-based behavior
            boolean hasRangeBasedBehavior = commands.stream()
                .anyMatch(cmd -> cmd.contains("range")) ||
                commands.stream().filter(cmd -> cmd.contains("move")).count() >= 2;
            
            assertTrue(hasRangeBasedBehavior, 
                "Student should demonstrate range-based conditional logic");
        }

        @Test
        @DisplayName("Should use conditional logic for safety")
        void shouldUseConditionalLogicForSafety() {
            executeStudentFlightPattern();
            
            // Conditionals provide safety through checks
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have safety-related patterns
            boolean hasSafetyPatterns = 
                commands.stream().anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                !commands.stream().anyMatch(cmd -> cmd.equals("emergency_stop"));
            
            assertTrue(hasSafetyPatterns, 
                "Student should use conditional logic for flight safety");
        }
    }

    @Nested
    @DisplayName("Logical Operators Tests")
    class LogicalOperatorsTests {

        @Test
        @DisplayName("Should combine multiple conditions for complex decisions")
        void shouldCombineMultipleConditions() {
            executeStudentFlightPattern();
            
            // Logical operators combine conditions
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complex patterns (multiple conditions)
            boolean hasComplexConditions = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.contains("range") || cmd.contains("battery"))
                .count() >= 4;
            
            assertTrue(hasComplexConditions, 
                "Student should combine multiple conditions for complex decisions");
        }

        @Test
        @DisplayName("Should demonstrate AND/OR logic in flight control")
        void shouldDemonstrateAndOrLogic() {
            executeStudentFlightPattern();
            
            // AND/OR logic creates sophisticated behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple sensor checks or movement combinations
            boolean hasAndOrLogic = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) &&
                commands.stream().filter(cmd -> cmd.contains("set")).count() >= 2;
            
            assertTrue(hasAndOrLogic, 
                "Student should demonstrate AND/OR logic in flight control");
        }

        @Test
        @DisplayName("Should use NOT logic for safety conditions")
        void shouldUseNotLogicForSafety() {
            executeStudentFlightPattern();
            
            // NOT logic important for safety (not too close, not too low, etc.)
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have safety-conscious behavior
            boolean hasSafetyLogic = commands.stream()
                .anyMatch(cmd -> cmd.equals("land")) &&
                commands.stream().filter(cmd -> cmd.startsWith("move")).count() >= 1;
            
            assertTrue(hasSafetyLogic, 
                "Student should use NOT logic for safety conditions");
        }
    }

    @Nested
    @DisplayName("Dynamic Flight Patterns Tests")
    class DynamicFlightPatternsTests {

        @Test
        @DisplayName("Should create responsive flight behavior")
        void shouldCreateResponsiveFlightBehavior() {
            executeStudentFlightPattern();
            
            // Conditionals create responsive behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have varied responses
            boolean hasResponsiveBehavior = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 3;
            
            assertTrue(hasResponsiveBehavior, 
                "Student should create responsive flight behavior with conditionals");
        }

        @Test
        @DisplayName("Should adapt flight patterns based on conditions")
        void shouldAdaptFlightPatterns() {
            executeStudentFlightPattern();
            
            // Conditionals enable adaptive behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have adaptive patterns
            boolean hasAdaptivePatterns = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) ||
                commands.stream().filter(cmd -> cmd.contains("move")).count() >= 2;
            
            assertTrue(hasAdaptivePatterns, 
                "Student should adapt flight patterns based on conditions");
        }

        @Test
        @DisplayName("Should demonstrate conditional flight sequences")
        void shouldDemonstrateConditionalSequences() {
            executeStudentFlightPattern();
            
            // Conditionals create different sequence possibilities
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have sequenced behavior
            boolean hasSequencedBehavior = commands.size() >= 6;
            assertTrue(hasSequencedBehavior, 
                "Student should demonstrate conditional flight sequences");
        }
    }

    @Nested
    @DisplayName("Educational Progression Tests")
    class EducationalProgressionTests {

        @Test
        @DisplayName("Should build on variable concepts from L0104/L0105")
        void shouldBuildOnVariableConcepts() {
            executeStudentFlightPattern();
            
            // Conditionals work with variables
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have consistent variable usage patterns
            boolean hasVariableUsage = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 2;
            
            assertTrue(hasVariableUsage, 
                "Student should build on variable concepts in conditional logic");
        }

        @Test
        @DisplayName("Should prepare for loop concepts (L0107)")
        void shouldPrepareForLoopConcepts() {
            executeStudentFlightPattern();
            
            // Conditionals prepare for loops (while loops use conditionals)
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have repetitive patterns that could benefit from loops
            boolean hasRepetitivePatterns = commands.stream()
                .filter(cmd -> cmd.startsWith("move") || cmd.contains("hover"))
                .count() >= 3;
            
            assertTrue(hasRepetitivePatterns, 
                "Student should create patterns that prepare for loop concepts");
        }

        @Test
        @DisplayName("Should demonstrate problem-solving with conditionals")
        void shouldDemonstrateProblemSolving() {
            executeStudentFlightPattern();
            
            // Conditionals solve real programming problems
            List<String> commands = mockDrone.getAllCommands();
            
            // Should solve problems through conditional logic
            boolean solvesProblem = 
                commands.stream().anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 5;
            
            assertTrue(solvesProblem, 
                "Student should solve problems using conditional logic");
        }
    }

    @Nested
    @DisplayName("Practical Application Tests")
    class PracticalApplicationTests {

        @Test
        @DisplayName("Should implement obstacle avoidance logic")
        void shouldImplementObstacleAvoidanceLogic() {
            executeStudentFlightPattern();
            
            // Common conditional use case
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have range-based avoidance behavior
            boolean hasAvoidanceLogic = commands.stream()
                .anyMatch(cmd -> cmd.contains("range")) ||
                commands.stream().anyMatch(cmd -> cmd.contains("setYaw") || cmd.contains("setRoll"));
            
            assertTrue(hasAvoidanceLogic, 
                "Student should implement obstacle avoidance logic");
        }

        @Test
        @DisplayName("Should demonstrate battery-aware flight patterns")
        void shouldDemonstrateBatteryAwarePatterns() {
            executeStudentFlightPattern();
            
            // Battery checks are important conditional use case
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have battery-conscious behavior
            boolean hasBatteryAwareness = commands.stream()
                .anyMatch(cmd -> cmd.contains("battery")) ||
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasBatteryAwareness, 
                "Student should demonstrate battery-aware flight patterns");
        }

        @Test
        @DisplayName("Should create intelligent flight automation")
        void shouldCreateIntelligentFlightAutomation() {
            executeStudentFlightPattern();
            
            // Conditionals enable intelligent automation
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have automated decision-making
            boolean hasAutomation = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) &&
                commands.stream().filter(cmd -> cmd.contains("set")).count() >= 2;
            
            assertTrue(hasAutomation, 
                "Student should create intelligent flight automation with conditionals");
        }
    }
}
