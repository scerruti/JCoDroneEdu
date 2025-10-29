package com.otabi.jcodroneedu.gui;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneSystem;
import com.otabi.jcodroneedu.DroneStatus;
import com.otabi.jcodroneedu.protocol.cardreader.CardColor;
import com.otabi.jcodroneedu.protocol.dronestatus.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A reusable Swing panel that displays real-time sensor data from a CoDrone EDU.
 * 
 * <p>This panel provides a comprehensive visualization of the drone's sensors including:
 * battery level, color sensors, motion (acceleration, gyroscope, angles), attitude
 * (roll, pitch, heading), range sensors, altitude, and position.</p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Create and connect to drone
 * Drone drone = new Drone();
 * drone.connect();
 * 
 * // Create the sensor monitor panel
 * SensorMonitorPanel sensorPanel = new SensorMonitorPanel(drone);
 * 
 * // Add to your GUI
 * JFrame frame = new JFrame("My Drone Monitor");
 * frame.add(sensorPanel);
 * frame.pack();
 * frame.setVisible(true);
 * 
 * // Start monitoring (updates at 5 Hz)
 * sensorPanel.startMonitoring();
 * 
 * // When done, stop monitoring
 * sensorPanel.stopMonitoring();
 * }</pre>
 * 
 * @since 1.0
 * @educational
 */
public class SensorMonitorPanel extends JPanel {

    private final Drone drone;
    private ScheduledExecutorService executor;
    
    // UI Components
    private final BatteryComponent batteryComponent = new BatteryComponent();
    private final JLabel accelLabel = new JLabel("N/A", SwingConstants.CENTER);
    private final JLabel gyroLabel = new JLabel("N/A", SwingConstants.CENTER);
    private final JLabel angleLabel = new JLabel("N/A", SwingConstants.CENTER);
    private final AngleBarComponent rollBar = new AngleBarComponent("Roll");
    private final AngleBarComponent pitchBar = new AngleBarComponent("Pitch");
    private final ArrowComponent headingArrow = new ArrowComponent();
    private final JProgressBar frontRange = new JProgressBar(0, 500);
    private final JProgressBar bottomRange = new JProgressBar(0, 500);
    private final JLabel altitudeLabel = new JLabel("N/A", SwingConstants.CENTER);
    private final JLabel positionLabel = new JLabel("N/A", SwingConstants.CENTER);
    private final JPanel frontColorSwatch = new JPanel();
    private final JPanel backColorSwatch = new JPanel();
    private final JLabel frontColorLabel = new JLabel("NONE", SwingConstants.CENTER);
    private final JLabel backColorLabel = new JLabel("NONE", SwingConstants.CENTER);

    /**
     * Creates a new sensor monitor panel for the given drone.
     * 
     * @param drone The connected drone to monitor (must be connected before calling startMonitoring)
     */
    public SensorMonitorPanel(Drone drone) {
        this.drone = drone;
        setupUi();
    }

    private void setupUi() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

        // Toolbar at top with control buttons
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Land button with down arrow
        JButton landButton = new JButton("LAND ▼");
        landButton.setPreferredSize(new Dimension(120, 40));
        landButton.setFont(new Font("Arial", Font.BOLD, 14));
        landButton.setBackground(new Color(70, 130, 180)); // Steel blue
        landButton.setForeground(Color.WHITE);
        landButton.setOpaque(true); // Ensure background color shows on all platforms
        landButton.setBorderPainted(false);
        landButton.setFocusPainted(false);
        landButton.addActionListener(e -> {
            drone.land();
            System.out.println("Landing command sent");
        });
        
        // Emergency stop button with stop sign symbol
        JButton stopButton = new JButton("⛔ STOP");
        stopButton.setPreferredSize(new Dimension(120, 40));
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBackground(new Color(220, 20, 60)); // Crimson red
        stopButton.setForeground(Color.WHITE);
        stopButton.setOpaque(true); // Ensure background color shows on all platforms
        stopButton.setBorderPainted(false);
        stopButton.setFocusPainted(false);
        stopButton.addActionListener(e -> {
            drone.emergencyStop();
            System.out.println("Emergency stop activated!");
        });
        
        toolbar.add(landButton);
        toolbar.add(stopButton);
        
        add(toolbar, BorderLayout.NORTH);

        // Center panel with GridBagLayout for sensor displays
        JPanel center = new JPanel(new GridBagLayout());
        add(center, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(6, 6, 6, 6);

        // Top row: Battery (~30%), spacer (~10%), Colors (~30%), empty space (~30%)
        // Battery on the left
        c.gridx = 0; c.gridy = 0; c.weightx = 0.3; c.weighty = 0.15;
        center.add(wrapWithTitledPanel("Battery", batteryComponent), c);

        // Spacer column (blank space between battery and colors)
        c.gridx = 1; c.gridy = 0; c.weightx = 0.1; c.weighty = 0.15;
        center.add(new JPanel(), c); // Empty panel for spacing

        // Color sensors
        c.gridx = 2; c.gridy = 0; c.weightx = 0.3; c.weighty = 0.15;
        JPanel colorPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        
        // Front color with swatch and label
        JPanel frontColorPanel = new JPanel(new BorderLayout(3, 3));
        frontColorSwatch.setPreferredSize(new Dimension(40, 40));
        frontColorSwatch.setMinimumSize(new Dimension(30, 30));
        frontColorSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        frontColorLabel.setFont(frontColorLabel.getFont().deriveFont(11f));
        frontColorPanel.add(frontColorSwatch, BorderLayout.CENTER);
        frontColorPanel.add(frontColorLabel, BorderLayout.SOUTH);
        colorPanel.add(wrapWithTitledPanel("Front Color", frontColorPanel));
        
        // Back color with swatch and label
        JPanel backColorPanel = new JPanel(new BorderLayout(3, 3));
        backColorSwatch.setPreferredSize(new Dimension(40, 40));
        backColorSwatch.setMinimumSize(new Dimension(30, 30));
        backColorSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        backColorLabel.setFont(backColorLabel.getFont().deriveFont(11f));
        backColorPanel.add(backColorSwatch, BorderLayout.CENTER);
        backColorPanel.add(backColorLabel, BorderLayout.SOUTH);
        colorPanel.add(wrapWithTitledPanel("Back Color", backColorPanel));
        
        center.add(colorPanel, c);
        
        // Empty space on the right
        c.gridx = 3; c.gridy = 0; c.weightx = 0.3; c.weighty = 0.15;
        center.add(new JPanel(), c);

        // Middle row: Motion and Angles (span all 4 columns)
        c.gridx = 0; c.gridy = 1; c.gridwidth = 4; c.weightx = 1.0; c.weighty = 0.15;
        JPanel motionPanel = new JPanel(new GridLayout(1, 3, 6, 6));
        motionPanel.add(wrapWithTitledPanel("Accel (g)", accelLabel, 14f));
        motionPanel.add(wrapWithTitledPanel("Gyro (deg/s)", gyroLabel, 14f));
        motionPanel.add(wrapWithTitledPanel("Angle (deg)", angleLabel, 14f));
        center.add(motionPanel, c);

        // Attitude visualization: roll/pitch bars and heading arrow (span all 4 columns)
        c.gridx = 0; c.gridy = 2; c.gridwidth = 4; c.weightx = 1.0; c.weighty = 0.2;
        JPanel attitudePanel = new JPanel(new GridLayout(1, 3, 6, 6));
        attitudePanel.add(wrapWithTitledPanel("Roll (deg)", rollBar));
        attitudePanel.add(wrapWithTitledPanel("Heading", headingArrow));
        attitudePanel.add(wrapWithTitledPanel("Pitch (deg)", pitchBar));
        center.add(attitudePanel, c);

        // Range sensors (only front and bottom are available) - span 3 columns
        c.gridx = 0; c.gridy = 3; c.gridwidth = 3; c.weightx = 1.0; c.weighty = 0.35;
        JPanel ranges = new JPanel(new GridLayout(1, 2, 6, 6));
        frontRange.setStringPainted(true);
        bottomRange.setStringPainted(true);
        ranges.add(wrapWithTitledPanel("Front (mm)", frontRange));
        ranges.add(wrapWithTitledPanel("Bottom (mm)", bottomRange));
        center.add(ranges, c);

        // Altitude and Position - 1 column on the right
        c.gridx = 3; c.gridy = 3; c.gridwidth = 1; c.weightx = 0.3; c.weighty = 0.35;
        JPanel altPosPanel = new JPanel(new GridLayout(2, 1, 6, 6));
        altPosPanel.add(wrapWithTitledPanel("Altitude", altitudeLabel, 12f));
        altPosPanel.add(wrapWithTitledPanel("Position", positionLabel, 12f));
        center.add(altPosPanel, c);
    }

    /**
     * Starts monitoring the drone sensors at 5 Hz (every 200ms).
     * Call this after the drone is connected.
     */
    public void startMonitoring() {
        if (executor != null) {
            stopMonitoring();
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::pollAndUpdate, 0, 200, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops monitoring the drone sensors and releases resources.
     */
    public void stopMonitoring() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    private void pollAndUpdate() {
        try {
            // Request recent data non-destructively
            drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.Motion);
            drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.Altitude);
            drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.Range);
            drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.Position);
            drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.State);
            drone.sendRequest(com.otabi.jcodroneedu.protocol.DataType.CardColor);

            // Allow a short window for receiver thread to populate status
            Thread.sleep(30);

            DroneStatus status = drone.getDroneStatus();

            // Battery from state
            com.otabi.jcodroneedu.protocol.dronestatus.State state = status.getState();
            final int batteryPercentage = state != null ? state.getBattery() : -1;

            // Motion
            Motion motion = status.getMotion();
            final String gyroText = motion != null ? String.format("%.1f, %.1f, %.1f", (double)motion.getGyroRoll(), (double)motion.getGyroPitch(), (double)motion.getGyroYaw()) : "N/A";
            final String accelText;
            final String angleText;
            if (motion != null) {
                double ax_ms2 = motion.getAccelX() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                double ay_ms2 = motion.getAccelY() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                double az_ms2 = motion.getAccelZ() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                double ax_g = ax_ms2 / 9.80665;
                double ay_g = ay_ms2 / 9.80665;
                double az_g = az_ms2 / 9.80665;
                accelText = String.format("%.2f, %.2f, %.2f g", ax_g, ay_g, az_g);
                angleText = String.format("%.1f, %.1f, %.1f", motion.getAngleRoll() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG, motion.getAnglePitch() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG, motion.getAngleYaw() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG);
            } else {
                accelText = "N/A";
                angleText = "N/A";
            }

            // Range
            Range range = status.getRange();

            // Altitude
            Altitude alt = status.getAltitude();
            final String altText = alt != null ? String.format("<html>Alt: %.2fm<br>Press: %.1fhPa<br>Temp: %.1f°C</html>", alt.getAltitude(), alt.getPressure() / 100.0, alt.getTemperature()) : "N/A";

            // Position (values are already in meters as floats)
            Position pos = status.getPosition();
            final String posText = pos != null ? String.format("<html>X: %.2fm<br>Y: %.2fm<br>Z: %.2fm</html>", pos.getX(), pos.getY(), pos.getZ()) : "N/A";

            // Card color
            CardColor cardColor = status.getCardColor();

            SwingUtilities.invokeLater(() -> {
                // Update battery component
                batteryComponent.setBatteryLevel(batteryPercentage);
                
                accelLabel.setText(accelText);
                gyroLabel.setText(gyroText);
                angleLabel.setText(angleText);

                // Update range progress bars (values are in mm)
                if (range != null) {
                    frontRange.setValue(range.getFront()); frontRange.setString(String.format("%d mm", range.getFront()));
                    bottomRange.setValue(range.getBottom()); bottomRange.setString(String.format("%d mm", range.getBottom()));
                }

                // Update roll/pitch/heading visual widgets
                if (motion != null) {
                    int roll = motion.getAngleRoll();
                    int pitch = motion.getAnglePitch();
                    int yaw = motion.getAngleYaw();
                    rollBar.setAngle(roll);
                    pitchBar.setAngle(pitch);
                    headingArrow.setHeading(yaw);
                }

                altitudeLabel.setText(altText);
                positionLabel.setText(posText);

                // Color swatches and labels - front and back
                if (cardColor != null && cardColor.getColor() != null && cardColor.getColor().length >= 2) {
                    // Get color indices directly from byte array
                    int frontColorIndex = cardColor.getColor()[0] & 0xFF;
                    int backColorIndex = cardColor.getColor()[1] & 0xFF;
                    
                    // Use enum to get both the color name and AWT Color
                    DroneSystem.CardColorIndex frontEnum = DroneSystem.CardColorIndex.fromIndex(frontColorIndex);
                    DroneSystem.CardColorIndex backEnum = DroneSystem.CardColorIndex.fromIndex(backColorIndex);
                    
                    frontColorSwatch.setBackground(frontEnum.toAwtColor());
                    backColorSwatch.setBackground(backEnum.toAwtColor());
                    frontColorLabel.setText(cardColor.getFrontColorName());
                    backColorLabel.setText(cardColor.getBackColorName());
                } else {
                    frontColorSwatch.setBackground(Color.LIGHT_GRAY);
                    backColorSwatch.setBackground(Color.LIGHT_GRAY);
                    frontColorLabel.setText("N/A");
                    backColorLabel.setText("N/A");
                }
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Silence errors so GUI doesn't spam dialogs
            // But if drone disconnected, show in status
            SwingUtilities.invokeLater(() -> batteryComponent.setBatteryLevel(-1));
        }
    }

    private JPanel wrapWithTitledPanel(String title, JComponent component) {
        return wrapWithTitledPanel(title, component, -1f);
    }

    private JPanel wrapWithTitledPanel(String title, JComponent component, float fontSize) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        if (fontSize > 0 && component instanceof JLabel) {
            JLabel lbl = (JLabel) component;
            lbl.setFont(lbl.getFont().deriveFont(fontSize));
        }
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel wrapWithTitledPanel(String title, JProgressBar bar) {
        bar.setStringPainted(true);
        return wrapWithTitledPanel(title, (JComponent) bar);
    }

    // Simple arrow component to indicate yaw/heading
    private static class ArrowComponent extends JComponent {
        private volatile int heading = 0; // degrees

        public ArrowComponent() {
            setPreferredSize(new Dimension(120, 120));
        }

        public void setHeading(int degrees) {
            this.heading = degrees;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int w = getWidth();
                int h = getHeight();
                int cx = w / 2;
                int cy = h / 2;
                int radius = Math.min(w, h) / 2 - 10;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // White background
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);

                // Draw compass circle
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

                // Draw cardinal directions
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.setColor(Color.DARK_GRAY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("N", cx - fm.stringWidth("N") / 2, cy - radius + 15);
                g2.drawString("S", cx - fm.stringWidth("S") / 2, cy + radius - 5);
                g2.drawString("E", cx + radius - 15, cy + fm.getAscent() / 2);
                g2.drawString("W", cx - radius + 5, cy + fm.getAscent() / 2);

                // Draw heading arrow
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3));
                g2.translate(cx, cy);
                g2.rotate(Math.toRadians(heading));
                int arrowLen = radius - 10;
                int[] xPoints = {0, -8, 0, 8};
                int[] yPoints = {-arrowLen, -arrowLen + 15, -arrowLen + 10, -arrowLen + 15};
                g2.fillPolygon(xPoints, yPoints, 4);
                g2.drawLine(0, -arrowLen + 10, 0, 0);

                // Draw heading value
                g2.rotate(-Math.toRadians(heading));
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                String text = heading + "°";
                int textWidth = g2.getFontMetrics().stringWidth(text);
                g2.drawString(text, -textWidth / 2, radius - 20);

            } finally {
                g2.dispose();
            }
        }
    }

    // Bar component to visualize roll or pitch angle
    private static class AngleBarComponent extends JComponent {
        private volatile int angle = 0;
        private final String label;

        public AngleBarComponent(String label) {
            this.label = label;
            setPreferredSize(new Dimension(100, 120));
        }

        public void setAngle(int angle) {
            this.angle = angle;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int w = getWidth();
                int h = getHeight();
                int cx = w / 2;
                int cy = h / 2;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);

                // Draw reference line (horizontal at 0 degrees)
                g2.setColor(Color.LIGHT_GRAY);
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{5, 5}, 0));
                g2.drawLine(10, cy, w - 10, cy);

                // Draw rotating bar
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(4));
                g2.translate(cx, cy);
                g2.rotate(Math.toRadians(angle)); // Positive angle rotates clockwise
                int barLen = Math.min(w, h * 4) / 2 - 20;
                g2.drawLine(-barLen, 0, barLen, 0);

                // Draw angle text
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                String text = label + ": " + angle + "°";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                g2.rotate(-Math.toRadians(angle)); // Rotate back for text
                g2.drawString(text, -textWidth / 2, cy - 5);

            } finally {
                g2.dispose();
            }
        }
    }

    // Battery visualization component
    private static class BatteryComponent extends JComponent {
        private volatile int batteryLevel = -1; // -1 = unknown, 0-100 = percentage

        public BatteryComponent() {
            setPreferredSize(new Dimension(120, 50));
            setMinimumSize(new Dimension(80, 40));
        }

        public void setBatteryLevel(int level) {
            this.batteryLevel = level;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int w = getWidth();
                int h = getHeight();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // White background
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);

                // Battery outline dimensions
                int batteryWidth = w - 20;
                int batteryHeight = h - 20;
                int x = 10;
                int y = 10;
                int terminalWidth = 6;
                int terminalHeight = batteryHeight / 3;

                // Draw battery body outline
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(x, y, batteryWidth, batteryHeight);

                // Draw battery terminal (positive nub)
                g2.fillRect(x + batteryWidth, y + (batteryHeight - terminalHeight) / 2, terminalWidth, terminalHeight);

                // Fill battery based on level
                if (batteryLevel >= 0 && batteryLevel <= 100) {
                    int fillWidth = (int) ((batteryWidth - 4) * (batteryLevel / 100.0));
                    
                    // Choose color based on battery level
                    Color fillColor;
                    if (batteryLevel > 50) {
                        fillColor = new Color(76, 175, 80); // Green
                    } else if (batteryLevel > 25) {
                        fillColor = new Color(255, 193, 7); // Yellow/Amber
                    } else {
                        fillColor = new Color(244, 67, 54); // Red
                    }
                    
                    g2.setColor(fillColor);
                    g2.fillRect(x + 2, y + 2, fillWidth, batteryHeight - 4);
                }

                // Draw percentage text
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                String text = batteryLevel >= 0 ? batteryLevel + "%" : "N/A";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.drawString(text, x + (batteryWidth - textWidth) / 2, y + (batteryHeight + textHeight) / 2 - 2);

            } finally {
                g2.dispose();
            }
        }
    }
}
