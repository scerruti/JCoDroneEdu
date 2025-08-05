package com.otabi.jcodroneedu.autonomous;

import com.otabi.jcodroneedu.Drone;
import java.util.Map;
import java.util.HashMap;

/**
 * Abstract base class for autonomous drone methods.
 * 
 * <p>This class provides a framework for students to implement their own autonomous
 * behaviors with standardized parameter validation, execution flow, and error handling.
 * It demonstrates key educational concepts:</p>
 * 
 * <ul>
 *   <li><strong>Template Method Pattern:</strong> Defines the algorithm structure while allowing customization</li>
 *   <li><strong>Parameter Validation:</strong> Ensures safe operation with input bounds checking</li>
 *   <li><strong>Error Handling:</strong> Consistent exception handling across all autonomous methods</li>
 *   <li><strong>Documentation Standards:</strong> Forces students to document their algorithms</li>
 * </ul>
 * 
 * <h3>🎯 Educational Goals:</h3>
 * <ul>
 *   <li>Learn object-oriented design patterns (Template Method)</li>
 *   <li>Practice parameter validation and defensive programming</li>
 *   <li>Understand algorithm decomposition and modular design</li>
 *   <li>Develop consistent documentation habits</li>
 * </ul>
 * 
 * <h3>📝 Example Implementation:</h3>
 * <pre>{@code
 * public class CircleMethod extends AutonomousMethod {
 *     public CircleMethod() {
 *         super("circle", "Flies the drone in a circular pattern");
 *     }
 *     
 *     @Override
 *     protected void defineParameters() {
 *         addParameter("radius", 20, 150, "Circle radius in centimeters");
 *         addParameter("speed", 10, 100, "Flight speed as percentage");
 *         addParameter("direction", 1, 1, "Direction: 1=clockwise, -1=counterclockwise");
 *     }
 *     
 *     @Override
 *     protected void executeAlgorithm(Drone drone, Map<String, Integer> params) {
 *         int radius = params.get("radius");
 *         int speed = params.get("speed");
 *         int direction = params.get("direction");
 *         
 *         // Implementation of circle algorithm
 *         // ...
 *     }
 *     
 *     @Override
 *     protected String getAlgorithmDescription() {
 *         return "Uses polygon approximation with 12 segments...";
 *     }
 * }
 * }</pre>
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 * @educational
 */
public abstract class AutonomousMethod {
    
    private final String methodName;
    private final String description;
    private final Map<String, ParameterDefinition> parameters;
    
    /**
     * Parameter definition for validation and documentation.
     */
    public static class ParameterDefinition {
        public final int minValue;
        public final int maxValue;
        public final String description;
        
        public ParameterDefinition(int minValue, int maxValue, String description) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.description = description;
        }
    }
    
    /**
     * Creates a new autonomous method with a name and description.
     * 
     * @param methodName The name of this autonomous method
     * @param description A brief description of what this method does
     */
    protected AutonomousMethod(String methodName, String description) {
        this.methodName = methodName;
        this.description = description;
        this.parameters = new HashMap<>();
        defineParameters();
    }
    
    /**
     * Define the parameters this autonomous method accepts.
     * Subclasses must implement this to specify their required parameters.
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * @Override
     * protected void defineParameters() {
     *     addParameter("speed", 10, 100, "Flight speed percentage");
     *     addParameter("radius", 30, 200, "Pattern radius in cm");
     * }
     * }</pre>
     */
    protected abstract void defineParameters();
    
    /**
     * Execute the autonomous algorithm.
     * This is where students implement their specific flight logic.
     * 
     * @param drone The drone instance to control
     * @param params Validated parameters for the algorithm
     */
    protected abstract void executeAlgorithm(Drone drone, Map<String, Integer> params);
    
    /**
     * Provide a detailed description of the algorithm.
     * Students should explain their approach, any mathematical concepts,
     * and key implementation details.
     * 
     * @return Detailed algorithm description for educational documentation
     */
    protected abstract String getAlgorithmDescription();
    
    /**
     * Add a parameter definition for validation.
     * 
     * @param name Parameter name
     * @param minValue Minimum allowed value (inclusive)
     * @param maxValue Maximum allowed value (inclusive)
     * @param description Human-readable description
     */
    protected final void addParameter(String name, int minValue, int maxValue, String description) {
        parameters.put(name, new ParameterDefinition(minValue, maxValue, description));
    }
    
    /**
     * Execute this autonomous method with the given parameters.
     * This is the main entry point that handles validation and execution.
     * 
     * @param drone The drone instance to control
     * @param params Parameter values to use
     * @throws IllegalArgumentException if any parameter is invalid
     * @throws IllegalStateException if the drone is not ready for autonomous flight
     */
    public final void execute(Drone drone, Map<String, Integer> params) {
        // Pre-execution validation
        validateDroneState(drone);
        validateParameters(params);
        
        try {
            // Log execution start for educational debugging
            System.out.printf("🤖 Starting autonomous method: %s%n", methodName);
            
            // Execute the algorithm
            executeAlgorithm(drone, params);
            
            // Ensure drone is stable after execution
            drone.hover(0.5);
            
            System.out.printf("✅ Completed autonomous method: %s%n", methodName);
            
        } catch (Exception e) {
            System.err.printf("❌ Error in autonomous method %s: %s%n", methodName, e.getMessage());
            
            // Emergency stabilization
            try {
                drone.hover(1.0);
            } catch (Exception stabilizeError) {
                System.err.println("⚠️ Warning: Could not stabilize drone after error");
            }
            
            throw new RuntimeException("Autonomous method failed: " + methodName, e);
        }
    }
    
    /**
     * Validate that the drone is ready for autonomous operation.
     */
    private void validateDroneState(Drone drone) {
        if (!drone.isConnected()) {
            throw new IllegalStateException("Drone must be connected before executing autonomous methods");
        }
        
        // Additional state checks could be added here:
        // - Battery level check
        // - Flight state verification
        // - Sensor calibration status
    }
    
    /**
     * Validate that all parameters are within acceptable ranges.
     */
    private void validateParameters(Map<String, Integer> params) {
        // Check that all required parameters are provided
        for (String requiredParam : parameters.keySet()) {
            if (!params.containsKey(requiredParam)) {
                throw new IllegalArgumentException(
                    String.format("Missing required parameter: %s", requiredParam));
            }
        }
        
        // Check that all parameters are within valid ranges
        for (Map.Entry<String, Integer> param : params.entrySet()) {
            String name = param.getKey();
            Integer value = param.getValue();
            
            if (!parameters.containsKey(name)) {
                throw new IllegalArgumentException(
                    String.format("Unknown parameter: %s", name));
            }
            
            ParameterDefinition def = parameters.get(name);
            if (value < def.minValue || value > def.maxValue) {
                throw new IllegalArgumentException(
                    String.format("Parameter %s must be between %d and %d, got: %d", 
                        name, def.minValue, def.maxValue, value));
            }
        }
    }
    
    /**
     * Get the name of this autonomous method.
     */
    public final String getMethodName() {
        return methodName;
    }
    
    /**
     * Get the description of this autonomous method.
     */
    public final String getDescription() {
        return description;
    }
    
    /**
     * Get parameter definitions for documentation and UI generation.
     */
    public final Map<String, ParameterDefinition> getParameterDefinitions() {
        return new HashMap<>(parameters);
    }
    
    /**
     * Generate comprehensive documentation for this autonomous method.
     * Useful for auto-generating student reference materials.
     */
    public final String generateDocumentation() {
        StringBuilder doc = new StringBuilder();
        
        doc.append(String.format("# %s%n%n", methodName));
        doc.append(String.format("**Description:** %s%n%n", description));
        
        doc.append("## Algorithm%n");
        doc.append(getAlgorithmDescription()).append("%n%n");
        
        doc.append("## Parameters%n");
        if (parameters.isEmpty()) {
            doc.append("This method takes no parameters.%n");
        } else {
            for (Map.Entry<String, ParameterDefinition> entry : parameters.entrySet()) {
                ParameterDefinition def = entry.getValue();
                doc.append(String.format("- **%s** (%d - %d): %s%n", 
                    entry.getKey(), def.minValue, def.maxValue, def.description));
            }
        }
        
        return doc.toString();
    }
}
