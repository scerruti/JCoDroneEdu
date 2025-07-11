package com.otabi.jcodroneedu.protocol.monitor;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

class Monitor0 implements Serializable
{
    public static final byte MONITOR_0_SIZE = 2;

    private MonitorDataType monitorDataType = MonitorDataType.F32;
    private byte index;

    public Monitor0()
    {
    }

    @Override
    public byte getSize()
    {
        return MONITOR_0_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        buffer.put(this.monitorDataType.getValue());
        buffer.put(this.index);
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        monitorDataType = MonitorDataType.fromByte(buffer.get());
        index = buffer.get();
    }
}
