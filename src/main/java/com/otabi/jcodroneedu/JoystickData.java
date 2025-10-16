package com.otabi.jcodroneedu;

/**
 * Immutable data class representing joystick state from the CoDrone EDU controller.
 * 
 * <p>This class provides a type-safe, Java-idiomatic way to access joystick data,
 * recommended over the array-based {@link Drone#getJoystickData()} method.</p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Recommended Java approach
 * JoystickData joystickData = drone.getJoystickDataObject();
 * System.out.println("Left stick: X=" + joystickData.getLeftX() + 
 *                    ", Y=" + joystickData.getLeftY());
 * System.out.println("Right stick: X=" + joystickData.getRightX() + 
 *                    ", Y=" + joystickData.getRightY());
 * System.out.println("Timestamp: " + joystickData.getTimestamp());
 * 
 * // Use joystick values for control
 * if (Math.abs(joystickData.getLeftY()) > 20) {
 *     System.out.println("Left stick moved vertically!");
 * }
 * }</pre>
 * 
 * <h3>Joystick Values:</h3>
 * <ul>
 *   <li>X and Y values range from -100 to 100</li>
 *   <li>0 represents neutral/center position</li>
 *   <li>Positive Y = forward/up, Negative Y = backward/down</li>
 *   <li>Positive X = right, Negative X = left</li>
 * </ul>
 * 
 * @author JCoDroneEdu Development Team
 * @since 1.0.0
 * @see Drone#getJoystickDataObject()
 */
public class JoystickData {
    private final int timestamp;
    private final int leftX;
    private final int leftY;
    private final int leftDirection;
    private final int leftEvent;
    private final int rightX;
    private final int rightY;
    private final int rightDirection;
    private final int rightEvent;
    
    /**
     * Creates a new JoystickData instance.
     * 
     * @param timestamp The timestamp in seconds when the joystick data was captured
     * @param leftX Left joystick X value (-100 to 100)
     * @param leftY Left joystick Y value (-100 to 100)
     * @param leftDirection Left joystick direction
     * @param leftEvent Left joystick event
     * @param rightX Right joystick X value (-100 to 100)
     * @param rightY Right joystick Y value (-100 to 100)
     * @param rightDirection Right joystick direction
     * @param rightEvent Right joystick event
     */
    public JoystickData(int timestamp, int leftX, int leftY, int leftDirection, int leftEvent,
                        int rightX, int rightY, int rightDirection, int rightEvent) {
        this.timestamp = timestamp;
        this.leftX = leftX;
        this.leftY = leftY;
        this.leftDirection = leftDirection;
        this.leftEvent = leftEvent;
        this.rightX = rightX;
        this.rightY = rightY;
        this.rightDirection = rightDirection;
        this.rightEvent = rightEvent;
    }
    
    /**
     * Gets the timestamp when this joystick data was captured.
     * 
     * @return Timestamp in seconds
     */
    public int getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the left joystick X (horizontal) value.
     * 
     * @return X value from -100 to 100 (0 is neutral)
     */
    public int getLeftX() {
        return leftX;
    }
    
    /**
     * Gets the left joystick Y (vertical) value.
     * 
     * @return Y value from -100 to 100 (0 is neutral)
     */
    public int getLeftY() {
        return leftY;
    }
    
    /**
     * Gets the left joystick direction value.
     * 
     * @return Direction value
     */
    public int getLeftDirection() {
        return leftDirection;
    }
    
    /**
     * Gets the left joystick event value.
     * 
     * @return Event value
     */
    public int getLeftEvent() {
        return leftEvent;
    }
    
    /**
     * Gets the right joystick X (horizontal) value.
     * 
     * @return X value from -100 to 100 (0 is neutral)
     */
    public int getRightX() {
        return rightX;
    }
    
    /**
     * Gets the right joystick Y (vertical) value.
     * 
     * @return Y value from -100 to 100 (0 is neutral)
     */
    public int getRightY() {
        return rightY;
    }
    
    /**
     * Gets the right joystick direction value.
     * 
     * @return Direction value
     */
    public int getRightDirection() {
        return rightDirection;
    }
    
    /**
     * Gets the right joystick event value.
     * 
     * @return Event value
     */
    public int getRightEvent() {
        return rightEvent;
    }
    
    /**
     * Returns a string representation of this joystick data.
     * 
     * @return String representation
     */
    @Override
    public String toString() {
        return String.format("JoystickData{timestamp=%d, leftX=%d, leftY=%d, rightX=%d, rightY=%d}", 
                           timestamp, leftX, leftY, rightX, rightY);
    }
}
