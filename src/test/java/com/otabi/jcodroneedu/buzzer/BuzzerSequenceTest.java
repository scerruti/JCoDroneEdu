package com.otabi.jcodroneedu.buzzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for BuzzerSequence class.
 * Tests Builder pattern, immutability, validation, and factory methods.
 */
@DisplayName("BuzzerSequence Unit Tests")
class BuzzerSequenceTest {
    
    @Test
    @DisplayName("Builder creates valid sequence with notes")
    void testBuilderCreatesSequence() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .addNote(880, 300)
            .build("test-sequence");
        
        assertNotNull(sequence, "Sequence should not be null");
        assertEquals("test-sequence", sequence.getName(), "Name should match");
        assertEquals(2, sequence.getNotes().size(), "Should have 2 notes");
    }
    
    @Test
    @DisplayName("Builder with pause creates correct notes")
    void testBuilderWithPause() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .addPause(100)
            .addNote(880, 200)
            .build("pause-test");
        
        List<BuzzerSequence.BuzzerNote> notes = sequence.getNotes();
        assertEquals(3, notes.size(), "Should have 3 notes (2 tones + 1 pause)");
        
        // Check pause (0 Hz note)
        BuzzerSequence.BuzzerNote pause = notes.get(1);
        assertEquals(0, pause.frequency, "Pause should have 0 Hz");
        assertEquals(100, pause.durationMs, "Pause should have correct duration");
    }
    
    @Test
    @DisplayName("Builder with delay creates correct notes")
    void testBuilderWithDelay() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200, 50)
            .addNote(880, 200)
            .build("delay-test");
        
        List<BuzzerSequence.BuzzerNote> notes = sequence.getNotes();
        BuzzerSequence.BuzzerNote firstNote = notes.get(0);
        
        assertEquals(50, firstNote.delayAfterMs, "First note should have delay");
    }
    
    @Test
    @DisplayName("Builder rejects invalid frequency (negative)")
    void testBuilderRejectsNegativeFrequency() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder();
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.addNote(-1, 200);  // Negative frequency
        }, "Should reject negative frequency");
    }
    
    @Test
    @DisplayName("Builder rejects invalid frequency (too high)")
    void testBuilderRejectsHighFrequency() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder();
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.addNote(10001, 200);  // Above MAX_FREQUENCY
        }, "Should reject frequency above 10000 Hz");
    }
    
    @Test
    @DisplayName("Builder rejects invalid duration")
    void testBuilderRejectsInvalidDuration() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder();
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.addNote(440, 0);  // Duration must be > 0
        }, "Should reject duration of 0");
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.addNote(440, -100);  // Negative duration
        }, "Should reject negative duration");
    }
    
    @Test
    @DisplayName("Builder rejects negative delay")
    void testBuilderRejectsNegativeDelay() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder();
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.addNote(440, 200, -50);  // Negative delay
        }, "Should reject negative delay");
    }
    
    @Test
    @DisplayName("Builder requires at least one note")
    void testBuilderRequiresNotes() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder();
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.build("empty-sequence");
        }, "Should reject empty sequence");
    }
    
    @Test
    @DisplayName("Builder requires non-null name")
    void testBuilderRequiresName() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder()
            .addNote(440, 200);
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.build(null);
        }, "Should reject null name");
    }
    
    @Test
    @DisplayName("Builder requires non-blank name")
    void testBuilderRequiresNonBlankName() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder()
            .addNote(440, 200);
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.build("   ");
        }, "Should reject blank name");
    }
    
    @Test
    @DisplayName("Sequence is immutable - notes list cannot be modified")
    void testSequenceImmutability() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("immutable-test");
        
        List<BuzzerSequence.BuzzerNote> notes = sequence.getNotes();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            notes.add(new BuzzerSequence.BuzzerNote(880, 200, 0));
        }, "Notes list should be unmodifiable");
    }
    
    @Test
    @DisplayName("getTotalDurationMs calculates correctly without delays")
    void testTotalDurationWithoutDelays() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .addNote(880, 300)
            .addNote(1760, 100)
            .build("duration-test");
        
        int expectedDuration = 200 + 300 + 100;
        assertEquals(expectedDuration, sequence.getTotalDurationMs(), 
            "Total duration should be sum of all note durations");
    }
    
    @Test
    @DisplayName("getTotalDurationMs includes delays")
    void testTotalDurationWithDelays() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200, 50)
            .addNote(880, 300, 100)
            .addNote(1760, 100, 0)
            .build("duration-delay-test");
        
        int expectedDuration = (200 + 50) + (300 + 100) + (100 + 0);
        assertEquals(expectedDuration, sequence.getTotalDurationMs(), 
            "Total duration should include note durations and delays");
    }
    
    @Test
    @DisplayName("getTotalDurationMs includes pauses")
    void testTotalDurationWithPauses() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .addPause(100)
            .addNote(880, 300)
            .build("duration-pause-test");
        
        int expectedDuration = 200 + 100 + 300;
        assertEquals(expectedDuration, sequence.getTotalDurationMs(), 
            "Total duration should include pauses");
    }
    
    @Test
    @DisplayName("Factory: createSuccessSequence creates valid sequence")
    void testCreateSuccessSequence() {
        BuzzerSequence sequence = BuzzerSequence.createSuccessSequence();
        
        assertNotNull(sequence, "Success sequence should not be null");
        assertEquals("success", sequence.getName(), "Should have 'success' name");
        assertTrue(sequence.getNotes().size() > 0, "Should have notes");
        assertTrue(sequence.getTotalDurationMs() > 0, "Should have duration");
    }
    
    @Test
    @DisplayName("Factory: createWarningSequence creates valid sequence")
    void testCreateWarningSequence() {
        BuzzerSequence sequence = BuzzerSequence.createWarningSequence();
        
        assertNotNull(sequence, "Warning sequence should not be null");
        assertEquals("warning", sequence.getName(), "Should have 'warning' name");
        assertTrue(sequence.getNotes().size() > 0, "Should have notes");
        assertTrue(sequence.getTotalDurationMs() > 0, "Should have duration");
    }
    
    @Test
    @DisplayName("Factory: createErrorSequence creates valid sequence")
    void testCreateErrorSequence() {
        BuzzerSequence sequence = BuzzerSequence.createErrorSequence();
        
        assertNotNull(sequence, "Error sequence should not be null");
        assertEquals("error", sequence.getName(), "Should have 'error' name");
        assertTrue(sequence.getNotes().size() > 0, "Should have notes");
        assertTrue(sequence.getTotalDurationMs() > 0, "Should have duration");
    }
    
    @Test
    @DisplayName("BuzzerNote stores all values correctly")
    void testBuzzerNoteValues() {
        BuzzerSequence.BuzzerNote note = new BuzzerSequence.BuzzerNote(440, 200, 50);
        
        assertEquals(440, note.frequency, "Frequency should match");
        assertEquals(200, note.durationMs, "Duration should match");
        assertEquals(50, note.delayAfterMs, "Delay should match");
    }
    
    @Test
    @DisplayName("Builder accumulates notes across multiple builds")
    void testBuilderAccumulation() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder();
        
        BuzzerSequence seq1 = builder
            .addNote(440, 200)
            .build("sequence-1");
        
        BuzzerSequence seq2 = builder
            .addNote(880, 300)
            .build("sequence-2");
        
        assertEquals(1, seq1.getNotes().size(), "First sequence should have 1 note");
        assertEquals(2, seq2.getNotes().size(), 
            "Second sequence should accumulate to have 2 notes (builder doesn't reset)");
    }
    
    @Test
    @DisplayName("Complex sequence with mixed notes, pauses, and delays")
    void testComplexSequence() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200, 50)   // Note with delay
            .addPause(100)            // Pause
            .addNote(880, 300)        // Note without delay
            .addNote(1760, 150, 25)   // Note with delay
            .addPause(75)             // Another pause
            .build("complex-test");
        
        List<BuzzerSequence.BuzzerNote> notes = sequence.getNotes();
        assertEquals(5, notes.size(), "Should have 5 total notes");
        
        // Verify structure
        assertEquals(440, notes.get(0).frequency, "First should be 440 Hz");
        assertEquals(0, notes.get(1).frequency, "Second should be pause (0 Hz)");
        assertEquals(880, notes.get(2).frequency, "Third should be 880 Hz");
        assertEquals(1760, notes.get(3).frequency, "Fourth should be 1760 Hz");
        assertEquals(0, notes.get(4).frequency, "Fifth should be pause (0 Hz)");
        
        // Verify total duration
        int expectedDuration = (200 + 50) + 100 + 300 + (150 + 25) + 75;
        assertEquals(expectedDuration, sequence.getTotalDurationMs(), 
            "Total duration should be correctly calculated");
    }
    
    @Test
    @DisplayName("Minimum valid frequency (0 Hz for silence) is accepted")
    void testMinimumValidFrequency() {
        assertDoesNotThrow(() -> {
            new BuzzerSequence.Builder()
                .addNote(0, 200)  // 0 Hz = silence/pause
                .build("min-freq-test");
        }, "Should accept minimum frequency of 0 Hz (silence)");
    }
    
    @Test
    @DisplayName("Maximum valid frequency (10000 Hz) is accepted")
    void testMaximumValidFrequency() {
        assertDoesNotThrow(() -> {
            new BuzzerSequence.Builder()
                .addNote(10000, 200)
                .build("max-freq-test");
        }, "Should accept maximum frequency of 10000 Hz");
    }
    
    @Test
    @DisplayName("Zero delay is accepted")
    void testZeroDelay() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200, 0)
            .build("zero-delay-test");
        
        BuzzerSequence.BuzzerNote note = sequence.getNotes().get(0);
        assertEquals(0, note.delayAfterMs, "Zero delay should be accepted");
    }
    
    @Test
    @DisplayName("Pause with zero duration is rejected")
    void testPauseRequiresPositiveDuration() {
        BuzzerSequence.Builder builder = new BuzzerSequence.Builder();
        
        assertThrows(IllegalArgumentException.class, () -> {
            builder.addPause(0);
        }, "Pause with zero duration should be rejected");
    }
    
    @Test
    @DisplayName("getName returns correct name")
    void testGetName() {
        String expectedName = "my-test-sequence";
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build(expectedName);
        
        assertEquals(expectedName, sequence.getName(), "getName should return correct name");
    }
    
    @Test
    @DisplayName("toString includes sequence name")
    void testToString() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("test-sequence");
        
        String toString = sequence.toString();
        assertTrue(toString.contains("test-sequence"), 
            "toString should include sequence name");
        assertTrue(toString.contains("notes=1"), 
            "toString should include note count");
    }
}
