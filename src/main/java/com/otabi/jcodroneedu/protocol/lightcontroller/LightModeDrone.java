package com.otabi.jcodroneedu.protocol.lightcontroller;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;

public class LightModeDrone extends LightMode
{
    public LightModeDrone(LightModes mode, short interval)
    {
        super(mode, interval);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        setMode(LightModesController.fromValue(buffer.get()));
    }
}
