package com.otabi.jcodroneedu.protocol.buzzer;

/**
 * Enum for musical notes and scales, matching Python BuzzerScale and Note enums.
 * Provides all musical notes from C1 to B8 with their corresponding hex values.
 */
public enum Note {
    // Octave 1
    C1(0x00), CS1(0x01), D1(0x02), DS1(0x03), E1(0x04), F1(0x05), 
    FS1(0x06), G1(0x07), GS1(0x08), A1(0x09), AS1(0x0A), B1(0x0B),
    
    // Octave 2
    C2(0x0C), CS2(0x0D), D2(0x0E), DS2(0x0F), E2(0x10), F2(0x11),
    FS2(0x12), G2(0x13), GS2(0x14), A2(0x15), AS2(0x16), B2(0x17),
    
    // Octave 3
    C3(0x18), CS3(0x19), D3(0x1A), DS3(0x1B), E3(0x1C), F3(0x1D),
    FS3(0x1E), G3(0x1F), GS3(0x20), A3(0x21), AS3(0x22), B3(0x23),
    
    // Octave 4 (Middle C)
    C4(0x24), CS4(0x25), D4(0x26), DS4(0x27), E4(0x28), F4(0x29),
    FS4(0x2A), G4(0x2B), GS4(0x2C), A4(0x2D), AS4(0x2E), B4(0x2F),
    
    // Octave 5
    C5(0x30), CS5(0x31), D5(0x32), DS5(0x33), E5(0x34), F5(0x35),
    FS5(0x36), G5(0x37), GS5(0x38), A5(0x39), AS5(0x3A), B5(0x3B),
    
    // Octave 6
    C6(0x3C), CS6(0x3D), D6(0x3E), DS6(0x3F), E6(0x40), F6(0x41),
    FS6(0x42), G6(0x43), GS6(0x44), A6(0x45), AS6(0x46), B6(0x47),
    
    // Octave 7
    C7(0x48), CS7(0x49), D7(0x4A), DS7(0x4B), E7(0x4C), F7(0x4D),
    FS7(0x4E), G7(0x4F), GS7(0x50), A7(0x51), AS7(0x52), B7(0x53),
    
    // Octave 8
    C8(0x54), CS8(0x55), D8(0x56), DS8(0x57), E8(0x58), F8(0x59),
    FS8(0x5A), G8(0x5B), GS8(0x5C), A8(0x5D), AS8(0x5E), B8(0x5F),
    
    /**
     * End of type marker
     */
    END_OF_TYPE(0x60),
    
    /**
     * Mute/silence
     */
    MUTE(0xEE),
    
    /**
     * End of musical score marker
     */
    FIN(0xFF);

    private final int value;

    Note(int value) {
        this.value = value;
    }

    /**
     * Get the integer value of this note
     * 
     * @return The integer value
     */
    public int getValue() {
        return value;
    }

    /**
     * Get Note from integer value
     * 
     * @param value The integer value
     * @return The corresponding Note
     * @throws IllegalArgumentException if value doesn't match any note
     */
    public static Note fromValue(int value) {
        for (Note note : values()) {
            if (note.value == value) {
                return note;
            }
        }
        throw new IllegalArgumentException("Unknown Note value: " + value);
    }
    
    /**
     * Get the frequency in Hz for this note (approximation for educational purposes)
     * Based on standard tuning where A4 = 440 Hz
     * 
     * @return The frequency in Hz, or 0 for special notes (MUTE, FIN, END_OF_TYPE)
     */
    public double getFrequency() {
        // Special notes return 0
        if (this == MUTE || this == FIN || this == END_OF_TYPE) {
            return 0.0;
        }
        
        // Calculate frequency based on note value
        // A4 (0x2D = 45) = 440 Hz
        // Each semitone = 2^(1/12) factor
        int semitonesFromA4 = this.value - A4.value;
        return 440.0 * Math.pow(2.0, semitonesFromA4 / 12.0);
    }
    
    /**
     * Check if this note represents an actual musical note (not a control value)
     * 
     * @return true if this is a playable musical note
     */
    public boolean isMusicalNote() {
        return this.value >= C1.value && this.value <= B8.value;
    }
}
