package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.CommandType;
import com.otabi.jcodroneedu.protocol.DeviceType;

/**
 * Manages commands related to the communication link and the controller's hardware.
 * This includes pairing, setting operational modes, and controlling hardware features
 * like the backlight.
 */
public class LinkController {

    private final Drone drone;

    public LinkController(Drone drone) {
        this.drone = drone;
    }

    /**
     * Sets the operational mode for the communication link.
     *
     * @param mode The desired link mode to set.
     */
    public void setLinkMode(LinkMode mode) {
        drone.sendCommand(CommandType.Link, mode.getValue(), DeviceType.Base, DeviceType.Controller);
    }

    /**
     * Sets the controller's main operational mode.
     *
     * @param mode The desired controller mode.
     */
    public void setControllerMode(ControllerMode mode) {
        drone.sendCommand(CommandType.ModeController, mode.getValue(), DeviceType.Base, DeviceType.Controller);
    }

    /**
     * Turns the controller's display backlight on or off.
     *
     * @param enable Set to true to turn the backlight on, false to turn it off.
     */
    public void setBacklight(boolean enable) {
        byte value = (byte) (enable ? 1 : 0);
        drone.sendCommand(CommandType.Backlight, value, DeviceType.Base, DeviceType.Controller);
    }

    /**
     * Enum representing the different modes for the communication link.
     */
    public enum LinkMode {
        CLIENT((byte) 0x00),
        SERVER((byte) 0x01),
        START_PAIRING((byte) 0x02);

        private final byte value;

        LinkMode(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    /**
     * Enum representing the different operational modes for the controller.
     */
    public enum ControllerMode {
        CONTROL((byte) 0x10),
        LINK((byte) 0x80);

        private final byte value;

        ControllerMode(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }
}
