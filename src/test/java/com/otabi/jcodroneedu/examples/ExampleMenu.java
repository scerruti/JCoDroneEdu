package com.otabi.jcodroneedu.examples;

import java.util.Scanner;

/**
 * Interactive menu system for running CoDrone EDU examples.
 * 
 * This program provides a centralized menu interface to launch any of the
 * available example programs. Examples are organized into categories:
 * - Educational lessons (L01xx series)
 * - Feature demonstrations
 * - Testing and diagnostic tools
 * 
 * Usage:
 *   Run this program and select an example by entering its number.
 *   Press 0 to exit the menu.
 * 
 * @educational This menu makes it easy to explore the library's capabilities
 */
public class ExampleMenu {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        boolean running = true;
        
        while (running) {
            displayMenu();
            
            System.out.print("\nEnter your choice (0 to exit): ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            try {
                int choice = Integer.parseInt(input);
                
                if (choice == 0) {
                    System.out.println("\nExiting Example Menu. Goodbye!");
                    running = false;
                } else {
                    runExample(choice);
                }
            } catch (NumberFormatException e) {
                System.out.println("\n❌ Invalid input. Please enter a number.");
            }
            
            if (running) {
                System.out.println("\nPress Enter to return to menu...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    private static void displayMenu() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           CoDrone EDU Example Menu - JCoDroneEdu              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("📚 EDUCATIONAL LESSONS (Beginner Friendly)");
        System.out.println("────────────────────────────────────────────────────────────────");
        System.out.println("  1. L01.01 - First Flight (Basic takeoff and landing)");
        System.out.println("  2. L01.02 - Flight Movements (Forward, backward, left, right)");
        System.out.println("  3. L01.03 - Flight Movements (Up and down)");
        System.out.println("  4. L01.03 - Turning Navigation (Rotating the drone)");
        System.out.println("  5. L01.04 - Variables (Using variables in flight)");
        System.out.println("  6. L01.05 - Variables (More variable practice)");
        System.out.println("  7. L01.06 - Conditionals (If statements in flight)");
        System.out.println("  8. L01.07 - For Loops (Repetitive flight patterns)");
        System.out.println("  9. L01.08 - While Loops (Conditional flight loops)");
        System.out.println(" 10. L01.09 - Nested Loops (Complex patterns)");
        System.out.println(" 11. L01.10 - Functions (Creating reusable flight code)");
        System.out.println(" 12. L01.10 - Fly Shapes (Flying geometric patterns)");
        System.out.println();
        
        System.out.println("🚁 FEATURE DEMONSTRATIONS (No Flight Required)");
        System.out.println("────────────────────────────────────────────────────────────────");
        System.out.println(" 20. Buzzer Example (Sound and music)");
        System.out.println(" 21. Controller Display Example (OLED graphics)");
        System.out.println(" 22. Controller Input Example (Button reading)");
        System.out.println(" 23. Optical Flow Example (Position tracking)");
        System.out.println(" 24. Reset and Trim Example (Configuration)");
        System.out.println();
        
        System.out.println("📊 SENSORS & MONITORING (No Flight Required)");
        System.out.println("────────────────────────────────────────────────────────────────");
        System.out.println(" 30. Sensor Display (CLI sensor monitor)");
        System.out.println(" 31. Sensor Display GUI (Graphical sensor monitor)");
        System.out.println(" 32. Motion Dump (Raw motion data)");
        System.out.println();
        
        System.out.println("🧪 TESTING & DIAGNOSTICS");
        System.out.println("────────────────────────────────────────────────────────────────");
        System.out.println(" 40. Smoke Test (Basic connectivity test)");
        System.out.println(" 41. Accel Test (Accelerometer monitor)");
        System.out.println(" 42. Multi Sensor Test (Range, flow, temp, color)");
        System.out.println(" 43. Quick LED Test (Test drone & controller LEDs)");
        System.out.println(" 44. Conservative Flight Test (Safe flight verification)");
        System.out.println(" 45. Test Harness (Interactive feature testing)");
        System.out.println();
        
        System.out.println("  0. Exit");
        System.out.println("════════════════════════════════════════════════════════════════");
    }
    
    private static void runExample(int choice) {
        System.out.println("\n" + "═".repeat(64));
        
        try {
            // Special handling for ConservativeFlight which uses System.exit()
            if (choice == 44) {
                System.out.println("🚁 Conservative Flight Test");
                System.out.println("═".repeat(64) + "\n");
                System.out.println("⚠️  Note: This example requires --allow-flight flag to actually fly.");
                System.out.println("    When run from the menu, it will display usage information only.");
                System.out.println("    To fly safely, use: ./gradlew runConservativeFlight --args='--allow-flight'\n");
                System.out.println("═".repeat(64));
                System.out.println("💡 Example information displayed (use Gradle task to fly)");
                return;
            }
            
            switch (choice) {
                // Educational Lessons
                case 1:
                    System.out.println("🚀 Running L01.01 - First Flight");
                    System.out.println("═".repeat(64) + "\n");
                    L0101FirstFlight.main(new String[]{});
                    break;
                    
                case 2:
                    System.out.println("🚀 Running L01.02 - Flight Movements");
                    System.out.println("═".repeat(64) + "\n");
                    L0102FlightMovements.main(new String[]{});
                    break;
                    
                case 3:
                    System.out.println("🚀 Running L01.03 - Flight Movements (Up/Down)");
                    System.out.println("═".repeat(64) + "\n");
                    L0103FlightMovements.main(new String[]{});
                    break;
                    
                case 4:
                    System.out.println("🚀 Running L01.03 - Turning Navigation");
                    System.out.println("═".repeat(64) + "\n");
                    L0103TurningNavigation.main(new String[]{});
                    break;
                    
                case 5:
                    System.out.println("🚀 Running L01.04 - Variables");
                    System.out.println("═".repeat(64) + "\n");
                    L0104Variables.main(new String[]{});
                    break;
                    
                case 6:
                    System.out.println("🚀 Running L01.05 - Variables");
                    System.out.println("═".repeat(64) + "\n");
                    L0105Variables.main(new String[]{});
                    break;
                    
                case 7:
                    System.out.println("🚀 Running L01.06 - Conditionals");
                    System.out.println("═".repeat(64) + "\n");
                    L0106Conditionals.main(new String[]{});
                    break;
                    
                case 8:
                    System.out.println("🚀 Running L01.07 - For Loops");
                    System.out.println("═".repeat(64) + "\n");
                    L0107ForLoops.main(new String[]{});
                    break;
                    
                case 9:
                    System.out.println("🚀 Running L01.08 - While Loops");
                    System.out.println("═".repeat(64) + "\n");
                    L0108WhileLoops.main(new String[]{});
                    break;
                    
                case 10:
                    System.out.println("🚀 Running L01.09 - Nested Loops");
                    System.out.println("═".repeat(64) + "\n");
                    L0109NestedLoops.main(new String[]{});
                    break;
                    
                case 11:
                    System.out.println("🚀 Running L01.10 - Functions");
                    System.out.println("═".repeat(64) + "\n");
                    L0110Functions.main(new String[]{});
                    break;
                    
                case 12:
                    System.out.println("🚀 Running L01.10 - Fly Shapes");
                    System.out.println("═".repeat(64) + "\n");
                    L0110FlyShapes.main(new String[]{});
                    break;
                
                // Feature Demonstrations
                case 20:
                    System.out.println("🔊 Running Buzzer Example");
                    System.out.println("═".repeat(64) + "\n");
                    BuzzerExample.main(new String[]{});
                    break;
                    
                case 21:
                    System.out.println("🖥️  Running Controller Display Example");
                    System.out.println("═".repeat(64) + "\n");
                    ControllerDisplayExample.main(new String[]{});
                    break;
                    
                case 22:
                    System.out.println("🎮 Running Controller Input Example");
                    System.out.println("═".repeat(64) + "\n");
                    ControllerInputExample.main(new String[]{});
                    break;
                    
                case 23:
                    System.out.println("📍 Running Optical Flow Example");
                    System.out.println("═".repeat(64) + "\n");
                    OpticalFlowExample.main(new String[]{});
                    break;
                    
                case 24:
                    System.out.println("⚙️  Running Reset and Trim Example");
                    System.out.println("═".repeat(64) + "\n");
                    ResetAndTrimExample.main(new String[]{});
                    break;
                
                // Sensors & Monitoring
                case 30:
                    System.out.println("📊 Running Sensor Display (CLI)");
                    System.out.println("═".repeat(64) + "\n");
                    SensorDisplay.main(new String[]{});
                    break;
                    
                case 31:
                    System.out.println("📊 Running Sensor Display GUI");
                    System.out.println("═".repeat(64) + "\n");
                    SensorDisplayGui.main(new String[]{});
                    break;
                    
                case 32:
                    System.out.println("📊 Running Motion Dump");
                    System.out.println("═".repeat(64) + "\n");
                    MotionDump.main(new String[]{});
                    break;
                
                // Testing & Diagnostics
                case 40:
                    System.out.println("🧪 Running Smoke Test");
                    System.out.println("═".repeat(64) + "\n");
                    com.otabi.jcodroneedu.examples.tests.SmokeTest.main(new String[]{});
                    break;
                    
                case 41:
                    System.out.println("🧪 Running Accel Test");
                    System.out.println("═".repeat(64) + "\n");
                    com.otabi.jcodroneedu.examples.tests.AccelTest.main(new String[]{});
                    break;
                    
                case 42:
                    System.out.println("🧪 Running Multi Sensor Test");
                    System.out.println("═".repeat(64) + "\n");
                    com.otabi.jcodroneedu.examples.tests.MultiSensorTest.main(new String[]{});
                    break;
                    
                case 43:
                    System.out.println("💡 Running Quick LED Test");
                    System.out.println("═".repeat(64) + "\n");
                    com.otabi.jcodroneedu.examples.tests.QuickLEDTest.main(new String[]{});
                    break;
                    
                case 45:
                    System.out.println("🔧 Running Test Harness");
                    System.out.println("═".repeat(64) + "\n");
                    com.otabi.jcodroneedu.examples.tests.TestHarness.main(new String[]{});
                    break;
                
                default:
                    System.out.println("❌ Invalid choice. Please select a valid option.");
                    System.out.println("═".repeat(64));
                    return;
            }
            
            System.out.println("\n" + "═".repeat(64));
            System.out.println("✅ Example completed successfully!");
            
        } catch (Exception e) {
            System.out.println("\n" + "═".repeat(64));
            System.out.println("❌ Error running example: " + e.getMessage());
            System.err.println("\nStack trace:");
            e.printStackTrace();
        }
    }
    
    /**
     * Attempts to clear the terminal screen for a cleaner menu display.
     * Works on most Unix-like systems and Windows.
     */
    private static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                // Windows - note: this may not work in all terminals
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Unix-like (macOS, Linux)
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing fails, just print some newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
