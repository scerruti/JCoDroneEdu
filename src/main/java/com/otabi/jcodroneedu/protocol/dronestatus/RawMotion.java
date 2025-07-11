package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class RawMotion implements Serializable
{
    public static final byte RAW_MOTION_SIZE = 12;

    private short accelX;
    private short accelY;
    private short accelZ;
    private short gyroRoll;
    private short gyroPitch;
    private short gyroYaw;

    public RawMotion(short accelX, short accelY, short accelZ, short gyroRoll, short gyroPitch, short gyroYaw)
    {
        this.accelX = accelX;
        this.accelY = accelY;
        this.accelZ = accelZ;
        this.gyroRoll = gyroRoll;
        this.gyroPitch = gyroPitch;
        this.gyroYaw = gyroYaw;
    }

    public RawMotion()
    {

    }

    @Override
    public byte getSize()
    {
        return RAW_MOTION_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.accelX = buffer.getShort();
        this.accelY = buffer.getShort();
        this.accelZ = buffer.getShort();
        this.gyroRoll = buffer.getShort();
        this.gyroPitch = buffer.getShort();
        this.gyroYaw = buffer.getShort();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(accelX);
        buffer.putShort(accelY);
        buffer.putShort(accelZ);
        buffer.putShort(gyroRoll);
        buffer.putShort(gyroPitch);
        buffer.putShort(gyroYaw);
    }
}
