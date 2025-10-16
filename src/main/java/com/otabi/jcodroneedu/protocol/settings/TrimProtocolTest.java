package com.otabi.jcodroneedu.protocol.settings;

/**
 * Simple test to verify the trim protocol classes work correctly.
 * Tests serialization, getters, and basic functionality.
 */
public class TrimProtocolTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 CoDrone EDU Trim Protocol Test");
        System.out.println("==================================");
        
        try {
            // Test 1: Create trim object with values
            System.out.println("Test 1: Creating trim object...");
            Trim trim = new Trim((short) 10, (short) -5, (short) 0, (short) 0);
            
            // Test 2: Verify getters work
            System.out.println("Test 2: Testing getters...");
            assertEquals(10, trim.getRoll(), "Roll value should be 10");
            assertEquals(-5, trim.getPitch(), "Pitch value should be -5");
            assertEquals(0, trim.getYaw(), "Yaw value should be 0");
            assertEquals(0, trim.getThrottle(), "Throttle value should be 0");
            System.out.println("✅ Getters working: roll=" + trim.getRoll() + 
                             ", pitch=" + trim.getPitch() + 
                             ", yaw=" + trim.getYaw() + 
                             ", throttle=" + trim.getThrottle());
            
            // Test 3: Test default constructor
            System.out.println("Test 3: Testing default constructor...");
            Trim defaultTrim = new Trim();
            assertEquals(0, defaultTrim.getRoll(), "Default roll should be 0");
            assertEquals(0, defaultTrim.getPitch(), "Default pitch should be 0");
            assertEquals(0, defaultTrim.getYaw(), "Default yaw should be 0");
            assertEquals(0, defaultTrim.getThrottle(), "Default throttle should be 0");
            System.out.println("✅ Default constructor working");
            
            // Test 4: Test setters
            System.out.println("Test 4: Testing setters...");
            defaultTrim.setRoll((short) 25);
            defaultTrim.setPitch((short) -15);
            assertEquals(25, defaultTrim.getRoll(), "Roll should be updated to 25");
            assertEquals(-15, defaultTrim.getPitch(), "Pitch should be updated to -15");
            System.out.println("✅ Setters working");
            
            // Test 5: Test size
            System.out.println("Test 5: Testing protocol size...");
            assertEquals(8, trim.getSize(), "Trim size should be 8 bytes");
            System.out.println("✅ Protocol size correct: " + trim.getSize() + " bytes");
            
            // Test 6: Test serialization (pack/unpack)
            System.out.println("Test 6: Testing serialization...");
            java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(8);
            
            // Pack the trim data
            trim.pack(buffer);
            buffer.flip();
            
            // Unpack into a new trim object
            Trim unpackedTrim = new Trim();
            unpackedTrim.unpack(buffer);
            
            // Verify values match
            assertEquals(trim.getRoll(), unpackedTrim.getRoll(), "Unpacked roll should match");
            assertEquals(trim.getPitch(), unpackedTrim.getPitch(), "Unpacked pitch should match");
            assertEquals(trim.getYaw(), unpackedTrim.getYaw(), "Unpacked yaw should match");
            assertEquals(trim.getThrottle(), unpackedTrim.getThrottle(), "Unpacked throttle should match");
            System.out.println("✅ Serialization working correctly");
            
            System.out.println("==================================");
            System.out.println("✅ All trim protocol tests passed!");
            System.out.println("✅ Trim class ready for use!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Simple assertion helper
    private static void assertEquals(int expected, short actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    // Note: The Object-based assertEquals overload was removed because it is not
    // used in this standalone test runner and triggered unused-method diagnostics
    // in IDEs. Keep the simple primitive helper above for the short/int comparisons
    // used by these tests.
}
