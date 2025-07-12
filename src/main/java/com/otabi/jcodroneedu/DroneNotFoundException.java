package com.otabi.jcodroneedu;

/**
 * Exception thrown when the CoDrone EDU cannot be found or connected to.
 * 
 * <p>This is a common exception that students will encounter during the learning process,
 * especially when working with the {@link Drone#connect()} method. It provides clear
 * error messages to help students troubleshoot connection issues.</p>
 * 
 * <h3>🔌 Common Connection Scenarios (L0101):</h3>
 * <ul>
 *   <li><strong>Drone not powered on:</strong> Make sure the CoDrone EDU is turned on</li>
 *   <li><strong>USB not connected:</strong> Check that the USB cable is properly connected</li>
 *   <li><strong>Multiple devices:</strong> Only one device should be connected at a time</li>
 *   <li><strong>Driver issues:</strong> May need to install or update drivers</li>
 * </ul>
 * 
 * <h3>💡 Student Troubleshooting Tips:</h3>
 * <pre>{@code
 * // Method 1: Simple connection (throws DroneNotFoundException)
 * try {
 *     drone.connect();
 *     System.out.println("✅ Connected successfully!");
 * } catch (DroneNotFoundException e) {
 *     System.out.println("❌ " + e.getMessage());
 *     System.out.println("💡 Check: Power on, USB cable, only one drone connected");
 * }
 * 
 * // Method 2: Use pair() for simpler connection (no exception handling needed)
 * drone.pair(); // Automatically handles connection errors
 * }</pre>
 * 
 * <h3>🎯 Learning Objectives:</h3>
 * <ul>
 *   <li>Understand exception handling concepts</li>
 *   <li>Practice try-catch blocks for real-world scenarios</li>
 *   <li>Learn hardware troubleshooting skills</li>
 *   <li>Develop debugging mindset for physical computing</li>
 * </ul>
 * 
 * @author CoDrone EDU Team
 * @version 2.3
 * @since 1.0
 * @see Drone#connect()
 * @see Drone#pair()
 */
public class DroneNotFoundException extends Exception {
    public DroneNotFoundException() {
        super("CoDrone not found.");
    }

    public DroneNotFoundException(String deviceName) {
        super("CoDrone not found" + (!(deviceName == null || deviceName.isEmpty()) ? ", on "+deviceName : "") + ".");
    }
}
