package com.otabi.jcodroneedu.protocol.controllerinput;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.DroneSystem;

import java.nio.ByteBuffer;

public class JoystickBlock implements Serializable
{
    public static final byte JOYSTICK_BLOCK_SIZE = 4;

    private byte x;
    private byte y;
    private DroneSystem.JoystickDirection direction;
    private DroneSystem.JoystickEvent event;

    public JoystickBlock(byte x, byte y, DroneSystem.JoystickDirection direction, DroneSystem.JoystickEvent event)
    {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.event = event;
    }

    public JoystickBlock()
    {
        this.x = 0;
        this.y = 0;
        this.direction = DroneSystem.JoystickDirection.None_;
        this.event = DroneSystem.JoystickEvent.None_;
    }

    @Override
    public byte getSize()
    {
        return JOYSTICK_BLOCK_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.x = buffer.get();
        this.y = buffer.get();
        this.direction = DroneSystem.JoystickDirection.fromValue(buffer.get());
        this.event = DroneSystem.JoystickEvent.fromValue(buffer.get());
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(this.x);
        buffer.put(this.y);
        buffer.put(this.direction.getValue());
        buffer.put(this.event.getValue());
    }

    // Getter methods
    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public DroneSystem.JoystickDirection getDirection() {
        return direction;
    }

    public DroneSystem.JoystickEvent getEvent() {
        return event;
    }
}
