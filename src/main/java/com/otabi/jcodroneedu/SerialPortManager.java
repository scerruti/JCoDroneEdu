package com.otabi.jcodroneedu;

import com.fazecast.jSerialComm.SerialPort;
import com.otabi.jcodroneedu.receiver.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manages the serial port connection for the drone controller.
 * This class handles port discovery, opening, closing, and reading data.
 */
public class SerialPortManager {

    private static final Logger log = LogManager.getLogger(SerialPortManager.class);
    private static final int CDE_CONTROLLER_VID = 1155; // Vendor ID for CoDrone EDU controller
    private SerialPort serialPort = null;
    private InputStream inputStream = null;
    private Thread readerThread = null;
    private volatile boolean isRunning = false;
    private final Receiver receiver;

    public SerialPortManager(Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     * Finds and opens a serial port connection to the CoDrone EDU controller.
     *
     * @param portName The specific port to connect to. If null, it will auto-detect.
     * @return true if the connection is successful, false otherwise.
     */
    public boolean open(String portName) throws DroneNotFoundException
    {
        String targetPortName = portName;

        if (targetPortName == null) {
            targetPortName = findControllerPort();
            if (targetPortName == null) {
                log.error("CoDrone EDU controller not found. Please ensure it's connected.");
                throw new DroneNotFoundException(portName);
            }
            log.info("Detected CoDrone EDU controller at port {}", targetPortName);
        }

        try {
            serialPort = SerialPort.getCommPort(targetPortName);
            serialPort.setBaudRate(115200);
            serialPort.setParity(SerialPort.NO_PARITY);
            serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            serialPort.setNumDataBits(8);

            if (!serialPort.openPort()) {
                log.error("Failed to open serial port. Check permissions or if it's in use.");
                return false;
            }

            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, DroneSystem.CommunicationConstants.SERIAL_TIMEOUT_MS, 0);
            inputStream = serialPort.getInputStream();
            startReaderThread();
            log.info("Connected to {}", targetPortName);
            return true;

        } catch (Exception e) {
            log.error("Error connecting to device: {}", e.getMessage(), e);
            disconnect();
            return false;
        }
    }

    /**
     * Scans available serial ports for the CoDrone EDU controller's vendor ID.
     *
     * @return The descriptive port name if found, otherwise null.
     */
    private String findControllerPort() {
        try {
            SerialPort[] ports = SerialPort.getCommPorts();
            for (SerialPort port : ports) {
                if (port.getVendorID() == CDE_CONTROLLER_VID) {
                    return port.getSystemPortName();
                }
            }
        } catch (Exception e) {
            // This is a fatal system error that affects both students and developers
            System.err.println("Serial library not installed or failed to load.");
            log.fatal("Serial library not installed or failed to load.", e);
            System.exit(-1);
        }
        return null;
    }

    /**
     * Starts a background thread to continuously read data from the serial port.
     */
    private void startReaderThread() {
        isRunning = true;
        readerThread = new Thread(() -> {
            int errorCount = 0;
            while (isRunning) {
                try {
                    int data = inputStream.read();
                    if (data != -1) {
                        receiver.call((byte) data);
                        errorCount = 0; // Reset on successful read
                    }
                } catch (IOException e) {
                    if (isRunning) {
                        errorCount++;
                        log.warn("Error reading from serial port: {}", e.getMessage());
                        if (errorCount > 5) {
                            log.error("Repeated communication errors. Closing port.");
                            disconnect();
                        }
                    }
                }
            }
        });
        readerThread.start();
    }

    /**
     * Closes the serial port connection and stops the reading thread.
     */
    public void disconnect() {
        isRunning = false;
        if (readerThread != null) {
            try {
                readerThread.join(500); // Wait for the thread to terminate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            readerThread = null;
        }

        if (isOpen()) {
            serialPort.closePort();
            // Student-friendly message
            System.out.println("Serial port disconnected.");
            // Developer logging
            log.info("Serial port disconnected.");
        }
        serialPort = null;
    }

    /**
     * Checks if the serial port is currently open.
     *
     * @return true if the port is open, false otherwise.
     */
    public boolean isOpen() {
        return serialPort != null && serialPort.isOpen();
    }


    public void write(byte[] data) {
        if (isOpen()) {
            serialPort.writeBytes(data, data.length);
        } else {
            // This is more of a developer error - student shouldn't see this during normal operation
            log.error("Cannot write, serial port is not open.");
        }
    }
}
