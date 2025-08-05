package com.otabi.jcodroneedu.protocol.display;

import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;

/**
 * Protocol class for drawing a point on the controller display screen.
 * Python equivalent: DisplayDrawPoint
 */
public class DisplayDrawPoint implements Serializable {
    
    private short x;
    private short y;
    private DisplayPixel pixel;

    /**
     * Default constructor with white pixel default.
     */
    public DisplayDrawPoint() {
        this.x = 0;
        this.y = 0;
        this.pixel = DisplayPixel.WHITE;
    }

    /**
     * Constructor with specified parameters.
     * @param x X coordinate
     * @param y Y coordinate
     * @param pixel The pixel type to draw
     */
    public DisplayDrawPoint(int x, int y, DisplayPixel pixel) {
        this.x = (short) x;
        this.y = (short) y;
        this.pixel = pixel;
    }

    // Getters and setters
    public int getX() { return x; }
    public void setX(int x) { this.x = (short) x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = (short) y; }
    
    public DisplayPixel getPixel() { return pixel; }
    public void setPixel(DisplayPixel pixel) { this.pixel = pixel; }

    @Override
    public byte getSize() {
        return 5;
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putShort(x);
        buffer.putShort(y);
        buffer.put(pixel.value());
    }

    @Override
    public byte[] toArray() {
        ByteBuffer buffer = ByteBuffer.allocate(getSize());
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        pack(buffer);
        return buffer.array();
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException {
        if (buffer.remaining() < getSize()) {
            throw new InvalidDataSizeException(getSize(), buffer.remaining());
        }
        
        this.x = buffer.getShort();
        this.y = buffer.getShort();
        this.pixel = DisplayPixel.fromByte(buffer.get());
    }
}
