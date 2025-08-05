package com.otabi.jcodroneedu.protocol.display;

/**
 * Enum for display font types used in CoDrone EDU controller display text operations.
 */
public enum DisplayFont {
    LIBERATION_MONO_5X8((byte) 0x00),
    LIBERATION_MONO_10X16((byte) 0x01);

    private final byte value;

    DisplayFont(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static DisplayFont fromByte(byte b) {
        for (DisplayFont font : values()) {
            if (font.value == b) {
                return font;
            }
        }
        throw new IllegalArgumentException("Unknown DisplayFont value: " + b);
    }
}
