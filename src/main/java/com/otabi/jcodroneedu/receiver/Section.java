package com.otabi.jcodroneedu.receiver;

// Data section
enum Section {
    Start((byte) 0x00),           // Start of transmission code
    Header((byte) 0x01),          // Header
    Data((byte) 0x02),            // Data
    End((byte) 0x03);             // Data verification

    private final byte value;

    Section(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static Section fromByte(byte value) {
        for (Section type : Section.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Section value: " + value);
    }
}
