package com.otabi.jcodroneedu.receiver;

// Data reception status
enum StateLoading {
    Ready((byte) 0x00),           // Ready to receive
    Receiving((byte) 0x01),       // Receiving
    Loaded((byte) 0x02),          // Received and waiting in command storage
    Failure((byte) 0x03);         // Reception failed

    private final byte value;

    StateLoading(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static StateLoading fromByte(byte value) {
        for (StateLoading type : StateLoading.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid StateLoading value: " + value);
    }
}

