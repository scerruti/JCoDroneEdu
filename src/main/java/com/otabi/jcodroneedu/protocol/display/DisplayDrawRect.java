package com.otabi.jcodroneedu.protocol.display;

import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;

/**
 * Protocol class for drawing a rectangle on the controller display screen.
 * Python equivalent: DisplayDrawRect
 */
public class DisplayDrawRect implements Serializable {
    
    private short x;
    private short y;
    private short width;
    private short height;
    private DisplayPixel pixel;
    private boolean flagFill;
    private DisplayLine line;

    /**
     * Default constructor with white pixel, filled, and solid line defaults.
     */
    public DisplayDrawRect() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.pixel = DisplayPixel.WHITE;
        this.flagFill = true;
        this.line = DisplayLine.SOLID;
    }

    /**
     * Constructor with specified parameters.
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @param pixel The pixel type to draw
     * @param flagFill Whether to fill the rectangle
     * @param line The line style for outline
     */
    public DisplayDrawRect(int x, int y, int width, int height, DisplayPixel pixel, boolean flagFill, DisplayLine line) {
        this.x = (short) x;
        this.y = (short) y;
        this.width = (short) width;
        this.height = (short) height;
        this.pixel = pixel;
        this.flagFill = flagFill;
        this.line = line;
    }

    // Getters and setters
    public int getX() { return x; }
    public void setX(int x) { this.x = (short) x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = (short) y; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = (short) width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = (short) height; }
    
    public DisplayPixel getPixel() { return pixel; }
    public void setPixel(DisplayPixel pixel) { this.pixel = pixel; }
    
    public boolean isFlagFill() { return flagFill; }
    public void setFlagFill(boolean flagFill) { this.flagFill = flagFill; }
    
    public DisplayLine getLine() { return line; }
    public void setLine(DisplayLine line) { this.line = line; }

    @Override
    public byte getSize() {
        return 11;
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putShort(x);
        buffer.putShort(y);
        buffer.putShort(width);
        buffer.putShort(height);
        buffer.put(pixel.value());
        buffer.put((byte) (flagFill ? 1 : 0));
        buffer.put(line.value());
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
        this.width = buffer.getShort();
        this.height = buffer.getShort();
        this.pixel = DisplayPixel.fromByte(buffer.get());
        this.flagFill = buffer.get() != 0;
        this.line = DisplayLine.fromByte(buffer.get());
    }
}
