# Autonomous Method Framework

A comprehensive framework for creating, managing, and executing autonomous drone behaviors in the CoDrone EDU Java library.

## 🎯 Educational Goals

This framework teaches students:

- **Object-Oriented Design:** Abstract classes, inheritance, and polymorphism
- **Design Patterns:** Template Method, Builder, Registry, and Singleton patterns
- **Parameter Validation:** Defensive programming and input validation
- **Algorithm Design:** Breaking complex behaviors into manageable components
- **Documentation:** Self-documenting code and auto-generated documentation

## 📁 Framework Components

### Core Classes

- **`AutonomousMethod`** - Abstract base class for all autonomous behaviors
- **`ParameterBuilder`** - Fluent API for building parameter sets
- **`AutonomousMethodRegistry`** - Central registry for method discovery and management

### Example Implementations

- **`AvoidWallAutonomousMethod`** - Maintains a minimum distance from a wall using the front range sensor
- **`KeepDistanceAutonomousMethod`** - Maintains a set distance from an object using the front range sensor
- **`AutonomousMethodDemo`** - Comprehensive usage examples

## 🚀 Quick Start

### 1. Basic Usage

```java
// Get the registry and a method
AutonomousMethodRegistry registry = AutonomousMethodRegistry.getInstance();
AutonomousMethod avoidWall = registry.getMethod("avoidWall");

// Build parameters
var params = ParameterBuilder.create()
    .timeout(10)
    .distance(50)
    .build();

// Execute with drone
try (Drone drone = new Drone()) {
    drone.connect();
    drone.takeoff();
    
    avoidWall.execute(drone, params);
    
    drone.land();
}
```

### 2. Creating Custom Methods

```java
public class SquareMethod extends AutonomousMethod {
    public SquareMethod() {
        super("square", "Flies the drone in a square pattern");
    }
    
    @Override
    protected void defineParameters() {
        addParameter("size", 50, 300, "Square side length in centimeters");
        addParameter("speed", 20, 100, "Flight speed as percentage");
    }
    
    @Override
    protected void executeAlgorithm(Drone drone, Map<String, Integer> params) {
        int size = params.get("size");
        int speed = params.get("speed");
        
        for (int side = 0; side < 4; side++) {
            drone.moveForward(size, "cm", speed / 100.0 * 2.0);
            drone.turnRight(90);
            drone.hover(0.5);
        }
    }
    
    @Override
    protected String getAlgorithmDescription() {
        return "Flies a square pattern using precise distance-based movement...";
    }
}
```

### 3. Register and Use Custom Methods

```java
// Register your custom method
AutonomousMethodRegistry registry = AutonomousMethodRegistry.getInstance();
registry.register(new MyCustomAutonomousMethod());

// Use it like any built-in method
AutonomousMethod custom = registry.getMethod("myCustomMethod");
var params = ParameterBuilder.create()
    .myParam(42)
    .build();
    
custom.execute(drone, params);
```

## 📚 Built-in Methods

### Avoid Wall
Maintains a minimum distance from a wall using the front range sensor and proportional control.

**Parameters:**
- `timeout` (1-30): Duration in seconds to run the avoidance loop
- `distance` (10-100): Target distance from wall in cm

### Keep Distance
Maintains a set distance from an object using the front range sensor.

**Parameters:**
- `timeout` (1-30): Duration in seconds to run the distance-keeping loop
- `distance` (10-100): Target distance from object in cm

## 🔧 Advanced Features

### Parameter Validation
All parameters are automatically validated against defined ranges:

```java
// This will throw an exception at runtime
var invalidParams = ParameterBuilder.create()
    .radius(300)  // Outside valid range (20-200)
    .build();
```

### Documentation Generation
The framework can auto-generate comprehensive documentation:

```java
AutonomousMethodRegistry registry = AutonomousMethodRegistry.getInstance();

// Generate usage guide
String usageGuide = registry.generateUsageGuide();

// Generate full documentation
String fullDocs = registry.generateDocumentation();

// Generate individual method docs
AutonomousMethod method = registry.getMethod("circle");
String methodDocs = method.generateDocumentation();
```

### Error Handling
Robust error handling with educational feedback:

- Parameter validation with clear error messages
- Drone state verification before execution
- Automatic stabilization after errors
- Detailed logging for debugging

## 🎓 Educational Progression

### Level 1: Using Built-in Methods
Students learn to:
- Use the registry to discover methods
- Build parameters with validation
- Execute autonomous behaviors safely

### Level 2: Creating Simple Methods  
Students implement:
- Basic geometric patterns (square, triangle)
- Parameter validation
- Algorithm documentation

### Level 3: Advanced Algorithms
Students develop:
- Multi-axis movement patterns
- Progressive parameter interpolation
- Complex mathematical models
- Optimization techniques

## 📖 Course Integration

### L0201: Introduction to Autonomous Methods
- Framework overview and concepts
- Using built-in methods
- Parameter building and validation

### L0202: Creating Custom Methods
- Extending AutonomousMethod
- Parameter definition
- Algorithm implementation

### L0203: Advanced Patterns
- 3D movement algorithms
- Mathematical modeling
- Performance optimization

### L0204: Framework Design
- Design patterns in the framework
- Registry and discovery patterns
- Plugin architecture concepts

## 🔍 Example Programs

Run the demonstration program to see the framework in action:

```bash
# Compile and run the demo
./gradlew run -PmainClass=com.otabi.jcodroneedu.autonomous.examples.AutonomousMethodDemo
```

This will demonstrate:
- Registry usage and method discovery
- Parameter building with validation
- Autonomous method execution (with connected drone)
- Documentation generation

## 🛠️ Best Practices

### For Students
1. **Start Simple:** Begin with basic geometric patterns
2. **Validate Early:** Define parameter ranges carefully
3. **Document Thoroughly:** Explain your algorithm clearly
4. **Test Incrementally:** Start with small parameter values
5. **Handle Errors:** Consider edge cases and error conditions

### For Educators
1. **Progressive Complexity:** Introduce concepts gradually
2. **Real-World Examples:** Connect algorithms to practical applications
3. **Peer Review:** Have students review each other's methods
4. **Performance Analysis:** Discuss algorithm efficiency and optimization
5. **Safety First:** Emphasize parameter validation and error handling

## 🔧 Extending the Framework

The framework is designed to be extensible:

- **Custom Parameter Types:** Add support for floating-point or string parameters
- **Visualization:** Integrate with flight path visualization tools
- **Simulation:** Add simulation mode for testing algorithms
- **Persistence:** Save and load autonomous method configurations
- **Remote Execution:** Execute methods from web interfaces or mobile apps

## 📋 Requirements

- Java 11 or higher
- CoDrone EDU Java library
- Connected CoDrone EDU controller (for flight execution)

## 🤝 Contributing

Students and educators are encouraged to contribute new autonomous methods:

1. Implement the `AutonomousMethod` interface
2. Add comprehensive documentation
3. Include parameter validation
4. Provide usage examples
5. Submit for inclusion in the framework

---

*This framework is part of the CoDrone EDU educational initiative, designed to teach programming concepts through hands-on drone programming.*
