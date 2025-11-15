package com.otabi.jcodroneedu.gui;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.ButtonData;
import com.otabi.jcodroneedu.JoystickData;
import com.otabi.jcodroneedu.protocol.DataType;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A reusable Swing component for displaying CoDrone EDU controller input in real-time.
 * This panel shows joystick positions, button states, and connection status.
 * 
 * <p><strong>Educational Usage:</strong></p>
 * <p>Students can easily add controller input monitoring to their programs by creating
 * an instance of this panel and adding it to their GUI. The panel handles all the
 * complexity of polling the controller and updating the display.</p>
 * 
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * // 1. Create and connect your drone
 * Drone drone = new Drone();
 * drone.pair();
 * 
 * // 2. Create the controller input panel
 * ControllerInputPanel panel = new ControllerInputPanel(drone);
 * 
 * // 3. Add to your GUI
 * JFrame frame = new JFrame("My Controller Monitor");
 * frame.add(panel);
 * frame.pack();
 * frame.setVisible(true);
 * 
 * // 4. Start monitoring
 * panel.startMonitoring();
 * 
 * // 5. When done, stop monitoring
 * panel.stopMonitoring();
 * drone.close();
 * }</pre>
 * 
 * @educational This component is designed for student use. Simply pass a connected
 *              Drone object to the constructor, add the panel to your GUI, and call
 *              startMonitoring() to see live controller input.
 * 
 * @author CoDrone EDU Development Team
 * @since 1.0
 */
public class ControllerInputPanel extends JPanel {
    private final Drone drone;
    private JPanel leftJoystickPanel;
    private JPanel rightJoystickPanel;
    private JLabel leftJoystickLabel;
    private JLabel rightJoystickLabel;
    private JLabel buttonStatusLabel;
    private JLabel connectionLabel;
    private JLabel lastUpdateLabel;
    
    private volatile boolean monitoring = false;
    private Thread updateThread;
    @SuppressWarnings("unused") // Reserved for future debounce feature
    private volatile long lastInputTime = 0;
    
    // Track button press times for visual feedback (debounce)
    private final Map<String, Long> buttonPressTimes = new ConcurrentHashMap<>();
    private static final long BUTTON_DISPLAY_MS = 200; // Show button press for at least 200ms
    
    // Button indicators
    private JLabel l1Button, l2Button, r1Button, r2Button;
    @SuppressWarnings("unused") // Reserved for future power button display
    private JLabel hButton, sButton, pButton, powerButton;
    private JLabel upButton, downButton, leftButton, rightButton;
    
    /**
     * Creates a new controller input panel for the specified drone.
     * 
     * @param drone the Drone object to monitor for controller input
     * @throws IllegalArgumentException if drone is null
     */
    public ControllerInputPanel(Drone drone) {
        if (drone == null) {
            throw new IllegalArgumentException("Drone cannot be null");
        }
        this.drone = drone;
        initializeUI();
    }
    
    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("CoDrone EDU Controller", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main panel with controller layout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 10));
        
        // Left side - Shoulder buttons (L1, L2) and left joystick with H/S buttons
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(createShoulderButtons("L1", "L2", true), BorderLayout.NORTH);
        leftPanel.add(createJoystickWithButtons("Left Joystick (J1)", true), BorderLayout.CENTER);
        
        // Right side - Shoulder buttons (R1, R2) and right joystick with Power/P buttons
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(createShoulderButtons("R1", "R2", false), BorderLayout.NORTH);
        rightPanel.add(createJoystickWithButtons("Right Joystick (J2)", false), BorderLayout.CENTER);
        
        // Center panel - Status and arrow buttons
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        
        // Status info at top
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        
        connectionLabel = new JLabel("Not monitoring", SwingConstants.CENTER);
        connectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        connectionLabel.setForeground(Color.GRAY);
        
        buttonStatusLabel = new JLabel("No buttons pressed", SwingConstants.CENTER);
        buttonStatusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        lastUpdateLabel = new JLabel("Waiting for input...", SwingConstants.CENTER);
        lastUpdateLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        lastUpdateLabel.setForeground(Color.GRAY);
        
        statusPanel.add(connectionLabel);
        statusPanel.add(buttonStatusLabel);
        statusPanel.add(lastUpdateLabel);
        
        // Arrow buttons at bottom center
        JPanel arrowPanel = createArrowButtons();
        
        centerPanel.add(statusPanel, BorderLayout.NORTH);
        centerPanel.add(arrowPanel, BorderLayout.CENTER);
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer with joystick values
        JPanel footerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        leftJoystickLabel = new JLabel("Left: X=0, Y=0", SwingConstants.CENTER);
        leftJoystickLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        rightJoystickLabel = new JLabel("Right: X=0, Y=0", SwingConstants.CENTER);
        rightJoystickLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        footerPanel.add(leftJoystickLabel);
        footerPanel.add(rightJoystickLabel);
        
        add(footerPanel, BorderLayout.SOUTH);
        
        setPreferredSize(new Dimension(900, 480));
    }
    
    /**
     * Creates the shoulder button display panel.
     */
    private JPanel createShoulderButtons(String b1, String b2, boolean isLeft) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(isLeft ? "Shoulder L" : "Shoulder R"));
        
        JLabel button1 = createButtonLabel(b1);
        JLabel button2 = createButtonLabel(b2);
        
        panel.add(button1);
        panel.add(button2);
        
        if (isLeft) {
            l1Button = button1;
            l2Button = button2;
        } else {
            r1Button = button1;
            r2Button = button2;
        }
        
        return panel;
    }
    
    /**
     * Creates a joystick display with associated buttons.
     */
    private JPanel createJoystickWithButtons(String title, boolean isLeft) {
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.setBorder(BorderFactory.createTitledBorder(title));
        
        // Joystick in center
        JoystickDisplayPanel joystickPanel = new JoystickDisplayPanel();
        joystickPanel.setPreferredSize(new Dimension(180, 180));
        
        if (isLeft) {
            leftJoystickPanel = joystickPanel;
            
            // H button above-left, S button below-right
            JLabel hBtn = createButtonLabel("H");
            JLabel sBtn = createButtonLabel("S");
            hButton = hBtn;
            sButton = sBtn;
            
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(hBtn);
            
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.add(sBtn);
            
            container.add(topPanel, BorderLayout.NORTH);
            container.add(joystickPanel, BorderLayout.CENTER);
            container.add(bottomPanel, BorderLayout.SOUTH);
        } else {
            rightJoystickPanel = joystickPanel;
            
            // Power button above-right (grayed out), P button below-left
            JLabel powerBtn = createButtonLabel("Power");
            powerBtn.setEnabled(false);
            powerBtn.setBackground(Color.DARK_GRAY);
            powerBtn.setToolTipText("Don't press - will disconnect!");
            
            JLabel pBtn = createButtonLabel("P");
            powerButton = powerBtn;
            pButton = pBtn;
            
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            topPanel.add(powerBtn);
            
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomPanel.add(pBtn);
            
            container.add(topPanel, BorderLayout.NORTH);
            container.add(joystickPanel, BorderLayout.CENTER);
            container.add(bottomPanel, BorderLayout.SOUTH);
        }
        
        return container;
    }
    
    /**
     * Creates the arrow button display panel.
     */
    private JPanel createArrowButtons() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("D-Pad (Center)"));
        
        JLabel up = createButtonLabel("↑");
        JLabel down = createButtonLabel("↓");
        JLabel left = createButtonLabel("←");
        JLabel right = createButtonLabel("→");
        
        upButton = up;
        downButton = down;
        leftButton = left;
        rightButton = right;
        
        panel.add(up, BorderLayout.NORTH);
        panel.add(down, BorderLayout.SOUTH);
        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        
        // Center spacer
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(40, 40));
        panel.add(spacer, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a button label with standard styling.
     */
    private JLabel createButtonLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.LIGHT_GRAY);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setPreferredSize(new Dimension(55, 35));
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }
    
    /**
     * Starts monitoring the controller input and updating the display.
     * Call this method after adding the panel to your GUI.
     * 
     * <p>The panel will poll the controller at approximately 20Hz and update
     * the joystick positions and button states in real-time.</p>
     * 
     * @educational Call this method after your GUI is visible to start seeing
     *              controller input updates.
     */
    public void startMonitoring() {
        if (monitoring) {
            return; // Already monitoring
        }
        
        monitoring = true;
        
        SwingUtilities.invokeLater(() -> {
            connectionLabel.setText("✓ Monitoring");
            connectionLabel.setForeground(new Color(0, 150, 0));
        });
        
        updateThread = new Thread(this::updateLoop);
        updateThread.setDaemon(true);
        updateThread.start();
    }
    
    /**
     * Stops monitoring the controller input.
     * Call this method when you're done using the panel or before closing your application.
     * 
     * @educational Always call this method to properly clean up when you're done
     *              monitoring the controller.
     */
    public void stopMonitoring() {
        monitoring = false;
        
        if (updateThread != null) {
            updateThread.interrupt();
            try {
                updateThread.join(1000); // Wait up to 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            connectionLabel.setText("Monitoring stopped");
            connectionLabel.setForeground(Color.GRAY);
        });
    }
    
    /**
     * Main update loop that polls the controller and updates the display.
     */
    private void updateLoop() {
        while (monitoring) {
            try {
                // Request fresh joystick and button data from controller
                drone.sendRequest(DataType.Joystick);
                drone.sendRequest(DataType.Button);
                Thread.sleep(20); // Small delay to allow data to arrive
                
                // Get joystick values using composite object
                JoystickData joystickData = drone.getJoystickDataObject();
                int leftX = joystickData.getLeftX();
                int leftY = joystickData.getLeftY();
                int rightX = joystickData.getRightX();
                int rightY = joystickData.getRightY();
                
                // Check if any joystick moved
                if (leftX != 0 || leftY != 0 || rightX != 0 || rightY != 0) {
                    lastInputTime = System.currentTimeMillis();
                    SwingUtilities.invokeLater(() -> 
                        lastUpdateLabel.setText("Last input: Joystick moved")
                    );
                }
                
                // Update joystick displays
                SwingUtilities.invokeLater(() -> {
                    leftJoystickLabel.setText(String.format("Left: X=%3d, Y=%3d", leftX, leftY));
                    rightJoystickLabel.setText(String.format("Right: X=%3d, Y=%3d", rightX, rightY));
                    
                    ((JoystickDisplayPanel)leftJoystickPanel).setPosition(leftX, leftY);
                    ((JoystickDisplayPanel)rightJoystickPanel).setPosition(rightX, rightY);
                });
                
                // Check button states
                updateButtonStates();
                
                Thread.sleep(30); // 20Hz update rate (20ms request + 30ms sleep = 50ms total)
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.err.println("Controller update error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Updates the button state displays based on current controller input.
     */
    private void updateButtonStates() {
        StringBuilder status = new StringBuilder();
        long currentTime = System.currentTimeMillis();
        
        // Get button data using composite object
        ButtonData buttonData = drone.getButtonDataObject();
        int buttonFlags = buttonData.getButtonFlags();
        String eventName = buttonData.getEventName();
        
        // Only register button presses on "Press" events (not Up)
        boolean isActivePress = "Press".equals(eventName) || "Down".equals(eventName);
        
        // Check each button flag and update press times if actively pressed
        if (isActivePress) {
            if ((buttonFlags & 0x0001) != 0) buttonPressTimes.put("l1", currentTime); // L1
            if ((buttonFlags & 0x0002) != 0) buttonPressTimes.put("l2", currentTime); // L2
            if ((buttonFlags & 0x0004) != 0) buttonPressTimes.put("r1", currentTime); // R1
            if ((buttonFlags & 0x0008) != 0) buttonPressTimes.put("r2", currentTime); // R2
            if ((buttonFlags & 0x0010) != 0) buttonPressTimes.put("h", currentTime);  // H
            if ((buttonFlags & 0x0020) != 0) buttonPressTimes.put("power", currentTime); // Power
            if ((buttonFlags & 0x0040) != 0) buttonPressTimes.put("up", currentTime); // Up arrow
            if ((buttonFlags & 0x0080) != 0) buttonPressTimes.put("left", currentTime); // Left arrow
            if ((buttonFlags & 0x0100) != 0) buttonPressTimes.put("right", currentTime); // Right arrow
            if ((buttonFlags & 0x0200) != 0) buttonPressTimes.put("down", currentTime); // Down arrow
            if ((buttonFlags & 0x0400) != 0) buttonPressTimes.put("s", currentTime);  // S
            if ((buttonFlags & 0x0800) != 0) buttonPressTimes.put("p", currentTime);  // P
        }
        
        // Display button as pressed if recently pressed (debounce)
        boolean l1Show = isRecentlyPressed("l1", currentTime);
        boolean l2Show = isRecentlyPressed("l2", currentTime);
        boolean r1Show = isRecentlyPressed("r1", currentTime);
        boolean r2Show = isRecentlyPressed("r2", currentTime);
        boolean hShow = isRecentlyPressed("h", currentTime);
        boolean sShow = isRecentlyPressed("s", currentTime);
        boolean pShow = isRecentlyPressed("p", currentTime);
        boolean powerShow = isRecentlyPressed("power", currentTime);
        boolean upShow = isRecentlyPressed("up", currentTime);
        boolean downShow = isRecentlyPressed("down", currentTime);
        boolean leftShow = isRecentlyPressed("left", currentTime);
        boolean rightShow = isRecentlyPressed("right", currentTime);
        
        // Check if any button is pressed
        boolean anyPressed = l1Show || l2Show || r1Show || r2Show || hShow || sShow || pShow || 
                           powerShow || upShow || downShow || leftShow || rightShow;
        if (anyPressed) {
            lastInputTime = currentTime;
            SwingUtilities.invokeLater(() -> 
                lastUpdateLabel.setText("Last input: Button pressed")
            );
        }
        
        SwingUtilities.invokeLater(() -> {
            // Shoulder buttons
            l1Button.setBackground(l1Show ? Color.GREEN : Color.LIGHT_GRAY);
            l2Button.setBackground(l2Show ? Color.GREEN : Color.LIGHT_GRAY);
            r1Button.setBackground(r1Show ? Color.GREEN : Color.LIGHT_GRAY);
            r2Button.setBackground(r2Show ? Color.GREEN : Color.LIGHT_GRAY);
            
            // Joystick buttons
            hButton.setBackground(hShow ? Color.GREEN : Color.LIGHT_GRAY);
            sButton.setBackground(sShow ? Color.GREEN : Color.LIGHT_GRAY);
            pButton.setBackground(pShow ? Color.GREEN : Color.LIGHT_GRAY);
            // Power button stays dark gray (disabled)
            
            // Arrow buttons
            upButton.setBackground(upShow ? Color.GREEN : Color.LIGHT_GRAY);
            downButton.setBackground(downShow ? Color.GREEN : Color.LIGHT_GRAY);
            leftButton.setBackground(leftShow ? Color.GREEN : Color.LIGHT_GRAY);
            rightButton.setBackground(rightShow ? Color.GREEN : Color.LIGHT_GRAY);
        });
        
        // Build status string (use debounced values for better display)
        if (l1Show) status.append("L1 ");
        if (l2Show) status.append("L2 ");
        if (r1Show) status.append("R1 ");
        if (r2Show) status.append("R2 ");
        if (hShow) status.append("H ");
        if (sShow) status.append("S ");
        if (pShow) status.append("P ");
        if (powerShow) status.append("Power ");
        if (upShow) status.append("↑ ");
        if (downShow) status.append("↓ ");
        if (leftShow) status.append("← ");
        if (rightShow) status.append("→ ");
        
        final String statusText = status.length() > 0 ? status.toString() : "No buttons pressed";
        SwingUtilities.invokeLater(() -> buttonStatusLabel.setText(statusText));
    }
    
    /**
     * Checks if a button was recently pressed (for debouncing).
     */
    private boolean isRecentlyPressed(String buttonId, long currentTime) {
        Long pressTime = buttonPressTimes.get(buttonId);
        return pressTime != null && (currentTime - pressTime) < BUTTON_DISPLAY_MS;
    }
    
    /**
     * Custom panel to display joystick position visually.
     * Shows a circular area with crosshairs and a red dot indicating current position.
     */
    private static class JoystickDisplayPanel extends JPanel {
        private int x = 0;
        private int y = 0;
        
        /**
         * Sets the joystick position and repaints the display.
         * 
         * @param x horizontal position (-100 to 100)
         * @param y vertical position (-100 to 100)
         */
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = -y; // Invert Y for display (positive = up)
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2;
            int centerY = height / 2;
            
            // Draw background circle
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillOval(10, 10, width - 20, height - 20);
            
            // Draw crosshairs
            g2.setColor(Color.GRAY);
            g2.drawLine(centerX, 10, centerX, height - 10);
            g2.drawLine(10, centerY, width - 10, centerY);
            
            // Draw range circles
            g2.setColor(new Color(200, 200, 200));
            int radius50 = (width - 20) / 4;
            g2.drawOval(centerX - radius50, centerY - radius50, radius50 * 2, radius50 * 2);
            g2.drawOval(10, 10, width - 20, height - 20);
            
            // Calculate joystick position (scale -100 to 100 -> panel coordinates)
            int posX = centerX + (x * (width - 40) / 200);
            int posY = centerY + (y * (height - 40) / 200);
            
            // Draw joystick indicator
            g2.setColor(Color.RED);
            int indicatorSize = 20;
            g2.fill(new Ellipse2D.Double(posX - indicatorSize/2, posY - indicatorSize/2, 
                                         indicatorSize, indicatorSize));
            
            // Draw position text
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            String posText = String.format("(%d, %d)", this.x, -this.y);
            g2.drawString(posText, 5, height - 5);
        }
    }
}
