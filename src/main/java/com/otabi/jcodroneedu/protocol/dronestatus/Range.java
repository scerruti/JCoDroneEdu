package com.otabi.jcodroneedu.protocol.dronestatus;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class Range implements Serializable
{
    public static final byte RANGE_SIZE = 12;

    private short left;
    private short front;
    private short right;
    private short rear;
    private short top;
    private short bottom;

    public Range(short left, short front, short right, short rear, short top, short bottom)
    {
        this.left = left;
        this.front = front;
        this.right = right;
        this.rear = rear;
        this.top = top;
        this.bottom = bottom;
    }

    public Range()
    {

    }

    @Override
    public byte getSize()
    {
        return RANGE_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.left = buffer.getShort();
        this.front = buffer.getShort();
        this.right = buffer.getShort();
        this.rear = buffer.getShort();
        this.top = buffer.getShort();
        this.bottom = buffer.getShort();
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.putShort(left);
        buffer.putShort(front);
        buffer.putShort(right);
        buffer.putShort(rear);
        buffer.putShort(top);
        buffer.putShort(bottom);
    }
}
