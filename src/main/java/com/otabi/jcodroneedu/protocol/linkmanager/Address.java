package com.otabi.jcodroneedu.protocol.linkmanager;

import java.nio.ByteBuffer;
import java.util.stream.Collectors;


import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

public class Address implements Serializable {
    public static final int ADDRESS_SIZE = 16;

    private byte[] address;

    public Address()
    {
    }

    public Address(byte[] address) {
        this.address = address;
    }

    public byte getSize() {
        return ADDRESS_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        address = new byte[getSize()];
        buffer.get(address, 0, getSize());
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(this.address);
    }

    public byte[] getAddress() {
        return address;
    }

}
