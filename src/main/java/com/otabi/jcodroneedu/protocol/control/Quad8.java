package com.otabi.jcodroneedu.protocol.control;


import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Quad8 implements Serializable
{

    public static final int SIZE = 4;

    private byte roll = 0;
    private byte pitch = 0;
    private byte yaw = 0;
    private byte throttle = 0;

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(this.roll);
        buffer.put(this.pitch);
        buffer.put(this.yaw);
        buffer.put(this.throttle);
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
    }

    public void setRoll(byte roll)
    {
        this.roll = roll;
    }

    public void setPitch(byte pitch)
    {
        this.pitch = pitch;
    }

    public void setYaw(byte yaw)
    {
        this.yaw = yaw;
    }

    public void setThrottle(byte throttle)
    {
        this.throttle = throttle;
    }

    public byte getRoll()
    {
        return roll;
    }

    public byte getPitch()
    {
        return pitch;
    }

    public byte getYaw()
    {
        return yaw;
    }

    public byte getThrottle()
    {
        return throttle;
    }
}