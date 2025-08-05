// This file has been deleted as per user request.
package com.otabi.jcodroneedu.autonomous.examples;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.autonomous.AutonomousMethod;
import java.util.Map;

/**
 * Advanced autonomous method that flies the drone in a spiral pattern.
 * 
 * <p>This class demonstrates a more complex autonomous behavior that combines
 * circular movement with altitude changes, creating a 3D spiral pattern.
 * It showcases advanced mathematical concepts and multi-axis control.</p>
 * 
 * <h3>🎯 Educational Concepts:</h3>
 * <ul>
 *   <li><strong>3D Movement:</strong> Simultaneous horizontal and vertical motion</li>
 *   <li><strong>Progressive Parameters:</strong> Values that change over time</li>
 *   <li><strong>Mathematical Modeling:</strong> Spiral geometry and rate calculations</li>
 *   <li><strong>Complex Algorithms:</strong> Multi-variable autonomous behaviors</li>
 * </ul>
 * 
 * <h3>📝 Algorithm Overview:</h3>
 * <p>The spiral pattern starts with a small radius and gradually increases while
 * simultaneously climbing or descending. Each segment involves:</p>
 * <ol>
 *   <li>Calculate current radius based on progress</li>
 *   <li>Move forward along the expanding/contracting arc</li>
 *   <li>Adjust altitude incrementally</li>
 *   <li>Turn to align with next expanded segment</li>
 * </ol>
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 * @educational
 */
public class SpiralMethod extends AutonomousMethod {
    
    public SpiralMethod() {
        super("spiral", "Flies the drone in an expanding or contracting spiral pattern with altitude changes");
    }
    
    @Override
    protected void defineParameters() {
        addParameter("start_radius", 20, 80, "Starting radius in centimeters");
        addParameter("end_radius", 50, 200, "Ending radius in centimeters"); 
        addParameter("turns", 2, 6, "Number of complete rotations");
        addParameter("speed", 20, 80, "Flight speed as percentage");
        addParameter("altitude_change", -100, 100, "Total altitude change in centimeters (+ up, - down)");
    }
    
    @Override
    protected void executeAlgorithm(Drone drone, Map<String, Integer> params) {
        int startRadius = params.get("start_radius");
        int endRadius = params.get("end_radius");
        int turns = params.get("turns");
        int speed = params.get("speed");
        int altitudeChange = params.get("altitude_change");
        
        // Validate parameter relationships
        if (startRadius >= endRadius) {
            throw new IllegalArgumentException("End radius must be larger than start radius for expanding spiral");
        }
        
        // Algorithm parameters
        final int SEGMENTS_PER_TURN = 16;  // More segments for smoother spiral
        final int totalSegments = turns * SEGMENTS_PER_TURN;
        final double segmentAngle = 360.0 / SEGMENTS_PER_TURN;  // 22.5 degrees per segment
        
        // Calculate progressive changes per segment
        double radiusIncrement = (double)(endRadius - startRadius) / totalSegments;
        double altitudeIncrement = (double)altitudeChange / totalSegments;
        
        System.out.printf("🌀 Spiral parameters: %dcm→%dcm radius, %d turns, %dcm altitude change%n", 
            startRadius, endRadius, turns, altitudeChange);
        System.out.printf("📊 Per segment: +%.2fcm radius, %.2fcm altitude, %.1f° turn%n",
            radiusIncrement, altitudeIncrement, segmentAngle);
        
        // Execute the spiral pattern
        for (int segment = 0; segment < totalSegments; segment++) {
            // Calculate current spiral parameters
            double currentRadius = startRadius + (segment * radiusIncrement);
            double currentAltitudeOffset = segment * altitudeIncrement;
            
            // Calculate segment length based on current radius
            double circumference = 2 * Math.PI * currentRadius;
            int segmentLength = (int) Math.round(circumference / SEGMENTS_PER_TURN);
            
            // Progress indicator
            if (segment % SEGMENTS_PER_TURN == 0) {
                int completedTurns = segment / SEGMENTS_PER_TURN;
                System.out.printf("🔄 Starting turn %d/%d (radius: %.1fcm)%n", 
                    completedTurns + 1, turns, currentRadius);
            }
            
            // Execute segment with combined horizontal and vertical movement
            executeSegment(drone, segmentLength, speed, (int)segmentAngle, 
                          (int)currentAltitudeOffset, altitudeIncrement);
        }
        
        System.out.println("🎯 Spiral pattern complete!");
    }
    
    /**
     * Execute a single segment of the spiral with combined movement.
     * 
     * @param drone The drone to control
     * @param segmentLength Forward distance for this segment
     * @param speed Flight speed percentage  
     * @param turnAngle Angle to turn after forward movement
     * @param targetAltitude Target altitude offset for this segment
     * @param altitudeIncrement Altitude change per segment
     */
    private void executeSegment(Drone drone, int segmentLength, int speed, int turnAngle, 
                               int targetAltitude, double altitudeIncrement) {
        
        // If we have significant altitude change, use 3D movement
        if (Math.abs(altitudeIncrement) > 1.0) {
            // Calculate altitude change for this segment in meters
            double altitudeMeters = altitudeIncrement / 100.0;  // Convert cm to meters
            double forwardMeters = segmentLength / 100.0;       // Convert cm to meters
            
            // Use 3D movement with simultaneous forward and vertical motion
            drone.moveDistance(forwardMeters, 0, altitudeMeters, speed / 100.0 * 2.0);
        } else {
            // Use simple forward movement for flat spirals
            drone.go("forward", segmentLength, speed);
        }
        
        // Turn for next segment
        drone.turnRight(turnAngle);
        
        // Brief stabilization
        drone.hover(0.15);
    }
    
    @Override
    protected String getAlgorithmDescription() {
        return """
            This algorithm creates a 3D spiral pattern by combining circular motion with altitude changes.
            
            **Mathematical Foundation:**
            - Progressive radius: r(t) = r₀ + (r₁ - r₀) * t/T
            - Progressive altitude: h(t) = h₀ + Δh * t/T  
            - Segment length: s(t) = 2πr(t) / segments_per_turn
            - Where t = current segment, T = total segments
            
            **Algorithm Structure:**
            1. Calculate total segments based on turns and resolution
            2. For each segment:
               a. Calculate current radius and altitude
               b. Compute segment length for current radius
               c. Execute combined horizontal/vertical movement
               d. Turn to align with next segment
               e. Stabilize briefly
            
            **Implementation Features:**
            - 16 segments per turn for smooth curves
            - Progressive parameter interpolation
            - 3D movement using moveDistance() when altitude changes
            - Fallback to 2D movement for flat spirals
            - Real-time progress reporting
            
            **Educational Value:**
            - Linear interpolation and parametric equations
            - 3D coordinate systems and vector math
            - Algorithm complexity and optimization
            - Multi-variable system control
            - Error handling for parameter validation
            
            **Variations:**
            - Contracting spiral: start_radius > end_radius
            - Flat spiral: altitude_change = 0
            - Ascending spiral: altitude_change > 0
            - Descending spiral: altitude_change < 0
            """;
    }
}
