package com.otabi.jcodroneedu.protocol.buzzer;

/**
 * Test class to verify all buzzer classes are accessible
 */
public class TestBuzzer {
    public static void main(String[] args) {
        System.out.println("Testing buzzer package classes...");
        
        // Test BuzzerMode
        BuzzerMode mode = BuzzerMode.SCALE;
        System.out.println("BuzzerMode.SCALE: " + mode);
        
        // Test Note
        Note note = Note.C4;
        System.out.println("Note.C4: " + note);
        
        // Test Buzzer
        Buzzer buzzer = new Buzzer();
        System.out.println("Buzzer created: " + buzzer);
        System.out.println("Buzzer size: " + buzzer.getSize());
        
        System.out.println("All buzzer classes accessible!");
    }
}
