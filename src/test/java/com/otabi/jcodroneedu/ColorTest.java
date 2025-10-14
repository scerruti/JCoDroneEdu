package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.lightcontroller.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ColorTest {
    @Test
    void colorConstructorAcceptsByteValues() {
        Color color1 = new Color((byte) 50, (byte) 100, (byte) 127);
        java.nio.ByteBuffer buf1 = java.nio.ByteBuffer.allocate(3);
        color1.pack(buf1);
        buf1.flip();
        byte[] expected1 = new byte[3];
        buf1.get(expected1);

        Color color1Unpacked = new Color((byte)0, (byte)0, (byte)0);
        java.nio.ByteBuffer buf1b = java.nio.ByteBuffer.allocate(3);
        buf1b.put(expected1);
        buf1b.flip();
        color1Unpacked.unpack(buf1b);

        java.nio.ByteBuffer verify1 = java.nio.ByteBuffer.allocate(3);
        color1Unpacked.pack(verify1);
        verify1.flip();
        byte[] actual1 = new byte[3];
        verify1.get(actual1);
        assertArrayEquals(expected1, actual1);

        Color color2 = new Color((byte) 255, (byte) 0, (byte) 0);
        java.nio.ByteBuffer buf2 = java.nio.ByteBuffer.allocate(3);
        color2.pack(buf2);
        buf2.flip();
        byte[] expected2 = new byte[3];
        buf2.get(expected2);

        Color color2Unpacked = new Color((byte)0, (byte)0, (byte)0);
        java.nio.ByteBuffer buf2b = java.nio.ByteBuffer.allocate(3);
        buf2b.put(expected2);
        buf2b.flip();
        color2Unpacked.unpack(buf2b);

        java.nio.ByteBuffer verify2 = java.nio.ByteBuffer.allocate(3);
        color2Unpacked.pack(verify2);
        verify2.flip();
        byte[] actual2 = new byte[3];
        verify2.get(actual2);
        assertArrayEquals(expected2, actual2);
    }
}
