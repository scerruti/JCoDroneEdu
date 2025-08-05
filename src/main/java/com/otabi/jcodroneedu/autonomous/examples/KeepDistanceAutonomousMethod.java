package com.otabi.jcodroneedu.autonomous.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.autonomous.AutonomousMethod;

import java.util.Map;

/**
 * Autonomous method that keeps the drone at a specified distance from an object using proportional control.
 * Runs as a background thread.
 */
public class KeepDistanceAutonomousMethod extends AutonomousMethod {
    public KeepDistanceAutonomousMethod() {
        super("keepDistance", "Keeps the drone at a specified distance from an object using the front range sensor.");
    }

    @Override
    protected void defineParameters() {
        addParameter("timeout", 1, 30, "Duration in seconds to run the loop.");
        addParameter("distance", 10, 100, "Target distance from object in cm.");
    }

    @Override
    protected void executeAlgorithm(Drone drone, Map<String, Integer> params) {
        int timeout = params.getOrDefault("timeout", 2);
        int distance = params.getOrDefault("distance", 50);
        int threshold = 10;
        double pValue = 0.4;
        long start = System.currentTimeMillis();
        long end = start + timeout * 1000L;
        while (System.currentTimeMillis() < end && !Thread.currentThread().isInterrupted()) {
            double currentDistance = drone.getFrontRange();
            double errorPercent = percentError(distance, currentDistance);
            int speed = (int) (errorPercent * pValue);
            if (currentDistance > distance + threshold || currentDistance < distance - threshold) {
                drone.sendControl(0, speed, 0, 0);
            } else {
                drone.hover(0.05);
            }
            try { Thread.sleep(5); } catch (InterruptedException e) { break; }
        }
        drone.hover(0.1);
    }

    private double percentError(double desired, double current) {
        double error = current - desired;
        if (error > 100) error = 100;
        if (error < -100) error = -100;
        return error;
    }

    @Override
    protected String getAlgorithmDescription() {
        return "Keeps the drone at a specified distance from an object using the front range sensor and proportional control. " +
               "Adjusts speed to maintain the target distance.";
    }
}
