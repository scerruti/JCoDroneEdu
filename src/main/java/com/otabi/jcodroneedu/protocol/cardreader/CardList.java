package com.otabi.jcodroneedu.protocol.cardreader;

import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

public class CardList implements Serializable
{
    public static final byte CARD_LIST_SIZE = 15;
    
    private byte index;
    private byte size;

    private byte cardIndex;
    private byte[] card;

    public CardList(byte index, byte size, byte cardIndex, byte[] card)
    {
        this.index = index;
        this.size = size;
        this.cardIndex = cardIndex;
        this.card = card;
    }

    public CardList()
    {

    }

    @Override
    public byte getSize()
    {
        return CARD_LIST_SIZE;
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.index = buffer.get();
        this.size = buffer.get();
        this.cardIndex = buffer.get();
        this.card = new byte[12];
        buffer.get(card);
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(this.index);
        buffer.put(this.size);
        buffer.put(this.cardIndex);
        buffer.put(this.card);
    }
}

