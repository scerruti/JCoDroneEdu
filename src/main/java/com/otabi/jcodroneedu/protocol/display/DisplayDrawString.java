package com.otabi.jcodroneedu.protocol.display;

import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Protocol class for drawing a string on the controller display screen.
 * Python equivalent: DisplayDrawString
 */
public class DisplayDrawString implements Serializable {
    
    private short x;
    private short y;
    private DisplayFont font;
    private DisplayPixel pixel;
    private String message;

    /**
     * Default constructor with default font and white pixel.
     */
    public DisplayDrawString() {
        this.x = 0;
        this.y = 0;
        this.font = DisplayFont.LIBERATION_MONO_5X8;
        this.pixel = DisplayPixel.WHITE;
        this.message = "";
    }

    /**
     * Constructor with specified parameters.
     * @param x X coordinate
     * @param y Y coordinate
     * @param font The font to use
     * @param pixel The pixel type to draw
     * @param message The message to display
     */
    public DisplayDrawString(int x, int y, DisplayFont font, DisplayPixel pixel, String message) {
        this.x = (short) x;
        this.y = (short) y;
        this.font = font;
        this.pixel = pixel;
        this.message = message != null ? message : "";
    }

    // Getters and setters
    public int getX() { return x; }
    public void setX(int x) { this.x = (short) x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = (short) y; }
    
    public DisplayFont getFont() { return font; }
    public void setFont(DisplayFont font) { this.font = font; }
    
    public DisplayPixel getPixel() { return pixel; }
    public void setPixel(DisplayPixel pixel) { this.pixel = pixel; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message != null ? message : ""; }

    @Override
    public byte getSize() {
        return (byte) (6 + message.getBytes(StandardCharsets.UTF_8).length);
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putShort(x);
        buffer.putShort(y);
        buffer.put(font.value());
        buffer.put(pixel.value());
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
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
        if (buffer.remaining() < 6) {
            throw new InvalidDataSizeException(6, buffer.remaining());
        }
        
        this.x = buffer.getShort();
        this.y = buffer.getShort();
        this.font = DisplayFont.fromByte(buffer.get());
        this.pixel = DisplayPixel.fromByte(buffer.get());
        
        // Read remaining bytes as message
        byte[] messageBytes = new byte[buffer.remaining()];
        buffer.get(messageBytes);
        this.message = new String(messageBytes, StandardCharsets.UTF_8);
    }
}
