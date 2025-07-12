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
 * The main class for interacting with the CoDrone EDU.
 * This class acts as a facade, providing a simple public API that delegates
 * complex operations to specialized controller and manager classes.
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
     * TODO
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

    public Drone(boolean autoConnect) throws DroneNotFoundException
    {
        this(autoConnect, null);
    }

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
     * <pre>pair</pre> replicates Python behavior of crashing on connection error for compatibility
     * use <pre>connect</pre> to implement with exception handling
     *
     * TODO Should I move this to javadoc?
     */

    /**
     * Connects to the CoDrone EDU controller. This method will attempt to
     * automatically find the correct serial port.
     *
     * @return true if the connection was successful, false otherwise.
     */
    // @Deprecated
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
     * Connects to the CoDrone EDU controller. This method will attempt to
     * automatically find the correct serial port.
     * @return true if the connection was successful, false otherwise.
     */
    // @Deprecated
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
     * Connects to the CoDrone EDU controller. This method will attempt to
     * automatically find the correct serial port.
     * @return true if the connection was successful, false otherwise.
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
     * @param durationMs The duration to hover, in milliseconds.
     */
    public void hover(long durationMs) {
        flightController.hover(durationMs);
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
    // --- Getters for Components ---
    // =================================================================================

    public FlightController getFlightController() {
        return flightController;
    }

    public LinkController getLinkController() {
        return linkController;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    public DroneStatus getDroneStatus() {
        return droneStatus;
    }

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

    public Receiver getReceiver()
    {
        return receiver;
    }
}
