package com.otabi.jcodroneedu;

/**
 * Represents device information from both the drone and controller.
 * 
 * <p>This class provides a type-safe, Java-idiomatic way to access information data,
 * as an alternative to the Python-compatible {@code Object[]} array returned by
 * {@link Drone#getInformationData()}.</p>
 * 
 * <p>Information data includes model numbers and firmware versions for both devices.</p>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * Drone drone = new Drone();
 * drone.pair();
 * 
 * // Java-style composite object
 * InformationData info = drone.getInformationDataObject();
 * System.out.println("Drone model: " + info.getDroneModel());
 * System.out.println("Drone firmware: " + info.getDroneFirmware());
 * System.out.println("Controller firmware: " + info.getControllerFirmware());
 * </pre>
 * 
 * @see Drone#getInformationDataObject()
 * @see Drone#getInformationData()
 * @since 1.0.0
 */
public class InformationData {
    private final double timestamp;
    private final DroneSystem.ModelNumber droneModel;
    private final String droneFirmware;
    private final DroneSystem.ModelNumber controllerModel;
    private final String controllerFirmware;
    
    /**
     * Constructs an InformationData object with the specified values.
     * 
     * @param timestamp Timestamp when the data was retrieved (seconds since program start)
     * @param droneModel Drone model number
     * @param droneFirmware Drone firmware version string (e.g., "1.2.3")
     * @param controllerModel Controller model number
     * @param controllerFirmware Controller firmware version string (e.g., "1.2.3")
     */
    public InformationData(double timestamp, DroneSystem.ModelNumber droneModel, String droneFirmware,
                          DroneSystem.ModelNumber controllerModel, String controllerFirmware) {
        this.timestamp = timestamp;
        this.droneModel = droneModel;
        this.droneFirmware = droneFirmware;
        this.controllerModel = controllerModel;
        this.controllerFirmware = controllerFirmware;
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
     * Gets the drone's model number.
     * 
     * @return Drone model number enum
     */
    public DroneSystem.ModelNumber getDroneModel() {
        return droneModel;
    }
    
    /**
     * Gets the drone's firmware version.
     * 
     * @return Firmware version string (format: "major.minor.build")
     */
    public String getDroneFirmware() {
        return droneFirmware;
    }
    
    /**
     * Gets the controller's model number.
     * 
     * @return Controller model number enum
     */
    public DroneSystem.ModelNumber getControllerModel() {
        return controllerModel;
    }
    
    /**
     * Gets the controller's firmware version.
     * 
     * @return Firmware version string (format: "major.minor.build")
     */
    public String getControllerFirmware() {
        return controllerFirmware;
    }
    
    /**
     * Returns a string representation of the information data.
     * 
     * @return Human-readable string with device information
     */
    @Override
    public String toString() {
        return String.format("InformationData{timestamp=%.2f, droneModel=%s, droneFirmware=%s, controllerModel=%s, controllerFirmware=%s}",
                timestamp, droneModel, droneFirmware, controllerModel, controllerFirmware);
    }
}
