package com.otabi.jcodroneedu.protocol.lightcontroller;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;

public class LightModeColor extends LightMode {

    private Color color;

    public LightModeColor(LightModes mode, Color color, short interval) {
        super(mode, interval);
        this.color = color;
    }

    public byte getSize() {
        return (byte) (super.getSize() + color.getSize());
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        super.pack(buffer);
        color.pack(buffer);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        super.unpack(buffer);
        color.unpack(buffer);
    }
}
