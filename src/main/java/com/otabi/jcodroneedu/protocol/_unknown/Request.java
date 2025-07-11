package com.otabi.jcodroneedu.protocol._unknown;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Request implements Serializable
{
    public static final byte REQUEST_SIZE = 1;
    private DataType dataType;

    public Request(DataType dataType)
    {
        this.dataType = dataType;
    }

    public byte getSize() {
        return REQUEST_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(dataType.value());
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.dataType = DataType.fromByte(buffer.get());
    }
}
