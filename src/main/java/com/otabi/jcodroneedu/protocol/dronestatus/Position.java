package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Position implements Serializable
{
    public static final byte POSITION_SIZE = 12;

    private int x;
    private int y;
    private int z;

    public Position(int x, int y, int z)
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
        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.z = buffer.getInt();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.putInt(z);
    }
}
