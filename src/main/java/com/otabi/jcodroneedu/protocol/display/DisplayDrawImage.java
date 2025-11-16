package com.otabi.jcodroneedu.protocol.display;

import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;

/**
 * Protocol class for drawing an image (pixel region) on the controller display screen.
 * This enables batch regional updates instead of pixel-by-pixel drawing, significantly
 * improving performance when updating multiple pixels at once.
 * 
 * The image data is stored as a byte array where each byte represents 8 vertical pixels
 * (bit-packed format). This matches the controller display's monochrome pixel layout.
 * 
 * Python equivalent: PIL/canvas-based drawing operations through controller_draw_canvas()
 */
public class DisplayDrawImage implements Serializable {
    
    private short x;
    private short y;
    private short width;
    private short height;
    private byte[] imageData;

    /**
     * Default constructor with zero position and empty image data.
     */
    public DisplayDrawImage() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.imageData = new byte[0];
    }

    /**
     * Constructor with specified parameters.
     * @param x X coordinate (starting position)
     * @param y Y coordinate (starting position)
     * @param width Width of image region
     * @param height Height of image region
     * @param imageData Byte array containing pixel data (bit-packed format)
     */
    public DisplayDrawImage(int x, int y, int width, int height, byte[] imageData) {
        this.x = (short) x;
        this.y = (short) y;
        this.width = (short) width;
        this.height = (short) height;
        this.imageData = imageData != null ? imageData : new byte[0];
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
    
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData != null ? imageData : new byte[0]; }

    @Override
    public byte getSize() {
        // 2 bytes (x) + 2 bytes (y) + 2 bytes (width) + 2 bytes (height) + image data length
        return (byte) (8 + imageData.length);
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putShort(x);
        buffer.putShort(y);
        buffer.putShort(width);
        buffer.putShort(height);
        buffer.put(imageData);
    }

    // Note: toArray() inherited from Serializable interface (handles LITTLE_ENDIAN)

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException {
        if (buffer.remaining() < 8) {
            throw new InvalidDataSizeException(8, buffer.remaining());
        }
        
        this.x = buffer.getShort();
        this.y = buffer.getShort();
        this.width = buffer.getShort();
        this.height = buffer.getShort();
        
        // Read remaining bytes as image data
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        this.imageData = data;
    }
}
