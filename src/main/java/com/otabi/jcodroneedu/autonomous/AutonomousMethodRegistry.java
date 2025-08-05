package com.otabi.jcodroneedu.autonomous;

import com.otabi.jcodroneedu.autonomous.examples.AvoidWallAutonomousMethod;
import com.otabi.jcodroneedu.autonomous.examples.KeepDistanceAutonomousMethod;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry for managing and discovering autonomous drone methods.
 * 
 * <p>This class provides centralized management of all available autonomous
 * methods, making it easy for students and educators to discover, document,
 * and execute autonomous behaviors. It demonstrates the Registry pattern
 * and provides a foundation for plugin-like autonomous method systems.</p>
 * 
 * <h3>🎯 Educational Goals:</h3>
 * <ul>
 *   <li>Learn the Registry design pattern</li>
 *   <li>Understand service discovery concepts</li>
 *   <li>Practice factory method patterns</li>
 *   <li>Experience plugin architecture design</li>
 * </ul>
 * 
 * <h3>📝 Usage Examples:</h3>
 * <pre>{@code
 * // List all available methods
 * AutonomousMethodRegistry registry = AutonomousMethodRegistry.getInstance();
 * List<String> methods = registry.listMethods();
 * System.out.println("Available methods: " + methods);
 * 
 * // Get a specific method
 * AutonomousMethod avoidWall = registry.getMethod("avoidWall");
 * 
 * // Execute with parameters
 * var params = new HashMap<String, Integer>();
 * params.put("timeout", 5);
 * params.put("distance", 50);
 * avoidWall.execute(drone, params);
 * 
 * // Generate documentation
 * String docs = registry.generateDocumentation();
 * System.out.println(docs);
 * }</pre>
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 * @educational
 */
public class AutonomousMethodRegistry {
    
    private static AutonomousMethodRegistry instance;
    private final Map<String, AutonomousMethod> methods;
    
    /**
     * Private constructor for singleton pattern.
     */
    private AutonomousMethodRegistry() {
        this.methods = new HashMap<>();
        registerBuiltInMethods();
    }
    
    /**
     * Get the singleton instance of the registry.
     * 
     * @return The registry instance
     */
    public static synchronized AutonomousMethodRegistry getInstance() {
        if (instance == null) {
            instance = new AutonomousMethodRegistry();
        }
        return instance;
    }
    
    /**
     * Register built-in autonomous methods that come with the framework.
     */
    private void registerBuiltInMethods() {
        register(new AvoidWallAutonomousMethod());
        register(new KeepDistanceAutonomousMethod());
        // Only register true autonomous (threaded, sensor-driven) methods as built-ins
    }
    
    /**
     * Register an autonomous method in the registry.
     * 
     * @param method The autonomous method to register
     * @throws IllegalArgumentException if a method with the same name already exists
     */
    public void register(AutonomousMethod method) {
        String name = method.getMethodName();
        
        if (methods.containsKey(name)) {
            throw new IllegalArgumentException(
                String.format("Method '%s' is already registered", name));
        }
        
        methods.put(name, method);
        System.out.printf("📝 Registered autonomous method: %s%n", name);
    }
    
    /**
     * Get an autonomous method by name.
     * 
     * @param methodName The name of the method to retrieve
     * @return The autonomous method
     * @throws IllegalArgumentException if the method is not found
     */
    public AutonomousMethod getMethod(String methodName) {
        AutonomousMethod method = methods.get(methodName);
        
        if (method == null) {
            throw new IllegalArgumentException(
                String.format("Unknown autonomous method: %s. Available methods: %s", 
                    methodName, String.join(", ", methods.keySet())));
        }
        
        return method;
    }
    
    /**
     * Check if a method is registered.
     * 
     * @param methodName The method name to check
     * @return true if the method exists, false otherwise
     */
    public boolean hasMethod(String methodName) {
        return methods.containsKey(methodName);
    }
    
    /**
     * Get a list of all registered method names.
     * 
     * @return Sorted list of method names
     */
    public List<String> listMethods() {
        return methods.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Get all registered methods.
     * 
     * @return Unmodifiable collection of all methods
     */
    public Collection<AutonomousMethod> getAllMethods() {
        return Collections.unmodifiableCollection(methods.values());
    }
    
    /**
     * Remove a method from the registry.
     * 
     * @param methodName The name of the method to remove
     * @return true if the method was removed, false if it didn't exist
     */
    public boolean unregister(String methodName) {
        AutonomousMethod removed = methods.remove(methodName);
        if (removed != null) {
            System.out.printf("🗑️ Unregistered autonomous method: %s%n", methodName);
            return true;
        }
        return false;
    }
    
    /**
     * Clear all registered methods (except built-ins).
     * This is primarily for testing purposes.
     */
    public void clearCustomMethods() {
        List<String> customMethods = methods.keySet().stream()
                .filter(name -> !isBuiltInMethod(name))
                .collect(Collectors.toList());
                
        for (String methodName : customMethods) {
            unregister(methodName);
        }
    }
    
    /**
     * Check if a method is a built-in method.
     * 
     * @param methodName The method name to check
     * @return true if it's a built-in method
     */
    private boolean isBuiltInMethod(String methodName) {
        return Set.of("avoidWall", "keepDistance").contains(methodName);
    }
    
    /**
     * Generate comprehensive documentation for all registered methods.
     * 
     * @return Formatted documentation string
     */
    public String generateDocumentation() {
        StringBuilder docs = new StringBuilder();
        
        docs.append("# Autonomous Drone Methods\n\n");
        docs.append("This document describes all available autonomous methods for the CoDrone EDU.\n\n");
        
        docs.append("## Quick Reference\n\n");
        docs.append("| Method | Description | Parameters |\n");
        docs.append("|--------|-------------|------------|\n");
        
        for (AutonomousMethod method : methods.values()) {
            String paramCount = String.valueOf(method.getParameterDefinitions().size());
            docs.append(String.format("| %s | %s | %s |\n", 
                method.getMethodName(), 
                method.getDescription(),
                paramCount));
        }
        
        docs.append("\n## Detailed Documentation\n\n");
        
        // Sort methods alphabetically for consistent documentation
        List<AutonomousMethod> sortedMethods = methods.values().stream()
                .sorted(Comparator.comparing(AutonomousMethod::getMethodName))
                .collect(Collectors.toList());
        
        for (AutonomousMethod method : sortedMethods) {
            docs.append(method.generateDocumentation()).append("\n");
        }
        
        return docs.toString();
    }
    
    /**
     * Generate a simple usage guide for educators.
     * 
     * @return Formatted usage guide
     */
    public String generateUsageGuide() {
        StringBuilder guide = new StringBuilder();
        
        guide.append("# Autonomous Methods Usage Guide\n\n");
        guide.append("## Basic Usage Pattern\n\n");
        guide.append("```java\n");
        guide.append("// 1. Get registry and method\n");
        guide.append("AutonomousMethodRegistry registry = AutonomousMethodRegistry.getInstance();\n");
        guide.append("AutonomousMethod method = registry.getMethod(\"avoidWall\");\n\n");
        guide.append("// 2. Build parameters\n");
        guide.append("var params = new HashMap<String, Integer>();\n");
        guide.append("params.put(\"timeout\", 5);\n");
        guide.append("params.put(\"distance\", 50);\n");
        guide.append("\n");
        guide.append("// 3. Execute with drone\n");
        guide.append("try (Drone drone = new Drone()) {\n");
        guide.append("    drone.connect();\n");
        guide.append("    drone.takeoff();\n");
        guide.append("    \n");
        guide.append("    method.execute(drone, params);\n");
        guide.append("    \n");
        guide.append("    drone.land();\n");
        guide.append("}\n");
        guide.append("```\n\n");
        
        guide.append("## Available Methods\n\n");
        for (String methodName : listMethods()) {
            AutonomousMethod method = getMethod(methodName);
            guide.append(String.format("### %s\n", methodName));
            guide.append(String.format("%s\n\n", method.getDescription()));
            
            guide.append("**Parameters:**\n");
            for (Map.Entry<String, AutonomousMethod.ParameterDefinition> entry : 
                 method.getParameterDefinitions().entrySet()) {
                String name = entry.getKey();
                AutonomousMethod.ParameterDefinition def = entry.getValue();
                guide.append(String.format("- `%s` (%d-%d): %s\n", 
                    name, def.minValue, def.maxValue, def.description));
            }
            guide.append("\n");
        }
        
        return guide.toString();
    }
    
    /**
     * Get the total number of registered methods.
     * 
     * @return The number of registered methods
     */
    public int getMethodCount() {
        return methods.size();
    }
    
    @Override
    public String toString() {
        return String.format("AutonomousMethodRegistry{methods=%d, names=%s}", 
            methods.size(), listMethods());
    }
}
