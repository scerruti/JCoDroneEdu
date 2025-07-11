package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Pairing implements Serializable
{
    public static final byte PAIRING_SIZE = 11;

    private short address0;
    private short address1;
    private short address2;
    private byte scramble;
    private byte channel0;
    private byte channel1;
    private byte channel2;
    private byte channel3;

    public Pairing(short address0, short address1, short address2, byte scramble, byte channel0, byte channel1, byte channel2, byte channel3)
    {
        this.address0 = address0;
        this.address1 = address1;
        this.address2 = address2;
        this.scramble = scramble;
        this.channel0 = channel0;
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.channel3 = channel3;
    }

    public Pairing()
    {

    }

    @Override
    public byte getSize()
    {
        return PAIRING_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.address0 = buffer.getShort();
        this.address1 = buffer.getShort();
        this.address2 = buffer.getShort();
        this.scramble = buffer.get();
        this.channel0 = buffer.get();
        this.channel1 = buffer.get();
        this.channel2 = buffer.get();
        this.channel3 = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(this.address0);
        buffer.putShort(this.address1);
        buffer.putShort(this.address2);
        buffer.put(this.scramble);
        buffer.put(this.channel0);
        buffer.put(this.channel1);
        buffer.put(this.channel2);
        buffer.put(this.channel3);
    }
}
