package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class RawFlow implements Serializable
{
    public static final byte RAW_FLOW_SIZE = 8;

    private float x;
    private float y;

    public RawFlow(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public RawFlow()
    {

    }

    /**
     * Gets the X-axis flow velocity.
     * @return X-axis velocity in meters
     */
    public float getX() {
        return x;
    }

    /**
     * Gets the Y-axis flow velocity.
     * @return Y-axis velocity in meters
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the X-axis flow velocity.
     * @param x X-axis velocity in meters
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets the Y-axis flow velocity.
     * @param y Y-axis velocity in meters
     */
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public byte getSize()
    {
        return RAW_FLOW_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.x = buffer.getFloat();
        this.y = buffer.getFloat();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putFloat(this.x);
        buffer.putFloat(this.y);
    }
}
