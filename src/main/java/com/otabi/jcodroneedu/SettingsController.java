package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.CommandType;

/**
 * Manages commands related to the drone's configuration, calibration,
 * and other persistent settings.
 */
public class SettingsController {

    private final Drone drone;

    public SettingsController(Drone drone) {
        this.drone = drone;
    }

    /**
     * Resets the drone's internal gyroscope sensor bias. This can help correct
     * for drift during flight. Note that this also resets the flight trim.
     */
    public void clearBias() {
        drone.sendCommand(CommandType.ClearBias, (byte) 0);
    }

    /**
     * Resets the flight trim values to their defaults.
     */
    public void clearTrim() {
        drone.sendCommand(CommandType.ClearTrim, (byte) 0);
    }

    /**
     * Resets all drone settings to their factory defaults.
     */
    public void setDefault() {
        drone.sendCommand(CommandType.SetDefault, (byte) 0);
    }

    /**
     * Clears internal counters, such as total flight time.
     * Note: This may require administrator privileges on the drone.
     */
    public void clearCounter() {
        drone.sendCommand(CommandType.ClearCounter, (byte) 0);
    }
}
