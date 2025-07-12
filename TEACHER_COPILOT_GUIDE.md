# 🤖 Teacher's Guide: Using GitHub Copilot for Test Creation

Welcome to AI-assisted test creation! This guide will help you use GitHub Copilot to create effective unit tests for validating student drone programming assignments.

## 🎯 What is GitHub Copilot?

GitHub Copilot is an AI programming assistant that helps you write code by suggesting completions based on your comments and existing code. Think of it as an intelligent autocomplete that understands programming patterns and can generate entire functions.

## 🚀 Getting Started

### Prerequisites
- VS Code with GitHub Copilot extension installed
- Basic understanding of Java and JUnit testing
- Familiarity with the CoDrone EDU API

### Key Concepts
- **Prompt Engineering**: Writing clear, specific comments that guide Copilot
- **Context Awareness**: Copilot learns from your existing code and patterns
- **Iterative Refinement**: Copilot suggestions improve as you provide more context

## 📝 Example: Creating a Square Flight Test

Let's walk through creating a test that validates whether students correctly implement a square flight pattern for the L0102 assignment.

### Step 1: Start with a Clear Prompt

```java
/**
 * Test that validates a student's implementation of a square flight pattern.
 * Requirements:
 * - Student must use the new go() method (v2.3)
 * - Must make exactly 4 movements to form a square
 * - Each movement should be forward, then turn right
 * - Should use consistent distance for all sides
 * - Must include takeoff and landing
 */
@Test
public void testSquareFlightPattern() {
    // TODO: Copilot will help implement this
}
```

**Why this prompt works:**
- 🎯 **Specific Requirements**: Lists exactly what the test should validate
- 📚 **Educational Context**: References the assignment (L0102) and version (v2.3)
- 🔧 **Implementation Details**: Specifies the go() method requirement
- 🧪 **Test Scope**: Clear about what constitutes success/failure

### Step 2: Let Copilot Generate the Implementation

After writing the prompt above, place your cursor after the `// TODO` comment and press `Tab` to accept Copilot's suggestions. You might get something like:

```java
@Test
public void testSquareFlightPattern() {
    // Execute student's drone operations
    executeStudentDroneOperations();
    
    // Verify the new go() method was used
    assertTrue(mockDrone.wasGoMethodUsed(),
        "💡 Try using the new go() method instead of setPitch/setRoll: go(\"forward\", 50)");
    
    // Check that exactly 4 movements were made for the square
    List<String> movements = mockDrone.getGoMethodCalls();
    assertEquals(4, movements.size(),
        "❌ A square needs exactly 4 movements. Found: " + movements.size());
    
    // Verify consistent distances (all sides equal)
    Set<String> uniqueDistances = movements.stream()
        .map(move -> move.split(",")[1].trim()) // Extract distance
        .collect(Collectors.toSet());
    assertEquals(1, uniqueDistances.size(),
        "🔧 All sides of a square should be the same length!");
    
    // Confirm takeoff and landing were called
    assertTrue(mockDrone.wasTakeoffCalled(),
        "🚁 Don't forget to takeoff before flying!");
    assertTrue(mockDrone.wasLandCalled(),
        "🛬 Always land your drone safely!");
}
```

### Step 3: Refine and Customize

Review Copilot's suggestion and adjust based on your specific needs:

```java
@Test
public void testSquareFlightPatternAdvanced() {
    // Set up test parameters
    int expectedDistance = 50; // cm
    int expectedMovements = 4;
    
    // Execute student's implementation
    executeStudentDroneOperations();
    
    // Validate flight pattern structure
    List<String> movements = mockDrone.getGoMethodCalls();
    assertEquals(expectedMovements, movements.size(),
        String.format("🔳 Expected %d movements for square, but found %d", 
                     expectedMovements, movements.size()));
    
    // Check movement pattern: forward, turn, forward, turn...
    for (int i = 0; i < movements.size(); i++) {
        String movement = movements.get(i);
        if (i % 2 == 0) {
            // Even indices should be forward movements
            assertTrue(movement.contains("forward"),
                String.format("🔄 Movement %d should be forward, but was: %s", i + 1, movement));
        } else {
            // Odd indices should be turns (if implementing turns)
            assertTrue(movement.contains("right") || movement.contains("turn"),
                String.format("↩️ Movement %d should be a right turn, but was: %s", i + 1, movement));
        }
    }
    
    // Validate safety practices
    assertTrue(mockDrone.wasTakeoffCalled(),
        "⚠️ Safety first: Always takeoff before attempting flight patterns!");
    assertTrue(mockDrone.wasLandCalled(),
        "✅ Good practice: Always land to complete your flight sequence!");
}
```

## 💡 Best Practices for AI-Assisted Test Creation

### 1. Write Context-Rich Comments
```java
// BAD: Test student code
// GOOD: Test that student uses obstacle avoidance when front sensor detects object within 30cm
```

### 2. Use Educational Language in Assertions
```java
// BAD: assertEquals(4, count);
// GOOD: assertEquals(4, movements.size(), 
//       "🔳 A square has 4 sides - check your loop count!");
```

### 3. Leverage Existing Patterns
Copilot learns from your codebase. If you have one well-written test, it will suggest similar patterns for new tests.

### 4. Break Down Complex Tests
```java
/**
 * Test obstacle avoidance behavior
 * 1. Drone should detect obstacle using front range sensor
 * 2. Should stop forward movement when object < 30cm
 * 3. Should attempt alternative path or hover
 * 4. Should not crash into obstacle
 */
```

### 5. Provide Student-Friendly Error Messages
```java
// Include emojis and helpful tips in assertion messages
assertFalse(mockDrone.wasCollisionDetected(),
    "🚫 Collision detected! Try using get_front_range() to avoid obstacles");
```

## 🔧 Advanced Copilot Techniques

### Using Copilot Chat for Test Planning
Open Copilot Chat (Ctrl+Shift+I) and ask:
```
"Help me create a test plan for validating student drone assignments. 
The assignment requires students to:
1. Connect to drone using pair() method
2. Fly in a triangle pattern
3. Use obstacle avoidance
4. Land safely

What edge cases should I test?"
```

### Generating Test Data
```java
// Comment: Generate test data for various flight patterns
// Copilot can suggest comprehensive test data sets
```

### Creating Parameterized Tests
```java
/**
 * Test various flight patterns with different parameters
 * Should test: square, triangle, circle with different sizes
 */
@ParameterizedTest
@ValueSource(strings = {"square,50", "triangle,30", "circle,40"})
public void testFlightPatterns(String patternAndSize) {
    // Copilot will suggest the implementation
}
```

## ⚠️ Important Considerations

### What Copilot Does Well
- ✅ Generates boilerplate test structure
- ✅ Suggests assertion patterns
- ✅ Creates realistic test data
- ✅ Follows coding conventions

### What Requires Human Judgment
- 🧠 **Educational Objectives**: Ensure tests align with learning goals
- 🎯 **Assignment Requirements**: Verify tests match your specific criteria
- 👨‍🎓 **Student Experience**: Craft helpful, encouraging error messages
- 🔍 **Edge Cases**: Consider scenarios Copilot might miss

### Common Pitfalls
1. **Over-reliance**: Always review and understand generated code
2. **Generic Messages**: Customize error messages for your students
3. **Missing Context**: Provide enough background for accurate suggestions
4. **Test Coverage**: Ensure important edge cases aren't overlooked

## 📚 Sample Prompts for Different Test Types

### Connection Testing
```java
/**
 * Test student's drone connection handling
 * Should verify proper exception handling when drone not found
 * Should test both pair() and connect() methods
 * Should validate resource cleanup with try-with-resources
 */
```

### Safety Testing
```java
/**
 * Critical safety test - emergency stop functionality
 * Must verify emergency_stop() immediately halts all movement
 * Should test emergency stop during various flight operations
 * Student safety is paramount - this test must never fail
 */
```

### Sensor Integration
```java
/**
 * Test sensor-based decision making
 * Student should use get_front_range() for obstacle detection
 * Should demonstrate conditional logic based on sensor readings
 * Must handle sensor data within realistic range values (0-400cm)
 */
```

## 🎓 Teaching Tips

1. **Start Simple**: Begin with basic tests and gradually add complexity
2. **Explain the AI**: Help students understand how Copilot assists (but doesn't replace) programming knowledge
3. **Review Together**: Use generated tests as teaching moments
4. **Encourage Experimentation**: Let students see how different prompts yield different results
5. **Emphasize Understanding**: Generated code should be understood, not just accepted

## 🚀 Next Steps

1. Try creating a simple test using the example above
2. Experiment with different prompt styles
3. Share successful prompts with other teachers
4. Iterate and improve based on student outcomes

Remember: GitHub Copilot is a powerful assistant, but you're the teacher. Use it to enhance your test creation workflow while maintaining focus on educational objectives and student success.

---

**Need Help?** 
- Check the GitHub Copilot documentation
- Experiment with different prompting techniques
- Review successful test patterns in the existing codebase
- Ask specific questions in Copilot Chat for targeted assistance
