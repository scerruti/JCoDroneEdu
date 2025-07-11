package com.otabi.jcodroneedu.protocol;

import java.util.HashMap;
import java.util.Map;

public enum CommandType {
    None_((byte) 0x00),                        // None

    Stop((byte) 0x01),                         // Stop

    // Configuration
    ModeControlFlight((byte) 0x02),            // Flight control mode setting
    Headless((byte) 0x03),                     // Headless mode selection
    ControlSpeed((byte) 0x04),                 // Control speed setting

    ClearBias((byte) 0x05),                    // Gyro bias reset (trim is also initialized)
    ClearTrim((byte) 0x06),                    // Trim initialization

    FlightEvent((byte) 0x07),                  // Flight event execution

    SetDefault((byte) 0x08),                   // Initialize to default settings
    Backlight((byte) 0x09),                    // Controller backlight setting
    ModeController((byte) 0x0A),               // Controller operation mode (0x10: control, 0x80: link)
    Link((byte) 0x0B),                         // Link control (0: Client Mode, 1: Server Mode, 2: Pairing Start)

    // Administrator
    ClearCounter((byte) 0xA0),                 // Counter clear (operates only when administrator privileges are acquired)

    // Navigation
    NavigationTargetClear((byte) 0xE0),       // Navigation target point initialization
    NavigationStart((byte) 0xE1),              // Navigation start (from the beginning)
    NavigationPause((byte) 0xE2),              // Navigation pause
    NavigationRestart((byte) 0xE3),            // Navigation restart (used when restarting after a pause)
    NavigationStop((byte) 0xE4),               // Navigation stop
    NavigationNext((byte) 0xE5),               // Change navigation target point to the next
    NavigationReturnToHome((byte) 0xE6),       // Return to start position

    GpsRtkBase((byte) 0xEA),
    GpsRtkRover((byte) 0xEB),

    EndOfType((byte) 0xEC);

    private final byte value;

    CommandType(byte value) {
        this.value =  value;
    }

    public byte getValue() {
        return value;
    }

    private static final Map<Byte, CommandType> lookup = new HashMap<>();

    static {
        for (CommandType c : CommandType.values()) {
            lookup.put(c.getValue(), c);
        }
    }

    public static CommandType fromValue(byte value) {
        return lookup.getOrDefault(value, None_);
    }

}
