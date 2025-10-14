package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.display.*;

/**
 * Educational example demonstrating CoDrone EDU controller display functionality.
 * 
 * This example shows how to draw various shapes and text on the controller's
 * built-in display screen. The controller has a 128x64 pixel monochrome display
 * that can be used for visual feedback and creative programming exercises.
 * 
 * Display Coordinate System:
 * - X axis: 0 (left) to 127 (right)
 * - Y axis: 0 (top) to 63 (bottom)
 * - Origin (0,0) is at the top-left corner
 * 
 * Key Concepts Demonstrated:
 * - Clearing the display screen
 * - Drawing basic shapes (points, lines, rectangles, circles)
 * - Displaying text with different fonts
 * - Using different pixel types and line styles
 * - Creating simple animations and patterns
 * 
 * @educational This example is designed for educational use in programming classes
 */
public class ControllerDisplayExample {
    
    public static void main(String[] args) {
        try (Drone drone = new Drone()) {
            System.out.println("=== CoDrone EDU Controller Display Demo ===\n");
            
            // Connect to the controller
            System.out.println("Connecting to drone controller...");
            drone.pair();
            System.out.println("Connected!\n");
            
            System.out.println("1. Clearing the display screen...");
            drone.controller_clear_screen();
            sleep(1000);
            
            System.out.println("2. Drawing basic shapes...");
            demonstrateBasicShapes(drone);
            sleep(2000);
            
            System.out.println("3. Creating text displays...");
            demonstrateTextDisplay(drone);
            sleep(2000);
            
            System.out.println("4. Drawing with different line styles...");
            demonstrateLineStyles(drone);
            sleep(2000);
            
            System.out.println("5. Creating a simple pattern...");
            demonstratePattern(drone);
            sleep(2000);
            
            System.out.println("6. Demonstrating pixel inversion...");
            demonstrateInversion(drone);
            sleep(2000);
            
            System.out.println("7. Creating a simple dashboard...");
            demonstrateDashboard(drone);
            sleep(3000);
            
            // Clear screen when done
            drone.controller_clear_screen();
            System.out.println("\n=== Demo Complete ===");
            System.out.println("Display functionality verified successfully!");
            
        } catch (Exception e) {
            System.err.println("Error in display demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates drawing basic geometric shapes.
     */
    private static void demonstrateBasicShapes(Drone drone) {
        // Clear screen first
        drone.controller_clear_screen();
        
        // Draw some points
        drone.controller_draw_point(10, 10);
        drone.controller_draw_point(15, 10);
        drone.controller_draw_point(20, 10);
        
        // Draw lines
        drone.controller_draw_line(30, 5, 50, 15);
        drone.controller_draw_line(30, 15, 50, 5);
        
        // Draw rectangles
        drone.controller_draw_rectangle(60, 5, 20, 15); // Outline
        drone.controller_draw_rectangle(85, 5, 15, 15, DisplayPixel.BLACK, true, DisplayLine.SOLID); // Filled
        
        // Draw circles
        drone.controller_draw_circle(15, 35, 8, DisplayPixel.BLACK, false); // Outline
        drone.controller_draw_circle(40, 35, 8, DisplayPixel.BLACK, true);  // Filled
        
        // Draw text label
        drone.controller_draw_string(5, 50, "Basic Shapes", DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.BLACK);
    }
    
    /**
     * Demonstrates text display with different fonts and positions.
     */
    private static void demonstrateTextDisplay(Drone drone) {
        // Clear screen first
        drone.controller_clear_screen();
        
        // Small font text
        drone.controller_draw_string(5, 5, "Small Font Text");
        drone.controller_draw_string(5, 15, "5x8 Characters");
        
        // Large font text
        drone.controller_draw_string(5, 30, "Large Font", DisplayFont.LIBERATION_MONO_10X16, DisplayPixel.BLACK);
        
        // Text with different pixel types
        drone.controller_draw_string(5, 50, "Regular", DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.BLACK);
        
        // Create a border around the screen
        drone.controller_draw_rectangle(0, 0, 127, 63);
    }
    
    /**
     * Demonstrates different line styles and patterns.
     */
    private static void demonstrateLineStyles(Drone drone) {
        // Clear screen first
        drone.controller_clear_screen();
        
        // Draw lines with different styles
        drone.controller_draw_line(10, 10, 110, 10, DisplayPixel.BLACK, DisplayLine.SOLID);
        drone.controller_draw_string(5, 2, "Solid");
        
        drone.controller_draw_line(10, 20, 110, 20, DisplayPixel.BLACK, DisplayLine.DOTTED);
        drone.controller_draw_string(5, 12, "Dotted");
        
        drone.controller_draw_line(10, 30, 110, 30, DisplayPixel.BLACK, DisplayLine.DASHED);
        drone.controller_draw_string(5, 22, "Dashed");
        
        // Draw rectangles with different line styles
        drone.controller_draw_rectangle(10, 40, 30, 15, DisplayPixel.BLACK, false, DisplayLine.SOLID);
        drone.controller_draw_rectangle(50, 40, 30, 15, DisplayPixel.BLACK, false, DisplayLine.DOTTED);
        drone.controller_draw_rectangle(90, 40, 30, 15, DisplayPixel.BLACK, false, DisplayLine.DASHED);
    }
    
    /**
     * Creates a simple geometric pattern.
     */
    private static void demonstratePattern(Drone drone) {
        // Clear screen first
        drone.controller_clear_screen();
        
        // Create a grid pattern
        for (int x = 10; x < 120; x += 20) {
            drone.controller_draw_line(x, 5, x, 55);
        }
        
        for (int y = 5; y < 60; y += 10) {
            drone.controller_draw_line(10, y, 110, y);
        }
        
        // Add some circles at intersections
        for (int x = 10; x < 120; x += 40) {
            for (int y = 15; y < 50; y += 20) {
                drone.controller_draw_circle(x, y, 3, DisplayPixel.BLACK, true);
            }
        }
        
        // Add title
        drone.controller_draw_string(25, 58, "Grid Pattern");
    }
    
    /**
     * Demonstrates pixel inversion effects.
     */
    private static void demonstrateInversion(Drone drone) {
        // Clear screen first
        drone.controller_clear_screen();
        
        // Draw some content
        drone.controller_draw_rectangle(20, 10, 80, 40, DisplayPixel.BLACK, true, DisplayLine.SOLID);
        drone.controller_draw_string(30, 25, "Original", DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.WHITE);
        
        sleep(1000);
        
        // Invert a portion
        drone.controller_invert_area(40, 20, 40, 20);
        drone.controller_draw_string(10, 55, "Inverted Area");
    }
    
    /**
     * Creates a simple status dashboard display.
     */
    private static void demonstrateDashboard(Drone drone) {
        // Clear screen first
        drone.controller_clear_screen();
        
        // Title bar
        drone.controller_draw_rectangle(0, 0, 127, 12, DisplayPixel.BLACK, true, DisplayLine.SOLID);
        drone.controller_draw_string(30, 2, "CoDrone Status", DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.WHITE);
        
        // Status indicators
        drone.controller_draw_string(5, 18, "Battery: [", DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.BLACK);
        
        // Battery indicator (simple bars)
        for (int i = 0; i < 8; i++) {
            drone.controller_draw_rectangle(53 + (i * 6), 20, 4, 6, DisplayPixel.BLACK, true, DisplayLine.SOLID);
        }
        drone.controller_draw_string(103, 18, "]");
        
        // Connection status
        drone.controller_draw_string(5, 30, "Connection: OK", DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.BLACK);
        
        // Flight mode
        drone.controller_draw_string(5, 42, "Mode: Ready", DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.BLACK);
        
        // Border
        drone.controller_draw_rectangle(0, 0, 127, 63);
    }
    
    /**
     * Helper method for delays between display updates.
     */
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
