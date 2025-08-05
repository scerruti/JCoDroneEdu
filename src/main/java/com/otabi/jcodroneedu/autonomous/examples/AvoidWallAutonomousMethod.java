package com.otabi.jcodroneedu.autonomous.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.autonomous.AutonomousMethod;

import java.util.Map;

/**
 * Autonomous method that makes the drone fly forward until it reaches a desired distance from a wall,
 * then keeps that distance using proportional control. Runs as a background thread.
 */
public class AvoidWallAutonomousMethod extends AutonomousMethod {
    public AvoidWallAutonomousMethod() {
        super("avoidWall", "Autonomously avoids walls by maintaining a minimum distance using the front range sensor.");
    }

    @Override
    protected void defineParameters() {
        addParameter("timeout", 1, 30, "Duration in seconds to run the avoidance loop.");
        addParameter("distance", 10, 100, "Target distance from wall in cm.");
    }

    @Override
    protected void executeAlgorithm(Drone drone, Map<String, Integer> params) {
        int timeout = params.getOrDefault("timeout", 2);
        int distance = params.getOrDefault("distance", 70);
        int threshold = 20;
        double pValue = 0.4;
        int counter = 0;
        long start = System.currentTimeMillis();
        long end = start + timeout * 1000L;
        // prevDistance removed (was unused)
        while (System.currentTimeMillis() < end && !Thread.currentThread().isInterrupted()) {
            double currentDistance = drone.getFrontRange();

            double errorPercent = percentError(distance, currentDistance);
            int speed = (int) (errorPercent * pValue);
            if (currentDistance > distance + threshold || currentDistance < distance - threshold) {
                drone.sendControl(0, speed, 0, 0);
            } else {
                drone.hover(0.05);
                counter++;
                if (counter == 20) break;
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
        return "Maintains a minimum distance from a wall using the front range sensor and proportional control. " +
               "If the drone is too close or too far, it adjusts its speed to maintain the target distance.";
    }
}
