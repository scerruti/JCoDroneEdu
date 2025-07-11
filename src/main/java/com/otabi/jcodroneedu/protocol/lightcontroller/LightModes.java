package com.otabi.jcodroneedu.protocol.lightcontroller;

import java.nio.ByteBuffer;

public interface LightModes
{
    public byte getValue();

    public LightModes getModeFromBuffer(ByteBuffer buffer);
}
