package com.otabi.jcodroneedu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SensorScalesTest {

    @Test
    void testAccelRawToMs2AndG() {
        // raw value 98 corresponds to ~9.80665 m/s^2 => ~1g
        double ms2 = 98 * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
        double g = ms2 * 1.0 / 9.80665;

        assertEquals(9.8, ms2, 0.5); // coarse check
        assertEquals(1.0, g, 0.05);
    }
}
