package com.otabi.jcodroneedu.protocol.controllerinput;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Joystick implements Serializable
{
    private JoystickBlock left;
    private JoystickBlock right;

    public Joystick(JoystickBlock left, JoystickBlock right)
    {
        this.left = left;
        this.right = right;
    }

    public Joystick()
    {

    }

    @Override
    public byte getSize()
    {
        return (byte) (left.getSize() + right.getSize());
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        left.unpack(buffer);
        right.unpack(buffer);
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        left.pack(buffer);
        right.pack(buffer);
    }
}
