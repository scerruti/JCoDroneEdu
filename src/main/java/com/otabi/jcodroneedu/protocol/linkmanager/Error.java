package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Error implements Serializable
{
    public static final byte ERROR_SIZE = 16;

    private long systemTime;
    private int errorFlagsForSensor;
    private int errorFlagsForState;

    public Error()
    {
    }

    public Error(long systemTime, int errorFlagsForSensor, int errorFlagsForState)
    {
        this.systemTime = systemTime;
        this.errorFlagsForSensor = errorFlagsForSensor;
        this.errorFlagsForState = errorFlagsForState;
    }

    @Override
    public byte getSize()
    {
        return ERROR_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        systemTime = buffer.getLong();
        errorFlagsForSensor = buffer.getInt();
        errorFlagsForState = buffer.getInt();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putLong(systemTime);
        buffer.putInt(errorFlagsForSensor);
        buffer.putInt(errorFlagsForState);
    }
}
