package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

/**
 * Represents altitude and barometric sensor data from the drone.
 * 
 * <p>This class contains barometric pressure sensor readings including:
 * <ul>
 *   <li><strong>temperature</strong> - Sensor die temperature in Celsius (typically 10-15°C below ambient)</li>
 *   <li><strong>pressure</strong> - Atmospheric pressure in Pascals</li>
 *   <li><strong>altitude</strong> - Barometric altitude in meters (calculated by drone firmware)</li>
 *   <li><strong>rangeHeight</strong> - Height from range sensor in meters</li>
 * </ul>
 * 
 * <p><strong>⚠️ IMPORTANT - Altitude Calibration:</strong><br>
 * The altitude value is calculated by the drone's firmware from barometric pressure.
 * This calculation may have a significant offset (typically +100 to +150 meters) due to:
 * <ul>
 *   <li>Incorrect reference sea-level pressure in firmware</li>
 *   <li>Factory calibration variations</li>
 *   <li>Temperature compensation issues</li>
 * </ul>
 * 
 * For accurate altitude measurements:
 * <ul>
 *   <li>Use the <strong>pressure</strong> value directly (accurate)</li>
 *   <li>Calculate altitude yourself using: {@code h = 44330 * (1 - (P/101325)^0.1903)}</li>
 *   <li>Or apply a calibration offset based on known elevation</li>
 * </ul>
 * 
 * Example: At sea level with 1006 hPa pressure:
 * <ul>
 *   <li>Calculated altitude: ~60m (using standard formula)</li>
 *   <li>Drone reports: ~168m (offset of +108m)</li>
 * </ul>
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Barometric_formula">Barometric Formula</a>
 */
public class Altitude implements Serializable
{
    public static final byte ALTITUDE_SIZE = 16;

    private float temperature;
    private float pressure;
    private float altitude;
    private float rangeHeight;

    public Altitude(int temperature, int rangeHeight, int pressure, int altitude)
    {
        this.temperature = temperature;
        this.rangeHeight = rangeHeight;
        this.pressure = pressure;
        this.altitude = altitude;
    }

    public Altitude()
    {

    }

    @Override
    public byte getSize()
    {
        return ALTITUDE_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putFloat(temperature);
        buffer.putFloat(pressure);
        buffer.putFloat(altitude);
        buffer.putFloat(rangeHeight);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.temperature = buffer.getFloat();
        this.pressure = buffer.getFloat();
        this.altitude = buffer.getFloat();
        this.rangeHeight = buffer.getFloat();
    }
    
    // Getters for sensor data access
    
    /**
     * Gets the sensor die temperature in Celsius.
     * Note: This is typically 10-15°C cooler than ambient air temperature.
     * @return Temperature in Celsius
     */
    public float getTemperature() { return temperature; }
    
    /**
     * Gets the atmospheric pressure in Pascals.
     * This is the most accurate sensor reading.
     * @return Pressure in Pascals (Pa). Divide by 100 for hPa, or 1000 for kPa.
     */
    public float getPressure() { return pressure; }
    
    /**
     * Gets the barometric altitude in meters above sea level.
     * <p><strong>⚠️ WARNING:</strong> This value is calculated by drone firmware and may have
     * a significant calibration offset (typically +100 to +150 meters too high).
     * For accurate altitude, calculate from pressure using the barometric formula,
     * or apply a calibration offset based on known elevation.
     * 
     * @return Altitude in meters (may require calibration offset)
     */
    public float getAltitude() { return altitude; }
    
    /**
     * Gets the height from the range sensor in meters.
     * This is the distance measured by the bottom-facing range sensor (when flying).
     * @return Range height in meters
     */
    public float getRangeHeight() { return rangeHeight; }

    @Override
    public String toString() {
        return "Altitude [temperature=" + temperature + ", altitude=" + altitude + ", pressure=" + pressure
                + ", rangeHeight=" + rangeHeight + "]";
    }

    
    
}
