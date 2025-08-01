package com.otabi.jcodroneedu;

import com.google.common.util.concurrent.RateLimiter;
import com.otabi.jcodroneedu.protocol.*;
import com.otabi.jcodroneedu.protocol._unknown.Request;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.protocol.linkmanager.Information;
import com.otabi.jcodroneedu.receiver.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
 *   <li>{@link #get_move_values()} - Debug your flight commands</li>
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
public class Drone implements AutoCloseable {

    private static final Logger log = LogManager.getLogger(Drone.class);

    // --- Core Components ---
    private final DroneStatus droneStatus;
    private final LinkManager linkManager;
    private final Receiver receiver;
    private final SerialPortManager serialPortManager;

    // --- Controllers (Facades for specific functionalities) ---
    private final FlightController flightController;
    private final LinkController linkController;
    private final SettingsController settingsController;

    private final RateLimiter commandRateLimiter;
    private boolean isConnected = false;

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

        // Initialize core components
        // The 'Internals' class from the original code seems to have been absorbed
        // into the new manager/controller structure. If it has other responsibilities,
        // it would be initialized here.
        this.receiver = new Receiver(this, droneStatus, linkManager);
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
        message.put((byte) 0x0A).put((byte) 0x55);
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

    /**
     * This is a setter function that allows you to set the pitch variable.
     * Once you set pitch, you have to use move() to actually execute the movement.
     * The pitch variable will remain what you last set it until the end of the
     * flight sequence, so you will have to set it back to 0 if you don't want
     * the drone to pitch again.
     *
     * @param pitch Sets the pitch variable (-100 - 100). The number represents the
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
     * @param roll Sets the roll variable (-100 - 100). The number represents the
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
     * @param yaw Sets the yaw variable (-100 - 100). The number represents the
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
     * @param throttle Sets the throttle variable (-100 - 100).
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
     *
     * @deprecated This method is deprecated and will be removed in a future release.Please use <pre>drone.get_move_values()</pre> instead.
     */
    @Deprecated(forRemoval = true)
    public void print_move_values(){
        flightController.print_move_values();
    }

    /**
     * Returns current values of roll, pitch, yaw, and throttle.
     *
     * @return A byte array of roll(0), pitch (1), yaw (2) and throttle (3) values.
     */
    public byte[] get_move_values(){
        return flightController.get_move_values();
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
     * @param roll     Controls sideways tilt. Positive values roll right, negative values roll left (-100 to 100).
     * @param pitch    Controls forward and backward tilt. Positive values pitch forward, negative values pitch backward (-100 to 100).
     * @param yaw      Controls rotation. Positive values turn left, negative values turn right (-100 to 100).
     * @param throttle Controls altitude. Positive values increase altitude, negative values decrease altitude (-100 to 100).
     */
    public void sendControl(int roll, int pitch, int yaw, int throttle) {
        flightController.sendControl(roll, pitch, yaw, throttle);
    }

    /**
     * Continuously sends a flight control command for a specified duration.
     *
     * @param roll     The roll value to maintain (-100 to 100).
     * @param pitch    The pitch value to maintain (-100 to 100).
     * @param yaw      The yaw value to maintain (-100 to 100).
     * @param throttle The throttle value to maintain (-100 to 100).
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
     * </ul>
     * 
     * @param direction String direction: "forward", "backward", "left", "right", "up", "down"
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
     * @param distance The distance to move forward
     * @param units The unit: "cm" (default), "in", "ft", "m"
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
     * @param distance The distance to move backward
     * @param units The unit: "cm" (default), "in", "ft", "m"
     * @param speed The speed from 0.5 to 2.0 m/s (default: 1.0)
     */
    public void moveBackward(double distance, String units, double speed) {
        flightController.moveBackward(distance, units, speed);
    }
    
    /**
     * Move backward with default units (cm) and speed (1.0 m/s).
     */
    public void moveBackward(double distance) {
        flightController.moveBackward(distance);
    }
    
    /**
     * Move backward with specified units and default speed (1.0 m/s).
     */
    public void moveBackward(double distance, String units) {
        flightController.moveBackward(distance, units);
    }
    
    /**
     * Move left a specific distance with precision control.
     * 
     * <p>This method provides Python API compatibility for precise leftward movement.</p>
     * 
     * @param distance The distance to move left
     * @param units The unit: "cm" (default), "in", "ft", "m"
     * @param speed The speed from 0.5 to 2.0 m/s (default: 1.0)
     */
    public void moveLeft(double distance, String units, double speed) {
        flightController.moveLeft(distance, units, speed);
    }
    
    /**
     * Move left with default units (cm) and speed (1.0 m/s).
     */
    public void moveLeft(double distance) {
        flightController.moveLeft(distance);
    }
    
    /**
     * Move left with specified units and default speed (1.0 m/s).
     */
    public void moveLeft(double distance, String units) {
        flightController.moveLeft(distance, units);
    }
    
    /**
     * Move right a specific distance with precision control.
     * 
     * <p>This method provides Python API compatibility for precise rightward movement.</p>
     * 
     * @param distance The distance to move right
     * @param units The unit: "cm" (default), "in", "ft", "m"
     * @param speed The speed from 0.5 to 2.0 m/s (default: 1.0)
     */
    public void moveRight(double distance, String units, double speed) {
        flightController.moveRight(distance, units, speed);
    }
    
    /**
     * Move right with default units (cm) and speed (1.0 m/s).
     */
    public void moveRight(double distance) {
        flightController.moveRight(distance);
    }
    
    /**
     * Move right with specified units and default speed (1.0 m/s).
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
    public void square(int speed, int seconds, int direction) {
        int power = Math.max(0, Math.min(100, speed));
        long duration = seconds * 1000L;

        // Square pattern: Forward -> Right -> Back -> Left
        sendControlWhile(0, power, 0, 0, duration);                    // Forward (pitch)
        sendControlWhile(0, -power, 0, 0, 50);                         // Brief stop

        sendControlWhile(power * direction, 0, 0, 0, duration);        // Right/Left (roll)
        sendControlWhile(-power * direction, 0, 0, 0, 50);             // Brief stop

        sendControlWhile(0, -power, 0, 0, duration);                   // Backward (pitch)
        sendControlWhile(0, power, 0, 0, 50);                          // Brief stop

        sendControlWhile(-power * direction, 0, 0, 0, duration);       // Left/Right (roll)
        sendControlWhile(power * direction, 0, 0, 0, 50);              // Brief stop
    }

    /**
     * Flies the drone in the shape of a triangle.
     * 
     * <p>This method provides Python API compatibility for triangular flight patterns
     * that demonstrate diagonal movement combinations.</p>
     * 
     * @param speed The flight speed (0-100)
     * @param seconds Duration for each side of the triangle in seconds
     * @param direction Direction of flight (1 for right, -1 for left)
     * 
     * @apiNote Equivalent to Python's {@code drone.triangle(speed, seconds, direction)}
     * @since 1.0
     * @educational
     */
    public void triangle(int speed, int seconds, int direction) {
        int power = Math.max(0, Math.min(100, speed));
        long duration = seconds * 1000L;

        // Triangle pattern: Forward-Right -> Back-Right -> Left
        sendControlWhile(power * direction, power, 0, 0, duration);     // Forward-Right diagonal
        sendControlWhile(-power * direction, -power, 0, 0, 50);         // Brief stop

        sendControlWhile(power * direction, -power, 0, 0, duration);    // Back-Right diagonal  
        sendControlWhile(-power * direction, power, 0, 0, 50);          // Brief stop

        sendControlWhile(-power * direction, 0, 0, 0, duration);        // Left edge
        sendControlWhile(power * direction, 0, 0, 0, 50);               // Brief stop
    }

    /**
     * Flies the drone in a circular pattern.
     * 
     * <p>This method provides Python API compatibility for circular flight patterns
     * using coordinated pitch and roll movements.</p>
     * 
     * @param speed The flight speed (0-100)
     * @param direction Direction of circle (1 for clockwise, -1 for counter-clockwise)
     * 
     * @apiNote Equivalent to Python's {@code drone.circle(speed, direction)}
     * @since 1.0
     * @educational
     */
    public void circle(int speed, int direction) {
        int power = Math.max(0, Math.min(100, speed));
        
        // Create circular motion by combining pitch and roll
        // This is a simplified circle - more complex implementations would use trigonometry
        for (int i = 0; i < 8; i++) {
            sendControlWhile(power * direction, power, 0, 0, 500);      // Forward-Right
            sendControlWhile(power * direction, 0, 0, 0, 250);          // Right
            sendControlWhile(power * direction, -power, 0, 0, 500);     // Back-Right
            sendControlWhile(0, -power, 0, 0, 250);                     // Back
            sendControlWhile(-power * direction, -power, 0, 0, 500);    // Back-Left
            sendControlWhile(-power * direction, 0, 0, 0, 250);         // Left
            sendControlWhile(-power * direction, power, 0, 0, 500);     // Forward-Left
            sendControlWhile(0, power, 0, 0, 250);                      // Forward
        }
    }

    /**
     * Flies the drone in a spiral pattern.
     * 
     * <p>This method provides Python API compatibility for spiral flight patterns
     * that combine circular motion with vertical movement.</p>
     * 
     * @param speed The flight speed (0-100)
     * @param seconds Total duration of the spiral in seconds
     * @param direction Direction of spiral (1 for clockwise, -1 for counter-clockwise)
     * 
     * @apiNote Equivalent to Python's {@code drone.spiral(speed, seconds, direction)}
     * @since 1.0
     * @educational
     */
    public void spiral(int speed, int seconds, int direction) {
        int power = Math.max(0, Math.min(100, speed));
        long totalDuration = seconds * 1000L;
        long segmentDuration = totalDuration / 16; // 16 segments for smooth spiral
        
        // Gradually increasing circular motion with altitude change
        for (int i = 0; i < 16; i++) {
            int radius = (i + 1) * power / 16; // Gradually increase radius
            sendControlWhile(radius * direction, radius, power / 4, 0, segmentDuration);
        }
    }

    /**
     * Flies the drone in a swaying motion.
     * 
     * <p>This method provides Python API compatibility for side-to-side swaying
     * that demonstrates oscillating movement patterns.</p>
     * 
     * @param speed The flight speed (0-100)  
     * @param seconds Total duration of the sway in seconds
     * @param direction Direction of initial sway (1 for right first, -1 for left first)
     * 
     * @apiNote Equivalent to Python's {@code drone.sway(speed, seconds, direction)}
     * @since 1.0
     * @educational
     */
    public void sway(int speed, int seconds, int direction) {
        int power = Math.max(0, Math.min(100, speed));
        long totalDuration = seconds * 1000L;
        long segmentDuration = totalDuration / 8; // 8 segments for smooth sway
        
        // Alternating left-right motion
        for (int i = 0; i < 4; i++) {
            sendControlWhile(power * direction, 0, 0, 0, segmentDuration);     // Right/Left
            sendControlWhile(-power * direction, 0, 0, 0, segmentDuration);    // Left/Right
        }
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
     * Flies the drone in a triangle pattern using yaw rotation.
     * Creates a triangle shape by combining forward movement with turning.
     * Based on Python CoDrone EDU triangle_turn() method.
     * 
     * @param speed Speed value from 0 to 100 (default: 60)
     * @param seconds Duration of each side in seconds (default: 2)
     * @param direction Direction multiplier: 1 for right, -1 for left (default: 1)
     */
    public void triangleTurn(int speed, int seconds, int direction) {
        int power = Math.max(0, Math.min(100, speed));
        long duration = seconds * 1000L; // Convert to milliseconds
        int dir = (direction >= 0) ? 1 : -1;
        
        // Triangle pattern using yaw and forward movement
        // Side 1: Forward + Right turn
        sendControlWhile(power * dir, power, 0, 0, duration);
        
        // Side 2: Forward + Left turn  
        sendControlWhile(power * dir, -power, 0, 0, duration);
        
        // Side 3: Backward to close triangle
        sendControlWhile(-power * dir, 0, 0, 0, duration);
    }

    /**
     * Overloaded triangleTurn with default parameters.
     */
    public void triangleTurn() {
        triangleTurn(60, 2, 1);
    }

    /**
     * Creates a circular flight pattern using coordinated pitch and roll movements.
     * Uses a 16-step approximation to create smooth circular motion.
     * Based on Python CoDrone EDU circle_turn() method.
     * 
     * @param speed Speed value from 0 to 100 (default: 30)
     * @param seconds Duration factor (default: 1)
     * @param direction Direction multiplier: 1 for clockwise, -1 for counter-clockwise (default: 1)
     */
    public void circleTurn(int speed, int seconds, int direction) {
        int baseSpeed = Math.max(0, Math.min(100, speed));
        int dir = (direction >= 0) ? 1 : -1;
        
        int pitch = baseSpeed;
        int roll = 0;
        
        // Create circular motion through 4 quadrants (16 steps total)
        // Quadrant 1: Increase roll, decrease pitch
        for (int i = 0; i < 4; i++) {
            sendControlWhile(roll * dir, pitch * dir, 0, 0, 400);
            roll += 10;
            pitch -= 10;
        }
        
        // Quadrant 2: Decrease roll, decrease pitch  
        for (int i = 0; i < 4; i++) {
            sendControlWhile(roll * dir, pitch * dir, 0, 0, 400);
            roll -= 10;
            pitch -= 10;
        }
        
        // Quadrant 3: Decrease roll, increase pitch
        for (int i = 0; i < 4; i++) {
            sendControlWhile(roll * dir, pitch * dir, 0, 0, 400);
            roll -= 10;
            pitch += 10;
        }
        
        // Quadrant 4: Increase roll, increase pitch
        for (int i = 0; i < 4; i++) {
            sendControlWhile(roll * dir, pitch * dir, 0, 0, 400);
            roll += 10;
            pitch += 10;
        }
    }

    /**
     * Overloaded circleTurn with default parameters.
     */
    public void circleTurn() {
        circleTurn(30, 1, 1);
    }

    // ========================================
    // End Built-in Flight Patterns
    // ========================================
}
