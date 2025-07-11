package com.otabi.jcodroneedu.protocol.cardreader;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class CardColor implements Serializable
{
    public static final byte CARD_COLOR_SIZE = 19;

    private byte[][] hsvl;
    private byte[] color;
    private byte card;

    public CardColor(byte[][] hsvl, byte[] color, byte card)
    {
        this.hsvl = hsvl;
        this.color = color;
        this.card = card;
    }

    public CardColor()
    {

    }

    @Override
    public byte getSize()
    {
        return CARD_COLOR_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        for (int i = 0; i <2 ; i++)
            buffer.put(hsvl[i]);
        buffer.put(color);
        buffer.put(card);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        hsvl = new byte[3][2];
        for (int i = 0; i <2 ; i++)
            buffer.get(hsvl[i]);
        color = new byte[2];
        buffer.get(color);
        buffer.put(card);

    }
}
