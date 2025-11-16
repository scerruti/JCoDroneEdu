package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.buzzer.Note;

/**
 * Simple test to play a single note repeatedly.
 * Useful for testing if a specific note/frequency is audible.
 */
public class SingleNoteTest {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        try {
            System.out.println("Single Note Test - G4 (392 Hz)");
            System.out.println("================================");
            
            // Connect to drone
            System.out.println("\nConnecting to drone...");
            drone.pair();
            Thread.sleep(1000);
            System.out.println("Connected!");
            
            // Play G4 (392 Hz) 5 times
            System.out.println("\nPlaying G4 (392.0 Hz) 5 times...");
            System.out.println("Listen carefully for the buzzer sound.\n");
            
            for (int i = 1; i <= 5; i++) {
                System.out.println("Playing #" + i + " - G4 (392.0 Hz) for 1 second");
                drone.droneBuzzer(Note.G4, 1000);  // 1 second duration
                Thread.sleep(1500);  // Wait 1.5 seconds between plays
            }
            
            System.out.println("\nNow testing with raw frequency (392 Hz)...");
            for (int i = 1; i <= 5; i++) {
                System.out.println("Playing #" + i + " - 392 Hz for 1 second");
                drone.droneBuzzer(392, 1000);
                Thread.sleep(1500);
            }
            
            System.out.println("\nTest complete!");
            
        } catch (InterruptedException e) {
            System.err.println("Test interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nClosing connection...");
            drone.close();
        }
    }
}
