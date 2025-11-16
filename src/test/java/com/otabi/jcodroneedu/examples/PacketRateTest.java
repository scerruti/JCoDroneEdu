package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;

/**
 * Test: Monitor incoming packet rate without sending display commands.
 * This helps determine if the 11-byte packets are responses to our commands
 * or just continuous telemetry from the controller.
 */
public class PacketRateTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            System.out.println("Monitoring packets for 10 seconds without sending any display commands...\n");
            
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 10000; // 10 seconds
            
            while (System.currentTimeMillis() < endTime) {
                Thread.sleep(100);
            }
            
            System.out.println("\n10 seconds complete.");
        }
    }
}
