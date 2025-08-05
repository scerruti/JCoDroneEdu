package com.otabi.jcodroneedu.protocol.display;

/**
 * Enum for display text alignment used in CoDrone EDU controller display string operations.
 */
public enum DisplayAlign {
    LEFT((byte) 0x00),
    CENTER((byte) 0x01),
    RIGHT((byte) 0x02);

    private final byte value;

    DisplayAlign(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static DisplayAlign fromByte(byte b) {
        for (DisplayAlign align : values()) {
            if (align.value == b) {
                return align;
            }
        }
        throw new IllegalArgumentException("Unknown DisplayAlign value: " + b);
    }
}
