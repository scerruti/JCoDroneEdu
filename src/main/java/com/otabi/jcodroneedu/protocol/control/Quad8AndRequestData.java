package com.otabi.jcodroneedu.protocol.control;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Quad8AndRequestData implements Serializable
{

    public static final int SIZE = 5;

    public byte roll = 0;
    public byte pitch = 0;
    public byte yaw = 0;
    public byte throttle = 0;
    public DataType dataType = DataType.None_;


    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(this.roll);
        buffer.put(this.pitch);
        buffer.put(this.yaw);
        buffer.put(this.throttle);
        buffer.put(this.dataType.value());
    }

    @Override
    public byte getSize() {
        return (byte) SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.roll = buffer.get();
        this.pitch = buffer.get();
        this.yaw = buffer.get();
        this.throttle = buffer.get();
        this.dataType = DataType.fromByte(buffer.get());
    }
}
