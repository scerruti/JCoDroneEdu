package com.otabi.jcodroneedu.protocol.monitor;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

class Monitor8 implements Serializable
{
    public static final int MONITOR_8_SIZE = 10;

    private long systemTime;
    private MonitorDataType monitorDataType = MonitorDataType.F32;
    private byte index;

    public Monitor8()
    {
    }

    @Override
    public byte getSize()
    {
        return MONITOR_8_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        buffer.putLong(this.systemTime);
        buffer.put(this.monitorDataType.getValue());
        buffer.put(this.index);
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        systemTime = buffer.getLong();
        monitorDataType = MonitorDataType.fromByte(buffer.get());
        index = buffer.get();
    }
}
