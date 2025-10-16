package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Version implements Serializable
{
    public static final byte VERSION_SIZE = 4;

    private short build;
    private byte minor;
    private byte major;

    public Version(short build, byte minor, byte major)
    {
        this.build = build;
        this.minor = minor;
        this.major = major;
    }

    @Override
    public byte getSize()
    {
        return VERSION_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(build);
        buffer.put((byte) minor);
        buffer.put((byte) major);
    }

        @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.build = buffer.getShort();
        this.minor = buffer.get();
        this.major = buffer.get();
    }

    @Override
    public String toString()
    {
        return String.format("%d.%d.%d", major & 0xFF, minor & 0xFF, build & 0xFFFF);
    }
}
