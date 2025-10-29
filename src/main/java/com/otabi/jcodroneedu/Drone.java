package com.otabi.jcodroneedu;

import com.google.common.util.concurrent.RateLimiter;
import com.otabi.jcodroneedu.autonomous.AutonomousMethod;
import com.otabi.jcodroneedu.autonomous.AutonomousMethodRegistry;
import com.otabi.jcodroneedu.buzzer.BuzzerSequence;
import com.otabi.jcodroneedu.buzzer.BuzzerSequenceRegistry;
import com.otabi.jcodroneedu.protocol.*;
import com.otabi.jcodroneedu.protocol.linkmanager.Request;
import com.otabi.jcodroneedu.protocol.buzzer.*;
import com.otabi.jcodroneedu.protocol.display.*;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.protocol.lightcontroller.Color;
import com.otabi.jcodroneedu.protocol.lightcontroller.LightDefault;
import com.otabi.jcodroneedu.protocol.linkmanager.Information;
import com.otabi.jcodroneedu.receiver.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

/**
 * The main class for controlling your CoDrone EDU drone.
 * 
 * <p>This is the primary class you'll use in all your drone programming assignments.
 * It provides simple methods to connect to your drone, make it fly, and safely land it.
 * The Drone class handles all the complex communication with the physical drone,
 * so you can focus on learning programming concepts.</p>
 * 
 * <h3>🚁 Basic Flight Pattern (L0101):</h3>
 * <pre>{@code
 * // Method 1: Simple pair() method
 * Drone drone = new Drone();
 * drone.pair();                    // Connect to your drone
 * drone.takeoff();                 // Launch into the air
 * drone.go("forward", 30, 2);      // Move forward at 30% power for 2 seconds
 * drone.go("right", 25, 1);        // Turn right at 25% power for 1 second
 * drone.hover(3);                   // Hover for 3 seconds  
 * drone.land();                    // Land safely
 * drone.close();                   // Clean up resources
 * 
 * // Method 2: Try-with-resources (automatic cleanup)
 * try (Drone drone = new Drone()) {
 *     drone.pair();
 *     drone.takeoff();
 *     drone.go("forward", 50, 1);  // Primary educational movement API
 *     drone.hover(5);
 *     drone.land();
 * }
 * }</pre>
 * 
 * <h3>🎯 Key Learning Concepts:</h3>
 * <ul>
 *   <li><strong>Connection Management:</strong> Always connect before flying</li>
 *   <li><strong>Resource Cleanup:</strong> Use {@code close()} or try-with-resources</li>
 *   <li><strong>Safety First:</strong> Use {@code emergencyStop()} if something goes wrong</li>
 *   <li><strong>Exception Handling:</strong> Catch {@link DroneNotFoundException} for connection issues</li>
 * </ul>
 * 
 * <h3>🔧 Movement Methods (L0102+):</h3>
 * <ul>
 *   <li>{@link #go(String, int, int)} - Primary educational movement API (matches Python)</li>
 *   <li>{@link #moveForward(double, String, double)} - Precise distance-based forward movement</li>
 *   <li>{@link #moveBackward(double, String, double)} - Precise distance-based backward movement</li>
 *   <li>{@link #moveLeft(double, String, double)} - Precise distance-based left movement</li>
 *   <li>{@link #moveRight(double, String, double)} - Precise distance-based right movement</li>
 *   <li>{@link #moveDistance(double, double, double, double)} - 3D movement control</li>
 *   <li>{@link #sendAbsolutePosition(double, double, double, double, int, int)} - Absolute positioning</li>
 *   <li>{@link #turn(int, Double)} - Basic turning with power and duration</li>
 *   <li>{@link #turnLeft(int)} - Turn left by specified degrees</li>
 *   <li>{@link #turnRight(int)} - Turn right by specified degrees</li>
 *   <li>{@link #turnDegree(int)} - Turn to specific heading angle</li>
 *   <li>{@link #setPitch(int)}, {@link #setRoll(int)} - Advanced manual control</li>
 *   <li>{@link #move()}, {@link #move(int)} - Execute movement commands</li>
 * </ul>
 * 
 * <h3>📡 Sensors & Safety:</h3>
 * <ul>
 *   <li>{@link #emergencyStop()} - Immediately stop all motors</li>
 *   <li>{@link #getMoveValues()} - Debug your flight commands</li>
 * </ul>
 * 
 * <p><strong>💡 Pro Tip:</strong> Always test your code with small movements first, 
 * and keep your finger ready on the emergency stop!</p>
 * 
 * @author Stephen Cerruti (with AI assistance from GitHub Copilot)
 * @version 2.3
 * @since 1.0
 * @see DroneNotFoundException
 */
import com.otabi.jcodroneedu.ml.ColorClassifier;

public class Drone implements AutoCloseable {
    // --- Machine Learning Color Classifier ---
    private ColorClassifier colorClassifier = null;
    private boolean colorClassifierLoaded = false;

    /**
     * Loads the color classifier from the given dataset path.
     * @param datasetPath Path to color dataset directory
     * @param showGraph Whether to show a 3D plot (optional)
     * @throws java.io.IOException if loading fails
     */
    public void loadColorClassifier(String datasetPath, boolean showGraph) throws java.io.IOException {
        colorClassifier = new ColorClassifier();
        colorClassifier.loadColorData(datasetPath, showGraph);
        colorClassifierLoaded = true;
    }

    /**
     * Unloads the color classifier (for test or reset).
     */
    public void unloadColorClassifier() {
        colorClassifier = null;
        colorClassifierLoaded = false;
    }

    private static final Logger log = LogManager.getLogger(Drone.class);

    // --- Core Components ---
    private final DroneStatus droneStatus;
    private final LinkManager linkManager;
    private final InventoryManager inventoryManager;
    private final ControllerInputManager controllerInputManager;
    private final Receiver receiver;
    private final SerialPortManager serialPortManager;

    // --- Controllers (Facades for specific functionalities) ---
    private final FlightController flightController;
    private final LinkController linkController;
    private final SettingsController settingsController;

    private final RateLimiter commandRateLimiter;
    private boolean isConnected = false;
    
    // --- Sensor Correction Settings ---
    private boolean useCorrectedElevation = false;
    private boolean useCalibratedTemperature = false;
    private double initialPressure = 0.0;  // For relative height measurements

    /**
     * Creates a new Drone instance ready for connection.
     * 
     * <p>This constructor initializes all internal components but does not automatically
     * connect to the physical drone. You must call {@link #pair()} or {@link #connect()}
     * before attempting any flight operations.</p>
     * 
     * <h3>🔌 Next Steps:</h3>
     * <ul>
     *   <li>Call {@code drone.pair()} for simple connection (crashes on error)</li>
     *   <li>Call {@code drone.connect()} for exception-based error handling</li>
     *   <li>Use try-with-resources for automatic cleanup</li>
     * </ul>
     * 
     * @see #pair()
     * @see #connect()
     */
    public Drone() {
        // Initialize state-holding managers
        this.droneStatus = new DroneStatus();
        this.linkManager = new LinkManager();
        this.inventoryManager = new InventoryManager();
        this.controllerInputManager = new ControllerInputManager();

        // Initialize CardColor with default NONE values
        byte[][] hsvl = new byte[2][4]; // All zeros
        byte[] colors = {0, 0}; // NONE for both sensors
        byte card = 0; // No card
        this.droneStatus.setCardColor(new com.otabi.jcodroneedu.protocol.cardreader.CardColor(hsvl, colors, card));

        // Initialize core components
        // The 'Internals' class from the original code seems to have been absorbed
        // into the new manager/controller structure. If it has other responsibilities,
        // it would be initialized here.
        this.receiver = new Receiver(this, droneStatus, linkManager, inventoryManager);
        this.serialPortManager = new SerialPortManager(receiver);

        // Initialize the controllers, passing a reference to this Drone instance
        this.flightController = new FlightController(this);
        this.linkController = new LinkController(this);
        this.settingsController = new SettingsController(this);

        // Set a default command rate limit (e.g., ~16 commands/sec)
        double permitsPerSecond = 1.0 / 0.060;
        this.commandRateLimiter = RateLimiter.create(permitsPerSecond);

        log.info("Drone instance created and ready for connection.");
    }

    /**
     * Creates a new Drone instance with optional automatic connection.
     * 
     * @param autoConnect If true, automatically attempts to connect to the drone
     * @throws DroneNotFoundException if autoConnect is true and connection fails
     * @see #Drone(boolean, String)
     */
    public Drone(boolean autoConnect) throws DroneNotFoundException
    {
        this(autoConnect, null);
    }

    /**
     * Creates a new Drone instance with optional automatic connection to a specific port.
     * 
     * <p>This constructor is useful when you know the specific serial port or want
     * to ensure connection to a particular drone controller.</p>
     * 
     * @param autoConnect If true, automatically attempts to connect to the drone
     * @param portName The specific serial port name (e.g., "COM3" on Windows, "/dev/ttyUSB0" on Linux)
     * @throws DroneNotFoundException if autoConnect is true and connection fails
     * @see #connect(String)
     */
    public Drone(boolean autoConnect, String portName) throws DroneNotFoundException
    {
        this();
        if (autoConnect) {
            connect(portName);
        }
    }

    // =================================================================================
    // --- Connection API ---
    // =================================================================================

    /**
     * Connects to the CoDrone EDU controller using Python-style error handling.
     * 
     * <p><strong>⚠️ Educational Note:</strong> This method replicates Python behavior 
     * by terminating the program if connection fails. For proper Java exception handling,
     * use {@link #connect()} instead.</p>
     * 
     * <h3>🎯 Learning Progression:</h3>
     * <ul>
     *   <li><strong>L0101:</strong> Use this method for simple connection</li>
     *   <li><strong>L0102+:</strong> Graduate to {@code connect()} with try-catch</li>
     * </ul>
     * 
     * @return true if connection successful (never returns false - program exits on failure)
     * @see #connect()
     */
    public boolean pair()
    {
        try
        {
            return connect(null);
        } catch (DroneNotFoundException e)
        {
            System.exit(-1);
        }

        return false;
    }

    /**
     * Connects to the CoDrone EDU controller on a specific port using Python-style error handling.
     * 
     * <p>This overload allows you to specify a particular serial port when multiple
     * devices might be connected.</p>
     * 
     * @param portName The serial port name (e.g., "COM3", "/dev/ttyUSB0")
     * @return true if connection successful (never returns false - program exits on failure)
     * @see #connect(String)
     */
    public boolean pair(String portName)
    {
        try
        {
            return connect(portName);
        } catch (DroneNotFoundException e)
        {
            System.exit(-1);
        }

        return false;
    }

    /**
     * Connects to the CoDrone EDU controller with proper exception handling.
     * 
     * <p>This is the recommended connection method for learning proper Java exception
     * handling patterns. Unlike {@link #pair()}, this method throws an exception
     * instead of terminating the program on failure.</p>
     * 
     * <h3>💡 Usage Example:</h3>
     * <pre>{@code
     * try {
     *     if (drone.connect()) {
     *         System.out.println("Connected successfully!");
     *         // Your flight code here
     *     }
     * } catch (DroneNotFoundException e) {
     *     System.err.println("Connection failed: " + e.getMessage());
     *     // Handle error appropriately
     * }
     * }</pre>
     * 
     * @return true if connection successful, false if connection failed but no exception occurred
     * @throws DroneNotFoundException if the drone controller cannot be found or connected to
     * @see #pair()
     */
    public boolean connect() throws DroneNotFoundException
    {
        return connect(null);
    }

    /**
     * Connects to the CoDrone EDU controller on a specific serial port.
     * @param portName The name of the serial port (e.g., "COM3" or "/dev/ttyUSB0").
     * @return true if the connection was successful, false otherwise.
     */
    public boolean connect(String portName) throws DroneNotFoundException
    {
        boolean opened = false;
        try {
            opened = serialPortManager.open(portName);
        } catch (DroneNotFoundException e) {
            disconnect(); // Ensure cleanup on failed open
            throw e;
        }

        if (!opened) {
            return false;
        }

        // After connecting, request initial state and information to verify drone communication
        sendRequest(DataType.State);
        sleep(100);
        sendRequest(DataType.Information);
        sleep(100);

        State currentState = droneStatus.getState();
        Information info = linkManager.getInformation();

        if (currentState != null && currentState.isReady() && info != null) {
            this.isConnected = true;
            
            // Student-friendly console output
            System.out.println("Successfully connected to CoDrone EDU.");
            System.out.printf("Model: %s, Firmware: %s\n", info.getModelNumber(), info.getVersion());
            System.out.printf("Battery: %d%%\n", currentState.getBattery());
            
            // Developer logging
            log.info("Connection established - Model: {}, Firmware: {}, Battery: {}%", 
                    info.getModelNumber(), info.getVersion(), currentState.getBattery());

            // Set initial speed and reset controls
            changeSpeed(2);
            flightController.resetMoveValues();
        } else {
            // Student-friendly error messages
            System.err.println("Could not verify connection with CoDrone EDU.");
            System.err.println("Check that the drone is on and paired to the controller.");
            System.err.println("How to pair: https://youtu.be/kMJhf5ykLSo");
            
            // Developer logging
            log.error("Connection verification failed - State ready: {}, Info available: {}", 
                    currentState != null && currentState.isReady(), info != null);
            disconnect();
        }

        return this.isConnected;
    }

    /**
     * Disconnects from the drone controller and closes the serial port.
     */
    // @Deprecated
    public void close() {
        disconnect();
    }

    /**
     * Disconnects from the drone controller and closes the serial port.
     */
    public void disconnect() {
        serialPortManager.disconnect();
        isConnected = false;
    }

    /**
     * Checks if the serial port to the controller is open.
     * @return true if the port is open, false otherwise.
     */
    public boolean isOpen() {
        return serialPortManager.isOpen();
    }

    /**
     * Checks if the application is successfully connected and communicating with the drone.
     * @return true if connected, false otherwise.
     */
    public boolean isConnected() {
        return isOpen() && isConnected;
    }


    // =================================================================================
    // --- Package-Protected Helpers for Controllers ---
    // =================================================================================

    /**
     * Sends a message with a header and a serializable payload.
     * This is the core method for sending data to the drone.
     * @param header The message header.
     * @param data The message payload.
     */
    void transfer(Header header, Serializable data) {
        commandRateLimiter.acquire();

        if (header == null || data == null) {
            log.error("Header or data was null.");
            throw new IllegalArgumentException("Header or data is null.");
        }

        byte[] headerArray = header.toArray();
        byte[] dataArray = data.toArray();

        int crc16 = CRC16.calc(headerArray, 0);
        crc16 = CRC16.calc(dataArray, crc16);

        ByteBuffer message = ByteBuffer.allocate(2 + headerArray.length + dataArray.length + 2);
        message.order(ByteOrder.LITTLE_ENDIAN);
        message.put(DroneSystem.CommunicationConstants.PROTOCOL_START_BYTE_1).put(DroneSystem.CommunicationConstants.PROTOCOL_START_BYTE_2);
        message.put(headerArray);
        message.put(dataArray);
        message.putShort((short) crc16);

        serialPortManager.write(message.array());
    }

    /**
     * Sends a simple command to the drone with default device types.
     */
    void sendCommand(CommandType type, byte option) {
        sendCommand(type, option, DeviceType.Base, DeviceType.Drone);
    }

    /**
     * Sends a simple command to a specific device.
     */
    void sendCommand(CommandType type, byte option, DeviceType from, DeviceType to) {
        Command command = new Command(type, option);
        sendMessage(command, from, to);
    }

    /**
     * Sends a serializable message with default device types.
     */
    void sendMessage(Serializable message) {
        sendMessage(message, DeviceType.Base, DeviceType.Drone);
    }

    /**
     * Sends a serializable message to a specific device.
     */
    void sendMessage(Serializable message, DeviceType from, DeviceType to) {
        Header header = new Header(DataType.fromClass(message.getClass()), message.getSize(), from, to);
        transfer(header, message);
    }

    /**
     * Sends a message and waits for an acknowledgment from the drone.
     * Throws an exception if the ACK is not received after 3 retries.
     * @param message The message to send.
     * @throws MessageNotSentException if the message could not be confirmed.
     */
    void sendMessageWait(Serializable message) throws MessageNotSentException {
        sendMessageWait(message, DeviceType.Base, DeviceType.Drone);
    }

    /**
     * Sends a message to a specific device and waits for an acknowledgment.
     * @param message The message to send.
     * @param from The originating device.
     * @param to The target device.
     * @throws MessageNotSentException if the message could not be confirmed.
     */
    void sendMessageWait(Serializable message, DeviceType from, DeviceType to) throws MessageNotSentException {
        Header header = new Header(DataType.fromClass(message.getClass()), message.getSize(), from, to);
        DataType expectingAck = header.getDataType();

        for (int tries = 1; tries <= 3; tries++) {
            try {
                receiver.expectAck(expectingAck);
                CompletableFuture<Void> ackFuture = receiver.getAckFuture(expectingAck);
                transfer(header, message);
                ackFuture.get(200, TimeUnit.MILLISECONDS);
                return; // Success!
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MessageNotSentException("Message sending was interrupted.", e);
            } catch (TimeoutException e) {
                log.warn("ACK not received for {}, attempt {}/3", expectingAck, tries);
            } catch (Exception e) {
                throw new MessageNotSentException("Failed to get ACK for " + expectingAck, e);
            }
        }
        throw new MessageNotSentException("Maximum retries exceeded for " + expectingAck);
    }

    /**
     * Sends a request for a specific type of data from the drone.
     * @param dataType The type of data to request.
     */
    public void sendRequest(DataType dataType) {
        Request request = new Request(dataType);
        sendMessage(request);
    }

    // =================================================================================
    // --- Flight Control API ---
    // =================================================================================

    /**
     * Commands the drone to take off and hover. This method blocks until the drone
     * reports it is in the takeoff state or a timeout occurs.
     */
    public void takeoff() {
        flightController.takeoff();
    }

    /**
     * Commands the drone to land gently at its current position. This method blocks
     * until the drone reports it is in the landing state or a timeout occurs.
     */
    public void land() {
        flightController.land();
    }

    /**
     * Immediately stops all motors. This is a critical safety command used for emergencies.
     */
    public void emergencyStop() {
        flightController.emergencyStop();
    }

    /**
     * Commands the drone to hover in place for a specific duration by sending zero
     * values for roll, pitch, yaw, and throttle.
     *
     * @param durationSeconds The duration to hover, in seconds (matching Python behavior).
     */
    public void hover(double durationSeconds) {
        flightController.hover(durationSeconds);
    }

    /**
     * Resets the drone's movement values to zero to ensure it stops any prior movement.
     */
    public void resetMoveValues() {
        flightController.resetMoveValues();
    }

    // ============================================================================
    // Autonomous Flight Methods - Sensor-Driven Behaviors
    // ============================================================================

    /**
     * Autonomously maintains a specified distance from a wall ahead of the drone.
     * The drone uses its front range sensor to detect the wall and adjusts pitch
     * (forward/backward movement) to stay at the target distance. This method
     * provides matching functionality to the Python API's avoid_wall() method.
     * 
     * <p><strong>🎯 How it works:</strong></p>
     * <ul>
     *   <li>Continuously reads front range sensor</li>
     *   <li>Calculates distance error from target</li>
     *   <li>Applies proportional control to adjust position</li>
     *   <li>Maintains position within threshold (±20cm)</li>
     * </ul>
     * 
     * <p><strong>💡 Educational Use (L0205):</strong></p>
     * <pre>{@code
     * // Basic wall avoidance
     * drone.takeoff();
     * drone.avoidWall(10, 50);  // Keep 50cm from wall for 10 seconds
     * drone.land();
     * 
     * // Following a wall at different distances
     * drone.takeoff();
     * drone.avoidWall(5, 80);   // Stay 80cm away
     * drone.setYaw(50);          // Start turning
     * drone.move(3);             // Turn while maintaining wall distance
     * drone.land();
     * }</pre>
     * 
     * <p><strong>⚠️ Safety Notes:</strong></p>
     * <ul>
     *   <li>Requires clear space in front of the drone</li>
     *   <li>Front range sensor must detect a surface within 200cm</li>
     *   <li>Does not control altitude, yaw, or roll - use hover() for stability</li>
     *   <li>Best used in controlled indoor environments</li>
     * </ul>
     * 
     * @param timeout Duration to maintain wall distance, in seconds (1-30)
     * @param distance Target distance from wall, in centimeters (10-100)
     * @throws IllegalArgumentException if timeout not in range 1-30 or distance not in range 10-100
     * @see #keepDistance(int, int)
     * @see #getFrontRange()
     * @educational
     * @pythonEquivalent avoid_wall(timeout, distance)
     */
    public void avoidWall(int timeout, int distance) {
        AutonomousMethodRegistry registry = AutonomousMethodRegistry.getInstance();
        AutonomousMethod method = registry.getMethod("avoidWall");
        
        Map<String, Integer> params = new HashMap<>();
        params.put("timeout", timeout);
        params.put("distance", distance);
        
        method.execute(this, params);
    }

    /**
     * Autonomously maintains a specified distance from an object ahead of the drone.
     * Similar to avoidWall(), but optimized for tracking moving objects or maintaining
     * distance from non-wall surfaces. The drone uses its front range sensor and applies
     * proportional control to adjust its position. This method provides matching
     * functionality to the Python API's keep_distance() method.
     * 
     * <p><strong>🎯 How it works:</strong></p>
     * <ul>
     *   <li>Continuously monitors front range sensor</li>
     *   <li>Calculates distance error from target</li>
     *   <li>Applies proportional control (P-controller)</li>
     *   <li>Maintains position within threshold (±10cm)</li>
     * </ul>
     * 
     * <p><strong>💡 Educational Use (L0206):</strong></p>
     * <pre>{@code
     * // Following a moving person/object
     * drone.takeoff();
     * drone.keepDistance(15, 60);  // Maintain 60cm distance for 15 seconds
     * drone.land();
     * 
     * // Combining with other movements
     * drone.takeoff();
     * drone.keepDistance(8, 40);   // Stay 40cm from object
     * drone.setRoll(30);           // Strafe right while tracking
     * drone.move(2);
     * drone.land();
     * }</pre>
     * 
     * <p><strong>🔬 Algorithm Details:</strong></p>
     * The method uses a proportional controller with P-value of 0.4:
     * <pre>{@code
     * error = (target_distance - current_distance) / target_distance
     * speed = error * 0.4 * 100
     * }</pre>
     * This creates smooth, responsive distance maintenance without oscillation.
     * 
     * <p><strong>⚠️ Safety Notes:</strong></p>
     * <ul>
     *   <li>Requires detectable object within sensor range (10-200cm)</li>
     *   <li>May move forward/backward to maintain distance</li>
     *   <li>Does not control altitude, yaw, or roll</li>
     *   <li>Best for flat, level surfaces</li>
     * </ul>
     * 
     * @param timeout Duration to maintain distance, in seconds (1-30)
     * @param distance Target distance from object, in centimeters (10-100)
     * @throws IllegalArgumentException if timeout not in range 1-30 or distance not in range 10-100
     * @see #avoidWall(int, int)
     * @see #getFrontRange()
     * @educational
     * @pythonEquivalent keep_distance(timeout, distance)
     */
    public void keepDistance(int timeout, int distance) {
        AutonomousMethodRegistry registry = AutonomousMethodRegistry.getInstance();
        AutonomousMethod method = registry.getMethod("keepDistance");
        
        Map<String, Integer> params = new HashMap<>();
        params.put("timeout", timeout);
        params.put("distance", distance);
        
        method.execute(this, params);
    }

    /**
     * This is a setter function that allows you to set the pitch variable.
     * Once you set pitch, you have to use move() to actually execute the movement.
     * The pitch variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want
     * the drone to pitch again.
     *
     * <p><strong>Examples:</strong></p>
     * <pre>{@code
     * // Basic usage with literal values
     * drone.setPitch(50);   // 50% forward power
     * drone.setPitch(-30);  // 30% backward power
     * drone.setPitch(0);    // Neutral/stop
     * 
     * // Using constants for better code quality
     * drone.setPitch(DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX);     // 100% forward
     * drone.setPitch(DroneSystem.FlightControlConstants.CONTROL_VALUE_OFF);     // Stop (most intuitive)
     * drone.setPitch(DroneSystem.FlightControlConstants.CONTROL_VALUE_MIN);     // 100% backward
     * 
     * // Alternative ways to express "stop" - all equivalent
     * drone.setPitch(DroneSystem.FlightControlConstants.CONTROL_VALUE_NEUTRAL); // Technical term
     * drone.setPitch(DroneSystem.FlightControlConstants.CONTROL_VALUE_STOP);    // Action-oriented
     * }</pre>
     *
     * @param pitch Sets the pitch variable ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} 
     *              to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}). The number represents the
     *              direction and power of the output for that flight motion variable.
     *              Negative pitch is backwards, positive pitch is forwards.
     */
    public void setPitch(int pitch) {
        flightController.setPitch(pitch);
    }

    /**
     * This is a setter function that allows you to set the roll variable.
     * Once you set roll, you have to use move() to actually execute the movement.
     * The roll variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want
     * the drone to roll again.
     *
     * <p><strong>Examples:</strong></p>
     * <pre>{@code
     * // Basic usage with literal values
     * drone.setRoll(40);   // 40% right power
     * drone.setRoll(-25);  // 25% left power
     * drone.setRoll(0);    // Neutral/stop
     * 
     * // Using constants for better code quality
     * drone.setRoll(DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX);     // 100% right
     * drone.setRoll(DroneSystem.FlightControlConstants.CONTROL_VALUE_OFF);     // Stop rolling
     * drone.setRoll(DroneSystem.FlightControlConstants.CONTROL_VALUE_MIN);     // 100% left
     * }</pre>
     *
     * @param roll Sets the roll variable ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} 
     *             to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}). The number represents the
     *             direction and power of the output for that flight motion variable.
     *             Negative roll is left, positive roll is right.
     */
    public void setRoll(int roll) {
        flightController.setRoll(roll);
    }

    /**
     * This is a setter function that allows you to set the yaw variable.
     * Once you set yaw, you have to use move()to actually execute the movement.
     * The yaw variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want
     * the drone to yaw again.
     *
     * <p><strong>Examples:</strong></p>
     * <pre>{@code
     * // Basic usage with literal values
     * drone.setYaw(60);   // 60% left turn power
     * drone.setYaw(-45);  // 45% right turn power
     * drone.setYaw(0);    // Neutral/stop turning
     * 
     * // Using constants for better code quality
     * drone.setYaw(DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX);     // 100% left turn
     * drone.setYaw(DroneSystem.FlightControlConstants.CONTROL_VALUE_NEUTRAL); // Stop turning
     * drone.setYaw(DroneSystem.FlightControlConstants.CONTROL_VALUE_MIN);     // 100% right turn
     * }</pre>
     *
     * @param yaw Sets the yaw variable ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} 
     *            to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}). The number represents the
     *            direction and power of the output for that flight motion variable.
     *            Negative yaw is right, positive yaw is left.
     */
    public void setYaw(int yaw) {
        flightController.setYaw(yaw);
    }

    /**
     * This is a setter function that allows you to set the throttle variable.
     * Once you set throttle, you have to use move() to actually execute the movement.
     * The throttle variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want the
     * drone to throttle again.
     *
     * <p><strong>Examples:</strong></p>
     * <pre>{@code
     * // Basic usage with literal values
     * drone.setThrottle(70);   // 70% upward power
     * drone.setThrottle(-20);  // 20% downward power
     * drone.setThrottle(0);    // Neutral/hover
     * 
     * // Using constants for better code quality
     * drone.setThrottle(DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX);  // 100% up
     * drone.setThrottle(DroneSystem.FlightControlConstants.CONTROL_VALUE_STOP); // Hover
     * drone.setThrottle(DroneSystem.FlightControlConstants.CONTROL_VALUE_MIN);  // 100% down
     * }</pre>
     *
     * @param throttle Sets the throttle variable ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} 
     *                 to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     *                 The number represents the direction and power of the
     *                 output for that flight motion variable.
     *                 Negative throttle is down, positive throttle is up.
     */
    public void setThrottle(int throttle) {
        flightController.setThrottle(throttle);
    }

    /**
     * Used with set_roll, set_pitch, set_yaw, set_throttle commands.
     * Sends flight movement values to the drone.
     */
    public void move()
    {
        flightController.move();
    }

    /**
     * Used with set_roll, set_pitch, set_yaw, set_throttle commands.
     * Sends flight movement values to the drone.
     *
     * @param duration Number of seconds to perform the action
     */
    public void move(int duration)
    {
        flightController.move(duration);
    }

    /**
     * Prints current values of roll, pitch, yaw, and throttle.
     */
    public void printMoveValues() {
        flightController.printMoveValues();
    }

    /**
     * Returns current values of roll, pitch, yaw, and throttle.
     *
     * @return A byte array of roll(0), pitch (1), yaw (2) and throttle (3) values.
     */
    /**
     * Returns current values of roll, pitch, yaw, and throttle.
     *
     * @return A byte array of roll(0), pitch (1), yaw (2) and throttle (3) values.
     */
    /**
     * @deprecated Use {@link #getMoveValues()} instead.
     */
    @Deprecated(forRemoval = true)
    public byte[] get_move_values() {
        return getMoveValues();
    }
    public byte[] getMoveValues() {
        return flightController.getMoveValues();
    }

    /**
     * Sets the drone's flight responsiveness level.
     *
     * @param speedLevel The desired speed level, from 1 (slow) to 3 (fast).
     */
    public void changeSpeed(int speedLevel) {
        flightController.changeSpeed(speedLevel);
    }

    /**
     * Enables or disables headless mode.
     *
     * @param enable Set to true to enable headless mode, false to disable.
     */
    public void setHeadlessMode(boolean enable) {
        flightController.setHeadlessMode(enable);
    }

    /**
     * Triggers a pre-programmed flight event, such as a flip.
     *
     * @param event The flight event to execute.
     */
    public void triggerFlightEvent(FlightController.FlightEvent event) {
        flightController.triggerFlightEvent(event);
    }

    /**
     * Sends a single, direct flight control command to the drone.
     *
     * <p><strong>Examples:</strong></p>
     * <pre>{@code
     * // Basic usage with literal values
     * drone.sendControl(30, 50, 0, 20);    // Roll right, pitch forward, no yaw, climb
     * drone.sendControl(0, 0, 0, 0);       // Hover in place
     * 
     * // Using constants for clarity
     * drone.sendControl(DroneSystem.FlightControlConstants.CONTROL_VALUE_OFF, 
     *                   DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX,
     *                   DroneSystem.FlightControlConstants.CONTROL_VALUE_STOP, 
     *                   30); // No roll, full forward, no yaw, climb at 30%
     * }</pre>
     *
     * @param roll     Controls sideways tilt. Positive values roll right, negative values roll left 
     *                 ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     * @param pitch    Controls forward and backward tilt. Positive values pitch forward, negative values pitch backward 
     *                 ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     * @param yaw      Controls rotation. Positive values turn left, negative values turn right 
     *                 ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     * @param throttle Controls altitude. Positive values increase altitude, negative values decrease altitude 
     *                 ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     */
    public void sendControl(int roll, int pitch, int yaw, int throttle) {
        flightController.sendControl(roll, pitch, yaw, throttle);
    }

    /**
     * Continuously sends a flight control command for a specified duration.
     *
     * <p><strong>Examples:</strong></p>
     * <pre>{@code
     * // Hover forward for 2 seconds
     * drone.sendControlWhile(0, 40, 0, 0, 2000);
     * 
     * // Turn left while climbing for 1.5 seconds
     * drone.sendControlWhile(0, 0, 60, 30, 1500);
     * 
     * // Using constants for maximum values
     * drone.sendControlWhile(DroneSystem.FlightControlConstants.CONTROL_VALUE_MAX, 
     *                        DroneSystem.FlightControlConstants.CONTROL_VALUE_OFF, 
     *                        DroneSystem.FlightControlConstants.CONTROL_VALUE_NEUTRAL, 
     *                        DroneSystem.FlightControlConstants.CONTROL_VALUE_STOP, 1000); // Full right roll
     * }</pre>
     *
     * @param roll     The roll value to maintain ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     * @param pitch    The pitch value to maintain ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     * @param yaw      The yaw value to maintain ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     * @param throttle The throttle value to maintain ({@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MIN} to {@link DroneSystem.FlightControlConstants#CONTROL_VALUE_MAX}).
     * @param timeMs   The duration to send the command for, in milliseconds.
     */
    public void sendControlWhile(int roll, int pitch, int yaw, int throttle, long timeMs) {
        flightController.sendControlWhile(roll, pitch, yaw, throttle, timeMs);
    }

    /**
     * Sends a high-level command to move the drone to a specific position relative to its current location.
     *
     * @param positionX          The target position on the X-axis in meters. Positive is forward, negative is backward.
     * @param positionY          The target position on the Y-axis in meters. Positive is left, negative is right.
     * @param positionZ          The target position on the Z-axis in meters. Positive is up, negative is down.
     * @param velocity           The speed to move towards the target position in m/s.
     * @param heading            The target heading in degrees. Positive is a left turn, negative is a right turn.
     * @param rotationalVelocity The speed for turning in degrees per second.
     */
    public void sendControlPosition(float positionX, float positionY, float positionZ, float velocity, int heading, int rotationalVelocity) {
        flightController.sendControlPosition(positionX, positionY, positionZ, velocity, heading, rotationalVelocity);
    }

    // =================================================================================
    // --- Link Control API ---
    // =================================================================================

    /**
     * Sets the operational mode for the communication link.
     *
     * @param mode The desired link mode (e.g., Client, Server, Pairing).
     */
    public void setLinkMode(LinkController.LinkMode mode) {
        linkController.setLinkMode(mode);
    }

    /**
     * Sets the controller's main operational mode (e.g., flight control vs. link management).
     *
     * @param mode The desired controller mode.
     */
    public void setControllerMode(LinkController.ControllerMode mode) {
        linkController.setControllerMode(mode);
    }

    /**
     * Turns the controller's display backlight on or off.
     *
     * @param enable Set to true to turn the backlight on, false to turn it off.
     */
    public void setBacklight(boolean enable) {
        linkController.setBacklight(enable);
    }

    // =================================================================================
    // --- Settings Control API ---
    // =================================================================================

    /**
     * Resets the drone's internal gyroscope sensor bias to correct for drift.
     */
    public void clearBias() {
        settingsController.clearBias();
    }

    /**
     * Resets the flight trim values to their default (zero) settings.
     */
    public void clearTrim() {
        settingsController.clearTrim();
    }

    /**
     * Resets all drone settings to their factory defaults.
     */
    public void setDefault() {
        settingsController.setDefault();
    }

    // ========================================
    // Python-Compatible Reset and Trim Methods
    // ========================================

    /**
     * Resets the gyroscope and calibrates it for accurate angle measurements.
     * <p>
     * This method initiates a gyroscope calibration process. During calibration,
     * the drone must be placed on a flat, level surface and remain completely
     * stationary. Moving the drone during calibration will result in incorrect
     * gyro values.
     * </p>
     * <p>
     * The method actively monitors the {@code MOTION_CALIBRATING} error flag
     * and blocks until calibration is complete (typically 3-5 seconds) or times out
     * after 10 seconds.
     * </p>
     * 
     * @throws RuntimeException if calibration fails or times out
     * @apiNote Equivalent to Python's {@code drone.reset_gyro()}
     * @see ErrorData#isCalibrating()
     * @see DroneSystem.ErrorFlagsForSensor#MOTION_CALIBRATING
     * @educational
     */
    public void resetGyro() {
        log.info("Starting gyroscope calibration - keep drone stationary on flat surface");
        
        // Send clear bias command to initiate calibration
        settingsController.clearBias();
        
        try {
            Thread.sleep(200); // Initial delay for command processing
            
            // Wait for calibration to complete by monitoring error flags
            long calibrationStart = System.currentTimeMillis();
            long timeout = 10000; // 10 second timeout
            long minCalibrationTime = 3000; // Minimum 3 seconds for calibration process
            boolean errorDataReceived = false;
            int nullDataCount = 0;
            
            while (System.currentTimeMillis() - calibrationStart < timeout) {
                // Request fresh error data to check calibration status
                sendRequest(DataType.Error);
                Thread.sleep(100);
                
                // Get current error data to check calibration flag
                ErrorData errors = getErrors();
                
                if (errors != null) {
                    errorDataReceived = true;
                    // Check if motion calibration flag is cleared (calibration complete)
                    boolean isCalibrating = errors.isCalibrating();
                    long elapsed = System.currentTimeMillis() - calibrationStart;
                    
                    // Ensure minimum calibration time has passed before accepting completion
                    // This prevents false positives from initial flag state
                    if (!isCalibrating && elapsed > minCalibrationTime) {
                        log.info("Gyroscope calibration complete ({}ms)", elapsed);
                        return;
                    }
                    
                    if (isCalibrating) {
                        log.debug("Calibrating... ({}ms elapsed)", elapsed);
                    }
                } else {
                    // Track null error data to provide better error message
                    nullDataCount++;
                    if (nullDataCount > 20 && !errorDataReceived) {
                        // After 2 seconds of no data, warn about possible connection issue
                        log.warn("No error data received - check drone connection");
                    }
                }
            }
            
            // Provide more specific error message based on what happened
            if (!errorDataReceived) {
                throw new RuntimeException("Gyroscope calibration timed out - no error data received. Check drone connection and ensure it is powered on.");
            } else {
                throw new RuntimeException("Gyroscope calibration timed out - ensure drone is on flat surface and stationary. Calibration flag may not have cleared.");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gyroscope calibration interrupted", e);
        }
    }

    /**
     * Sets the drone's trim values for roll and pitch to compensate for
     * minor balance issues or manufacturing variations.
     * <p>
     * Trim values help the drone maintain level flight without constant
     * stick input. Positive roll trim makes the drone tilt right,
     * negative makes it tilt left. Positive pitch trim makes it tilt forward,
     * negative makes it tilt backward.
     * </p>
     * 
     * @param roll Roll trim value from -100 to 100
     * @param pitch Pitch trim value from -100 to 100  
     * @throws IllegalArgumentException if trim values are outside valid range
     * @apiNote Equivalent to Python's {@code drone.set_trim(roll, pitch)}
     * @educational
     */
    public void setTrim(int roll, int pitch) {
        // Validate input parameters
        if (roll < -100 || roll > 100) {
            throw new IllegalArgumentException("Roll trim must be between -100 and 100, got: " + roll);
        }
        if (pitch < -100 || pitch > 100) {
            throw new IllegalArgumentException("Pitch trim must be between -100 and 100, got: " + pitch);
        }
        
        log.debug("Setting trim values - roll: {}, pitch: {}", roll, pitch);
        
        // Send trim command multiple times for reliability (matching Python behavior)
        for (int attempt = 0; attempt < 3; attempt++) {
            settingsController.sendTrim((short) roll, (short) pitch, (short) 0, (short) 0);
            try {
                Thread.sleep(200); // Delay between attempts
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Trim setting interrupted", e);
            }
        }
        
        log.info("Trim values set successfully - roll: {}, pitch: {}", roll, pitch);
    }

    /**
     * Resets all trim values to zero (neutral position).
     * <p>
     * This is equivalent to calling {@code set_trim(0, 0)} and restores
     * the drone to factory trim settings.
     * </p>
     * 
     * @apiNote Equivalent to Python's {@code drone.reset_trim()}
     * @educational  
     */
    public void resetTrim() {
        log.debug("Resetting trim values to zero");
        clearTrim();
        log.info("Trim values reset to neutral");
    }

    /**
     * Gets the current trim values from the drone.
     * <p>
     * Returns the current roll and pitch trim values that are being
     * applied to keep the drone level during flight.
     * </p>
     * 
     * @return Array containing [roll, pitch] trim values from -100 to 100
     * @apiNote Equivalent to Python's {@code drone.get_trim()}
     * @educational
     */
    /**
     * Gets the current trim values from the drone.
     * <p>
     * Returns the current roll and pitch trim values that are being
     * applied to keep the drone level during flight.
     * </p>
     *
     * @return Array containing [roll, pitch] trim values from -100 to 100
     * @apiNote Equivalent to Python's {@code drone.get_trim()}
     * @educational
     */
    public int[] getTrim() {
        log.debug("Requesting current trim values");
        
        // Only request data if connected, otherwise return defaults
        if (isConnected()) {
            try {
                // Request fresh trim data from drone
                sendRequest(DataType.Trim);
                
                Thread.sleep(80); // Allow time for data to be received (matching Python delay)
                
                // Get trim data from drone status
                com.otabi.jcodroneedu.protocol.settings.Trim trimData = droneStatus.getTrim();
                if (trimData != null) {
                    int[] result = {(int) trimData.getRoll(), (int) trimData.getPitch()};
                    log.debug("Retrieved trim values - roll: {}, pitch: {}", result[0], result[1]);
                    return result;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Trim data request interrupted", e);
            } catch (Exception e) {
                log.warn("Failed to retrieve trim data: {}", e.getMessage());
            }
        }
        
        log.warn("No trim data available (disconnected or no data), returning default values");
        return new int[]{0, 0}; // Default neutral trim
    }

    /**
     * Clears internal flight counters, such as total flight time.
     */
    public void clearCounter() {
        settingsController.clearCounter();
    }

    // =================================================================================
    // --- Component Access (Advanced Usage) ---
    // =================================================================================

    /**
     * Gets the internal flight controller for advanced flight operations.
     * 
     * <p><strong>⚠️ Advanced Usage:</strong> This exposes internal implementation details.
     * Most educational assignments should use the direct methods on {@code Drone} instead.</p>
     * 
     * @return the flight controller instance
     */
    public FlightController getFlightController() {
        return flightController;
    }

    /**
     * Gets the internal link controller for communication management.
     * 
     * <p><strong>⚠️ Advanced Usage:</strong> This exposes internal implementation details.</p>
     * 
     * @return the link controller instance
     */
    public LinkController getLinkController() {
        return linkController;
    }

    /**
     * Gets the internal settings controller for drone configuration.
     * 
     * <p><strong>⚠️ Advanced Usage:</strong> This exposes internal implementation details.</p>
     * 
     * @return the settings controller instance
     */
    public SettingsController getSettingsController() {
        return settingsController;
    }

    /**
     * Gets the current drone status information.
     * 
     * <p>This provides access to battery level, flight state, and other status data.</p>
     * 
     * @return the drone status instance
     */
    public DroneStatus getDroneStatus() {
        return droneStatus;
    }

    /**
     * Gets the link manager for connection information.
     * 
     * <p>This provides access to controller model, firmware version, and connection details.</p>
     * 
     * @return the link manager instance
     */
    public LinkManager getLinkManager() {
        return linkManager;
    }

    /**
     * Helper method to pause execution, handling InterruptedException.
     * @param durationMs The duration to sleep, in milliseconds.
     */
    private void sleep(long durationMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Gets the internal receiver component for communication handling.
     * 
     * <p><strong>⚠️ Internal Use:</strong> This method exposes low-level communication
     * details and should typically not be used in educational assignments.</p>
     * 
     * @return the receiver instance
     */
    public Receiver getReceiver()
    {
        return receiver;
    }
    
    /**
     * Fly in a given direction for the given duration and power.
     * 
     * <p>This is the <strong>primary educational movement API</strong> for CoDrone EDU,
     * matching the Python implementation exactly. Students can use simple commands
     * like {@code drone.go("forward", 30, 1)} for intuitive movement.</p>
     * 
     * <h3>🎯 Educational Usage Examples:</h3>
     * <pre>{@code
     * // Basic movement patterns
     * drone.go("forward", 30, 2);    // Move forward at 30% power for 2 seconds
     * drone.go("backward", 50, 1);   // Move backward at 50% power for 1 second
     * drone.go("left", 25, 3);       // Move left at 25% power for 3 seconds
     * drone.go("right", 40, 1);      // Move right at 40% power for 1 second
     * drone.go("up", 30, 2);         // Move up at 30% power for 2 seconds
     * drone.go("down", 30, 1);       // Move down at 30% power for 1 second
     * 
     * // Simple square pattern
     * for (int i = 0; i < 4; i++) {
     *     drone.go("forward", 50, 1);
     *     drone.go("right", 30, 1);  // Turn (though turn_right() is preferred)
     * }
     * }</pre>
     * 
     * <h3>🔧 How it works:</h3>
     * <ol>
     *   <li>Resets all control variables to 0</li>
     *   <li>Sets the appropriate control variable based on direction</li>
     *   <li>Executes the movement for the specified duration</li>
     *   <li>Hovers for 1 second to stabilize</li>
     * </ol>
     * 
     * <p><strong>💡 Pro Tips:</strong></p>
     * <ul>
     *   <li>Start with low power values (20-30) when learning</li>
     *   <li>Use short durations (1-2 seconds) for precise control</li>
     *   <li>Valid directions: "forward", "backward", "left", "right", "up", "down"</li>
     *   <li>Direction names are case-insensitive</li>
     *   <li>For type safety, use {@link DroneSystem.DirectionConstants}</li>
     * </ul>
     * 
     * <p><strong>Type-Safe Examples:</strong></p>
     * <pre>{@code
     * // Using direction constants for better code quality
     * drone.go(DroneSystem.DirectionConstants.FORWARD, 30, 2);
     * drone.go(DroneSystem.DirectionConstants.LEFT, 25, 3);
     * drone.go(DroneSystem.DirectionConstants.UP, 30, 2);
     * }</pre>
     * 
     * @param direction String direction: "forward", "backward", "left", "right", "up", "down"
     *                  or use {@link DroneSystem.DirectionConstants} for type safety
     * @param power Power level from 0-100 (higher = faster movement)
     * @param duration Duration in seconds (how long to fly in that direction)
     */
    public void go(String direction, int power, int duration) {
        flightController.go(direction, power, duration);
    }
    
    /**
     * Fly in a given direction for the given duration using default power (50%).
     * 
     * <p>Simplified version of {@link #go(String, int, int)} with moderate power.</p>
     * 
     * @param direction String direction: "forward", "backward", "left", "right", "up", "down"
     *                  or use {@link DroneSystem.DirectionConstants} for type safety
     * @param duration Duration in seconds
     */
    public void go(String direction, int duration) {
        flightController.go(direction, duration);
    }
    
    /**
     * Fly in a given direction for 1 second using default power (50%).
     * 
     * <p>Simplified version of {@link #go(String, int, int)} with moderate power and short duration.</p>
     * 
     * @param direction String direction: "forward", "backward", "left", "right", "up", "down"
     *                  or use {@link DroneSystem.DirectionConstants} for type safety
     */
    public void go(String direction) {
        flightController.go(direction);
    }

    // ========================================
    // Distance-Based Movement Methods (Punch List Item #2)
    // Python API Compatibility: move_forward(), move_backward(), move_left(), move_right()
    // ========================================
    
    /**
     * Move forward a specific distance with precision control.
     * 
     * <p>This method provides Python API compatibility, allowing students to move the drone
     * a precise distance forward using familiar units.</p>
     * 
     * <p><strong>Examples:</strong></p>
     * <pre>
     * drone.moveForward(50);           // Move 50 cm forward at default speed
     * drone.moveForward(100, "cm");    // Move 100 cm forward
     * drone.moveForward(2, "ft", 1.5); // Move 2 feet forward at 1.5 m/s
     * drone.moveForward(0.5, "m");     // Move 0.5 meters forward
     * </pre>
     * 
     * <p><strong>For better type safety, use constants:</strong></p>
     * <pre>
     * drone.moveForward(100, DroneSystem.UnitConversion.UNIT_CENTIMETERS);    // Type-safe cm
     * drone.moveForward(2, DroneSystem.UnitConversion.UNIT_FEET, 1.5);        // Type-safe feet
     * drone.moveForward(0.5, DroneSystem.UnitConversion.UNIT_METERS);         // Type-safe meters
     * </pre>
     * 
     * @param distance The distance to move forward
     * @param units The unit: {@link DroneSystem.UnitConversion#UNIT_CENTIMETERS} (default), 
     *              {@link DroneSystem.UnitConversion#UNIT_INCHES}, 
     *              {@link DroneSystem.UnitConversion#UNIT_FEET}, 
     *              {@link DroneSystem.UnitConversion#UNIT_METERS}
     * @param speed The speed from 0.5 to 2.0 m/s (default: 0.5)
     * 
     * @see #moveBackward(double, String, double)
     * @see #moveLeft(double, String, double)
     * @see #moveRight(double, String, double)
     */
    public void moveForward(double distance, String units, double speed) {
        flightController.moveForward(distance, units, speed);
    }
    
    /**
     * Move forward with default units (cm) and speed (0.5 m/s).
     */
    public void moveForward(double distance) {
        flightController.moveForward(distance);
    }
    
    /**
     * Move forward with specified units and default speed (0.5 m/s).
     */
    public void moveForward(double distance, String units) {
        flightController.moveForward(distance, units);
    }
    
    /**
     * Move backward a specific distance with precision control.
     * 
     * <p>This method provides Python API compatibility for precise backward movement.</p>
     * 
     * <p><strong>For better type safety, use constants:</strong></p>
     * <pre>
     * drone.moveBackward(50, DroneSystem.UnitConversion.UNIT_CENTIMETERS);
     * drone.moveBackward(1, DroneSystem.UnitConversion.UNIT_METERS, 1.2);
     * </pre>
     * 
     * @param distance The distance to move backward
     * @param units The unit: {@link DroneSystem.UnitConversion#UNIT_CENTIMETERS} (default), 
     *              {@link DroneSystem.UnitConversion#UNIT_INCHES}, 
     *              {@link DroneSystem.UnitConversion#UNIT_FEET}, 
     *              {@link DroneSystem.UnitConversion#UNIT_METERS}
     * @param speed The speed from 0.5 to 2.0 m/s (default: 0.5)
     */
    public void moveBackward(double distance, String units, double speed) {
        flightController.moveBackward(distance, units, speed);
    }
    
    /**
     * Move backward with default units (cm) and speed (0.5 m/s).
     */
    public void moveBackward(double distance) {
        flightController.moveBackward(distance);
    }
    
    /**
     * Move backward with specified units and default speed (0.5 m/s).
     */
    public void moveBackward(double distance, String units) {
        flightController.moveBackward(distance, units);
    }
    
    /**
     * Move left a specific distance with precision control.
     * 
     * <p>This method provides Python API compatibility for precise leftward movement.</p>
     * 
     * <p><strong>For better type safety, use constants:</strong></p>
     * <pre>
     * drone.moveLeft(30, DroneSystem.UnitConversion.UNIT_CENTIMETERS);
     * drone.moveLeft(1.5, DroneSystem.UnitConversion.UNIT_FEET, 0.8);
     * </pre>
     * 
     * @param distance The distance to move left
     * @param units The unit: {@link DroneSystem.UnitConversion#UNIT_CENTIMETERS} (default), 
     *              {@link DroneSystem.UnitConversion#UNIT_INCHES}, 
     *              {@link DroneSystem.UnitConversion#UNIT_FEET}, 
     *              {@link DroneSystem.UnitConversion#UNIT_METERS}
     * @param speed The speed from 0.5 to 2.0 m/s (default: 0.5)
     */
    public void moveLeft(double distance, String units, double speed) {
        flightController.moveLeft(distance, units, speed);
    }
    
    /**
     * Move left with default units (cm) and speed (0.5 m/s).
     */
    public void moveLeft(double distance) {
        flightController.moveLeft(distance);
    }
    
    /**
     * Move left with specified units and default speed (0.5 m/s).
     */
    public void moveLeft(double distance, String units) {
        flightController.moveLeft(distance, units);
    }
    
    /**
     * Move right a specific distance with precision control.
     * 
     * <p>This method provides Python API compatibility for precise rightward movement.</p>
     * 
     * <p><strong>For better type safety, use constants:</strong></p>
     * <pre>
     * drone.moveRight(40, DroneSystem.UnitConversion.UNIT_CENTIMETERS);
     * drone.moveRight(2, DroneSystem.UnitConversion.UNIT_FEET, 1.1);
     * </pre>
     * 
     * @param distance The distance to move right
     * @param units The unit: {@link DroneSystem.UnitConversion#UNIT_CENTIMETERS} (default), 
     *              {@link DroneSystem.UnitConversion#UNIT_INCHES}, 
     *              {@link DroneSystem.UnitConversion#UNIT_FEET}, 
     *              {@link DroneSystem.UnitConversion#UNIT_METERS}
     * @param speed The speed from 0.5 to 2.0 m/s (default: 0.5)
     */
    public void moveRight(double distance, String units, double speed) {
        flightController.moveRight(distance, units, speed);
    }
    
    /**
     * Move right with default units (cm) and speed (0.5 m/s).
     */
    public void moveRight(double distance) {
        flightController.moveRight(distance);
    }
    
    /**
     * Move right with specified units and default speed (0.5 m/s).
     */
    public void moveRight(double distance, String units) {
        flightController.moveRight(distance, units);
    }
    
    /**
     * Move the drone in 3D space to a specific relative position.
     * 
     * <p>This method provides Python API compatibility for 3D movement, allowing simultaneous
     * movement along multiple axes.</p>
     * 
     * <p><strong>Examples:</strong></p>
     * <pre>
     * drone.moveDistance(1.0, 0.5, 0.25, 1.0); // Move 1m forward, 0.5m left, 0.25m up
     * drone.moveDistance(0, 0, -0.5, 0.8);     // Move 0.5m down at 0.8 m/s
     * </pre>
     * 
     * @param positionX Distance in meters (+ forward, - backward)
     * @param positionY Distance in meters (+ left, - right)
     * @param positionZ Distance in meters (+ up, - down)
     * @param velocity Speed from 0.5 to 2.0 m/s
     */
    public void moveDistance(double positionX, double positionY, double positionZ, double velocity) {
        flightController.moveDistance(positionX, positionY, positionZ, velocity);
    }
    
    /**
     * Send absolute position command to the drone.
     * 
     * <p>This method provides Python API compatibility for absolute positioning relative
     * to the takeoff point.</p>
     * 
     * <p><strong>Examples:</strong></p>
     * <pre>
     * drone.sendAbsolutePosition(1.0, 0, 0.8, 0.5, 0, 0);   // Fly to specific coordinates
     * drone.sendAbsolutePosition(0, 0, 1.0, 1.0, 90, 30);   // Hover 1m up, turn 90°
     * </pre>
     * 
     * @param positionX Absolute X position in meters from takeoff point
     * @param positionY Absolute Y position in meters from takeoff point
     * @param positionZ Absolute Z position in meters from takeoff point
     * @param velocity Speed from 0.5 to 2.0 m/s
     * @param heading Target heading in degrees (-360 to 360)
     * @param rotationalVelocity Rotational speed in degrees/s (10 to 360)
     */
    public void sendAbsolutePosition(double positionX, double positionY, double positionZ, 
                                   double velocity, int heading, int rotationalVelocity) {
        flightController.sendAbsolutePosition(positionX, positionY, positionZ, velocity, heading, rotationalVelocity);
    }

    // ========================================
    // End Distance-Based Movement Methods
    // ========================================
    
    // =============================================================================
    // PHASE 3: Turning Methods (Punch List Item #3)
    // Educational wrapper methods for Python-compatible turning
    // =============================================================================

    /**
     * Turns the drone in the yaw direction (left/right rotation).
     * 
     * <p>This method provides simple yaw control for educational use, matching the
     * Python {@code turn(power, seconds)} function.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * Students learn basic rotational movement concepts and directional control.
     * This method serves as an introduction to yaw control before progressing to
     * more precise degree-based turning methods.</p>
     * 
     * @param power The turning power (-100 to 100). Positive values turn left, 
     *              negative values turn right.
     * @param seconds The duration to turn in seconds. Use null for single command.
     * 
     * @since 1.0
     * @see #turnLeft(int)
     * @see #turnRight(int)
     * @see #turnDegree(int)
     * 
     * @educational
     * <strong>Example Usage:</strong>
     * <pre>{@code
     * // Turn left at 50% power for 2 seconds
     * drone.turn(50, 2.0);
     * 
     * // Quick turn right for 1 second  
     * drone.turn(-30, 1.0);
     * }</pre>
     */
    public void turn(int power, Double seconds) {
        flightController.turn(power, seconds);
    }

    /**
     * Overloaded turn method with default 1 second duration.
     */
    public void turn(int power) {
        turn(power, 1.0);
    }

    /**
     * Turns the drone to a specific degree relative to its initial heading.
     * 
     * <p>This method provides precise angular control using a proportional controller,
     * matching the Python {@code turn_degree(degree, timeout, p_value)} function.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * Students learn precise angular positioning and closed-loop control concepts.
     * This method is essential for autonomous navigation and flight patterns.</p>
     * 
     * @param degree The target heading in degrees (-180 to 180). Positive values
     *               are relative left turns, negative values are relative right turns.
     * @param timeout Maximum time in seconds to attempt the turn (default: 3.0)
     * @param pValue Proportional gain for the control system (default: 10.0)
     * 
     * @since 1.0
     * @see #turnLeft(int)
     * @see #turnRight(int)
     * @see #turn(int, Double)
     * 
     * @educational
     * <strong>Example Usage:</strong>
     * <pre>{@code
     * // Turn exactly 90 degrees left
     * drone.turnDegree(90);
     * 
     * // Turn 45 degrees right with custom timeout
     * drone.turnDegree(-45, 5.0);
     * 
     * // Precise turn with custom control parameters
     * drone.turnDegree(180, 4.0, 15.0);
     * }</pre>
     */
    public void turnDegree(int degree, double timeout, double pValue) {
        flightController.turnDegree(degree, timeout, pValue);
    }

    /**
     * Overloaded turnDegree with default timeout.
     */
    public void turnDegree(int degree, double timeout) {
        flightController.turnDegree(degree, timeout);
    }

    /**
     * Overloaded turnDegree with default timeout and pValue.
     */
    public void turnDegree(int degree) {
        flightController.turnDegree(degree);
    }

    /**
     * Turns the drone left by the specified number of degrees.
     * 
     * <p>This method provides intuitive left turning for educational use,
     * matching the Python {@code turn_left(degree, timeout)} function.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * Students learn directional control with clear semantic meaning. This method
     * is perfect for building navigation patterns and obstacle avoidance behaviors.</p>
     * 
     * @param degrees The number of degrees to turn left (0 to 179)
     * @param timeout Maximum time in seconds to attempt the turn (default: 3.0)
     * 
     * @since 1.0
     * @see #turnRight(int, double)
     * @see #turnDegree(int)
     * @see #turn(int, Double)
     * 
     * @educational
     * <strong>Example Usage:</strong>
     * <pre>{@code
     * // Turn left 90 degrees (quarter turn)
     * drone.turnLeft(90);
     * 
     * // Turn left 45 degrees with longer timeout
     * drone.turnLeft(45, 5.0);
     * 
     * // Small adjustment turn
     * drone.turnLeft(15);
     * }</pre>
     */
    public void turnLeft(int degrees, double timeout) {
        flightController.turnLeft(degrees, timeout);
    }

    /**
     * Overloaded turnLeft with default timeout.
     */
    public void turnLeft(int degrees) {
        flightController.turnLeft(degrees);
    }

    /**
     * Turns the drone right by the specified number of degrees.
     * 
     * <p>This method provides intuitive right turning for educational use,
     * matching the Python {@code turn_right(degree, timeout)} function.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * Students learn directional control with clear semantic meaning. This method
     * is perfect for building navigation patterns and obstacle avoidance behaviors.</p>
     * 
     * @param degrees The number of degrees to turn right (0 to 179)
     * @param timeout Maximum time in seconds to attempt the turn (default: 3.0)
     * 
     * @since 1.0
     * @see #turnLeft(int, double)
     * @see #turnDegree(int)
     * @see #turn(int, Double)
     * 
     * @educational
     * <strong>Example Usage:</strong>
     * <pre>{@code
     * // Turn right 90 degrees (quarter turn)
     * drone.turnRight(90);
     * 
     * // Turn right 45 degrees with longer timeout
     * drone.turnRight(45, 5.0);
     * 
     * // Small adjustment turn
     * drone.turnRight(15);
     * }</pre>
     */
    public void turnRight(int degrees, double timeout) {
        flightController.turnRight(degrees, timeout);
    }

    /**
     * Overloaded turnRight with default timeout.
     */
    public void turnRight(int degrees) {
        flightController.turnRight(degrees);
    }

    // =================================================================================
    // SENSOR DATA ACCESS METHODS
    // =================================================================================

    /**
     * Gets the current battery level as a percentage.
     * 
     * <p>Returns the drone's battery level from 0-100%. This is essential for
     * monitoring flight safety and determining when the drone needs to land.</p>
     * 
     * <h3>🔋 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Check battery before takeoff</li>
     *   <li><strong>L0108 While Loops:</strong> Monitor battery during flight</li>
     *   <li><strong>Safety Programming:</strong> Automatic landing when low</li>
     * </ul>
     * 
     * @return battery level as percentage (0-100)
     * @apiNote Equivalent to Python's {@code drone.get_battery()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Check battery before takeoff
     * int battery = drone.getBattery();
     * if (battery > 50) {
     *     drone.takeOff();
     *     System.out.println("Battery sufficient: " + battery + "%");
     * } else {
     *     System.out.println("Battery too low: " + battery + "%");
     * }
     * }</pre>
     */
    public int getBattery() {
        return flightController.getBattery();
    }

    /**
     * Gets the current flight state as a readable string.
     * 
     * <p>Returns the drone's current flight mode such as "READY", "FLIGHT", 
     * "TAKE_OFF", "LANDING", etc. Useful for understanding drone status and
     * creating conditional logic based on flight state.</p>
     * 
     * <h3>✈️ Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Check if drone is ready to fly</li>
     *   <li><strong>L0107 Loops:</strong> Wait for takeoff completion</li>
     *   <li><strong>State Management:</strong> Track flight progression</li>
     * </ul>
     * 
     * @return flight state string (e.g., "READY", "FLIGHT", "TAKE_OFF", "LANDING")
     * @apiNote Equivalent to Python's {@code drone.get_flight_state()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Wait for takeoff to complete
     * drone.takeOff();
     * while (!drone.getFlightState().equals("FLIGHT")) {
     *     System.out.println("Current state: " + drone.getFlightState());
     *     drone.sleep(100);
     * }
     * System.out.println("Takeoff complete!");
     * }</pre>
     */
    public String getFlightState() {
        return flightController.getFlightState();
    }

    /**
     * Gets the current movement state as a readable string.
     * 
     * <p>Returns the drone's movement status such as "READY", "HOVERING", 
     * "MOVING", "RETURN_HOME". This helps understand what the drone is
     * currently doing in terms of movement.</p>
     * 
     * @return movement state string (e.g., "READY", "HOVERING", "MOVING", "RETURN_HOME")
     * @apiNote Equivalent to Python's {@code drone.get_movement_state()}
     * @since 1.0
     */
    public String getMovementState() {
        return flightController.getMovementState();
    }

    /**
     * Gets the current error state data from the drone.
     * 
     * <p>Returns error information about both sensor and state problems.
     * This method requests fresh error data from the drone and returns
     * it as an array containing timestamp, sensor error flags, and state error flags.</p>
     * 
     * <p><strong>Error Flags:</strong></p>
     * <ul>
     *   <li><strong>Sensor Errors:</strong> {@link DroneSystem.ErrorFlagsForSensor}
     *     <ul>
     *       <li>MOTION_CALIBRATING: Gyroscope/accelerometer calibrating</li>
     *       <li>MOTION_NO_ANSWER: Motion sensor unresponsive</li>
     *       <li>MOTION_WRONG_VALUE: Motion sensor giving incorrect data</li>
     *       <li>MOTION_NOT_CALIBRATED: Motion sensor not calibrated</li>
     *       <li>PRESSURE_NO_ANSWER: Barometer unresponsive</li>
     *       <li>PRESSURE_WRONG_VALUE: Barometer giving incorrect data</li>
     *       <li>RANGE_GROUND_NO_ANSWER: Bottom range sensor unresponsive</li>
     *       <li>RANGE_GROUND_WRONG_VALUE: Bottom range sensor incorrect data</li>
     *       <li>FLOW_NO_ANSWER: Optical flow sensor unresponsive</li>
     *       <li>FLOW_WRONG_VALUE: Optical flow sensor incorrect data</li>
     *       <li>FLOW_CANNOT_RECOGNIZE_GROUND_IMAGE: Cannot recognize ground pattern</li>
     *     </ul>
     *   </li>
     *   <li><strong>State Errors:</strong> {@link DroneSystem.ErrorFlagsForState}
     *     <ul>
     *       <li>NOT_REGISTERED: Device not registered</li>
     *       <li>FLASH_READ_LOCK_UNLOCKED: Flash memory read lock not engaged</li>
     *       <li>BOOTLOADER_WRITE_LOCK_UNLOCKED: Bootloader write lock not engaged</li>
     *       <li>LOW_BATTERY: Battery level is low</li>
     *       <li>TAKEOFF_FAILURE_CHECK_PROPELLER_AND_MOTOR: Takeoff failed</li>
     *       <li>CHECK_PROPELLER_VIBRATION: Propeller vibration detected</li>
     *       <li>ATTITUDE_NOT_STABLE: Drone attitude too tilted or inverted</li>
     *       <li>CANNOT_FLIP_LOW_BATTERY: Battery below 50% for flip</li>
     *       <li>CANNOT_FLIP_TOO_HEAVY: Drone too heavy for flip</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * <h3>🔍 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Check for specific error conditions</li>
     *   <li><strong>L0108 Loops:</strong> Monitor errors during flight</li>
     *   <li><strong>Debugging:</strong> Diagnose hardware or sensor issues</li>
     *   <li><strong>Safety Programming:</strong> Respond to error states</li>
     * </ul>
     * 
     * @return Array containing [timestamp, sensorErrorFlags, stateErrorFlags], 
     *         or null if error data is unavailable
     * @apiNote Equivalent to Python's {@code drone.get_error_data()}
     * @since 2.5
     * 
     * @example
     * <pre>{@code
     * // L0106: Check for low battery error
     * double[] errorData = drone.getErrorData();
     * if (errorData != null) {
     *     int stateErrors = (int) errorData[2];
     *     if ((stateErrors & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0) {
     *         System.out.println("Warning: Low battery detected!");
     *         drone.land();
     *     }
     * }
     * }</pre>
     * 
     * @example
     * <pre>{@code
     * // L0108: Monitor calibration status
     * drone.resetGyro();
     * while (true) {
     *     double[] errorData = drone.getErrorData();
     *     if (errorData != null) {
     *         int sensorErrors = (int) errorData[1];
     *         if ((sensorErrors & DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING.getValue()) == 0) {
     *             System.out.println("Calibration complete!");
     *             break;
     *         }
     *     }
     *     Thread.sleep(100);
     * }
     * }</pre>
     */
    public double[] getErrorData() {
        return getErrorData(0.2);
    }

    /**
     * Gets the current error state data from the drone with custom delay.
     * 
     * <p>Allows specifying a custom delay for error data request response.
     * Use shorter delays when checking frequently, longer delays when
     * connection is slow.</p>
     * 
     * @param delay Delay in seconds to wait for error data response (default 0.2)
     * @return Array containing [timestamp, sensorErrorFlags, stateErrorFlags], 
     *         or null if error data is unavailable
     * @apiNote Equivalent to Python's {@code drone.get_error_data(delay)}
     * @since 2.5
     * 
     * @see #getErrorData()
     */
    public double[] getErrorData(double delay) {
        log.debug("Requesting error data");
        
        try {
            // Request fresh error data from drone
            sendRequest(DataType.Error);
            
            // Wait for response
            Thread.sleep((long) (delay * 1000));
            
            // Get error data from link manager
            var error = linkManager.getError();
            if (error == null) {
                log.debug("No error data available");
                return null;
            }
            
            // Convert to Python-compatible format: [timestamp, sensorFlags, stateFlags]
            double timestamp = error.getSystemTime() / 1000.0; // Convert ms to seconds
            double[] result = {
                timestamp,
                (double) error.getErrorFlagsForSensor(),
                (double) error.getErrorFlagsForState()
            };
            
            log.debug("Retrieved error data - timestamp: {}, sensor flags: 0x{}, state flags: 0x{}",
                     timestamp, 
                     Integer.toHexString(error.getErrorFlagsForSensor()),
                     Integer.toHexString(error.getErrorFlagsForState()));
            
            return result;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error data request interrupted", e);
        } catch (Exception e) {
            log.error("Failed to get error data", e);
            return null;
        }
    }

    /**
     * Gets error state information as a type-safe immutable object (recommended).
     * 
     * <p><strong>This is the recommended Java-idiomatic way to check for errors.</strong>
     * Use this instead of {@link #getErrorData()} for cleaner, more maintainable code.</p>
     * 
     * <p>Returns an {@link ErrorData} object providing:
     * <ul>
     *   <li><strong>Type-safe error checking:</strong> No bitwise operations needed</li>
     *   <li><strong>IDE auto-completion:</strong> All error types visible in IDE</li>
     *   <li><strong>Named methods:</strong> No array indexing required</li>
     *   <li><strong>Compile-time safety:</strong> Can't mix up sensor vs state errors</li>
     * </ul>
     * </p>
     * 
     * <h3>🔍 Comparison with Array-Based API:</h3>
     * <pre>{@code
     * // Array-based approach (Python-compatible)
     * double[] errorArray = drone.getErrorData();
     * if (errorArray != null) {
     *     int stateErrors = (int) errorArray[2];
     *     if ((stateErrors & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0) {
     *         drone.land();
     *     }
     * }
     * 
     * // Object-based approach (Java-idiomatic, recommended)
     * ErrorData errorData = drone.getErrors();
     * if (errorData != null && errorData.isLowBattery()) {
     *     drone.land();
     * }
     * }</pre>
     * 
     * <h3>📖 Usage Examples:</h3>
     * <pre>{@code
     * // Basic error checking
     * ErrorData errors = drone.getErrors();
     * if (errors != null) {
     *     if (errors.hasCriticalErrors()) {
     *         System.out.println("Critical error - landing!");
     *         drone.land();
     *     }
     *     
     *     if (errors.isCalibrating()) {
     *         System.out.println("Please wait - calibrating...");
     *     }
     * }
     * 
     * // Check specific sensor errors
     * if (errors.hasSensorError(DroneSystem.ErrorFlagsForSensor.FLOW_NO_ANSWER)) {
     *     System.out.println("Warning: Optical flow sensor not responding");
     * }
     * 
     * // Check specific state errors
     * if (errors.hasStateError(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE)) {
     *     System.out.println("Attitude unstable - avoiding maneuvers");
     * }
     * 
     * // Get all active errors
     * Set<DroneSystem.ErrorFlagsForSensor> sensorErrors = errors.getSensorErrors();
     * Set<DroneSystem.ErrorFlagsForState> stateErrors = errors.getStateErrors();
     * 
     * // Display all errors
     * System.out.println(errors.toDetailedString());
     * }</pre>
     * 
     * <h3>🎓 Educational Context:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Safe error handling patterns</li>
     *   <li><strong>L0109 Methods:</strong> Clean data encapsulation</li>
     *   <li><strong>L0111 Object-Oriented:</strong> Immutable data objects</li>
     *   <li><strong>Safety Programming:</strong> Proactive error monitoring</li>
     * </ul>
     * 
     * <h3>⚠️ Important Notes:</h3>
     * <ul>
     *   <li>Uses default 0.2 second delay for sensor data collection</li>
     *   <li>Returns null if no error data is available yet</li>
     *   <li>Always check for null before accessing the returned object</li>
     *   <li>For custom delays, use the overload with delay parameter</li>
     * </ul>
     * 
     * @return ErrorData object containing type-safe error information, or null if unavailable
     * @see ErrorData
     * @see #getErrorData()
     * @see DroneSystem.ErrorFlagsForSensor
     * @see DroneSystem.ErrorFlagsForState
     */
    public ErrorData getErrors() {
        double[] errorArray = getErrorData();
        return ErrorData.fromArray(errorArray);
    }

    /**
     * Gets error state information as a type-safe immutable object with custom delay (recommended).
     * 
     * <p>Same as {@link #getErrors()} but allows specifying a custom delay
     * for sensor data collection.</p>
     * 
     * <h3>📖 Usage Example:</h3>
     * <pre>{@code
     * // Use longer delay for more accurate data
     * ErrorData errors = drone.getErrors(0.5);
     * if (errors != null && errors.hasAnyErrors()) {
     *     System.out.println(errors.toDetailedString());
     * }
     * }</pre>
     * 
     * @param delay Time in seconds to wait for sensor data collection (typically 0.1 to 0.5)
     * @return ErrorData object containing type-safe error information, or null if unavailable
     * @see ErrorData
     * @see #getErrors()
     * @see #getErrorData(double)
     */
    public ErrorData getErrors(double delay) {
        double[] errorArray = getErrorData(delay);
        return ErrorData.fromArray(errorArray);
    }

    /**
     * Gets the current height from the ground in centimeters.
     * 
     * <p>Measures the distance from the drone to the ground using the bottom
     * range sensor. Essential for altitude control and safe landing procedures.</p>
     * 
     * <h3>📏 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Maintain safe flying height</li>
     *   <li><strong>L0108 While Loops:</strong> Altitude-based navigation</li>
     *   <li><strong>Safety Programming:</strong> Prevent ground collisions</li>
     * </ul>
     * 
     * @return height in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_height()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Maintain safe flying height
     * double height = drone.getHeight();
     * if (height < 30) {
     *     drone.go("up", 50, 1);
     *     System.out.println("Climbing to safe height");
     * } else if (height > 200) {
     *     drone.go("down", 50, 1);
     *     System.out.println("Descending to safe height");
     * }
     * }</pre>
     */
    public double getHeight() {
        return flightController.getHeight();
    }

    /**
     * Gets the current height from the ground in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return height in the specified unit
     * @since 1.0
     */
    public double getHeight(String unit) {
        return flightController.getHeight(unit);
    }

    /**
     * Gets the distance measured by the front range sensor in centimeters.
     * 
     * <p>Measures the distance from the drone's front to the nearest obstacle.
     * Critical for obstacle avoidance and navigation in confined spaces.</p>
     * 
     * <h3>🚧 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Detect obstacles ahead</li>
     *   <li><strong>L0107 Loops:</strong> Navigate around obstacles</li>
     *   <li><strong>Autonomous Flight:</strong> Wall following behavior</li>
     * </ul>
     * 
     * @return distance in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_front_range()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Simple obstacle avoidance
     * double frontDistance = drone.getFrontRange();
     * if (frontDistance < 50) {
     *     System.out.println("Obstacle detected! Distance: " + frontDistance + "cm");
     *     drone.turnLeft(90);
     * } else {
     *     drone.go("forward", 50, 1);
     * }
     * }</pre>
     */
    public double getFrontRange() {
        return flightController.getFrontRange();
    }

    /**
     * Gets the distance measured by the front range sensor in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return distance in the specified unit
     * @since 1.0
     */
    public double getFrontRange(String unit) {
        return flightController.getFrontRange(unit);
    }

    /**
     * Gets the distance measured by the back/rear range sensor in centimeters.
     * 
     * <p>Returns the distance to objects behind the drone. Useful for
     * backing up safely and creating comprehensive obstacle avoidance behaviors.</p>
     * 
     * <h3>🛡️ Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Check rear clearance before reversing</li>
     *   <li><strong>L0108 While Loops:</strong> Back away from obstacles</li>
     *   <li><strong>Safety Programming:</strong> 360-degree awareness</li>
     * </ul>
     * 
     * @return distance in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_back_range()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Safe backing maneuver
     * if (drone.getBackRange() > 100) {
     *     drone.go("backward", 30, 2);
     * } else {
     *     System.out.println("Can't back up - obstacle detected!");
     * }
     * }</pre>
     */
    public double getBackRange() {
        return flightController.getBackRange();
    }

    /**
     * Gets the distance measured by the back/rear range sensor in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return distance in the specified unit
     * @since 1.0
     */
    public double getBackRange(String unit) {
        return flightController.getBackRange(unit);
    }

    /**
     * Gets the distance measured by the top range sensor in centimeters.
     * 
     * <p>Returns the distance to objects above the drone. Useful for
     * altitude management and avoiding ceiling collisions.</p>
     * 
     * <h3>⬆️ Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Check ceiling clearance</li>
     *   <li><strong>3D Navigation:</strong> Vertical obstacle avoidance</li>
     *   <li><strong>Indoor Flying:</strong> Ceiling-aware behaviors</li>
     * </ul>
     * 
     * @return distance in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_top_range()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Safe altitude gain
     * if (drone.getTopRange() > 50) {
     *     drone.go("up", 20, 1);
     * } else {
     *     System.out.println("Ceiling too close!");
     * }
     * }</pre>
     */
    public double getTopRange() {
        return flightController.getTopRange();
    }

    /**
     * Gets the distance measured by the top range sensor in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return distance in the specified unit
     * @since 1.0
     */
    public double getTopRange(String unit) {
        return flightController.getTopRange(unit);
    }

    /**
     * Gets the distance measured by the bottom range sensor in centimeters.
     * 
     * <p>Returns the distance to the ground or objects below the drone.
     * This is the same as {@link #getHeight()} but provides Python API compatibility.</p>
     * 
     * <h3>⬇️ Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Maintain safe altitude</li>
     *   <li><strong>Landing Logic:</strong> Controlled descent</li>
     *   <li><strong>Terrain Following:</strong> Maintain ground clearance</li>
     * </ul>
     * 
     * @return distance in centimeters  
     * @apiNote Equivalent to Python's {@code drone.get_bottom_range()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Altitude monitoring
     * if (drone.getBottomRange() < 20) {
     *     System.out.println("Too close to ground!");
     *     drone.go("up", 30, 1);
     * }
     * }</pre>
     */
    public double getBottomRange() {
        return flightController.getBottomRange();
    }

    /**
     * Gets the distance measured by the bottom range sensor in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return distance in the specified unit
     * @since 1.0
     */
    public double getBottomRange(String unit) {
        return flightController.getBottomRange(unit);
    }

    /**
     * Gets the distance measured by the left range sensor in centimeters.
     * 
     * <p>Returns the distance to objects on the left side of the drone.
     * Useful for side obstacle avoidance and corridor navigation.</p>
     * 
     * <h3>⬅️ Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Side obstacle detection</li>
     *   <li><strong>Navigation:</strong> Corridor following</li>
     *   <li><strong>Complex Patterns:</strong> Wall-following behaviors</li>
     * </ul>
     * 
     * @return distance in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_left_range()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Corridor navigation
     * if (drone.getLeftRange() < 30) {
     *     drone.go("right", 20, 1);  // Move away from left wall
     * }
     * }</pre>
     */
    public double getLeftRange() {
        return flightController.getLeftRange();
    }

    /**
     * Gets the distance measured by the left range sensor in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return distance in the specified unit
     * @since 1.0
     */
    public double getLeftRange(String unit) {
        return flightController.getLeftRange(unit);
    }

    /**
     * Gets the distance measured by the right range sensor in centimeters.
     * 
     * <p>Returns the distance to objects on the right side of the drone.
     * Useful for side obstacle avoidance and corridor navigation.</p>
     * 
     * <h3>➡️ Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Side obstacle detection</li>
     *   <li><strong>Navigation:</strong> Corridor following</li>
     *   <li><strong>Complex Patterns:</strong> Wall-following behaviors</li>
     * </ul>
     * 
     * @return distance in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_right_range()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0106: Corridor navigation
     * if (drone.getRightRange() < 30) {
     *     drone.go("left", 20, 1);  // Move away from right wall
     * }
     * }</pre>
     */
    public double getRightRange() {
        return flightController.getRightRange();
    }

    /**
     * Gets the distance measured by the right range sensor in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return distance in the specified unit
     * @since 1.0
     */
    public double getRightRange(String unit) {
        return flightController.getRightRange(unit);
    }

    /**
     * Detects if there is a wall or obstacle within a specified distance in front of the drone.
     *
     * <p>This method is designed for educational use, providing a simple way to check for obstacles ahead using the front range sensor.</p>
     *
     * <h3>🧑‍🏫 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> If/else obstacle detection</li>
     *   <li><strong>L0107 Loops:</strong> Repeat until clear path</li>
     *   <li><strong>Autonomous Flight:</strong> Wall avoidance and navigation</li>
     * </ul>
     *
     * @param distance the distance threshold in centimeters
     * @return true if an obstacle is detected within the specified distance, false otherwise
     * @apiNote Equivalent to Python's {@code drone.detect_wall(distance)}
     * @since 1.1
     *
     * @example
     * <pre>{@code
     * if (drone.detectWall(30)) {
     *     System.out.println("Wall detected ahead!");
     *     drone.stop();
     * }
     * }</pre>
     */
    public boolean detectWall(int distance) {
        return getFrontRange() < distance;
    }

    /**
     * Gets the X position relative to the takeoff point in centimeters.
     * 
     * <p>Returns the drone's current X coordinate relative to where it took off.
     * Positive X is typically to the right, negative X is to the left.</p>
     * 
     * <h3>🗺️ Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0105 Variables:</strong> Track position for return journeys</li>
     *   <li><strong>L0106 Conditionals:</strong> Create boundary limits</li>
     *   <li><strong>L0110 Functions:</strong> Calculate distance from home</li>
     * </ul>
     * 
     * @return X position in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_pos_x()}
     * @since 1.0
     * 
     * @example
     * <pre>{@code
     * // L0105: Track position for safe return
     * double startX = drone.getPosX();
     * drone.go("right", 50, 3); // Move right for 3 seconds
     * double currentX = drone.getPosX();
     * System.out.println("Moved " + (currentX - startX) + "cm to the right");
     * }</pre>
     */
    public double getPosX() {
        return flightController.getPosX();
    }

    /**
     * Gets the X position relative to the takeoff point in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return X position in the specified unit
     * @since 1.0
     */
    public double getPosX(String unit) {
        return flightController.getPosX(unit);
    }

    /**
     * Gets the Y position relative to the takeoff point in centimeters.
     * 
     * <p>Returns the drone's current Y coordinate relative to where it took off.
     * Positive Y is typically forward, negative Y is backward.</p>
     * 
     * @return Y position in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_pos_y()}
     * @since 1.0
     */
    public double getPosY() {
        return flightController.getPosY();
    }

    /**
     * Gets the Y position relative to the takeoff point in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return Y position in the specified unit
     * @since 1.0
     */
    public double getPosY(String unit) {
        return flightController.getPosY(unit);
    }

    /**
     * Gets the Z position relative to the takeoff point in centimeters.
     * 
     * <p>Returns the drone's current Z coordinate relative to where it took off.
     * Positive Z is typically up, negative Z is down.</p>
     * 
     * @return Z position in centimeters
     * @apiNote Equivalent to Python's {@code drone.get_pos_z()}
     * @since 1.0
     */
    public double getPosZ() {
        return flightController.getPosZ();
    }

    /**
     * Gets the Z position relative to the takeoff point in the specified unit.
     * 
     * @param unit measurement unit ("cm", "mm", "m", or "in")
     * @return Z position in the specified unit
     * @since 1.0
     */
    public double getPosZ(String unit) {
        return flightController.getPosZ(unit);
    }

    /**
     * Gets the X-axis acceleration in G-force units.
     * 
     * <p>Measures the acceleration along the X-axis (left/right).
     * Useful for understanding drone movement and creating responsive behaviors.</p>
     * 
     * <h3>🔬 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Detect sudden movements</li>
     *   <li><strong>Physics Learning:</strong> Understand acceleration concepts</li>
     *   <li><strong>Advanced Control:</strong> Create smooth flight patterns</li>
     * </ul>
     * 
     * @return X acceleration in G-force
     * @apiNote Equivalent to Python's {@code drone.get_accel_x()}
     * @since 1.0
     */
    public double getAccelX() {
        return flightController.getAccelX();
    }

    /**
     * Gets the Y-axis acceleration in G-force units.
     * 
     * <p>Measures the acceleration along the Y-axis (forward/backward).</p>
     * 
     * @return Y acceleration in G-force
     * @apiNote Equivalent to Python's {@code drone.get_accel_y()}
     * @since 1.0
     */
    public double getAccelY() {
        return flightController.getAccelY();
    }

    /**
     * Gets the Z-axis acceleration in G-force units.
     * 
     * <p>Measures the acceleration along the Z-axis (up/down).</p>
     * 
     * @return Z acceleration in G-force
     * @apiNote Equivalent to Python's {@code drone.get_accel_z()}
     * @since 1.0
     */
    public double getAccelZ() {
        return flightController.getAccelZ();
    }

    /**
     * Gets the X-axis angle (roll) in degrees.
     * 
     * <p>Measures the drone's roll angle. Positive values indicate rolling right,
     * negative values indicate rolling left.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Detect if drone is level</li>
     *   <li><strong>Physics Learning:</strong> Understand orientation concepts</li>
     *   <li><strong>Stabilization:</strong> Create auto-leveling behaviors</li>
     * </ul>
     * 
     * @return X angle (roll) in degrees
     * @apiNote Equivalent to Python's {@code drone.get_angle_x()}
     * @since 1.0
     */
    public double getAngleX() {
        return flightController.getAngleX();
    }

    /**
     * Gets the Y-axis angle (pitch) in degrees.
     * 
     * <p>Measures the drone's pitch angle. Positive values indicate nose up,
     * negative values indicate nose down.</p>
     * 
     * @return Y angle (pitch) in degrees
     * @apiNote Equivalent to Python's {@code drone.get_angle_y()}
     * @since 1.0
     */
    public double getAngleY() {
        return flightController.getAngleY();
    }

    /**
     * Gets the Z-axis angle (yaw) in degrees.
     * 
     * <p>Measures the drone's yaw angle (heading). This is the direction the
     * drone is facing relative to its takeoff orientation.</p>
     * 
     * @return Z angle (yaw) in degrees
     * @apiNote Equivalent to Python's {@code drone.get_angle_z()}
     * @since 1.0
     */
    public double getAngleZ() {
        return flightController.getAngleZ();
    }

    // Array-based sensor methods for Python compatibility
    
    /**
     * Gets accelerometer data as an array [x, y, z].
     * 
     * <p>Returns acceleration values for all three axes in G-force units.
     * This method provides Python API compatibility for accessing accelerometer
     * data in array format.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0202 Arrays:</strong> Practice array data handling</li>
     *   <li><strong>Physics Learning:</strong> Understand 3D acceleration vectors</li>
     *   <li><strong>Data Analysis:</strong> Process multiple sensor values together</li>
     * </ul>
     * 
     * @return Array containing [x, y, z] acceleration values in G-force
     * @apiNote Equivalent to Python's {@code drone.get_accel()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets acceleration data as an array [x, y, z].
     *
     * @return Array containing [x, y, z] acceleration values in G-force
     * @apiNote Equivalent to Python's {@code drone.get_accel()}
     * @since 1.0
     * @educational
     */
    public int[] getAccel() {
        return flightController.get_accel();
    }

    /**
     * Gets gyroscope data as an array [x, y, z].
     * 
     * <p>Returns angular velocity values for all three axes in degrees per second.
     * This method provides Python API compatibility for accessing gyroscope
     * data in array format.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0202 Arrays:</strong> Practice array data handling</li>
     *   <li><strong>Physics Learning:</strong> Understand rotational motion</li>
     *   <li><strong>Motion Detection:</strong> Detect rapid movements or vibrations</li>
     * </ul>
     * 
     * @return Array containing [x, y, z] angular velocity values in degrees/second
     * @apiNote Equivalent to Python's {@code drone.get_gyro()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets gyroscope data as an array [x, y, z].
     *
     * @return Array containing [x, y, z] angular velocity values in degrees/second
     * @apiNote Equivalent to Python's {@code drone.get_gyro()}
     * @since 1.0
     * @educational
     */
    public int[] getGyro() {
        return flightController.get_gyro();
    }

    /**
     * Gets angle data as an array [x, y, z].
     * 
     * <p>Returns orientation angles for all three axes in degrees.
     * This method provides Python API compatibility for accessing angle
     * data in array format.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0202 Arrays:</strong> Practice array data handling</li>
     *   <li><strong>Coordinate Systems:</strong> Understand 3D orientation</li>
     *   <li><strong>Navigation:</strong> Track drone orientation in 3D space</li>
     * </ul>
     * 
     * @return Array containing [x, y, z] angle values in degrees (roll, pitch, yaw)
     * @apiNote Equivalent to Python's {@code drone.get_angle()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets angle data as an array [x, y, z].
     *
     * @return Array containing [x, y, z] angle values in degrees (roll, pitch, yaw)
     * @apiNote Equivalent to Python's {@code drone.get_angle()}
     * @since 1.0
     * @educational
     */
    public int[] getAngle() {
        return flightController.get_angle();
    }

    /**
     * Gets color sensor data including HSV and color values.
     * 
     * <p>Returns the raw color sensor data from the drone's color detection card.
     * This method provides Python API compatibility for accessing color sensor
     * data including HSV (Hue, Saturation, Value) and detected color information.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0201 2D Arrays:</strong> Practice working with multi-dimensional arrays</li>
     *   <li><strong>Color Theory:</strong> Understand HSV color space and RGB detection</li>
     *   <li><strong>Data Structures:</strong> Work with complex nested data</li>
     *   <li><strong>Computer Vision:</strong> Learn about color detection algorithms</li>
     * </ul>
     * 
     * <h3>📊 Return Format:</h3>
     * <p>Returns a 2D array where:</p>
     * <ul>
     *   <li><strong>hsvl[0]:</strong> HSV values for front sensor [H, S, V, L]</li>
     *   <li><strong>hsvl[1]:</strong> HSV values for back sensor [H, S, V, L]</li>
     * </ul>
     * 
     * @return 2D array containing HSV data for front and back color sensors, or null if no data available
     * @apiNote Equivalent to Python's {@code drone.get_color_data()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets color sensor data including HSV and color values.
     *
     * @return 2D array containing HSV data for front and back color sensors, or null if no data available
     * @apiNote Equivalent to Python's {@code drone.get_color_data()}
     * @since 1.0
     * @educational
     */
    public int[][] getColorData() {
        com.otabi.jcodroneedu.protocol.cardreader.CardColor cardColor = droneStatus.getCardColor();
        if (cardColor == null) {
            return null;
        }
        
        byte[][] hsvl = cardColor.getHsvl();
        if (hsvl == null) {
            return null;
        }
        
        // Convert byte arrays to int arrays for easier educational use
        int[][] result = new int[hsvl.length][];
        for (int i = 0; i < hsvl.length; i++) {
            if (hsvl[i] != null) {
                result[i] = new int[hsvl[i].length];
                for (int j = 0; j < hsvl[i].length; j++) {
                    result[i][j] = hsvl[i][j] & 0xFF; // Convert unsigned byte to int
                }
            }
        }
        
        return result;
    }

    /**
     * Gets detected color values for front and back sensors.
     * 
     * <p>Returns the color detection results from both color sensors.
     * This method provides Python API compatibility for accessing the
     * simplified color detection results.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0202 Arrays:</strong> Practice array data handling</li>
     *   <li><strong>Color Recognition:</strong> Learn about automated color detection</li>
     *   <li><strong>Decision Making:</strong> Use color data for program logic</li>
     *   <li><strong>Sensor Integration:</strong> Combine multiple sensor inputs</li>
     * </ul>
     * 
     * <h3>🎨 Color Values:</h3>
     * <p>Color values correspond to {@link DroneSystem.CardColorIndex}:</p>
     * <ul>
     *   <li><strong>0:</strong> UNKNOWN</li>
     *   <li><strong>1:</strong> WHITE</li>
     *   <li><strong>2:</strong> RED</li>
     *   <li><strong>3:</strong> YELLOW</li>
     *   <li><strong>4:</strong> GREEN</li>
     *   <li><strong>5:</strong> CYAN</li>
     *   <li><strong>6:</strong> BLUE</li>
     *   <li><strong>7:</strong> MAGENTA</li>
     *   <li><strong>8:</strong> BLACK</li>
     * </ul>
     * 
     * @return Array containing [front_color, back_color] values, or null if no data available
     * @apiNote Equivalent to Python's {@code drone.get_colors()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets detected color values for front and back sensors.
     *
     * @return Array containing [front_color, back_color] values, or null if no data available
     * @apiNote Equivalent to Python's {@code drone.get_colors()}
     * @since 1.0
     * @educational
     */
    public int[] getColors() {
        com.otabi.jcodroneedu.protocol.cardreader.CardColor cardColor = droneStatus.getCardColor();
        if (cardColor == null) {
            return null;
        }

        // If classifier is loaded and HSVL data is available, use ML prediction
        if (colorClassifierLoaded && colorClassifier != null) {
            byte[][] hsvl = cardColor.getHsvl();
            if (hsvl != null && hsvl.length >= 2 && hsvl[0] != null && hsvl[1] != null) {
                int[] result = new int[2];
                for (int i = 0; i < 2; i++) {
                    double[] sample = new double[hsvl[i].length];
                    for (int j = 0; j < hsvl[i].length; j++) {
                        sample[j] = hsvl[i][j] & 0xFF;
                    }
                    String label = colorClassifier.predictColor(sample);
                    // Map label to index (1=WHITE, 2=RED, ... 8=BLACK, 0=UNKNOWN)
                    // You may want to adjust this mapping to match your label set
                    result[i] = mapLabelToIndex(label);
                }
                return result;
            }
        }

        // Fallback: use hardware color values
        byte[] colorData = cardColor.getColor();
        if (colorData == null || colorData.length < 2) {
            return null;
        }
        return new int[]{
            colorData[0] & 0xFF, // Front color sensor
            colorData[1] & 0xFF  // Back color sensor
        };
    }

    // Helper: Map classifier label to color index (adjust as needed)
    private int mapLabelToIndex(String label) {
        switch (label.toLowerCase()) {
            case "white": return 1;
            case "red": return 2;
            case "yellow": return 3;
            case "green": return 4;
            case "cyan": 
            case "light_blue":
            case "lightblue": return 5;
            case "blue": return 6;
            case "magenta":
            case "purple": return 7;
            case "black": return 8;
            default: return 0; // UNKNOWN
        }
    }

    /**
     * Gets the detected color from the front color sensor.
     * 
     * <p>Returns the color detection result from the front-facing color sensor.
     * This method provides Python API compatibility for accessing individual
     * sensor readings.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0103 Variables:</strong> Store and use single sensor values</li>
     *   <li><strong>Conditional Logic:</strong> Make decisions based on color detection</li>
     *   <li><strong>Pattern Recognition:</strong> Identify specific colors in environment</li>
     *   <li><strong>Autonomous Navigation:</strong> Use color cues for direction</li>
     * </ul>
     * 
     * <h3>🎨 Color Values:</h3>
     * <p>Color values correspond to {@link DroneSystem.CardColorIndex}:</p>
     * <ul>
     *   <li><strong>0:</strong> UNKNOWN</li>
     *   <li><strong>1:</strong> WHITE</li>
     *   <li><strong>2:</strong> RED</li>
     *   <li><strong>3:</strong> YELLOW</li>
     *   <li><strong>4:</strong> GREEN</li>
     *   <li><strong>5:</strong> CYAN</li>
     *   <li><strong>6:</strong> BLUE</li>
     *   <li><strong>7:</strong> MAGENTA</li>
     *   <li><strong>8:</strong> BLACK</li>
     * </ul>
     * 
     * @return Color value from front sensor (0-8), or -1 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_front_color()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets the detected color from the front color sensor.
     *
     * @return Color value from front sensor (0-8), or -1 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_front_color()}
     * @since 1.0
     * @educational
     */
    public int getFrontColor() {
        int[] colors = getColors();
        return (colors != null && colors.length > 0) ? colors[0] : -1;
    }

    /**
     * Gets the detected color from the back color sensor.
     * 
     * <p>Returns the color detection result from the back-facing color sensor.
     * This method provides Python API compatibility for accessing individual
     * sensor readings.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0103 Variables:</strong> Store and use single sensor values</li>
     *   <li><strong>Conditional Logic:</strong> Make decisions based on color detection</li>
     *   <li><strong>Pattern Recognition:</strong> Identify specific colors in environment</li>
     *   <li><strong>Autonomous Navigation:</strong> Use color cues for direction</li>
     * </ul>
     * 
     * <h3>🎨 Color Values:</h3>
     * <p>Color values correspond to {@link DroneSystem.CardColorIndex}:</p>
     * <ul>
     *   <li><strong>0:</strong> UNKNOWN</li>
     *   <li><strong>1:</strong> WHITE</li>
     *   <li><strong>2:</strong> RED</li>
     *   <li><strong>3:</strong> YELLOW</li>
     *   <li><strong>4:</strong> GREEN</li>
     *   <li><strong>5:</strong> CYAN</li>
     *   <li><strong>6:</strong> BLUE</li>
     *   <li><strong>7:</strong> MAGENTA</li>
     *   <li><strong>8:</strong> BLACK</li>
     * </ul>
     * 
     * @return Color value from back sensor (0-8), or -1 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_back_color()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets the detected color from the back color sensor.
     *
     * @return Color value from back sensor (0-8), or -1 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_back_color()}
     * @since 1.0
     * @educational
     */
    public int getBackColor() {
        int[] colors = getColors();
        return (colors != null && colors.length > 1) ? colors[1] : -1;
    }

    // =============================================================================
    // PHASE 3.5: Advanced Sensor Data Access (Punch List Items #10, #17)
    // Position, pressure, temperature, and comprehensive sensor methods
    // =============================================================================

    /**
     * Gets position data as an array [x, y, z] in millimeters.
     * 
     * <p>Returns the drone's position relative to the takeoff point.
     * This method provides Python API compatibility for accessing position
     * data in array format for educational navigation projects.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0202 Arrays:</strong> Practice array data handling</li>
     *   <li><strong>3D Coordinates:</strong> Understand 3D navigation and positioning</li>
     *   <li><strong>Data Analysis:</strong> Track movement patterns and distances</li>
     *   <li><strong>Navigation Projects:</strong> Implement autonomous navigation</li>
     * </ul>
     * 
     * <h3>📊 Coordinate System:</h3>
     * <ul>
     *   <li><strong>X-axis:</strong> Forward/backward (positive = forward)</li>
     *   <li><strong>Y-axis:</strong> Left/right (positive = left)</li>
     *   <li><strong>Z-axis:</strong> Up/down (positive = up)</li>
     * </ul>
     * 
     * @return Array containing [x, y, z] position values in millimeters, or null if no data
     * @apiNote Equivalent to Python's {@code drone.get_position_data()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets position data as an array [x, y, z] in millimeters.
     *
     * @return Array containing [x, y, z] position in mm
     * @apiNote Equivalent to Python's {@code drone.get_position_data()}
     * @since 1.0
     * @educational
     */
    public float[] getPositionData() {
        var position = droneStatus.getPosition();
        if (position == null) {
            return null;
        }
        return new float[]{position.getX(), position.getY(), position.getZ()};
    }

    /**
     * Gets the X position (forward/backward) in meters.
     * 
     * <p>Returns the drone's forward/backward position relative to takeoff point.
     * Positive values indicate forward movement, negative values indicate backward.</p>
     * 
     * @return X position in meters, or 0 if no data available
     * @educational
     */
    public float getPositionX() {
        var position = droneStatus.getPosition();
        return (position != null) ? position.getX() : 0;
    }

    /**
     * Gets the Y position (left/right) in meters.
     * 
     * <p>Returns the drone's left/right position relative to takeoff point.
     * Positive values indicate leftward movement, negative values indicate rightward.</p>
     * 
     * @return Y position in meters, or 0 if no data available
     * @educational
     */
    public float getPositionY() {
        var position = droneStatus.getPosition();
        return (position != null) ? position.getY() : 0;
    }

    /**
     * Gets the Z position (up/down) in meters.
     * 
     * <p>Returns the drone's up/down position relative to takeoff point.
     * Positive values indicate upward movement, negative values indicate downward.</p>
     * 
     * @return Z position in meters, or 0 if no data available
     * @educational
     */
    public float getPositionZ() {
        var position = droneStatus.getPosition();
        return (position != null) ? position.getZ() : 0;
    }

    /**
     * Gets atmospheric pressure in Pascals.
     * 
     * <p>Returns the current atmospheric pressure reading from the drone's
     * barometric sensor. This data is useful for altitude calculations and
     * environmental monitoring projects.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Physics Learning:</strong> Understand atmospheric pressure</li>
     *   <li><strong>Data Science:</strong> Environmental data collection</li>
     *   <li><strong>Weather Monitoring:</strong> Pressure trend analysis</li>
     *   <li><strong>Altitude Calculation:</strong> Barometric altitude estimation</li>
     * </ul>
     * 
     * @return Pressure in Pascals, or 0.0 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_pressure("Pa")}
     * @since 1.0
     * @educational
     */
    /**
     * Gets the current pressure value from the drone in Pascals.
     *
     * @return Pressure in Pascals (Pa)
     * @apiNote Equivalent to Python's {@code drone.get_pressure()}
     * @since 1.0
     * @educational
     */
    public double getPressure() {
        var altitude = droneStatus.getAltitude();
        return (altitude != null) ? altitude.getPressure() : 0.0;
    }

    /**
     * Gets atmospheric pressure in the specified unit.
     * 
     * <p>Returns atmospheric pressure converted to the requested unit.
     * Supports common pressure units for educational and scientific use.</p>
     * 
     * @param unit The unit for pressure measurement. Supported values:
     *             "Pa" (Pascals), "kPa" (kilopascals), "mbar" (millibars),
     *             "inHg" (inches of mercury), "atm" (atmospheres)
     * @return Pressure in the specified unit, or 0.0 if no data available
     * @throws IllegalArgumentException if unit is not supported
     * @apiNote Equivalent to Python's {@code drone.get_pressure(unit)}
     * @since 1.0
     * @educational
     */
    /**
     * Gets the current pressure value from the drone in the specified unit.
     *
     * @param unit The unit for the return value: "hPa", "psi", etc.
     * @return Pressure in the specified unit
     * @apiNote Equivalent to Python's {@code drone.get_pressure(unit)}
     * @since 1.0
     * @educational
     */
    public double getPressure(String unit) {
        double pascals = getPressure();
        if (pascals == 0.0) {
            return 0.0;
        }
        
        switch (unit.toLowerCase()) {
            case "pa":
                return pascals;
            case "kpa":
                return pascals / 1000.0;
            case "mbar":
                return pascals / 100.0;
            case "inhg":
                return pascals / 3386.389;
            case "atm":
                return pascals / 101325.0;
            default:
                throw new IllegalArgumentException("Unsupported pressure unit: " + unit + 
                    ". Supported units: Pa, kPa, mbar, inHg, atm");
        }
    }

    /**
     * Gets the uncorrected elevation from the drone's firmware.
     * 
     * <p><strong>⚠️ Important:</strong> This returns the raw altitude value from the drone's
     * firmware, which has a known offset of approximately +100 to +150 meters. This value
     * should NOT be used for accurate altitude measurements. Use {@link #getCorrectedElevation()}
     * for accurate altitude readings.</p>
     * 
     * <p>This method is provided for:
     * <ul>
     *   <li>Python API compatibility (matches {@code drone.get_elevation()})</li>
     *   <li>Educational purposes to demonstrate sensor calibration concepts</li>
     *   <li>Debugging and comparison with corrected values</li>
     * </ul>
     * 
     * <h3>📊 Example Readings:</h3>
     * <pre>
     * // At 16m above sea level with 1011 hPa pressure:
     * double uncorrected = drone.getUncorrectedElevation();  // ~126m (off by ~110m!)
     * double corrected = drone.getCorrectedElevation();       // ~17m (accurate!)
     * </pre>
     * 
     * @return Uncorrected elevation in meters (with firmware offset), or 0.0 if no data available
     * @see #getCorrectedElevation()
     * @see #getElevation()
     * @apiNote Equivalent to Python's {@code drone.get_elevation()}
     * @since 1.0
     * @educational
     */
    public double getUncorrectedElevation() {
        var altitude = droneStatus.getAltitude();
        return altitude != null ? altitude.getAltitude() : 0.0;
    }

    /**
     * Gets the elevation reading - either corrected or uncorrected based on settings.
     * 
     * <p>This convenience method matches the Python API while allowing flexible behavior:
     * <ul>
     *   <li><strong>Default:</strong> Returns uncorrected firmware value (Python compatibility)</li>
     *   <li><strong>After {@code useCorrectedElevation(true)}:</strong> Returns corrected value</li>
     * </ul>
     * 
     * <p><strong>🎓 Educational Note:</strong> For explicit control and clearer code,
     * prefer using {@link #getUncorrectedElevation()} or {@link #getCorrectedElevation()}
     * directly instead of relying on state-based behavior.</p>
     * 
     * <h3>📝 Usage Examples:</h3>
     * <pre>
     * // Python-style default (uncorrected):
     * double elevation = drone.getElevation();  // Returns uncorrected value
     * 
     * // Switch to corrected values:
     * drone.useCorrectedElevation(true);
     * double elevation = drone.getElevation();  // Now returns corrected value
     * 
     * // Explicit control (recommended):
     * double raw = drone.getUncorrectedElevation();
     * double accurate = drone.getCorrectedElevation();
     * </pre>
     * 
     * @return Elevation in meters (corrected or uncorrected based on settings)
     * @see #getUncorrectedElevation()
     * @see #getCorrectedElevation()
     * @see #useCorrectedElevation(boolean)
     * @apiNote Equivalent to Python's {@code drone.get_elevation()}
     * @since 1.0
     * @educational
     */
    public double getElevation() {
        return useCorrectedElevation ? getCorrectedElevation() : getUncorrectedElevation();
    }

    /**
     * Sets whether {@link #getElevation()} returns corrected or uncorrected values.
     * 
     * <p>This allows switching between Python-compatible default behavior (uncorrected)
     * and accurate altitude readings (corrected) without changing code that calls
     * {@code getElevation()}.</p>
     * 
     * <p><strong>Note:</strong> This only affects {@link #getElevation()}. The explicit
     * methods {@link #getUncorrectedElevation()} and {@link #getCorrectedElevation()}
     * always return their respective values regardless of this setting.</p>
     * 
     * @param useCorrected If true, {@code getElevation()} returns corrected altitude.
     *                     If false (default), returns uncorrected firmware value.
     * @see #getElevation()
     * @since 1.0
     * @educational
     */
    public void useCorrectedElevation(boolean useCorrected) {
        this.useCorrectedElevation = useCorrected;
    }

    /**
     * Calculates accurate altitude from barometric pressure using the standard barometric formula.
     * 
     * <p>This method provides accurate altitude calculation, correcting the drone's built-in
     * altitude reading which has a firmware offset of +100 to +150 meters.</p>
     * 
     * <p><strong>🌐 Automatic Calibration:</strong> This method now automatically attempts to
     * determine your location and fetch current weather data for best accuracy:
     * <ol>
     *   <li>Try OS location services (if available via JNI)</li>
     *   <li>Try IP-based geolocation</li>
     *   <li>Fall back to standard atmosphere (101325 Pa)</li>
     * </ol>
     * All failures are handled gracefully - you'll always get a valid altitude reading!</p>
     * 
     * <p>The calculation uses the international standard atmosphere formula:
     * <pre>h = 44330 * (1 - (P/P₀)^0.1903)</pre>
     * where P is measured pressure and P₀ is sea-level pressure.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Physics Learning:</strong> Understand barometric altitude calculation</li>
     *   <li><strong>Math Application:</strong> Real-world exponential functions</li>
     *   <li><strong>Sensor Calibration:</strong> Compare calculated vs sensor-reported values</li>
     *   <li><strong>Data Science:</strong> Error analysis and calibration techniques</li>
     *   <li><strong>Graceful Degradation:</strong> Learn about fallback strategies</li>
     * </ul>
     * 
     * <p><strong>Note:</strong> For explicit control of pressure source, use
     * {@link #getCorrectedElevation(double)} or {@link #getCorrectedElevation(double, double)}.</p>
     * 
     * @return Corrected altitude in meters above sea level, or 0.0 if no pressure data available
     * @see #getPressure()
     * @see #getUncorrectedElevation()
     * @see #getCorrectedElevation(double)
     * @see #getCorrectedElevation(double, double)
     * @since 1.0
     * @educational
     */
    public double getCorrectedElevation() {
        double seaLevelPressure = com.otabi.jcodroneedu.util.WeatherService.getAutomaticSeaLevelPressure();
        return getCorrectedElevation(seaLevelPressure);
    }

    /**
     * Calculates corrected altitude from barometric pressure using a calibrated sea-level pressure.
     * 
     * <p>This allows for more accurate altitude calculations when the current local
     * sea-level pressure is known (from local weather reports or nearby weather stations).
     * Uses the international standard atmosphere formula with calibrated sea-level pressure.</p>
     * 
     * <h3>📡 Getting Local Sea-Level Pressure:</h3>
     * <ul>
     *   <li>Check local weather stations or weather apps</li>
     *   <li>Look for "barometric pressure" or "QNH" in aviation weather</li>
     *   <li>Convert if needed: 1 hPa = 100 Pa, 1 inHg = 3386.39 Pa</li>
     * </ul>
     * 
     * @param seaLevelPressure The current sea-level pressure in Pascals (Pa).
     *                         Standard atmosphere is 101325 Pa (1013.25 hPa).
     *                         Get local value from weather reports for best accuracy.
     * @return Corrected altitude in meters above sea level, or 0.0 if no pressure data available
     * @see #getPressure()
     * @see #getCorrectedElevation()
     * @since 1.0
     * @educational
     */
    public double getCorrectedElevation(double seaLevelPressure) {
        double pressure = getPressure();
        if (pressure == 0.0) {
            return 0.0;
        }
        
        // International standard atmosphere formula
        // h = 44330 * (1 - (P/P₀)^0.1903)
        return 44330.0 * (1.0 - Math.pow(pressure / seaLevelPressure, 0.1903));
    }

    /**
     * @deprecated Use {@link #getCorrectedElevation()} instead.
     *             Method renamed for clarity (calculated → corrected).
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public double getCalculatedAltitude() {
        return getCorrectedElevation();
    }

    /**
     * @deprecated Use {@link #getCorrectedElevation(double)} instead.
     *             Method renamed for clarity (calculated → corrected).
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public double getCalculatedAltitude(double seaLevelPressure) {
        return getCorrectedElevation(seaLevelPressure);
    }

    /**
     * Gets corrected elevation using current sea-level pressure from online weather data.
     * 
     * <p>This method automatically fetches the current barometric pressure from online
     * weather services (Open-Meteo API) based on your location coordinates. This provides
     * the most accurate altitude readings by accounting for local weather conditions.</p>
     * 
     * <h3>📍 How to Find Your Coordinates:</h3>
     * <ul>
     *   <li><strong>Google Maps:</strong> Right-click location → "What's here?"</li>
     *   <li><strong>iPhone:</strong> Open Compass app (shows coordinates at bottom)</li>
     *   <li><strong>Settings:</strong> Enable Location Services for more accuracy</li>
     *   <li><strong>Command line:</strong> curl ipinfo.io (approximate)</li>
     * </ul>
     * 
     * <h3>🌐 Network Requirements:</h3>
     * <ul>
     *   <li>Requires internet connection</li>
     *   <li>Uses Open-Meteo API (free, no API key)</li>
     *   <li>Falls back to standard pressure if offline</li>
     *   <li>5-second timeout to avoid blocking</li>
     * </ul>
     * 
     * <h3>💡 Example Usage:</h3>
     * <pre>
     * // San Francisco, CA
     * double elevation = drone.getCorrectedElevation(37.7749, -122.4194);
     * 
     * // New York, NY  
     * double elevation = drone.getCorrectedElevation(40.7128, -74.0060);
     * 
     * // Tokyo, Japan
     * double elevation = drone.getCorrectedElevation(35.6762, 139.6503);
     * </pre>
     * 
     * @param latitude Latitude of your location in decimal degrees (-90 to 90)
     * @param longitude Longitude of your location in decimal degrees (-180 to 180)
     * @return Corrected elevation in meters with weather-calibrated sea-level pressure
     * @throws IllegalArgumentException if coordinates are out of valid range
     * @see com.otabi.jcodroneedu.util.WeatherService#getSeaLevelPressure(double, double)
     * @since 1.0
     * @educational
     */
    public double getCorrectedElevation(double latitude, double longitude) {
        double seaLevelPressure = com.otabi.jcodroneedu.util.WeatherService.getSeaLevelPressure(latitude, longitude);
        return getCorrectedElevation(seaLevelPressure);
    }

    /**
     * Sets the initial pressure reference point for relative height measurements.
     * 
     * <p>This method captures the current barometric pressure from the drone and stores it
     * as a reference point. Subsequent calls to {@link #getHeightFromPressure()} will return
     * the height change relative to this reference point.</p>
     * 
     * <p><strong>🎯 Use Cases:</strong></p>
     * <ul>
     *   <li><strong>Indoor Flying:</strong> Measure height above floor without knowing sea level</li>
     *   <li><strong>Climb/Descent Tracking:</strong> Monitor altitude changes during flight</li>
     *   <li><strong>Simple Calibration:</strong> No need for weather data or coordinates</li>
     *   <li><strong>Relative Navigation:</strong> "How much higher/lower am I than where I started?"</li>
     * </ul>
     * 
     * <h3>📝 Usage Pattern:</h3>
     * <pre>
     * // Place drone on ground/starting position
     * drone.setInitialPressure();
     * 
     * // Fly around
     * drone.takeoff();
     * Thread.sleep(3000);
     * 
     * // Check how high you've climbed
     * double heightChange = drone.getHeightFromPressure();
     * System.out.printf("Height above starting point: %.2f m\n", heightChange / 1000.0);
     * </pre>
     * 
     * <p><strong>Note:</strong> Call this method when the drone is at the position you want
     * to use as your reference "zero" point. Typically this is done on the ground before takeoff.</p>
     * 
     * @see #getHeightFromPressure()
     * @see #getHeightFromPressure(double, double)
     * @apiNote Equivalent to Python's {@code drone.set_initial_pressure()}
     * @since 1.0
     * @educational
     */
    public void setInitialPressure() {
        this.initialPressure = getPressure();
    }

    /**
     * Gets the relative height change from the initial pressure reference point.
     * 
     * <p>Returns the height change in millimeters since {@link #setInitialPressure()} was called.
     * This uses a linear approximation of pressure-to-height conversion with default calibration
     * values (b=0, m=9.34 mm/Pa).</p>
     * 
     * <p><strong>⚠️ Important:</strong> You MUST call {@link #setInitialPressure()} first,
     * otherwise this will return 0.0.</p>
     * 
     * <h3>🎯 Educational Context:</h3>
     * <ul>
     *   <li><strong>Linear vs Exponential:</strong> Compare with {@link #getCorrectedElevation()}</li>
     *   <li><strong>Relative vs Absolute:</strong> This measures change, not position</li>
     *   <li><strong>Indoor Applications:</strong> Works without internet or weather data</li>
     *   <li><strong>Sensor Precision:</strong> Accuracy is ±5-10mm in stable conditions</li>
     * </ul>
     * 
     * <h3>📐 Formula:</h3>
     * <pre>height_mm = (initial_pressure - current_pressure) * 9.34</pre>
     * 
     * <p><strong>Why 9.34?</strong> This is an empirically-derived constant representing
     * approximately 9.34 mm of height change per Pascal of pressure change near sea level.</p>
     * 
     * @return Height change in millimeters from initial reference point, or 0.0 if
     *         {@code setInitialPressure()} has not been called
     * @see #setInitialPressure()
     * @see #getHeightFromPressure(double, double)
     * @apiNote Equivalent to Python's {@code drone.height_from_pressure()}
     * @since 1.0
     * @educational
     */
    public double getHeightFromPressure() {
        return getHeightFromPressure(0.0, 9.34);
    }

    /**
     * Gets the relative height change with custom calibration parameters.
     * 
     * <p>This overload allows fine-tuning the pressure-to-height conversion using
     * custom calibration values. The formula is:
     * <pre>height_mm = (initial_pressure - current_pressure + b) * m</pre>
     * 
     * <h3>📊 Calibration Parameters:</h3>
     * <ul>
     *   <li><strong>b (offset):</strong> Pressure offset in Pascals. Use to correct for
     *       systematic errors or sensor drift. Default: 0.0</li>
     *   <li><strong>m (slope):</strong> Conversion factor in mm/Pa. Adjusts sensitivity.
     *       Default: 9.34 mm/Pa</li>
     * </ul>
     * 
     * <h3>🔬 When to Adjust:</h3>
     * <ul>
     *   <li><strong>m parameter:</strong> If measurements consistently over/under-report height</li>
     *   <li><strong>b parameter:</strong> If you need to compensate for known pressure offset</li>
     *   <li><strong>Default values:</strong> Work well for most educational applications</li>
     * </ul>
     * 
     * <h3>💡 Example Calibration:</h3>
     * <pre>
     * // Measure known height (e.g., 1 meter = 1000mm)
     * drone.setInitialPressure();
     * drone.goToHeight(100); // Fly to 100cm
     * double measured = drone.getHeightFromPressure();
     * 
     * // If measured is 1050mm instead of 1000mm, adjust m:
     * double newM = 9.34 * (1000.0 / 1050.0); // ≈ 8.9
     * double corrected = drone.getHeightFromPressure(0, newM);
     * </pre>
     * 
     * @param b Pressure offset in Pascals (y-intercept of linear model)
     * @param m Conversion slope in millimeters per Pascal
     * @return Height change in millimeters from initial reference point
     * @see #setInitialPressure()
     * @see #getHeightFromPressure()
     * @apiNote Equivalent to Python's {@code drone.height_from_pressure(b, m)}
     * @since 1.0
     * @educational
     */
    public double getHeightFromPressure(double b, double m) {
        if (initialPressure == 0.0) {
            return 0.0;
        }
        double currentPressure = getPressure();
        double heightMm = (initialPressure - currentPressure + b) * m;
        return Math.round(heightMm * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Gets temperature from the drone (DEPRECATED - use getDroneTemperature).
     * 
     * <p><strong>⚠️ DEPRECATED:</strong> This method is deprecated and will be removed
     * in a future release. Use {@link #getDroneTemperature()} instead.</p>
     * 
     * <p>This method exists for Python API compatibility. Python's {@code get_temperature()}
     * was deprecated in favor of {@code get_drone_temperature()} to clarify that it returns
     * the drone's internal sensor temperature, not ambient air temperature.</p>
     * 
     * @return Temperature in Celsius (uncalibrated or calibrated based on settings)
     * @deprecated Use {@link #getDroneTemperature()} instead. This method is deprecated
     *             to match Python's API deprecation.
     * @apiNote Equivalent to Python's deprecated {@code drone.get_temperature()}
     * @since 1.0
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public double getTemperature() {
        log.warn("getTemperature() is deprecated. Use getDroneTemperature() instead.");
        return getDroneTemperature();
    }

    /**
     * Gets temperature from the drone in specified unit (DEPRECATED - use getDroneTemperature).
     * 
     * <p><strong>⚠️ DEPRECATED:</strong> This method is deprecated and will be removed
     * in a future release. Use {@link #getDroneTemperature(String)} instead.</p>
     * 
     * @param unit The unit for temperature measurement: "C", "F", or "K"
     * @return Temperature in the specified unit (uncalibrated or calibrated based on settings)
     * @deprecated Use {@link #getDroneTemperature(String)} instead. This method is deprecated
     *             to match Python's API deprecation.
     * @apiNote Equivalent to Python's deprecated {@code drone.get_temperature(unit)}
     * @since 1.0
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public double getTemperature(String unit) {
        log.warn("getTemperature(unit) is deprecated. Use getDroneTemperature(unit) instead.");
        return getDroneTemperature(unit);
    }

    /**
     * Gets the uncalibrated temperature reading from the drone's sensor in Celsius.
     * 
     * <p><strong>ALWAYS</strong> returns the raw sensor die temperature without any calibration.
     * The sensor chip typically reads 10-15°C cooler than ambient air temperature.</p>
     * 
     * <p>This is the explicit method for getting uncalibrated temperature. Unlike
     * {@link #getDroneTemperature()}, this method always returns raw sensor values
     * regardless of the {@link #useCalibratedTemperature(boolean)} setting.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Sensor Calibration:</strong> Measure the sensor offset experimentally</li>
     *   <li><strong>Data Collection:</strong> Raw sensor data for scientific analysis</li>
     *   <li><strong>Physics Learning:</strong> Heat transfer and thermal properties</li>
     *   <li><strong>Comparison:</strong> Compare with {@link #getCalibratedTemperature()}</li>
     * </ul>
     * 
     * <h3>📝 Usage Example:</h3>
     * <pre>{@code
     * double raw = drone.getUncalibratedTemperature();       // 8°C (sensor die)
     * double calibrated = drone.getCalibratedTemperature();  // 20°C (ambient estimate)
     * double offset = calibrated - raw;                      // 12°C correction
     * }</pre>
     * 
     * @return Uncalibrated temperature in Celsius (sensor die temperature), or 0.0 if no data available
     * @see #getCalibratedTemperature() for calibrated ambient temperature
     * @see #getDroneTemperature() for switchable temperature (respects settings)
     * @since 1.0
     * @educational
     */
    public double getUncalibratedTemperature() {
        var altitude = droneStatus.getAltitude();
        return (altitude != null) ? altitude.getTemperature() : 0.0;
    }

    /**
     * Gets the uncalibrated temperature reading in the specified unit.
     * 
     * <p><strong>ALWAYS</strong> returns the raw sensor temperature without calibration,
     * converted to the requested unit.</p>
     * 
     * @param unit The unit for temperature measurement. Supported values:
     *             "C" (Celsius), "F" (Fahrenheit), "K" (Kelvin)
     * @return Uncalibrated temperature in the specified unit, or 0.0 if no data available
     * @throws IllegalArgumentException if unit is not supported
     * @see #getUncalibratedTemperature() for Celsius version
     * @see #getCalibratedTemperature(String) for calibrated temperature with unit
     * @since 1.0
     * @educational
     */
    public double getUncalibratedTemperature(String unit) {
        double celsius = getUncalibratedTemperature();
        return convertTemperature(celsius, unit);
    }

    /**
     * Gets the temperature reading - either uncalibrated or calibrated based on settings.
     * 
     * <p>This convenience method matches the Python API while allowing flexible behavior:
     * <ul>
     *   <li><strong>Default:</strong> Returns uncalibrated sensor value (Python compatibility)</li>
     *   <li><strong>After {@code useCalibratedTemperature(true)}:</strong> Returns calibrated value</li>
     * </ul>
     * 
     * <p><strong>🎓 Educational Note:</strong> For explicit control and clearer code,
     * prefer using {@link #getUncalibratedTemperature()} or {@link #getCalibratedTemperature()}
     * directly instead of relying on state-based behavior.</p>
     * 
     * <h3>📝 Usage Examples:</h3>
     * <pre>{@code
     * // Python-style default (uncalibrated):
     * double temp = drone.getDroneTemperature();  // Returns uncalibrated (8°C)
     * 
     * // Switch to calibrated values:
     * drone.useCalibratedTemperature(true);
     * double temp = drone.getDroneTemperature();  // Now returns calibrated (20°C)
     * 
     * // Explicit control (recommended):
     * double raw = drone.getUncalibratedTemperature();     // 8°C
     * double accurate = drone.getCalibratedTemperature();  // 20°C
     * }</pre>
     * 
     * @return Temperature in Celsius (uncalibrated or calibrated based on settings), or 0.0 if no data available
     * @see #getUncalibratedTemperature()
     * @see #getCalibratedTemperature()
     * @see #useCalibratedTemperature(boolean)
     * @apiNote Equivalent to Python's {@code drone.get_drone_temperature()}
     * @since 1.0
     * @educational
     */
    public double getDroneTemperature() {
        return useCalibratedTemperature ? getCalibratedTemperature() : getUncalibratedTemperature();
    }

    /**
     * Gets the temperature reading in the specified unit - uncalibrated or calibrated based on settings.
     * 
     * <p>Returns temperature converted to the requested unit, respecting the calibration setting.</p>
     * 
     * <p><strong>Note:</strong> This method's behavior changes based on {@link #useCalibratedTemperature(boolean)}.
     * For explicit control, use {@link #getUncalibratedTemperature(String)} or
     * {@link #getCalibratedTemperature(String)}.</p>
     * 
     * @param unit The unit for temperature measurement. Supported values:
     *             "C" (Celsius), "F" (Fahrenheit), "K" (Kelvin)
     * @return Temperature in the specified unit (uncalibrated or calibrated based on settings), or 0.0 if no data available
     * @throws IllegalArgumentException if unit is not supported
     * @apiNote Equivalent to Python's {@code drone.get_drone_temperature(unit)}
     * @see #getUncalibratedTemperature(String) for always uncalibrated
     * @see #getCalibratedTemperature(String) for always calibrated
     * @see #useCalibratedTemperature(boolean)
     * @since 1.0
     * @educational
     */
    public double getDroneTemperature(String unit) {
        return useCalibratedTemperature ? getCalibratedTemperature(unit) : getUncalibratedTemperature(unit);
    }

    /**
     * Sets whether {@link #getDroneTemperature()} returns uncalibrated or calibrated values.
     * 
     * <p>This allows switching between Python-compatible default behavior (uncalibrated)
     * and accurate ambient temperature readings (calibrated) without changing code that calls
     * {@code getDroneTemperature()}.</p>
     * 
     * <p><strong>Note:</strong> This only affects {@link #getDroneTemperature()}. The explicit
     * methods {@link #getUncalibratedTemperature()} and {@link #getCalibratedTemperature()}
     * always return their respective values regardless of this setting.</p>
     * 
     * <h3>📝 Usage Example:</h3>
     * <pre>{@code
     * // Initially returns uncalibrated temperature
     * System.out.println("Raw: " + drone.getDroneTemperature());  // 8°C
     * 
     * // Flip the switch to get calibrated temperatures
     * drone.useCalibratedTemperature(true);
     * System.out.println("Calibrated: " + drone.getDroneTemperature());  // 20°C
     * 
     * // Explicit methods always work regardless of setting
     * System.out.println("Always raw: " + drone.getUncalibratedTemperature());  // 8°C
     * System.out.println("Always calibrated: " + drone.getCalibratedTemperature());  // 20°C
     * }</pre>
     * 
     * @param useCalibrated If true, {@code getDroneTemperature()} returns calibrated temperature.
     *                      If false (default), returns uncalibrated sensor value.
     * @see #getDroneTemperature()
     * @since 1.0
     * @educational
     */
    public void useCalibratedTemperature(boolean useCalibrated) {
        this.useCalibratedTemperature = useCalibrated;
    }

    /**
     * Default temperature calibration offset in Celsius.
     * 
     * <p>The BMP280 barometric sensor reports die temperature, which is typically
     * 10-15°C cooler than ambient air. This default offset (12°C) provides a
     * reasonable estimate of ambient temperature.</p>
     * 
     * <p>Students can experiment with different offsets based on their environment
     * by using {@link #getCalibratedTemperature(double)}.</p>
     */
    private static final double DEFAULT_TEMPERATURE_OFFSET_C = 12.0;

    /**
     * Gets the calibrated ambient temperature in Celsius.
     * 
     * <p><strong>ALWAYS</strong> returns calibrated temperature regardless of settings.
     * This is the explicit method for getting calibrated ambient temperature estimates.</p>
     * 
     * <p>This method applies a default calibration offset of 12°C to the raw sensor
     * reading to estimate ambient air temperature. The sensor die is typically 10-15°C
     * cooler than ambient air due to heat dissipation.</p>
     * 
     * <p>Unlike {@link #getDroneTemperature()}, this method always returns calibrated
     * values regardless of the {@link #useCalibratedTemperature(boolean)} setting.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Sensor Calibration:</strong> Demonstrates offset correction</li>
     *   <li><strong>Scientific Method:</strong> Compare with reference thermometer</li>
     *   <li><strong>Error Analysis:</strong> Understand sensor limitations</li>
     *   <li><strong>Weather Stations:</strong> More accurate ambient readings</li>
     * </ul>
     * 
     * <h3>📝 Usage Example:</h3>
     * <pre>{@code
     * double sensorTemp = drone.getUncalibratedTemperature();  // 8°C (sensor die)
     * double ambientTemp = drone.getCalibratedTemperature();   // 20°C (ambient estimate)
     * double offset = ambientTemp - sensorTemp;                // 12°C correction
     * }</pre>
     * 
     * @return Estimated ambient temperature in Celsius, or 0.0 if no data available
     * @see #getUncalibratedTemperature() for raw sensor reading
     * @see #getCalibratedTemperature(double) for custom offset
     * @see #getDroneTemperature() for switchable temperature (respects settings)
     * @since 1.0
     * @educational
     */
    public double getCalibratedTemperature() {
        return getUncalibratedTemperature() + DEFAULT_TEMPERATURE_OFFSET_C;
    }

    /**
     * Gets the calibrated ambient temperature with a custom offset in Celsius.
     * 
     * <p><strong>ALWAYS</strong> applies the specified calibration offset regardless of settings.</p>
     * 
     * <p>Apply your own calibration offset based on experimental measurements
     * with a reference thermometer.</p>
     * 
     * <h3>🧪 Calibration Procedure:</h3>
     * <ol>
     *   <li>Place drone and reference thermometer in same location</li>
     *   <li>Wait 5 minutes for thermal stabilization</li>
     *   <li>Read both temperatures</li>
     *   <li>Calculate: offset = reference - drone reading</li>
     *   <li>Use this offset for future measurements</li>
     * </ol>
     * 
     * <h3>📝 Usage Example:</h3>
     * <pre>{@code
     * // Experimental calibration
     * double sensorTemp = drone.getUncalibratedTemperature();  // 7.5°C
     * double referenceTemp = 21.0;                             // From thermometer
     * double offset = referenceTemp - sensorTemp;              // 13.5°C
     * 
     * // Use calibrated readings
     * double ambient = drone.getCalibratedTemperature(offset); // 21.0°C
     * }</pre>
     * 
     * @param offsetCelsius The calibration offset to add to sensor reading (in Celsius)
     * @return Calibrated temperature in Celsius, or offset value if no data available
     * @see #getUncalibratedTemperature() for raw sensor reading
     * @see #getCalibratedTemperature() for default offset
     * @since 1.0
     * @educational
     */
    public double getCalibratedTemperature(double offsetCelsius) {
        return getUncalibratedTemperature() + offsetCelsius;
    }

    /**
     * Gets the calibrated ambient temperature in the specified unit.
     * 
     * <p><strong>ALWAYS</strong> returns calibrated temperature (with default 12°C offset)
     * regardless of settings, converted to the requested unit.</p>
     * 
     * @param unit The unit for temperature measurement: "C", "F", or "K"
     * @return Calibrated temperature in the specified unit, or 0.0 if no data available
     * @throws IllegalArgumentException if unit is not supported
     * @see #getCalibratedTemperature() for Celsius version
     * @see #getCalibratedTemperature(double, String) for custom offset with unit conversion
     * @since 1.0
     * @educational
     */
    public double getCalibratedTemperature(String unit) {
        double calibratedCelsius = getCalibratedTemperature();
        return convertTemperature(calibratedCelsius, unit);
    }

    /**
     * Gets the calibrated ambient temperature with custom offset in the specified unit.
     * 
     * <p><strong>ALWAYS</strong> applies the specified calibration offset regardless of settings,
     * with unit conversion.</p>
     * 
     * <h3>📝 Usage Example:</h3>
     * <pre>{@code
     * // Custom offset with Fahrenheit output
     * double tempF = drone.getCalibratedTemperature(13.5, "F");
     * }</pre>
     * 
     * @param offsetCelsius The calibration offset in Celsius
     * @param unit The unit for output: "C", "F", or "K"
     * @return Calibrated temperature in the specified unit
     * @throws IllegalArgumentException if unit is not supported
     * @see #getCalibratedTemperature(double) for Celsius version
     * @since 1.0
     * @educational
     */
    public double getCalibratedTemperature(double offsetCelsius, String unit) {
        double calibratedCelsius = getCalibratedTemperature(offsetCelsius);
        return convertTemperature(calibratedCelsius, unit);
    }

    /**
     * Converts temperature from Celsius to the specified unit.
     * 
     * @param celsius Temperature in Celsius
     * @param unit Target unit: "C", "F", or "K"
     * @return Converted temperature
     * @throws IllegalArgumentException if unit is not supported
     */
    private double convertTemperature(double celsius, String unit) {
        switch (unit.toUpperCase()) {
            case "C":
                return celsius;
            case "F":
                return (celsius * 9.0 / 5.0) + 32.0;
            case "K":
                return celsius + 273.15;
            default:
                throw new IllegalArgumentException("Unsupported temperature unit: " + unit + 
                    ". Supported units: C, F, K");
        }
    }

    /**
     * Gets comprehensive sensor data in a single call.
     * 
     * <p>Returns an array containing all major sensor readings for advanced
     * data analysis and logging. This method provides efficient access to
     * multiple sensor values simultaneously.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Data Science:</strong> Multi-sensor data analysis</li>
     *   <li><strong>L0202 Arrays:</strong> Complex array data handling</li>
     *   <li><strong>Sensor Fusion:</strong> Combining multiple sensor inputs</li>
     *   <li><strong>Research Projects:</strong> Comprehensive data logging</li>
     * </ul>
     * 
     * <h3>📊 Array Structure:</h3>
     * <p>Returns array with indices:</p>
     * <ul>
     *   <li>[0-2]: Position (x, y, z) in mm</li>
     *   <li>[3-5]: Acceleration (x, y, z) in G-force</li>
     *   <li>[6-8]: Gyroscope (x, y, z) in degrees/second</li>
     *   <li>[9-11]: Angle (roll, pitch, yaw) in degrees</li>
     *   <li>[12-17]: Range sensors (front, back, top, bottom, left, right) in mm</li>
     *   <li>[18]: Battery percentage (0-100)</li>
     *   <li>[19]: Pressure in Pascals</li>
     *   <li>[20]: Temperature in Celsius</li>
     * </ul>
     * 
     * @return Array containing all sensor values, or null if no data available
     * @apiNote Equivalent to Python's {@code drone.get_sensor_data()}
     * @since 1.0
     * @educational
     */
    /**
     * Gets all sensor data as a double array.
     *
     * @return Array containing all sensor values, or null if no data available
     * @apiNote Equivalent to Python's {@code drone.get_sensor_data()}
     * @since 1.0
     * @educational
     */
    public double[] getSensorData() {
        // Check if we have basic sensor data
        if (droneStatus.getPosition() == null || droneStatus.getRange() == null) {
            return null;
        }
        
        double[] sensorData = new double[21];
        
        // Position data (0-2) - now in meters as floats
        float[] position = getPositionData();
        if (position != null) {
            sensorData[0] = position[0]; // x
            sensorData[1] = position[1]; // y  
            sensorData[2] = position[2]; // z
        }
        
        // Motion data (3-11)
        int[] accel = getAccel();
        if (accel != null) {
            sensorData[3] = accel[0]; // accel x
            sensorData[4] = accel[1]; // accel y
            sensorData[5] = accel[2]; // accel z
        }
        
        int[] gyro = getGyro();
        if (gyro != null) {
            sensorData[6] = gyro[0]; // gyro x
            sensorData[7] = gyro[1]; // gyro y
            sensorData[8] = gyro[2]; // gyro z
        }
        
        int[] angle = getAngle();
        if (angle != null) {
            sensorData[9] = angle[0];  // roll
            sensorData[10] = angle[1]; // pitch
            sensorData[11] = angle[2]; // yaw
        }
        
        // Range data (12-17)
        sensorData[12] = getFrontRange();
        sensorData[13] = getBackRange();
        sensorData[14] = getTopRange();
        sensorData[15] = getBottomRange();
        sensorData[16] = getLeftRange();
        sensorData[17] = getRightRange();
        
        // State data (18)
        sensorData[18] = getBattery();
        
        // Environmental data (19-20)
        sensorData[19] = getPressure();
        sensorData[20] = getDroneTemperature();
        
        return sensorData;
    }

    // =============================================================================
    // PHASE 3.6: Optical Flow Sensors (Punch List Item #14)
    // Advanced navigation sensors for velocity measurements
    // =============================================================================

    /**
     * Retrieve optical flow velocity value measured by optical flow sensor
     * from the X direction (forward and reverse) in specified units.
     * 
     * <p>This method provides Python API compatibility for advanced navigation
     * applications that require precise velocity measurements.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * Students learn about optical flow sensors and velocity measurement techniques.
     * This is particularly useful for advanced robotics curricula involving
     * navigation algorithms and autonomous movement control.</p>
     * 
     * <p><strong>Technical Details:</strong><br>
     * The optical flow sensor tracks ground texture movement to calculate
     * relative velocity. X-axis represents forward/backward movement relative
     * to the drone's orientation.</p>
     * 
     * @param unit The unit for the return value: "cm", "in", "mm", or "m"
     * @return X-axis flow velocity in the specified unit, or 0.0 if no data available
     * 
     * @apiNote Equivalent to Python's {@code drone.get_flow_velocity_x(unit)}
     * @since 1.0
     * @educational Advanced navigation and robotics curricula
     */
    /**
     * Retrieve optical flow velocity value measured by optical flow sensor
     * from the X direction (forward and reverse) in specified units.
     *
     * @param unit The unit for the return value: "cm", "in", "mm", or "m"
     * @return X-axis flow velocity in the specified unit, or 0.0 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_flow_velocity_x(unit)}
     * @since 1.0
     * @educational Advanced navigation and robotics curricula
     */
    public double getFlowVelocityX(String unit) {
        var rawFlow = droneStatus.getRawFlow();
        if (rawFlow == null) {
            // Try to request new data only in real drone scenarios
            // In test scenarios, this will just return 0.0
            try {
                sendRequest(DataType.RawFlow);
                Thread.sleep(10); // Brief delay for data request
                rawFlow = droneStatus.getRawFlow();
            } catch (Exception e) {
                // Ignore exceptions (e.g., in test scenarios)
                // This allows tests to work without a real drone connection
            }
        }
        
        if (rawFlow == null) {
            return 0.0; // No data available
        }
        
        // Convert from meters to requested unit
        return convertMetersToUnit(rawFlow.getX(), unit);
    }

    /**
     * Retrieve optical flow velocity value measured by optical flow sensor
     * from the X direction (forward and reverse) in centimeters.
     * 
     * <p>Convenience method that defaults to centimeters for simplified use.</p>
     * 
     * @return X-axis flow velocity in centimeters, or 0.0 if no data available
     * 
     * @apiNote Equivalent to Python's {@code drone.get_flow_velocity_x()}
     * @since 1.0
     * @educational Advanced navigation and robotics curricula
     */
    /**
     * Retrieve optical flow velocity value measured by optical flow sensor
     * from the X direction (forward and reverse) in centimeters.
     *
     * @return X-axis flow velocity in centimeters, or 0.0 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_flow_velocity_x()}
     * @since 1.0
     * @educational Advanced navigation and robotics curricula
     */
    public double getFlowVelocityX() {
        return getFlowVelocityX("cm");
    }

    /**
     * Retrieve optical flow velocity value measured by optical flow sensor
     * from the Y direction (left and right) in specified units.
     * 
     * <p>This method provides Python API compatibility for advanced navigation
     * applications that require precise velocity measurements.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * Students learn about optical flow sensors and velocity measurement techniques.
     * This is particularly useful for advanced robotics curricula involving
     * navigation algorithms and lateral movement control.</p>
     * 
     * <p><strong>Technical Details:</strong><br>
     * The optical flow sensor tracks ground texture movement to calculate
     * relative velocity. Y-axis represents left/right movement relative
     * to the drone's orientation.</p>
     * 
     * @param unit The unit for the return value: "cm", "in", "mm", or "m"
     * @return Y-axis flow velocity in the specified unit, or 0.0 if no data available
     * 
     * @apiNote Equivalent to Python's {@code drone.get_flow_velocity_y(unit)}
     * @since 1.0
     * @educational Advanced navigation and robotics curricula
     */
    /**
     * Retrieve optical flow velocity value measured by optical flow sensor
     * from the Y direction (left and right) in specified units.
     *
     * @param unit The unit for the return value: "cm", "in", "mm", or "m"
     * @return Y-axis flow velocity in the specified unit, or 0.0 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_flow_velocity_y(unit)}
     * @since 1.0
     * @educational Advanced navigation and robotics curricula
     */
    public double getFlowVelocityY(String unit) {
        var rawFlow = droneStatus.getRawFlow();
        if (rawFlow == null) {
            // Try to request new data only in real drone scenarios
            // In test scenarios, this will just return 0.0
            try {
                sendRequest(DataType.RawFlow);
                Thread.sleep(10); // Brief delay for data request
                rawFlow = droneStatus.getRawFlow();
            } catch (Exception e) {
                // Ignore exceptions (e.g., in test scenarios)
                // This allows tests to work without a real drone connection
            }
        }
        
        if (rawFlow == null) {
            return 0.0; // No data available
        }
        
        // Convert from meters to requested unit
        return convertMetersToUnit(rawFlow.getY(), unit);
    }

    /**
     * Retrieve optical flow velocity value measured by optical flow sensor
     * from the Y direction (left and right) in centimeters.
     * 
     * <p>Convenience method that defaults to centimeters for simplified use.</p>
     * 
     * @return Y-axis flow velocity in centimeters, or 0.0 if no data available
     * 
     * @apiNote Equivalent to Python's {@code drone.get_flow_velocity_y()}
     * @since 1.0
     * @educational Advanced navigation and robotics curricula
     */
    /**
     * Retrieve optical flow velocity value measured by optical flow sensor
     * from the Y direction (left and right) in centimeters.
     *
     * @return Y-axis flow velocity in centimeters, or 0.0 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_flow_velocity_y()}
     * @since 1.0
     * @educational Advanced navigation and robotics curricula
     */
    public double getFlowVelocityY() {
        return getFlowVelocityY("cm");
    }

    /**
     * Get raw optical flow data for advanced applications.
     * 
     * <p>This method provides access to the complete optical flow data structure
     * for advanced users who need direct access to flow sensor readings.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * Advanced computer science students can use this for implementing
     * custom navigation algorithms or analyzing sensor data patterns.</p>
     * 
     * @return Array containing [timestamp, x_velocity, y_velocity] in meters, or null if no data available
     * 
     * @apiNote Similar to Python's {@code drone.get_flow_data()} but returns simplified array
     * @since 1.0
     * @educational Advanced robotics and data analysis curricula
     */
    /**
     * Get raw optical flow data for advanced applications.
     *
     * @return Array containing [timestamp, x_velocity, y_velocity] in meters, or null if no data available
     * @apiNote Similar to Python's {@code drone.get_flow_data()} but returns simplified array
     * @since 1.0
     * @educational Advanced robotics and data analysis curricula
     */
    public double[] getFlowData() {
        var rawFlow = droneStatus.getRawFlow();
        if (rawFlow == null) {
            // Try to request new data only in real drone scenarios
            // In test scenarios, this will just return null
            try {
                sendRequest(DataType.RawFlow);
                Thread.sleep(10); // Brief delay for data request
                rawFlow = droneStatus.getRawFlow();
            } catch (Exception e) {
                // Ignore exceptions (e.g., in test scenarios)
                // This allows tests to work without a real drone connection
            }
        }
        
        if (rawFlow == null) {
            return null; // No data available
        }
        
        // Return simplified flow data: [timestamp, x, y]
        double currentTime = System.currentTimeMillis() / 1000.0; // Current time in seconds
        return new double[]{currentTime, rawFlow.getX(), rawFlow.getY()};
    }

    /**
     * Convert meters to various distance units.
     * 
     * @param meters The distance in meters
     * @param unit The target unit: "cm", "mm", "m", or "in"
     * @return The distance in the specified unit
     * @throws IllegalArgumentException if unit is not supported
     */
    private double convertMetersToUnit(double meters, String unit) {
        switch (unit.toLowerCase()) {
            case DroneSystem.UnitConversion.UNIT_METERS:
                return meters;
            case DroneSystem.UnitConversion.UNIT_CENTIMETERS:
                return meters * DroneSystem.UnitConversion.METERS_TO_CENTIMETERS;
            case DroneSystem.UnitConversion.UNIT_MILLIMETERS:
                return meters * DroneSystem.UnitConversion.METERS_TO_MILLIMETERS;
            case DroneSystem.UnitConversion.UNIT_INCHES:
                return meters * DroneSystem.UnitConversion.METERS_TO_INCHES;
            default:
                throw new IllegalArgumentException("Unsupported distance unit: " + unit + 
                    ". Supported units: " + DroneSystem.UnitConversion.UNIT_METERS + ", " + 
                    DroneSystem.UnitConversion.UNIT_CENTIMETERS + ", " + 
                    DroneSystem.UnitConversion.UNIT_MILLIMETERS + ", " + 
                    DroneSystem.UnitConversion.UNIT_INCHES);
        }
    }

    // Legacy method names for Python compatibility (deprecated in Python but supported)
    /**
     * @deprecated Use {@link #get_flow_velocity_x(String)} instead.
     * This method is provided for backward compatibility with older Python code.
     */
    /**
     * @deprecated Use {@link #getFlowVelocityX(String)} instead.
     * This method is provided for backward compatibility with older Python code.
     */
    @Deprecated
    public double getFlowX(String unit) {
        return getFlowVelocityX(unit);
    }

    /**
     * @deprecated Use {@link #get_flow_velocity_x()} instead.
     * This method is provided for backward compatibility with older Python code.
     */
    /**
     * @deprecated Use {@link #getFlowVelocityX()} instead.
     * This method is provided for backward compatibility with older Python code.
     */
    @Deprecated
    public double getFlowX() {
        return getFlowVelocityX();
    }

    /**
     * @deprecated Use {@link #get_flow_velocity_y(String)} instead.
     * This method is provided for backward compatibility with older Python code.
     */
    /**
     * @deprecated Use {@link #getFlowVelocityY(String)} instead.
     * This method is provided for backward compatibility with older Python code.
     */
    @Deprecated
    public double getFlowY(String unit) {
        return getFlowVelocityY(unit);
    }

    /**
     * @deprecated Use {@link #get_flow_velocity_y()} instead.
     * This method is provided for backward compatibility with older Python code.
     */
    /**
     * @deprecated Use {@link #getFlowVelocityY()} instead.
     * This method is provided for backward compatibility with older Python code.
     */
    @Deprecated
    public double getFlowY() {
        return getFlowVelocityY();
    }

    // =============================================================================
    // PHASE 4: Built-in Flight Patterns (Punch List Item #6)
    // Educational pattern methods for Python-compatible flight patterns
    // =============================================================================

    /**
     * Flies the drone in the shape of a square.
     * 
     * <p>This method provides Python API compatibility for engaging flight patterns
     * that demonstrate coordinated movement sequences.</p>
     * 
     * <p><strong>Educational Context:</strong><br>
     * Students learn about sequential programming, coordinate systems, and
     * combining multiple movements into complex behaviors. The square pattern
     * is an excellent introduction to flight patterns and algorithmic thinking.</p>
     * 
     * <p><strong>Implementation Note:</strong><br>
     * Uses direct motor control (sendControlWhile) to precisely replicate
     * the Python implementation's flight behavior.</p>
     * 
     * @param speed The flight speed (0-100). Higher values create larger squares.
     * @param seconds Duration for each side of the square in seconds.
     * @param direction Direction of flight (1 for right, -1 for left)
     * 
     * @apiNote Equivalent to Python's {@code drone.square(speed, seconds, direction)}
     * @since 1.0
     * @educational
     */
    /**
     * Flight pattern methods have been moved to BasicPatternDrone for educational purposes.
     * 
     * <p><strong>Educational Note:</strong> This method has been moved to {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone} 
     * to demonstrate object-oriented inheritance concepts. To use pattern methods, you should:</p>
     * 
     * <ol>
     *   <li>Import the pattern package: {@code import com.otabi.jcodroneedu.patterns.BasicPatternDrone;}</li>
     *   <li>Create a BasicPatternDrone instance: {@code BasicPatternDrone drone = new BasicPatternDrone();}</li>
     *   <li>Use pattern methods: {@code drone.square(speed, seconds, direction);}</li>
     * </ol>
     * 
     * <p>This design teaches inheritance where {@code BasicPatternDrone} extends {@code Drone} 
     * with additional pattern functionality, demonstrating the "is-a" relationship fundamental 
     * to object-oriented programming.</p>
     * 
     * @param speed The flight speed (0-100). Higher values create larger squares.
     * @param seconds Duration for each side of the square in seconds.
     * @param direction Direction of flight (1 for right, -1 for left)
     * 
     * @throws UnsupportedOperationException Always thrown to guide students to BasicPatternDrone
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#square(int, int, int)} instead
     * @educational This method teaches inheritance by requiring students to use the specialized class
     */
    @Deprecated
    public void square(int speed, int seconds, int direction) {
        throw new UnsupportedOperationException(
            "Pattern methods have been moved to BasicPatternDrone for educational purposes.\n" +
            "To use square patterns:\n" +
            "1. Import: import com.otabi.jcodroneedu.patterns.BasicPatternDrone;\n" +
            "2. Create: BasicPatternDrone drone = new BasicPatternDrone();\n" +
            "3. Use: drone.square(" + speed + ", " + seconds + ", " + direction + ");\n" +
            "\n" +
            "This teaches inheritance where BasicPatternDrone extends Drone with pattern methods."
        );
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#square(int, int)} instead
     */
    @Deprecated
    public void square(int speed, int seconds) {
        square(speed, seconds, 1);
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#square(int)} instead
     */
    @Deprecated
    public void square(int speed) {
        square(speed, 1, 1);
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#square()} instead
     */
    @Deprecated
    public void square() {
        square(60, 1, 1);
    }

    /**
     * Flight pattern methods have been moved to BasicPatternDrone for educational purposes.
     * 
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#triangle(int, int, int)} instead
     * @throws UnsupportedOperationException Always thrown to guide students to BasicPatternDrone
     */
    @Deprecated
    public void triangle(int speed, int seconds, int direction) {
        throw new UnsupportedOperationException(
            "Pattern methods have been moved to BasicPatternDrone for educational purposes.\n" +
            "To use triangle patterns:\n" +
            "1. Import: import com.otabi.jcodroneedu.patterns.BasicPatternDrone;\n" +
            "2. Create: BasicPatternDrone drone = new BasicPatternDrone();\n" +
            "3. Use: drone.triangle(" + speed + ", " + seconds + ", " + direction + ");\n" +
            "\n" +
            "This teaches inheritance where BasicPatternDrone extends Drone with pattern methods."
        );
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#circle(int, int)} instead
     */
    @Deprecated
    public void circle(int speed, int direction) {
        throw new UnsupportedOperationException(
            "Pattern methods have been moved to BasicPatternDrone for educational purposes.\n" +
            "To use circle patterns:\n" +
            "1. Import: import com.otabi.jcodroneedu.patterns.BasicPatternDrone;\n" +
            "2. Create: BasicPatternDrone drone = new BasicPatternDrone();\n" +
            "3. Use: drone.circle(" + speed + ", " + direction + ");\n" +
            "\n" +
            "This teaches inheritance where BasicPatternDrone extends Drone with pattern methods."
        );
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#spiral(int, int, int)} instead
     */
    @Deprecated
    public void spiral(int speed, int seconds, int direction) {
        throw new UnsupportedOperationException(
            "Pattern methods have been moved to BasicPatternDrone for educational purposes.\n" +
            "To use spiral patterns:\n" +
            "1. Import: import com.otabi.jcodroneedu.patterns.BasicPatternDrone;\n" +
            "2. Create: BasicPatternDrone drone = new BasicPatternDrone();\n" +
            "3. Use: drone.spiral(" + speed + ", " + seconds + ", " + direction + ");\n" +
            "\n" +
            "This teaches inheritance where BasicPatternDrone extends Drone with pattern methods."
        );
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#sway(int, int, int)} instead
     */
    @Deprecated
    public void sway(int speed, int seconds, int direction) {
        throw new UnsupportedOperationException(
            "Pattern methods have been moved to BasicPatternDrone for educational purposes.\n" +
            "To use sway patterns:\n" +
            "1. Import: import com.otabi.jcodroneedu.patterns.BasicPatternDrone;\n" +
            "2. Create: BasicPatternDrone drone = new BasicPatternDrone();\n" +
            "3. Use: drone.sway(" + speed + ", " + seconds + ", " + direction + ");\n" +
            "\n" +
            "This teaches inheritance where BasicPatternDrone extends Drone with pattern methods."
        );
    }

    /**
     * Performs a flip maneuver in the specified direction.
     * Requires battery level above 50% for safety.
     * Based on Python CoDrone EDU flip() method.
     * 
     * @param direction The flip direction: "front", "back", "left", "right"
     */
    public void flip(String direction) {
        // Check battery level for safety
        int battery = getBattery();
        if (battery < 50) {
            System.out.println("Warning: Unable to perform flip; battery level is below 50%.");
            // TODO: Add buzzer warning when buzzer methods are implemented
            return;
        }
        
        FlightController.FlightEvent flipMode;
        switch (direction.toLowerCase()) {
            case "back":
                flipMode = FlightController.FlightEvent.FLIP_REAR;
                break;
            case "front":
                flipMode = FlightController.FlightEvent.FLIP_FRONT;
                break;
            case "right":
                flipMode = FlightController.FlightEvent.FLIP_RIGHT;
                break;
            case "left":
                flipMode = FlightController.FlightEvent.FLIP_LEFT;
                break;
            default:
                System.out.println("Invalid flip direction. Use: front, back, left, or right");
                return;
        }
        
        triggerFlightEvent(flipMode);
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#triangleTurn(int, int, int)} instead
     */
    @Deprecated
    public void triangleTurn(int speed, int seconds, int direction) {
        throw new UnsupportedOperationException(
            "Pattern methods have been moved to BasicPatternDrone for educational purposes.\n" +
            "To use triangleTurn patterns:\n" +
            "1. Import: import com.otabi.jcodroneedu.patterns.BasicPatternDrone;\n" +
            "2. Create: BasicPatternDrone drone = new BasicPatternDrone();\n" +
            "3. Use: drone.triangleTurn(" + speed + ", " + seconds + ", " + direction + ");\n" +
            "\n" +
            "This teaches inheritance where BasicPatternDrone extends Drone with pattern methods."
        );
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#triangleTurn()} instead
     */
    @Deprecated
    public void triangleTurn() {
        triangleTurn(60, 2, 1);
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#circleTurn(int, int, int)} instead
     */
    @Deprecated
    public void circleTurn(int speed, int seconds, int direction) {
        throw new UnsupportedOperationException(
            "Pattern methods have been moved to BasicPatternDrone for educational purposes.\n" +
            "To use circleTurn patterns:\n" +
            "1. Import: import com.otabi.jcodroneedu.patterns.BasicPatternDrone;\n" +
            "2. Create: BasicPatternDrone drone = new BasicPatternDrone();\n" +
            "3. Use: drone.circleTurn(" + speed + ", " + seconds + ", " + direction + ");\n" +
            "\n" +
            "This teaches inheritance where BasicPatternDrone extends Drone with pattern methods."
        );
    }

    /**
     * @deprecated Use {@link com.otabi.jcodroneedu.patterns.BasicPatternDrone#circleTurn()} instead
     */
    @Deprecated
    public void circleTurn() {
        circleTurn(30, 1, 1);
    }

    // =============================================================================
    // PHASE 7: LED Control Methods (Punch List Item #7)
    // Educational LED control for classroom identification and debugging
    // =============================================================================

    /**
     * Creates a Color object safely handling unsigned byte values.
     * This works around a limitation in the Color constructor validation.
     * 
     * @param red Red component (0-255)
     * @param green Green component (0-255) 
     * @param blue Blue component (0-255)
     * @return Color object with the specified RGB values
     */
    private Color createColor(int red, int green, int blue) {
        // The Color constructor has a bug where it tries to validate byte parameters
        // as unsigned bytes, but bytes are sign-extended when passed to int parameters.
        // We need to create the Color object directly and set the fields using reflection
        // or find another workaround.
        
        // For now, let's use values 0-127 and scale them if needed
        byte r = (byte) Math.min(127, red * 127 / 255);
        byte g = (byte) Math.min(127, green * 127 / 255);
        byte b = (byte) Math.min(127, blue * 127 / 255);
        
        return new Color(r, g, b);
    }

    /**
     * Educational LED mode constants for easier student use.
     * These provide friendly names for the LED animation modes.
     */
    public static class LEDMode {
        /** Solid color - stays on constantly */
        public static final String SOLID = "solid";
        /** Dimming effect - slowly brightens and dims */
        public static final String DIMMING = "dimming";
        /** Fade in effect - gradually brightens */
        public static final String FADE_IN = "fade_in";
        /** Fade out effect - gradually dims */
        public static final String FADE_OUT = "fade_out";
        /** Blink effect - turns on and off */
        public static final String BLINK = "blink";
        /** Double blink - two quick blinks then pause */
        public static final String DOUBLE_BLINK = "double_blink";
        /** Rainbow effect - cycles through colors */
        public static final String RAINBOW = "rainbow";
    }

    /**
     * Sets the drone LED to a solid color.
     * 
     * <p>This is the most basic LED control method, perfect for early lessons
     * where students learn about RGB color mixing and drone identification.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0101 First Flight:</strong> Identify your drone in a group</li>
     *   <li><strong>L0103 Variables:</strong> Store and use RGB color values</li>
     *   <li><strong>Color Theory:</strong> Learn RGB color mixing (0-255 each)</li>
     *   <li><strong>Debugging:</strong> Visual feedback for program states</li>
     * </ul>
     * 
     * <h3>🌈 Common Colors:</h3>
     * <ul>
     *   <li>Red: {@code setDroneLED(DroneSystem.ColorConstants.RGB_MAX, 0, 0)}</li>
     *   <li>Green: {@code setDroneLED(0, DroneSystem.ColorConstants.RGB_MAX, 0)}</li>
     *   <li>Blue: {@code setDroneLED(0, 0, DroneSystem.ColorConstants.RGB_MAX)}</li>
     *   <li>Yellow: {@code setDroneLED(DroneSystem.ColorConstants.RGB_MAX, DroneSystem.ColorConstants.RGB_MAX, 0)}</li>
     *   <li>Purple: {@code setDroneLED(DroneSystem.ColorConstants.RGB_MAX, 0, DroneSystem.ColorConstants.RGB_MAX)}</li>
     *   <li>White: {@code setDroneLED(DroneSystem.ColorConstants.RGB_MAX, DroneSystem.ColorConstants.RGB_MAX, DroneSystem.ColorConstants.RGB_MAX)}</li>
     * </ul>
     * 
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * @param brightness Overall brightness (0-255, where 255 is full brightness)
     * 
     * @throws IllegalArgumentException if any color value is outside 0-255 range
     * @apiNote Equivalent to Python's {@code drone.set_drone_LED(r, g, b, brightness)}
     * @since 1.0
     * @educational
     */
    public void setDroneLED(int red, int green, int blue, int brightness) {
        // Validate input parameters
        if (red < DroneSystem.ColorConstants.RGB_MIN || red > DroneSystem.ColorConstants.RGB_MAX) {
            throw new IllegalArgumentException("Red must be between " + DroneSystem.ColorConstants.RGB_MIN + 
                " and " + DroneSystem.ColorConstants.RGB_MAX + ", got: " + red);
        }
        if (green < DroneSystem.ColorConstants.RGB_MIN || green > DroneSystem.ColorConstants.RGB_MAX) {
            throw new IllegalArgumentException("Green must be between " + DroneSystem.ColorConstants.RGB_MIN + 
                " and " + DroneSystem.ColorConstants.RGB_MAX + ", got: " + green);
        }
        if (blue < DroneSystem.ColorConstants.RGB_MIN || blue > DroneSystem.ColorConstants.RGB_MAX) {
            throw new IllegalArgumentException("Blue must be between " + DroneSystem.ColorConstants.RGB_MIN + 
                " and " + DroneSystem.ColorConstants.RGB_MAX + ", got: " + blue);
        }
        if (brightness < DroneSystem.ColorConstants.RGB_MIN || brightness > DroneSystem.ColorConstants.RGB_MAX) {
            throw new IllegalArgumentException("Brightness must be between " + DroneSystem.ColorConstants.RGB_MIN + 
                " and " + DroneSystem.ColorConstants.RGB_MAX + ", got: " + brightness);
        }

        // Create color and send to drone
        // Note: Python uses BodyHold mode with brightness as interval
        Color color = createColor(red, green, blue);
        LightDefault lightDefault = new LightDefault(
            com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodyHold, 
            color, 
            (short) brightness
        );
        sendMessage(lightDefault);
        
        // Small delay for command processing
        try {
            Thread.sleep(DroneSystem.CommunicationConstants.LED_COMMAND_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sets the drone LED to a solid color with full brightness.
     * 
     * <p>Simplified version for younger students or quick testing.</p>
     * 
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * 
     * @apiNote Equivalent to Python's {@code drone.set_drone_LED(r, g, b, 255)}
     * @since 1.0
     * @educational
     */
    public void setDroneLED(int red, int green, int blue) {
        setDroneLED(red, green, blue, DroneSystem.ColorConstants.RGB_MAX);
    }

    /**
     * Sets the drone LED to a specific color with animation mode.
     * 
     * <p>This method adds animation effects to the LED, making it more engaging
     * for students and useful for advanced programming concepts like debugging
     * different program states.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>L0106 Conditionals:</strong> Different colors for different states</li>
     *   <li><strong>L0107 Loops:</strong> Use blinking to show loop iterations</li>
     *   <li><strong>Debugging:</strong> Rainbow mode for "searching", solid for "found"</li>
     *   <li><strong>User Experience:</strong> Visual feedback for program status</li>
     * </ul>
     * 
     * <h3>💡 Animation Modes:</h3>
     * <ul>
     *   <li>{@code "solid"} - Steady color (same as setDroneLED)</li>
     *   <li>{@code "dimming"} - Slowly brightens and dims</li>
     *   <li>{@code "fade_in"} - Gradually brightens from off</li>
     *   <li>{@code "fade_out"} - Gradually dims to off</li>
     *   <li>{@code "blink"} - Regular on/off blinking</li>
     *   <li>{@code "double_blink"} - Two quick blinks then pause</li>
     *   <li>{@code "rainbow"} - Cycles through colors (ignores RGB values)</li>
     * </ul>
     * 
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * @param mode Animation mode (use LEDMode constants or strings above)
     * @param speed Animation speed (1-10, where 10 is fastest)
     * 
     * @throws IllegalArgumentException if any parameter is out of range
     * @apiNote Equivalent to Python's {@code drone.set_drone_LED_mode(r, g, b, mode, speed)}
     * @since 1.0
     * @educational
     */
    public void setDroneLEDMode(int red, int green, int blue, String mode, int speed) {
        // Validate input parameters
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException("Red must be between 0 and 255, got: " + red);
        }
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException("Green must be between 0 and 255, got: " + green);
        }
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Blue must be between 0 and 255, got: " + blue);
        }
        if (speed < 1 || speed > 10) {
            throw new IllegalArgumentException("Speed must be between 1 and 10, got: " + speed);
        }
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        // Convert speed to interval (Python-compatible calculation)
        short interval;
        com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone lightMode;
        
        switch (mode.toLowerCase()) {
            case "solid":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodyHold;
                interval = (short) 255; // Full brightness for solid
                break;
            case "dimming":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodyDimming;
                interval = (short) ((11 - speed) * 5); // interval ranges [5,50]
                break;
            case "fade_in":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodySunrise;
                interval = (short) ((11 - speed) * 12); // interval ranges [12,120]
                break;
            case "fade_out":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodySunset;
                interval = (short) ((11 - speed) * 12); // interval ranges [12,120]
                break;
            case "blink":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodyFlicker;
                interval = (short) ((11 - speed) * 100); // interval ranges [100,1000]
                break;
            case "double_blink":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodyFlickerDouble;
                interval = (short) ((11 - speed) * 60); // interval ranges [60,600]
                break;
            case "rainbow":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodyRainbow;
                interval = (short) ((11 - speed) * 7); // interval ranges [7,70]
                break;
            default:
                throw new IllegalArgumentException("Invalid LED mode: " + mode + 
                    ". Valid modes are: solid, dimming, fade_in, fade_out, blink, double_blink, rainbow");
        }

        // Create color and send to drone
        Color color = createColor(red, green, blue);
        LightDefault lightDefault = new LightDefault(lightMode, color, interval);
        sendMessage(lightDefault);
        
        // Small delay for command processing
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Turns off the drone LED.
     * 
     * <p>This method turns off all LED lights on the drone, returning it to its
     * default state. Useful for cleanup or when switching between different
     * program modes.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Program Cleanup:</strong> Turn off LEDs at program end</li>
     *   <li><strong>State Transitions:</strong> Clear visual state before new mode</li>
     *   <li><strong>Power Conservation:</strong> Reduce power consumption</li>
     * </ul>
     * 
     * @apiNote Equivalent to Python's {@code drone.drone_LED_off()}
     * @since 1.0
     * @educational
     */
    public void droneLEDOff() {
        Color color = createColor(0, 0, 0);
        LightDefault lightDefault = new LightDefault(
            com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodyHold, 
            color, 
            (short) 0
        );
        sendMessage(lightDefault);
        
        // Small delay for command processing
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sets the controller LED to a solid color.
     * 
     * <p>Controls the LED on the controller (remote control) rather than the drone.
     * This is useful for team identification or indicating controller status.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Team Identification:</strong> Each student has a different controller color</li>
     *   <li><strong>Status Indication:</strong> Green for ready, red for error, etc.</li>
     *   <li><strong>Debugging:</strong> Controller LED for one state, drone LED for another</li>
     * </ul>
     * 
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * @param brightness Overall brightness (0-255)
     * 
     * @throws IllegalArgumentException if any color value is outside 0-255 range
     * @apiNote Equivalent to Python's {@code drone.set_controller_LED(r, g, b, brightness)}
     * @since 1.0
     * @educational
     */
    public void setControllerLED(int red, int green, int blue, int brightness) {
        // Validate input parameters
        if (red < DroneSystem.ColorConstants.RGB_MIN || red > DroneSystem.ColorConstants.RGB_MAX) {
            throw new IllegalArgumentException("Red must be between " + DroneSystem.ColorConstants.RGB_MIN + 
                " and " + DroneSystem.ColorConstants.RGB_MAX + ", got: " + red);
        }
        if (green < DroneSystem.ColorConstants.RGB_MIN || green > DroneSystem.ColorConstants.RGB_MAX) {
            throw new IllegalArgumentException("Green must be between " + DroneSystem.ColorConstants.RGB_MIN + 
                " and " + DroneSystem.ColorConstants.RGB_MAX + ", got: " + green);
        }
        if (blue < DroneSystem.ColorConstants.RGB_MIN || blue > DroneSystem.ColorConstants.RGB_MAX) {
            throw new IllegalArgumentException("Blue must be between " + DroneSystem.ColorConstants.RGB_MIN + 
                " and " + DroneSystem.ColorConstants.RGB_MAX + ", got: " + blue);
        }
        if (brightness < DroneSystem.ColorConstants.RGB_MIN || brightness > DroneSystem.ColorConstants.RGB_MAX) {
            throw new IllegalArgumentException("Brightness must be between " + DroneSystem.ColorConstants.RGB_MIN + 
                " and " + DroneSystem.ColorConstants.RGB_MAX + ", got: " + brightness);
        }

        // Create color and send to controller
        Color color = createColor(red, green, blue);
        LightDefault lightDefault = new LightDefault(
            com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodyHold, 
            color, 
            (short) brightness
        );
        sendMessage(lightDefault, DeviceType.Base, DeviceType.Controller);
        
        // Small delay for command processing
        try {
            Thread.sleep(DroneSystem.CommunicationConstants.LED_COMMAND_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sets the controller LED to a solid color with full brightness.
     * 
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * 
     * @apiNote Equivalent to Python's {@code drone.set_controller_LED(r, g, b, 255)}
     * @since 1.0
     * @educational
     */
    public void setControllerLED(int red, int green, int blue) {
        setControllerLED(red, green, blue, DroneSystem.ColorConstants.RGB_MAX);
    }

    /**
     * Turns off the controller LED.
     * 
     * <p>Turns off all LED lights on the controller, returning it to its default state.</p>
     * 
     * @apiNote Equivalent to Python's {@code drone.controller_LED_off()}
     * @since 1.0
     * @educational
     */
    public void controllerLEDOff() {
        Color color = createColor(0, 0, 0);
        LightDefault lightDefault = new LightDefault(
            com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodyHold, 
            color, 
            (short) 0
        );
        sendMessage(lightDefault, DeviceType.Base, DeviceType.Controller);
        
        // Small delay for command processing
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sets the controller LED to a specific color with animation mode.
     * 
     * <p>This method adds animation effects to the controller LED, enabling 
     * differentiated visual feedback between drone and controller states.
     * Perfect for team identification and multi-device programs.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Team Coordination:</strong> Different controller animations for different teams</li>
     *   <li><strong>Status Indication:</strong> Controller shows ready/busy/error states</li>
     *   <li><strong>Debugging:</strong> Controller LED for program state, drone LED for flight state</li>
     *   <li><strong>User Interface:</strong> Visual feedback for user interactions</li>
     * </ul>
     * 
     * <h3>💡 Animation Modes:</h3>
     * <ul>
     *   <li>{@code "solid"} - Steady color (same as setControllerLED)</li>
     *   <li>{@code "dimming"} - Slowly brightens and dims</li>
     *   <li>{@code "fade_in"} - Gradually brightens from off</li>
     *   <li>{@code "fade_out"} - Gradually dims to off</li>
     *   <li>{@code "blink"} - Regular on/off blinking</li>
     *   <li>{@code "double_blink"} - Two quick blinks then pause</li>
     *   <li>{@code "rainbow"} - Cycles through colors (ignores RGB values)</li>
     * </ul>
     * 
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * @param mode Animation mode (use LEDMode constants or strings above)
     * @param speed Animation speed (1-10, where 10 is fastest)
     * 
     * @throws IllegalArgumentException if any parameter is out of range
     * @apiNote Equivalent to Python's {@code drone.set_controller_LED_mode(r, g, b, mode, speed)}
     * @since 1.0
     * @educational
     */
    public void setControllerLEDMode(int red, int green, int blue, String mode, int speed) {
        // Validate input parameters
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException("Red must be between 0 and 255, got: " + red);
        }
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException("Green must be between 0 and 255, got: " + green);
        }
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Blue must be between 0 and 255, got: " + blue);
        }
        if (speed < 1 || speed > 10) {
            throw new IllegalArgumentException("Speed must be between 1 and 10, got: " + speed);
        }
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        // Convert speed to interval (Python-compatible calculation)
        short interval;
        com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController lightMode;
        
        switch (mode.toLowerCase()) {
            case "solid":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodyHold;
                interval = (short) 255; // Full brightness for solid
                break;
            case "dimming":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodyDimming;
                interval = (short) ((11 - speed) * 5); // interval ranges [5,50]
                break;
            case "fade_in":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodySunrise;
                interval = (short) ((11 - speed) * 12); // interval ranges [12,120]
                break;
            case "fade_out":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodySunset;
                interval = (short) ((11 - speed) * 12); // interval ranges [12,120]
                break;
            case "blink":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodyFlicker;
                interval = (short) ((11 - speed) * 100); // interval ranges [100,1000]
                break;
            case "double_blink":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodyFlickerDouble;
                interval = (short) ((11 - speed) * 60); // interval ranges [60,600]
                break;
            case "rainbow":
                lightMode = com.otabi.jcodroneedu.protocol.lightcontroller.LightModesController.BodyRainbow;
                interval = (short) ((11 - speed) * 7); // interval ranges [7,70]
                break;
            default:
                throw new IllegalArgumentException("Invalid LED mode: " + mode + 
                    ". Valid modes are: solid, dimming, fade_in, fade_out, blink, double_blink, rainbow");
        }

        // Create color and send to controller
        Color color = createColor(red, green, blue);
        LightDefault lightDefault = new LightDefault(lightMode, color, interval);
        sendMessage(lightDefault, DeviceType.Base, DeviceType.Controller);
        
        // Small delay for command processing
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // =============================================================================
    // Educational Helper Methods - Simple Colors for Young Students
    // =============================================================================

    /**
     * Sets the drone LED to red.
     * 
     * <p>Simple helper method for young students who are just learning.
     * Perfect for first programs and identification.</p>
     * 
     * @educational
     */
    public void setDroneLEDRed() {
        setDroneLED(DroneSystem.ColorConstants.RGB_RED[0], 
                   DroneSystem.ColorConstants.RGB_RED[1], 
                   DroneSystem.ColorConstants.RGB_RED[2]);
    }

    /**
     * Sets the drone LED to green.
     * 
     * @educational
     */
    public void setDroneLEDGreen() {
        setDroneLED(DroneSystem.ColorConstants.RGB_GREEN[0], 
                   DroneSystem.ColorConstants.RGB_GREEN[1], 
                   DroneSystem.ColorConstants.RGB_GREEN[2]);
    }

    /**
     * Sets the drone LED to blue.
     * 
     * @educational
     */
    public void setDroneLEDBlue() {
        setDroneLED(DroneSystem.ColorConstants.RGB_BLUE[0], 
                   DroneSystem.ColorConstants.RGB_BLUE[1], 
                   DroneSystem.ColorConstants.RGB_BLUE[2]);
    }

    /**
     * Sets the drone LED to yellow.
     * 
     * @educational
     */
    public void setDroneLEDYellow() {
        setDroneLED(DroneSystem.ColorConstants.RGB_YELLOW[0], 
                   DroneSystem.ColorConstants.RGB_YELLOW[1], 
                   DroneSystem.ColorConstants.RGB_YELLOW[2]);
    }

    /**
     * Sets the drone LED to purple.
     * 
     * @educational
     */
    public void setDroneLEDPurple() {
        setDroneLED(DroneSystem.ColorConstants.RGB_PURPLE[0], 
                   DroneSystem.ColorConstants.RGB_PURPLE[1], 
                   DroneSystem.ColorConstants.RGB_PURPLE[2]);
    }

    /**
     * Sets the drone LED to white.
     * 
     * @educational
     */
    public void setDroneLEDWhite() {
        setDroneLED(DroneSystem.ColorConstants.RGB_WHITE[0], 
                   DroneSystem.ColorConstants.RGB_WHITE[1], 
                   DroneSystem.ColorConstants.RGB_WHITE[2]);
    }

    /**
     * Sets the drone LED to orange.
     * 
     * @educational
     */
    public void setDroneLEDOrange() {
        setDroneLED(DroneSystem.ColorConstants.RGB_ORANGE[0], 
                   DroneSystem.ColorConstants.RGB_ORANGE[1], 
                   DroneSystem.ColorConstants.RGB_ORANGE[2]);
    }

    // ========================================
    // Buzzer Control Methods
    // ========================================

    /**
     * Plays a note using the drone's buzzer for a specified duration.
     * The drone buzzer provides audio feedback for educational programming.
     * 
     * @param note The musical note to play (Note enum) or frequency (Integer)
     * @param duration The duration to play the note in milliseconds
     * @throws IllegalArgumentException if note is neither Note nor Integer, or if duration is negative
     * @educational
     */
    public void droneBuzzer(Object note, int duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration must be non-negative");
        }

        BuzzerMode mode;
        int value;
        
        if (note instanceof Note) {
            mode = BuzzerMode.SCALE;
            value = ((Note) note).getValue();
        } else if (note instanceof Integer) {
            mode = BuzzerMode.HZ;
            value = (Integer) note;
        } else {
            throw new IllegalArgumentException("Note must be a Note enum or Integer frequency");
        }

        // Create buzzer command
        Buzzer buzzer = new Buzzer(mode, value, duration);

        Header header = new Header();
        header.setDataType(DataType.Buzzer);
        header.setLength(buzzer.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Drone);

        transfer(header, buzzer);
        
        // Sleep for the duration to match Python behavior
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Send mute command to stop the buzzer
        sendBuzzerMute(DeviceType.Drone, 10);
    }

    /**
     * Plays a note using the controller's buzzer for a specified duration.
     * The controller buzzer provides local audio feedback for students.
     * 
     * @param note The musical note to play (Note enum) or frequency (Integer)
     * @param duration The duration to play the note in milliseconds
     * @throws IllegalArgumentException if note is neither Note nor Integer, or if duration is negative
     * @educational
     */
    public void controllerBuzzer(Object note, int duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration must be non-negative");
        }

        BuzzerMode mode;
        int value;
        
        if (note instanceof Note) {
            mode = BuzzerMode.SCALE;
            value = ((Note) note).getValue();
        } else if (note instanceof Integer) {
            mode = BuzzerMode.HZ;
            value = (Integer) note;
        } else {
            throw new IllegalArgumentException("Note must be a Note enum or Integer frequency");
        }

        sendBuzzer(mode, value, duration);
        
        // Sleep for the duration to match Python behavior
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Send mute command to stop the buzzer
        sendBuzzerMute(DeviceType.Controller, 10);
    }

    /**
     * Starts the drone buzzer playing a note indefinitely.
     * Call stop_drone_buzzer() to stop the sound.
     * 
     * @param note The musical note to play (Note enum) or frequency (Integer)
     * @throws IllegalArgumentException if note is neither Note nor Integer
     * @educational
     */
    public void startDroneBuzzer(Object note) {
        BuzzerMode mode;
        int value;
        
        if (note instanceof Note) {
            mode = BuzzerMode.SCALE;
            value = ((Note) note).getValue();
        } else if (note instanceof Integer) {
            mode = BuzzerMode.HZ;
            value = (Integer) note;
        } else {
            throw new IllegalArgumentException("Note must be a Note enum or Integer frequency");
        }

        Buzzer buzzer = new Buzzer(mode, value, 65535); // Maximum duration

        Header header = new Header();
        header.setDataType(DataType.Buzzer);
        header.setLength(buzzer.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Drone);

        transfer(header, buzzer);
        
        // Small delay to ensure command is processed
        try {
            Thread.sleep(70);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stops the drone buzzer.
     * 
     * @educational
     */
    public void stopDroneBuzzer() {
        sendBuzzerMute(DeviceType.Drone, 1);
        
        // Small delay to ensure command is processed
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Starts the controller buzzer playing a note indefinitely.
     * Call stop_controller_buzzer() to stop the sound.
     * 
     * @param note The musical note to play (Note enum) or frequency (Integer)
     * @throws IllegalArgumentException if note is neither Note nor Integer
     * @educational
     */
    public void startControllerBuzzer(Object note) {
        BuzzerMode mode;
        int value;
        
        if (note instanceof Note) {
            mode = BuzzerMode.SCALE;
            value = ((Note) note).getValue();
        } else if (note instanceof Integer) {
            mode = BuzzerMode.HZ;
            value = (Integer) note;
        } else {
            throw new IllegalArgumentException("Note must be a Note enum or Integer frequency");
        }

        sendBuzzer(mode, value, 65535); // Maximum duration
        
        // Small delay to ensure command is processed
        try {
            Thread.sleep(70);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stops the controller buzzer.
     * 
     * @educational
     */
    public void stopControllerBuzzer() {
        sendBuzzerMute(DeviceType.Controller, 1);
        
        // Small delay to ensure command is processed
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ============================================================================
    // Combined LED + Buzzer Methods
    // ============================================================================

    /**
     * Pings the drone using buzzer and LED to help locate it visually and audibly.
     * 
     * <p>This is a "find my drone" feature that makes the drone easy to locate in a crowded
     * space or if it's lost. The drone will blink its LED in a double-blink pattern and
     * play three buzzer beeps. If RGB values are not specified, a random color is used
     * to make it more noticeable.</p>
     * 
     * <h3>🎯 How it Works:</h3>
     * <ol>
     *   <li>Sets LED to double-blink mode with specified or random color</li>
     *   <li>Plays three buzzer beeps (200ms each)</li>
     *   <li>Sets LED to solid color at full brightness</li>
     * </ol>
     * 
     * <h3>💡 Educational Use (L0304):</h3>
     * <pre>{@code
     * // Find drone with random color
     * drone.pair();
     * drone.ping();  // Random bright color + beeps
     * 
     * // Find drone with specific color
     * drone.ping(255, 0, 0);  // Red
     * 
     * // Use in a search scenario
     * System.out.println("Locating drone...");
     * drone.ping(0, 255, 0);  // Green = found!
     * 
     * // Multiple drones - different colors
     * drone1.ping(255, 0, 0);    // Red drone
     * drone2.ping(0, 0, 255);    // Blue drone
     * drone3.ping(255, 255, 0);  // Yellow drone
     * }</pre>
     * 
     * <h3>🔍 Use Cases:</h3>
     * <ul>
     *   <li><strong>Lost Drone:</strong> Locate drone in a cluttered area</li>
     *   <li><strong>Drone Identification:</strong> Identify which drone is which in multi-drone setups</li>
     *   <li><strong>Status Indication:</strong> Signal readiness or completion</li>
     *   <li><strong>Debugging:</strong> Confirm program is running on the correct drone</li>
     * </ul>
     * 
     * <p><strong>⚠️ Note:</strong> If any RGB value is null or not provided, ALL colors
     * will be randomly generated to create a unique, easily visible color.</p>
     * 
     * @param red Red component (0-255), or null for random
     * @param green Green component (0-255), or null for random
     * @param blue Blue component (0-255), or null for random
     * @educational
     * @pythonEquivalent ping(r, g, b)
     */
    public void ping(Integer red, Integer green, Integer blue) {
        // Generate random color if any component is not specified
        if (red == null || green == null || blue == null) {
            Random random = new Random();
            red = random.nextInt(256);
            green = random.nextInt(256);
            blue = random.nextInt(256);
        }
        
        // Set double-blink LED mode (speed 7 for moderate blink rate)
        setDroneLEDMode(red, green, blue, "double_blink", 7);
        
        // Play three buzzer beeps (200ms each, 200ms pause between)
        for (int i = 0; i < 3; i++) {
            drone_buzzer(1000, 200);  // 1000 Hz for 200ms
            
            // Pause between beeps (except after last one)
            if (i < 2) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        // Set to solid color at full brightness
        setDroneLED(red, green, blue, 255);
    }

    /**
     * Pings the drone using buzzer and LED with a random color.
     * Convenience overload for the most common use case.
     * 
     * @educational
     * @pythonEquivalent ping()
     */
    public void ping() {
        ping(null, null, null);
    }

    // ============================================================================
    // Buzzer Sequence Methods - Predefined Sound Patterns
    // ============================================================================

    /**
     * Plays a predefined buzzer sequence on the drone.
     * 
     * <p>This method provides access to built-in sound patterns ("success", "warning", "error")
     * and any custom sequences registered via {@link #registerBuzzerSequence(String, BuzzerSequence)}.
     * It matches the Python API's drone_buzzer_sequence(kind) functionality.</p>
     * 
     * <h3>🎵 Built-in Sequences:</h3>
     * <ul>
     *   <li><strong>"success"</strong> - Two ascending tones (1600 Hz → 2200 Hz)</li>
     *   <li><strong>"warning"</strong> - Three 800 Hz beeps with pauses</li>
     *   <li><strong>"error"</strong> - Three short 150 Hz beeps</li>
     * </ul>
     * 
     * <h3>💡 Educational Use (L0301):</h3>
     * <pre>{@code
     * // Use built-in sequences for feedback
     * drone.takeoff();
     * 
     * if (drone.getFrontRange() < 30) {
     *     drone.droneBuzzerSequence("warning");  // Object detected
     *     drone.land();
     * } else {
     *     drone.moveForward(50, 2.0);
     *     drone.droneBuzzerSequence("success");  // Mission complete
     * }
     * 
     * // Use custom sequence
     * BuzzerSequence fanfare = new BuzzerSequence.Builder()
     *     .addNote(523, 200)  // C
     *     .addNote(659, 200)  // E
     *     .addNote(784, 400)  // G
     *     .build("fanfare");
     * 
     * drone.registerBuzzerSequence("fanfare", fanfare);
     * drone.droneBuzzerSequence("fanfare");
     * }</pre>
     * 
     * @param sequenceName The name of the sequence to play ("success", "warning", "error", or custom)
     * @throws IllegalArgumentException if sequence name is not registered
     * @see #registerBuzzerSequence(String, BuzzerSequence)
     * @see #controllerBuzzerSequence(String)
     * @educational
     * @pythonEquivalent drone_buzzer_sequence(kind)
     */
    public void droneBuzzerSequence(String sequenceName) {
        BuzzerSequenceRegistry registry = BuzzerSequenceRegistry.getInstance();
        BuzzerSequence sequence = registry.get(sequenceName);
        
        if (sequence == null) {
            throw new IllegalArgumentException(
                "Unknown buzzer sequence: '" + sequenceName + "'. " +
                "Available sequences: " + registry.list());
        }
        
        playSequence(sequence, DeviceType.Drone);
    }

    /**
     * Plays a predefined buzzer sequence on the controller.
     * 
     * <p>This method provides access to built-in sound patterns ("success", "warning", "error")
     * and any custom sequences registered via {@link #registerBuzzerSequence(String, BuzzerSequence)}.
     * It matches the Python API's controller_buzzer_sequence(kind) functionality.</p>
     * 
     * <h3>🎵 Built-in Sequences:</h3>
     * <ul>
     *   <li><strong>"success"</strong> - Two ascending tones (1600 Hz → 2200 Hz)</li>
     *   <li><strong>"warning"</strong> - Three 800 Hz beeps with pauses</li>
     *   <li><strong>"error"</strong> - Three short 150 Hz beeps</li>
     * </ul>
     * 
     * <h3>💡 Educational Use (L0302):</h3>
     * <pre>{@code
     * // Provide local feedback without drone
     * drone.pair();
     * 
     * System.out.println("Testing controller...");
     * drone.controllerBuzzerSequence("success");
     * 
     * System.out.println("Press button to continue...");
     * // Wait for button press
     * drone.controllerBuzzerSequence("warning");
     * 
     * // Custom notification sound
     * BuzzerSequence doorbell = new BuzzerSequence.Builder()
     *     .addNote(659, 300)  // E
     *     .addPause(100)
     *     .addNote(523, 300)  // C
     *     .build("doorbell");
     * 
     * drone.registerBuzzerSequence("doorbell", doorbell);
     * drone.controllerBuzzerSequence("doorbell");
     * }</pre>
     * 
     * @param sequenceName The name of the sequence to play ("success", "warning", "error", or custom)
     * @throws IllegalArgumentException if sequence name is not registered
     * @see #registerBuzzerSequence(String, BuzzerSequence)
     * @see #droneBuzzerSequence(String)
     * @educational
     * @pythonEquivalent controller_buzzer_sequence(kind)
     */
    public void controllerBuzzerSequence(String sequenceName) {
        BuzzerSequenceRegistry registry = BuzzerSequenceRegistry.getInstance();
        BuzzerSequence sequence = registry.get(sequenceName);
        
        if (sequence == null) {
            throw new IllegalArgumentException(
                "Unknown buzzer sequence: '" + sequenceName + "'. " +
                "Available sequences: " + registry.list());
        }
        
        playSequence(sequence, DeviceType.Controller);
    }

    /**
     * Registers a custom buzzer sequence for later use.
     * 
     * <p>This allows students to create and register their own sound patterns that can then
     * be played using {@link #droneBuzzerSequence(String)} or {@link #controllerBuzzerSequence(String)}.
     * Registered sequences persist for the lifetime of the Drone object.</p>
     * 
     * <h3>🎓 Educational Value (L0303):</h3>
     * Students learn:
     * <ul>
     *   <li>Composition - building complex behaviors from simple parts</li>
     *   <li>Registration pattern - storing objects for later retrieval</li>
     *   <li>Reusability - define once, use many times</li>
     * </ul>
     * 
     * <h3>💡 Usage Example:</h3>
     * <pre>{@code
     * // Create a custom alarm sequence
     * BuzzerSequence alarm = new BuzzerSequence.Builder()
     *     .addNote(1000, 100)
     *     .addPause(50)
     *     .addNote(1000, 100)
     *     .addPause(50)
     *     .addNote(1000, 100)
     *     .addPause(200)
     *     .addNote(1500, 300)
     *     .build("alarm");
     * 
     * // Register it
     * drone.registerBuzzerSequence("alarm", alarm);
     * 
     * // Use it multiple times
     * drone.takeoff();
     * for (int i = 0; i < 3; i++) {
     *     drone.droneBuzzerSequence("alarm");
     *     drone.hover(1.0);
     * }
     * drone.land();
     * }</pre>
     * 
     * <p><strong>⚠️ Note:</strong> If a sequence with the given name already exists
     * (including built-ins like "success"), it will be replaced. This allows overriding
     * default sequences if desired.</p>
     * 
     * @param name The name to register the sequence under
     * @param sequence The buzzer sequence to register
     * @throws IllegalArgumentException if name is null/empty or sequence is null
     * @see BuzzerSequence.Builder
     * @see #droneBuzzerSequence(String)
     * @see #controllerBuzzerSequence(String)
     * @educational
     */
    public void registerBuzzerSequence(String name, BuzzerSequence sequence) {
        BuzzerSequenceRegistry.getInstance().register(name, sequence);
    }

    /**
     * Helper method to play a buzzer sequence on the specified device.
     * 
     * @param sequence The sequence to play
     * @param device The target device (Drone or Controller)
     */
    private void playSequence(BuzzerSequence sequence, DeviceType device) {
        // Add initial delay like Python version (200ms)
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        
        // Play each note in the sequence
        for (BuzzerSequence.BuzzerNote note : sequence.getNotes()) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            
            // Play the note (or silence if frequency is 0)
            if (note.frequency > 0) {
                Buzzer buzzer = new Buzzer(BuzzerMode.HZ, note.frequency, note.durationMs);
                
                Header header = new Header();
                header.setDataType(DataType.Buzzer);
                header.setLength(buzzer.getSize());
                header.setFrom(DeviceType.Base);
                header.setTo(device);
                
                transfer(header, buzzer);
            }
            
            // Wait for note duration + delay
            try {
                Thread.sleep(note.durationMs + note.delayAfterMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // ========================================
    // Private Buzzer Helper Methods
    // ========================================

    /**
     * Sends a buzzer command to the controller.
     * 
     * @param mode The buzzer mode (SCALE for notes, HZ for frequencies)
     * @param value The note value or frequency
     * @param duration The duration in milliseconds
     */
    private void sendBuzzer(BuzzerMode mode, int value, int duration) {
        Buzzer buzzer = new Buzzer(mode, value, duration);
        
        Header header = new Header();
        header.setDataType(DataType.Buzzer);
        header.setLength(buzzer.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, buzzer);
    }

    /**
     * Sends a mute command to the specified device.
     * 
     * @param target The target device (Drone or Controller)
     * @param duration The mute duration in milliseconds
     */
    private void sendBuzzerMute(DeviceType target, int duration) {
        Buzzer buzzer = new Buzzer(BuzzerMode.MUTE, Note.MUTE.getValue(), duration);
        
        Header header = new Header();
        header.setDataType(DataType.Buzzer);
        header.setLength(buzzer.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(target);

        transfer(header, buzzer);
    }

    // ========================================
    // Controller Display Methods
    // ========================================

    /**
     * Clears the entire controller display screen.
     * 
     * @param pixel The pixel type to use for clearing (WHITE or BLACK)
     * @educational
     */
    public void controllerClearScreen(DisplayPixel pixel) {
        DisplayClearAll clearCommand = new DisplayClearAll(pixel);
        
        Header header = new Header();
        header.setDataType(DataType.DisplayClear);
        header.setLength(clearCommand.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, clearCommand);
    }

    /**
     * Clears the entire controller display screen with white pixels.
     * 
     * @educational
     */
    public void controllerClearScreen() {
        controllerClearScreen(DisplayPixel.WHITE);
    }

    /**
     * Draws a point on the controller display.
     * 
     * @param x X coordinate (0-127)
     * @param y Y coordinate (0-63)
     * @param pixel The pixel type to draw (BLACK, WHITE, INVERSE, OUTLINE)
     * @educational
     */
    public void controllerDrawPoint(int x, int y, DisplayPixel pixel) {
        DisplayDrawPoint drawCommand = new DisplayDrawPoint(x, y, pixel);
        
        Header header = new Header();
        header.setDataType(DataType.DisplayDrawPoint);
        header.setLength(drawCommand.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, drawCommand);
    }

    /**
     * Draws a black point on the controller display.
     * 
     * @param x X coordinate (0-127)
     * @param y Y coordinate (0-63)
     * @educational
     */
    public void controllerDrawPoint(int x, int y) {
        controllerDrawPoint(x, y, DisplayPixel.BLACK);
    }

    /**
     * Draws a line on the controller display.
     * 
     * @param x1 Starting X coordinate
     * @param y1 Starting Y coordinate
     * @param x2 Ending X coordinate
     * @param y2 Ending Y coordinate
     * @param pixel The pixel type to draw
     * @param line The line style (SOLID, DOTTED, DASHED)
     * @educational
     */
    public void controllerDrawLine(int x1, int y1, int x2, int y2, DisplayPixel pixel, DisplayLine line) {
        DisplayDrawLine drawCommand = new DisplayDrawLine(x1, y1, x2, y2, pixel, line);
        
        Header header = new Header();
        header.setDataType(DataType.DisplayDrawLine);
        header.setLength(drawCommand.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, drawCommand);
    }

    /**
     * Draws a solid black line on the controller display.
     * 
     * @param x1 Starting X coordinate
     * @param y1 Starting Y coordinate
     * @param x2 Ending X coordinate
     * @param y2 Ending Y coordinate
     * @educational
     */
    public void controllerDrawLine(int x1, int y1, int x2, int y2) {
        controllerDrawLine(x1, y1, x2, y2, DisplayPixel.BLACK, DisplayLine.SOLID);
    }

    /**
     * Draws a rectangle on the controller display.
     * 
     * @param x X coordinate of top-left corner
     * @param y Y coordinate of top-left corner
     * @param width Width of the rectangle
     * @param height Height of the rectangle
     * @param pixel The pixel type to draw
     * @param filled Whether to fill the rectangle
     * @param line The line style for outline
     * @educational
     */
    public void controllerDrawRectangle(int x, int y, int width, int height, DisplayPixel pixel, boolean filled, DisplayLine line) {
        DisplayDrawRect drawCommand = new DisplayDrawRect(x, y, width, height, pixel, filled, line);
        
        Header header = new Header();
        header.setDataType(DataType.DisplayDrawRect);
        header.setLength(drawCommand.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, drawCommand);
    }

    /**
     * Draws a solid black rectangle outline on the controller display.
     * 
     * @param x X coordinate of top-left corner
     * @param y Y coordinate of top-left corner
     * @param width Width of the rectangle
     * @param height Height of the rectangle
     * @educational
     */
    public void controllerDrawRectangle(int x, int y, int width, int height) {
        controllerDrawRectangle(x, y, width, height, DisplayPixel.BLACK, false, DisplayLine.SOLID);
    }

    /**
     * Draws a circle on the controller display.
     * 
     * @param x X coordinate of center
     * @param y Y coordinate of center
     * @param radius Radius of the circle
     * @param pixel The pixel type to draw
     * @param filled Whether to fill the circle
     * @educational
     */
    public void controllerDrawCircle(int x, int y, int radius, DisplayPixel pixel, boolean filled) {
        DisplayDrawCircle drawCommand = new DisplayDrawCircle(x, y, radius, pixel, filled);
        
        Header header = new Header();
        header.setDataType(DataType.DisplayDrawCircle);
        header.setLength(drawCommand.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, drawCommand);
    }

    /**
     * Draws a solid black filled circle on the controller display.
     * 
     * @param x X coordinate of center
     * @param y Y coordinate of center
     * @param radius Radius of the circle
     * @educational
     */
    public void controllerDrawCircle(int x, int y, int radius) {
        controllerDrawCircle(x, y, radius, DisplayPixel.BLACK, true);
    }

    /**
     * Draws text on the controller display.
     * 
     * @param x X coordinate for text position
     * @param y Y coordinate for text position
     * @param message The text message to display
     * @param font The font to use (LIBERATION_MONO_5X8 or LIBERATION_MONO_10X16)
     * @param pixel The pixel type to draw
     * @educational
     */
    public void controllerDrawString(int x, int y, String message, DisplayFont font, DisplayPixel pixel) {
        DisplayDrawString drawCommand = new DisplayDrawString(x, y, font, pixel, message);
        
        Header header = new Header();
        header.setDataType(DataType.DisplayDrawString);
        header.setLength(drawCommand.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, drawCommand);
    }

    /**
     * Draws black text on the controller display using the default font.
     * 
     * @param x X coordinate for text position
     * @param y Y coordinate for text position
     * @param message The text message to display
     * @educational
     */
    public void controllerDrawString(int x, int y, String message) {
        controllerDrawString(x, y, message, DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.BLACK);
    }

    /**
     * Clears a specific rectangular area on the controller display.
     * 
     * @param x X coordinate of top-left corner
     * @param y Y coordinate of top-left corner
     * @param width Width of area to clear
     * @param height Height of area to clear
     * @param pixel The pixel type to use for clearing
     * @educational
     */
    public void controllerClearArea(int x, int y, int width, int height, DisplayPixel pixel) {
        DisplayClear clearCommand = new DisplayClear(x, y, width, height, pixel);
        
        Header header = new Header();
        header.setDataType(DataType.DisplayClear);
        header.setLength(clearCommand.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, clearCommand);
    }

    /**
     * Clears a specific rectangular area on the controller display with white pixels.
     * 
     * @param x X coordinate of top-left corner
     * @param y Y coordinate of top-left corner
     * @param width Width of area to clear
     * @param height Height of area to clear
     * @educational
     */
    public void controllerClearArea(int x, int y, int width, int height) {
        controllerClearArea(x, y, width, height, DisplayPixel.WHITE);
    }

    /**
     * Inverts the pixels in a specific rectangular area on the controller display.
     * 
     * @param x X coordinate of top-left corner
     * @param y Y coordinate of top-left corner
     * @param width Width of area to invert
     * @param height Height of area to invert
     * @educational
     */
    public void controllerInvertArea(int x, int y, int width, int height) {
        DisplayInvert invertCommand = new DisplayInvert(x, y, width, height);
        
        Header header = new Header();
        header.setDataType(DataType.DisplayInvert);
        header.setLength(invertCommand.getSize());
        header.setFrom(DeviceType.Base);
        header.setTo(DeviceType.Controller);

        transfer(header, invertCommand);
    }


    // ========================================
    // Controller Input Methods
    // ========================================

    // =================================================================================
    // --- Controller Input API ---
    // =================================================================================

    /**
     * Returns the current button data array (Python API compatibility).
     * 
     * <p>Contains [timestamp, button_flags, event_name].</p>
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getButtonDataObject()} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     *
     * @return Object array with button state information
     * @educational
     * @see #getButtonDataObject() Recommended Java alternative
     */
    public Object[] getButtonData() {
        return controllerInputManager.getButtonDataArray();
    }

    /**
     * Returns the current joystick data array (Python API compatibility).
     * 
     * <p>Contains [timestamp, left_x, left_y, left_dir, left_event, right_x, right_y, right_dir, right_event].</p>
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getJoystickDataObject()} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     *
     * @return int array with joystick state information
     * @educational
     * @see #getJoystickDataObject() Recommended Java alternative
     */
    public int[] getJoystickData() {
        return controllerInputManager.getJoystickDataArray();
    }

    /**
     * Gets the left joystick X (horizontal) value.
     *
     * @return X-axis value from -100 to 100, where 0 is neutral
     * @educational
     */
    public int getLeftJoystickX() {
        return controllerInputManager.getLeftJoystickX();
    }

    /**
     * Gets the left joystick Y (vertical) value.
     *
     * @return Y-axis value from -100 to 100, where 0 is neutral
     * @educational
     */
    public int getLeftJoystickY() {
        return controllerInputManager.getLeftJoystickY();
    }

    /**
     * Gets the right joystick X (horizontal) value.
     *
     * @return X-axis value from -100 to 100, where 0 is neutral
     * @educational
     */
    public int getRightJoystickX() {
        return controllerInputManager.getRightJoystickX();
    }

    /**
     * Gets the right joystick Y (vertical) value.
     *
     * @return Y-axis value from -100 to 100, where 0 is neutral
     * @educational
     */
    public int getRightJoystickY() {
        return controllerInputManager.getRightJoystickY();
    }

    /**
     * Checks if the L1 button is currently pressed or held.
     * 
     * @return true if L1 button is pressed, false otherwise
     * @educational
     */
    public boolean l1_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.FRONT_LEFT_TOP);
    }

    /**
     * Checks if the L2 button is currently pressed or held.
     * 
     * @return true if L2 button is pressed, false otherwise
     * @educational
     */
    public boolean l2_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.FRONT_LEFT_BOTTOM);
    }

    /**
     * Checks if the R1 button is currently pressed or held.
     * 
     * @return true if R1 button is pressed, false otherwise
     * @educational
     */
    public boolean r1_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.FRONT_RIGHT_TOP);
    }

    /**
     * Checks if the R2 button is currently pressed or held.
     * 
     * @return true if R2 button is pressed, false otherwise
     * @educational
     */
    public boolean r2_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.FRONT_RIGHT_BOTTOM);
    }

    /**
     * Checks if the H button is currently pressed or held.
     * 
     * @return true if H button is pressed, false otherwise
     * @educational
     */
    public boolean h_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.TOP_LEFT);
    }

    /**
     * Checks if the power button is currently pressed or held.
     * 
     * @return true if power button is pressed, false otherwise
     * @educational
     */
    public boolean power_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.TOP_RIGHT);
    }

    /**
     * Checks if the up arrow button is currently pressed or held.
     * 
     * @return true if up arrow is pressed, false otherwise
     * @educational
     */
    public boolean up_arrow_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.MID_UP);
    }

    /**
     * Checks if the left arrow button is currently pressed or held.
     * 
     * @return true if left arrow is pressed, false otherwise
     * @educational
     */
    public boolean left_arrow_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.MID_LEFT);
    }

    /**
     * Checks if the right arrow button is currently pressed or held.
     * 
     * @return true if right arrow is pressed, false otherwise
     * @educational
     */
    public boolean right_arrow_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.MID_RIGHT);
    }

    /**
     * Checks if the down arrow button is currently pressed or held.
     * 
     * @return true if down arrow is pressed, false otherwise
     * @educational
     */
    public boolean down_arrow_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.MID_DOWN);
    }

    /**
     * Checks if the S button is currently pressed or held.
     * 
     * @return true if S button is pressed, false otherwise
     * @educational
     */
    public boolean s_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.BOTTOM_LEFT);
    }

    /**
     * Checks if the P button is currently pressed or held.
     * 
     * @return true if P button is pressed, false otherwise
     * @educational
     */
    public boolean p_pressed() {
        return isButtonPressed(DroneSystem.ButtonFlag.BOTTOM_RIGHT);
    }

    /**
     * Helper method to check if a specific button is currently pressed.
     * 
     * @param buttonFlag The button flag constant to check
     * @return true if the button is pressed or held, false otherwise
     */
    private boolean isButtonPressed(int buttonFlag) {
        ButtonData buttonData = controllerInputManager.getButtonDataObject();
        
        // Button is pressed if:
        // 1. The button flag bit is set in buttonFlags, AND
        // 2. The event is Press or Down (actively pressed/held)
        // This prevents false positives from Up events where the flag might linger
        boolean buttonFlagSet = (buttonData.getButtonFlags() & buttonFlag) != 0;
        boolean activeEvent = "Press".equals(buttonData.getEventName()) || 
                             "Down".equals(buttonData.getEventName());
        
        return buttonFlagSet && activeEvent;
    }

    /**
     * Updates button data when button input is received from controller.
     * This method is called internally by the receiver.
     * 
     * @param button The button protocol data received
     */
    public void updateButtonData(com.otabi.jcodroneedu.protocol.controllerinput.Button button) {
        controllerInputManager.updateButtonData(button);
    }

    /**
     * Updates joystick data when joystick input is received from controller.
     * This method is called internally by the receiver.
     * 
     * @param joystick The joystick protocol data received
     */
    public void updateJoystickData(com.otabi.jcodroneedu.protocol.controllerinput.Joystick joystick) {
        controllerInputManager.updateJoystickData(joystick);
    }

    /**
     * Gets button data as a type-safe Java object (recommended for Java code).
     * 
     * <p>This method provides a Java-idiomatic way to access button data with proper types
     * instead of working with Object arrays.</p>
     * 
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * ButtonData data = drone.getButtonDataObject();
     * System.out.println("Button: " + data.getButtonFlags());
     * System.out.println("Event: " + data.getEventName());
     * }</pre>
     * 
     * @return ButtonData object containing timestamp, flags, and event name
     * @since 1.0.0
     * @see ButtonData
     */
    public ButtonData getButtonDataObject() {
        return controllerInputManager.getButtonDataObject();
    }

    /**
     * Gets joystick data as a type-safe Java object (recommended for Java code).
     * 
     * <p>This method provides a Java-idiomatic way to access joystick data with proper types
     * instead of working with int arrays.</p>
     * 
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * JoystickData data = drone.getJoystickDataObject();
     * System.out.println("Left X: " + data.getLeftX());
     * System.out.println("Right Y: " + data.getRightY());
     * }</pre>
     * 
     * @return JoystickData object containing all joystick values
     * @since 1.0.0
     * @see JoystickData
     */
    public JoystickData getJoystickDataObject() {
        return controllerInputManager.getJoystickDataObject();
    }

    // ========================================
    // Inventory Methods
    // ========================================

    /**
     * Retrieves flight count statistics from the drone and returns an array containing:
     * [timestamp, flight_time, takeoff_count, landing_count, accident_count]
     *
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getCountDataObject(double)} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     *
     * @param delay Delay in seconds to wait for response (default 0.05)
     * @return Object array with count data
     * @educational
     * @since 1.0.0
     * @see #getCountDataObject(double) Recommended Java alternative
     * @see #getFlightTime() For individual value access
     */
    public Object[] getCountData(double delay) {
        Request request = new Request(DataType.Count);
        sendMessage(request, DeviceType.Base, DeviceType.Drone);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return inventoryManager.getCountDataArray();
    }

    /**
     * Retrieves flight count statistics from the drone with default delay.
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getCountDataObject()} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     * 
     * @return Object array with count data [timestamp, flight_time, takeoff_count, landing_count, accident_count]
     * @educational
     * @since 1.0.0
     * @see #getCountDataObject() Recommended Java alternative
     * @see #getFlightTime() For individual value access
     */
    public Object[] getCountData() {
        return getCountData(0.05);
    }

    /**
     * Gets the total flight time in seconds from the count data.
     * 
     * @return Flight time in seconds
     * @educational
     * @since 1.0.0
     */
    public int getFlightTime() {
        getCountData();
        return inventoryManager.getFlightTime();
    }

    /**
     * Gets the total number of takeoffs from the count data.
     * 
     * @return Number of takeoffs
     * @educational
     * @since 1.0.0
     */
    public int getTakeoffCount() {
        getCountData();
        return inventoryManager.getTakeoffCount();
    }

    /**
     * Gets the total number of landings from the count data.
     * 
     * @return Number of landings
     * @educational
     * @since 1.0.0
     */
    public int getLandingCount() {
        getCountData();
        return inventoryManager.getLandingCount();
    }

    /**
     * Gets the total number of accidents from the count data.
     * 
     * @return Number of accidents
     * @educational
     * @since 1.0.0
     */
    public int getAccidentCount() {
        getCountData();
        return inventoryManager.getAccidentCount();
    }

    /**
     * Retrieves device information for both drone and controller.
     * Returns an array containing: [timestamp, drone_model, drone_firmware, controller_model, controller_firmware]
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getInformationDataObject(double)} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     * 
     * @param delay Delay in seconds to wait for response (default 0.05)
     * @return Object array with information data
     * @educational
     * @since 1.0.0
     * @see #getInformationDataObject(double) Recommended Java alternative
     */
    public Object[] getInformationData(double delay) {
        Request request = new Request(DataType.Information);
        sendMessage(request, DeviceType.Base, DeviceType.Drone);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        sendMessage(request, DeviceType.Base, DeviceType.Controller);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return inventoryManager.getInformationDataArray();
    }

    /**
     * Retrieves device information with default delay.
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getInformationDataObject()} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     * 
     * @return Object array with information data [timestamp, drone_model, drone_firmware, controller_model, controller_firmware]
     * @educational
     * @since 1.0.0
     * @see #getInformationDataObject() Recommended Java alternative
     */
    public Object[] getInformationData() {
        return getInformationData(0.05);
    }

    /**
     * Retrieves CPU ID for both drone and controller.
     * Returns an array containing: [timestamp, drone_cpu_id, controller_cpu_id]
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getCpuIdDataObject(double)} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     * 
     * @param delay Delay in seconds to wait for response (default 0.05)
     * @return Object array with CPU ID data
     * @educational
     * @since 1.0.0
     * @see #getCpuIdDataObject(double) Recommended Java alternative
     */
    public Object[] getCpuIdData(double delay) {
        Request request = new Request(DataType.Address);
        sendMessage(request, DeviceType.Base, DeviceType.Drone);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        sendMessage(request, DeviceType.Base, DeviceType.Controller);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return inventoryManager.getCpuIdDataArray();
    }

    /**
     * Retrieves CPU ID data with default delay.
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getCpuIdDataObject()} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     * 
     * @return Object array with CPU ID data [timestamp, drone_cpu_id, controller_cpu_id]
     * @educational
     * @since 1.0.0
     * @see #getCpuIdDataObject() Recommended Java alternative
     */
    public Object[] getCpuIdData() {
        return getCpuIdData(0.05);
    }

    /**
     * Retrieves addresses for both drone and controller.
     * Returns an array containing: [timestamp, drone_address, controller_address]
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getAddressDataObject(double)} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     * 
     * @param delay Delay in seconds to wait for response (default 0.05)
     * @return Object array with address data
     * @educational
     * @since 1.0.0
     * @see #getAddressDataObject(double) Recommended Java alternative
     */
    public Object[] getAddressData(double delay) {
        Request request = new Request(DataType.Address);
        sendMessage(request, DeviceType.Base, DeviceType.Drone);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        sendMessage(request, DeviceType.Base, DeviceType.Controller);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return inventoryManager.getAddressDataArray();
    }

    /**
     * Retrieves address data with default delay.
     * 
     * <p><strong>Note for Java developers:</strong> Consider using {@link #getAddressDataObject()} 
     * for a more type-safe, Java-idiomatic approach. This method is provided primarily for 
     * Python API compatibility.</p>
     * 
     * @return Object array with address data [timestamp, drone_address, controller_address]
     * @educational
     * @since 1.0.0
     * @see #getAddressDataObject() Recommended Java alternative
     */
    public Object[] getAddressData() {
        return getAddressData(0.05);
    }

    // ========================================
    // Inventory Methods - Java Composite Objects
    // ========================================
    
    /**
     * Retrieves flight count statistics from the drone as a type-safe Java object.
     * This method provides a more Java-idiomatic alternative to {@link #getCountData()}.
     * 
     * @param delay Delay in seconds to wait for response
     * @return CountData object with flight statistics, or null if not available
     * @educational
     * @since 1.0.0
     * @see #getCountData()
     */
    public CountData getCountDataObject(double delay) {
        Request request = new Request(DataType.Count);
        sendMessage(request, DeviceType.Base, DeviceType.Drone);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return inventoryManager.getCountDataObject();
    }
    
    /**
     * Retrieves flight count statistics from the drone as a type-safe Java object with default delay.
     * This method provides a more Java-idiomatic alternative to {@link #getCountData()}.
     * 
     * @return CountData object with flight statistics, or null if not available
     * @educational
     * @since 1.0.0
     * @see #getCountData()
     */
    public CountData getCountDataObject() {
        return getCountDataObject(0.05);
    }
    
    /**
     * Retrieves device information as a type-safe Java object.
     * This method provides a more Java-idiomatic alternative to {@link #getInformationData()}.
     * 
     * @param delay Delay in seconds to wait for response
     * @return InformationData object with device information, or null if not available
     * @educational
     * @since 1.0.0
     * @see #getInformationData()
     */
    public InformationData getInformationDataObject(double delay) {
        Request request = new Request(DataType.Information);
        sendMessage(request, DeviceType.Base, DeviceType.Drone);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        sendMessage(request, DeviceType.Base, DeviceType.Controller);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return inventoryManager.getInformationDataObject();
    }
    
    /**
     * Retrieves device information as a type-safe Java object with default delay.
     * This method provides a more Java-idiomatic alternative to {@link #getInformationData()}.
     * 
     * @return InformationData object with device information, or null if not available
     * @educational
     * @since 1.0.0
     * @see #getInformationData()
     */
    public InformationData getInformationDataObject() {
        return getInformationDataObject(0.05);
    }
    
    /**
     * Retrieves CPU IDs as a type-safe Java object.
     * This method provides a more Java-idiomatic alternative to {@link #getCpuIdData()}.
     * 
     * @param delay Delay in seconds to wait for response
     * @return CpuIdData object with CPU IDs, or null if not available
     * @educational
     * @since 1.0.0
     * @see #getCpuIdData()
     */
    public CpuIdData getCpuIdDataObject(double delay) {
        Request request = new Request(DataType.Address);
        sendMessage(request, DeviceType.Base, DeviceType.Drone);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        sendMessage(request, DeviceType.Base, DeviceType.Controller);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return inventoryManager.getCpuIdDataObject();
    }
    
    /**
     * Retrieves CPU IDs as a type-safe Java object with default delay.
     * This method provides a more Java-idiomatic alternative to {@link #getCpuIdData()}.
     * 
     * @return CpuIdData object with CPU IDs, or null if not available
     * @educational
     * @since 1.0.0
     * @see #getCpuIdData()
     */
    public CpuIdData getCpuIdDataObject() {
        return getCpuIdDataObject(0.05);
    }
    
    /**
     * Retrieves Bluetooth addresses as a type-safe Java object.
     * This method provides a more Java-idiomatic alternative to {@link #getAddressData()}.
     * 
     * @param delay Delay in seconds to wait for response
     * @return AddressData object with Bluetooth addresses, or null if not available
     * @educational
     * @since 1.0.0
     * @see #getAddressData()
     */
    public AddressData getAddressDataObject(double delay) {
        Request request = new Request(DataType.Address);
        sendMessage(request, DeviceType.Base, DeviceType.Drone);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        sendMessage(request, DeviceType.Base, DeviceType.Controller);
        try {
            Thread.sleep((long)(delay * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return inventoryManager.getAddressDataObject();
    }
    
    /**
     * Retrieves Bluetooth addresses as a type-safe Java object with default delay.
     * This method provides a more Java-idiomatic alternative to {@link #getAddressData()}.
     * 
     * @return AddressData object with Bluetooth addresses, or null if not available
     * @educational
     * @since 1.0.0
     * @see #getAddressData()
     */
    public AddressData getAddressDataObject() {
        return getAddressDataObject(0.05);
    }

    // ========================================
    // End Built-in Flight Patterns
    // ========================================
}
