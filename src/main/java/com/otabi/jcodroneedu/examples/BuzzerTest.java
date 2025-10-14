package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.buzzer.Note;

/**
 * Simple buzzer test program.
 * Tests both drone and controller buzzers with various notes and frequencies.
 */
public class BuzzerTest {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        try {
            System.out.println("CoDrone EDU Buzzer Test");
            System.out.println("=======================");
            
            // Connect to drone
            System.out.println("\nConnecting to drone...");
            drone.pair();
            Thread.sleep(1000);
            System.out.println("Connected!");
            
            // Test 1: Drone buzzer with scale notes (using Note enum)
            System.out.println("\nTest 1: Drone buzzer - Musical scale (C4-C5)");
            Note[] notes = {Note.C4, Note.D4, Note.E4, Note.F4, Note.G4, Note.A4, Note.B4, Note.C5};
            for (Note note : notes) {
                System.out.println("  Playing: " + note + " (" + String.format("%.1f", note.getFrequency()) + " Hz)");
                drone.drone_buzzer(note, 500);  // 500ms duration
                Thread.sleep(600);  // Wait between notes
            }
            
            // Test 2: Drone buzzer with frequencies
            System.out.println("\nTest 2: Drone buzzer - Frequencies (Hz)");
            int[] frequencies = {440, 880, 1320, 1760};  // A notes in different octaves
            for (int freq : frequencies) {
                System.out.println("  Playing: " + freq + " Hz");
                drone.drone_buzzer(freq, 500);
                Thread.sleep(600);
            }
            
            // Test 3: Controller buzzer
            System.out.println("\nTest 3: Controller buzzer - Simple melody");
            System.out.println("  Playing melody on controller...");
            drone.controller_buzzer(Note.C4, 300);
            Thread.sleep(350);
            drone.controller_buzzer(Note.E4, 300);
            Thread.sleep(350);
            drone.controller_buzzer(Note.G4, 300);
            Thread.sleep(350);
            drone.controller_buzzer(Note.C5, 600);
            Thread.sleep(700);
            
            // Test 4: Different durations
            System.out.println("\nTest 4: Duration test (same note, different lengths)");
            int[] durations = {100, 250, 500, 1000};
            for (int duration : durations) {
                System.out.println("  Duration: " + duration + "ms");
                drone.drone_buzzer(Note.A4, duration);  // A4 = 440 Hz
                Thread.sleep(duration + 200);
            }
            
            // Test 5: Volume test (if supported - using different frequencies)
            System.out.println("\nTest 5: Frequency range test");
            System.out.println("  Low frequency (200 Hz)");
            drone.drone_buzzer(200, 1000);
            Thread.sleep(1200);
            
            System.out.println("  Mid frequency (1000 Hz)");
            drone.drone_buzzer(1000, 1000);
            Thread.sleep(1200);
            
            System.out.println("  High frequency (4000 Hz)");
            drone.drone_buzzer(4000, 1000);
            Thread.sleep(1200);
            
            System.out.println("\nBuzzer test complete!");
            
        } catch (InterruptedException e) {
            System.err.println("Test interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during buzzer test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nClosing connection...");
            drone.close();
        }
    }
}
