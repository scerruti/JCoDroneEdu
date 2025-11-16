package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.display.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Protocol-level tests for display functionality.
 * These tests focus on the protocol classes themselves
 * without requiring a full Drone instance.
 */
public class DisplayProtocolTest {

    @Test
    void testDisplayClearAllProtocol() {
        DisplayClearAll clearAll = new DisplayClearAll();
        
        // Test default constructor
        assertEquals(DisplayPixel.WHITE, clearAll.getPixel());
        assertEquals(1, clearAll.getSize());
        
        // Test parameterized constructor
        DisplayClearAll clearAllBlack = new DisplayClearAll(DisplayPixel.BLACK);
        assertEquals(DisplayPixel.BLACK, clearAllBlack.getPixel());
        
        // Test serialization
        byte[] data = clearAllBlack.toArray();
        assertEquals(1, data.length);
        assertEquals(DisplayPixel.BLACK.value(), data[0]);
        
        // Test parsing
        DisplayClearAll parsed = new DisplayClearAll();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(DisplayPixel.BLACK, parsed.getPixel());
    }

    @Test
    void testDisplayDrawPointProtocol() {
        DisplayDrawPoint point = new DisplayDrawPoint(50, 30, DisplayPixel.BLACK);
        
        // Test values
        assertEquals(50, point.getX());
        assertEquals(30, point.getY());
        assertEquals(DisplayPixel.BLACK, point.getPixel());
        assertEquals(5, point.getSize());
        
        // Test serialization
        byte[] data = point.toArray();
        assertEquals(5, data.length);
        
        // Test parsing - need to set the buffer to LITTLE_ENDIAN
        DisplayDrawPoint parsed = new DisplayDrawPoint();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(50, parsed.getX());
        assertEquals(30, parsed.getY());
        assertEquals(DisplayPixel.BLACK, parsed.getPixel());
    }

    @Test
    void testDisplayDrawLineProtocol() {
        DisplayDrawLine line = new DisplayDrawLine(10, 20, 30, 40, DisplayPixel.WHITE, DisplayLine.DOTTED);
        
        // Test values
        assertEquals(10, line.getX1());
        assertEquals(20, line.getY1());
        assertEquals(30, line.getX2());
        assertEquals(40, line.getY2());
        assertEquals(DisplayPixel.WHITE, line.getPixel());
        assertEquals(DisplayLine.DOTTED, line.getLine());
        assertEquals(10, line.getSize());
        
        // Test serialization
        byte[] data = line.toArray();
        assertEquals(10, data.length);
        
        // Test parsing
        DisplayDrawLine parsed = new DisplayDrawLine();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(10, parsed.getX1());
        assertEquals(20, parsed.getY1());
        assertEquals(30, parsed.getX2());
        assertEquals(40, parsed.getY2());
        assertEquals(DisplayPixel.WHITE, parsed.getPixel());
        assertEquals(DisplayLine.DOTTED, parsed.getLine());
    }

    @Test
    void testDisplayDrawRectProtocol() {
        DisplayDrawRect rect = new DisplayDrawRect(15, 25, 60, 40, DisplayPixel.INVERSE, true, DisplayLine.DASHED);
        
        // Test values
        assertEquals(15, rect.getX());
        assertEquals(25, rect.getY());
        assertEquals(60, rect.getWidth());
        assertEquals(40, rect.getHeight());
        assertEquals(DisplayPixel.INVERSE, rect.getPixel());
        assertTrue(rect.isFlagFill());
        assertEquals(DisplayLine.DASHED, rect.getLine());
        assertEquals(11, rect.getSize());
        
        // Test serialization
        byte[] data = rect.toArray();
        assertEquals(11, data.length);
        
        // Test parsing
        DisplayDrawRect parsed = new DisplayDrawRect();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(15, parsed.getX());
        assertEquals(25, parsed.getY());
        assertEquals(60, parsed.getWidth());
        assertEquals(40, parsed.getHeight());
        assertEquals(DisplayPixel.INVERSE, parsed.getPixel());
        assertTrue(parsed.isFlagFill());
        assertEquals(DisplayLine.DASHED, parsed.getLine());
    }

    @Test
    void testDisplayDrawCircleProtocol() {
        DisplayDrawCircle circle = new DisplayDrawCircle(75, 35, 20, DisplayPixel.OUTLINE, false);
        
        // Test values
        assertEquals(75, circle.getX());
        assertEquals(35, circle.getY());
        assertEquals(20, circle.getRadius());
        assertEquals(DisplayPixel.OUTLINE, circle.getPixel());
        assertFalse(circle.isFlagFill());
        assertEquals(8, circle.getSize());
        
        // Test serialization
        byte[] data = circle.toArray();
        assertEquals(8, data.length);
        
        // Test parsing
        DisplayDrawCircle parsed = new DisplayDrawCircle();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(75, parsed.getX());
        assertEquals(35, parsed.getY());
        assertEquals(20, parsed.getRadius());
        assertEquals(DisplayPixel.OUTLINE, parsed.getPixel());
        assertFalse(parsed.isFlagFill());
    }

    @Test
    void testDisplayDrawStringProtocol() {
        String message = "Hello CoDrone!";
        DisplayDrawString string = new DisplayDrawString(10, 15, DisplayFont.LIBERATION_MONO_10X16, DisplayPixel.BLACK, message);
        
        // Test values
        assertEquals(10, string.getX());
        assertEquals(15, string.getY());
        assertEquals(DisplayFont.LIBERATION_MONO_10X16, string.getFont());
        assertEquals(DisplayPixel.BLACK, string.getPixel());
        assertEquals(message, string.getMessage());
        assertEquals(6 + message.getBytes(java.nio.charset.StandardCharsets.UTF_8).length, string.getSize());
        
        // Test serialization
        byte[] data = string.toArray();
        assertEquals(6 + message.length(), data.length);
        
        // Test parsing
        DisplayDrawString parsed = new DisplayDrawString();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(10, parsed.getX());
        assertEquals(15, parsed.getY());
        assertEquals(DisplayFont.LIBERATION_MONO_10X16, parsed.getFont());
        assertEquals(DisplayPixel.BLACK, parsed.getPixel());
        assertEquals(message, parsed.getMessage());
    }

    @Test
    void testDisplayClearProtocol() {
        DisplayClear clear = new DisplayClear(20, 30, 50, 40, DisplayPixel.BLACK);
        
        // Test values
        assertEquals(20, clear.getX());
        assertEquals(30, clear.getY());
        assertEquals(50, clear.getWidth());
        assertEquals(40, clear.getHeight());
        assertEquals(DisplayPixel.BLACK, clear.getPixel());
        assertEquals(9, clear.getSize());
        
        // Test serialization
        byte[] data = clear.toArray();
        assertEquals(9, data.length);
        
        // Test parsing
        DisplayClear parsed = new DisplayClear();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(20, parsed.getX());
        assertEquals(30, parsed.getY());
        assertEquals(50, parsed.getWidth());
        assertEquals(40, parsed.getHeight());
        assertEquals(DisplayPixel.BLACK, parsed.getPixel());
    }

    @Test
    void testDisplayInvertProtocol() {
        DisplayInvert invert = new DisplayInvert(25, 35, 70, 50);
        
        // Test values
        assertEquals(25, invert.getX());
        assertEquals(35, invert.getY());
        assertEquals(70, invert.getWidth());
        assertEquals(50, invert.getHeight());
        assertEquals(8, invert.getSize());
        
        // Test serialization
        byte[] data = invert.toArray();
        assertEquals(8, data.length);
        
        // Test parsing
        DisplayInvert parsed = new DisplayInvert();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(25, parsed.getX());
        assertEquals(35, parsed.getY());
        assertEquals(70, parsed.getWidth());
        assertEquals(50, parsed.getHeight());
    }

    @Test
    void testNullStringHandling() {
        // Test null string handling
        DisplayDrawString string = new DisplayDrawString(0, 0, DisplayFont.LIBERATION_MONO_5X8, DisplayPixel.BLACK, null);
        assertEquals("", string.getMessage());
        
        // Test setting null message
        string.setMessage(null);
        assertEquals("", string.getMessage());
    }

    @Test
    void testDisplayDrawImageProtocol() {
        // Test data: simple 2x2 pixel pattern
        byte[] imageData = new byte[]{(byte) 0xFF, (byte) 0x80};
        DisplayDrawImage image = new DisplayDrawImage(10, 20, 2, 16, imageData);
        
        // Test values
        assertEquals(10, image.getX());
        assertEquals(20, image.getY());
        assertEquals(2, image.getWidth());
        assertEquals(16, image.getHeight());
        assertArrayEquals(imageData, image.getImageData());
        assertEquals(8 + imageData.length, image.getSize());
        
        // Test serialization
        byte[] data = image.toArray();
        assertEquals(8 + imageData.length, data.length);
        
        // Test parsing
        DisplayDrawImage parsed = new DisplayDrawImage();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(10, parsed.getX());
        assertEquals(20, parsed.getY());
        assertEquals(2, parsed.getWidth());
        assertEquals(16, parsed.getHeight());
        assertArrayEquals(imageData, parsed.getImageData());
    }

    @Test
    void testDisplayDrawImageEmptyData() {
        // Test with empty image data
        DisplayDrawImage image = new DisplayDrawImage(0, 0, 0, 0, new byte[0]);
        
        // Test values
        assertEquals(0, image.getX());
        assertEquals(0, image.getY());
        assertEquals(0, image.getWidth());
        assertEquals(0, image.getHeight());
        assertEquals(0, image.getImageData().length);
        assertEquals(8, image.getSize());
        
        // Test serialization
        byte[] data = image.toArray();
        assertEquals(8, data.length);
        
        // Test parsing
        DisplayDrawImage parsed = new DisplayDrawImage();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(0, parsed.getX());
        assertEquals(0, parsed.getY());
        assertEquals(0, parsed.getWidth());
        assertEquals(0, parsed.getHeight());
        assertEquals(0, parsed.getImageData().length);
    }

    @Test
    void testDisplayDrawImageNullData() {
        // Test with null image data (should default to empty array)
        DisplayDrawImage image = new DisplayDrawImage(5, 10, 16, 8, null);
        
        assertEquals(5, image.getX());
        assertEquals(10, image.getY());
        assertEquals(16, image.getWidth());
        assertEquals(8, image.getHeight());
        assertEquals(0, image.getImageData().length);
        
        // Test serialization
        byte[] data = image.toArray();
        assertEquals(8, data.length);
        
        // Test parsing
        DisplayDrawImage parsed = new DisplayDrawImage();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(5, parsed.getX());
        assertEquals(10, parsed.getY());
        assertEquals(16, parsed.getWidth());
        assertEquals(8, parsed.getHeight());
        assertEquals(0, parsed.getImageData().length);
    }

    @Test
    void testDisplayDrawImageLargeData() {
        // Test with realistic large image data representing part of a full display
        // (Full 128x64 display = 1024 bytes, but protocol limits single message to 255 bytes)
        // This tests a chunk that could be one part of a full display update
        byte[] largeImageData = new byte[240];  // 240 bytes of image data
        for (int i = 0; i < largeImageData.length; i++) {
            largeImageData[i] = (byte) (i % 256);
        }
        
        // This represents a chunk of the display update with 240 bytes of image data
        DisplayDrawImage image = new DisplayDrawImage(0, 0, 128, 30, largeImageData);
        
        // Test values
        assertEquals(0, image.getX());
        assertEquals(0, image.getY());
        assertEquals(128, image.getWidth());
        assertEquals(30, image.getHeight());
        assertEquals(240, image.getImageData().length);
        // Total payload: 8 bytes header + 240 bytes image = 248 bytes (fits in 255 limit)
        // Note: getSize() returns byte, so 248 is stored as (byte)248 = -8
        assertEquals((byte)248, image.getSize());
        
        // Test serialization
        byte[] data = image.toArray();
        assertEquals(248, data.length);
        
        // Test parsing
        DisplayDrawImage parsed = new DisplayDrawImage();
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        parsed.unpack(buffer);
        assertEquals(0, parsed.getX());
        assertEquals(0, parsed.getY());
        assertEquals(128, parsed.getWidth());
        assertEquals(30, parsed.getHeight());
        assertArrayEquals(largeImageData, parsed.getImageData());
    }
}
