package com.otabi.jcodroneedu.protocol._unknown;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class RequestOption implements Serializable
{
    public static final byte REQUEST_SIZE = 5;
    private DataType dataType;
    private int option;

    public byte getSize() {
        return REQUEST_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(dataType.value());
        buffer.putInt(option);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.dataType = DataType.fromByte(buffer.get());
        this.option = buffer.getInt();
    }
}
