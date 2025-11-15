package com.otabi.jcodroneedu.buzzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an immutable sequence of buzzer notes that can be played on the drone or controller.
 * 
 * <p>A BuzzerSequence is composed of one or more {@link BuzzerNote} objects, each specifying
 * a frequency, duration, and optional delay. This class provides a fluent {@link Builder} API
 * for students to create custom sound patterns and melodies.</p>
 * 
 * <h3>🎵 Educational Objectives:</h3>
 * <ul>
 *   <li>Learn the Builder design pattern for complex object construction</li>
 *   <li>Understand immutability and why it matters</li>
 *   <li>Practice composition (building complex behaviors from simple parts)</li>
 *   <li>Explore sound/music programming concepts</li>
 * </ul>
 * 
 * <h3>💡 Usage Examples (L0301):</h3>
 * <pre>{@code
 * // Create a simple two-note sequence
 * BuzzerSequence beepBeep = new BuzzerSequence.Builder()
 *     .addNote(1000, 100)  // 1000 Hz for 100ms
 *     .addPause(50)         // 50ms silence
 *     .addNote(1500, 100)  // 1500 Hz for 100ms
 *     .build("beep-beep");
 * 
 * // Register and play
 * drone.registerBuzzerSequence("beep-beep", beepBeep);
 * drone.droneBuzzerSequence("beep-beep");
 * 
 * // Create a musical scale
 * BuzzerSequence scale = new BuzzerSequence.Builder()
 *     .addNote(261, 200)  // C
 *     .addNote(293, 200)  // D
 *     .addNote(329, 200)  // E
 *     .addNote(349, 200)  // F
 *     .addNote(392, 200)  // G
 *     .addNote(440, 200)  // A
 *     .addNote(493, 200)  // B
 *     .addNote(523, 400)  // C (octave higher, longer)
 *     .build("c-major-scale");
 * 
 * drone.registerBuzzerSequence("scale", scale);
 * drone.controllerBuzzerSequence("scale");
 * }</pre>
 * 
 * <h3>🎼 Built-in Sequences:</h3>
 * The system includes three predefined sequences (registered automatically):
 * <ul>
 *   <li><strong>success</strong> - Two ascending tones (1600 Hz, 2200 Hz)</li>
 *   <li><strong>warning</strong> - Three 800 Hz beeps with pauses</li>
 *   <li><strong>error</strong> - Three short 150 Hz beeps</li>
 * </ul>
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 * @see BuzzerSequenceRegistry
 * @see Builder
 * @educational
 * @pythonEquivalent controller_buzzer_sequence(kind), drone_buzzer_sequence(kind)
 * @pythonReference https://docs.robolink.com/docs/CoDroneEDU/Python/Drone-Function-Documentation#drone_buzzer_sequence
 */
public class BuzzerSequence {
    
    private final String name;
    private final List<BuzzerNote> notes;
    
    /**
     * Private constructor - use {@link Builder} to create instances.
     * 
     * @param name The name/identifier for this sequence
     * @param notes The list of notes in the sequence
     */
    private BuzzerSequence(String name, List<BuzzerNote> notes) {
        this.name = name;
        this.notes = Collections.unmodifiableList(new ArrayList<>(notes));
    }
    
    /**
     * Gets the name of this buzzer sequence.
     * 
     * @return The sequence name (e.g., "success", "warning", "my-melody")
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets an unmodifiable list of all notes in this sequence.
     * 
     * @return Immutable list of {@link BuzzerNote} objects
     */
    public List<BuzzerNote> getNotes() {
        return notes;
    }
    
    /**
     * Gets the total duration of this sequence in milliseconds.
     * Includes all note durations and delays.
     * 
     * @return Total sequence duration in milliseconds
     */
    public int getTotalDurationMs() {
        return notes.stream()
            .mapToInt(note -> note.durationMs + note.delayAfterMs)
            .sum();
    }
    
    /**
     * Gets the number of notes in this sequence.
     * 
     * @return Number of notes (pauses count as notes with 0 Hz frequency)
     */
    public int getNoteCount() {
        return notes.size();
    }
    
    @Override
    public String toString() {
        return String.format("BuzzerSequence[name=%s, notes=%d, duration=%dms]", 
            name, getNoteCount(), getTotalDurationMs());
    }
    
    /**
     * Represents a single note in a buzzer sequence.
     * 
     * <p>A note consists of:</p>
     * <ul>
     *   <li><strong>frequency</strong> - The pitch in Hz (0 for silence/pause)</li>
     *   <li><strong>durationMs</strong> - How long to play the note in milliseconds</li>
     *   <li><strong>delayAfterMs</strong> - Silence after the note in milliseconds</li>
     * </ul>
     * 
     * <p>This class is immutable - all fields are final.</p>
     * 
     * @educational Demonstrates immutable value objects
     */
    public static class BuzzerNote {
        /** Frequency in Hz (0 = silence) */
        public final int frequency;
        
        /** Duration to play the note in milliseconds */
        public final int durationMs;
        
        /** Silence after the note in milliseconds */
        public final int delayAfterMs;
        
        /**
         * Creates a new buzzer note.
         * 
         * @param frequency The frequency in Hz (0-10000, 0 for silence)
         * @param durationMs The duration in milliseconds (1-10000)
         * @param delayAfterMs The delay after in milliseconds (0-10000)
         */
        public BuzzerNote(int frequency, int durationMs, int delayAfterMs) {
            this.frequency = frequency;
            this.durationMs = durationMs;
            this.delayAfterMs = delayAfterMs;
        }
        
        @Override
        public String toString() {
            if (frequency == 0) {
                return String.format("Pause[%dms]", durationMs + delayAfterMs);
            }
            return String.format("Note[%dHz, %dms + %dms delay]", 
                frequency, durationMs, delayAfterMs);
        }
    }
    
    /**
     * Fluent builder for creating custom buzzer sequences.
     * 
     * <p>This builder provides an intuitive API for constructing sequences note-by-note.
     * Methods can be chained together for readability.</p>
     * 
     * <h3>🎓 Educational Value (L0302):</h3>
     * <pre>{@code
     * // The builder pattern lets you construct complex objects step by step
     * BuzzerSequence mario = new BuzzerSequence.Builder()
     *     .addNote(659, 125)   // E5
     *     .addNote(659, 125)   // E5
     *     .addPause(125)
     *     .addNote(659, 125)   // E5
     *     .addPause(125)
     *     .addNote(523, 125)   // C5
     *     .addNote(659, 125)   // E5
     *     .addPause(125)
     *     .addNote(784, 125)   // G5
     *     .addPause(375)
     *     .addNote(392, 125)   // G4
     *     .build("mario-start");
     * }</pre>
     * 
     * @educational Teaches fluent interface and method chaining
     */
    public static class Builder {
        private final List<BuzzerNote> notes = new ArrayList<>();
        
        /**
         * Adds a note to the sequence.
         * 
         * @param frequency The frequency in Hz (50-10000)
         * @param durationMs The duration in milliseconds (1-10000)
         * @return This builder for method chaining
         * @throws IllegalArgumentException if parameters are out of range
         */
        public Builder addNote(int frequency, int durationMs) {
            return addNote(frequency, durationMs, 0);
        }
        
        /**
         * Adds a note with a delay after it.
         * 
         * @param frequency The frequency in Hz (50-10000)
         * @param durationMs The duration in milliseconds (1-10000)
         * @param delayAfterMs The delay after the note in milliseconds (0-10000)
         * @return This builder for method chaining
         * @throws IllegalArgumentException if parameters are out of range
         */
        public Builder addNote(int frequency, int durationMs, int delayAfterMs) {
            if (frequency < 0 || frequency > 10000) {
                throw new IllegalArgumentException(
                    "Frequency must be 0-10000 Hz, got: " + frequency);
            }
            if (durationMs < 1 || durationMs > 10000) {
                throw new IllegalArgumentException(
                    "Duration must be 1-10000 ms, got: " + durationMs);
            }
            if (delayAfterMs < 0 || delayAfterMs > 10000) {
                throw new IllegalArgumentException(
                    "Delay must be 0-10000 ms, got: " + delayAfterMs);
            }
            
            notes.add(new BuzzerNote(frequency, durationMs, delayAfterMs));
            return this;
        }
        
        /**
         * Adds a pause (silence) to the sequence.
         * 
         * <p>This is equivalent to {@code addNote(0, pauseMs, 0)} but more readable.</p>
         * 
         * @param pauseMs The duration of silence in milliseconds (1-10000)
         * @return This builder for method chaining
         * @throws IllegalArgumentException if pauseMs is out of range
         */
        public Builder addPause(int pauseMs) {
            if (pauseMs < 1 || pauseMs > 10000) {
                throw new IllegalArgumentException(
                    "Pause must be 1-10000 ms, got: " + pauseMs);
            }
            notes.add(new BuzzerNote(0, pauseMs, 0));
            return this;
        }
        
        /**
         * Builds the immutable BuzzerSequence.
         * 
         * @param name The name for this sequence
         * @return A new immutable BuzzerSequence
         * @throws IllegalArgumentException if name is null/empty or no notes added
         */
        public BuzzerSequence build(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Sequence name cannot be null or empty");
            }
            if (notes.isEmpty()) {
                throw new IllegalArgumentException(
                    "Cannot build sequence with no notes. Add at least one note or pause.");
            }
            
            return new BuzzerSequence(name, notes);
        }
    }
    
    // ============================================================================
    // Built-in Sequence Factory Methods
    // ============================================================================
    
    /**
     * Creates the built-in "success" sequence.
     * Two ascending tones: 1600 Hz → 2200 Hz
     * 
     * @return The success sequence
     */
    public static BuzzerSequence createSuccessSequence() {
        return new Builder()
            .addNote(1600, 120, 50)
            .addNote(2200, 120)
            .build("success");
    }
    
    /**
     * Creates the built-in "warning" sequence.
     * Three 800 Hz beeps with pauses between them.
     * 
     * @return The warning sequence
     */
    public static BuzzerSequence createWarningSequence() {
        return new Builder()
            .addNote(800, 120, 60)
            .addNote(800, 120, 180)
            .addNote(800, 220)
            .build("warning");
    }
    
    /**
     * Creates the built-in "error" sequence.
     * Three short 150 Hz beeps (low, urgent tone).
     * 
     * @return The error sequence
     */
    public static BuzzerSequence createErrorSequence() {
        return new Builder()
            .addNote(150, 180, 50)
            .addNote(150, 180, 50)
            .addNote(150, 180)
            .build("error");
    }
}
