package com.otabi.jcodroneedu.protocol.lightcontroller;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public enum LightModesDrone implements LightModes {

    None_((byte) 0x00),

    RearNone((byte) 0x10),
    RearManual((byte) 0x11),      // Manual control
    RearHold((byte) 0x12),        // Keep the specified color on
    RearFlicker((byte) 0x13),     // Flicker
    RearFlickerDouble((byte) 0x14), // Flicker (flickers twice and turns off for the same duration as the flicker)
    RearDimming((byte) 0x15),       // Slowly flicker by controlling brightness
    RearSunrise((byte) 0x16),
    RearSunset((byte) 0x17),

    BodyNone((byte) 0x20),
    BodyManual((byte) 0x21),      // Manual control
    BodyHold((byte) 0x22),        // Keep the specified color on
    BodyFlicker((byte) 0x23),     // Flicker
    BodyFlickerDouble((byte) 0x24), // Flicker (flickers twice and turns off for the same duration as the flicker)
    BodyDimming((byte) 0x25),       // Slowly flicker by controlling brightness
    BodySunrise((byte) 0x26),
    BodySunset((byte) 0x27),
    BodyRainbow((byte) 0x28),
    BodyRainbow2((byte) 0x29),

    ANone((byte) 0x30),
    AManual((byte) 0x31),      // Manual control
    AHold((byte) 0x32),        // Keep the specified color on
    AFlicker((byte) 0x33),     // Flicker
    AFlickerDouble((byte) 0x34), // Flicker (flickers twice and turns off for the same duration as the flicker)
    ADimming((byte) 0x35),       // Slowly flicker by controlling brightness
    ASunrise((byte) 0x36),
    ASunset((byte) 0x37),

    BNone((byte) 0x40),
    BManual((byte) 0x41),      // Manual control
    BHold((byte) 0x42),        // Keep the specified color on
    BFlicker((byte) 0x43),     // Flicker
    BFlickerDouble((byte) 0x44), // Flicker (flickers twice and turns off for the same duration as the flicker)
    BDimming((byte) 0x45),       // Slowly flicker by controlling brightness
    BSunrise((byte) 0x46),
    BSunset((byte) 0x47),

    EndOfType((byte) 0x60);

    private final byte value;

    LightModesDrone(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    private static final Map<Byte, LightModes> valueMap = new HashMap<>();

    static {
        for (LightModesDrone mode : LightModesDrone.values()) {
            valueMap.put(mode.getValue(), mode);
        }
    }

    public static LightModes fromValue(byte value) {
        return valueMap.get(value);
    }

    public LightModes getModeFromBuffer(ByteBuffer buffer) {
        return fromValue(buffer.get());
    }
}

