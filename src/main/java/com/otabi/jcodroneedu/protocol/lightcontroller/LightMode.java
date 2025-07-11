package com.otabi.jcodroneedu.protocol.lightcontroller;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public abstract class LightMode implements Serializable
{
    public static final byte LIGHT_MODE_SIZE = 3;

    private LightModes mode;
    private short interval;

    public LightMode(LightModes mode, short interval)
    {
        this.mode = mode;
        this.interval = interval;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.mode = mode.getModeFromBuffer(buffer);
        this.interval = buffer.getShort();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(mode.getValue());
        buffer.putShort(interval);
    }

    @Override
    public byte getSize()
    {
        return LIGHT_MODE_SIZE;
    }

    public LightModes getMode()
    {
        return mode;
    }

    protected void setMode(LightModes mode)
    {
        this.mode = mode;
    }
}
