package com.otabi.jcodroneedu.protocol.control;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Position implements Serializable
{

    public static final int SIZE = 20;

    public float positionX = 0.0f;
    public float positionY = 0.0f;
    public float positionZ = 0.0f;
    public float velocity = 0.0f;
    public short heading = 0;
    public short rotationalVelocity = 0;

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putFloat(this.positionX);
        buffer.putFloat(this.positionY);
        buffer.putFloat(this.positionZ);
        buffer.putFloat(this.velocity);
        buffer.putShort(this.heading);
        buffer.putShort(this.rotationalVelocity);
    }

    @Override
    public byte getSize()
    {
        return SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.positionX = buffer.getFloat();
        this.positionY = buffer.getFloat();
        this.positionZ = buffer.getFloat();
        this.velocity = buffer.getFloat();
        this.heading = buffer.getShort();
        this.rotationalVelocity = buffer.getShort();
    }
}