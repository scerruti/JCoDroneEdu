package com.otabi.jcodroneedu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardColorIndexTest {

    @Test
    void testCardColorNames() {
        assertEquals("NONE", DroneSystem.cardColorIndexToName(0x00));  // No card detected
        assertEquals("WHITE", DroneSystem.cardColorIndexToName(0x01));
        assertEquals("RED", DroneSystem.cardColorIndexToName(0x02));
        assertEquals("YELLOW", DroneSystem.cardColorIndexToName(0x03));
        assertEquals("GREEN", DroneSystem.cardColorIndexToName(0x04));
        assertEquals("LIGHT_BLUE", DroneSystem.cardColorIndexToName(0x05));  // Was CYAN
        assertEquals("BLUE", DroneSystem.cardColorIndexToName(0x06));
        assertEquals("PURPLE", DroneSystem.cardColorIndexToName(0x07));  // Was MAGENTA
        assertEquals("BLACK", DroneSystem.cardColorIndexToName(0x08));
        assertEquals("INVALID", DroneSystem.cardColorIndexToName(0xFF));  // Out of range
    }
}
