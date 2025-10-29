package com.otabi.jcodroneedu.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * Utility class for fetching real-time weather data for altitude calibration.
 * 
 * <p>This class provides methods to obtain current sea-level pressure from
 * online weather services, which can be used to improve altitude calculation
 * accuracy. Uses Open-Meteo API (free, no API key required).</p>
 * 
 * @educational
 * @since 1.0
 */
public class WeatherService {
    
    private static final String OPEN_METEO_API = "https://api.open-meteo.com/v1/forecast";
    private static final String IPINFO_API = "https://ipinfo.io/json";
    private static final int TIMEOUT_MS = 5000;
    
    // Hook for OS-level location detection (to be implemented with JNI)
    private static LocationProvider osLocationProvider = null;
    
    /**
     * Interface for OS-level location providers.
     * Implement this with JNI to get location from macOS/Windows/Linux.
     */
    public interface LocationProvider {
        /**
         * Gets location from OS.
         * @return [latitude, longitude] or null if unavailable
         */
        double[] getLocation();
    }
    
    /**
     * Sets a custom OS-level location provider (for JNI implementations).
     * 
     * @param provider The location provider to use, or null to disable
     */
    public static void setOSLocationProvider(LocationProvider provider) {
        osLocationProvider = provider;
    }
    
    /**
     * Automatically determines the best sea-level pressure using multi-tier fallback.
     * 
     * <p>This method attempts to get your location and fetch accurate sea-level pressure
     * using the following strategy:</p>
     * <ol>
     *   <li><strong>OS Location:</strong> Use system location services (if JNI provider set)</li>
     *   <li><strong>IP Geolocation:</strong> Estimate location from IP address</li>
     *   <li><strong>Standard Atmosphere:</strong> Fall back to 101325 Pa (1013.25 hPa)</li>
     * </ol>
     * 
     * <p>All failures are silent - the method always returns a valid pressure value.</p>
     * 
     * <h3>🎯 Educational Context:</h3>
     * <ul>
     *   <li><strong>Graceful Degradation:</strong> System works even when services fail</li>
     *   <li><strong>Fallback Strategies:</strong> Multiple data sources for reliability</li>
     *   <li><strong>Network Resilience:</strong> Handles offline scenarios</li>
     * </ul>
     * 
     * @return Sea-level pressure in Pascals (Pa)
     * @since 1.0
     * @educational
     */
    public static double getAutomaticSeaLevelPressure() {
        // Try OS-level location first (requires JNI)
        if (osLocationProvider != null) {
            try {
                double[] location = osLocationProvider.getLocation();
                if (location != null && location.length == 2) {
                    double pressure = getSeaLevelPressure(location[0], location[1]);
                    if (pressure != 101325.0) { // If not default fallback
                        return pressure;
                    }
                }
            } catch (Exception e) {
                // Silently continue to next method
            }
        }
        
        // Try IP-based geolocation
        try {
            double[] location = getLocationFromIP();
            if (location != null) {
                double pressure = getSeaLevelPressure(location[0], location[1]);
                if (pressure != 101325.0) { // If not default fallback
                    return pressure;
                }
            }
        } catch (Exception e) {
            // Silently continue to default
        }
        
        // Fall back to standard atmosphere
        return 101325.0;
    }
    
    /**
     * Gets approximate location from IP address using ipinfo.io API.
     * 
     * <p>Uses IP geolocation to estimate your location. This is less accurate than
     * OS location services but works without permissions. Accuracy is typically
     * within 50-100 km, which is sufficient for sea-level pressure calibration.</p>
     * 
     * <h3>🌐 API Information:</h3>
     * <ul>
     *   <li>Service: ipinfo.io</li>
     *   <li>Cost: Free (50,000 requests/month)</li>
     *   <li>No API key required for basic usage</li>
     *   <li>Returns: City-level location</li>
     * </ul>
     * 
     * @return Array of [latitude, longitude] or null if unavailable
     * @since 1.0
     * @educational
     */
    public static double[] getLocationFromIP() {
        try {
            URL url = new URL(IPINFO_API);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("User-Agent", "JCoDroneEdu/1.0 (Educational)");
            
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                JSONObject json = new JSONObject(response.toString());
                String loc = json.getString("loc"); // Format: "lat,lon"
                String[] parts = loc.split(",");
                
                if (parts.length == 2) {
                    double latitude = Double.parseDouble(parts[0]);
                    double longitude = Double.parseDouble(parts[1]);
                    return new double[]{latitude, longitude};
                }
            }
        } catch (Exception e) {
            // Silently return null
        }
        return null;
    }
    
    /**
     * Fetches the current sea-level pressure for a given location.
     * 
     * <p>Uses the Open-Meteo weather API to obtain accurate barometric pressure
     * readings. This data can be used with {@link com.otabi.jcodroneedu.Drone#getCorrectedElevation(double)}
     * for improved altitude accuracy.</p>
     * 
     * <h3>🌐 API Information:</h3>
     * <ul>
     *   <li>Service: Open-Meteo (open-meteo.com)</li>
     *   <li>Cost: Free, no API key required</li>
     *   <li>Rate Limit: Reasonable use (10,000 requests/day)</li>
     *   <li>Data: Real-time weather station data</li>
     * </ul>
     * 
     * <h3>📍 Finding Your Coordinates:</h3>
     * <ul>
     *   <li>Google Maps: Right-click → "What's here?"</li>
     *   <li>iPhone: Compass app shows coordinates</li>
     *   <li>Command line: Use 'curl ipinfo.io' for approximate location</li>
     * </ul>
     * 
     * <h3>💡 Example Usage:</h3>
     * <pre>
     * // Get pressure for New York City
     * double pressure = WeatherService.getSeaLevelPressure(40.7128, -74.0060);
     * double elevation = drone.getCorrectedElevation(pressure);
     * </pre>
     * 
     * @param latitude Latitude in decimal degrees (-90 to 90)
     * @param longitude Longitude in decimal degrees (-180 to 180)
     * @return Sea-level pressure in Pascals, or 101325.0 (standard atmosphere) if unavailable
     * @throws IllegalArgumentException if coordinates are out of valid range
     * @educational
     */
    public static double getSeaLevelPressure(double latitude, double longitude) {
        // Validate coordinates
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
        
        try {
            // Build API URL
            String urlString = String.format(
                "%s?latitude=%.4f&longitude=%.4f&current=surface_pressure&timezone=auto",
                OPEN_METEO_API, latitude, longitude
            );
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("User-Agent", "JCoDroneEdu/1.0 (Educational)");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                // Parse JSON response
                JSONObject json = new JSONObject(response.toString());
                JSONObject current = json.getJSONObject("current");
                double pressureHPa = current.getDouble("surface_pressure");
                
                // Convert hPa to Pascals
                return pressureHPa * 100.0;
            } else {
                System.err.println("Weather API returned status code: " + responseCode);
                return 101325.0; // Standard atmosphere
            }
            
        } catch (Exception e) {
            System.err.println("Failed to fetch weather data: " + e.getMessage());
            System.err.println("Using standard atmosphere pressure (101325 Pa)");
            return 101325.0; // Standard atmosphere fallback
        }
    }
    
    /**
     * Checks if internet connectivity is available.
     * 
     * <p>Performs a quick connectivity check before attempting to fetch weather data.
     * This is useful to avoid unnecessary delays when offline.</p>
     * 
     * @return true if internet is available, false otherwise
     * @educational
     */
    public static boolean isInternetAvailable() {
        try {
            // Try multiple common endpoints for better reliability
            String[] testUrls = {
                "https://api.open-meteo.com",
                "https://www.google.com",
                "https://www.cloudflare.com"
            };
            
            for (String urlString : testUrls) {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("HEAD");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code >= 200 && code < 400) {
                        return true;
                    }
                } catch (Exception ignored) {
                    // Try next URL
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets a formatted weather report including pressure and location.
     * 
     * <p>Returns a human-readable string with weather information useful
     * for educational displays and debugging.</p>
     * 
     * @param latitude Latitude in decimal degrees
     * @param longitude Longitude in decimal degrees
     * @return Formatted string with weather information
     * @educational
     */
    public static String getWeatherReport(double latitude, double longitude) {
        try {
            String urlString = String.format(
                "%s?latitude=%.4f&longitude=%.4f&current=surface_pressure,temperature_2m&timezone=auto",
                OPEN_METEO_API, latitude, longitude
            );
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("User-Agent", "JCoDroneEdu/1.0 (Educational)");
            
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                JSONObject json = new JSONObject(response.toString());
                JSONObject current = json.getJSONObject("current");
                double pressureHPa = current.getDouble("surface_pressure");
                double tempC = current.getDouble("temperature_2m");
                
                return String.format(
                    "Location: %.4f°, %.4f°\n" +
                    "Sea-Level Pressure: %.2f hPa (%.0f Pa)\n" +
                    "Temperature: %.1f°C",
                    latitude, longitude, pressureHPa, pressureHPa * 100, tempC
                );
            }
        } catch (Exception e) {
            return "Weather data unavailable: " + e.getMessage();
        }
        return "Weather data unavailable";
    }
}
