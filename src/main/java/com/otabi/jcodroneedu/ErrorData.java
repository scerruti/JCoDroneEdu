package com.otabi.jcodroneedu;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

/**
 * Immutable data class representing error state information from the CoDrone EDU.
 * 
 * <p>This class provides a type-safe, Java-idiomatic way to access error data,
 * recommended over the array-based {@link Drone#getErrorData()} method.</p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Recommended Java approach - type-safe with enum sets
 * ErrorData errorData = drone.getErrors();
 * if (errorData != null) {
 *     // Check for specific errors using type-safe methods
 *     if (errorData.hasStateError(DroneSystem.ErrorFlagsForState.LOW_BATTERY)) {
 *         System.out.println("Warning: Low battery!");
 *         drone.land();
 *     }
     
 *     // Check if calibrating
 *     if (errorData.hasSensorError(DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING)) {
 *         System.out.println("Calibrating... please wait");
 *     }
 *     
 *     // Get all active errors as sets
 *     Set<DroneSystem.ErrorFlagsForSensor> sensorErrors = errorData.getSensorErrors();
 *     Set<DroneSystem.ErrorFlagsForState> stateErrors = errorData.getStateErrors();
 *     
 *     // Check if any errors exist
 *     if (!errorData.hasAnyErrors()) {
 *         System.out.println("All systems normal!");
 *     }
 * }
 * }</pre>
 * 
 * <h3>Comparison with Python-style API:</h3>
 * <pre>{@code
 * // Python-compatible approach (still available)
 * double[] errorArray = drone.getErrorData();
 * if (errorArray != null) {
 *     int stateErrors = (int) errorArray[2];
 *     if ((stateErrors & DroneSystem.ErrorFlagsForState.LOW_BATTERY.getValue()) != 0) {
 *         System.out.println("Warning: Low battery!");
 *     }
 * }
 * 
 * // Java-idiomatic approach (recommended)
 * ErrorData errorData = drone.getErrors();
 * if (errorData != null && errorData.hasStateError(DroneSystem.ErrorFlagsForState.LOW_BATTERY)) {
 *     System.out.println("Warning: Low battery!");
 * }
 * }</pre>
 * 
 * <h3>Benefits of Type-Safe Approach:</h3>
 * <ul>
 *   <li><strong>No array indexing:</strong> Named methods instead of errorData[1], errorData[2]</li>
 *   <li><strong>No bit manipulation:</strong> Simple boolean checks instead of bitwise operations</li>
 *   <li><strong>IDE auto-completion:</strong> All available errors visible in IDE</li>
 *   <li><strong>Compile-time safety:</strong> Can't mix up sensor vs state error flags</li>
 *   <li><strong>More readable:</strong> Self-documenting code</li>
 * </ul>
 * 
 * @author JCoDroneEdu Development Team
 * @since 2.5
 * @see Drone#getErrors()
 * @see DroneSystem.ErrorFlagsForSensor
 * @see DroneSystem.ErrorFlagsForState
 */
public class ErrorData {
    private final Instant timestamp;
    private final int sensorErrorFlags;
    private final int stateErrorFlags;
    private final Set<DroneSystem.ErrorFlagsForSensor> sensorErrors;
    private final Set<DroneSystem.ErrorFlagsForState> stateErrors;
    
    /**
     * Creates a new ErrorData instance from raw flag values.
     * 
     * @param timestampSeconds The timestamp in seconds when the error data was captured
     * @param sensorErrorFlags The raw sensor error flags bitmask
     * @param stateErrorFlags The raw state error flags bitmask
     */
    public ErrorData(double timestampSeconds, int sensorErrorFlags, int stateErrorFlags) {
        this.timestamp = Instant.ofEpochMilli((long) (timestampSeconds * 1000));
        this.sensorErrorFlags = sensorErrorFlags;
        this.stateErrorFlags = stateErrorFlags;
        this.sensorErrors = parseSensorErrors(sensorErrorFlags);
        this.stateErrors = parseStateErrors(stateErrorFlags);
    }
    
    /**
     * Creates a new ErrorData instance from a Python-style array.
     * 
     * @param errorArray Array containing [timestamp, sensorFlags, stateFlags]
     * @return ErrorData instance, or null if array is invalid
     */
    public static ErrorData fromArray(double[] errorArray) {
        if (errorArray == null || errorArray.length != 3) {
            return null;
        }
        return new ErrorData(errorArray[0], (int) errorArray[1], (int) errorArray[2]);
    }
    
    // --- Sensor Error Methods ---
    
    /**
     * Checks if a specific sensor error is present.
     * 
     * @param errorFlag The sensor error flag to check
     * @return true if this error is present, false otherwise
     */
    public boolean hasSensorError(DroneSystem.ErrorFlagsForSensor errorFlag) {
        return sensorErrors.contains(errorFlag);
    }
    
    /**
     * Gets all active sensor errors as an immutable set.
     * 
     * @return Set of active sensor errors (empty if no errors)
     */
    public Set<DroneSystem.ErrorFlagsForSensor> getSensorErrors() {
        return EnumSet.copyOf(sensorErrors);
    }
    
    /**
     * Gets the raw sensor error flags bitmask.
     * 
     * <p>Use this for bitwise operations or Python-style compatibility.
     * For Java code, prefer {@link #hasSensorError(DroneSystem.ErrorFlagsForSensor)}.</p>
     * 
     * @return Raw sensor error flags
     */
    public int getSensorErrorFlags() {
        return sensorErrorFlags;
    }
    
    /**
     * Checks if any sensor errors are present.
     * 
     * @return true if any sensor errors exist, false otherwise
     */
    public boolean hasAnySensorErrors() {
        return !sensorErrors.isEmpty();
    }
    
    // --- State Error Methods ---
    
    /**
     * Checks if a specific state error is present.
     * 
     * @param errorFlag The state error flag to check
     * @return true if this error is present, false otherwise
     */
    public boolean hasStateError(DroneSystem.ErrorFlagsForState errorFlag) {
        return stateErrors.contains(errorFlag);
    }
    
    /**
     * Gets all active state errors as an immutable set.
     * 
     * @return Set of active state errors (empty if no errors)
     */
    public Set<DroneSystem.ErrorFlagsForState> getStateErrors() {
        return EnumSet.copyOf(stateErrors);
    }
    
    /**
     * Gets the raw state error flags bitmask.
     * 
     * <p>Use this for bitwise operations or Python-style compatibility.
     * For Java code, prefer {@link #hasStateError(DroneSystem.ErrorFlagsForState)}.</p>
     * 
     * @return Raw state error flags
     */
    public int getStateErrorFlags() {
        return stateErrorFlags;
    }
    
    /**
     * Checks if any state errors are present.
     * 
     * @return true if any state errors exist, false otherwise
     */
    public boolean hasAnyStateErrors() {
        return !stateErrors.isEmpty();
    }
    
    // --- Combined Methods ---
    
    /**
     * Checks if any errors (sensor or state) are present.
     * 
     * @return true if any errors exist, false otherwise
     */
    public boolean hasAnyErrors() {
        return hasAnySensorErrors() || hasAnyStateErrors();
    }
    
    /**
     * Gets the timestamp when this error data was captured.
     * 
     * @return Timestamp as Instant
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the timestamp in seconds (Python-compatible format).
     * 
     * @return Timestamp in seconds since epoch
     */
    public double getTimestampSeconds() {
        return timestamp.toEpochMilli() / 1000.0;
    }
    
    // --- Convenience Methods ---
    
    /**
     * Checks if the drone is currently calibrating.
     * 
     * @return true if motion calibration is in progress
     */
    public boolean isCalibrating() {
        return hasSensorError(DroneSystem.ErrorFlagsForSensor.MOTION_CALIBRATING);
    }
    
    /**
     * Checks if the battery is low.
     * 
     * @return true if low battery error is present
     */
    public boolean isLowBattery() {
        return hasStateError(DroneSystem.ErrorFlagsForState.LOW_BATTERY);
    }
    
    /**
     * Checks if there are any critical errors that should stop flight.
     * 
     * @return true if critical errors exist
     */
    public boolean hasCriticalErrors() {
        return hasStateError(DroneSystem.ErrorFlagsForState.LOW_BATTERY)
            || hasStateError(DroneSystem.ErrorFlagsForState.ATTITUDE_NOT_STABLE)
            || hasStateError(DroneSystem.ErrorFlagsForState.TAKEOFF_FAILURE_CHECK_PROPELLER_AND_MOTOR)
            || hasStateError(DroneSystem.ErrorFlagsForState.CHECK_PROPELLER_VIBRATION);
    }
    
    // --- Private Helper Methods ---
    
    private static Set<DroneSystem.ErrorFlagsForSensor> parseSensorErrors(int flags) {
        Set<DroneSystem.ErrorFlagsForSensor> errors = EnumSet.noneOf(DroneSystem.ErrorFlagsForSensor.class);
        for (DroneSystem.ErrorFlagsForSensor error : DroneSystem.ErrorFlagsForSensor.values()) {
            if ((flags & error.getValue()) != 0) {
                errors.add(error);
            }
        }
        return errors;
    }
    
    private static Set<DroneSystem.ErrorFlagsForState> parseStateErrors(int flags) {
        Set<DroneSystem.ErrorFlagsForState> errors = EnumSet.noneOf(DroneSystem.ErrorFlagsForState.class);
        for (DroneSystem.ErrorFlagsForState error : DroneSystem.ErrorFlagsForState.values()) {
            if ((flags & error.getValue()) != 0) {
                errors.add(error);
            }
        }
        return errors;
    }
    
    // --- Object Methods ---
    
    /**
     * Returns a string representation of this error data.
     * 
     * @return String representation listing all active errors
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ErrorData{");
        sb.append("timestamp=").append(timestamp);
        
        if (hasAnySensorErrors()) {
            sb.append(", sensorErrors=").append(sensorErrors);
        }
        
        if (hasAnyStateErrors()) {
            sb.append(", stateErrors=").append(stateErrors);
        }
        
        if (!hasAnyErrors()) {
            sb.append(", no errors");
        }
        
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Returns a detailed multi-line string describing all active errors.
     * 
     * @return Detailed error description
     */
    public String toDetailedString() {
        if (!hasAnyErrors()) {
            return "No errors detected - all systems normal";
        }
        
        StringBuilder sb = new StringBuilder("Error Status:\n");
        
        if (hasAnySensorErrors()) {
            sb.append("  Sensor Errors:\n");
            for (DroneSystem.ErrorFlagsForSensor error : sensorErrors) {
                sb.append("    - ").append(error.name()).append("\n");
            }
        }
        
        if (hasAnyStateErrors()) {
            sb.append("  State Errors:\n");
            for (DroneSystem.ErrorFlagsForState error : stateErrors) {
                sb.append("    - ").append(error.name()).append("\n");
            }
        }
        
        return sb.toString();
    }
}
