package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class UpdateLocation implements Serializable
{
    public static final byte UPDATE_LOCATION_SIZE = 2;

    private short indexBlockNext;

    public UpdateLocation()
    {
    }

    public UpdateLocation(short indexBlockNext)
    {
        this.indexBlockNext = indexBlockNext;
    }

    @Override
    public byte getSize()
    {
        return UPDATE_LOCATION_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.indexBlockNext = buffer.getShort();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(indexBlockNext);
    }
}
