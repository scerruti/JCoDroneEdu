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

    public Flow(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.y = z;
    }

    public Flow()
    {

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
