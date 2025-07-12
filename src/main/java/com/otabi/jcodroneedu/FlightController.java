package com.otabi.jcodroneedu;

import com.google.common.util.concurrent.RateLimiter;
import com.otabi.jcodroneedu.protocol.CommandType;
import com.otabi.jcodroneedu.protocol.DeviceType;
import com.otabi.jcodroneedu.protocol.control.Position;
import com.otabi.jcodroneedu.protocol.control.Quad8;
import com.otabi.jcodroneedu.protocol.dronestatus.State;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Manages the direct flight control and flight-related commands for the drone.
 */
public class FlightController {
    private final Drone drone;
    private final DroneStatus droneStatus;
    private final RateLimiter controlLoopRateLimiter = RateLimiter.create(50.0); // 50 commands per second

    private final Quad8 control;

    public FlightController(Drone drone) {
        this.drone = drone;
        this.droneStatus = drone.getDroneStatus(); // Get a reference to the status cache
        this.control = new Quad8();
    }

    /**
     * Represents the pre-programmed flight events the drone can execute.
     */
    public enum FlightEvent {
        NONE(0x00),
        STOP(0x10),
        TAKE_OFF(0x11),
        LANDING(0x12),
        REVERSE(0x13),
        FLIP_FRONT(0x14),
        FLIP_REAR(0x15),
        FLIP_LEFT(0x16),
        FLIP_RIGHT(0x17),
        RETURN(0x18),
        SHOT(0x90),
        UNDER_ATTACK(0x91),
        RESET_HEADING(0xA0),
        END_OF_TYPE(0xA1);

        private final byte value;

        FlightEvent(int value) {
            this.value = (byte) value;
        }

        public byte getValue() {
            return value;
        }
    }

    /**
     * Commands the drone to take off. This method is blocking and follows a two-stage process:
     * 1. It repeatedly sends the takeoff command until the drone reports its state as TAKEOFF.
     * 2. It then waits until the drone reports its state as FLIGHT, indicating it's stable and ready.
     * Both stages have timeouts to prevent the program from hanging.
     */
    public void takeoff() {
        resetMoveValues();
        long timeout = 4000; // 4-second timeout for each stage
        long startTime = System.currentTimeMillis();

        // Stage 1: Wait for the drone to enter TAKEOFF mode
        while (System.currentTimeMillis() - startTime < timeout) {
            State currentState = droneStatus.getState();
            if (currentState != null && currentState.isTakeOff()) {
                break; // Stage 1 complete
            }
            triggerFlightEvent(FlightEvent.TAKE_OFF);
            sleep(10);
        }

        // Stage 2: Wait for the drone to stabilize and enter FLIGHT mode
        startTime = System.currentTimeMillis(); // Reset timer for the next stage
        while (System.currentTimeMillis() - startTime < timeout) {
            State currentState = droneStatus.getState();
            if (currentState != null && currentState.isFlight()) {
                return; // Takeoff successful and stable
            }
            sleep(10);
        }
    }

    /**
     * Commands the drone to land gently. This method is blocking and follows a two-stage process:
     * 1. It repeatedly sends the landing command until the drone reports its state as LANDING.
     * 2. It then waits until the drone reports its state as READY, indicating the landing is complete.
     * Both stages have timeouts to prevent the program from hanging.
     */
    public void land() {
        resetMoveValues();
        long timeout = 4000; // 4-second timeout for each stage
        long startTime = System.currentTimeMillis();

        // Stage 1: Wait for the drone to enter LANDING mode
        while (System.currentTimeMillis() - startTime < timeout) {
            State currentState = droneStatus.getState();
            if (currentState != null && currentState.isLanding()) {
                break; // Stage 1 complete
            }
            triggerFlightEvent(FlightEvent.LANDING);
            sleep(10);
        }

        // Stage 2: Wait for the drone to complete landing and enter READY mode
        startTime = System.currentTimeMillis(); // Reset timer for the next stage
        while (System.currentTimeMillis() - startTime < timeout) {
            State currentState = droneStatus.getState();
            if (currentState != null && currentState.isReady()) {
                return; // Landing successful
            }
            sleep(10);
        }
    }

    /**
     * Sends a command to stop all motors immediately.
     */
    public void emergencyStop() {
        resetMoveValues();
        triggerFlightEvent(FlightEvent.STOP);
    }

    /**
     * Commands the drone to hover in place for a specific duration by sending a single
     * command to cease all movement and then pausing the thread.
     * @param durationMs The duration to hover, in milliseconds.
     */
    public void hover(long durationMs) {
        sendControl(0, 0, 0, 0);
        sleep(durationMs);
    }

    /**
     * Resets the values of roll, pitch, yaw, and throttle to 0 by sending
     * a hover command multiple times to ensure the drone is stable.
     * @param attempts The number of times the hover command is sent.
     */
    public void resetMoveValues(int attempts) {
        for (int i = 0; i < attempts; i++) {
            hover(10); // Send a brief hover command
        }
    }

    /**
     * Resets the drone's movement values to zero.
     */
    public void resetMoveValues() {
        resetMoveValues(3);
    }

    /**
     * Sets the drone's responsiveness level.
     * @param speedLevel An integer from 1 (slow) to 3 (fast).
     */
    public void changeSpeed(int speedLevel) {
        byte level = (byte) Math.max(1, Math.min(speedLevel, 3));
        drone.sendCommand(CommandType.ControlSpeed, level);
    }

    /**
     * Enables or disables headless mode.
     * @param enable true to enable headless mode, false to disable.
     */
    public void setHeadlessMode(boolean enable) {
        byte value = (byte) (enable ? 1 : 0);
        drone.sendCommand(CommandType.Headless, value);
    }

    /**
     * Triggers a pre-programmed flight event.
     * @param event The flight event to trigger.
     */
    public void triggerFlightEvent(FlightEvent event) {
        drone.sendCommand(CommandType.FlightEvent, event.getValue());
    }

    /**
     * This is a setter function that allows you to set the pitch variable.
     * Once you set pitch, you have to use move() to actually execute the movement.
     * The pitch variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want
     * the drone to pitch again.
     *
     * @param pitch Sets the pitch variable (-100 - 100). The number represents the
     *              direction and power of the output for that flight motion variable.
     *              Negative pitch is backwards, positive pitch is forwards.
     */
    public void setPitch(int pitch) {
        control.setPitch((byte) Math.max(-100, Math.min(pitch, 100)));
    }

    /**
     * This is a setter function that allows you to set the roll variable.
     * Once you set roll, you have to use move() to actually execute the movement.
     * The roll variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want
     * the drone to roll again.
     *
     * @param roll Sets the roll variable (-100 - 100). The number represents the
     *             direction and power of the output for that flight motion variable.
     *             Negative roll is left, positive roll is right.
     */
    public void setRoll(int roll) {
        control.setRoll((byte) Math.max(-100, Math.min(roll, 100)));
    }

    /**
     * This is a setter function that allows you to set the yaw variable.
     * Once you set yaw, you have to use move()to actually execute the movement.
     * The yaw variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want
     * the drone to yaw again.
     *
     * @param yaw Sets the yaw variable (-100 - 100). The number represents the
     *            direction and power of the output for that flight motion variable.
     *            Negative yaw is right, positive yaw is left.
     */
    public void setYaw(int yaw) {
        control.setYaw((byte) Math.max(-100, Math.min(yaw, 100)));
    }

    /**
     * This is a setter function that allows you to set the throttle variable.
     * Once you set throttle, you have to use move() to actually execute the movement.
     * The throttle variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want the
     * drone to throttle again.
     *
     * @param throttle Sets the throttle variable (-100 - 100).
     *                 The number represents the direction and power of the
     *                 output for that flight motion variable.
     *                 Negative throttle is down, positive throttle is up.
     */
    public void setThrottle(int throttle) {
        control.setThrottle((byte) Math.max(-100, Math.min(throttle, 100)));
    }

    /**
     * Used with set_roll, set_pitch, set_yaw, set_throttle commands.
     * Sends flight movement values to the drone.
     */
    public void move()
    {
        sendControl(control);
    }

    /**
     * Used with set_roll, set_pitch, set_yaw, set_throttle commands.
     * Sends flight movement values to the drone.
     *
     * @param duration Number of seconds to perform the action
     */
    public void move(int duration)
    {
        int milliseconds = duration * 1000;
        // there is a while loop inside of the send control method.
        sendControlWhile(control, milliseconds);
    }

    /**
     * Prints current values of roll, pitch, yaw, and throttle.
     *
     * @deprecated This method is deprecated and will be removed in a future release.Please use <pre>drone.get_move_values()</pre> instead.
     */
    @Deprecated
    public void print_move_values(){
        // Keep System.out for student debugging - this is educational output
        System.out.println(Arrays.toString(new byte[]{control.getRoll(), control.getPitch(), control.getYaw(), control.getThrottle()}));
    }

    /**
     * Returns current values of roll, pitch, yaw, and throttle.
     *
     * @return A byte array of roll(0), pitch (1), yaw (2) and throttle (3) values.
     */
    public byte[] get_move_values(){
        return new byte[]{control.getRoll(), control.getPitch(), control.getYaw(), control.getThrottle()};
    }


    /**
     * Sends a single flight control command to the drone.
     * The input values will be clamped to the range of -100 to 100.
     *
     * @param control     Quad8 containung roll, pitch, yaw and throttle
     */
    private void sendControl(Quad8 control)
    {
        sendControl(control.getRoll(), control.getPitch(), control.getYaw(), control.getThrottle());
    }

    /**
     * Sends a single flight control command to the drone.
     * The input values will be clamped to the range of -100 to 100.
     *
     * @param roll     The roll value (-100 to 100).
     * @param pitch    The pitch value (-100 to 100).
     * @param yaw      The yaw value (-100 to 100).
     * @param throttle The throttle value (-100 to 100).
     */
    public void sendControl(int roll, int pitch, int yaw, int throttle) {
        Quad8 controlMessage = new Quad8();
        controlMessage.setRoll((byte) Math.max(-100, Math.min(roll, 100)));
        controlMessage.setPitch((byte) Math.max(-100, Math.min(pitch, 100)));
        controlMessage.setYaw((byte) Math.max(-100, Math.min(yaw, 100)));
        controlMessage.setThrottle((byte) Math.max(-100, Math.min(throttle, 100)));
        drone.sendMessage(controlMessage, DeviceType.Base, DeviceType.Drone);
    }

    /**
     * Continuously sends flight control commands for a specified duration.
     *
     * @param control     Quad8 containung roll, pitch, yaw and throttle
     * @param timeMs      The duration to send commands for, in milliseconds.
     */
    private void sendControlWhile(Quad8 control, long timeMs)
    {
        sendControlWhile(control.getRoll(), control.getPitch(), control.getYaw(), control.getThrottle(), timeMs);
    }

    /**
     * Continuously sends flight control commands for a specified duration.
     *
     * @param roll     The roll value (-100 to 100).
     * @param pitch    The pitch value (-100 to 100).
     * @param yaw      The yaw value (-100 to 100).
     * @param throttle The throttle value (-100 to 100).
     * @param timeMs   The duration to send commands for, in milliseconds.
     */
    public void sendControlWhile(int roll, int pitch, int yaw, int throttle, long timeMs) {
        if (timeMs <= 0) return;
        long endTime = System.currentTimeMillis() + timeMs;
        while (System.currentTimeMillis() < endTime) {
            controlLoopRateLimiter.acquire();
            sendControl(roll, pitch, yaw, throttle);
        }
        sendControl(0, 0, 0, 0);
    }

    /**
     * Sends a position-based movement command to the drone.
     *
     * @param positionX          The target position on the X-axis (Front/Back) in meters.
     * @param positionY          The target position on the Y-axis (Left/Right) in meters.
     * @param positionZ          The target position on the Z-axis (Up/Down) in meters.
     * @param velocity           The speed to move towards the target position in m/s.
     * @param heading            The target heading in degrees (-360 to 360).
     * @param rotationalVelocity The speed for turning in degrees/s.
     */
    public void sendControlPosition(float positionX, float positionY, float positionZ, float velocity, int heading, int rotationalVelocity) {
        Position positionMessage = new Position();
        positionMessage.positionX = Math.max(-10.0f, Math.min(positionX, 10.0f));
        positionMessage.positionY = Math.max(-10.0f, Math.min(positionY, 10.0f));
        positionMessage.positionZ = Math.max(-10.0f, Math.min(positionZ, 10.0f));
        positionMessage.velocity = Math.max(0.5f, Math.min(velocity, 2.0f));
        positionMessage.heading = (short) Math.max(-360, Math.min(heading, 360));
        positionMessage.rotationalVelocity = (short) Math.max(10, Math.min(rotationalVelocity, 360));
        drone.sendMessage(positionMessage, DeviceType.Base, DeviceType.Drone);
    }

    /**
     * Helper method to pause execution, handling InterruptedException.
     * @param durationMs The duration to sleep, in milliseconds.
     */
    private void sleep(long durationMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}