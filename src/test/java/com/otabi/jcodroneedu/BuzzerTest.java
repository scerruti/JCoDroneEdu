package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.buzzer.Note;
import com.otabi.jcodroneedu.protocol.buzzer.BuzzerMode;

/**
 * Simple test to verify buzzer classes compile and work
 */
public class BuzzerTest {
    public static void main(String[] args) {
        // Test Note enum
        Note note = Note.C4;
        System.out.println("Note C4 value: " + note.getValue());
        System.out.println("Note C4 frequency: " + note.getFrequency());
        
        // Test BuzzerMode enum  
        BuzzerMode mode = BuzzerMode.SCALE;
        System.out.println("BuzzerMode SCALE value: " + mode.getValue());
        
        System.out.println("Buzzer classes working!");
    }
}
