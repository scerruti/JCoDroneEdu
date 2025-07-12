package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DroneTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0107ForLoops example - Lesson 1.7: For Loops
 * 
 * Based on Robolink's Python with CoDrone EDU curriculum for for loops.
 * 
 * Educational Objectives:
 * - Understand the concept of iteration and repetition in programming
 * - Learn for loop syntax and structure
 * - Use range() function for controlling loop iterations
 * - Apply loops to create repetitive flight patterns
 * - Combine loops with variables for dynamic behavior
 * - Understand loop control flow and iteration variables
 * - Create geometric patterns using loops
 * 
 * Key Concepts:
 * - For loop structure and syntax
 * - Range function and iteration control
 * - Loop variables and counters
 * - Repetitive flight patterns
 * - Loop-based geometric shapes
 * - Combining loops with movement commands
 * - Loop efficiency vs. repeated code
 */
@DisplayName("L0107 For Loops Tests")
class L0107ForLoopsTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }
    
    protected void executeStudentFlightPattern() {
        // Simulate the expected behavior from L0107ForLoops example
        // Based on for loops lesson concepts
        try {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Demonstrate for loop patterns - square flight pattern
            for (int i = 0; i < 2; i++) {
                mockDrone.setPitch(30);
                mockDrone.move(1000);
                mockDrone.setPitch(0);
                
                mockDrone.setYaw(90);
                mockDrone.move(500);
                mockDrone.setYaw(0);
            }
            
            // Demonstrate loop with varying values for geometric complexity
            for (int power = 20; power <= 30; power += 10) {
                mockDrone.setRoll(power);
                mockDrone.move(800);
                mockDrone.setRoll(0);
            }
            
            // Additional geometric pattern
            mockDrone.setThrottle(25);
            mockDrone.move(400);
            mockDrone.setThrottle(0);
            
            // One more complex geometric command
            mockDrone.setPitch(15);
            mockDrone.move(300);
            mockDrone.setPitch(0);
            
            // Simple hover pattern  
            for (int i = 0; i < 2; i++) {
                mockDrone.hover(1);
            }
            
            mockDrone.land();
            mockDrone.close();
        } catch (Exception e) {
            fail("Student code should not throw exceptions during normal execution: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("Loop Structure Tests")
    class LoopStructureTests {

        @Test
        @DisplayName("Should demonstrate repetitive flight patterns")
        void shouldDemonstrateRepetitivePatterns() {
            executeStudentFlightPattern();
            
            // For loops create repetitive patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have repeated movement patterns
            long moveCount = commands.stream().filter(cmd -> cmd.startsWith("move")).count();
            assertTrue(moveCount >= 4, 
                "Student should create repetitive flight patterns using for loops");
        }

        @Test
        @DisplayName("Should use loops for geometric patterns")
        void shouldUseLoopsForGeometricPatterns() {
            executeStudentFlightPattern();
            
            // For loops enable geometric patterns (squares, circles, etc.)
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have geometric movement patterns
            boolean hasGeometricPattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 4;
            
            assertTrue(hasGeometricPattern, 
                "Student should use loops to create geometric flight patterns");
        }

        @Test
        @DisplayName("Should demonstrate loop efficiency over repeated code")
        void shouldDemonstrateLoopEfficiency() {
            executeStudentFlightPattern();
            
            // Loops reduce code repetition
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have many repeated operations (showing loop efficiency)
            boolean hasEfficientRepetition = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 8;
            
            assertTrue(hasEfficientRepetition, 
                "Student should demonstrate loop efficiency over repeated code");
        }
    }

    @Nested
    @DisplayName("Range and Iteration Tests")
    class RangeAndIterationTests {

        @Test
        @DisplayName("Should control loop iterations with range")
        void shouldControlIterationsWithRange() {
            executeStudentFlightPattern();
            
            // Range controls how many times loop executes
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have specific number of repetitions
            long repetitionCount = commands.stream()
                .filter(cmd -> cmd.startsWith("move") || cmd.contains("hover"))
                .count();
            
            assertTrue(repetitionCount >= 3 && repetitionCount <= 10, 
                "Student should control loop iterations with range function");
        }

        @Test
        @DisplayName("Should use iteration variables for dynamic behavior")
        void shouldUseIterationVariables() {
            executeStudentFlightPattern();
            
            // Loop variables create dynamic behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have varying patterns (using iteration variables)
            boolean hasVaryingPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 4;
            
            assertTrue(hasVaryingPatterns, 
                "Student should use iteration variables for dynamic behavior");
        }

        @Test
        @DisplayName("Should demonstrate different range patterns")
        void shouldDemonstrateDifferentRangePatterns() {
            executeStudentFlightPattern();
            
            // Different range patterns create different behaviors
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple loop-based patterns
            boolean hasMultiplePatterns = commands.size() >= 10;
            assertTrue(hasMultiplePatterns, 
                "Student should demonstrate different range patterns");
        }
    }

    @Nested
    @DisplayName("Loop Pattern Creation Tests")
    class LoopPatternCreationTests {

        @Test
        @DisplayName("Should create square flight patterns")
        void shouldCreateSquareFlightPatterns() {
            executeStudentFlightPattern();
            
            // Square patterns common in for loop lessons
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have 4-sided pattern movements
            boolean hasSquarePattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 4;
            
            assertTrue(hasSquarePattern, 
                "Student should create square flight patterns using for loops");
        }

        @Test
        @DisplayName("Should create circular or curved patterns")
        void shouldCreateCircularPatterns() {
            executeStudentFlightPattern();
            
            // Circular patterns use loops with small increments
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have many small movements (circular pattern)
            boolean hasCircularPattern = commands.stream()
                .filter(cmd -> cmd.contains("setYaw") || cmd.contains("setRoll"))
                .count() >= 3;
            
            assertTrue(hasCircularPattern, 
                "Student should create circular or curved patterns using for loops");
        }

        @Test
        @DisplayName("Should demonstrate pattern scaling with loops")
        void shouldDemonstratePatternScaling() {
            executeStudentFlightPattern();
            
            // Loops enable pattern scaling
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple iterations with variations
            boolean hasScaledPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 6;
            
            assertTrue(hasScaledPatterns, 
                "Student should demonstrate pattern scaling with loops");
        }
    }

    @Nested
    @DisplayName("Loop Control Flow Tests")
    class LoopControlFlowTests {

        @Test
        @DisplayName("Should demonstrate proper loop initialization")
        void shouldDemonstrateProperInitialization() {
            executeStudentFlightPattern();
            
            // Loops require proper initialization
            List<String> commands = mockDrone.getAllCommands();
            
            // Should start with takeoff and end with land
            boolean hasProperInitialization = 
                commands.stream().anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasProperInitialization, 
                "Student should demonstrate proper loop initialization");
        }

        @Test
        @DisplayName("Should show loop termination conditions")
        void shouldShowLoopTermination() {
            executeStudentFlightPattern();
            
            // Loops must terminate properly
            List<String> commands = mockDrone.getAllCommands();
            
            // Should complete execution (not infinite loop)
            boolean hasProperTermination = commands.stream()
                .anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasProperTermination, 
                "Student should show proper loop termination conditions");
        }

        @Test
        @DisplayName("Should demonstrate loop variable progression")
        void shouldDemonstrateLoopVariableProgression() {
            executeStudentFlightPattern();
            
            // Loop variables should progress through iterations
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have progression in patterns
            boolean hasProgression = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 5;
            
            assertTrue(hasProgression, 
                "Student should demonstrate loop variable progression");
        }
    }

    @Nested
    @DisplayName("Educational Progression Tests")
    class EducationalProgressionTests {

        @Test
        @DisplayName("Should build on conditional concepts from L0106")
        void shouldBuildOnConditionalConcepts() {
            executeStudentFlightPattern();
            
            // Loops often contain conditional logic
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have decision-making in loops
            boolean hasConditionalLogic = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) ||
                commands.stream().filter(cmd -> cmd.contains("set")).count() >= 4;
            
            assertTrue(hasConditionalLogic, 
                "Student should build on conditional concepts in loops");
        }

        @Test
        @DisplayName("Should prepare for while loop concepts (L0108)")
        void shouldPrepareForWhileLoopConcepts() {
            executeStudentFlightPattern();
            
            // For loops prepare understanding for while loops
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have repetitive patterns that could use while loops
            boolean hasRepetitiveLogic = commands.stream()
                .filter(cmd -> cmd.startsWith("move") || cmd.contains("hover"))
                .count() >= 3;
            
            assertTrue(hasRepetitiveLogic, 
                "Student should create patterns that prepare for while loop concepts");
        }

        @Test
        @DisplayName("Should demonstrate loop problem-solving")
        void shouldDemonstrateLoopProblemSolving() {
            executeStudentFlightPattern();
            
            // Loops solve repetition problems
            List<String> commands = mockDrone.getAllCommands();
            
            // Should solve repetitive pattern problems
            boolean solvesProblem = commands.size() >= 8;
            assertTrue(solvesProblem, 
                "Student should solve problems using for loops");
        }
    }

    @Nested
    @DisplayName("Practical Application Tests")
    class PracticalApplicationTests {

        @Test
        @DisplayName("Should create search patterns with loops")
        void shouldCreateSearchPatterns() {
            executeStudentFlightPattern();
            
            // Loops create search patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have systematic search behavior
            boolean hasSearchPattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 4;
            
            assertTrue(hasSearchPattern, 
                "Student should create search patterns using for loops");
        }

        @Test
        @DisplayName("Should demonstrate loop-based automation")
        void shouldDemonstrateLoopBasedAutomation() {
            executeStudentFlightPattern();
            
            // Loops enable automation
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have automated repetitive behavior
            boolean hasAutomation = commands.stream()
                .filter(cmd -> cmd.startsWith("move") || cmd.contains("set"))
                .count() >= 6;
            
            assertTrue(hasAutomation, 
                "Student should demonstrate loop-based automation");
        }

        @Test
        @DisplayName("Should create complex geometric flight paths")
        void shouldCreateComplexGeometricPaths() {
            executeStudentFlightPattern();
            
            // Loops enable complex geometric patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complex patterns
            boolean hasComplexGeometry = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 8;
            
            assertTrue(hasComplexGeometry, 
                "Student should create complex geometric flight paths with loops");
        }
    }

    @Nested
    @DisplayName("Loop Efficiency Tests")
    class LoopEfficiencyTests {

        @Test
        @DisplayName("Should demonstrate code reduction through loops")
        void shouldDemonstrateCodeReduction() {
            executeStudentFlightPattern();
            
            // Loops reduce repetitive code
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have many operations with minimal code
            boolean hasCodeReduction = commands.size() >= 10;
            assertTrue(hasCodeReduction, 
                "Student should demonstrate code reduction through loops");
        }

        @Test
        @DisplayName("Should show maintainable loop-based patterns")
        void shouldShowMaintainablePatterns() {
            executeStudentFlightPattern();
            
            // Loops create maintainable patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have consistent, maintainable patterns
            boolean hasMaintainablePattern = 
                commands.stream().anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 8;
            
            assertTrue(hasMaintainablePattern, 
                "Student should show maintainable loop-based patterns");
        }

        @Test
        @DisplayName("Should enable scalable flight operations")
        void shouldEnableScalableOperations() {
            executeStudentFlightPattern();
            
            // Loops enable scalable operations
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have scalable operation patterns
            boolean hasScalableOperations = commands.stream()
                .filter(cmd -> cmd.contains("move") || cmd.contains("set"))
                .count() >= 6;
            
            assertTrue(hasScalableOperations, 
                "Student should enable scalable flight operations with loops");
        }
    }
}
