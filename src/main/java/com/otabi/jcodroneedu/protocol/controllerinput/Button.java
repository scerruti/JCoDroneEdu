package com.otabi.jcodroneedu.protocol.controllerinput;

import com.otabi.jcodroneedu.DroneSystem;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Button implements Serializable
{
    public static final byte BUTTON_SIZE = 3;

    private short button;
    private DroneSystem.ButtonEvent event;

    public Button(short button, DroneSystem.ButtonEvent event)
    {
        this.button = button;
        this.event = event;
    }

    public Button()
    {

    }

    @Override
    public byte getSize()
    {
        return BUTTON_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.button = buffer.getShort();
        this.event = DroneSystem.ButtonEvent.fromValue(buffer.get());
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(button);
        buffer.put(event.getValue());
    }
}
