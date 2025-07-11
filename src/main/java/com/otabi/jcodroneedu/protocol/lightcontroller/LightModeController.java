package com.otabi.jcodroneedu.protocol.lightcontroller;

import java.nio.ByteBuffer;

public class LightModeController extends LightMode
{
    public LightModeController(LightModes mode, short interval)
    {
        super(mode, interval);
    }

    @Override
    public void unpack(ByteBuffer buffer)
    {
        setMode(LightModesController.fromValue(buffer.get()));
    }
}
