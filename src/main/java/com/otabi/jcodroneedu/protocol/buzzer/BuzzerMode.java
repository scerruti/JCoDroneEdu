package com.otabi.jcodroneedu.protocol.buzzer;

/**
 * Enum for buzzer operation modes, matching Python BuzzerMode.
 * Used to control how buzzer commands are executed.
 */
public enum BuzzerMode {
    /**
     * Stop buzzer (used when receiving commands to turn off buzzer)
     */
    STOP(0),
    
    /**
     * Mute - apply immediately
     */
    MUTE(1),
    
    /**
     * Mute - reserve for later execution
     */
    MUTE_RESERVE(2),
    
    /**
     * Musical scale - apply immediately
     */
    SCALE(3),
    
    /**
     * Musical scale - reserve for later execution
     */
    SCALE_RESERVE(4),
    
    /**
     * Frequency in Hz - apply immediately
     */
    HZ(5),
    
    /**
     * Frequency in Hz - reserve for later execution
     */
    HZ_RESERVE(6),
    
    /**
     * End of type marker
     */
    END_OF_TYPE(7);

    private final int value;

    BuzzerMode(int value) {
        this.value = value;
    }

    /**
     * Get the integer value of this mode
     * 
     * @return The integer value
     */
    public int getValue() {
        return value;
    }

    /**
     * Get BuzzerMode from integer value
     * 
     * @param value The integer value
     * @return The corresponding BuzzerMode
     * @throws IllegalArgumentException if value doesn't match any mode
     */
    public static BuzzerMode fromValue(int value) {
        for (BuzzerMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown BuzzerMode value: " + value);
    }
}
