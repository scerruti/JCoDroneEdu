package com.otabi.jcodroneedu;

/**
 * Immutable data class representing button state from the CoDrone EDU controller.
 * 
 * <p>This class provides a type-safe, Java-idiomatic way to access button data,
 * recommended over the array-based {@link Drone#getButtonData()} method.</p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Recommended Java approach
 * ButtonData buttonData = drone.getButtonDataObject();
 * System.out.println("Button event: " + buttonData.getEventName());
 * System.out.println("Button flags: " + buttonData.getButtonFlags());
 * System.out.println("Timestamp: " + buttonData.getTimestamp());
 * 
 * // Check for specific button press
 * if (buttonData.getButtonFlags() == 1 && 
 *     (buttonData.getEventName().equals("Press") || buttonData.getEventName().equals("Down"))) {
 *     System.out.println("L1 button pressed!");
 * }
 * }</pre>
 * 
 * <h3>Button Flags:</h3>
 * <ul>
 *   <li>0 = No button</li>
 *   <li>1 = L1</li>
 *   <li>2 = L2</li>
 *   <li>4 = R1</li>
 *   <li>8 = R2</li>
 * </ul>
 * 
 * <h3>Event Names:</h3>
 * <ul>
 *   <li>"None_" = No event</li>
 *   <li>"Press" = Button just pressed</li>
 *   <li>"Down" = Button held down</li>
 *   <li>"Up" = Button just released</li>
 * </ul>
 * 
 * @author JCoDroneEdu Development Team
 * @since 1.0.0
 * @see Drone#getButtonDataObject()
 */
public class ButtonData {
    private final double timestamp;
    private final int buttonFlags;
    private final String eventName;
    
    /**
     * Creates a new ButtonData instance.
     * 
     * @param timestamp The timestamp in seconds when the button data was captured
     * @param buttonFlags The button flags indicating which button(s) are active
     * @param eventName The event name ("Press", "Down", "Up", or "None_")
     */
    public ButtonData(double timestamp, int buttonFlags, String eventName) {
        this.timestamp = timestamp;
        this.buttonFlags = buttonFlags;
        this.eventName = eventName != null ? eventName : "None_";
    }
    
    /**
     * Gets the timestamp when this button data was captured.
     * 
     * @return Timestamp in seconds
     */
    public double getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the button flags indicating which button(s) are active.
     * 
     * @return Button flags (1=L1, 2=L2, 4=R1, 8=R2, 0=none)
     */
    public int getButtonFlags() {
        return buttonFlags;
    }
    
    /**
     * Gets the event name for this button data.
     * 
     * @return Event name ("Press", "Down", "Up", or "None_")
     */
    public String getEventName() {
        return eventName;
    }
    
    /**
     * Returns a string representation of this button data.
     * 
     * @return String representation
     */
    @Override
    public String toString() {
        return String.format("ButtonData{timestamp=%.3f, buttonFlags=%d, eventName='%s'}", 
                           timestamp, buttonFlags, eventName);
    }
}
