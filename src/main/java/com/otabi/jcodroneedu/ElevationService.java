package com.otabi.jcodroneedu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ElevationService handles elevation calculations from barometric pressure.
 * 
 * <p>This service bridges sensor telemetry (pressure readings) with external data sources
 * (weather APIs, location detection) to provide accurate altitude measurements. It separates
 * the domain logic of elevation calculation from raw sensor telemetry.</p>
 * 
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Calculate corrected elevation using sea-level pressure calibration</li>
 *   <li>Provide unit conversion for elevation measurements</li>
 *   <li>Integrate with WeatherService for automatic pressure calibration</li>
 *   <li>Apply the international standard atmosphere formula</li>
 * </ul>
 * 
 * <h3>Design Rationale:</h3>
 * <p>Elevation calculation involves more than sensor telemetry—it requires external data
 * (weather APIs), location detection, and domain-specific formulas. By separating this into
 * its own service, we keep TelemetryService focused on fresh sensor reads while providing
 * a natural home for altitude-related features like rate of climb, altitude history, etc.</p>
 * 
 * @since 1.0.15
 * @educational
 */
public class ElevationService {
    private static final Logger log = LogManager.getLogger(ElevationService.class);
    
    private final TelemetryService telemetryService;
    
    /**
     * Creates an ElevationService that delegates to the provided TelemetryService.
     * 
     * @param telemetryService The telemetry service for accessing sensor data
     */
    public ElevationService(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }
    
    /**
     * Gets the uncorrected (raw firmware) elevation in the specified unit.
     * 
     * <p>This delegates directly to TelemetryService for the raw sensor reading.
     * The firmware calculates altitude from pressure but includes an offset of ~100-150m.
     * For accurate altitude, use {@link #getCorrectedElevation()} instead.</p>
     * 
     * @param unit Target unit: "m", "cm", "km", "ft", or "mi" (case-insensitive)
     * @return Uncorrected elevation in the specified unit
     * @see #getCorrectedElevation()
     */
    public double getUncorrectedElevation(String unit) {
        return telemetryService.getUncorrectedElevation(unit);
    }
    
    /**
     * Calculates accurate altitude using automatic sea-level pressure detection.
     * 
     * <p>This method automatically determines the best sea-level pressure using a multi-tier
     * fallback strategy:</p>
     * <ol>
     *   <li>Try OS location services (if available via JNI)</li>
     *   <li>Try IP-based geolocation</li>
     *   <li>Fall back to standard atmosphere (101325 Pa)</li>
     * </ol>
     * 
     * <p>Uses the international standard atmosphere formula:
     * <pre>h = 44330 * (1 - (P/P₀)^0.1903)</pre>
     * where P is measured pressure and P₀ is sea-level pressure.</p>
     * 
     * @return Corrected altitude in meters above sea level, or 0.0 if no pressure data
     * @see #getCorrectedElevation(double)
     * @see #getCorrectedElevation(double, double)
     */
    public double getCorrectedElevation() {
        double seaLevelPressure = com.otabi.jcodroneedu.util.WeatherService.getAutomaticSeaLevelPressure();
        return getCorrectedElevation(seaLevelPressure);
    }
    
    /**
     * Calculates accurate altitude using automatic sea-level pressure detection,
     * converted to the specified unit.
     * 
     * @param unit Target unit: "m", "cm", "km", "ft", or "mi" (case-insensitive)
     * @return Corrected altitude in the specified unit
     * @see #getCorrectedElevation()
     */
    public double getCorrectedElevation(String unit) {
        double meters = getCorrectedElevation();
        return convertMetersToUnit(meters, unit);
    }
    
    /**
     * Calculates corrected altitude from barometric pressure using a specified sea-level pressure.
     * 
     * <p>This allows for manual calibration when the current local sea-level pressure is known
     * (from weather reports or nearby weather stations). Uses the international standard
     * atmosphere formula with the provided sea-level pressure.</p>
     * 
     * <h3>📡 Getting Local Sea-Level Pressure:</h3>
     * <ul>
     *   <li>Check local weather stations or weather apps</li>
     *   <li>Look for "barometric pressure" or "QNH" in aviation weather</li>
     *   <li>Convert if needed: 1 hPa = 100 Pa, 1 inHg = 3386.39 Pa</li>
     * </ul>
     * 
     * @param seaLevelPressure The current sea-level pressure in Pascals (Pa).
     *                         Standard atmosphere is 101325 Pa (1013.25 hPa).
     * @return Corrected altitude in meters above sea level, or 0.0 if no pressure data
     * @see #getCorrectedElevation()
     */
    public double getCorrectedElevation(double seaLevelPressure) {
        double pressure = telemetryService.getPressure("pa");
        if (pressure == 0.0) {
            return 0.0;
        }
        
        // International standard atmosphere formula
        // h = 44330 * (1 - (P/P₀)^0.1903)
        return calculateAltitudeFromPressure(pressure, seaLevelPressure);
    }
    
    /**
     * Calculates corrected altitude using a specified sea-level pressure,
     * converted to the specified unit.
     * 
     * @param seaLevelPressure The current sea-level pressure in Pascals (Pa)
     * @param unit Target unit: "m", "cm", "km", "ft", or "mi" (case-insensitive)
     * @return Corrected altitude in the specified unit
     * @see #getCorrectedElevation(double)
     */
    public double getCorrectedElevation(double seaLevelPressure, String unit) {
        double meters = getCorrectedElevation(seaLevelPressure);
        return convertMetersToUnit(meters, unit);
    }
    
    /**
     * Gets corrected elevation using current sea-level pressure from online weather data.
     * 
     * <p>This method automatically fetches the current barometric pressure from online
     * weather services (Open-Meteo API) based on the provided coordinates. This provides
     * the most accurate altitude readings by accounting for local weather conditions.</p>
     * 
     * <h3>📍 How to Find Your Coordinates:</h3>
     * <ul>
     *   <li><strong>Google Maps:</strong> Right-click location → "What's here?"</li>
     *   <li><strong>iPhone:</strong> Open Compass app (shows coordinates at bottom)</li>
     *   <li><strong>Command line:</strong> curl ipinfo.io (approximate)</li>
     * </ul>
     * 
     * <h3>🌐 Network Requirements:</h3>
     * <ul>
     *   <li>Requires internet connection</li>
     *   <li>Uses Open-Meteo API (free, no API key)</li>
     *   <li>Falls back to standard pressure if offline</li>
     *   <li>5-second timeout to avoid blocking</li>
     * </ul>
     * 
     * @param latitude Latitude in decimal degrees (-90 to 90)
     * @param longitude Longitude in decimal degrees (-180 to 180)
     * @return Corrected elevation in meters with weather-calibrated sea-level pressure
     * @throws IllegalArgumentException if coordinates are out of valid range
     * @see com.otabi.jcodroneedu.util.WeatherService#getSeaLevelPressure(double, double)
     */
    public double getCorrectedElevation(double latitude, double longitude) {
        double seaLevelPressure = com.otabi.jcodroneedu.util.WeatherService.getSeaLevelPressure(latitude, longitude);
        return getCorrectedElevation(seaLevelPressure);
    }
    
    /**
     * Gets corrected elevation using current sea-level pressure from online weather data,
     * converted to the specified unit.
     * 
     * @param latitude Latitude in decimal degrees (-90 to 90)
     * @param longitude Longitude in decimal degrees (-180 to 180)
     * @param unit Target unit: "m", "cm", "km", "ft", or "mi" (case-insensitive)
     * @return Corrected elevation in the specified unit
     * @see #getCorrectedElevation(double, double)
     */
    public double getCorrectedElevation(double latitude, double longitude, String unit) {
        double meters = getCorrectedElevation(latitude, longitude);
        return convertMetersToUnit(meters, unit);
    }
    
    /**
     * Applies the international standard atmosphere formula to calculate altitude.
     * 
     * <p>Formula: h = 44330 * (1 - (P/P₀)^0.1903)</p>
     * 
     * @param pressure Current atmospheric pressure in Pascals
     * @param seaLevelPressure Sea-level reference pressure in Pascals
     * @return Altitude in meters above sea level
     */
    private double calculateAltitudeFromPressure(double pressure, double seaLevelPressure) {
        return 44330.0 * (1.0 - Math.pow(pressure / seaLevelPressure, 0.1903));
    }
    
    /**
     * Converts meters to the specified unit.
     * 
     * @param meters Value in meters
     * @param unit Target unit: "m", "cm", "km", "ft", or "mi" (case-insensitive)
     * @return Converted value, or original meters if unit is null or unrecognized
     */
    private double convertMetersToUnit(double meters, String unit) {
        if (unit == null) return meters;
        
        switch (unit.toLowerCase()) {
            case "m":
                return meters;
            case "cm":
                return meters * 100.0;
            case "km":
                return meters / 1000.0;
            case "ft":
                return meters * 3.28084;
            case "mi":
                return meters / 1609.34;
            default:
                log.warn("Unknown elevation unit '{}', defaulting to meters", unit);
                return meters;
        }
    }
}
