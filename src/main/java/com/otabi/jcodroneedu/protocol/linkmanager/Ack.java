package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;

public class Ack implements Serializable
{
    public static final byte ACK_SIZE = 5;
    private int systemTime;
    private DataType dataType;

    public byte getSize() {
        return ACK_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putInt(systemTime)
                .put(dataType.value());
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.systemTime = buffer.getInt();
        this.dataType = DataType.fromByte(buffer.get());
    }
}
