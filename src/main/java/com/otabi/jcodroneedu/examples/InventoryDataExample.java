package com.otabi.jcodroneedu.examples;

import com.otabi.jcodroneedu.*;

/**
 * Demonstrates three different ways to access inventory data in Java:
 * 1. Python-compatible Object arrays
 * 2. Individual typed getters
 * 3. Java composite objects (recommended for Java developers)
 */
public class InventoryDataExample {
    public static void main(String[] args) {
        Drone drone = new Drone();
        
        try {
            drone.pair();
            
            System.out.println("=== Inventory Data Access Examples ===\n");
            
            // ========================================
            // Style 1: Python-Compatible Arrays
            // ========================================
            System.out.println("1. PYTHON-STYLE ARRAYS (for Python compatibility):");
            Object[] countArray = drone.getCountData();
            System.out.println("   Flight time: " + countArray[1] + " seconds");
            System.out.println("   Takeoffs: " + countArray[2]);
            System.out.println("   Landings: " + countArray[3]);
            System.out.println("   Note: Requires casting and array indexing\n");
            
            // ========================================
            // Style 2: Individual Typed Getters
            // ========================================
            System.out.println("2. INDIVIDUAL GETTERS (simple and type-safe):");
            int flightTime = drone.getFlightTime();
            int takeoffs = drone.getTakeoffCount();
            int landings = drone.getLandingCount();
            int accidents = drone.getAccidentCount();
            System.out.println("   Flight time: " + flightTime + " seconds");
            System.out.println("   Takeoffs: " + takeoffs);
            System.out.println("   Landings: " + landings);
            System.out.println("   Accidents: " + accidents);
            System.out.println("   Note: Best for getting individual values\n");
            
            // ========================================
            // Style 3: Java Composite Objects (RECOMMENDED)
            // ========================================
            System.out.println("3. JAVA COMPOSITE OBJECTS (recommended for Java):");
            CountData countData = drone.getCountDataObject();
            System.out.println("   " + countData);
            System.out.println("   Flight time: " + countData.getFlightTime() + " seconds");
            System.out.println("   Takeoffs: " + countData.getTakeoffCount());
            System.out.println("   Landings: " + countData.getLandingCount());
            System.out.println("   Accidents: " + countData.getAccidentCount());
            System.out.println("   Timestamp: " + countData.getTimestamp());
            System.out.println("   Note: Most Java-idiomatic, includes all data\n");
            
            // ========================================
            // Device Information Example
            // ========================================
            System.out.println("=== Device Information ===\n");
            
            InformationData info = drone.getInformationDataObject();
            System.out.println("Drone:");
            System.out.println("  Model: " + info.getDroneModel());
            System.out.println("  Firmware: " + info.getDroneFirmware());
            System.out.println("\nController:");
            System.out.println("  Model: " + info.getControllerModel());
            System.out.println("  Firmware: " + info.getControllerFirmware());
            
            // ========================================
            // CPU ID Example
            // ========================================
            System.out.println("\n=== CPU IDs ===\n");
            
            CpuIdData cpuIds = drone.getCpuIdDataObject();
            System.out.println("Drone CPU ID: " + cpuIds.getDroneCpuId());
            System.out.println("Controller CPU ID: " + cpuIds.getControllerCpuId());
            
            // ========================================
            // Address Example
            // ========================================
            System.out.println("\n=== Bluetooth Addresses ===\n");
            
            AddressData addresses = drone.getAddressDataObject();
            System.out.println("Drone address: " + addresses.getDroneAddress());
            System.out.println("Controller address: " + addresses.getControllerAddress());
            
        } finally {
            drone.close();
        }
    }
}
