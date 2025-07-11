package com.otabi.jcodroneedu.protocol;

public enum DeviceType {

    None_((byte) 0x00),

    Drone((byte) 0x10),          // Drone (Server)

    Controller((byte) 0x20),     // Controller (Client)

    Link((byte) 0x30),           // Link Module (Client)
    LinkServer((byte) 0x31),     // Link Module (Server, temporarily changes communication type only when the link module operates as a server)
    BleClient((byte) 0x32),      // BLE Client
    BleServer((byte) 0x33),      // BLE Server

    Range((byte) 0x40),          // Range Sensor Module

    Base((byte) 0x70),           // Base

    ByScratch((byte) 0x80),      // ByScratch
    Scratch((byte) 0x81),        // Scratch
    Entry((byte) 0x82),          // Entry

    Tester((byte) 0xA0),         // Tester
    Monitor((byte) 0xA1),        // Monitor
    Updater((byte) 0xA2),        // Firmware Update Tool
    Encryptor((byte) 0xA3),      // Encryption Tool

    Whispering((byte) 0xFE),      // Transmit only to directly adjacent devices (the receiving device processes it as if it were sent to itself and does not forward it to other devices)
    Broadcasting((byte) 0xFF);

    private final byte value;

    DeviceType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static DeviceType fromByte(byte b) {
        for (DeviceType type : DeviceType.values()) {
            if (type.value == b) {
                return type;
            }
        }
        throw new IllegalArgumentException("No DeviceType found for byte value: " + b);
    }
}