package com.otabi.jcodroneedu.gui;

import com.otabi.jcodroneedu.Drone;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A simple-to-use sensor monitor that opens a window showing all drone sensors.
 * This class handles all the GUI complexity so students don't need to know Swing.
 * 
 * <p><strong>Ultra-Simple Usage:</strong></p>
 * <pre>{@code
 * Drone drone = new Drone();
 * drone.pair();
 * 
 * // That's it! One line creates and shows the monitor:
 * SensorMonitor monitor = new SensorMonitor(drone);
 * 
 * // Your code continues here...
 * // The monitor window stays open and updates automatically
 * 
 * // When done (optional - window can be closed manually):
 * monitor.close();
 * }</pre>
 * 
 * <p>The monitor automatically:</p>
 * <ul>
 *   <li>Creates and displays a window with all sensor information</li>
 *   <li>Starts updating sensors at 5 Hz</li>
 *   <li>Adds Land and Emergency Stop buttons for safety</li>
 *   <li>Handles cleanup when the window is closed</li>
 *   <li>Runs as a daemon so it won't prevent your program from ending</li>
 * </ul>
 * 
 * @educational This is the easiest way to see your drone's sensors. Just create
 *              one object and pass your drone - no GUI knowledge required!
 * 
 * @author CoDrone EDU Development Team
 * @since 1.0
 */
public class SensorMonitor {
    private final JFrame frame;
    private final SensorMonitorPanel panel;
    private volatile boolean isOpen = true;
    
    /**
     * Creates and displays a sensor monitor window for the given drone.
     * The window appears immediately and starts updating sensor data.
     * 
     * @param drone the connected Drone to monitor
     * @educational Just pass your connected drone and the monitor window appears!
     */
    public SensorMonitor(Drone drone) {
        this.panel = new SensorMonitorPanel(drone);
        this.frame = new JFrame("CoDrone EDU - Sensor Monitor");
        
        // Setup frame
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center on screen
        
        // Handle window closing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
        
        // Show the window
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            
            // Start monitoring after window is visible
            // Add small delay to ensure drone is ready
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    if (isOpen) {
                        panel.startMonitoring();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }
    
    /**
     * Closes the monitor window and stops updating.
     * This is optional - students can just close the window manually.
     * 
     * @educational You can call this when done, or just close the window with the X button.
     */
    public void close() {
        if (isOpen) {
            cleanup();
            SwingUtilities.invokeLater(() -> frame.dispose());
        }
    }
    
    /**
     * Internal cleanup when window closes.
     */
    private void cleanup() {
        isOpen = false;
        panel.stopMonitoring();
    }
    
    /**
     * Checks if the monitor window is still open.
     * 
     * @return true if the window is open, false if closed
     */
    public boolean isOpen() {
        return isOpen && frame.isVisible();
    }
}
