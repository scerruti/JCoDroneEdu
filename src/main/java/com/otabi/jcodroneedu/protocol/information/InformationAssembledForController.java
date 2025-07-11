package com.otabi.jcodroneedu.protocol.information;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class InformationAssembledForController implements Serializable
{
    public static final byte INFORMATION_ASSEMBLED_FOR_CONTROLLER_SIZE = 18;

    private short angleRoll;
    private short anglePitch;
    private short angleYaw;

    private short rpm;

    private short positionX;
    private short positionY;
    private short positionZ;

    private byte speedX;
    private byte speedY;

    private byte rangeHeight;

    private byte rssi;


    public InformationAssembledForController(short angleRoll, short anglePitch, short angleYaw, short rpm, short positionX, short positionY, short positionZ, byte speedX, byte speedY, byte rangeHeight, byte rssi)
    {
        this.angleRoll = angleRoll;
        this.anglePitch = anglePitch;
        this.angleYaw = angleYaw;
        this.rpm = rpm;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.speedX = speedX;
        this.speedY = speedY;
        this.rangeHeight = rangeHeight;
        this.rssi = rssi;
    }

    public InformationAssembledForController()
    {

    }

    @Override
    public byte getSize()
    {
        return INFORMATION_ASSEMBLED_FOR_CONTROLLER_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.angleRoll = buffer.getShort();
        this.anglePitch = buffer.getShort();
        this.angleYaw = buffer.getShort();

        this.rpm = buffer.getShort();

        this.positionX = buffer.getShort();
        this.positionY = buffer.getShort();
        this.positionZ = buffer.getShort();

        this.speedX = buffer.get();
        this.speedY = buffer.get();

        this.rangeHeight = buffer.get();

        this.rssi = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(this.angleRoll);
        buffer.putShort(this.anglePitch);
        buffer.putShort(this.angleYaw);

        buffer.putShort(this.rpm);

        buffer.putShort(this.positionX);
        buffer.putShort(this.positionY);
        buffer.putShort(this.positionZ);

        buffer.put(this.speedX);
        buffer.put(this.speedY);

        buffer.put(this.rangeHeight);

        buffer.put(this.rssi);
    }
}
