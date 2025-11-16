package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Test: Display the robot emoji image on the controller screen.
 */
public class RobotEmojiTest {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone()) {
            drone.pair();
            System.out.println("Connected to drone");
            
            // Load PNG image from file
            File pngFile = new File("/tmp/robot_fixed.png");
            if (!pngFile.exists()) {
                System.out.println("ERROR: /tmp/robot_fixed.png not found");
                return;
            }
            
            System.out.println("Loading image...");
            BufferedImage loadedImage = ImageIO.read(pngFile);
            System.out.println("Image size: " + loadedImage.getWidth() + "x" + loadedImage.getHeight());
            
            // Draw 10 times
            for (int iteration = 1; iteration <= 10; iteration++) {
                System.out.println("Iteration " + iteration + "/10 - Drawing robot emoji...");
                
                var canvas = drone.controllerCreateCanvas();
                var graphics = canvas.getGraphics();
                graphics.drawImage(loadedImage, 0, 0, null);
                drone.controllerDrawCanvas(canvas);
                
                if (iteration < 10) {
                    Thread.sleep(3000);
                }
            }
            
            System.out.println("Test complete!");
        }
    }
}
