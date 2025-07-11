package com.otabi.jcodroneedu.protocol.cardreader;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class CardRaw implements Serializable
{
    public static final byte CARD_RAW_SIZE = 37;

    private byte[][] rgbRaw;
    private byte[][] rgb;
    private byte[][] hsvl;
    private byte[] color;
    private byte card;

    public CardRaw(byte[][] rgbRaw, byte[][] rgb, byte[][] hsvl, byte[] color, byte card)
    {
        this.rgbRaw = rgbRaw;
        this.rgb = rgb;
        this.hsvl = hsvl;
        this.color = color;
        this.card = card;
    }

    public CardRaw()
    {

    }

    @Override
    public byte getSize()
    {
        return CARD_RAW_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        for (int i = 0; i <2 ; i++)
            buffer.put(rgbRaw[i]);
        for (int i = 0; i <2 ; i++)
            buffer.put(rgb[i]);
        for (int i = 0; i <2 ; i++)
            buffer.put(hsvl[i]);
        buffer.put(color);
        buffer.put(card);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        rgbRaw = new byte[3][2];
        for (int i = 0; i <2 ; i++)
            buffer.get(rgbRaw[i]);
        rgb = new byte[3][2];
        for (int i = 0; i <2 ; i++)
            buffer.get(rgb[i]);
        hsvl = new byte[3][2];
        for (int i = 0; i <2 ; i++)
            buffer.get(hsvl[i]);
        color = new byte[2];
        buffer.get(color);
        buffer.put(card);

    }
}
