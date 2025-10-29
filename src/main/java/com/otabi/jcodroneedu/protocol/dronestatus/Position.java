package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Position implements Serializable
{
    public static final byte POSITION_SIZE = 12;

    private float x;
    private float y;
    private float z;

    public Position(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position()
    {

    }

    @Override
    public byte getSize()
    {
        return POSITION_SIZE;
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
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
    }
    
    // Position getters (in meters from takeoff point)
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
}
