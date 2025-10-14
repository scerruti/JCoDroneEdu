package com.otabi.jcodroneedu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardColorIndexTest {

    @Test
    void testCardColorNames() {
        assertEquals("WHITE", DroneSystem.cardColorIndexToName(0x01));
        assertEquals("RED", DroneSystem.cardColorIndexToName(0x02));
        assertEquals("BLUE", DroneSystem.cardColorIndexToName(0x06));
        assertEquals("UNKNOWN", DroneSystem.cardColorIndexToName(0x00));
    }
}
