package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Registration implements Serializable
{
    public final static byte REGISTRATION_SIZE = 5;

    private Address address;
    private short year;
    private byte month;
    private byte key;
    private boolean flagValid;

    public Registration(Address address, short year, byte month, byte key, boolean flagValid)
    {
        this.address = address;
        this.year = year;
        this.month = month;
        this.key = key;
        this.flagValid = flagValid;
    }

    public Registration()
    {

    }

    @Override
    public byte getSize()
    {
        return REGISTRATION_SIZE + Address.ADDRESS_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        address.unpack(buffer);
        year = buffer.getShort();
        month = buffer.get();
        key = buffer.get();
        flagValid = buffer.get() != 0;
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        address.pack(buffer);
        buffer.putShort(year);
        buffer.put(month);
        buffer.put(key);
        buffer.put((byte) (flagValid ? 1 : 0));
    }
}
