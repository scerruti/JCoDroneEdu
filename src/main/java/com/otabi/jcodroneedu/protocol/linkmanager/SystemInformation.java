package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class SystemInformation implements Serializable
{
    public static final byte SYSTEM_INFORMATION_SIZE = 8;

    private int crc32bootloader;
    private int crc32application;

    public SystemInformation()
    {
    }

    public SystemInformation(int crc32bootloader, int crc32application)
    {
        this.crc32bootloader = crc32bootloader;
        this.crc32application = crc32application;
    }

    @Override
    public byte getSize()
    {
        return SYSTEM_INFORMATION_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.crc32bootloader = buffer.getInt();
        this.crc32application = buffer.getInt();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putInt(this.crc32bootloader);
        buffer.putInt(this.crc32application);
    }
}
