
package com.otabi.jcodroneedu;

import java.util.ArrayList;
import java.util.List;

public class TestDrone extends Drone {
    private final List<String> commandHistory = new ArrayList<>();
    private double frontRangeValue = 50.0; // default mock value

    public void setFrontRangeValue(double value) {
        this.frontRangeValue = value;
    }

    public List<String> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }

    @Override
    public double getFrontRange() {
        commandHistory.add("getFrontRange");
        return frontRangeValue;
    }

    @Override
    public void sendControl(int roll, int pitch, int yaw, int throttle) {
        commandHistory.add(String.format("sendControl(%d,%d,%d,%d)", roll, pitch, yaw, throttle));
    }

    @Override
    public void hover(double seconds) {
        commandHistory.add(String.format("hover(%.2f)", seconds));
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    // Optionally override other methods as needed for more detailed tests
}
