package com.otabi.jcodroneedu;

/**
 * Represents Bluetooth address information from both the drone and controller.
 * 
 * <p>This class provides a type-safe, Java-idiomatic way to access address data,
 * as an alternative to the Python-compatible {@code Object[]} array returned by
 * {@link Drone#getAddressData()}.</p>
 * 
 * <p>Addresses are 5-byte Bluetooth addresses represented as strings.</p>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * Drone drone = new Drone();
 * drone.pair();
 * 
 * // Java-style composite object
 * AddressData addresses = drone.getAddressDataObject();
 * System.out.println("Drone address: " + addresses.getDroneAddress());
 * System.out.println("Controller address: " + addresses.getControllerAddress());
 * </pre>
 * 
 * @see Drone#getAddressDataObject()
 * @see Drone#getAddressData()
 * @since 1.0.0
 */
public class AddressData {
    private final double timestamp;
    private final String droneAddress;
    private final String controllerAddress;
    
    /**
     * Constructs an AddressData object with the specified values.
     * 
     * @param timestamp Timestamp when the data was retrieved (seconds since program start)
     * @param droneAddress Drone's Bluetooth address (5-byte address as string)
     * @param controllerAddress Controller's Bluetooth address (5-byte address as string)
     */
    public AddressData(double timestamp, String droneAddress, String controllerAddress) {
        this.timestamp = timestamp;
        this.droneAddress = droneAddress;
        this.controllerAddress = controllerAddress;
    }
    
    /**
     * Gets the timestamp when this data was retrieved.
     * 
     * @return Timestamp in seconds since program start
     */
    public double getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the drone's Bluetooth address.
     * 
     * @return Bluetooth address as string
     */
    public String getDroneAddress() {
        return droneAddress;
    }
    
    /**
     * Gets the controller's Bluetooth address.
     * 
     * @return Bluetooth address as string
     */
    public String getControllerAddress() {
        return controllerAddress;
    }
    
    /**
     * Returns a string representation of the address data.
     * 
     * @return Human-readable string with Bluetooth addresses
     */
    @Override
    public String toString() {
        return String.format("AddressData{timestamp=%.2f, droneAddress=%s, controllerAddress=%s}",
                timestamp, droneAddress, controllerAddress);
    }
}
