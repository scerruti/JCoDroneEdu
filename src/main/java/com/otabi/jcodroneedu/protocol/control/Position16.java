package com.otabi.jcodroneedu.protocol.control;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Position16 implements Serializable
{

    public static final int SIZE = 12;

    public short positionX = 0;
    public short positionY = 0;
    public short positionZ = 0;
    public short velocity = 0;
    public short heading = 0;
    public short rotationalVelocity = 0;


    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(this.positionX);
        buffer.putShort(this.positionY);
        buffer.putShort(this.positionZ);
        buffer.putShort(this.velocity);
        buffer.putShort(this.heading);
        buffer.putShort(this.rotationalVelocity);
    }

    @Override
    public byte getSize() {
        return (byte) SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.positionX = buffer.getShort();
        this.positionY = buffer.getShort();
        this.positionZ = buffer.getShort();
        this.velocity = buffer.getShort();
        this.heading = buffer.getShort();
        this.rotationalVelocity = buffer.getShort();
    }
}
