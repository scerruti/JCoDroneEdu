package com.otabi.jcodroneedu.protocol.settings;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Trim implements Serializable
{
    public static final byte TRIM_SIZE = 8;

    private short roll;
    private short pitch;
    private short yaw;
    private short throttle;

    public Trim(short roll, short pitch, short yaw, short throttle)
    {
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
        this.throttle = throttle;
    }

    @Override
    public byte getSize()
    {
        return TRIM_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.roll = buffer.getShort();
        this.pitch = buffer.getShort();
        this.yaw = buffer.getShort();
        this.throttle = buffer.getShort();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(this.roll);
        buffer.putShort(this.pitch);
        buffer.putShort(this.yaw);
        buffer.putShort(this.throttle);
    }
}
