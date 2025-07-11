package com.otabi.jcodroneedu.protocol.information;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class InformationAssembledForEntry implements Serializable
{
    public static final byte INFORMATION_ASSEMBLED_FOR_ENTRY_SIZE = 18;

    private short angleRoll;
    private short anglePitch;
    private short angleYaw;

    private short positionX;
    private short positionY;
    private short positionZ;

    private byte rangeHeight;

    private float altitude;

    @Override
    public byte getSize()
    {
        return INFORMATION_ASSEMBLED_FOR_ENTRY_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.angleRoll = buffer.getShort();
        this.anglePitch = buffer.getShort();
        this.angleYaw = buffer.getShort();

        this.positionX = buffer.getShort();
        this.positionY = buffer.getShort();
        this.positionZ = buffer.getShort();

        this.rangeHeight = buffer.get();

        this.altitude = buffer.getFloat();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(this.angleRoll);
        buffer.putShort(this.anglePitch);
        buffer.putShort(this.angleYaw);

        buffer.putShort(this.positionX);
        buffer.putShort(this.positionY);
        buffer.putShort(this.positionZ);

        buffer.put(this.rangeHeight);

        buffer.putFloat(this.altitude);
    }

}
