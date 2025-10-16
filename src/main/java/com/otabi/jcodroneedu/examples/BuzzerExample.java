package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Educational example showing how to use the CoDrone EDU buzzer API
 * 
 * This example demonstrates various buzzer methods that students can use
 * to add audio feedback to their drone programs.
 */
public class BuzzerExample {
    public static void main(String[] args) {
        System.out.println("CoDrone EDU Buzzer API - Educational Example");
        System.out.println("============================================");
        System.out.println();
        
        // Note: This example shows the API calls but doesn't actually connect to hardware
        // In a real classroom scenario, students would have their drones connected
        
        try {
            // Create drone instance (this would connect to hardware in real use)
            Drone drone = new Drone();
            
            System.out.println("Example 1: Playing individual notes");
            System.out.println("// Play a C note on the drone for 500ms");
            System.out.println("drone.drone_buzzer(Note.C4, 500);");
            
            System.out.println();
            System.out.println("Example 2: Playing frequencies");
            System.out.println("// Play 440 Hz (A note) on controller for 1 second");
            System.out.println("drone.controller_buzzer(440, 1000);");
            
            System.out.println();
            System.out.println("Example 3: Continuous buzzer control");
            System.out.println("// Start a buzzer and stop it later");
            System.out.println("drone.start_drone_buzzer(Note.G4);");
            System.out.println("// ... do other drone operations ...");
            System.out.println("drone.stop_drone_buzzer();");
            
            System.out.println();
            System.out.println("Example 4: Simple melody");
            System.out.println("// Play a simple scale");
            System.out.println("Note[] scale = {Note.C4, Note.D4, Note.E4, Note.F4, Note.G4, Note.A4, Note.B4, Note.C5};");
            System.out.println("for (Note note : scale) {");
            System.out.println("    drone.drone_buzzer(note, 300);");
            System.out.println("    Thread.sleep(50); // Short pause between notes");
            System.out.println("}");
            
            System.out.println();
            System.out.println("Example 5: Audio feedback for flight events");
            System.out.println("// Take off with audio confirmation");
            System.out.println("drone.takeoff();");
            System.out.println("drone.controller_buzzer(Note.C5, 200); // Success sound");
            System.out.println();
            System.out.println("// Landing with audio confirmation");
            System.out.println("drone.land();");
            System.out.println("drone.controller_buzzer(Note.C4, 500); // Landing sound");
            
            System.out.println();
            System.out.println("Example 6: Alarm or warning sounds");
            System.out.println("// Battery low warning");
            System.out.println("for (int i = 0; i < 3; i++) {");
            System.out.println("    drone.controller_buzzer(800, 100); // High pitched beep");
            System.out.println("    Thread.sleep(100);");
            System.out.println("}");
            
            System.out.println();
            System.out.println("✅ The buzzer API provides rich audio feedback capabilities!");
            System.out.println("✅ Students can now create more engaging and accessible drone programs!");
            System.out.println("✅ Perfect for classroom demonstrations and student projects!");
            
            // Close the drone connection (in real use)
            drone.close();
            
        } catch (Exception e) {
            System.err.println("Error in buzzer example: " + e.getMessage());
        }
    }
}
