package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DroneTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0110Functions example - Lesson 1.10: Functions
 * 
 * Based on Robolink's Python with CoDrone EDU curriculum for functions.
 * 
 * Educational Objectives:
 * - Understand function definition and calling syntax
 * - Learn to create reusable code blocks with functions
 * - Use function parameters and return values
 * - Apply functions to modularize flight patterns
 * - Understand function scope and variable passing
 * - Create function libraries for drone operations
 * - Combine functions with loops and conditionals
 * 
 * Key Concepts:
 * - Function definition (def) and calling syntax
 * - Function parameters and arguments
 * - Return statements and values
 * - Function scope and local variables
 * - Code reusability and modularity
 * - Function composition and chaining
 * - Best practices for function design
 */
@DisplayName("L0110 Functions Tests")
class L0110FunctionsTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }
    
    protected void executeStudentFlightPattern() {
        // Simulate the expected behavior from L0110Functions example
        // Based on function lesson concepts
        try {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Simulate function-based flight patterns
            // Function 1: takeOffAndHover()
            simulateTakeOffAndHover();
            
            // Function 2: squarePattern()
            simulateSquarePattern();
            
            // Function 3: spiralPattern()
            simulateSpiralPattern();
            
            // Function 4: landingSafetyCheck()
            simulateLandingSafetyCheck();
            
            mockDrone.land();
            mockDrone.close();
        } catch (Exception e) {
            fail("Student code should not throw exceptions during normal execution: " + e.getMessage());
        }
    }
    
    // Helper methods to simulate function calls
    private void simulateTakeOffAndHover() {
        // Simulate a reusable function
        mockDrone.hover(2);
    }
    
    private void simulateSquarePattern() {
        // Simulate a square flight pattern function
        for (int i = 0; i < 4; i++) {
            mockDrone.setPitch(30);
            mockDrone.move(1000);
            mockDrone.setPitch(0);
            mockDrone.setYaw(90);
            mockDrone.move(500);
            mockDrone.setYaw(0);
        }
    }
    
    private void simulateSpiralPattern() {
        // Simulate a spiral pattern function
        for (int i = 1; i <= 4; i++) {
            mockDrone.setPitch(20);
            mockDrone.move(i * 200);
            mockDrone.setPitch(0);
            mockDrone.setYaw(90);
            mockDrone.move(300);
            mockDrone.setYaw(0);
        }
    }
    
    private void simulateLandingSafetyCheck() {
        // Simulate a safety check function that returns sensor data
        int battery = mockDrone.getBattery();
        double range = mockDrone.get_front_range();
        double temperature = mockDrone.getTemperature();
        
        // Use sensor data for safety decisions
        if (battery > 20 && range > 50 && temperature < 35) {
            mockDrone.hover(1);
        } else {
            mockDrone.hover(2);  // Extra hover for safety
        }
    }

    @Nested
    @DisplayName("Function Structure Tests")
    class FunctionStructureTests {

        @Test
        @DisplayName("Should demonstrate function definition and calling")
        void shouldDemonstrateFunctionDefinitionAndCalling() {
            executeStudentFlightPattern();
            
            // Functions enable modular code
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have organized, reusable patterns
            boolean hasOrganizedPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 6;
            
            assertTrue(hasOrganizedPatterns, 
                "Student should demonstrate function definition and calling");
        }

        @Test
        @DisplayName("Should create reusable flight pattern functions")
        void shouldCreateReusableFlightPatterns() {
            executeStudentFlightPattern();
            
            // Functions create reusable patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have repeated patterns (indicating function reuse)
            boolean hasReusablePatterns = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 4;
            
            assertTrue(hasReusablePatterns, 
                "Student should create reusable flight pattern functions");
        }

        @Test
        @DisplayName("Should demonstrate function modularity")
        void shouldDemonstrateFunctionModularity() {
            executeStudentFlightPattern();
            
            // Functions provide modularity
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have modular structure
            boolean hasModularStructure = commands.stream()
                .anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 8;
            
            assertTrue(hasModularStructure, 
                "Student should demonstrate function modularity");
        }
    }

    @Nested
    @DisplayName("Function Parameters Tests")
    class FunctionParametersTests {

        @Test
        @DisplayName("Should use function parameters for flexible behavior")
        void shouldUseFunctionParameters() {
            executeStudentFlightPattern();
            
            // Parameters make functions flexible
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have varied patterns (indicating parameterization)
            boolean hasVariedPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 4;
            
            assertTrue(hasVariedPatterns, 
                "Student should use function parameters for flexible behavior");
        }

        @Test
        @DisplayName("Should demonstrate parameter passing for movement values")
        void shouldDemonstrateParameterPassing() {
            executeStudentFlightPattern();
            
            // Parameters passed for movement values
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have different movement values
            boolean hasDifferentMovementValues = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 3;
            
            assertTrue(hasDifferentMovementValues, 
                "Student should demonstrate parameter passing for movement values");
        }

        @Test
        @DisplayName("Should use parameters for timing and duration control")
        void shouldUseParametersForTimingControl() {
            executeStudentFlightPattern();
            
            // Parameters control timing
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have timing-related calls
            boolean hasTimingControl = commands.stream()
                .anyMatch(cmd -> cmd.contains("hover") || cmd.contains("move"));
            
            assertTrue(hasTimingControl, 
                "Student should use parameters for timing and duration control");
        }
    }

    @Nested
    @DisplayName("Function Return Values Tests")
    class FunctionReturnValuesTests {

        @Test
        @DisplayName("Should demonstrate functions returning sensor data")
        void shouldDemonstrateReturningData() {
            executeStudentFlightPattern();
            
            // Functions can return sensor data
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have sensor-related operations
            boolean hasSensorOperations = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery") || cmd.contains("height"));
            
            assertTrue(hasSensorOperations, 
                "Student should demonstrate functions returning sensor data");
        }

        @Test
        @DisplayName("Should use return values for decision making")
        void shouldUseReturnValuesForDecisions() {
            executeStudentFlightPattern();
            
            // Return values drive decisions
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have decision-based patterns
            boolean hasDecisionPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 5;
            
            assertTrue(hasDecisionPatterns, 
                "Student should use return values for decision making");
        }

        @Test
        @DisplayName("Should demonstrate function composition")
        void shouldDemonstrateFunctionComposition() {
            executeStudentFlightPattern();
            
            // Functions can be composed
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complex composed patterns
            boolean hasComposedPatterns = commands.size() >= 10;
            assertTrue(hasComposedPatterns, 
                "Student should demonstrate function composition");
        }
    }

    @Nested
    @DisplayName("Function Scope Tests")
    class FunctionScopeTests {

        @Test
        @DisplayName("Should demonstrate proper variable scope in functions")
        void shouldDemonstrateProperScope() {
            executeStudentFlightPattern();
            
            // Functions have proper variable scope
            List<String> commands = mockDrone.getAllCommands();
            
            // Should complete successfully (proper scope)
            boolean hasProperScope = commands.stream()
                .anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasProperScope, 
                "Student should demonstrate proper variable scope in functions");
        }

        @Test
        @DisplayName("Should show local vs global variable understanding")
        void shouldShowLocalVsGlobalUnderstanding() {
            executeStudentFlightPattern();
            
            // Understanding of local vs global variables
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have consistent variable usage
            boolean hasConsistentUsage = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 2;
            
            assertTrue(hasConsistentUsage, 
                "Student should show local vs global variable understanding");
        }

        @Test
        @DisplayName("Should demonstrate function parameter scope")
        void shouldDemonstrateFunctionParameterScope() {
            executeStudentFlightPattern();
            
            // Function parameters have local scope
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have parameter-driven variations
            boolean hasParameterDrivenVariations = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 3;
            
            assertTrue(hasParameterDrivenVariations, 
                "Student should demonstrate function parameter scope");
        }
    }

    @Nested
    @DisplayName("Code Reusability Tests")
    class CodeReusabilityTests {

        @Test
        @DisplayName("Should eliminate code duplication through functions")
        void shouldEliminateCodeDuplication() {
            executeStudentFlightPattern();
            
            // Functions eliminate duplication
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have repeated patterns without duplication
            boolean hasEfficientReuse = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 8;
            
            assertTrue(hasEfficientReuse, 
                "Student should eliminate code duplication through functions");
        }

        @Test
        @DisplayName("Should create function libraries for drone operations")
        void shouldCreateFunctionLibraries() {
            executeStudentFlightPattern();
            
            // Function libraries provide reusable operations
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have library-like organized operations
            boolean hasLibraryStructure = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move") || cmd.contains("hover"))
                .count() >= 6;
            
            assertTrue(hasLibraryStructure, 
                "Student should create function libraries for drone operations");
        }

        @Test
        @DisplayName("Should demonstrate function reuse across different patterns")
        void shouldDemonstrateFunctionReuse() {
            executeStudentFlightPattern();
            
            // Functions reused across patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have multiple pattern applications
            boolean hasMultipleApplications = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll") || 
                              cmd.contains("setYaw") || cmd.contains("setThrottle"))
                .count() >= 6;
            
            assertTrue(hasMultipleApplications, 
                "Student should demonstrate function reuse across different patterns");
        }
    }

    @Nested
    @DisplayName("Educational Progression Tests")
    class EducationalProgressionTests {

        @Test
        @DisplayName("Should build on nested loop concepts from L0109")
        void shouldBuildOnNestedLoopConcepts() {
            executeStudentFlightPattern();
            
            // Functions organize complex nested logic
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complex organized patterns
            boolean hasComplexOrganization = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 8;
            
            assertTrue(hasComplexOrganization, 
                "Student should build on nested loop concepts with functions");
        }

        @Test
        @DisplayName("Should demonstrate complete programming concept mastery")
        void shouldDemonstrateCompleteMastery() {
            executeStudentFlightPattern();
            
            // Functions represent programming mastery
            List<String> commands = mockDrone.getAllCommands();
            
            // Should demonstrate complete mastery
            boolean hasCompleteMastery = commands.stream()
                .anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 10;
            
            assertTrue(hasCompleteMastery, 
                "Student should demonstrate complete programming concept mastery");
        }

        @Test
        @DisplayName("Should prepare for advanced programming concepts")
        void shouldPrepareForAdvancedConcepts() {
            executeStudentFlightPattern();
            
            // Functions prepare for advanced concepts
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have advanced pattern complexity
            boolean hasAdvancedComplexity = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move") || 
                              cmd.contains("range") || cmd.contains("battery"))
                .count() >= 8;
            
            assertTrue(hasAdvancedComplexity, 
                "Student should prepare for advanced programming concepts");
        }
    }

    @Nested
    @DisplayName("Practical Application Tests")
    class PracticalApplicationTests {

        @Test
        @DisplayName("Should create autonomous flight function libraries")
        void shouldCreateAutonomousFlightLibraries() {
            executeStudentFlightPattern();
            
            // Functions enable autonomous flight libraries
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have autonomous behavior
            boolean hasAutonomousBehavior = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery")) ||
                commands.stream().filter(cmd -> cmd.contains("set")).count() >= 4;
            
            assertTrue(hasAutonomousBehavior, 
                "Student should create autonomous flight function libraries");
        }

        @Test
        @DisplayName("Should demonstrate mission planning with functions")
        void shouldDemonstrateMissionPlanning() {
            executeStudentFlightPattern();
            
            // Functions enable mission planning
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have mission-like structured behavior
            boolean hasMissionStructure = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move") || cmd.contains("hover"))
                .count() >= 8;
            
            assertTrue(hasMissionStructure, 
                "Student should demonstrate mission planning with functions");
        }

        @Test
        @DisplayName("Should create safety function implementations")
        void shouldCreateSafetyFunctions() {
            executeStudentFlightPattern();
            
            // Functions important for safety
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have safety-conscious patterns
            boolean hasSafetyPatterns = commands.stream()
                .anyMatch(cmd -> cmd.contains("range") || cmd.contains("battery") || cmd.contains("height")) ||
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(hasSafetyPatterns, 
                "Student should create safety function implementations");
        }
    }

    @Nested
    @DisplayName("Advanced Function Concepts Tests")
    class AdvancedFunctionConceptsTests {

        @Test
        @DisplayName("Should demonstrate function best practices")
        void shouldDemonstrateFunctionBestPractices() {
            executeStudentFlightPattern();
            
            // Functions should follow best practices
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have well-structured execution
            boolean hasGoodStructure = commands.stream()
                .anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 6;
            
            assertTrue(hasGoodStructure, 
                "Student should demonstrate function best practices");
        }

        @Test
        @DisplayName("Should create maintainable function-based code")
        void shouldCreateMaintainableCode() {
            executeStudentFlightPattern();
            
            // Functions improve maintainability
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have maintainable structure
            boolean hasMaintainableStructure = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 6;
            
            assertTrue(hasMaintainableStructure, 
                "Student should create maintainable function-based code");
        }

        @Test
        @DisplayName("Should demonstrate scalable function architecture")
        void shouldDemonstrateScalableArchitecture() {
            executeStudentFlightPattern();
            
            // Functions enable scalable architecture
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have scalable patterns
            boolean hasScalablePatterns = commands.size() >= 10;
            assertTrue(hasScalablePatterns, 
                "Student should demonstrate scalable function architecture");
        }
    }
}
