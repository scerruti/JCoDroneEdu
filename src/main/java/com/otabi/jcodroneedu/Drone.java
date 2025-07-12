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
}
