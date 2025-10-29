package com.otabi.jcodroneedu.protocol.settings;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

/**
 * Protocol class representing flight statistics and usage counters from the drone.
 * 
 * <p>This class stores historical data about the drone's operation including:
 * <ul>
 *   <li>Total system operation time (seconds since first power-on)</li>
 *   <li>Total flight time (seconds motors have been running)</li>
 *   <li>Number of takeoffs performed</li>
 *   <li>Number of landings performed</li>
 *   <li>Number of accidents/crashes detected</li>
 * </ul>
 * 
 * <p>Count data is stored in the drone's persistent memory and survives power cycles.
 * This information is useful for:
 * <ul>
 *   <li>Tracking drone usage and flight hours</li>
 *   <li>Monitoring student practice sessions</li>
 *   <li>Detecting potential maintenance needs</li>
 *   <li>Analyzing flight patterns and safety</li>
 * </ul>
 * 
 * <p><strong>Binary Protocol Format (14 bytes):</strong></p>
 * <pre>
 * Offset | Size | Type | Description
 * -------|------|------|----------------------------------
 *   0    |  4   | u32  | System operation time (seconds)
 *   4    |  4   | u32  | Flight time (seconds)
 *   8    |  2   | u16  | Number of takeoffs
 *  10    |  2   | u16  | Number of landings
 *  12    |  2   | u16  | Number of accidents
 * </pre>
 * 
 * <p><strong>Note:</strong> This class name matches the Python CoDrone EDU implementation
 * for API compatibility, though "FlightStatistics" might be more descriptive.</p>
 * 
 * @see com.otabi.jcodroneedu.Drone#getCountData()
 * @see com.otabi.jcodroneedu.Drone#getFlightTime()
 * @see com.otabi.jcodroneedu.Drone#getTakeoffCount()
 * @see com.otabi.jcodroneedu.Drone#getLandingCount()
 * @see com.otabi.jcodroneedu.Drone#getAccidentCount()
 */
public class Count implements Serializable
{
    public static final byte COUNT_SIZE = 14;

    private int timeSystem;      // System operation time in seconds (u32)
    private int timeFlight;      // Flight time in seconds (u32)
    private short countTakeOff;  // Number of takeoffs (u16)
    private short countLanding;  // Number of landings (u16)
    private short countAccident; // Number of accidents (u16)

    /**
     * Default constructor for protocol deserialization.
     * Creates a Count object with all fields initialized to zero.
     */
    public Count() {
        // Default constructor for factory
    }

    /**
     * Constructs a Count object with specified flight statistics.
     * 
     * @param timeSystem Total system operation time in seconds since first power-on
     * @param timeFlight Total flight time in seconds (motors running)
     * @param countTakeOff Total number of takeoffs performed
     * @param countLanding Total number of landings performed
     * @param countAccident Total number of accidents/crashes detected
     */
    public Count(int timeSystem, int timeFlight, short countTakeOff, short countLanding, short countAccident)
    {
        this.timeSystem = timeSystem;
        this.timeFlight = timeFlight;
        this.countTakeOff = countTakeOff;
        this.countLanding = countLanding;
        this.countAccident = countAccident;
    }

    /**
     * Returns the size of this message in bytes.
     * 
     * @return Size in bytes (14)
     */
    @Override
    public byte getSize()
    {
        return COUNT_SIZE;
    }

    /**
     * Deserializes count data from a binary buffer.
     * Reads 14 bytes in little-endian format.
     * 
     * @param buffer ByteBuffer containing the serialized count data
     * @throws InvalidDataSizeException if buffer doesn't contain enough data
     */
    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.timeSystem = buffer.getInt();
        this.timeFlight = buffer.getInt();
        this.countTakeOff = buffer.getShort();
        this.countLanding = buffer.getShort();
        this.countAccident = buffer.getShort();
    }

    /**
     * Serializes count data to a binary buffer.
     * Writes 14 bytes in little-endian format.
     * 
     * @param buffer ByteBuffer to write the serialized data to
     */
    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putInt(this.timeSystem);
        buffer.putInt(this.timeFlight);
        buffer.putShort(this.countTakeOff);
        buffer.putShort(this.countLanding);
        buffer.putShort(this.countAccident);
    }

    /**
     * Gets the total system operation time in seconds.
     * This is the cumulative time the drone has been powered on since first use.
     * 
     * @return System operation time in seconds (unsigned 32-bit value)
     */
    public int getTimeSystem() { return timeSystem; }
    
    /**
     * Gets the total flight time in seconds.
     * This is the cumulative time the motors have been running.
     * 
     * @return Flight time in seconds (unsigned 32-bit value)
     */
    public int getTimeFlight() { return timeFlight; }
    
    /**
     * Gets the total number of takeoffs.
     * 
     * @return Number of takeoffs performed (unsigned 16-bit value)
     */
    public short getCountTakeOff() { return countTakeOff; }
    
    /**
     * Gets the total number of landings.
     * 
     * @return Number of landings performed (unsigned 16-bit value)
     */
    public short getCountLanding() { return countLanding; }
    
    /**
     * Gets the total number of accidents/crashes.
     * The drone detects accidents using accelerometer threshold detection.
     * 
     * @return Number of accidents detected (unsigned 16-bit value)
     */
    public short getCountAccident() { return countAccident; }
}
