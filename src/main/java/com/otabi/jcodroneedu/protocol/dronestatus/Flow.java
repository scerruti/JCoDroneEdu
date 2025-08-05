package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Flow implements Serializable
{
    public static final byte RAW_FLOW_SIZE = 12;

    private float x;
    private float y;
    private float z;

    public Flow(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Flow(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.z = 0.0f;
    }

    public Flow()
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
     * Gets the Z-axis flow velocity.
     * @return Z-axis velocity in meters
     */
    public float getZ() {
        return z;
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

    /**
     * Sets the Z-axis flow velocity.
     * @param z Z-axis velocity in meters
     */
    public void setZ(float z) {
        this.z = z;
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
        this.z = buffer.getFloat();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putFloat(this.x);
        buffer.putFloat(this.y);
        buffer.putFloat(this.z);
    }
}
