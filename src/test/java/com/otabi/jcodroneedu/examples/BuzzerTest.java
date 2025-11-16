package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.buzzer.BuzzerSequence;
import com.otabi.jcodroneedu.protocol.buzzer.Note;

/**
 * Comprehensive buzzer test program.
 * Tests drone and controller buzzers with notes, frequencies, and sequences.
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
                drone.droneBuzzer(note, 500);  // 500ms duration
                Thread.sleep(600);  // Wait between notes
            }
            
            // Test 2: Drone buzzer with frequencies
            System.out.println("\nTest 2: Drone buzzer - Frequencies (Hz)");
            int[] frequencies = {440, 880, 1320, 1760};  // A notes in different octaves
            for (int freq : frequencies) {
                System.out.println("  Playing: " + freq + " Hz");
                drone.droneBuzzer(freq, 500);
                Thread.sleep(600);
            }
            
            // Test 3: Controller buzzer
            System.out.println("\nTest 3: Controller buzzer - Simple melody");
            System.out.println("  Playing melody on controller...");
            drone.controllerBuzzer(Note.C4, 300);
            Thread.sleep(350);
            drone.controllerBuzzer(Note.E4, 300);
            Thread.sleep(350);
            drone.controllerBuzzer(Note.G4, 300);
            Thread.sleep(350);
            drone.controllerBuzzer(Note.C5, 600);
            Thread.sleep(700);
            
            // Test 4: Different durations
            System.out.println("\nTest 4: Duration test (same note, different lengths)");
            int[] durations = {100, 250, 500, 1000};
            for (int duration : durations) {
                System.out.println("  Duration: " + duration + "ms");
                drone.droneBuzzer(Note.A4, duration);  // A4 = 440 Hz
                Thread.sleep(duration + 200);
            }
            
            // Test 5: Volume test (if supported - using different frequencies)
            System.out.println("\nTest 5: Frequency range test");
            System.out.println("  Low frequency (200 Hz)");
            drone.droneBuzzer(200, 1000);
            Thread.sleep(1200);
            
            System.out.println("  Mid frequency (1000 Hz)");
            drone.droneBuzzer(1000, 1000);
            Thread.sleep(1200);
            
            System.out.println("  High frequency (4000 Hz)");
            drone.droneBuzzer(4000, 1000);
            Thread.sleep(1200);
            
            // Test 6: Built-in buzzer sequences
            System.out.println("\nTest 6: Built-in buzzer sequences");
            
            System.out.println("  Playing 'success' on drone...");
            drone.droneBuzzerSequence("success");
            Thread.sleep(1000);
            
            System.out.println("  Playing 'warning' on drone...");
            drone.droneBuzzerSequence("warning");
            Thread.sleep(1000);
            
            System.out.println("  Playing 'error' on drone...");
            drone.droneBuzzerSequence("error");
            Thread.sleep(1000);
            
            System.out.println("  Playing 'success' on controller...");
            drone.controllerBuzzerSequence("success");
            Thread.sleep(1000);
            
            // Test 7: Custom buzzer sequences
            System.out.println("\nTest 7: Custom buzzer sequences");
            
            // Create a simple melody
            System.out.println("  Creating custom melody...");
            BuzzerSequence melody = new BuzzerSequence.Builder()
                .addNote(523, 200)  // C5
                .addPause(50)
                .addNote(659, 200)  // E5
                .addPause(50)
                .addNote(784, 400)  // G5
                .build("custom-melody");
            
            drone.registerBuzzerSequence("custom-melody", melody);
            System.out.println("  Playing custom melody on drone...");
            drone.droneBuzzerSequence("custom-melody");
            Thread.sleep(1500);
            
            // Create a more complex sequence
            System.out.println("  Creating Star Wars theme intro...");
            BuzzerSequence starWars = new BuzzerSequence.Builder()
                .addNote(440, 500)   // A
                .addNote(440, 500)   // A
                .addNote(440, 500)   // A
                .addNote(349, 350)   // F
                .addPause(150)
                .addNote(523, 150)   // C
                .addNote(440, 500)   // A
                .addNote(349, 350)   // F
                .addPause(150)
                .addNote(523, 150)   // C
                .addNote(440, 1000)  // A (long)
                .build("starwars");
            
            drone.registerBuzzerSequence("starwars", starWars);
            System.out.println("  Playing Star Wars on drone...");
            drone.droneBuzzerSequence("starwars");
            Thread.sleep(5000);
            
            // Test 8: Alarm sequence
            System.out.println("\nTest 8: Alarm sequence");
            BuzzerSequence alarm = new BuzzerSequence.Builder()
                .addNote(1000, 100)
                .addPause(50)
                .addNote(1000, 100)
                .addPause(50)
                .addNote(1000, 100)
                .addPause(200)
                .addNote(1500, 300)
                .build("alarm");
            
            drone.registerBuzzerSequence("alarm", alarm);
            System.out.println("  Playing alarm on controller...");
            drone.controllerBuzzerSequence("alarm");
            Thread.sleep(2000);
            
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
