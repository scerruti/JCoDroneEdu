package com.otabi.jcodroneedu.protocol.settings;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Bias implements Serializable
{
    public static final byte BIAS_SIZE = 12;

    private short accelX;
    private short accelY;
    private short accelZ;
    private short gyroRoll;
    private short gyroPitch;
    private short gyroYaw;

    public Bias(short accelX, short accelY, short accelZ, short gyroRoll, short gyroPitch, short gyroYaw)
    {
        this.accelX = accelX;
        this.accelY = accelY;
        this.accelZ = accelZ;
        this.gyroRoll = gyroRoll;
        this.gyroPitch = gyroPitch;
        this.gyroYaw = gyroYaw;
    }

    @Override
    public byte getSize()
    {
        return BIAS_SIZE;
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
        buffer.putShort(this.accelX);
        buffer.putShort(this.accelY);
        buffer.putShort(this.accelZ);
        buffer.putShort(this.gyroRoll);
        buffer.putShort(this.gyroPitch);
        buffer.putShort(this.gyroYaw);
    }
}
