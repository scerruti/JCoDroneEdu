package com.otabi.jcodroneedu.examples.tests;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneNotFoundException;
import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.cardreader.CardColor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * ColorSensorDebug - Debug tool to see raw color sensor data.
 * Shows HSVL values and color detection results.
 */
public class ColorSensorDebug {
    public static void main(String[] args) {
        try (Drone drone = new Drone()) {
            try {
                System.out.println("Auto-detecting controller port...");
                drone.connect();
            } catch (DroneNotFoundException e) {
                System.err.println("Could not connect to controller: " + e.getMessage());
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("\nColorSensorDebug - Place drone on colored card, press Enter to read.");
            System.out.println("Make sure the drone is sitting on a color card!\n");
            
            while (true) {
                System.out.println("Press Enter to read color sensor...");
                in.readLine();

                // Request color data
                System.out.println("Requesting CardColor data..."); // Removed unused DataType reference
                drone.sendRequest(DataType.CardColor);

                // Wait for data
                Thread.sleep(200);

                // Get the raw CardColor object
                CardColor cardColor = drone.getDroneStatus().getCardColor();
                
                if (cardColor == null) {
                    System.out.println("❌ NO COLOR DATA RECEIVED!");
                    System.out.println("   Make sure:");
                    System.out.println("   1. Drone is ON and connected");
                    System.out.println("   2. Drone is sitting on a colored card");
                    System.out.println("   3. Color card is flat under the drone\n");
                    continue;
                }

                System.out.println("\n✅ Color data received!");
                
                // Show HSVL data
                byte[][] hsvl = cardColor.getHsvl();
                if (hsvl != null && hsvl.length >= 2) {
                    System.out.println("\nHSVL Data:");
                    System.out.printf("  Front sensor: H=%3d S=%3d V=%3d L=%3d%n",
                        hsvl[0][0] & 0xFF, hsvl[0][1] & 0xFF, hsvl[0][2] & 0xFF, hsvl[0][3] & 0xFF);
                    System.out.printf("  Back sensor:  H=%3d S=%3d V=%3d L=%3d%n",
                        hsvl[1][0] & 0xFF, hsvl[1][1] & 0xFF, hsvl[1][2] & 0xFF, hsvl[1][3] & 0xFF);
                } else {
                    System.out.println("  HSVL data: null or invalid");
                }

                // Show detected colors using the built-in toString and helper methods
                System.out.println("\n" + cardColor.toString());
                
                // Note about BLACK value
                byte[] colors = cardColor.getColor();
                if (colors != null && colors.length >= 2) {
                    int frontColor = colors[0] & 0xFF;
                    int backColor = colors[1] & 0xFF;
                    if (frontColor == 8 || backColor == 8) {
                        System.out.println("\nNote: BLACK (8) often indicates no card visible (drone in air)");
                    }
                }

                // Show card value
                System.out.printf("\nCard byte: 0x%02X%n", cardColor.getCard() & 0xFF);
                System.out.println("─────────────────────────────────────────\n");
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
