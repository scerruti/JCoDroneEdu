package com.otabi.jcodroneedu.protocol;

import java.nio.ByteBuffer;

public class Command implements Serializable
{
    public static final byte COMMAND_SIZE = 2;

    private CommandType commandType;
    private byte option;

    public Command(CommandType commandType, byte option)
    {
        this.commandType = commandType;
        this.option = option;
    }

    public Command()
    {

    }

    @Override
    public byte getSize()
    {
        return COMMAND_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.commandType = CommandType.fromValue(buffer.get());
        this.option = buffer.get();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(commandType.getValue());
        buffer.put(option);
    }
}
