package com.otabi.jcodroneedu;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CircularStatsTest {

    static double circularMeanDeg(List<Double> v) {
        double sx = 0.0, sy = 0.0;
        for (double d : v) {
            double rad = Math.toRadians(d);
            sx += Math.cos(rad);
            sy += Math.sin(rad);
        }
        double mean = Math.toDegrees(Math.atan2(sy, sx));
        if (mean < 0) mean += 360.0;
        return mean;
    }

    @Test
    void testWrapAroundMean() {
        List<Double> samples = Arrays.asList(359.0, 0.0, 1.0);
    double mean = circularMeanDeg(samples);
    // normalize to [0,360)
    double norm = ((mean % 360.0) + 360.0) % 360.0;
    assertEquals(0.0, norm, 1.0); // allow small rounding error
    }
}
