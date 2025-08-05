package com.otabi.jcodroneedu.protocol.display;

import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;

/**
 * Protocol class for drawing a line on the controller display screen.
 * Python equivalent: DisplayDrawLine
 */
public class DisplayDrawLine implements Serializable {
    
    private short x1;
    private short y1;
    private short x2;
    private short y2;
    private DisplayPixel pixel;
    private DisplayLine line;

    /**
     * Default constructor with white pixel and solid line defaults.
     */
    public DisplayDrawLine() {
        this.x1 = 0;
        this.y1 = 0;
        this.x2 = 0;
        this.y2 = 0;
        this.pixel = DisplayPixel.WHITE;
        this.line = DisplayLine.SOLID;
    }

    /**
     * Constructor with specified parameters.
     * @param x1 Starting X coordinate
     * @param y1 Starting Y coordinate
     * @param x2 Ending X coordinate
     * @param y2 Ending Y coordinate
     * @param pixel The pixel type to draw
     * @param line The line style
     */
    public DisplayDrawLine(int x1, int y1, int x2, int y2, DisplayPixel pixel, DisplayLine line) {
        this.x1 = (short) x1;
        this.y1 = (short) y1;
        this.x2 = (short) x2;
        this.y2 = (short) y2;
        this.pixel = pixel;
        this.line = line;
    }

    // Getters and setters
    public int getX1() { return x1; }
    public void setX1(int x1) { this.x1 = (short) x1; }
    
    public int getY1() { return y1; }
    public void setY1(int y1) { this.y1 = (short) y1; }
    
    public int getX2() { return x2; }
    public void setX2(int x2) { this.x2 = (short) x2; }
    
    public int getY2() { return y2; }
    public void setY2(int y2) { this.y2 = (short) y2; }
    
    public DisplayPixel getPixel() { return pixel; }
    public void setPixel(DisplayPixel pixel) { this.pixel = pixel; }
    
    public DisplayLine getLine() { return line; }
    public void setLine(DisplayLine line) { this.line = line; }

    @Override
    public byte getSize() {
        return 10;
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putShort(x1);
        buffer.putShort(y1);
        buffer.putShort(x2);
        buffer.putShort(y2);
        buffer.put(pixel.value());
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
        
        this.x1 = buffer.getShort();
        this.y1 = buffer.getShort();
        this.x2 = buffer.getShort();
        this.y2 = buffer.getShort();
        this.pixel = DisplayPixel.fromByte(buffer.get());
        this.line = DisplayLine.fromByte(buffer.get());
    }
}
