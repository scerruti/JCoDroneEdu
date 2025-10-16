package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.settings.Count;
import com.otabi.jcodroneedu.protocol.linkmanager.Information;
import com.otabi.jcodroneedu.protocol.linkmanager.Address;
import com.otabi.jcodroneedu.protocol.linkmanager.Version;
import com.otabi.jcodroneedu.protocol.DeviceType;

/**
 * Manages and stores inventory data from the drone and controller.
 * This class acts as a cache for flight statistics, device information,
 * CPU IDs, and addresses. It is populated by the Receiver as messages
 * are parsed from the drone.
 * 
 * <p>Inventory data includes:</p>
 * <ul>
 *   <li><strong>Count Data:</strong> Flight time, takeoff/landing/accident counts</li>
 *   <li><strong>Information Data:</strong> Device models and firmware versions</li>
 *   <li><strong>CPU ID Data:</strong> Unique CPU identifiers for drone and controller</li>
 *   <li><strong>Address Data:</strong> Communication addresses for drone and controller</li>
 * </ul>
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 * @since 1.0.0
 */
public class InventoryManager {
    
    // --- Count Data ---
    private volatile double countTimestamp = 0.0;
    private volatile int flightTime = 0;
    private volatile int takeoffCount = 0;
    private volatile int landingCount = 0;
    private volatile int accidentCount = 0;
    
    // --- Information Data ---
    private volatile double informationTimestamp = 0.0;
    private volatile DroneSystem.ModelNumber droneModel = DroneSystem.ModelNumber.NONE_;
    private volatile String droneFirmware = "";
    private volatile DroneSystem.ModelNumber controllerModel = DroneSystem.ModelNumber.NONE_;
    private volatile String controllerFirmware = "";
    
    // --- CPU ID Data ---
    private volatile double cpuIdTimestamp = 0.0;
    private volatile String droneCpuId = "";
    private volatile String controllerCpuId = "";
    
    // --- Address Data ---
    private volatile double addressTimestamp = 0.0;
    private volatile String droneAddress = "";
    private volatile String controllerAddress = "";
    
    /**
     * Updates count data when received from the drone.
     * 
     * @param count The count protocol data
     */
    public void updateCount(Count count) {
        if (count != null) {
            this.countTimestamp = System.currentTimeMillis() / 1000.0;
            this.flightTime = count.getTimeFlight();
            this.takeoffCount = count.getCountTakeOff();
            this.landingCount = count.getCountLanding();
            this.accidentCount = count.getCountAccident();
        }
    }
    
    /**
     * Updates information data when received from drone or controller.
     * 
     * @param information The information protocol data
     */
    public void updateInformation(Information information) {
        if (information != null) {
            this.informationTimestamp = System.currentTimeMillis() / 1000.0;
            
            // Determine device type from model number
            int deviceTypeByte = (information.getModelNumber().getValue() >> 8) & 0xFF;
            DeviceType deviceType = DeviceType.fromByte((byte) deviceTypeByte);
            
            if (deviceType == DeviceType.Drone) {
                this.droneModel = information.getModelNumber();
                this.droneFirmware = formatFirmwareVersion(information.getVersion());
            } else if (deviceType == DeviceType.Controller) {
                this.controllerModel = information.getModelNumber();
                this.controllerFirmware = formatFirmwareVersion(information.getVersion());
            }
        }
    }
    
    /**
     * Updates CPU ID data when received from drone or controller.
     * 
     * @param cpuId The CPU ID as base64 string
     * @param deviceType Whether this is from Drone or Controller
     */
    public void updateCpuId(String cpuId, DeviceType deviceType) {
        if (cpuId != null) {
            this.cpuIdTimestamp = System.currentTimeMillis() / 1000.0;
            
            if (deviceType == DeviceType.Drone) {
                this.droneCpuId = cpuId;
            } else if (deviceType == DeviceType.Controller) {
                this.controllerCpuId = cpuId;
            }
        }
    }
    
    /**
     * Updates address data when received from drone or controller.
     * 
     * @param address The address protocol data
     * @param deviceType Whether this is from Drone or Controller
     */
    public void updateAddress(Address address, DeviceType deviceType) {
        if (address != null) {
            this.addressTimestamp = System.currentTimeMillis() / 1000.0;
            
            // Convert address bytes to base64 string (matching Python behavior)
            String addressString = java.util.Base64.getEncoder().encodeToString(address.getAddress());
            
            if (deviceType == DeviceType.Drone) {
                this.droneAddress = addressString;
            } else if (deviceType == DeviceType.Controller) {
                this.controllerAddress = addressString;
            }
        }
    }
    
    /**
     * Gets count data as an array matching Python format.
     * [timestamp, flight_time, takeoff_count, landing_count, accident_count]
     * 
     * @return Object array with count data
     */
    public Object[] getCountDataArray() {
        return new Object[] {
            countTimestamp,
            flightTime,
            takeoffCount,
            landingCount,
            accidentCount
        };
    }
    
    /**
     * Gets information data as an array matching Python format.
     * [timestamp, drone_model, drone_firmware, controller_model, controller_firmware]
     * 
     * @return Object array with information data
     */
    public Object[] getInformationDataArray() {
        return new Object[] {
            informationTimestamp,
            droneModel,
            droneFirmware,
            controllerModel,
            controllerFirmware
        };
    }
    
    /**
     * Gets CPU ID data as an array matching Python format.
     * [timestamp, drone_cpu_id, controller_cpu_id]
     * 
     * @return Object array with CPU ID data
     */
    public Object[] getCpuIdDataArray() {
        return new Object[] {
            cpuIdTimestamp,
            droneCpuId,
            controllerCpuId
        };
    }
    
    /**
     * Gets address data as an array matching Python format.
     * [timestamp, drone_address, controller_address]
     * 
     * @return Object array with address data
     */
    public Object[] getAddressDataArray() {
        return new Object[] {
            addressTimestamp,
            droneAddress,
            controllerAddress
        };
    }
    
    // --- Composite Object Getters ---
    
    /**
     * Gets count data as a type-safe Java object.
     * 
     * @return CountData object with flight statistics
     */
    public CountData getCountDataObject() {
        return new CountData(countTimestamp, flightTime, takeoffCount, landingCount, accidentCount);
    }
    
    /**
     * Gets information data as a type-safe Java object.
     * 
     * @return InformationData object with device information
     */
    public InformationData getInformationDataObject() {
        return new InformationData(informationTimestamp, droneModel, droneFirmware, 
                                   controllerModel, controllerFirmware);
    }
    
    /**
     * Gets CPU ID data as a type-safe Java object.
     * 
     * @return CpuIdData object with CPU IDs
     */
    public CpuIdData getCpuIdDataObject() {
        return new CpuIdData(cpuIdTimestamp, droneCpuId, controllerCpuId);
    }
    
    /**
     * Gets address data as a type-safe Java object.
     * 
     * @return AddressData object with Bluetooth addresses
     */
    public AddressData getAddressDataObject() {
        return new AddressData(addressTimestamp, droneAddress, controllerAddress);
    }
    
    // --- Individual Getters ---
    
    public double getCountTimestamp() { return countTimestamp; }
    public int getFlightTime() { return flightTime; }
    public int getTakeoffCount() { return takeoffCount; }
    public int getLandingCount() { return landingCount; }
    public int getAccidentCount() { return accidentCount; }
    
    public double getInformationTimestamp() { return informationTimestamp; }
    public DroneSystem.ModelNumber getDroneModel() { return droneModel; }
    public String getDroneFirmware() { return droneFirmware; }
    public DroneSystem.ModelNumber getControllerModel() { return controllerModel; }
    public String getControllerFirmware() { return controllerFirmware; }
    
    public double getCpuIdTimestamp() { return cpuIdTimestamp; }
    public String getDroneCpuId() { return droneCpuId; }
    public String getControllerCpuId() { return controllerCpuId; }
    
    public double getAddressTimestamp() { return addressTimestamp; }
    public String getDroneAddress() { return droneAddress; }
    public String getControllerAddress() { return controllerAddress; }
    
    /**
     * Formats firmware version as a string.
     * 
     * @param version The version object
     * @return Formatted version string (e.g., "1.2.3")
     */
    private String formatFirmwareVersion(Version version) {
        if (version == null) {
            return "";
        }
        return version.toString();
    }
}
