package com.otabi.jcodroneedu.protocol.cardreader;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class CardRange implements Serializable
{
    public static final byte CARD_RANGE_SIZE = 24;

    private byte[][][] range;

    public CardRange(byte[][][] range)
    {
        this.range = range;
    }

    public CardRange()
    {

    }

    @Override
    public byte getSize()
    {
        return CARD_RANGE_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.range = new byte[2][3][2];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 3; j++)
                buffer.get(this.range[i][j]);
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 3; j++)
                buffer.put(this.range[i][j]);

    }
}
