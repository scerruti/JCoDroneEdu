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
        // HSVL: 2 sensors, 4 shorts each (H,S,V,L) = 8 shorts = 16 bytes
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                buffer.putShort((short) (hsvl[i][j] & 0xFF));
            }
        }
        // Color values: 2 bytes
        buffer.put(color);
        // Card: 1 byte
        buffer.put(card);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        // HSVL: 2 sensors, 4 shorts each (H,S,V,L) = 8 shorts = 16 bytes
        hsvl = new byte[2][4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                hsvl[i][j] = (byte) buffer.getShort();  // Read as short, store as byte (0-255 range)
            }
        }
        // Color values: 2 bytes
        color = new byte[2];
        buffer.get(color);
        // Card: 1 byte
        card = buffer.get();
    }

    // Getter methods for accessing the color data
    public byte[][] getHsvl() {
        return hsvl;
    }

    public byte[] getColor() {
        return color;
    }

    public byte getCard() {
        return card;
    }

    /**
     * Returns a string representation of the detected colors.
     * Format: "Front: [COLOR_NAME], Back: [COLOR_NAME]"
     * 
     * @return String describing front and back sensor colors, or "No color data" if unavailable
     */
    @Override
    public String toString() {
        if (color == null || color.length < 2) {
            return "No color data";
        }
        
        int frontColor = color[0] & 0xFF;
        int backColor = color[1] & 0xFF;
        
        return String.format("Front: %s (%d), Back: %s (%d)",
            com.otabi.jcodroneedu.DroneSystem.cardColorIndexToName(frontColor), frontColor,
            com.otabi.jcodroneedu.DroneSystem.cardColorIndexToName(backColor), backColor);
    }

    /**
     * Gets the name of the front sensor color.
     * 
     * @return Color name (e.g., "RED", "BLUE", "NONE") or "INVALID" if no data
     */
    public String getFrontColorName() {
        if (color == null || color.length < 1) {
            return "INVALID";
        }
        return com.otabi.jcodroneedu.DroneSystem.cardColorIndexToName(color[0] & 0xFF);
    }

    /**
     * Gets the name of the back sensor color.
     * 
     * @return Color name (e.g., "RED", "BLUE", "NONE") or "INVALID" if no data
     */
    public String getBackColorName() {
        if (color == null || color.length < 2) {
            return "INVALID";
        }
        return com.otabi.jcodroneedu.DroneSystem.cardColorIndexToName(color[1] & 0xFF);
    }
}
