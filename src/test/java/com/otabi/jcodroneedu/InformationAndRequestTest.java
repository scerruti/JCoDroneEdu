package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.linkmanager.Information;
import com.otabi.jcodroneedu.protocol.linkmanager.Version;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class InformationAndRequestTest {

    @Test
    void requestClassIsMappedToDataType() {
        // Ensure the Request class used by the API maps to DataType.Request
        DataType dt = DataType.fromClass(com.otabi.jcodroneedu.protocol.linkmanager.Request.class);
        assertNotNull(dt, "DataType.fromClass should return a mapping for Request");
        assertEquals(DataType.Request, dt, "Request.class should map to DataType.Request");
    }

    @Test
    void informationUnpackHandlesUnknownModelNumber() throws Exception {
        // Build a payload with an unknown model number (int 0xAABBCCDD) and valid remainder
        Information info = new Information();
        ByteBuffer buf = ByteBuffer.allocate(info.getSize());

        // modeUpdate (1 byte)
        buf.put((byte) 0x00);
        // model number (4 bytes) - use a large unknown value
        buf.putInt(0xAABBCCDD);
        // Version - fill defaults: we need to put bytes that Version.unpack expects
    Version v = new Version((short)123, (byte)2, (byte)3);
    v.pack(buf);
        // year (short)
        buf.putShort((short)2025);
        // month/day
        buf.put((byte)10);
        buf.put((byte)13);

        buf.flip();

        // Should not throw and should set modelNumber to NONE_
        info.unpack(buf);
        assertNotNull(info.getModelNumber(), "ModelNumber should be non-null after unpack");
        assertEquals(DroneSystem.ModelNumber.NONE_, info.getModelNumber(), "Unknown model should map to NONE_");
    }
}
