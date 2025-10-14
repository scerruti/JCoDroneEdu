package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneSystem;
import com.otabi.jcodroneedu.DroneStatus;
import com.otabi.jcodroneedu.protocol.cardreader.CardColor;
import com.otabi.jcodroneedu.protocol.dronestatus.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple Swing-based sensor display. Connects to the controller, polls non-flying
 * telemetry and displays it in a lightweight GUI. Safe by default (no flight commands).
 */
public class SensorDisplayGui {

    private final JFrame frame = new JFrame("CoDrone EDU Sensor Monitor");
    private final JLabel batteryLabel = new JLabel("N/A", SwingConstants.CENTER);
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
    private final JPanel colorSwatch = new JPanel();

    private Drone drone;
    private ScheduledExecutorService executor;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SensorDisplayGui gui = new SensorDisplayGui();
            gui.start();
        });
    }

    public void start() {
        setupUi();

        // Connect to drone in background so UI stays responsive
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                drone = new Drone();
                boolean ok;
                if (getArgPort() != null) {
                    ok = drone.connect(getArgPort());
                } else {
                    ok = drone.connect();
                }

                if (!ok) {
                    showError("Could not connect to CoDrone EDU controller. Ensure it is powered and paired.");
                    return;
                }

                // Start periodic polling at 5 Hz
                executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate(this::pollAndUpdate, 0, 200, TimeUnit.MILLISECONDS);

            } catch (Exception e) {
                showError("Connection failed: " + e.getMessage());
            }
        });
    }

    private String getArgPort() {
        // Gradle JavaExec will not make args accessible here easily; keep null by default
        return null;
    }

    private void showError(String msg) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void setupUi() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

    JPanel center = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(6, 6, 6, 6);

    // Top row: Battery and Color
    c.gridx = 0; c.gridy = 0; c.weightx = 0.6; c.weighty = 0.15;
    center.add(wrapWithTitledPanel("Battery", batteryLabel, 24f), c);

    c.gridx = 1; c.gridy = 0; c.weightx = 0.4; c.weighty = 0.15;
    colorSwatch.setPreferredSize(new Dimension(120, 60));
    colorSwatch.setMinimumSize(new Dimension(80, 40));
    colorSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
    center.add(wrapWithTitledPanel("Card Color", (JComponent) colorSwatch), c);

    // Middle row: Motion and Angles
    c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.weightx = 1.0; c.weighty = 0.15;
    JPanel motionPanel = new JPanel(new GridLayout(1, 3, 6, 6));
    motionPanel.add(wrapWithTitledPanel("Accel (g)", accelLabel, 14f));
    motionPanel.add(wrapWithTitledPanel("Gyro (deg/s)", gyroLabel, 14f));
    motionPanel.add(wrapWithTitledPanel("Angle (deg)", angleLabel, 14f));
    center.add(motionPanel, c);

    // Attitude visualization: roll/pitch bars and heading arrow
    c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.weightx = 1.0; c.weighty = 0.2;
    JPanel attitudePanel = new JPanel(new GridLayout(1, 3, 6, 6));
    attitudePanel.add(wrapWithTitledPanel("Roll (deg)", rollBar));
    attitudePanel.add(wrapWithTitledPanel("Heading", headingArrow));
    attitudePanel.add(wrapWithTitledPanel("Pitch (deg)", pitchBar));
    center.add(attitudePanel, c);

    // Range sensors (only front and bottom are available)
    c.gridx = 0; c.gridy = 3; c.gridwidth = 2; c.weightx = 1.0; c.weighty = 0.35;
    JPanel ranges = new JPanel(new GridLayout(1, 2, 6, 6));
    frontRange.setStringPainted(true);
    bottomRange.setStringPainted(true);
    ranges.add(wrapWithTitledPanel("Front (mm)", frontRange));
    ranges.add(wrapWithTitledPanel("Bottom (mm)", bottomRange));
    center.add(ranges, c);

    // Bottom row: Altitude and Position
    c.gridx = 0; c.gridy = 4; c.gridwidth = 2; c.weightx = 1.0; c.weighty = 0.15;
    JPanel bottom = new JPanel(new GridLayout(1, 2, 6, 6));
    altitudeLabel.setFont(altitudeLabel.getFont().deriveFont(11f));
    positionLabel.setFont(positionLabel.getFont().deriveFont(11f));
    bottom.add(wrapWithTitledPanel("Altitude / Pressure / Temp", altitudeLabel));
    bottom.add(wrapWithTitledPanel("Position (m)", positionLabel));
    center.add(bottom, c);

    frame.add(center, BorderLayout.CENTER);

    frame.setSize(900, 700);
    frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                shutdown();
            }
        });
    }

    private void pollAndUpdate() {
        if (drone == null || !drone.isConnected()) return;

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
            final String batteryText = state != null ? String.format("Battery: %d%%", state.getBattery()) : "Battery: N/A";

            // Motion
            Motion motion = status.getMotion();
            final String accelText;
            final String gyroText = motion != null ? String.format("%.1f, %.1f, %.1f", (double)motion.getGyroRoll(), (double)motion.getGyroPitch(), (double)motion.getGyroYaw()) : "N/A";
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
            final String altText = alt != null ? String.format("<html>Alt: %.2fm<br>Press: %.1fhPa<br>Temp: %.1f°C</html>", alt.getAltitude(), alt.getPressure(), alt.getTemperature()) : "N/A";

            // Position (values are already in meters as floats)
            Position pos = status.getPosition();
            final String posText = pos != null ? String.format("<html>X: %.2fm<br>Y: %.2fm<br>Z: %.2fm</html>", pos.getX(), pos.getY(), pos.getZ()) : "N/A";

            // Card color
            CardColor cardColor = status.getCardColor();

            SwingUtilities.invokeLater(() -> {
                // compact labels
                batteryLabel.setText(batteryText.replace("Battery: ", ""));
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

                // Color swatch
                if (cardColor != null) {
                    colorSwatch.setBackground(parseCardColor(cardColor));
                } else {
                    colorSwatch.setBackground(Color.LIGHT_GRAY);
                }
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Silence errors so GUI doesn't spam dialogs
            // But if drone disconnected, show in status
            SwingUtilities.invokeLater(() -> batteryLabel.setText("Battery: ERROR"));
        }
    }

    private void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
        if (drone != null) {
            try {
                drone.disconnect();
            } catch (Exception ignore) {
            }
        }
        System.exit(0);
    }

    // --- UI helpers ---
    private JPanel wrapWithTitledPanel(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(comp, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createTitledBorder(title));
        return p;
    }

    private JPanel wrapWithTitledPanel(String title, JLabel label, float bigFontSize) {
        if (bigFontSize > 0) {
            label.setFont(label.getFont().deriveFont(Font.BOLD, bigFontSize));
        }
        return wrapWithTitledPanel(title, (JComponent) label);
    }

    private JPanel wrapWithTitledPanel(String title, JProgressBar bar) {
        bar.setStringPainted(true);
        return wrapWithTitledPanel(title, (JComponent) bar);
    }

    private Color parseCardColor(CardColor cardColor) {
        String s = cardColor.toString().toLowerCase();
        switch (s) {
            case "red": return Color.RED;
            case "green": return Color.GREEN;
            case "blue": return Color.BLUE;
            case "yellow": return Color.YELLOW;
            case "purple": return new Color(128, 0, 128);
            case "white": return Color.WHITE;
            case "black": return Color.BLACK;
            default: return Color.GRAY;
        }
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

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);

                // Draw circle background
                g2.setColor(Color.LIGHT_GRAY);
                int r = Math.min(w, h) / 2 - 8;
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);

                // Rotate and draw arrow
                g2.setColor(Color.RED);
                g2.translate(cx, cy);
                g2.rotate(Math.toRadians(-heading)); // negative so positive yaw is clockwise visually

                int arrowLen = r - 16;
                Polygon arrow = new Polygon();
                arrow.addPoint(0, -arrowLen);
                arrow.addPoint(6, -arrowLen + 18);
                arrow.addPoint(2, -arrowLen + 18);
                arrow.addPoint(2, arrowLen - 12);
                arrow.addPoint(-2, arrowLen - 12);
                arrow.addPoint(-2, -arrowLen + 18);
                arrow.addPoint(-6, -arrowLen + 18);

                g2.fill(arrow);

            } finally {
                g2.dispose();
            }
        }
    }

    // Component to visualize angle (roll or pitch) as a rotating bar
    private static class AngleBarComponent extends JComponent {
        private volatile int angle = 0; // degrees
        private final String label;

        public AngleBarComponent(String label) {
            this.label = label;
            setPreferredSize(new Dimension(150, 40));
            setMinimumSize(new Dimension(100, 30));
        }

        public void setAngle(int degrees) {
            this.angle = degrees;
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
}
