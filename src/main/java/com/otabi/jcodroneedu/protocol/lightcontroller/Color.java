package com.otabi.jcodroneedu.protocol.lightcontroller;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

import static com.otabi.jcodroneedu.protocol.Validator.isValidUnsignedByte;

public class Color implements Serializable
{
    public static final byte COLOR_SIZE = 3;

    private byte r;
    private byte g;
    private byte b;

    public Color(byte r, byte g, byte b)
    {
        // Interpret the incoming signed Java bytes as unsigned values in 0..255
        if (!(isValidUnsignedByte(r & 0xFF) && isValidUnsignedByte(g & 0xFF) && isValidUnsignedByte(b & 0xFF)))
        {
            throw new IllegalArgumentException("Colors must be in the range of 0 to 255.");
        }
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.r = buffer.get();
        this.g = buffer.get();
        this.b = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(r);
        buffer.put(g);
        buffer.put(b);
    }

    @Override
    public byte getSize()
    {
        return COLOR_SIZE;
    }
}
