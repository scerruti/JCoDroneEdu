package com.otabi.jcodroneedu.protocol.settings;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Weight implements Serializable
{
    public static final byte WEIGHT_SIZE = 4;

    private float weight;

    public Weight(float weight)
    {
        this.weight = weight;
    }

    @Override
    public byte getSize()
    {
        return WEIGHT_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.weight = buffer.getFloat();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putFloat(this.weight);
    }
}
