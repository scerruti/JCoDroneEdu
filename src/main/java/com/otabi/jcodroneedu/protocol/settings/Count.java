package com.otabi.jcodroneedu.protocol.settings;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Count implements Serializable
{
    public static final byte COUNT_SIZE = 14;

    private int timeSystem;
    private int timeFlight;
    private short countTakeOff;
    private short countLanding;
    private short countAccident;

    public Count(int timeSystem, int timeFlight, short countTakeOff, short countLanding, short countAccident)
    {
        this.timeSystem = timeSystem;
        this.timeFlight = timeFlight;
        this.countTakeOff = countTakeOff;
        this.countLanding = countLanding;
        this.countAccident = countAccident;
    }

    @Override
    public byte getSize()
    {
        return COUNT_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.timeSystem = buffer.getInt();
        this.timeFlight = buffer.getInt();
        this.countTakeOff = buffer.getShort();
        this.countLanding = buffer.getShort();
        this.countAccident = buffer.getShort();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putInt(this.timeSystem);
        buffer.putInt(this.timeFlight);
        buffer.putShort(this.countTakeOff);
        buffer.putShort(this.countLanding);
        buffer.putShort(this.countAccident);
    }
}
