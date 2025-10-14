package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.protocol.buzzer.Note;

/**
 * Melody player example for CoDrone EDU.
 * Demonstrates how to play musical sequences using the buzzer.
 * 
 * To create your own melody:
 * 1. Define arrays of notes and durations
 * 2. Each note corresponds to a duration at the same index
 * 3. Add pauses between notes for better rhythm
 */
public class MelodyPlayer {
    
    /**
     * Plays a sequence of notes on the drone buzzer.
     * 
     * @param drone The drone instance
     * @param notes Array of notes to play
     * @param durations Array of durations (in ms) for each note
     * @param pausePercent Percentage of duration to pause between notes (0-100)
     */
    public static void playMelody(Drone drone, Note[] notes, int[] durations, int pausePercent) 
            throws InterruptedException {
        if (notes.length != durations.length) {
            throw new IllegalArgumentException("Notes and durations arrays must have same length");
        }
        
        for (int i = 0; i < notes.length; i++) {
            if (notes[i] == Note.MUTE) {
                // Rest (silence)
                Thread.sleep(durations[i]);
            } else {
                // Play note
                drone.drone_buzzer(notes[i], durations[i]);
                // Pause between notes (percentage of note duration)
                int pause = durations[i] + (durations[i] * pausePercent / 100);
                Thread.sleep(pause);
            }
        }
    }
    
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        try {
            System.out.println("CoDrone EDU Melody Player");
            System.out.println("=========================");
            
            // Connect to drone
            System.out.println("\nConnecting to drone...");
            drone.pair();
            Thread.sleep(1000);
            System.out.println("Connected!");
            
            // Example melody 1: Simple ascending pattern
            System.out.println("\nPlaying Example Melody 1...");
            Note[] melody1 = {
                Note.C4, Note.D4, Note.E4, Note.F4, 
                Note.G4, Note.A4, Note.B4, Note.C5
            };
            int[] durations1 = {
                400, 400, 400, 400,
                400, 400, 400, 600
            };
            playMelody(drone, melody1, durations1, 10);
            
            Thread.sleep(1000);
            
            // Example melody 2: Pattern with rests
            System.out.println("\nPlaying Example Melody 2...");
            Note[] melody2 = {
                Note.C4, Note.E4, Note.G4, Note.MUTE,
                Note.C4, Note.E4, Note.G4, Note.MUTE,
                Note.G4, Note.F4, Note.E4, Note.D4, Note.C4
            };
            int[] durations2 = {
                300, 300, 400, 200,
                300, 300, 400, 200,
                200, 200, 200, 200, 600
            };
            playMelody(drone, melody2, durations2, 15);
            
            Thread.sleep(1000);
            
            // Example melody 3: Pomp and Circumstance (Public Domain)
            System.out.println("\nPlaying Pomp and Circumstance...");
            // "Land of Hope and Glory" theme from Pomp and Circumstance March No. 1
            // by Edward Elgar (1901) - Public Domain
            Note[] pompAndCircumstance = {
                // Main theme
                Note.D4, Note.D4, Note.D4, Note.G4, Note.FS4, Note.G4,
                Note.A4, Note.A4, Note.A4, Note.D5, Note.C5, Note.B4,
                Note.A4, Note.A4, Note.G4, Note.FS4, Note.E4, Note.D4,
                Note.E4, Note.FS4, Note.G4, Note.MUTE,
                // Repeat
                Note.D4, Note.D4, Note.D4, Note.G4, Note.FS4, Note.G4,
                Note.A4, Note.A4, Note.A4, Note.D5, Note.C5, Note.B4,
                Note.A4, Note.A4, Note.B4, Note.C5, Note.B4, Note.A4,
                Note.G4, Note.MUTE, Note.G4, Note.MUTE
            };
            int[] pompDurations = {
                // Main theme
                300, 300, 600, 600, 300, 300,
                300, 300, 600, 600, 300, 300,
                300, 300, 300, 300, 300, 300,
                600, 300, 900, 300,
                // Repeat
                300, 300, 600, 600, 300, 300,
                300, 300, 600, 600, 300, 300,
                300, 300, 300, 300, 300, 300,
                900, 300, 900, 300
            };
            playMelody(drone, pompAndCircumstance, pompDurations, 10);
            
            System.out.println("\nMelody playback complete!");
            
        } catch (InterruptedException e) {
            System.err.println("Playback interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during playback: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nClosing connection...");
            drone.close();
        }
    }
}
