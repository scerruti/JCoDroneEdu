package com.otabi.jcodroneedu.protocol.display;

/**
 * Enum for display line types used in CoDrone EDU controller display drawing operations.
 */
public enum DisplayLine {
    SOLID((byte) 0x00),
    DOTTED((byte) 0x01),
    DASHED((byte) 0x02);

    private final byte value;

    DisplayLine(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static DisplayLine fromByte(byte b) {
        for (DisplayLine line : values()) {
            if (line.value == b) {
                return line;
            }
        }
        throw new IllegalArgumentException("Unknown DisplayLine value: " + b);
    }
}
