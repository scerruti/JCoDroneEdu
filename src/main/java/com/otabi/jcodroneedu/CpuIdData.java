package com.otabi.jcodroneedu;

/**
 * Represents CPU ID information from both the drone and controller.
 * 
 * <p>This class provides a type-safe, Java-idiomatic way to access CPU ID data,
 * as an alternative to the Python-compatible {@code Object[]} array returned by
 * {@link Drone#getCpuIdData()}.</p>
 * 
 * <p>CPU IDs are unique 96-bit identifiers for each device, represented as
 * hexadecimal strings.</p>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * Drone drone = new Drone();
 * drone.pair();
 * 
 * // Java-style composite object
 * CpuIdData cpuIds = drone.getCpuIdDataObject();
 * System.out.println("Drone CPU ID: " + cpuIds.getDroneCpuId());
 * System.out.println("Controller CPU ID: " + cpuIds.getControllerCpuId());
 * </pre>
 * 
 * @see Drone#getCpuIdDataObject()
 * @see Drone#getCpuIdData()
 * @since 1.0.0
 */
public class CpuIdData {
    private final double timestamp;
    private final String droneCpuId;
    private final String controllerCpuId;
    
    /**
     * Constructs a CpuIdData object with the specified values.
     * 
     * @param timestamp Timestamp when the data was retrieved (seconds since program start)
     * @param droneCpuId Drone's CPU ID (96-bit value as hex string)
     * @param controllerCpuId Controller's CPU ID (96-bit value as hex string)
     */
    public CpuIdData(double timestamp, String droneCpuId, String controllerCpuId) {
        this.timestamp = timestamp;
        this.droneCpuId = droneCpuId;
        this.controllerCpuId = controllerCpuId;
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
     * Gets the drone's unique CPU ID.
     * 
     * @return CPU ID as hexadecimal string
     */
    public String getDroneCpuId() {
        return droneCpuId;
    }
    
    /**
     * Gets the controller's unique CPU ID.
     * 
     * @return CPU ID as hexadecimal string
     */
    public String getControllerCpuId() {
        return controllerCpuId;
    }
    
    /**
     * Returns a string representation of the CPU ID data.
     * 
     * @return Human-readable string with CPU IDs
     */
    @Override
    public String toString() {
        return String.format("CpuIdData{timestamp=%.2f, droneCpuId=%s, controllerCpuId=%s}",
                timestamp, droneCpuId, controllerCpuId);
    }
}
