package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;
import java.util.Random;

/**
 * Lesson 1.8: While Loops
 * 
 * This lesson demonstrates the use of while loops with the CoDrone EDU.
 * Students will learn:
 * - How while loops differ from for loops
 * - When to use a while loop (when the number of iterations is unknown)
 * - How to create loop conditions
 * - How to use random numbers to vary behavior
 * 
 * Challenge: Tower of Terror
 * Program the drone to mimic a drop tower ride by ascending and then
 * performing random up/down movements with pauses, just like the
 * Tower of Terror ride at Disney parks.
 */
public class L0108WhileLoops {
    public static void main(String[] args) {
        // Variables for the sway pattern
        int swayCount = 0;
        int maxSways = 4;
        int rollPower = 30;
        int moveDuration = 1;  // seconds

        try (Drone drone = new Drone(true)) {
            drone.takeoff();
            System.out.println("Drone has taken off!");
            System.out.println();

            // Demonstration: Sway back and forth using a while loop
            System.out.println("=== Sway Demonstration ===");
            while (swayCount < maxSways) {
                System.out.println("Sway #" + (swayCount + 1));
                drone.setRoll(rollPower);
                drone.move(moveDuration);
                drone.setRoll(-rollPower);
                drone.move(moveDuration);
                swayCount++;
            }

            drone.setRoll(0);
            System.out.println("Sway complete!");
            System.out.println();

            // Tower of Terror Challenge
            // Rules: 
            // 1. Must use either a for loop OR a while loop (your choice!)
            // 2. Must go up, pause, down, pause at least 3 times
            // 3. Must use random numbers at least 2 times for variety
            
            System.out.println("=== Tower of Terror Challenge ===");
            Random random = new Random();
            
            // Initial ascent to starting height
            int initialThrottle = 50;
            int initialClimbTime = 2;  // seconds
            int initialPauseTime = 1;   // seconds
            
            System.out.println("Initial climb: throttle=" + initialThrottle + ", duration=" + initialClimbTime + "s");
            drone.setThrottle(initialThrottle);
            drone.move(initialClimbTime);
            drone.hover(initialPauseTime);
            System.out.println("Current height: " + drone.getHeight() + " cm");
            System.out.println();

            // Perform the drop tower pattern
            int numberOfDrops = 0;
            int targetDrops = 3;  // Minimum required drops
            int maxHeightCm = 250;  // Safety limit: 250cm = 2.5 meters
            
            while (numberOfDrops < targetDrops) {
                System.out.println("--- Drop Cycle #" + (numberOfDrops + 1) + " ---");
                
                // Safety check: don't go too high
                double currentHeight = drone.getHeight();
                System.out.println("Current height: " + currentHeight + " cm");
                if (currentHeight > maxHeightCm) {
                    System.out.println("Safety limit reached! Stopping at " + maxHeightCm + " cm");
                    break;
                }
                
                // Random ascent: vary both speed and duration
                int minUpSpeed = 40;
                int maxUpSpeed = 60;
                int upSpeedRange = maxUpSpeed - minUpSpeed + 1;
                int upSpeed = random.nextInt(upSpeedRange) + minUpSpeed;
                
                int minDuration = 1;
                int maxDuration = 2;
                int durationRange = maxDuration - minDuration + 1;
                int upDuration = random.nextInt(durationRange) + minDuration;
                
                System.out.println("  Ascending: speed=" + upSpeed + ", duration=" + upDuration + "s");
                drone.setThrottle(upSpeed);
                drone.move(upDuration);
                
                int pauseTime = 1;  // seconds
                drone.hover(pauseTime);
                System.out.println("  Height after ascent: " + drone.getHeight() + " cm");
                
                // Random drop: vary both speed and duration
                int minDownSpeed = 30;
                int maxDownSpeed = 50;
                int downSpeedRange = maxDownSpeed - minDownSpeed + 1;
                int downSpeed = -(random.nextInt(downSpeedRange) + minDownSpeed);
                
                int downDuration = random.nextInt(durationRange) + minDuration;
                
                System.out.println("  Dropping: speed=" + downSpeed + ", duration=" + downDuration + "s");
                drone.setThrottle(downSpeed);
                drone.move(downDuration);
                drone.hover(pauseTime);
                System.out.println("  Height after drop: " + drone.getHeight() + " cm");
                System.out.println();
                
                numberOfDrops++;
            }

            System.out.println("Tower of Terror complete! Landing...");
            drone.land();
        } catch (DroneNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
