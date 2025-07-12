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
     * @param durationSeconds The duration to hover, in seconds (matching Python behavior).
     */
    public void hover(double durationSeconds) {
        sendControl(0, 0, 0, 0);
        sleep((long)(durationSeconds * 1000));
    }

    /**
     * Resets the values of roll, pitch, yaw, and throttle to 0 by sending
     * a hover command multiple times to ensure the drone is stable.
     * @param attempts The number of times the hover command is sent.
     */
    public void resetMoveValues(int attempts) {
        for (int i = 0; i < attempts; i++) {
            hover(0.01); // Send a brief 10ms hover command
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
                case "forward":
                    setPitch(power);
                    break;
                case "backward":
                    setPitch(-power);
                    break;
                case "right":
                    setRoll(power);
                    break;
                case "left":
                    setRoll(-power);
                    break;
                case "up":
                    setThrottle(power);
                    break;
                case "down":
                    setThrottle(-power);
                    break;
                default:
                    System.out.println("Warning: Invalid direction '" + direction + "'. Valid directions are: forward, backward, left, right, up, down");
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
     * @param speed The movement speed from 0.5 to 2.0 m/s (default: 1.0)
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
     * Move backward with default units (cm) and speed (1.0 m/s).
     */
    public void moveBackward(double distance) {
        moveBackward(distance, "cm", 1.0);
    }
    
    /**
     * Move backward with specified units and default speed (1.0 m/s).
     */
    public void moveBackward(double distance, String units) {
        moveBackward(distance, units, 1.0);
    }
    
    /**
     * Move left a specific distance with precision control.
     * 
     * This method implements the Python move_left() functionality, allowing students to 
     * move the drone a precise distance to the left.
     * 
     * @param distance The distance to move left
     * @param units The unit of measurement: "cm", "in", "ft", "m" (default: "cm")
     * @param speed The movement speed from 0.5 to 2.0 m/s (default: 1.0)
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
     * Move left with default units (cm) and speed (1.0 m/s).
     */
    public void moveLeft(double distance) {
        moveLeft(distance, "cm", 1.0);
    }
    
    /**
     * Move left with specified units and default speed (1.0 m/s).
     */
    public void moveLeft(double distance, String units) {
        moveLeft(distance, units, 1.0);
    }
    
    /**
     * Move right a specific distance with precision control.
     * 
     * This method implements the Python move_right() functionality, allowing students to 
     * move the drone a precise distance to the right.
     * 
     * @param distance The distance to move right
     * @param units The unit of measurement: "cm", "in", "ft", "m" (default: "cm")
     * @param speed The movement speed from 0.5 to 2.0 m/s (default: 1.0)
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
     * Move right with default units (cm) and speed (1.0 m/s).
     */
    public void moveRight(double distance) {
        moveRight(distance, "cm", 1.0);
    }
    
    /**
     * Move right with specified units and default speed (1.0 m/s).
     */
    public void moveRight(double distance, String units) {
        moveRight(distance, units, 1.0);
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
            case "cm":
                return distance / 100.0;
            case "ft":
                return distance / 3.28084;
            case "in":
                return distance / 39.37;
            case "m":
                return distance;
            default:
                System.out.println("Error: Invalid unit '" + units + "'. Valid units are: cm, ft, in, m");
                return -1;
        }
    }

    // ========================================
    // End Distance-Based Movement Methods
    // ========================================
}