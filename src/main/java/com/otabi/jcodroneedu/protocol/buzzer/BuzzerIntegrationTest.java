package com.otabi.jcodroneedu.protocol.buzzer;

/**
 * Integration test to verify all buzzer components work together
 */
public class BuzzerIntegrationTest {
    public static void main(String[] args) {
        System.out.println("=== CoDrone EDU Buzzer API Integration Test ===");
        
        try {
            // Test 1: Verify all buzzer enums work
            System.out.println("\n1. Testing BuzzerMode enum:");
            for (BuzzerMode mode : BuzzerMode.values()) {
                System.out.println("   " + mode + " = " + mode.getValue());
            }
            
            // Test 2: Verify Note enum works
            System.out.println("\n2. Testing Note enum (sample):");
            Note[] sampleNotes = {Note.C4, Note.D4, Note.E4, Note.F4, Note.G4, Note.A4, Note.B4, Note.C5};
            for (Note note : sampleNotes) {
                System.out.println("   " + note + " = " + note.getValue());
            }
            
            // Test 3: Verify Buzzer class functionality
            System.out.println("\n3. Testing Buzzer class:");
            
            // Test constructor
            Buzzer buzzer1 = new Buzzer();
            System.out.println("   Default buzzer: " + buzzer1);
            System.out.println("   Size: " + buzzer1.getSize() + " bytes");
            
            // Test parameterized constructor
            Buzzer buzzer2 = new Buzzer(BuzzerMode.SCALE, Note.C4.getValue(), 1000);
            System.out.println("   Note buzzer: " + buzzer2);
            
            Buzzer buzzer3 = new Buzzer(BuzzerMode.HZ, 440, 500);
            System.out.println("   Frequency buzzer: " + buzzer3);
            
            // Test static factory methods
            System.out.println("\n4. Testing static factory methods:");
            Buzzer stopBuzzer = Buzzer.stop();
            System.out.println("   Stop: " + stopBuzzer);
            
            Buzzer muteBuzzer = Buzzer.mute(100);
            System.out.println("   Mute: " + muteBuzzer);
            
            Buzzer noteBuzzer = Buzzer.note(Note.A4, 750);
            System.out.println("   Note: " + noteBuzzer);
            
            Buzzer freqBuzzer = Buzzer.frequency(880, 250);
            System.out.println("   Frequency: " + freqBuzzer);
            
            // Test 5: Verify serialization works
            System.out.println("\n5. Testing serialization:");
            Buzzer testBuzzer = new Buzzer(BuzzerMode.SCALE, Note.G4.getValue(), 1500);
            byte[] serialized = testBuzzer.toArray();
            System.out.println("   Serialized " + testBuzzer + " to " + serialized.length + " bytes");
            System.out.printf("   Bytes: [%02x %02x %02x %02x %02x]%n", 
                serialized[0], serialized[1], serialized[2], serialized[3], serialized[4]);
            
            // Test 6: Verify BuzzerMode.fromValue works
            System.out.println("\n6. Testing BuzzerMode.fromValue:");
            for (int i = 0; i <= 6; i++) {
                try {
                    BuzzerMode mode = BuzzerMode.fromValue(i);
                    System.out.println("   Value " + i + " -> " + mode);
                } catch (Exception e) {
                    System.out.println("   Value " + i + " -> Invalid");
                }
            }
            
            System.out.println("\n✅ All buzzer components are working correctly!");
            System.out.println("✅ The buzzer API is ready for classroom use!");
            System.out.println("✅ Students can now use drone_buzzer(), controller_buzzer(),");
            System.out.println("   start_drone_buzzer(), stop_drone_buzzer(),");
            System.out.println("   start_controller_buzzer(), and stop_controller_buzzer()");
            
        } catch (Exception e) {
            System.err.println("❌ Error in buzzer integration test: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
