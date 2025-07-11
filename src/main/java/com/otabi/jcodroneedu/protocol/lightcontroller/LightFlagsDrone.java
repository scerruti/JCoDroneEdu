package com.otabi.jcodroneedu.protocol.lightcontroller;

import java.util.HashMap;
import java.util.Map;

public enum LightFlagsDrone {

    None_((short) 0x0000),

    Rear((short) 0x0001),
    BodyRed((short) 0x0002),
    BodyGreen((short) 0x0004),
    BodyBlue((short) 0x0008),

    A((short) 0x0010),
    B((short) 0x0020);

    private final short value;

    LightFlagsDrone(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    private static final Map<Short, LightFlagsDrone> valueMap = new HashMap<>();

    static {
        for (LightFlagsDrone mode : LightFlagsDrone.values()) {
            valueMap.put(mode.getValue(), mode);
        }
    }

    public static LightFlagsDrone fromValue(short value) {
        return valueMap.get(value);
    }
}
