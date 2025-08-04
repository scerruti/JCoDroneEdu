package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

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
    public float getTemperature() { return temperature; }
    public float getPressure() { return pressure; }
    public float getAltitude() { return altitude; }
    public float getRangeHeight() { return rangeHeight; }
}
