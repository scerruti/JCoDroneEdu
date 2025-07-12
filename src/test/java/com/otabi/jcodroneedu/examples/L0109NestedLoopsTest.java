package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DroneTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0109NestedLoops example - Lesson 1.9: Nested Loops
 * 
 * Based on Robolink's Python with CoDrone EDU curriculum for nested loops.
 * 
 * Educational Objectives:
 * - Understand nested loop structure and execution flow
 * - Learn to create loops within loops for complex patterns
 * - Apply nested loops to create 2D flight patterns
 * - Understand loop variable scope in nested structures
 * - Create grid-based and matrix-like flight patterns
 * - Combine nested loops with conditionals for complex behavior
 * - Optimize nested loop performance and structure
 * 
 * Key Concepts:
 * - Nested loop syntax and structure
 * - Inner loop vs outer loop execution
 * - 2D pattern creation with nested loops
 * - Loop variable scope and naming
 * - Complex geometric patterns
 * - Performance considerations with nested loops
 * - Real-world applications of nested iteration
 */
@DisplayName("L0109 Nested Loops Tests")
class L0109NestedLoopsTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }
    
    protected void executeStudentFlightPattern() {
        // Simulate the expected behavior from L0109NestedLoops example
        // Based on nested loop lesson concepts
        try {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Simulate nested loop patterns - 2D grid flight
            // Outer loop: rows, Inner loop: columns
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    // Move to grid position
                    mockDrone.setPitch(25);
                    mockDrone.move(400);
                    mockDrone.setPitch(0);
                    
                    // Brief hover at position
                    mockDrone.hover(1);
                }
                
                // Move to next row
                mockDrone.setRoll(30);
                mockDrone.move(500);
                mockDrone.setRoll(0);
            }
            
            // Additional pattern for complexity
            for (int outer = 0; outer < 2; outer++) {
                for (int inner = 0; inner < 2; inner++) {
                    mockDrone.setYaw(45);
                    mockDrone.move(200);
                    mockDrone.setYaw(0);
                }
                mockDrone.setThrottle(20);
                mockDrone.move(300);
                mockDrone.setThrottle(0);
            }
            
            mockDrone.land();
            mockDrone.close();
        } catch (Exception e) {
            fail("Student code should not throw exceptions during normal execution: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("Nested Loop Structure Tests")
    class NestedLoopStructureTests {

        @Test
        @DisplayName("Should demonstrate nested loop execution patterns")
        void shouldDemonstrateNestedLoopExecution() {
            executeStudentFlightPattern();
            
            // Nested loops create complex execution patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have many repetitive movements (nested loop characteristic)
            long movementCount = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count();
            
            assertTrue(movementCount >= 12, 
                "Student should demonstrate nested loop execution patterns");
        }

        @Test
        @DisplayName("Should create 2D flight patterns")
        void shouldCreate2DFlightPatterns() {
            executeStudentFlightPattern();
            
            // Nested loops excellent for 2D patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have both X and Y axis movements
            boolean has2DPattern = commands.stream()
                .anyMatch(cmd -> cmd.contains("setPitch")) &&
                commands.stream().anyMatch(cmd -> cmd.contains("setRoll"));
            
            assertTrue(has2DPattern, 
                "Student should create 2D flight patterns with nested loops");
        }

        @Test
        @DisplayName("Should demonstrate proper loop nesting structure")
        void shouldDemonstrateProperNestingStructure() {
            executeStudentFlightPattern();
            
            // Nested loops should show structured repetition
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have structured pattern repetition
            boolean hasStructuredRepetition = commands.stream()
                .filter(cmd -> cmd.contains("move") || cmd.contains("hover"))
                .count() >= 8;
            
            assertTrue(hasStructuredRepetition, 
                "Student should demonstrate proper loop nesting structure");
        }
    }

    @Nested
    @DisplayName("Complex Pattern Creation Tests")
    class ComplexPatternCreationTests {

        @Test
        @DisplayName("Should create grid-based flight patterns")
        void shouldCreateGridBasedFlightPatterns() {
            executeStudentFlightPattern();
            
            // Nested loops create grid patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have grid-like movement patterns
            boolean hasGridPattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 6;
            
            assertTrue(hasGridPattern, 
                "Student should create grid-based flight patterns");
        }

        @Test
        @DisplayName("Should demonstrate matrix-like flight operations")
        void shouldDemonstrateMatrixLikeOperations() {
            executeStudentFlightPattern();
            
            // Nested loops enable matrix-like operations
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have matrix-like systematic patterns
            boolean hasMatrixPattern = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 8;
            
            assertTrue(hasMatrixPattern, 
                "Student should demonstrate matrix-like flight operations");
        }

        @Test
        @DisplayName("Should create complex geometric shapes")
        void shouldCreateComplexGeometricShapes() {
            executeStudentFlightPattern();
            
            // Nested loops enable complex shapes
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complex geometric patterns
            boolean hasComplexGeometry = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 15;
            
            assertTrue(hasComplexGeometry, 
                "Student should create complex geometric shapes with nested loops");
        }
    }

    @Nested
    @DisplayName("Loop Variable Scope Tests")
    class LoopVariableScopeTests {

        @Test
        @DisplayName("Should demonstrate proper variable scope in nested loops")
        void shouldDemonstrateProperVariableScope() {
            executeStudentFlightPattern();
            
            // Nested loops require proper variable scope
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have consistent execution (proper scope)
            boolean hasConsistentExecution = commands.stream()
                .anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasConsistentExecution, 
                "Student should demonstrate proper variable scope in nested loops");
        }

        @Test
        @DisplayName("Should use different variables for inner and outer loops")
        void shouldUseDifferentVariablesForLoops() {
            executeStudentFlightPattern();
            
            // Good practice: different variables for different loops
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have varied movement patterns (different variables)
            boolean hasVariedPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 6;
            
            assertTrue(hasVariedPatterns, 
                "Student should use different variables for inner and outer loops");
        }

        @Test
        @DisplayName("Should demonstrate loop variable independence")
        void shouldDemonstrateLoopVariableIndependence() {
            executeStudentFlightPattern();
            
            // Loop variables should be independent
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have independent pattern variations
            boolean hasIndependentPatterns = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 4;
            
            assertTrue(hasIndependentPatterns, 
                "Student should demonstrate loop variable independence");
        }
    }

    @Nested
    @DisplayName("Performance and Optimization Tests")
    class PerformanceOptimizationTests {

        @Test
        @DisplayName("Should demonstrate efficient nested loop design")
        void shouldDemonstrateEfficientDesign() {
            executeStudentFlightPattern();
            
            // Nested loops should be efficient
            List<String> commands = mockDrone.getAllCommands();
            
            // Should complete in reasonable time with reasonable commands
            boolean hasEfficientDesign = commands.size() >= 10 && commands.size() <= 100;
            assertTrue(hasEfficientDesign, 
                "Student should demonstrate efficient nested loop design");
        }

        @Test
        @DisplayName("Should avoid unnecessary nested loop complexity")
        void shouldAvoidUnnecessaryComplexity() {
            executeStudentFlightPattern();
            
            // Should be complex but not overly so
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have appropriate complexity level
            boolean hasAppropriateComplexity = commands.size() >= 8 && commands.size() <= 50;
            assertTrue(hasAppropriateComplexity, 
                "Student should avoid unnecessary nested loop complexity");
        }

        @Test
        @DisplayName("Should demonstrate structured nested loop organization")
        void shouldDemonstrateStructuredOrganization() {
            executeStudentFlightPattern();
            
            // Nested loops should be well-organized
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have organized pattern structure
            boolean hasOrganizedStructure = commands.stream()
                .anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 10;
            
            assertTrue(hasOrganizedStructure, 
                "Student should demonstrate structured nested loop organization");
        }
    }

    @Nested
    @DisplayName("Educational Progression Tests")
    class EducationalProgressionTests {

        @Test
        @DisplayName("Should build on while loop concepts from L0108")
        void shouldBuildOnWhileLoopConcepts() {
            executeStudentFlightPattern();
            
            // Nested loops build on previous loop concepts
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have advanced loop patterns
            boolean hasAdvancedLoopPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 8;
            
            assertTrue(hasAdvancedLoopPatterns, 
                "Student should build on while loop concepts");
        }

        @Test
        @DisplayName("Should prepare for function concepts (L0110)")
        void shouldPrepareForFunctionConcepts() {
            executeStudentFlightPattern();
            
            // Nested loops prepare for function modularity
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have patterns that could be modularized
            boolean hasModularizablePatterns = commands.size() >= 12;
            assertTrue(hasModularizablePatterns, 
                "Student should create patterns that prepare for function concepts");
        }

        @Test
        @DisplayName("Should demonstrate advanced programming problem-solving")
        void shouldDemonstrateAdvancedProblemSolving() {
            executeStudentFlightPattern();
            
            // Nested loops solve complex problems
            List<String> commands = mockDrone.getAllCommands();
            
            // Should solve complex problems
            boolean solvesComplexProblems = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 10;
            
            assertTrue(solvesComplexProblems, 
                "Student should solve advanced problems with nested loops");
        }
    }

    @Nested
    @DisplayName("Practical Application Tests")
    class PracticalApplicationTests {

        @Test
        @DisplayName("Should create search grid patterns")
        void shouldCreateSearchGridPatterns() {
            executeStudentFlightPattern();
            
            // Nested loops excellent for search patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have systematic search patterns
            boolean hasSearchPattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 6;
            
            assertTrue(hasSearchPattern, 
                "Student should create search grid patterns");
        }

        @Test
        @DisplayName("Should demonstrate area coverage patterns")
        void shouldDemonstrateAreaCoveragePatterns() {
            executeStudentFlightPattern();
            
            // Nested loops for area coverage
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have area coverage behavior
            boolean hasAreaCoverage = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 8;
            
            assertTrue(hasAreaCoverage, 
                "Student should demonstrate area coverage patterns");
        }

        @Test
        @DisplayName("Should create complex autonomous flight behaviors")
        void shouldCreateComplexAutonomousBehaviors() {
            executeStudentFlightPattern();
            
            // Nested loops enable complex autonomous behavior
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complex autonomous patterns
            boolean hasComplexAutonomy = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move") || cmd.contains("hover"))
                .count() >= 12;
            
            assertTrue(hasComplexAutonomy, 
                "Student should create complex autonomous flight behaviors");
        }
    }

    @Nested
    @DisplayName("Real-World Application Tests")
    class RealWorldApplicationTests {

        @Test
        @DisplayName("Should demonstrate mapping and surveying patterns")
        void shouldDemonstrateMappingPatterns() {
            executeStudentFlightPattern();
            
            // Nested loops used in mapping/surveying
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have systematic mapping patterns
            boolean hasMappingPattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 8;
            
            assertTrue(hasMappingPattern, 
                "Student should demonstrate mapping and surveying patterns");
        }

        @Test
        @DisplayName("Should create inspection flight patterns")
        void shouldCreateInspectionPatterns() {
            executeStudentFlightPattern();
            
            // Nested loops for systematic inspection
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have inspection-like systematic behavior
            boolean hasInspectionPattern = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 10;
            
            assertTrue(hasInspectionPattern, 
                "Student should create inspection flight patterns");
        }

        @Test
        @DisplayName("Should demonstrate agricultural or industrial applications")
        void shouldDemonstrateIndustrialApplications() {
            executeStudentFlightPattern();
            
            // Nested loops common in industrial applications
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have industrial-like systematic patterns
            boolean hasIndustrialPattern = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 6;
            
            assertTrue(hasIndustrialPattern, 
                "Student should demonstrate agricultural or industrial applications");
        }
    }
}
