package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.Drone;
// DroneNotFoundException not used; handle generic exceptions instead

import java.util.Scanner;

/**
 * Interactive menu-driven test harness for exercising flight and sensor code.
 * - safe-by-default: flight commands require explicit confirmation
 * - reuses Drone.connect/pair APIs from the project
 */
public class TestHarness {

    private static void printMenu() {
        System.out.println("--- CoDrone EDU Test Harness ---");
        System.out.println("1) Auto-detect & connect");
        System.out.println("2) Connect to specific port");
        System.out.println("3) Show battery");
        System.out.println("4) Show model/info");
        System.out.println("5) Read sensors once (state/joystick)");
        System.out.println("6) Hover (safe) — will run a short hover");
        System.out.println("7) Conservative flight (requires explicit confirm)");
        System.out.println("8) Disconnect");
        System.out.println("9) Quit");
        System.out.print("Select> ");
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Drone drone = null;

        try {
            while (true) {
                printMenu();
                String line = in.nextLine().trim();
                if (line.isEmpty()) continue;

                int choice;
                try {
                    choice = Integer.parseInt(line);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid selection");
                    continue;
                }

                switch (choice) {
                    case 1:
                        if (drone != null && drone.isConnected()) {
                            System.out.println("Already connected.");
                            break;
                        }
                        try {
                            drone = new Drone();
                            System.out.println("Attempting auto-detect and pair()...");
                            boolean ok = drone.pair();
                            System.out.println("Connected: " + ok);
                        } catch (Exception e) {
                            System.err.println("Drone not found or connection failed: " + e.getMessage());
                        }
                        break;

                    case 2:
                        System.out.print("Port path: ");
                        String port = in.nextLine().trim();
                        if (port.isEmpty()) {
                            System.out.println("No port provided.");
                            break;
                        }
                        if (drone == null) drone = new Drone();
                        boolean ok = drone.connect(port);
                        System.out.println("Connected: " + ok);
                        break;

                    case 3:
                        if (drone == null || !drone.isConnected()) {
                            System.out.println("Not connected");
                            break;
                        }
                        try {
                            System.out.printf("Battery: %d%%%n", drone.getBattery());
                        } catch (Exception e) {
                            System.out.println("Error reading battery: " + e.getMessage());
                        }
                        break;

                    case 4:
                        if (drone == null || !drone.isConnected()) {
                            System.out.println("Not connected");
                            break;
                        }
                        try {
                            // Use existing LinkManager / Information API
                            com.otabi.jcodroneedu.LinkManager lm = drone.getLinkManager();
                            if (lm != null && lm.getInformation() != null) {
                                com.otabi.jcodroneedu.protocol.linkmanager.Information info = lm.getInformation();
                                String model = info.getModelNumber() != null ? info.getModelNumber().toString() : "UNKNOWN";
                                String fw = info.getVersion() != null ? info.getVersion().toString() : "UNKNOWN";
                                System.out.println("Model: " + model);
                                System.out.println("Firmware: " + fw);
                            } else {
                                System.out.println("Model/firmware information not available yet.");
                            }
                        } catch (Exception e) {
                            System.out.println("Error reading information: " + e.getMessage());
                        }
                        break;

                    case 5:
                        if (drone == null || !drone.isConnected()) {
                            System.out.println("Not connected");
                            break;
                        }
                        try {
                            // Drone exposes a DroneStatus and joystick data via existing APIs
                            System.out.println("Status: " + drone.getDroneStatus());
                            System.out.println("Joystick: " + java.util.Arrays.toString(drone.getJoystickData()));
                        } catch (Exception e) {
                            System.out.println("Error reading sensors: " + e.getMessage());
                        }
                        break;

                    case 6:
                        if (drone == null || !drone.isConnected()) {
                            System.out.println("Not connected");
                            break;
                        }
                        System.out.println("This will send a safe hover command (no takeoff). Confirm (y/N)? ");
                        String confirm = in.nextLine().trim();
                        if (confirm.equalsIgnoreCase("y")) {
                            try {
                                drone.hover(0.5);
                                System.out.println("Hover command sent.");
                            } catch (Exception e) {
                                System.out.println("Hover failed: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Aborted.");
                        }
                        break;

                    case 7:
                        if (drone == null || !drone.isConnected()) {
                            System.out.println("Not connected");
                            break;
                        }
                        System.out.println("Conservative flight will perform a short supervised takeoff/hover/land sequence.");
                        System.out.println("Type CONFIRM to proceed: ");
                        String proceed = in.nextLine().trim();
                        if (proceed.equals("CONFIRM")) {
                            System.out.println("Proceeding with conservative flight...");
                            try {
                                // Reuse conservative pattern from examples
                                com.otabi.jcodroneedu.examples.ConservativeFlight.runShortFlight(drone);
                                System.out.println("Conservative flight completed.");
                            } catch (Exception e) {
                                System.out.println("Flight failed: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Conservative flight aborted.");
                        }
                        break;

                    case 8:
                        if (drone != null) {
                            try {
                                drone.disconnect();
                                System.out.println("Disconnected.");
                            } catch (Exception e) {
                                System.out.println("Disconnect failed: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Not connected");
                        }
                        break;

                    case 9:
                        System.out.println("Quitting... Goodbye.");
                        if (drone != null) {
                            try { drone.disconnect(); } catch (Exception ignored) {}
                        }
                        in.close();
                        return;

                    default:
                        System.out.println("Unknown option");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (drone != null) drone.disconnect();
            } catch (Exception ignored) {}
        }
    }
}
