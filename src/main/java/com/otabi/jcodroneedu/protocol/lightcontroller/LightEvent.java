package com.otabi.jcodroneedu.protocol.lightcontroller;


import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class LightEvent implements Serializable {
    public static final byte LIGHT_EVENT_SIZE = 4;

    protected byte event;
    protected short interval;
    protected byte repeat;

    public LightEvent(byte event, short interval, byte repeat) {
        this.event = event;
        this.interval = interval;
        this.repeat = repeat;
    }

    public LightEvent()
    {

    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.event = buffer.get();
        this.interval = buffer.getShort();
        this.repeat = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(this.event);
        buffer.putShort(this.interval);
        buffer.put(this.repeat);
    }

    @Override
    public byte getSize()
    {
        return LIGHT_EVENT_SIZE;
    }
}
