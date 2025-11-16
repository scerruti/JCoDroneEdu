package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.gui.SensorMonitorPanel;

import javax.swing.*;

/**
 * Simple example showing how to use the SensorMonitorPanel component in your own code.
 * 
 * <p>This demonstrates the minimal code needed to create a sensor monitoring GUI
 * for your drone. Students can copy this pattern to add sensor displays to their
 * own drone applications.</p>
 * 
 * <h3>Usage:</h3>
 * <pre>{@code
 * // 1. Create and connect drone
 * Drone drone = new Drone();
 * drone.connect();
 * 
 * // 2. Create the sensor panel
 * SensorMonitorPanel sensorPanel = new SensorMonitorPanel(drone);
 * 
 * // 3. Add to your GUI
 * JFrame frame = new JFrame("My Drone App");
 * frame.add(sensorPanel);
 * frame.pack();
 * frame.setVisible(true);
 * 
 * // 4. Start monitoring
 * sensorPanel.startMonitoring();
 * 
 * // 5. Clean up when done
 * sensorPanel.stopMonitoring();
 * drone.close();
 * }</pre>
 * 
 * @since 1.0
 * @educational
 */
public class SimpleSensorMonitor {

    public static void main(String[] args) {
        // Create the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create the frame
            JFrame frame = new JFrame("CoDrone EDU - Simple Sensor Monitor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Connect to drone (in a background thread to keep GUI responsive)
            new Thread(() -> {
                try {
                    // Step 1: Create and connect the drone
                    Drone drone = new Drone();
                    System.out.println("Connecting to drone...");
                    
                    boolean connected = drone.connect();
                    if (!connected) {
                        JOptionPane.showMessageDialog(frame,
                            "Could not connect to CoDrone EDU.\nMake sure the controller is on and paired.",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                        return;
                    }
                    
                    System.out.println("Connected successfully!");
                    
                    // Step 2: Create the sensor monitor panel
                    SensorMonitorPanel sensorPanel = new SensorMonitorPanel(drone);
                    
                    // Step 3: Add the panel to the GUI
                    SwingUtilities.invokeLater(() -> {
                        frame.add(sensorPanel);
                        frame.pack();
                        frame.setLocationRelativeTo(null); // Center on screen
                        frame.setVisible(true);
                        
                        // Step 4: Start monitoring (updates every 200ms)
                        sensorPanel.startMonitoring();
                        System.out.println("Sensor monitoring started!");
                    });
                    
                    // Step 5: Set up cleanup when window closes
                    frame.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.out.println("Stopping sensor monitor...");
                            sensorPanel.stopMonitoring();
                            drone.close();
                            System.out.println("Disconnected. Goodbye!");
                        }
                    });
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }).start();
        });
    }
}
