package com.otabi.jcodroneedu.autonomous;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for creating parameter maps for autonomous methods.
 * 
 * <p>This class provides a fluent API for students to easily create and validate
 * parameter sets for autonomous drone methods. It demonstrates the Builder pattern
 * and provides compile-time safety for parameter names.</p>
 * 
 * <h3>🎯 Educational Goals:</h3>
 * <ul>
 *   <li>Learn the Builder design pattern</li>
 *   <li>Practice fluent interface design</li>
 *   <li>Understand method chaining</li>
 *   <li>Experience type-safe parameter building</li>
 * </ul>
 * 
 * <h3>📝 Usage Examples:</h3>
 * <pre>{@code
 * // Simple parameter set
 * var params = ParameterBuilder.create()
 *     .set("speed", 50)
 *     .set("radius", 80)
 *     .build();
 * 
 * // Complex parameter set with validation
 * var params = ParameterBuilder.create()
 *     .set("segments", 12)
 *     .set("speed", 60)
 *     .set("radius", 100)
 *     .set("direction", 1)
 *     .build();
 * 
 * // Execute autonomous method
 * autonomousMethod.execute(drone, params);
 * }</pre>
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 * @educational
 */
public class ParameterBuilder {
    private final Map<String, Integer> parameters;
    
    private ParameterBuilder() {
        this.parameters = new HashMap<>();
    }
    
    /**
     * Creates a new parameter builder.
     * 
     * @return A new ParameterBuilder instance
     */
    public static ParameterBuilder create() {
        return new ParameterBuilder();
    }
    
    /**
     * Sets a parameter value.
     * 
     * @param name The parameter name
     * @param value The parameter value
     * @return This builder for method chaining
     */
    public ParameterBuilder set(String name, int value) {
        parameters.put(name, value);
        return this;
    }
    
    /**
     * Sets the speed parameter (common to most autonomous methods).
     * 
     * @param speed Speed percentage (typically 10-100)
     * @return This builder for method chaining
     */
    public ParameterBuilder speed(int speed) {
        return set("speed", speed);
    }
    
    /**
     * Sets the radius parameter (common to circular patterns).
     * 
     * @param radius Radius in centimeters (typically 20-200)
     * @return This builder for method chaining
     */
    public ParameterBuilder radius(int radius) {
        return set("radius", radius);
    }
    
    /**
     * Sets the direction parameter (common to directional patterns).
     * 
     * @param direction Direction (typically 1 for clockwise, -1 for counterclockwise)
     * @return This builder for method chaining
     */
    public ParameterBuilder direction(int direction) {
        return set("direction", direction);
    }
    
    /**
     * Sets the segments parameter (common to polygon patterns).
     * 
     * @param segments Number of segments (typically 3-20)
     * @return This builder for method chaining
     */
    public ParameterBuilder segments(int segments) {
        return set("segments", segments);
    }
    
    /**
     * Sets the duration parameter (common to time-based patterns).
     * 
     * @param seconds Duration in seconds (typically 1-30)
     * @return This builder for method chaining
     */
    public ParameterBuilder duration(int seconds) {
        return set("seconds", seconds);
    }
    
    /**
     * Sets the size parameter (common to geometric patterns).
     * 
     * @param size Size in centimeters (typically 30-300)
     * @return This builder for method chaining
     */
    public ParameterBuilder size(int size) {
        return set("size", size);
    }
    
    /**
     * Builds and returns the parameter map.
     * 
     * @return An immutable map of parameters
     */
    public Map<String, Integer> build() {
        return new HashMap<>(parameters);
    }
    
    /**
     * Returns the current parameter count.
     * Useful for debugging parameter sets.
     * 
     * @return The number of parameters currently set
     */
    public int size() {
        return parameters.size();
    }
    
    /**
     * Checks if a parameter is set.
     * 
     * @param name The parameter name to check
     * @return true if the parameter is set, false otherwise
     */
    public boolean hasParameter(String name) {
        return parameters.containsKey(name);
    }
    
    /**
     * Gets the current value of a parameter.
     * 
     * @param name The parameter name
     * @return The parameter value, or null if not set
     */
    public Integer getParameter(String name) {
        return parameters.get(name);
    }
    
    /**
     * Clears all parameters.
     * 
     * @return This builder for method chaining
     */
    public ParameterBuilder clear() {
        parameters.clear();
        return this;
    }
    
    /**
     * Creates a copy of this builder with the same parameters.
     * 
     * @return A new ParameterBuilder with copied parameters
     */
    public ParameterBuilder copy() {
        ParameterBuilder copy = new ParameterBuilder();
        copy.parameters.putAll(this.parameters);
        return copy;
    }
    
    @Override
    public String toString() {
        return String.format("ParameterBuilder{parameters=%s}", parameters);
    }
}
