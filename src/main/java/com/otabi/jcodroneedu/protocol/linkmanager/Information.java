package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.DroneSystem;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Information implements Serializable
{
    public static final byte INFORMATION_LENGTH = 13;

    private DroneSystem.ModeUpdate modeUpdate;
    private DroneSystem.ModelNumber modelNumber;
    private Version version;
    private short year;
    private byte month;
    private byte day;

    public Information()
    {
    }

    public Information(DroneSystem.ModeUpdate modeUpdate, DroneSystem.ModelNumber modelNumber, Version version, short year, byte month, byte day) {
        this.modeUpdate = modeUpdate;
        this.modelNumber = modelNumber;
        this.version = version;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public byte getSize() {
        return INFORMATION_LENGTH;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        modeUpdate = DroneSystem.ModeUpdate.fromValue(buffer.get());
        modelNumber = DroneSystem.ModelNumber.fromValue(buffer.getInt());
        version.unpack(buffer);
        year = buffer.getShort();
        month = buffer.get();
        day = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(modeUpdate.getValue());
        buffer.putInt(modelNumber.getValue());
        version.pack(buffer);
        buffer.putShort(year);
        buffer.put(month);
        buffer.put(day);
    }

    public DroneSystem.ModelNumber getModelNumber()
    {
        return modelNumber;
    }

    public Version getVersion()
    {
        return version;
    }
}