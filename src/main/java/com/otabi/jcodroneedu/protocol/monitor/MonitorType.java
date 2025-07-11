package com.otabi.jcodroneedu.protocol.monitor;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class MonitorType implements Serializable
{
    public static final byte MONITOR_TYPE_SIZE = 1;

    private MonitorHeaderType monitorHeaderType = MonitorHeaderType.Monitor8;

    public MonitorType() {
    }

    @Override
    public byte getSize() {
        return MONITOR_TYPE_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        monitorHeaderType = MonitorHeaderType.fromByte(buffer.get());
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(this.monitorHeaderType.getValue());
    }
}

