package com.otabi.jcodroneedu;

import com.google.common.util.concurrent.RateLimiter;
import com.otabi.jcodroneedu.protocol.*;
import com.otabi.jcodroneedu.protocol._unknown.Request;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.protocol.lightcontroller.Color;
import com.otabi.jcodroneedu.protocol.lightcontroller.LightDefault;
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
    public int[] get_accel() {
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
    public int[] get_gyro() {
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
    public int[] get_angle() {
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
    public int[][] get_color_data() {
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
    public int[] get_colors() {
        com.otabi.jcodroneedu.protocol.cardreader.CardColor cardColor = droneStatus.getCardColor();
        if (cardColor == null) {
            return null;
        }
        
        byte[] colorData = cardColor.getColor();
        if (colorData == null || colorData.length < 2) {
            return null;
        }
        
        // Convert byte values to int for easier educational use
        return new int[]{
            colorData[0] & 0xFF, // Front color sensor
            colorData[1] & 0xFF  // Back color sensor
        };
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
    public int get_front_color() {
        int[] colors = get_colors();
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
    public int get_back_color() {
        int[] colors = get_colors();
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
    public int[] get_position_data() {
        var position = droneStatus.getPosition();
        if (position == null) {
            return null;
        }
        return new int[]{position.getX(), position.getY(), position.getZ()};
    }

    /**
     * Gets the X position (forward/backward) in millimeters.
     * 
     * <p>Returns the drone's forward/backward position relative to takeoff point.
     * Positive values indicate forward movement, negative values indicate backward.</p>
     * 
     * @return X position in millimeters, or 0 if no data available
     * @educational
     */
    public int getPositionX() {
        var position = droneStatus.getPosition();
        return (position != null) ? position.getX() : 0;
    }

    /**
     * Gets the Y position (left/right) in millimeters.
     * 
     * <p>Returns the drone's left/right position relative to takeoff point.
     * Positive values indicate leftward movement, negative values indicate rightward.</p>
     * 
     * @return Y position in millimeters, or 0 if no data available
     * @educational
     */
    public int getPositionY() {
        var position = droneStatus.getPosition();
        return (position != null) ? position.getY() : 0;
    }

    /**
     * Gets the Z position (up/down) in millimeters.
     * 
     * <p>Returns the drone's up/down position relative to takeoff point.
     * Positive values indicate upward movement, negative values indicate downward.</p>
     * 
     * @return Z position in millimeters, or 0 if no data available
     * @educational
     */
    public int getPositionZ() {
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
    public double get_pressure() {
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
    public double get_pressure(String unit) {
        double pascals = get_pressure();
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
     * Gets the drone's internal temperature in Celsius.
     * 
     * <p>Returns the temperature reading from the drone's internal sensor.
     * This data is useful for environmental monitoring and thermal analysis projects.</p>
     * 
     * <h3>🎯 Educational Usage:</h3>
     * <ul>
     *   <li><strong>Environmental Science:</strong> Temperature monitoring</li>
     *   <li><strong>Data Collection:</strong> Multi-sensor environmental data</li>
     *   <li><strong>Physics Learning:</strong> Heat transfer and thermal properties</li>
     *   <li><strong>Weather Projects:</strong> Temperature trend analysis</li>
     * </ul>
     * 
     * @return Temperature in Celsius, or 0.0 if no data available
     * @apiNote Equivalent to Python's {@code drone.get_drone_temperature("C")}
     * @since 1.0
     * @educational
     */
    public double get_drone_temperature() {
        var altitude = droneStatus.getAltitude();
        return (altitude != null) ? altitude.getTemperature() : 0.0;
    }

    /**
     * Gets the drone's internal temperature in the specified unit.
     * 
     * <p>Returns temperature converted to the requested unit.
     * Supports common temperature scales for educational use.</p>
     * 
     * @param unit The unit for temperature measurement. Supported values:
     *             "C" (Celsius), "F" (Fahrenheit), "K" (Kelvin)
     * @return Temperature in the specified unit, or 0.0 if no data available
     * @throws IllegalArgumentException if unit is not supported
     * @apiNote Equivalent to Python's {@code drone.get_drone_temperature(unit)}
     * @since 1.0
     * @educational
     */
    public double get_drone_temperature(String unit) {
        double celsius = get_drone_temperature();
        var altitude = droneStatus.getAltitude();
        if (altitude == null) {
            return 0.0; // No data available
        }
        
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
    public double[] get_sensor_data() {
        // Check if we have basic sensor data
        if (droneStatus.getPosition() == null || droneStatus.getRange() == null) {
            return null;
        }
        
        double[] sensorData = new double[21];
        
        // Position data (0-2)
        int[] position = get_position_data();
        if (position != null) {
            sensorData[0] = position[0]; // x
            sensorData[1] = position[1]; // y  
            sensorData[2] = position[2]; // z
        }
        
        // Motion data (3-11)
        int[] accel = get_accel();
        if (accel != null) {
            sensorData[3] = accel[0]; // accel x
            sensorData[4] = accel[1]; // accel y
            sensorData[5] = accel[2]; // accel z
        }
        
        int[] gyro = get_gyro();
        if (gyro != null) {
            sensorData[6] = gyro[0]; // gyro x
            sensorData[7] = gyro[1]; // gyro y
            sensorData[8] = gyro[2]; // gyro z
        }
        
        int[] angle = get_angle();
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
        sensorData[19] = get_pressure();
        sensorData[20] = get_drone_temperature();
        
        return sensorData;
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
     *   <li>Red: {@code setDroneLED(255, 0, 0)}</li>
     *   <li>Green: {@code setDroneLED(0, 255, 0)}</li>
     *   <li>Blue: {@code setDroneLED(0, 0, 255)}</li>
     *   <li>Yellow: {@code setDroneLED(255, 255, 0)}</li>
     *   <li>Purple: {@code setDroneLED(255, 0, 255)}</li>
     *   <li>White: {@code setDroneLED(255, 255, 255)}</li>
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
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException("Red must be between 0 and 255, got: " + red);
        }
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException("Green must be between 0 and 255, got: " + green);
        }
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Blue must be between 0 and 255, got: " + blue);
        }
        if (brightness < 0 || brightness > 255) {
            throw new IllegalArgumentException("Brightness must be between 0 and 255, got: " + brightness);
        }

        // Create color and send to drone
        Color color = createColor(red, green, blue);
        LightDefault lightDefault = new LightDefault(
            com.otabi.jcodroneedu.protocol.lightcontroller.LightModesDrone.BodyHold, 
            color, 
            (short) brightness
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
        setDroneLED(red, green, blue, 255);
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
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException("Red must be between 0 and 255, got: " + red);
        }
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException("Green must be between 0 and 255, got: " + green);
        }
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Blue must be between 0 and 255, got: " + blue);
        }
        if (brightness < 0 || brightness > 255) {
            throw new IllegalArgumentException("Brightness must be between 0 and 255, got: " + brightness);
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
            Thread.sleep(5);
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
        setControllerLED(red, green, blue, 255);
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
        setDroneLED(255, 0, 0);
    }

    /**
     * Sets the drone LED to green.
     * 
     * @educational
     */
    public void setDroneLEDGreen() {
        setDroneLED(0, 255, 0);
    }

    /**
     * Sets the drone LED to blue.
     * 
     * @educational
     */
    public void setDroneLEDBlue() {
        setDroneLED(0, 0, 255);
    }

    /**
     * Sets the drone LED to yellow.
     * 
     * @educational
     */
    public void setDroneLEDYellow() {
        setDroneLED(255, 255, 0);
    }

    /**
     * Sets the drone LED to purple.
     * 
     * @educational
     */
    public void setDroneLEDPurple() {
        setDroneLED(255, 0, 255);
    }

    /**
     * Sets the drone LED to white.
     * 
     * @educational
     */
    public void setDroneLEDWhite() {
        setDroneLED(255, 255, 255);
    }

    /**
     * Sets the drone LED to orange.
     * 
     * @educational
     */
    public void setDroneLEDOrange() {
        setDroneLED(255, 165, 0);
    }

    // ========================================
    // End Built-in Flight Patterns
    // ========================================
}
