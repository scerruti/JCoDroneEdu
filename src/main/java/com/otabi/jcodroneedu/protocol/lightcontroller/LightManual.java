package com.otabi.jcodroneedu.protocol.lightcontroller;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class LightManual implements Serializable
{
    public static final byte MANUAL_SIZE = 3;

    private short flags;
    private byte brightness;

    public LightManual(short flags, byte brightness)
    {
        this.flags = flags;
        this.brightness = brightness;
    }

    public LightManual()
    {

    }

    public byte getSize()
    {
        return MANUAL_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.flags = buffer.getShort();
        this.brightness = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(flags);
        buffer.put(brightness);
    }
}
