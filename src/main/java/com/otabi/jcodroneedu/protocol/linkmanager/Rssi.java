package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Rssi implements Serializable
{
    public static final byte RSSI_SIZE = 1;

    private byte rssi;

    public Rssi(byte rssi)
    {
        this.rssi = rssi;
    }

    public Rssi()
    {

    }

    /**
     * Returns the RSSI value in dBm as a signed byte mapped to int.
     * Typical BLE values are negative (e.g., -40 strong, -90 weak).
     */
    public int getRssi() {
        return (int) rssi;
    }

    @Override
    public byte getSize()
    {
        return RSSI_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.rssi = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(rssi);
    }
}
