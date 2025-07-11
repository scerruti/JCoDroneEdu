package com.otabi.jcodroneedu.protocol.monitor;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

class Monitor4 implements Serializable
{
    public static final int MONITOR_4_SIZE = 6;

    private int systemTime;
    private MonitorDataType monitorDataType = MonitorDataType.F32;
    private byte index;

    public Monitor4()
    {
    }

    @Override
    public byte getSize()
    {
        return MONITOR_4_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        systemTime = buffer.getInt();
        monitorDataType = MonitorDataType.fromByte(buffer.get());
        index = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putInt(this.systemTime);
        buffer.put(this.monitorDataType.getValue());
        buffer.put(this.index);
    }
}
