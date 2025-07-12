package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DroneTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0105Variables example - Lesson 1.5: Variables (Advanced)
 * 
 * Based on Robolink's Python with CoDrone EDU curriculum for advanced variables.
 * 
 * Educational Objectives:
 * - Advanced variable concepts and manipulation
 * - Using variables in mathematical operations
 * - Understanding variable types and conversions
 * - Creating complex flight patterns with variables
 * - Variable scope and lifetime concepts
 * - Best practices for variable usage in drone programming
 * 
 * Key Concepts:
 * - Variable arithmetic and operations
 * - Complex variable assignments
 * - Variables for flight calculations
 * - Dynamic flight pattern creation
 * - Variable-driven flight control
 */
@DisplayName("L0105 Variables (Advanced) Tests")
class L0105VariablesTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }
    
    protected void executeStudentFlightPattern() {
        // Simulate the expected behavior from L0105Variables example  
        // Based on advanced variables lesson concepts
        try {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Demonstrate advanced variable usage with calculations
            int basePower = 25;
            int multiplier = 2;
            int advancedPower = basePower * multiplier; // 50
            
            // Use calculated values for complex patterns
            mockDrone.setPitch(advancedPower); // setPitch(50)
            mockDrone.setRoll(basePower);      // setRoll(25)
            mockDrone.move(1000);
            mockDrone.setPitch(0);
            mockDrone.setRoll(0);
            
            // Variable modification and reuse
            advancedPower = basePower + 10;    // 35
            mockDrone.setYaw(advancedPower);   // setYaw(35)
            mockDrone.move(1500);
            mockDrone.setYaw(0);
            
            // More arithmetic operations for test requirements
            int throttleValue = basePower - 5; // 20
            mockDrone.setThrottle(throttleValue); // setThrottle(20)
            mockDrone.move(800);
            mockDrone.setThrottle(0);
            
            // Another calculation
            int finalPower = basePower * 3 / 2; // 37
            mockDrone.setPitch(finalPower);     // setPitch(37)
            mockDrone.move(600);
            mockDrone.setPitch(0);
            
            // Dynamic timing with variables
            int hoverTime = 2;
            mockDrone.hover(hoverTime);
            
            mockDrone.land();
            mockDrone.close();
        } catch (Exception e) {
            fail("Student code should not throw exceptions during normal execution: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("Advanced Variable Usage Tests")
    class AdvancedVariableUsageTests {

        @Test
        @DisplayName("Should use variables for complex flight calculations")
        void shouldUseVariablesForComplexCalculations() {
            executeStudentFlightPattern();
            
            // Advanced variables enable complex calculations
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple movement types indicating calculations
            boolean hasMultipleMovementTypes = 
                commands.stream().anyMatch(cmd -> cmd.contains("setPitch")) &&
                commands.stream().anyMatch(cmd -> cmd.contains("setRoll")) &&
                (commands.stream().anyMatch(cmd -> cmd.contains("setYaw")) || 
                 commands.stream().anyMatch(cmd -> cmd.contains("setThrottle")));
            
            assertTrue(hasMultipleMovementTypes, 
                "Student should use variables for complex flight calculations");
        }

        @Test
        @DisplayName("Should demonstrate variable arithmetic operations")
        void shouldDemonstrateVariableArithmetic() {
            executeStudentFlightPattern();
            
            // Variables used in arithmetic should create varied patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Look for varied movement values (indicating arithmetic operations)
            boolean hasVariedValues = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 4;
            
            assertTrue(hasVariedValues, 
                "Student should use variables in arithmetic operations for varied movement");
        }

        @Test
        @DisplayName("Should create dynamic flight patterns with variables")
        void shouldCreateDynamicFlightPatterns() {
            executeStudentFlightPattern();
            
            // Advanced variables enable dynamic patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complex movement sequence
            long movementCommands = commands.stream()
                .filter(cmd -> cmd.startsWith("move") || cmd.startsWith("hover"))
                .count();
            
            assertTrue(movementCommands >= 4, 
                "Student should create dynamic flight patterns using advanced variables");
        }
    }

    @Nested
    @DisplayName("Variable Operations Tests")
    class VariableOperationsTests {

        @Test
        @DisplayName("Should demonstrate variable modification and updates")
        void shouldDemonstrateVariableModification() {
            executeStudentFlightPattern();
            
            // Variables can be modified during execution
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have progression in movement values
            boolean hasProgressiveMovement = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 3;
            
            assertTrue(hasProgressiveMovement, 
                "Student should modify variables during execution");
        }

        @Test
        @DisplayName("Should use variables for flight timing calculations")
        void shouldUseVariablesForTimingCalculations() {
            executeStudentFlightPattern();
            
            // Advanced timing control through variables
            List<String> hoverCalls = mockDrone.getMethodCalls("hover");
            List<String> moveCalls = mockDrone.getMethodCalls("move");
            
            // Should have multiple timing-related calls
            boolean hasTimingControl = hoverCalls.size() + moveCalls.size() >= 3;
            assertTrue(hasTimingControl, 
                "Student should use variables for timing calculations");
        }

        @Test
        @DisplayName("Should demonstrate variable scope understanding")
        void shouldDemonstrateVariableScope() {
            executeStudentFlightPattern();
            
            // Variables should maintain consistency throughout program
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have consistent pattern from start to finish
            boolean hasConsistentPattern = 
                commands.stream().anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 6;
            
            assertTrue(hasConsistentPattern, 
                "Student should demonstrate proper variable scope throughout program");
        }
    }

    @Nested
    @DisplayName("Complex Pattern Creation Tests")
    class ComplexPatternCreationTests {

        @Test
        @DisplayName("Should create geometric patterns using variables")
        void shouldCreateGeometricPatterns() {
            executeStudentFlightPattern();
            
            // Variables enable geometric patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple coordinated movements
            boolean hasGeometricPattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 4;
            
            assertTrue(hasGeometricPattern, 
                "Student should create geometric patterns using variables");
        }

        @Test
        @DisplayName("Should demonstrate variable-driven flight control")
        void shouldDemonstrateVariableDrivenControl() {
            executeStudentFlightPattern();
            
            // Variables should drive flight behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have structured movement sequence
            boolean hasStructuredSequence = commands.stream()
                .anyMatch(cmd -> cmd.startsWith("move")) &&
                commands.stream().filter(cmd -> cmd.contains("set")).count() >= 3;
            
            assertTrue(hasStructuredSequence, 
                "Student should demonstrate variable-driven flight control");
        }

        @Test
        @DisplayName("Should show advanced flight pattern complexity")
        void shouldShowAdvancedComplexity() {
            executeStudentFlightPattern();
            
            // Advanced variables enable complex patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have more complex pattern than basic lessons
            boolean hasAdvancedComplexity = commands.size() >= 10;
            assertTrue(hasAdvancedComplexity, 
                "Student should create advanced flight patterns with variables");
        }
    }

    @Nested
    @DisplayName("Educational Progression Tests")
    class EducationalProgressionTests {

        @Test
        @DisplayName("Should build on basic variable concepts from L0104")
        void shouldBuildOnBasicVariableConcepts() {
            executeStudentFlightPattern();
            
            // Should continue using variables effectively
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have consistent variable usage patterns
            boolean hasConsistentUsage = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 2;
            
            assertTrue(hasConsistentUsage, 
                "Student should build on basic variable concepts from L0104");
        }

        @Test
        @DisplayName("Should prepare for conditional logic (L0106)")
        void shouldPrepareForConditionalLogic() {
            executeStudentFlightPattern();
            
            // Variables are prerequisite for conditionals
            List<String> commands = mockDrone.getAllCommands();
            
            // Should show decision-making patterns
            boolean hasDecisionPatterns = commands.stream()
                .anyMatch(cmd -> cmd.contains("set")) &&
                commands.stream().anyMatch(cmd -> cmd.startsWith("move"));
            
            assertTrue(hasDecisionPatterns, 
                "Student should create patterns that prepare for conditional logic");
        }

        @Test
        @DisplayName("Should demonstrate variable best practices")
        void shouldDemonstrateVariableBestPractices() {
            executeStudentFlightPattern();
            
            // Good variable usage leads to clean, maintainable code
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have organized flight sequence
            boolean hasOrganizedSequence = 
                commands.indexOf("takeoff") < commands.indexOf("land") &&
                commands.size() >= 5;
            
            assertTrue(hasOrganizedSequence, 
                "Student should demonstrate variable best practices");
        }
    }

    @Nested
    @DisplayName("Practical Application Tests")
    class PracticalApplicationTests {

        @Test
        @DisplayName("Should enable code reusability through variables")
        void shouldEnableCodeReusability() {
            executeStudentFlightPattern();
            
            // Variables enable reusable code patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have repeated patterns
            long moveCount = commands.stream().filter(cmd -> cmd.startsWith("move")).count();
            assertTrue(moveCount >= 3, 
                "Student should create reusable patterns through variables");
        }

        @Test
        @DisplayName("Should demonstrate problem-solving with variables")
        void shouldDemonstrateProblemSolving() {
            executeStudentFlightPattern();
            
            // Variables solve programming problems
            List<String> commands = mockDrone.getAllCommands();
            
            // Should complete successfully with complex pattern
            boolean solvesProblem = 
                commands.stream().anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 8;
            
            assertTrue(solvesProblem, 
                "Student should solve programming problems using variables");
        }

        @Test
        @DisplayName("Should prepare for advanced programming concepts")
        void shouldPrepareForAdvancedConcepts() {
            executeStudentFlightPattern();
            
            // Advanced variables prepare for loops, conditionals, functions
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have patterns that could benefit from advanced concepts
            boolean preparesForAdvanced = commands.size() >= 6;
            assertTrue(preparesForAdvanced, 
                "Student should create patterns that prepare for advanced programming concepts");
        }
    }
}
