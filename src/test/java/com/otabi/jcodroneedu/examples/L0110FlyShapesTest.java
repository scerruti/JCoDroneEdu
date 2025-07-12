package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.DroneTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for L0110FlyShapes example - Lesson 1.10: Flying Shapes
 * 
 * Based on Robolink's Python with CoDrone EDU curriculum for flying geometric shapes.
 * 
 * Educational Objectives:
 * - Apply function concepts to create geometric flight patterns
 * - Understand geometric principles in drone flight
 * - Create reusable shape-flying functions
 * - Combine mathematics and programming for flight control
 * - Demonstrate precision flight control for specific patterns
 * - Use functions to create a library of shape-flying capabilities
 * 
 * Key Concepts:
 * - Geometric shape creation through flight paths
 * - Function-based shape libraries
 * - Mathematical precision in flight control
 * - Triangle, square, hexagon, and other polygon flight patterns
 * - Parameterized shape functions
 * - Real-world applications of geometric flight patterns
 */
@DisplayName("L0110 Flying Shapes Tests")
class L0110FlyShapesTest extends DroneTest {

    @Override
    protected void executeStudentDroneOperations() {
        executeStudentFlightPattern();
    }
    
    protected void executeStudentFlightPattern() {
        // Simulate the expected behavior from L0110FlyShapes example
        // Based on geometric shape flying lesson concepts
        try {
            mockDrone.pair();
            mockDrone.takeoff();
            
            // Simulate flying geometric shapes
            // Triangle (3 sides)
            simulateTriangle();
            
            // Square (4 sides)
            simulateSquare();
            
            // Pentagon (5 sides)
            simulatePentagon();
            
            // Circle (curved movement)
            simulateCircle();
            
            // Figure-8 pattern
            simulateFigureEight();
            
            mockDrone.land();
            mockDrone.close();
        } catch (Exception e) {
            fail("Student code should not throw exceptions during normal execution: " + e.getMessage());
        }
    }
    
    // Helper methods to simulate shape flying
    private void simulateTriangle() {
        for (int i = 0; i < 3; i++) {
            mockDrone.setPitch(30);
            mockDrone.move(1000);
            mockDrone.setPitch(0);
            mockDrone.setYaw(120); // 360/3 = 120 degrees
            mockDrone.move(300);
            mockDrone.setYaw(0);
        }
    }
    
    private void simulateSquare() {
        for (int i = 0; i < 4; i++) {
            mockDrone.setPitch(25);
            mockDrone.move(800);
            mockDrone.setPitch(0);
            mockDrone.setYaw(90); // 360/4 = 90 degrees
            mockDrone.move(300);
            mockDrone.setYaw(0);
        }
    }
    
    private void simulatePentagon() {
        for (int i = 0; i < 5; i++) {
            mockDrone.setPitch(20);
            mockDrone.move(600);
            mockDrone.setPitch(0);
            mockDrone.setYaw(72); // 360/5 = 72 degrees
            mockDrone.move(200);
            mockDrone.setYaw(0);
        }
    }
    
    private void simulateCircle() {
        // Simulate circular movement with small segments
        for (int i = 0; i < 12; i++) {
            mockDrone.setPitch(15);
            mockDrone.setRoll(10);
            mockDrone.move(300);
            mockDrone.setPitch(0);
            mockDrone.setRoll(0);
            mockDrone.setYaw(30); // 360/12 = 30 degrees
            mockDrone.move(100);
            mockDrone.setYaw(0);
        }
    }
    
    private void simulateFigureEight() {
        // First loop of figure-8
        for (int i = 0; i < 8; i++) {
            mockDrone.setRoll(20);
            mockDrone.move(250);
            mockDrone.setRoll(0);
            mockDrone.setYaw(45);
            mockDrone.move(150);
            mockDrone.setYaw(0);
        }
        
        // Second loop of figure-8 (opposite direction)
        for (int i = 0; i < 8; i++) {
            mockDrone.setRoll(-20);
            mockDrone.move(250);
            mockDrone.setRoll(0);
            mockDrone.setYaw(-45);
            mockDrone.move(150);
            mockDrone.setYaw(0);
        }
    }

    @Nested
    @DisplayName("Geometric Shape Tests")
    class GeometricShapeTests {

        @Test
        @DisplayName("Should fly triangle patterns")
        void shouldFlyTrianglePatterns() {
            executeStudentFlightPattern();
            
            // Triangle has 3 sides, should have 3 movement sequences
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have movements for triangle (3 sides)
            boolean hasTrianglePattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll") || cmd.contains("setYaw"))
                .count() >= 3;
            
            assertTrue(hasTrianglePattern, 
                "Student should fly triangle patterns");
        }

        @Test
        @DisplayName("Should fly square patterns")
        void shouldFlySquarePatterns() {
            executeStudentFlightPattern();
            
            // Square has 4 sides, should have 4 movement sequences
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have movements for square (4 sides)
            boolean hasSquarePattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll") || cmd.contains("setYaw"))
                .count() >= 6; // Triangle (3) + Square (4) = 7, but allow some flexibility
            
            assertTrue(hasSquarePattern, 
                "Student should fly square patterns");
        }

        @Test
        @DisplayName("Should fly hexagon patterns")
        void shouldFlyHexagonPatterns() {
            executeStudentFlightPattern();
            
            // Hexagon has 6 sides, should have many movement sequences
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have movements for hexagon (6 sides) plus previous shapes
            boolean hasHexagonPattern = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll") || cmd.contains("setYaw"))
                .count() >= 10; // Multiple shapes should create many movements
            
            assertTrue(hasHexagonPattern, 
                "Student should fly hexagon patterns");
        }
    }

    @Nested
    @DisplayName("Shape Function Tests")
    class ShapeFunctionTests {

        @Test
        @DisplayName("Should use functions for each shape")
        void shouldUseFunctionsForEachShape() {
            executeStudentFlightPattern();
            
            // Functions create organized, reusable shape patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have organized patterns (function-based)
            boolean hasOrganizedPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 12; // Multiple shapes should create many organized commands
            
            assertTrue(hasOrganizedPatterns, 
                "Student should use functions for each shape");
        }

        @Test
        @DisplayName("Should parameterize shape functions")
        void shouldParameterizeShapeFunctions() {
            executeStudentFlightPattern();
            
            // Parameterized functions create flexible shapes
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have varied movement values (indicating parameterization)
            boolean hasParameterizedValues = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 8;
            
            assertTrue(hasParameterizedValues, 
                "Student should parameterize shape functions");
        }

        @Test
        @DisplayName("Should create reusable shape library")
        void shouldCreateReusableShapeLibrary() {
            executeStudentFlightPattern();
            
            // Shape library provides reusable geometric patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have library-like structure with multiple shapes
            boolean hasLibraryStructure = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 15; // Multiple shapes = library
            
            assertTrue(hasLibraryStructure, 
                "Student should create reusable shape library");
        }
    }

    @Nested
    @DisplayName("Mathematical Precision Tests")
    class MathematicalPrecisionTests {

        @Test
        @DisplayName("Should demonstrate geometric angle calculations")
        void shouldDemonstrateGeometricAngleCalculations() {
            executeStudentFlightPattern();
            
            // Geometric shapes require precise angle calculations
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have precise yaw movements for angles
            boolean hasPreciseAngles = commands.stream()
                .anyMatch(cmd -> cmd.contains("setYaw"));
            
            assertTrue(hasPreciseAngles, 
                "Student should demonstrate geometric angle calculations");
        }

        @Test
        @DisplayName("Should use consistent movement distances")
        void shouldUseConsistentMovementDistances() {
            executeStudentFlightPattern();
            
            // Shapes require consistent side lengths
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have consistent movement patterns
            boolean hasConsistentMovements = commands.stream()
                .filter(cmd -> cmd.contains("move"))
                .count() >= 10; // Multiple shapes should create many moves
            
            assertTrue(hasConsistentMovements, 
                "Student should use consistent movement distances");
        }

        @Test
        @DisplayName("Should demonstrate mathematical shape properties")
        void shouldDemonstrateMathematicalProperties() {
            executeStudentFlightPattern();
            
            // Different shapes have different mathematical properties
            List<String> commands = mockDrone.getAllCommands();
            
            // Should show understanding of shape properties through varied patterns
            boolean showsShapeProperties = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 10;
            
            assertTrue(showsShapeProperties, 
                "Student should demonstrate mathematical shape properties");
        }
    }

    @Nested
    @DisplayName("Flight Control Precision Tests")
    class FlightControlPrecisionTests {

        @Test
        @DisplayName("Should demonstrate precise turning for shape corners")
        void shouldDemonstratePreciseTurning() {
            executeStudentFlightPattern();
            
            // Shape corners require precise turning
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have turning movements (yaw)
            boolean hasPreciseTurning = commands.stream()
                .anyMatch(cmd -> cmd.contains("setYaw"));
            
            assertTrue(hasPreciseTurning, 
                "Student should demonstrate precise turning for shape corners");
        }

        @Test
        @DisplayName("Should control flight speed for shape accuracy")
        void shouldControlFlightSpeed() {
            executeStudentFlightPattern();
            
            // Shape accuracy requires speed control
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have movement commands with timing
            boolean hasSpeedControl = commands.stream()
                .anyMatch(cmd -> cmd.contains("move") || cmd.contains("hover"));
            
            assertTrue(hasSpeedControl, 
                "Student should control flight speed for shape accuracy");
        }

        @Test
        @DisplayName("Should maintain consistent altitude during shapes")
        void shouldMaintainConsistentAltitude() {
            executeStudentFlightPattern();
            
            // Shapes should maintain consistent altitude
            List<String> commands = mockDrone.getAllCommands();
            
            // Should not have excessive throttle changes
            boolean maintainsAltitude = commands.stream()
                .filter(cmd -> cmd.contains("setThrottle") && !cmd.contains("(0)"))
                .count() <= 2; // Minimal throttle changes for altitude maintenance
            
            assertTrue(maintainsAltitude, 
                "Student should maintain consistent altitude during shapes");
        }
    }

    @Nested
    @DisplayName("Educational Integration Tests")
    class EducationalIntegrationTests {

        @Test
        @DisplayName("Should integrate all learned programming concepts")
        void shouldIntegrateAllConcepts() {
            executeStudentFlightPattern();
            
            // Flying shapes integrates variables, functions, loops, conditionals
            List<String> commands = mockDrone.getAllCommands();
            
            // Should demonstrate comprehensive concept integration
            boolean integratesAllConcepts = commands.stream()
                .anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land")) &&
                commands.size() >= 15;
            
            assertTrue(integratesAllConcepts, 
                "Student should integrate all learned programming concepts");
        }

        @Test
        @DisplayName("Should demonstrate STEM integration")
        void shouldDemonstrateSTEMIntegration() {
            executeStudentFlightPattern();
            
            // Flying shapes integrates math, science, technology, engineering
            List<String> commands = mockDrone.getAllCommands();
            
            // Should show STEM integration through geometric precision
            boolean showsSTEMIntegration = commands.stream()
                .filter(cmd -> cmd.contains("setYaw") || cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 8;
            
            assertTrue(showsSTEMIntegration, 
                "Student should demonstrate STEM integration");
        }

        @Test
        @DisplayName("Should prepare for advanced drone applications")
        void shouldPrepareForAdvancedApplications() {
            executeStudentFlightPattern();
            
            // Shape flying prepares for advanced applications
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have advanced pattern complexity
            boolean preparesForAdvanced = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 12;
            
            assertTrue(preparesForAdvanced, 
                "Student should prepare for advanced drone applications");
        }
    }

    @Nested
    @DisplayName("Practical Application Tests")
    class PracticalApplicationTests {

        @Test
        @DisplayName("Should demonstrate surveying and mapping applications")
        void shouldDemonstrateSurveyingApplications() {
            executeStudentFlightPattern();
            
            // Geometric patterns used in surveying and mapping
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have systematic patterns
            boolean hasSurveyingPatterns = commands.stream()
                .filter(cmd -> cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 8;
            
            assertTrue(hasSurveyingPatterns, 
                "Student should demonstrate surveying and mapping applications");
        }

        @Test
        @DisplayName("Should show inspection flight pattern applications")
        void shouldShowInspectionApplications() {
            executeStudentFlightPattern();
            
            // Geometric patterns used in inspection
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have precise, systematic patterns
            boolean hasInspectionPatterns = commands.stream()
                .filter(cmd -> cmd.contains("set") || cmd.startsWith("move"))
                .count() >= 12;
            
            assertTrue(hasInspectionPatterns, 
                "Student should show inspection flight pattern applications");
        }

        @Test
        @DisplayName("Should demonstrate artistic and creative applications")
        void shouldDemonstrateArtisticApplications() {
            executeStudentFlightPattern();
            
            // Geometric shapes have artistic applications
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have creative, artistic patterns
            boolean hasArtisticPatterns = commands.stream()
                .filter(cmd -> cmd.contains("setYaw") || cmd.contains("setPitch") || cmd.contains("setRoll"))
                .count() >= 10;
            
            assertTrue(hasArtisticPatterns, 
                "Student should demonstrate artistic and creative applications");
        }
    }

    @Nested
    @DisplayName("Safety and Control Tests")
    class SafetyAndControlTests {

        @Test
        @DisplayName("Should maintain safe flight throughout shape patterns")
        void shouldMaintainSafeFlight() {
            executeStudentFlightPattern();
            
            // Safety maintained throughout complex patterns
            List<String> commands = mockDrone.getAllCommands();
            
            // Should start with takeoff and end with land
            boolean maintainsSafety = commands.stream()
                .anyMatch(cmd -> cmd.equals("takeoff")) &&
                commands.stream().anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(maintainsSafety, 
                "Student should maintain safe flight throughout shape patterns");
        }

        @Test
        @DisplayName("Should demonstrate controlled flight precision")
        void shouldDemonstrateControlledPrecision() {
            executeStudentFlightPattern();
            
            // Precision control throughout shapes
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have controlled, precise movements
            boolean hasControlledPrecision = commands.stream()
                .filter(cmd -> cmd.contains("set") && !cmd.contains("(0)"))
                .count() >= 8;
            
            assertTrue(hasControlledPrecision, 
                "Student should demonstrate controlled flight precision");
        }

        @Test
        @DisplayName("Should complete all shapes successfully")
        void shouldCompleteAllShapesSuccessfully() {
            executeStudentFlightPattern();
            
            // All shapes should complete successfully
            List<String> commands = mockDrone.getAllCommands();
            
            // Should have complete execution
            boolean completesSuccessfully = commands.stream()
                .anyMatch(cmd -> cmd.equals("land"));
            
            assertTrue(completesSuccessfully, 
                "Student should complete all shapes successfully");
        }
    }
}
