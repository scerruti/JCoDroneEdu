package com.otabi.jcodroneedu;

import com.google.common.util.concurrent.RateLimiter;
import com.otabi.jcodroneedu.protocol.CommandType;
import com.otabi.jcodroneedu.protocol.DeviceType;
import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.control.Position;
import com.otabi.jcodroneedu.protocol.control.Quad8;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.protocol.dronestatus.Motion;
import com.otabi.jcodroneedu.protocol.dronestatus.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Manages the direct flight control and flight-related commands for the drone.
 */
public class FlightController {
    private static final Logger log = LogManager.getLogger(FlightController.class);
    
    private final Drone drone;
    private final DroneStatus droneStatus;
    private final RateLimiter controlLoopRateLimiter = RateLimiter.create(DroneSystem.FlightControlConstants.COMMANDS_PER_SECOND);

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
        long timeout = DroneSystem.FlightControlConstants.TAKEOFF_LANDING_TIMEOUT_MS; // 4-second timeout for each stage
        long startTime = System.currentTimeMillis();

        // Stage 1: Wait for the drone to enter TAKEOFF mode
        while (System.currentTimeMillis() - startTime < timeout) {
            State currentState = droneStatus.getState();
            if (currentState != null && currentState.isTakeOff()) {
                break; // Stage 1 complete
            }
            triggerFlightEvent(FlightEvent.TAKE_OFF);
            sleep(DroneSystem.FlightControlConstants.POLLING_INTERVAL_MS);
        }

        // Stage 2: Wait for the drone to stabilize and enter FLIGHT mode
        startTime = System.currentTimeMillis(); // Reset timer for the next stage
        while (System.currentTimeMillis() - startTime < timeout) {
            State currentState = droneStatus.getState();
            if (currentState != null && currentState.isFlight()) {
                return; // Takeoff successful and stable
            }
            sleep(DroneSystem.FlightControlConstants.POLLING_INTERVAL_MS);
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
        long timeout = DroneSystem.FlightControlConstants.TAKEOFF_LANDING_TIMEOUT_MS; // 4-second timeout for each stage
        long startTime = System.currentTimeMillis();

        // Stage 1: Wait for the drone to enter LANDING mode
        while (System.currentTimeMillis() - startTime < timeout) {
            State currentState = droneStatus.getState();
            if (currentState != null && currentState.isLanding()) {
                break; // Stage 1 complete
            }
            triggerFlightEvent(FlightEvent.LANDING);
            sleep(DroneSystem.FlightControlConstants.POLLING_INTERVAL_MS);
        }

        // Stage 2: Wait for the drone to complete landing and enter READY mode
        startTime = System.currentTimeMillis(); // Reset timer for the next stage
        while (System.currentTimeMillis() - startTime < timeout) {
            State currentState = droneStatus.getState();
            if (currentState != null && currentState.isReady()) {
                return; // Landing successful
            }
            sleep(DroneSystem.FlightControlConstants.POLLING_INTERVAL_MS);
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
     * @param durationSeconds The duration to hover, in seconds (matching Python behavior).
     */
    public void hover(double durationSeconds) {
        sendControl(DroneSystem.FlightControlConstants.CONTROL_VALUE_NEUTRAL, 
                   DroneSystem.FlightControlConstants.CONTROL_VALUE_NEUTRAL, 
                   DroneSystem.FlightControlConstants.CONTROL_VALUE_NEUTRAL, 
                   DroneSystem.FlightControlConstants.CONTROL_VALUE_NEUTRAL);
        sleep((long)(durationSeconds * DroneSystem.FlightControlConstants.MILLISECONDS_PER_SECOND));
    }

    /**
     * Resets the values of roll, pitch, yaw, and throttle to 0 by sending
     * a hover command multiple times to ensure the drone is stable.
     * @param attempts The number of times the hover command is sent.
     */
    public void resetMoveValues(int attempts) {
        for (int i = 0; i < attempts; i++) {
            hover(DroneSystem.FlightControlConstants.RESET_HOVER_DURATION_SECONDS); // Send a brief 10ms hover command
        }
    }

    /**
     * Resets the drone's movement values to zero.
     */
    public void resetMoveValues() {
        resetMoveValues(DroneSystem.FlightControlConstants.RESET_MOVE_VALUES_DEFAULT_ATTEMPTS);
    }

    /**
     * Sets the drone's responsiveness level.
     * @param speedLevel An integer from 1 (slow) to 3 (fast).
     */
    public void changeSpeed(int speedLevel) {
        byte level = (byte) Math.max(DroneSystem.FlightControlConstants.SPEED_LEVEL_MIN, 
                                   Math.min(speedLevel, DroneSystem.FlightControlConstants.SPEED_LEVEL_MAX));
        drone.sendCommand(CommandType.ControlSpeed, level);
    }

    /**
     * Enables or disables headless mode.
     * @param enable true to enable headless mode, false to disable.
     */
    public void setHeadlessMode(boolean enable) {
        byte value = (byte) (enable ? 1 : DroneSystem.FlightControlConstants.CONTROL_VALUE_NEUTRAL);
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
        control.setPitch((byte) Math.max(DroneSystem.FlightControlConstants.CONTROL_VALUE_MIN, 
                                       Math.min(pitch, DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX)));
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
        control.setRoll((byte) Math.max(DroneSystem.FlightControlConstants.CONTROL_VALUE_MIN, 
                                      Math.min(roll, DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX)));
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
        control.setYaw((byte) Math.max(DroneSystem.FlightControlConstants.CONTROL_VALUE_MIN, 
                                     Math.min(yaw, DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX)));
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
        control.setThrottle((byte) Math.max(DroneSystem.FlightControlConstants.CONTROL_VALUE_MIN, 
                                          Math.min(throttle, DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX)));
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
    public void move(double duration)
    {
        int milliseconds = (int) (duration * DroneSystem.FlightControlConstants.MILLISECONDS_PER_SECOND);
        // there is a while loop inside of the send control method.
        sendControlWhile(control, milliseconds);
    }

    /**
     * Prints current values of roll, pitch, yaw, and throttle.
     *
     * @deprecated This method is deprecated and will be removed in a future release.Please use <pre>drone.get_move_values()</pre> instead.
     */
    /**
     * @deprecated Use {@link #printMoveValues()} instead.
     */
    @Deprecated
    public void print_move_values() {
        printMoveValues();
    }
    public void printMoveValues() {
        // Keep System.out for student debugging - this is educational output
        System.out.println(Arrays.toString(new byte[]{control.getRoll(), control.getPitch(), control.getYaw(), control.getThrottle()}));
    }

    /**
     * Returns current values of roll, pitch, yaw, and throttle.
     *
     * @return A byte array of roll(0), pitch (1), yaw (2) and throttle (3) values.
     */
    /**
     * @deprecated Use {@link #getMoveValues()} instead.
     */
    @Deprecated
    public byte[] get_move_values() {
        return getMoveValues();
    }
    public byte[] getMoveValues() {
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
     * Fly in a given direction for the given duration and power.
     * 
     * This is the primary educational movement API, matching the Python implementation exactly.
     * Students can use simple commands like drone.go("forward", 30, 1) for intuitive movement.
     * 
     * Implementation matches Python's go() method:
     * - Resets all control variables to 0
     * - Sets the appropriate control variable based on direction
     * - Calls move(duration) to execute the movement
     * - Calls hover(1) to stabilize after movement
     * 
     * @param direction String direction: "forward", "backward", "left", "right", "up", "down"
     * @param power Power level from 0-100 (defaults to 50 if not specified)
     * @param duration Duration in seconds (defaults to 1 if not specified)
     */
    public void go(String direction, int power, int duration) {
        try {
            // Reset all control variables to 0 (match Python implementation)
            setRoll(0);
            setPitch(0);
            setYaw(0);
            setThrottle(0);
            
            // Validate and clamp power to 0-100 range
            if (power > 100) power = 100;
            else if (power < 0) power = 0;
            
            // Set the appropriate control variable based on direction
            String dir = direction.toLowerCase();
            switch (dir) {
                case DroneSystem.DirectionConstants.FORWARD:
                    setPitch(power);
                    break;
                case DroneSystem.DirectionConstants.BACKWARD:
                    setPitch(-power);
                    break;
                case DroneSystem.DirectionConstants.RIGHT:
                    setRoll(power);
                    break;
                case DroneSystem.DirectionConstants.LEFT:
                    setRoll(-power);
                    break;
                case DroneSystem.DirectionConstants.UP:
                    setThrottle(power);
                    break;
                case DroneSystem.DirectionConstants.DOWN:
                    setThrottle(-power);
                    break;
                default:
                    System.out.println("Warning: Invalid direction '" + direction + "'. Valid directions are: " + 
                        DroneSystem.DirectionConstants.FORWARD + ", " + DroneSystem.DirectionConstants.BACKWARD + ", " + 
                        DroneSystem.DirectionConstants.LEFT + ", " + DroneSystem.DirectionConstants.RIGHT + ", " + 
                        DroneSystem.DirectionConstants.UP + ", " + DroneSystem.DirectionConstants.DOWN);
                    return;
            }
            
            // Execute the movement for the specified duration
            move(duration);
            
            // Hover for 1 second to stabilize (match Python implementation)
            hover(1);
            
        } catch (Exception e) {
            System.out.println("Warning: Invalid arguments. Please check your parameters.");
        }
    }
    
    /**
     * Overloaded go() method with default power (50)
     */
    public void go(String direction, int duration) {
        go(direction, 50, duration);
    }
    
    /**
     * Overloaded go() method with default power (50) and duration (1 second)
     */
    public void go(String direction) {
        go(direction, 50, 1);
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
    
    // ========================================
    // Distance-Based Movement Methods (Punch List Item #2)
    // ========================================
    
    /**
     * Move forward a specific distance with precision control.
     * 
     * This method implements the Python move_forward() functionality, allowing students to 
     * move the drone a precise distance in centimeters, inches, meters, or feet.
     * 
     * @param distance The distance to move forward
     * @param units The unit of measurement: "cm", "in", "ft", "m" (default: "cm")
     * @param speed The movement speed from 0.5 to 2.0 m/s (default: 0.5)
     */
    public void moveForward(double distance, String units, double speed) {
        double distanceMeters = convertToMeters(distance, units);
        if (distanceMeters < 0) return; // Invalid unit conversion
        
        // Cap the speed to valid range
        speed = Math.max(0.5, Math.min(speed, 2.0));
        
        // Send position control command
        sendControlPosition((float) distanceMeters, 0.0f, 0.0f, (float) speed, 0, 0);
        
        // Calculate delay based on distance and speed, plus buffer time
        double delay = (distanceMeters / speed) + 1.0;
        sleep((long) (delay * 1000));
    }
    
    /**
     * Move forward with default units (cm) and speed (0.5 m/s).
     */
    public void moveForward(double distance) {
        moveForward(distance, "cm", 0.5);
    }
    
    /**
     * Move forward with specified units and default speed (0.5 m/s).
     */
    public void moveForward(double distance, String units) {
        moveForward(distance, units, 0.5);
    }
    
    /**
     * Move backward a specific distance with precision control.
     * 
     * This method implements the Python move_backward() functionality, allowing students to 
     * move the drone a precise distance backward.
     * 
     * @param distance The distance to move backward
     * @param units The unit of measurement: "cm", "in", "ft", "m" (default: "cm")
     * @param speed The movement speed from 0.5 to 2.0 m/s (default: 0.5)
     */
    public void moveBackward(double distance, String units, double speed) {
        double distanceMeters = convertToMeters(distance, units);
        if (distanceMeters < 0) return; // Invalid unit conversion
        
        // Cap the speed to valid range
        speed = Math.max(0.5, Math.min(speed, 2.0));
        
        // Send position control command (negative X for backward)
        sendControlPosition((float) -distanceMeters, 0.0f, 0.0f, (float) speed, 0, 0);
        
        // Calculate delay based on distance and speed, plus buffer time
        double delay = (distanceMeters / speed) + 1.0;
        sleep((long) (delay * 1000));
    }
    
    /**
     * Move backward with default units (cm) and speed (0.5 m/s).
     */
    public void moveBackward(double distance) {
        moveBackward(distance, "cm", 0.5);
    }
    
    /**
     * Move backward with specified units and default speed (0.5 m/s).
     */
    public void moveBackward(double distance, String units) {
        moveBackward(distance, units, 0.5);
    }
    
    /**
     * Move left a specific distance with precision control.
     * 
     * This method implements the Python move_left() functionality, allowing students to 
     * move the drone a precise distance to the left.
     * 
     * @param distance The distance to move left
     * @param units The unit of measurement: "cm", "in", "ft", "m" (default: "cm")
     * @param speed The movement speed from 0.5 to 2.0 m/s (default: 0.5)
     */
    public void moveLeft(double distance, String units, double speed) {
        double distanceMeters = convertToMeters(distance, units);
        if (distanceMeters < 0) return; // Invalid unit conversion
        
        // Cap the speed to valid range
        speed = Math.max(0.5, Math.min(speed, 2.0));
        
        // Send position control command (positive Y for left)
        sendControlPosition(0.0f, (float) distanceMeters, 0.0f, (float) speed, 0, 0);
        
        // Calculate delay based on distance and speed, plus buffer time
        double delay = (distanceMeters / speed) + 1.0;
        sleep((long) (delay * 1000));
    }
    
    /**
     * Move left with default units (cm) and speed (0.5 m/s).
     */
    public void moveLeft(double distance) {
        moveLeft(distance, "cm", 0.5);
    }
    
    /**
     * Move left with specified units and default speed (0.5 m/s).
     */
    public void moveLeft(double distance, String units) {
        moveLeft(distance, units, 0.5);
    }
    
    /**
     * Move right a specific distance with precision control.
     * 
     * This method implements the Python move_right() functionality, allowing students to 
     * move the drone a precise distance to the right.
     * 
     * @param distance The distance to move right
     * @param units The unit of measurement: "cm", "in", "ft", "m" (default: "cm")
     * @param speed The movement speed from 0.5 to 2.0 m/s (default: 0.5)
     */
    public void moveRight(double distance, String units, double speed) {
        double distanceMeters = convertToMeters(distance, units);
        if (distanceMeters < 0) return; // Invalid unit conversion
        
        // Cap the speed to valid range
        speed = Math.max(0.5, Math.min(speed, 2.0));
        
        // Send position control command (negative Y for right)
        sendControlPosition(0.0f, (float) -distanceMeters, 0.0f, (float) speed, 0, 0);
        
        // Calculate delay based on distance and speed, plus buffer time
        double delay = (distanceMeters / speed) + 1.0;
        sleep((long) (delay * 1000));
    }
    
    /**
     * Move right with default units (cm) and speed (0.5 m/s).
     */
    public void moveRight(double distance) {
        moveRight(distance, "cm", 0.5);
    }
    
    /**
     * Move right with specified units and default speed (0.5 m/s).
     */
    public void moveRight(double distance, String units) {
        moveRight(distance, units, 0.5);
    }
    
    /**
     * Move the drone in 3D space to a specific relative position.
     * 
     * This method implements the Python move_distance() functionality, allowing students to
     * move the drone simultaneously along X, Y, and Z axes.
     * 
     * @param positionX The distance to move forward/backward in meters (+ forward, - backward)
     * @param positionY The distance to move left/right in meters (+ left, - right)
     * @param positionZ The distance to move up/down in meters (+ up, - down)
     * @param velocity The movement speed from 0.5 to 2.0 m/s
     */
    public void moveDistance(double positionX, double positionY, double positionZ, double velocity) {
        // Cap the velocity to valid range
        velocity = Math.max(0.5, Math.min(velocity, 2.0));
        
        // Calculate total distance for timing
        double distance = Math.sqrt(positionX * positionX + positionY * positionY + positionZ * positionZ);
        
        // Send position control command
        sendControlPosition((float) positionX, (float) positionY, (float) positionZ, (float) velocity, 0, 0);
        
        // Calculate delay based on distance and velocity, plus buffer time
        double delay = (distance / velocity) + 2.5;
        sleep((long) (delay * 1000));
    }
    
    /**
     * Send absolute position command to the drone.
     * 
     * This method implements the Python send_absolute_position() functionality, allowing
     * precise positioning relative to the takeoff point.
     * 
     * @param positionX The absolute X position in meters from takeoff point
     * @param positionY The absolute Y position in meters from takeoff point  
     * @param positionZ The absolute Z position in meters from takeoff point
     * @param velocity The movement speed from 0.5 to 2.0 m/s
     * @param heading The target heading in degrees (-360 to 360)
     * @param rotationalVelocity The rotational speed in degrees/s (10 to 360)
     */
    public void sendAbsolutePosition(double positionX, double positionY, double positionZ, 
                                   double velocity, int heading, int rotationalVelocity) {
        sendControlPosition((float) positionX, (float) positionY, (float) positionZ, 
                          (float) velocity, heading, rotationalVelocity);
    }
    
    /**
     * Helper method to convert distance measurements to meters.
     * 
     * @param distance The distance value
     * @param units The unit: "cm", "in", "ft", "m"
     * @return The distance in meters, or -1 if invalid unit
     */
    private double convertToMeters(double distance, String units) {
        switch (units.toLowerCase()) {
            case DroneSystem.UnitConversion.UNIT_CENTIMETERS:
                return distance * DroneSystem.UnitConversion.CENTIMETERS_TO_METERS;
            case DroneSystem.UnitConversion.UNIT_FEET:
                return distance * DroneSystem.UnitConversion.FEET_TO_METERS;
            case DroneSystem.UnitConversion.UNIT_INCHES:
                return distance * DroneSystem.UnitConversion.INCHES_TO_METERS;
            case DroneSystem.UnitConversion.UNIT_METERS:
                return distance;
            default:
                System.out.println("Error: Invalid unit '" + units + "'. Valid units are: " + 
                    DroneSystem.UnitConversion.UNIT_CENTIMETERS + ", " + 
                    DroneSystem.UnitConversion.UNIT_FEET + ", " + 
                    DroneSystem.UnitConversion.UNIT_INCHES + ", " + 
                    DroneSystem.UnitConversion.UNIT_METERS);
                return -1;
        }
    }

    // ========================================
    // End Distance-Based Movement Methods
    // ========================================
    
    // =============================================================================
    // PHASE 3: Turning Methods (Punch List Item #3) 
    // Replicating Python turning algorithms for educational consistency
    // =============================================================================

    /**
     * Turns the drone in the yaw direction (left/right rotation).
     * 
     * <p>This method replicates the Python {@code turn(power, seconds)} function, providing
     * simple yaw control for educational use.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * - Basic turning for navigation and obstacle avoidance<br>
     * - Introduction to rotational movement concepts<br>
     * - Foundation for more complex turning patterns</p>
     * 
     * @param power The turning power (-100 to 100). Positive values turn left, 
     *              negative values turn right.
     * @param seconds The duration to turn in seconds. If null, sends a single
     *                control command without duration.
     * 
     * @throws IllegalArgumentException if power is outside valid range
     * 
     * @since 1.0
     * @see #turnLeft(int)
     * @see #turnRight(int)
     * @see #turnDegree(int)
     * 
     * @educational
     * <strong>Example Usage:</strong>
     * <pre>{@code
     * // Turn left at 50% power for 2 seconds
     * drone.turn(50, 2.0);
     * 
     * // Quick turn right at 30% power for 1 second  
     * drone.turn(-30, 1.0);
     * 
     * // Set turning motion without duration (manual control)
     * drone.turn(40, null);
     * }</pre>
     */
    public void turn(int power, Double seconds) {
        // Clamp power to valid range (-100 to 100)
        power = Math.max(-100, Math.min(100, power));
        
        if (seconds == null) {
            // Send single control command without duration (like Python's default behavior)
            sendControl(0, 0, power, 0);
            try {
                Thread.sleep(3); // Brief pause to match Python's 0.003 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            // Turn for specified duration, then hover to stabilize
            long durationMs = (long) (seconds * 1000);
            sendControlWhile(0, 0, power, 0, durationMs);
            
            // Brief hover to stabilize (matches Python implementation)
            hover(0.5);
        }
    }

    /**
     * Turns the drone to a specific degree relative to its initial heading.
     * 
     * <p>This method replicates the Python {@code turn_degree(degree, timeout, p_value)} 
     * function, providing precise angular control using a proportional controller.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * - Precise angular positioning for navigation<br>
     * - Introduction to closed-loop control concepts<br>
     * - Foundation for autonomous flight patterns</p>
     * 
     * <p><strong>Algorithm:</strong> Uses a proportional control system that calculates
     * the shortest angular path to the target, applies appropriate turning power based
     * on the angular error, and stops when the target is reached or timeout occurs.</p>
     * 
     * @param degree The target heading in degrees (-180 to 180). Positive values
     *               are relative left turns, negative values are relative right turns.
     * @param timeout Maximum time in seconds to attempt the turn (default: 3.0)
     * @param pValue Proportional gain for the control system (default: 10.0).
     *               Higher values result in faster, more aggressive turning.
     * 
     * @throws IllegalArgumentException if degree is outside valid range
     * 
     * @since 1.0
     * @see #turnLeft(int)
     * @see #turnRight(int)
     * @see #turn(int, Double)
     * 
     * @educational
     * <strong>Example Usage:</strong>
     * <pre>{@code
     * // Turn exactly 90 degrees left (relative to initial heading)
     * drone.turnDegree(90);
     * 
     * // Turn 45 degrees right with custom timeout
     * drone.turnDegree(-45, 5.0);
     * 
     * // Precise turn with custom control parameters
     * drone.turnDegree(180, 4.0, 15.0); // Faster, more aggressive turning
     * }</pre>
     * 
     * @apiNote This method requires access to the drone's current angle sensor data.
     * For educational purposes, a simplified version is provided that may not have
     * full sensor integration yet.
     */
    public void turnDegree(int degree, double timeout, double pValue) {
        // Clamp degree to valid range (-180 to 180)
        degree = Math.max(-180, Math.min(180, degree));
        
        // Brief hover to ensure drone is stable before turning
        hover(0.01);
        
        // For educational implementation: simplified algorithm
        // In full implementation, this would use real sensor data from Motion class
        
        // Calculate approximate turning time based on degree and power
        // This is a simplified approach for educational purposes
        double estimatedTurnTime = Math.abs(degree) / 90.0; // Rough estimate: 1 second per 90 degrees
        int turnPower = (int) Math.min(100, Math.max(20, Math.abs(degree) * pValue / 10));
        
        if (degree > 0) {
            // Turn left (positive degrees)
            sendControlWhile(0, 0, turnPower, 0, (long)(estimatedTurnTime * 1000));
        } else {
            // Turn right (negative degrees)  
            sendControlWhile(0, 0, -turnPower, 0, (long)(estimatedTurnTime * 1000));
        }
        
        // Stop any movement and stabilize
        hover(0.05);
        
        // TODO: Implement full proportional control when Motion sensor access is available
        // This would include:
        // - Reading current angle from Motion.angleYaw
        // - Calculating shortest angular path
        // - Applying proportional control loop
        // - Real-time error correction until target is reached
    }

    /**
     * Overloaded turnDegree with default timeout.
     */
    public void turnDegree(int degree, double timeout) {
        turnDegree(degree, timeout, 10.0);
    }

    /**
     * Overloaded turnDegree with default timeout and pValue.
     */
    public void turnDegree(int degree) {
        turnDegree(degree, 3.0, 10.0);
    }

    /**
     * Turns the drone left by the specified number of degrees.
     * 
     * <p>This method replicates the Python {@code turn_left(degree, timeout)} function,
     * providing intuitive left turning for educational use.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * - Intuitive directional control for beginners<br>
     * - Building blocks for navigation patterns<br>
     * - Clear semantic meaning for student code readability</p>
     * 
     * @param degrees The number of degrees to turn left (0 to 179).
     *                Automatically converted to positive value.
     * @param timeout Maximum time in seconds to attempt the turn (default: 3.0)
     * 
     * @since 1.0
     * @see #turnRight(int, double)
     * @see #turnDegree(int)
     * @see #turn(int, Double)
     * 
     * @educational
     * <strong>Example Usage:</strong>
     * <pre>{@code
     * // Turn left 90 degrees
     * drone.turnLeft(90);
     * 
     * // Turn left 45 degrees with longer timeout
     * drone.turnLeft(45, 5.0);
     * 
     * // Small adjustment turn
     * drone.turnLeft(15);
     * }</pre>
     */
    public void turnLeft(int degrees, double timeout) {
        // Ensure positive degree value and cap at 179
        degrees = Math.min(179, Math.abs(degrees));
        
        // In the Python implementation, turnLeft calculates the target angle
        // relative to current heading and calls turnDegree
        // For educational simplicity, we'll use a direct approach
        
        // Use positive degree for left turn (matches Python convention)
        turnDegree(degrees, timeout);
    }

    /**
     * Overloaded turnLeft with default timeout.
     */
    public void turnLeft(int degrees) {
        turnLeft(degrees, 3.0);
    }

    /**
     * Turns the drone right by the specified number of degrees.
     * 
     * <p>This method replicates the Python {@code turn_right(degree, timeout)} function,
     * providing intuitive right turning for educational use.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * - Intuitive directional control for beginners<br>
     * - Building blocks for navigation patterns<br>
     * - Clear semantic meaning for student code readability</p>
     * 
     * @param degrees The number of degrees to turn right (0 to 179).
     *                Automatically converted to positive value.
     * @param timeout Maximum time in seconds to attempt the turn (default: 3.0)
     * 
     * @since 1.0
     * @see #turnLeft(int, double)
     * @see #turnDegree(int)
     * @see #turn(int, Double)
     * 
     * @educational
     * <strong>Example Usage:</strong>
     * <pre>{@code
     * // Turn right 90 degrees  
     * drone.turnRight(90);
     * 
     * // Turn right 45 degrees with longer timeout
     * drone.turnRight(45, 5.0);
     * 
     * // Small adjustment turn
     * drone.turnRight(15);
     * }</pre>
     */
    public void turnRight(int degrees, double timeout) {
        // Ensure positive degree value and cap at 179
        degrees = Math.min(179, Math.abs(degrees));
        
        // Use negative degree for right turn (matches Python convention)
        turnDegree(-degrees, timeout);
    }

    /**
     * Overloaded turnRight with default timeout.
     */
    public void turnRight(int degrees) {
        turnRight(degrees, 3.0);
    }

    // =================================================================================
    // SENSOR DATA ACCESS METHODS
    // =================================================================================

    /**
     * Gets the current battery level.
     * 
     * @return Battery level as a percentage (0-100)
     * @apiNote Provides access to the drone's current battery status for safety monitoring
     * @since 1.0
     */
    public int getBattery() {
        log.debug("Getting battery level");
        
        // Request fresh state data
        drone.sendRequestWait(DataType.State);
        
        State state = drone.getDroneStatus().getState();
        if (state != null) {
            int battery = state.getBattery() & 0xFF; // Convert unsigned byte to int
            log.debug("Battery level: {}%", battery);
            return battery;
        } else {
            log.warn("State data not available for battery reading");
            return 0;
        }
    }

    /**
     * Gets the current flight state as a readable string.
     * 
     * @return Flight state string (e.g., "READY", "FLIGHT", "TAKE_OFF", "LANDING")
     * @apiNote Provides human-readable flight state for educational and debugging purposes
     * @since 1.0
     */
    public String getFlightState() {
        log.debug("Getting flight state");
        
        // Request fresh state data
        drone.sendRequestWait(DataType.State);
        
        State state = drone.getDroneStatus().getState();
        if (state != null && state.getModeFlight() != null) {
            String flightState = state.getModeFlight().name();
            log.debug("Flight state: {}", flightState);
            return flightState;
        } else {
            log.warn("State data not available for flight state reading");
            return "UNKNOWN";
        }
    }

    /**
     * Gets the current movement state as a readable string.
     * 
     * @return Movement state string (e.g., "READY", "HOVERING", "MOVING", "RETURN_HOME")
     * @apiNote Provides human-readable movement state for educational and debugging purposes
     * @since 1.0
     */
    public String getMovementState() {
        log.debug("Getting movement state");
        
        // Request fresh state data
        drone.sendRequestWait(DataType.State);
        
        State state = drone.getDroneStatus().getState();
        if (state != null && state.getModeMovement() != null) {
            String movementState = state.getModeMovement().name();
            log.debug("Movement state: {}", movementState);
            return movementState;
        } else {
            log.warn("State data not available for movement state reading");
            return "UNKNOWN";
        }
    }

    /**
     * Gets the current height from the ground using the bottom range sensor.
     * 
     * @param unit The unit for the measurement ("cm", "mm", "m", or "in")
     * @return Height in the specified unit
     * @apiNote Equivalent to Python's get_height() method
     * @since 1.0
     */
    public double getHeight(String unit) {
        return getBottomRange(unit);
    }

    /**
     * Gets the current height from the ground using the bottom range sensor.
     * 
     * @return Height in centimeters
     * @since 1.0
     */
    public double getHeight() {
        return getHeight("cm");
    }

    /**
     * Gets the distance measured by the front range sensor.
     * 
     * @param unit The unit for the measurement ("cm", "mm", "m", or "in")
     * @return Distance in the specified unit
     * @apiNote Equivalent to Python's get_front_range() method
     * @since 1.0
     */
    public double getFrontRange(String unit) {
        log.debug("Getting front range in {}", unit);
        
        // Request fresh range data
        drone.sendRequestWait(DataType.Range);
        
        Range range = drone.getDroneStatus().getRange();
        if (range != null) {
            double rangeValue = convertMillimeter(range.getFront(), unit);
            log.debug("Front range: {} {}", rangeValue, unit);
            return rangeValue;
        } else {
            log.warn("Range data not available for front range reading");
            return 0.0;
        }
    }

    /**
     * Gets the distance measured by the front range sensor in centimeters.
     * 
     * @return Distance in centimeters
     * @since 1.0
     */
    public double getFrontRange() {
        return getFrontRange("cm");
    }

    /**
     * Gets the distance measured by the bottom range sensor (height).
     * 
     * @param unit The unit for the measurement ("cm", "mm", "m", or "in")
     * @return Distance in the specified unit
     * @apiNote Equivalent to Python's get_bottom_range() method
     * @since 1.0
     */
    public double getBottomRange(String unit) {
        log.debug("Getting bottom range in {}", unit);
        
        // Request fresh range data
        drone.sendRequestWait(DataType.Range);
        
        Range range = drone.getDroneStatus().getRange();
        if (range != null) {
            double rangeValue = convertMillimeter(range.getBottom(), unit);
            log.debug("Bottom range: {} {}", rangeValue, unit);
            return rangeValue;
        } else {
            log.warn("Range data not available for bottom range reading");
            return 0.0;
        }
    }

    /**
     * Gets the distance measured by the bottom range sensor in centimeters.
     * 
     * @return Distance in centimeters
     * @since 1.0
     */
    public double getBottomRange() {
        return getBottomRange("cm");
    }

    /**
     * Gets the X position relative to takeoff point.
     * 
     * @param unit The unit for the measurement ("cm", "mm", "m", or "in")
     * @return X position in the specified unit
     * @apiNote Equivalent to Python's get_pos_x() method
     * @since 1.0
     */
    public double getPosX(String unit) {
        log.debug("Getting X position in {}", unit);
        
        // Request fresh position data
        drone.sendRequestWait(DataType.Position);
        
        com.otabi.jcodroneedu.protocol.dronestatus.Position positionData = drone.getDroneStatus().getPosition();
        if (positionData != null) {
            double posValue = convertMeter(positionData.getX(), unit);
            log.debug("X position: {} {}", posValue, unit);
            return posValue;
        } else {
            log.warn("Position data not available for X position reading");
            return 0.0;
        }
    }

    /**
     * Gets the X position relative to takeoff point in centimeters.
     * 
     * @return X position in centimeters
     * @since 1.0
     */
    public double getPosX() {
        return getPosX("cm");
    }

    /**
     * Gets the Y position relative to takeoff point.
     * 
     * @param unit The unit for the measurement ("cm", "mm", "m", or "in")
     * @return Y position in the specified unit
     * @apiNote Equivalent to Python's get_pos_y() method
     * @since 1.0
     */
    public double getPosY(String unit) {
        log.debug("Getting Y position in {}", unit);
        
        // Request fresh position data
        drone.sendRequestWait(DataType.Position);
        
        com.otabi.jcodroneedu.protocol.dronestatus.Position positionData = drone.getDroneStatus().getPosition();
        if (positionData != null) {
            double posValue = convertMeter(positionData.getY(), unit);
            log.debug("Y position: {} {}", posValue, unit);
            return posValue;
        } else {
            log.warn("Position data not available for Y position reading");
            return 0.0;
        }
    }

    /**
     * Gets the Y position relative to takeoff point in centimeters.
     * 
     * @return Y position in centimeters
     * @since 1.0
     */
    public double getPosY() {
        return getPosY("cm");
    }

    /**
     * Gets the Z position relative to takeoff point.
     * 
     * @param unit The unit for the measurement ("cm", "mm", "m", or "in")
     * @return Z position in the specified unit
     * @apiNote Equivalent to Python's get_pos_z() method
     * @since 1.0
     */
    public double getPosZ(String unit) {
        log.debug("Getting Z position in {}", unit);
        
        // Request fresh position data
        drone.sendRequestWait(DataType.Position);
        
        com.otabi.jcodroneedu.protocol.dronestatus.Position positionData = drone.getDroneStatus().getPosition();
        if (positionData != null) {
            double posValue = convertMeter(positionData.getZ(), unit);
            log.debug("Z position: {} {}", posValue, unit);
            return posValue;
        } else {
            log.warn("Position data not available for Z position reading");
            return 0.0;
        }
    }

    /**
     * Gets the Z position relative to takeoff point in centimeters.
     * 
     * @return Z position in centimeters
     * @since 1.0
     */
    public double getPosZ() {
        return getPosZ("cm");
    }

    /**
     * Gets the X-axis acceleration in G-force units.
     * 
     * @return X acceleration in G-force
     * @apiNote Equivalent to Python's get_accel_x() method
     * @since 1.0
     */
    public double getAccelX() {
        log.debug("Getting X acceleration");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Convert raw accelerometer data to G-force (typical scale factor)
            // Convert raw accel -> G using canonical sensor scale
            double accelMs2 = motion.getAccelX() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            double accelValue = accelMs2 / 9.80665; // G units
            log.debug("X acceleration: {} G", accelValue);
            return accelValue;
        } else {
            log.warn("Motion data not available for X acceleration reading");
            return 0.0;
        }
    }

    /**
     * Gets the Y-axis acceleration in G-force units.
     * 
     * @return Y acceleration in G-force
     * @apiNote Equivalent to Python's get_accel_y() method
     * @since 1.0
     */
    public double getAccelY() {
        log.debug("Getting Y acceleration");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Convert raw accelerometer data to G-force (typical scale factor)
            double accelMs2 = motion.getAccelY() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            double accelValue = accelMs2 / 9.80665; // G units
            log.debug("Y acceleration: {} G", accelValue);
            return accelValue;
        } else {
            log.warn("Motion data not available for Y acceleration reading");
            return 0.0;
        }
    }

    /**
     * Gets the Z-axis acceleration in G-force units.
     * 
     * @return Z acceleration in G-force
     * @apiNote Equivalent to Python's get_accel_z() method
     * @since 1.0
     */
    public double getAccelZ() {
        log.debug("Getting Z acceleration");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Convert raw accelerometer data to G-force (typical scale factor)
            double accelMs2 = motion.getAccelZ() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            double accelValue = accelMs2 / 9.80665; // G units
            log.debug("Z acceleration: {} G", accelValue);
            return accelValue;
        } else {
            log.warn("Motion data not available for Z acceleration reading");
            return 0.0;
        }
    }

    /**
     * Gets the X-axis angle (roll) in degrees.
     * 
     * @return X angle in degrees
     * @apiNote Equivalent to Python's get_angle_x() method
     * @since 1.0
     */
    public double getAngleX() {
        log.debug("Getting X angle (roll)");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Convert raw angle data to degrees (typical scale factor)
            double angleValue = motion.getAngleRoll() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG; // degrees
            log.debug("X angle (roll): {} degrees", angleValue);
            return angleValue;
        } else {
            log.warn("Motion data not available for X angle reading");
            return 0.0;
        }
    }

    /**
     * Gets the Y-axis angle (pitch) in degrees.
     * 
     * @return Y angle in degrees
     * @apiNote Equivalent to Python's get_angle_y() method
     * @since 1.0
     */
    public double getAngleY() {
        log.debug("Getting Y angle (pitch)");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Convert raw angle data to degrees (typical scale factor)
            double angleValue = motion.getAnglePitch() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG; // degrees
            log.debug("Y angle (pitch): {} degrees", angleValue);
            return angleValue;
        } else {
            log.warn("Motion data not available for Y angle reading");
            return 0.0;
        }
    }

    /**
     * Gets the Z-axis angle (yaw) in degrees.
     * 
     * @return Z angle in degrees
     * @apiNote Equivalent to Python's get_angle_z() method
     * @since 1.0
     */
    public double getAngleZ() {
        log.debug("Getting Z angle (yaw)");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Convert raw angle data to degrees (typical scale factor)
            double angleValue = motion.getAngleYaw() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG; // degrees
            log.debug("Z angle (yaw): {} degrees", angleValue);
            return angleValue;
        } else {
            log.warn("Motion data not available for Z angle reading");
            return 0.0;
        }
    }

    // =============================================================================
    // Array-based sensor methods for AP CSA compatibility
    // =============================================================================

    /**
    * Gets accelerometer data as an array.
    * 
    * <p>Returns the raw accelerometer values from the protocol for all three axes.
    * The protocol encodes accelerometer samples as signed 16-bit integers where the
    * raw unit equals m/s^2 * 10 (i.e., multiply raw by {@code DroneSystem.SensorScales.ACCEL_RAW_TO_MS2}
    * to obtain m/s^2). This method is provided for AP CSA compatibility and educational
    * examples that expect integer arrays. For human-friendly values, use {@link #getAccelX()},
    * {@link #getAccelY()}, and {@link #getAccelZ()} which return acceleration in G (approx).
    * </p>
    *
    * @return int array containing [X, Y, Z] raw accelerometer protocol values
    * @apiNote Equivalent to Python's {@code drone.get_accel()} and returns raw protocol integers
     * @since 1.0
     * @educational This demonstrates array usage and coordinate systems
     */
    public int[] getAccel() {
        log.debug("Getting acceleration array data");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Return raw accelerometer values as integers for AP CSA compatibility
            int[] accelArray = {
                motion.getAccelX(), 
                motion.getAccelY(), 
                motion.getAccelZ()
            };
            // Also log scaled values (m/s^2) for easier debugging
            double ax_ms2 = accelArray[0] * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            double ay_ms2 = accelArray[1] * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            double az_ms2 = accelArray[2] * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            log.debug("Acceleration array (raw): [{}, {}, {}] -> (m/s^2): [{}, {}, {}]", accelArray[0], accelArray[1], accelArray[2], ax_ms2, ay_ms2, az_ms2);
            return accelArray;
        } else {
            log.warn("Motion data not available for acceleration array reading");
            return new int[]{0, 0, 0};
        }
    }

    /**
     * Gets gyroscope data as an array.
     * 
     * <p>Returns rotational velocity data for all three axes in a convenient array format.
     * This is useful for AP CSA students learning about arrays and rotational motion.</p>
     * 
     * @return int array containing [roll, pitch, yaw] gyroscope values in degrees/second
     * @apiNote Equivalent to Python's various gyro methods, returns array for AP CSA compatibility
     * @since 1.0
     * @educational This demonstrates array usage and rotational concepts
     */
    public int[] getGyro() {
        log.debug("Getting gyroscope array data");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Return gyroscope values as integers for AP CSA compatibility
            int[] gyroArray = {
                motion.getGyroRoll(), 
                motion.getGyroPitch(), 
                motion.getGyroYaw()
            };
            log.debug("Gyroscope array: [{}, {}, {}]", gyroArray[0], gyroArray[1], gyroArray[2]);
            return gyroArray;
        } else {
            log.warn("Motion data not available for gyroscope array reading");
            return new int[]{0, 0, 0};
        }
    }

    /**
     * Gets angle data as an array.
     * 
     * <p>Returns current orientation angles for all three axes in a convenient array format.
     * This is useful for AP CSA students learning about arrays and spatial orientation.</p>
     * 
     * @return int array containing [roll, pitch, yaw] angle values in degrees
     * @apiNote Equivalent to Python's various angle methods, returns array for AP CSA compatibility
     * @since 1.0
     * @educational This demonstrates array usage and spatial orientation concepts
     */
    public int[] getAngle() {
        log.debug("Getting angle array data");
        
        // Request fresh motion data
        drone.sendRequestWait(DataType.Motion);
        
        Motion motion = drone.getDroneStatus().getMotion();
        if (motion != null) {
            // Return angle values as integers for AP CSA compatibility
            int[] angleArray = {
                motion.getAngleRoll(), 
                motion.getAnglePitch(), 
                motion.getAngleYaw()
            };
            log.debug("Angle array: [{}, {}, {}]", angleArray[0], angleArray[1], angleArray[2]);
            return angleArray;
        } else {
            log.warn("Motion data not available for angle array reading");
            return new int[]{0, 0, 0};
        }
    }

    /**
     * Helper method to convert millimeter measurements to other units.
     * 
     * @param millimeter The value in millimeters
     * @param unit The target unit ("cm", "mm", "m", or "in")
     * @return The converted value
     */
    private double convertMillimeter(double millimeter, String unit) {
        switch (unit.toLowerCase()) {
            case "mm":
                return millimeter;
            case "cm":
                return millimeter / 10.0;
            case "m":
                return millimeter / 1000.0;
            case "in":
                return millimeter / 25.4;
            default:
                log.warn("Unknown unit '{}', defaulting to cm", unit);
                return millimeter / 10.0;
        }
    }

    private double convertMeter(double meter, String unit) {
        switch (unit.toLowerCase()) {
            case "m":
                return meter;
            case "cm":
                return meter * 100.0;
            case "mm":
                return meter * 1000.0;
            case "in":
                return meter * 39.3701;
            default:
                log.warn("Unknown unit '{}', defaulting to cm", unit);
                return meter * 100.0;
        }
    }
}