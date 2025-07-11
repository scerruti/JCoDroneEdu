package com.otabi.jcodroneedu;

public class DroneNotFoundException extends Exception {
    public DroneNotFoundException() {
        super("CoDrone not found.");
    }

    public DroneNotFoundException(String deviceName) {
        super("CoDrone not found" + (!(deviceName == null || deviceName.isEmpty()) ? ", on "+deviceName : "") + ".");
    }
}
