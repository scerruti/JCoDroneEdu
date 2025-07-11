package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Motion implements Serializable
{
    public static final byte MOTION_SIZE = 18;

    private short accelX;
    private short accelY;
    private short accelZ;
    private short gyroRoll;
    private short gyroPitch;
    private short gyroYaw;
    private short angleRoll;
    private short anglePitch;
    private short angleYaw;

    public Motion(short accelX, short accelY, short accelZ, short gyroRoll, short gyroPitch, short gyroYaw, short angleRoll, short anglePitch, short angleYaw)
    {
        this.accelX = accelX;
        this.accelY = accelY;
        this.accelZ = accelZ;
        this.gyroRoll = gyroRoll;
        this.gyroPitch = gyroPitch;
        this.gyroYaw = gyroYaw;
        this.angleRoll = angleRoll;
        this.anglePitch = anglePitch;
        this.angleYaw = angleYaw;
    }

    public Motion()
    {

    }

    @Override
    public byte getSize()
    {
        return MOTION_SIZE;
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
        this.angleRoll = buffer.getShort();
        this.anglePitch = buffer.getShort();
        this.angleYaw = buffer.getShort();
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
        buffer.putShort(this.angleRoll);
        buffer.putShort(this.anglePitch);
        buffer.putShort(this.angleYaw);
    }
}
