package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.dronestatus.Motion;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MotionFixtureTest {

    @Test
    void testParseMotionFixture() throws IOException, InvalidDataSizeException {
        record Expected(short ax, short ay, short az, short ar, short ap, short ayaw) {}

        var fixtures = java.util.Map.of(
            "motion_frame_le.hex", new Expected((short)98, (short)0, (short)-98, (short)10, (short)-5, (short)90),
            "motion_frame_le_zero.hex", new Expected((short)0,(short)0,(short)0,(short)0,(short)0,(short)0),
            "motion_frame_le_max.hex", new Expected((short)32767,(short)32767,(short)32767,(short)32767,(short)32767,(short)32767),
            "motion_frame_le_mixed.hex", new Expected((short)42,(short)-40,(short)100,(short)10,(short)-10,(short)30)
        );

        for (var e : fixtures.entrySet()) {
            String name = e.getKey();
            Expected expect = e.getValue();
            Path p = Path.of("src/test/resources/fixtures/" + name);
            String hex = Files.readString(p).trim();

            byte[] bytes = new byte[hex.length()/2];
            for (int i = 0; i < bytes.length; i++) {
                int hi = Character.digit(hex.charAt(i*2), 16);
                int lo = Character.digit(hex.charAt(i*2+1), 16);
                bytes[i] = (byte)((hi << 4) | lo);
            }

            ByteBuffer buf = ByteBuffer.wrap(bytes);
            buf.order(ByteOrder.LITTLE_ENDIAN);

            Motion m = new Motion();
            m.unpack(buf);

            assertEquals(expect.ax, m.getAccelX(), name + " accelX");
            assertEquals(expect.ay, m.getAccelY(), name + " accelY");
            assertEquals(expect.az, m.getAccelZ(), name + " accelZ");

            assertEquals(expect.ar, m.getAngleRoll(), name + " angleRoll");
            assertEquals(expect.ap, m.getAnglePitch(), name + " anglePitch");
            assertEquals(expect.ayaw, m.getAngleYaw(), name + " angleYaw");

            // scaled checks (coarse)
            double ax_ms2 = m.getAccelX() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
            assertTrue(Double.isFinite(ax_ms2));
        }

        // truncated fixture should fail unpacking because of insufficient bytes
        Path trunc = Path.of("src/test/resources/fixtures/motion_frame_le_trunc.hex");
        String hx = Files.readString(trunc).trim();
        byte[] btrunc = new byte[hx.length()/2];
        for (int i = 0; i < btrunc.length; i++) {
            int hi = Character.digit(hx.charAt(i*2), 16);
            int lo = Character.digit(hx.charAt(i*2+1), 16);
            btrunc[i] = (byte)((hi << 4) | lo);
        }
        ByteBuffer bt = ByteBuffer.wrap(btrunc);
        bt.order(ByteOrder.LITTLE_ENDIAN);
        Motion mt = new Motion();
        assertThrows(java.nio.BufferUnderflowException.class, () -> mt.unpack(bt));
    }
}
