package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.controllerinput.Button;
import com.otabi.jcodroneedu.protocol.controllerinput.Joystick;

/**
 * Manages controller input data (buttons and joysticks) from the CoDrone EDU remote controller.
 * 
 * <p>This manager stores button and joystick state with properly named fields and provides
 * three access patterns:</p>
 * <ol>
 *   <li><strong>Python-compatible arrays</strong> - for translation compatibility</li>
 *   <li><strong>Individual typed getters</strong> - for simple value queries</li>
 *   <li><strong>Java composite objects</strong> - recommended for type-safe Java code</li>
 * </ol>
 * 
 * <p><strong>Thread Safety:</strong> All setter methods are synchronized to ensure thread-safe
 * updates from the receiver thread. Getter methods return defensive copies of mutable data.</p>
 * 
 * @author JCoDroneEdu Development Team
 * @since 1.0.0
 */
public class ControllerInputManager {
    
    // --- Button State (with named fields) ---
    private volatile double buttonTimestamp = 0.0;
    private volatile int buttonFlags = 0;
    private volatile String buttonEventName = "None_";
    
    // --- Joystick State (with named fields) ---
    private volatile int joystickTimestamp = 0;
    private volatile int leftX = 0;
    private volatile int leftY = 0;
    private volatile int leftDirection = 0;
    private volatile int leftEvent = 0;
    private volatile int rightX = 0;
    private volatile int rightY = 0;
    private volatile int rightDirection = 0;
    private volatile int rightEvent = 0;
    
    /**
     * Creates a new ControllerInputManager with default (neutral) values.
     */
    public ControllerInputManager() {
        // All fields initialized to neutral/zero values
    }
    
    // =================================================================================
    // --- Button Update Methods ---
    // =================================================================================
    
    /**
     * Updates button state from received button protocol message.
     * 
     * @param button The button protocol message
     */
    public synchronized void updateButtonData(Button button) {
        if (button != null) {
            long currentTime = System.currentTimeMillis();
            this.buttonTimestamp = (double) currentTime / 1000.0;
            this.buttonFlags = (int) button.getButton();
            this.buttonEventName = button.getEvent() != null ? button.getEvent().name() : "None_";
        }
    }
    
    // =================================================================================
    // --- Joystick Update Methods ---
    // =================================================================================
    
    /**
     * Updates joystick state from received joystick protocol message.
     * 
     * @param joystick The joystick protocol message
     */
    public synchronized void updateJoystickData(Joystick joystick) {
        if (joystick != null) {
            long currentTime = System.currentTimeMillis();
            this.joystickTimestamp = (int) (currentTime / 1000);
            this.leftX = joystick.getLeft().getX();
            this.leftY = joystick.getLeft().getY();
            this.leftDirection = joystick.getLeft().getDirection().getValue();
            this.leftEvent = joystick.getLeft().getEvent().getValue();
            this.rightX = joystick.getRight().getX();
            this.rightY = joystick.getRight().getY();
            this.rightDirection = joystick.getRight().getDirection().getValue();
            this.rightEvent = joystick.getRight().getEvent().getValue();
        }
    }
    
    // =================================================================================
    // --- Python-Compatible Array Methods ---
    // =================================================================================
    
    /**
     * Gets button data as an array (Python API compatibility).
     * 
     * <p>Returns: [timestamp, button_flags, event_name]</p>
     * 
     * @return Object array with button data
     */
    public Object[] getButtonDataArray() {
        return new Object[]{buttonTimestamp, buttonFlags, buttonEventName};
    }
    
    /**
     * Gets joystick data as an array (Python API compatibility).
     * 
     * <p>Returns: [timestamp, left_x, left_y, left_dir, left_event, 
     * right_x, right_y, right_dir, right_event]</p>
     * 
     * @return int array with joystick data
     */
    public int[] getJoystickDataArray() {
        return new int[]{
            joystickTimestamp,
            leftX, leftY, leftDirection, leftEvent,
            rightX, rightY, rightDirection, rightEvent
        };
    }
    
    // =================================================================================
    // --- Individual Typed Getters ---
    // =================================================================================
    
    /**
     * Gets the left joystick X (horizontal) value.
     * 
     * @return X value from -100 to 100 (0 is neutral)
     */
    public int getLeftJoystickX() {
        return leftX;
    }
    
    /**
     * Gets the left joystick Y (vertical) value.
     * 
     * @return Y value from -100 to 100 (0 is neutral)
     */
    public int getLeftJoystickY() {
        return leftY;
    }
    
    /**
     * Gets the right joystick X (horizontal) value.
     * 
     * @return X value from -100 to 100 (0 is neutral)
     */
    public int getRightJoystickX() {
        return rightX;
    }
    
    /**
     * Gets the right joystick Y (vertical) value.
     * 
     * @return Y value from -100 to 100 (0 is neutral)
     */
    public int getRightJoystickY() {
        return rightY;
    }
    
    /**
     * Gets current button flags value.
     * 
     * @return Button flags as integer
     */
    public int getButtonFlags() {
        return buttonFlags;
    }
    
    /**
     * Gets current button event name.
     * 
     * @return Event name (e.g., "Press", "Down", "Up", "None_")
     */
    public String getButtonEventName() {
        return buttonEventName;
    }
    
    // =================================================================================
    // --- Java Composite Object Methods ---
    // =================================================================================
    
    /**
     * Gets button data as a type-safe Java object (recommended for Java code).
     * 
     * @return ButtonData object containing timestamp, flags, and event name
     */
    public ButtonData getButtonDataObject() {
        return new ButtonData(buttonTimestamp, buttonFlags, buttonEventName);
    }
    
    /**
     * Gets joystick data as a type-safe Java object (recommended for Java code).
     * 
     * @return JoystickData object containing all joystick values
     */
    public JoystickData getJoystickDataObject() {
        return new JoystickData(
            joystickTimestamp,
            leftX, leftY, leftDirection, leftEvent,
            rightX, rightY, rightDirection, rightEvent
        );
    }
}
