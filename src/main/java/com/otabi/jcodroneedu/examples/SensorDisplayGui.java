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
    private final JProgressBar rollBar = new JProgressBar(-180, 180);
    private final JProgressBar pitchBar = new JProgressBar(-180, 180);
    private final ArrowComponent headingArrow = new ArrowComponent();
    private final JProgressBar leftRange = new JProgressBar(0, 500); // mm
    private final JProgressBar frontRange = new JProgressBar(0, 500);
    private final JProgressBar rightRange = new JProgressBar(0, 500);
    private final JProgressBar rearRange = new JProgressBar(0, 500);
    private final JProgressBar topRange = new JProgressBar(0, 500);
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
    c.gridx = 0; c.gridy = 0; c.weightx = 0.6; c.weighty = 0.0;
    center.add(wrapWithTitledPanel("Battery", batteryLabel, 24f), c);

    c.gridx = 1; c.gridy = 0; c.weightx = 0.4;
    colorSwatch.setPreferredSize(new Dimension(120, 60));
    colorSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
    center.add(wrapWithTitledPanel("Card Color", (JComponent) colorSwatch), c);

    // Middle row: Motion and Angles
    c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.weighty = 0.0;
    JPanel motionPanel = new JPanel(new GridLayout(1, 3));
    motionPanel.add(wrapWithTitledPanel("Accel (m/s^2, g)", accelLabel, 14f));
    motionPanel.add(wrapWithTitledPanel("Gyro (deg/s)", gyroLabel, 14f));
    motionPanel.add(wrapWithTitledPanel("Angle (deg)", angleLabel, 14f));
    center.add(motionPanel, c);

    // Attitude visualization: roll/pitch bars and heading arrow
    c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.weighty = 0.0;
    JPanel attitudePanel = new JPanel(new GridLayout(1, 3, 6, 6));
    rollBar.setStringPainted(true);
    pitchBar.setStringPainted(true);
    attitudePanel.add(wrapWithTitledPanel("Roll (deg)", rollBar));
    attitudePanel.add(wrapWithTitledPanel("Heading", headingArrow));
    attitudePanel.add(wrapWithTitledPanel("Pitch (deg)", pitchBar));
    center.add(attitudePanel, c);

    // Range sensors grid
    c.gridx = 0; c.gridy = 3; c.gridwidth = 2; c.weighty = 0.0;
    JPanel ranges = new JPanel(new GridLayout(3, 2, 6, 6));
    ranges.add(wrapWithTitledPanel("Left (mm)", leftRange));
    ranges.add(wrapWithTitledPanel("Front (mm)", frontRange));
    ranges.add(wrapWithTitledPanel("Right (mm)", rightRange));
    ranges.add(wrapWithTitledPanel("Rear (mm)", rearRange));
    ranges.add(wrapWithTitledPanel("Top (mm)", topRange));
    ranges.add(wrapWithTitledPanel("Bottom (mm)", bottomRange));
    center.add(ranges, c);

    // Bottom row: Altitude and Position
    c.gridx = 0; c.gridy = 3; c.gridwidth = 2; c.weighty = 0.0;
    JPanel bottom = new JPanel(new GridLayout(1, 2));
    bottom.add(wrapWithTitledPanel("Altitude / Pressure / Temp", altitudeLabel, 12f));
    bottom.add(wrapWithTitledPanel("Position (m)", positionLabel, 12f));
    center.add(bottom, c);

    frame.add(center, BorderLayout.CENTER);

    frame.setSize(800, 600);
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
            final String gyroText = motion != null ? String.format("Gyro: %.1f, %.1f, %.1f", (double)motion.getGyroRoll(), (double)motion.getGyroPitch(), (double)motion.getGyroYaw()) : "Gyro: N/A";
            final String angleText;
            if (motion != null) {
                double ax_ms2 = motion.getAccelX() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                double ay_ms2 = motion.getAccelY() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                double az_ms2 = motion.getAccelZ() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
                double ax_g = ax_ms2 / 9.80665;
                double ay_g = ay_ms2 / 9.80665;
                double az_g = az_ms2 / 9.80665;
                accelText = String.format("m/s^2: %.2f, %.2f, %.2f | g: %.3f, %.3f, %.3f", ax_ms2, ay_ms2, az_ms2, ax_g, ay_g, az_g);
                angleText = String.format("Angle: %.1f, %.1f, %.1f", motion.getAngleRoll() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG, motion.getAnglePitch() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG, motion.getAngleYaw() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG);
            } else {
                accelText = "Accel: N/A";
                angleText = "Angle: N/A";
            }

            // Range
            Range range = status.getRange();

            // Altitude
            Altitude alt = status.getAltitude();
            final String altText = alt != null ? String.format("Alt: %.2fm Pressure: %.2fhPa Temp: %.2f°C RangeHeight: %.2fcm", alt.getAltitude(), alt.getPressure(), alt.getTemperature(), alt.getRangeHeight()) : "Altitude: N/A";

            // Position
            Position pos = status.getPosition();
            final String posText = pos != null ? String.format("X: %.2fm  Y: %.2fm  Z: %.2fm", pos.getX() / 1000.0, pos.getY() / 1000.0, pos.getZ() / 1000.0) : "Position: N/A";

            // Card color
            CardColor cardColor = status.getCardColor();

            SwingUtilities.invokeLater(() -> {
                // compact labels
                batteryLabel.setText(batteryText.replace("Battery: ", ""));
                accelLabel.setText(accelText);
                gyroLabel.setText(gyroText.replace("Gyro: ", ""));
                angleLabel.setText(angleText.replace("Angle: ", ""));

                // Update range progress bars (values are in mm)
                if (range != null) {
                    leftRange.setValue(range.getLeft()); leftRange.setString(String.format("%d mm", range.getLeft()));
                    frontRange.setValue(range.getFront()); frontRange.setString(String.format("%d mm", range.getFront()));
                    rightRange.setValue(range.getRight()); rightRange.setString(String.format("%d mm", range.getRight()));
                    rearRange.setValue(range.getRear()); rearRange.setString(String.format("%d mm", range.getRear()));
                    topRange.setValue(range.getTop()); topRange.setString(String.format("%d mm", range.getTop()));
                    bottomRange.setValue(range.getBottom()); bottomRange.setString(String.format("%d mm", range.getBottom()));
                }

                // Update roll/pitch/heading visual widgets
                if (motion != null) {
                    int roll = motion.getAngleRoll();
                    int pitch = motion.getAnglePitch();
                    int yaw = motion.getAngleYaw();
                    rollBar.setValue(roll);
                    rollBar.setString(String.format("%d°", roll));
                    pitchBar.setValue(pitch);
                    pitchBar.setString(String.format("%d°", pitch));
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
}
