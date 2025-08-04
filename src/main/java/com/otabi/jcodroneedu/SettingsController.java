package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.CommandType;
import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.DeviceType;
import com.otabi.jcodroneedu.protocol.Header;
import com.otabi.jcodroneedu.protocol.settings.Trim;

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
     * Sends trim values to the drone for roll, pitch, yaw, and throttle.
     * 
     * @param roll Roll trim value (-100 to 100)
     * @param pitch Pitch trim value (-100 to 100) 
     * @param yaw Yaw trim value (-100 to 100)
     * @param throttle Throttle trim value (-100 to 100)
     */
    public void sendTrim(short roll, short pitch, short yaw, short throttle) {
        Trim trim = new Trim(roll, pitch, yaw, throttle);
        
        Header header = new Header();
        header.setDataType(DataType.Trim);
        header.setLength(trim.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Drone);
        
        drone.transfer(header, trim);
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
