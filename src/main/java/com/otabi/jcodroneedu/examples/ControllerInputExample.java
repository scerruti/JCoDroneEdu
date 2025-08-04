package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Educational example showing how to use the CoDrone EDU controller input API
 * 
 * This example demonstrates how students can use controller buttons and joysticks
 * to create interactive drone programs with user input.
 */
public class ControllerInputExample {
    public static void main(String[] args) {
        System.out.println("CoDrone EDU Controller Input API - Educational Example");
        System.out.println("=====================================================");
        System.out.println();
        
        // Note: This example shows the API calls but doesn't actually connect to hardware
        // In a real classroom scenario, students would have their controllers connected
        
        try {
            // Create drone instance (this would connect to hardware in real use)
            Drone drone = new Drone();
            
            System.out.println("Example 1: Reading Controller Buttons");
            System.out.println("// Check if specific buttons are pressed");
            System.out.println("if (drone.power_pressed()) {");
            System.out.println("    drone.takeoff();  // Power button starts flight");
            System.out.println("}");
            System.out.println();
            System.out.println("if (drone.up_arrow_pressed()) {");
            System.out.println("    drone.go(\"up\", 30, 1);  // Up arrow moves drone up");
            System.out.println("}");
            System.out.println();
            System.out.println("if (drone.down_arrow_pressed()) {");
            System.out.println("    drone.land();  // Down arrow lands drone");
            System.out.println("}");
            
            System.out.println();
            System.out.println("Example 2: Manual Flight Control with Joysticks");
            System.out.println("// Use joysticks for precise manual control");
            System.out.println("int leftX = drone.get_left_joystick_x();    // Range: -100 to 100");
            System.out.println("int leftY = drone.get_left_joystick_y();    // Range: -100 to 100");
            System.out.println("int rightX = drone.get_right_joystick_x();  // Range: -100 to 100");
            System.out.println("int rightY = drone.get_right_joystick_y();  // Range: -100 to 100");
            System.out.println();
            System.out.println("// Convert joystick values to drone movement");
            System.out.println("if (Math.abs(leftY) > 10) {  // Dead zone of 10");
            System.out.println("    int power = Math.abs(leftY);");
            System.out.println("    String direction = leftY > 0 ? \"forward\" : \"backward\";");
            System.out.println("    drone.go(direction, power, 0.1);");
            System.out.println("}");
            
            System.out.println();
            System.out.println("Example 3: Interactive Drone Games");
            System.out.println("// Create a simple drone control game");
            System.out.println("while (true) {");
            System.out.println("    // Emergency stop");
            System.out.println("    if (drone.h_pressed()) {");
            System.out.println("        drone.emergency_stop();");
            System.out.println("        break;");
            System.out.println("    }");
            System.out.println();
            System.out.println("    // LED control with buttons");
            System.out.println("    if (drone.l1_pressed()) {");
            System.out.println("        drone.setDroneLEDRed();    // L1 = Red");
            System.out.println("    } else if (drone.r1_pressed()) {");
            System.out.println("        drone.setDroneLEDBlue();   // R1 = Blue");
            System.out.println("    }");
            System.out.println();
            System.out.println("    // Sound control with arrows");
            System.out.println("    if (drone.left_arrow_pressed()) {");
            System.out.println("        drone.controller_buzzer(440, 200);  // A note");
            System.out.println("    } else if (drone.right_arrow_pressed()) {");
            System.out.println("        drone.controller_buzzer(880, 200);  // High A note");
            System.out.println("    }");
            System.out.println();
            System.out.println("    Thread.sleep(50);  // Small delay");
            System.out.println("}");
            
            System.out.println();
            System.out.println("Example 4: Advanced Controller Data Access");
            System.out.println("// Get complete button and joystick data");
            System.out.println("Object[] buttonData = drone.get_button_data();");
            System.out.println("// buttonData[0] = timestamp");
            System.out.println("// buttonData[1] = button flags (integer)");
            System.out.println("// buttonData[2] = event name (\"Press\", \"Down\", \"Up\")");
            System.out.println();
            System.out.println("int[] joystickData = drone.get_joystick_data();");
            System.out.println("// joystickData[0] = timestamp");
            System.out.println("// joystickData[1] = left X, joystickData[2] = left Y");
            System.out.println("// joystickData[5] = right X, joystickData[6] = right Y");
            
            System.out.println();
            System.out.println("Example 5: Input Validation and Safety");
            System.out.println("// Always validate input and implement safety features");
            System.out.println("if (drone.s_pressed() && drone.p_pressed()) {");
            System.out.println("    // Both S and P pressed = safety confirmation");
            System.out.println("    System.out.println(\"Safety mode activated\");");
            System.out.println("    drone.land();");
            System.out.println("}");
            System.out.println();
            System.out.println("// Check joystick is in neutral position before takeoff");
            System.out.println("boolean joysticksNeutral = Math.abs(drone.get_left_joystick_x()) < 5 &&");
            System.out.println("                          Math.abs(drone.get_left_joystick_y()) < 5 &&");
            System.out.println("                          Math.abs(drone.get_right_joystick_x()) < 5 &&");
            System.out.println("                          Math.abs(drone.get_right_joystick_y()) < 5;");
            System.out.println("if (joysticksNeutral && drone.power_pressed()) {");
            System.out.println("    drone.takeoff();");
            System.out.println("} else {");
            System.out.println("    System.out.println(\"Please center joysticks before takeoff\");");
            System.out.println("}");
            
            System.out.println();
            System.out.println("Available Controller Input Methods:");
            System.out.println("=================================");
            System.out.println("Button Detection:");
            System.out.println("  • l1_pressed(), l2_pressed(), r1_pressed(), r2_pressed()");
            System.out.println("  • h_pressed(), power_pressed()");
            System.out.println("  • up_arrow_pressed(), down_arrow_pressed()");
            System.out.println("  • left_arrow_pressed(), right_arrow_pressed()");
            System.out.println("  • s_pressed(), p_pressed()");
            System.out.println();
            System.out.println("Joystick Values:");
            System.out.println("  • get_left_joystick_x(), get_left_joystick_y()");
            System.out.println("  • get_right_joystick_x(), get_right_joystick_y()");
            System.out.println();
            System.out.println("Advanced Data:");
            System.out.println("  • get_button_data() - Complete button state information");
            System.out.println("  • get_joystick_data() - Complete joystick state information");
            
            System.out.println();
            System.out.println("✅ Controller input enables interactive drone programming!");
            System.out.println("✅ Perfect for classroom engagement and user interaction!");
            System.out.println("✅ Students can create games, manual control, and responsive behaviors!");
            
            // Close the drone connection (in real use)
            drone.close();
            
        } catch (Exception e) {
            System.err.println("Error in controller input example: " + e.getMessage());
        }
    }
}
