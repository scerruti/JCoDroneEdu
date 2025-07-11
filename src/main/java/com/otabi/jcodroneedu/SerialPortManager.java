package com.otabi.jcodroneedu;

import com.fazecast.jSerialComm.SerialPort;
import com.otabi.jcodroneedu.receiver.Receiver;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manages the serial port connection for the drone controller.
 * This class handles port discovery, opening, closing, and reading data.
 */
public class SerialPortManager {

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
                System.err.println("CoDrone EDU controller not found. Please ensure it's connected.");
                // return false;
                throw new DroneNotFoundException(portName);
            }
            System.out.printf("Detected CoDrone EDU controller at port %s.\n", targetPortName);
        }

        try {
            serialPort = SerialPort.getCommPort(targetPortName);
            serialPort.setBaudRate(115200);
            serialPort.setParity(SerialPort.NO_PARITY);
            serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            serialPort.setNumDataBits(8);

            if (!serialPort.openPort()) {
                System.err.println("Failed to open serial port. Check permissions or if it's in use.");
                return false;
            }

            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
            inputStream = serialPort.getInputStream();
            startReaderThread();
            System.out.printf("Connected to %s.\n", targetPortName);
            return true;

        } catch (Exception e) {
            System.err.println("Error connecting to device: " + e.getMessage());
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
            System.err.println("Serial library not installed or failed to load.");
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
                        System.err.println("Error reading from serial port: " + e.getMessage());
                        if (errorCount > 5) {
                            System.err.println("Repeated communication errors. Closing port.");
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
            System.out.println("Serial port disconnected.");
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
            // Or handle this error more robustly
            System.err.println("Cannot write, serial port is not open.");
        }
    }
}
