package com.otabi.jcodroneedu.protocol;

import com.otabi.jcodroneedu.protocol.lightcontroller.Colors;
import com.otabi.jcodroneedu.protocol.lightcontroller.LightEvent;

import java.nio.ByteBuffer;

public class CommandLightEventColors implements Serializable
{
    private Command command;
    private LightEvent lightEvent;
    private Colors color;

    public CommandLightEventColors(Command command, LightEvent lightEvent, Colors color)
    {
        this.command = command;
        this.lightEvent = lightEvent;
        this.color = color;
    }

    @Override
    public byte getSize()
    {
        return (byte) (command.getSize() + lightEvent.getSize() + 1);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.command.unpack(buffer);
        this.lightEvent.unpack(buffer);
        this.color = Colors.fromByte(buffer.get());
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        command.pack(buffer);
        lightEvent.pack(buffer);
        buffer.put(color.value());
    }
}
