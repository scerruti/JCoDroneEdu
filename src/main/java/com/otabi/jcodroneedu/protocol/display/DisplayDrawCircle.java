package com.otabi.jcodroneedu.protocol.display;

import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;

/**
 * Protocol class for drawing a circle on the controller display screen.
 * Python equivalent: DisplayDrawCircle
 */
public class DisplayDrawCircle implements Serializable {
    
    private short x;
    private short y;
    private short radius;
    private DisplayPixel pixel;
    private boolean flagFill;

    /**
     * Default constructor with white pixel and filled circle defaults.
     */
    public DisplayDrawCircle() {
        this.x = 0;
        this.y = 0;
        this.radius = 0;
        this.pixel = DisplayPixel.WHITE;
        this.flagFill = true;
    }

    /**
     * Constructor with specified parameters.
     * @param x X coordinate
     * @param y Y coordinate
     * @param radius Radius of circle
     * @param pixel The pixel type to draw
     * @param flagFill Whether to fill the circle
     */
    public DisplayDrawCircle(int x, int y, int radius, DisplayPixel pixel, boolean flagFill) {
        this.x = (short) x;
        this.y = (short) y;
        this.radius = (short) radius;
        this.pixel = pixel;
        this.flagFill = flagFill;
    }

    // Getters and setters
    public int getX() { return x; }
    public void setX(int x) { this.x = (short) x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = (short) y; }
    
    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = (short) radius; }
    
    public DisplayPixel getPixel() { return pixel; }
    public void setPixel(DisplayPixel pixel) { this.pixel = pixel; }
    
    public boolean isFlagFill() { return flagFill; }
    public void setFlagFill(boolean flagFill) { this.flagFill = flagFill; }

    @Override
    public byte getSize() {
        return 8;
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putShort(x);
        buffer.putShort(y);
        buffer.putShort(radius);
        buffer.put(pixel.value());
        buffer.put((byte) (flagFill ? 1 : 0));
    }

    // Note: toArray() inherited from Serializable interface (handles LITTLE_ENDIAN)

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException {
        if (buffer.remaining() < getSize()) {
            throw new InvalidDataSizeException(getSize(), buffer.remaining());
        }
        
        this.x = buffer.getShort();
        this.y = buffer.getShort();
        this.radius = buffer.getShort();
        this.pixel = DisplayPixel.fromByte(buffer.get());
        this.flagFill = buffer.get() != 0;
    }
}
