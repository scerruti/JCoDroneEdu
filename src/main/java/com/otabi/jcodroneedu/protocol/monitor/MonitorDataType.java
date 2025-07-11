package com.otabi.jcodroneedu.protocol.monitor;

public enum MonitorDataType {
    U8((byte) 0x00),
    S8((byte) 0x01),
    U16((byte) 0x02),
    S16((byte) 0x03),
    U32((byte) 0x04),
    S32((byte) 0x05),
    U64((byte) 0x06),
    S64((byte) 0x07),
    F32((byte) 0x08),
    F64((byte) 0x09),
    EndOfTypeOf((byte) 0x0A);

    private final byte value;

    MonitorDataType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static MonitorDataType fromByte(byte value) {
        for (MonitorDataType type : MonitorDataType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid MonitorDataType value: " + value);
    }
}

