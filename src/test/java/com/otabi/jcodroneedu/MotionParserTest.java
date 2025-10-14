package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.dronestatus.Motion;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MotionParserTest {

    @Test
    void testMotionPackUnpackLittleEndian() throws InvalidDataSizeException {
        Motion m = new Motion((short)100,(short)-50,(short)0,(short)10,(short)-10,(short)5,(short)1,(short)2,(short)359);

        ByteBuffer buf = ByteBuffer.allocate(Motion.MOTION_SIZE);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        m.pack(buf);
        buf.flip();

        Motion m2 = new Motion();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        m2.unpack(buf);

        assertEquals(m.getAccelX(), m2.getAccelX());
        assertEquals(m.getAccelY(), m2.getAccelY());
        assertEquals(m.getAccelZ(), m2.getAccelZ());
        assertEquals(m.getAngleYaw(), m2.getAngleYaw());
    }
}

