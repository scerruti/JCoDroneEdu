package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Attitude implements Serializable
{
    public static final byte ATTITUDE_SIZE = 6;

    private short roll;
    private short pitch;
    private short yaw;

    public Attitude(short roll, short pitch, short yaw)
    {
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Attitude()
    {

    }

    @Override
    public byte getSize()
    {
        return ATTITUDE_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.roll = buffer.getShort();
        this.pitch = buffer.getShort();
        this.yaw = buffer.getShort();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(this.roll);
        buffer.putShort(this.pitch);
        buffer.putShort(this.yaw);
    }
}
