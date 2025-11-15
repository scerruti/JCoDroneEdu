package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.dronestatus.Altitude;
import com.otabi.jcodroneedu.protocol.dronestatus.Position;
import com.otabi.jcodroneedu.protocol.dronestatus.Range;
import com.otabi.jcodroneedu.protocol.dronestatus.Motion;
import com.otabi.jcodroneedu.protocol.dronestatus.State;
import com.otabi.jcodroneedu.protocol.dronestatus.Attitude;
import com.otabi.jcodroneedu.protocol.dronestatus.Flow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TelemetryService centralizes sensor reads, freshness, and unit conversion.
 *
 * Responsibilities:
 * - Requests fresh telemetry frames (Range, Position, Altitude)
 * - Provides simple getters with unit conversion for student-facing API
 * - Serves as a single source of truth for sensor access
 */
public class TelemetryService {
    private static final Logger log = LogManager.getLogger(TelemetryService.class);
    private final Drone drone;
    private final Map<DataType, Long> lastUpdateMillis = new ConcurrentHashMap<>();
    private final Map<DataType, CompletableFuture<Void>> inFlight = new ConcurrentHashMap<>();
    private final Map<DataType, Integer> failureCounts = new ConcurrentHashMap<>();
    private final Map<DataType, Long> nextAllowedAfterMillis = new ConcurrentHashMap<>();

    public TelemetryService(Drone drone) {
        this.drone = drone;
    }

    // ---------------- Range (mm) -> converted ----------------

    public double getFrontRange(String unit) {
        ensureFresh(DataType.Range, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_RANGE_MS);
        Range range = drone.getDroneStatus().getRange();
        if (range == null) {
            log.warn("Range data not available for front range reading");
            return 0.0;
        }
        return convertMillimeter(range.getFront(), unit);
    }

    public double getBottomRange(String unit) {
        ensureFresh(DataType.Range, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_RANGE_MS);
        Range range = drone.getDroneStatus().getRange();
        if (range == null) {
            log.warn("Range data not available for bottom range reading");
            return 0.0;
        }
        return convertMillimeter(range.getBottom(), unit);
    }

    public double getHeight(String unit) {
        return getBottomRange(unit);
    }

    // ---------------- Position (m) -> converted ----------------

    public double getPosX(String unit) {
        ensureFresh(DataType.Position, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_POSITION_MS);
        Position p = drone.getDroneStatus().getPosition();
        if (p == null) {
            log.warn("Position data not available for X reading");
            return 0.0;
        }
        return convertMeter(p.getX(), unit);
    }

    public double getPosY(String unit) {
        ensureFresh(DataType.Position, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_POSITION_MS);
        Position p = drone.getDroneStatus().getPosition();
        if (p == null) {
            log.warn("Position data not available for Y reading");
            return 0.0;
        }
        return convertMeter(p.getY(), unit);
    }

    public double getPosZ(String unit) {
        ensureFresh(DataType.Position, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_POSITION_MS);
        Position p = drone.getDroneStatus().getPosition();
        if (p == null) {
            log.warn("Position data not available for Z reading");
            return 0.0;
        }
        return convertMeter(p.getZ(), unit);
    }

    // ---------------- Altitude/Barometer snapshot (no unit helpers here yet) ----------------

    public Altitude getAltitudeSnapshot() {
        ensureFresh(DataType.Altitude, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_ALTITUDE_MS);
        return drone.getDroneStatus().getAltitude();
    }

    // Pressure (Pa) with unit conversion
    public double getPressure(String unit) {
        Altitude alt = getAltitudeSnapshot();
        double pascals = (alt != null) ? alt.getPressure() : 0.0;
        int retries = 0;
        while (pascals == 0.0 && retries < DroneSystem.CommunicationConstants.TelemetryConfig.PRESSURE_RETRY_COUNT) {
            try {
                Thread.sleep(DroneSystem.CommunicationConstants.TelemetryConfig.PRESSURE_RETRY_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
            // Force refresh for altitude frame
            lastUpdateMillis.remove(DataType.Altitude);
            alt = getAltitudeSnapshot();
            pascals = (alt != null) ? alt.getPressure() : 0.0;
            retries++;
        }
        if (pascals == 0.0) return 0.0;
        if (unit == null) unit = "pa";
        switch (unit.toLowerCase()) {
            case "pa":
                return pascals;
            case "kpa":
                return pascals / 1000.0;
            case "mbar":
                return pascals / 100.0;
            case "inhg":
                return pascals / 3386.389;
            case "atm":
                return pascals / 101325.0;
            default:
                log.warn("Unknown pressure unit '{}', defaulting to Pa", unit);
                return pascals;
        }
    }

    // Temperature with unit conversion (C default)
    public double getTemperature(String unit) {
        Altitude alt = getAltitudeSnapshot();
        if (alt == null) return 0.0;
        double c = alt.getTemperature();
        if (unit == null) unit = "c";
        switch (unit.toLowerCase()) {
            case "c":
                return c;
            case "f":
                return c * 9.0 / 5.0 + 32.0;
            case "k":
                return c + 273.15;
            default:
                log.warn("Unknown temperature unit '{}', defaulting to C", unit);
                return c;
        }
    }

    // Uncorrected elevation (from firmware) with unit conversion
    public double getUncorrectedElevation(String unit) {
        Altitude alt = getAltitudeSnapshot();
        if (alt == null) return 0.0;
        double meters = alt.getAltitude();
        if (unit == null) unit = "m";
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
                log.warn("Unknown elevation unit '{}', defaulting to m", unit);
                return meters;
        }
    }

    // ---------------- Motion snapshot and helpers ----------------

    public Motion getMotionSnapshot() {
        ensureFresh(DataType.Motion, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_MOTION_MS);
        return drone.getDroneStatus().getMotion();
    }

    public double getAccelX_G() {
        Motion m = getMotionSnapshot();
        if (m == null) return 0.0;
        double ax_ms2 = m.getAccelX() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
        return ax_ms2 / 9.80665;
    }

    public double getAccelY_G() {
        Motion m = getMotionSnapshot();
        if (m == null) return 0.0;
        double ay_ms2 = m.getAccelY() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
        return ay_ms2 / 9.80665;
    }

    public double getAccelZ_G() {
        Motion m = getMotionSnapshot();
        if (m == null) return 0.0;
        double az_ms2 = m.getAccelZ() * DroneSystem.SensorScales.ACCEL_RAW_TO_MS2;
        return az_ms2 / 9.80665;
    }

    public int[] getAccelRaw() {
        Motion m = getMotionSnapshot();
        if (m == null) return new int[]{0,0,0};
        return new int[]{ m.getAccelX(), m.getAccelY(), m.getAccelZ() };
    }

    public int[] getGyroRaw() {
        Motion m = getMotionSnapshot();
        if (m == null) return new int[]{0,0,0};
        return new int[]{ m.getGyroRoll(), m.getGyroPitch(), m.getGyroYaw() };
    }

    public int[] getAngleRaw() {
        Motion m = getMotionSnapshot();
        if (m == null) return new int[]{0,0,0};
        return new int[]{ m.getAngleRoll(), m.getAnglePitch(), m.getAngleYaw() };
    }

    public double getAngleX_Deg() {
        Motion m = getMotionSnapshot();
        if (m == null) return 0.0;
        return m.getAngleRoll() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG;
    }

    public double getAngleY_Deg() {
        Motion m = getMotionSnapshot();
        if (m == null) return 0.0;
        return m.getAnglePitch() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG;
    }

    public double getAngleZ_Deg() {
        Motion m = getMotionSnapshot();
        if (m == null) return 0.0;
        return m.getAngleYaw() * DroneSystem.SensorScales.ANGLE_RAW_TO_DEG;
    }

    // ---------------- Other snapshots (for future expansion) ----------------

    public State getStateSnapshot() {
        ensureFresh(DataType.State, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_STATE_MS);
        return drone.getDroneStatus().getState();
    }

    public Attitude getAttitudeSnapshot() {
        ensureFresh(DataType.Attitude, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_STATE_MS);
        return drone.getDroneStatus().getAttitude();
    }

    public Range getRangeSnapshot() {
        ensureFresh(DataType.Range, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_RANGE_MS);
        return drone.getDroneStatus().getRange();
    }

    public Position getPositionSnapshot() {
        ensureFresh(DataType.Position, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_POSITION_MS);
        return drone.getDroneStatus().getPosition();
    }

    public Flow getFlowSnapshot() {
        ensureFresh(DataType.Flow, DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_RANGE_MS);
        return drone.getDroneStatus().getFlow();
    }

    // ---------------- Internals ----------------

    private void ensureFresh(DataType type, long freshnessMs) {
        long now = System.currentTimeMillis();

        long effectiveFreshness = freshnessMs;
        try {
            var lm = drone.getLinkManager();
            var rssiObj = (lm != null) ? lm.getRssi() : null;
            if (rssiObj != null) {
                int rssi = rssiObj.getRssi();
                if (rssi != 0 && rssi <= DroneSystem.CommunicationConstants.TelemetryConfig.RSSI_WEAK_DBM) {
                    effectiveFreshness = (long) Math.ceil(freshnessMs * DroneSystem.CommunicationConstants.TelemetryConfig.FRESHNESS_WEAK_SCALE);
                }
            }
        } catch (Exception ignored) {}

        Long last = lastUpdateMillis.get(type);
        if (last != null && (now - last) <= effectiveFreshness) {
            return; // Fresh enough
        }

        // Backoff gate: if recent failures, avoid spamming the link
        Long nextAllowed = nextAllowedAfterMillis.get(type);
        if (nextAllowed != null && now < nextAllowed) {
            // Still in backoff; serve cached if any and return
            return;
        }

        CompletableFuture<Void> existing = inFlight.get(type);
        if (existing != null) {
            try {
                existing.get(DroneSystem.CommunicationConstants.TelemetryConfig.WAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (TimeoutException te) {
                // Serve stale; refresh will complete later
            } catch (Exception e) {
                log.debug("ensureFresh in-flight error for {}: {}", type, e.toString());
            }
            return;
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        CompletableFuture<Void> prior = inFlight.putIfAbsent(type, future);
        if (prior != null) {
            // Another thread just started it; wait briefly
            try {
                prior.get(DroneSystem.CommunicationConstants.TelemetryConfig.WAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception ignored) {
            }
            return;
        }

        try {
            if (DroneSystem.CommunicationConstants.TelemetryConfig.CLASSROOM_MODE && DroneSystem.CommunicationConstants.TelemetryConfig.JITTER_MAX_MS > 0) {
                int jitter = ThreadLocalRandom.current().nextInt(0, DroneSystem.CommunicationConstants.TelemetryConfig.JITTER_MAX_MS + 1);
                if (jitter > 0) {
                    try { Thread.sleep(jitter); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }

            drone.sendRequestWait(type);
            lastUpdateMillis.put(type, System.currentTimeMillis());
            future.complete(null);
            // Success: reset failure count and backoff
            failureCounts.remove(type);
            nextAllowedAfterMillis.remove(type);
        } catch (Exception e) {
            future.completeExceptionally(e);
            // Failure: increment backoff
            int count = failureCounts.getOrDefault(type, 0) + 1;
            failureCounts.put(type, count);
            long backoff = DroneSystem.CommunicationConstants.TelemetryConfig.FAILURE_BACKOFF_BASE_MS;
            // Exponential backoff capped at max
            backoff = Math.min(DroneSystem.CommunicationConstants.TelemetryConfig.FAILURE_BACKOFF_MAX_MS,
                    backoff << Math.min(count - 1, 4)); // cap shift to avoid overflow
            nextAllowedAfterMillis.put(type, System.currentTimeMillis() + backoff);
        } finally {
            inFlight.remove(type);
        }
    }

    /**
     * Returns the age in milliseconds of the last update for the given DataType,
     * or -1 if no update has been received.
     */
    public long getLastUpdateAgeMillis(DataType type) {
        Long last = lastUpdateMillis.get(type);
        if (last == null) return -1L;
        return Math.max(0L, System.currentTimeMillis() - last);
    }

    private double convertMillimeter(double millimeter, String unit) {
        if (unit == null) unit = DroneSystem.UnitConversion.UNIT_CENTIMETERS;
        switch (unit.toLowerCase()) {
            case "mm":
                return millimeter;
            case "cm":
                return millimeter / 10.0;
            case "m":
                return millimeter / 1000.0;
            case "in":
                return millimeter / 25.4;
            default:
                log.warn("Unknown unit '{}', defaulting to cm", unit);
                return millimeter / 10.0;
        }
    }

    private double convertMeter(double meter, String unit) {
        if (unit == null) unit = DroneSystem.UnitConversion.UNIT_CENTIMETERS;
        switch (unit.toLowerCase()) {
            case "m":
                return meter;
            case "cm":
                return meter * 100.0;
            case "mm":
                return meter * 1000.0;
            case "in":
                return meter * 39.3701;
            default:
                log.warn("Unknown unit '{}', defaulting to cm", unit);
                return meter * 100.0;
        }
    }
}
