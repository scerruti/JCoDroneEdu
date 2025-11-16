package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test: Monitor echo receipts during display rendering iterations.
 * Tracks whether echoes are received for every DisplayDrawImage chunk,
 * and correlates with display rendering consistency.
 */
public class DisplayEchoTrackingTest {
    
    // Global state to track echoes
    static class EchoTracker {
        final ConcurrentHashMap<String, AtomicInteger> echoCount = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, Long> firstEchoTime = new ConcurrentHashMap<>();
        
        void recordEcho(String chunkId) {
            echoCount.putIfAbsent(chunkId, new AtomicInteger(0));
            echoCount.get(chunkId).incrementAndGet();
            firstEchoTime.putIfAbsent(chunkId, System.currentTimeMillis());
        }
        
        void reset() {
            echoCount.clear();
            firstEchoTime.clear();
        }
        
        void report() {
            System.out.println("\n=== ECHO TRACKING SUMMARY ===");
            for (String chunkId : echoCount.keySet()) {
                int count = echoCount.get(chunkId).get();
                System.out.println(chunkId + ": " + count + " echo(s)");
            }
        }
    }
    
    static final EchoTracker tracker = new EchoTracker();
    
    public static void main(String[] args) throws Exception {
        // Install a hook to capture echoes (in real code, would modify Receiver)
        System.out.println("NOTE: This test requires modification of Receiver.java to track echoes per chunk.");
        System.out.println("Currently, the echo logging doesn't tag which chunk the echo is for.\n");
        
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            
            // Load PNG image
            File pngFile = new File("/tmp/robot_fixed.png");
            if (!pngFile.exists()) {
                System.out.println("ERROR: /tmp/robot_fixed.png not found");
                return;
            }
            
            BufferedImage loadedImage = ImageIO.read(pngFile);
            System.out.println("Image loaded: " + loadedImage.getWidth() + "x" + loadedImage.getHeight());
            
            // Draw 3 times and monitor echoes
            for (int iteration = 1; iteration <= 3; iteration++) {
                System.out.println("\n=== ITERATION " + iteration + " ===");
                tracker.reset();
                
                var canvas = drone.controllerCreateCanvas();
                var graphics = canvas.getGraphics();
                graphics.drawImage(loadedImage, 0, 0, null);
                
                System.out.println("Drawing canvas (8 chunks, ~3 retries each = ~24 echoes expected)...");
                long startTime = System.currentTimeMillis();
                
                drone.controllerDrawCanvas(canvas);
                
                long endTime = System.currentTimeMillis();
                System.out.println("Canvas sent in " + (endTime - startTime) + "ms");
                
                // Give time for echoes to arrive
                Thread.sleep(500);
                
                tracker.report();
                System.out.println("Display result: Check screen visually");
            }
            
            System.out.println("\n=== KEY INSIGHT ===");
            System.out.println("If display rendering is inconsistent but echoes are always received,");
            System.out.println("it proves the issue is NOT lost delivery but display buffer sequencing.");
        }
    }
}
