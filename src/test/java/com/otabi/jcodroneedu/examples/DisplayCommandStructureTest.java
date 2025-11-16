package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Test: Send individual display commands and capture the response packet structure.
 * This helps identify what these 11-byte "Ack" packets actually contain.
 */
public class DisplayCommandStructureTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            
            // Test 1: Send just DisplayClear, observe response
            System.out.println("\n=== TEST 1: DisplayClear ===");
            drone.controllerClearScreen();
            Thread.sleep(500);
            
            // Test 2: Send a single DisplayDrawImage chunk
            System.out.println("\n=== TEST 2: Single DisplayDrawImage Chunk ===");
            byte[] testChunk = new byte[128];
            for (int i = 0; i < testChunk.length; i++) {
                testChunk[i] = (byte)0xAA; // Pattern: 10101010
            }
            drone.controllerDrawImage(0, 0, 128, 8, testChunk);
            Thread.sleep(500);
            
            // Test 3: Send multiple chunks rapid-fire
            System.out.println("\n=== TEST 3: Three Rapid DisplayDrawImage Chunks ===");
            for (int i = 0; i < 3; i++) {
                drone.controllerDrawImage(0, i*8, 128, 8, testChunk);
                Thread.sleep(50);
            }
            Thread.sleep(500);
            
            System.out.println("\nTest complete.");
        }
    }
}
