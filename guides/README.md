# CoDrone EDU Documentation Guides

This directory contains educational guides for CoDrone EDU users.

## Structure

```
guides/
└── student/
    ├── index.md                    - Navigation page for student guides
    └── sensor-monitoring.md        - Monitoring tools guide
```

## Student Guides

The `student/` directory contains guides written for students learning to program with CoDrone EDU. These guides are designed for high school students, particularly those in AP Computer Science A or similar courses.

### Available Guides

1. **[Monitoring Tools: Sensors and Controller Input](student/sensor-monitoring.md)**
   - Comprehensive guide to monitoring drone sensors and controller input
   - Covers SensorDisplayGui, ControllerInputGui, and BothMonitors
   - Includes easy one-line monitors and custom GUI components
   - Perfect for debugging and learning

## Main Documentation

For the complete API reference and tutorials, see:
- **[Student Guide](../student-guide.md)** - Main guide for students
- **[Teacher Guide](../teacher-guide.md)** - Resources for educators

## Contributing

To add new guides:
1. Create your markdown file in the appropriate subdirectory
2. Update the index.md file to link to it
3. Consider adding a reference in the main student-guide.md
4. Follow the existing documentation style and tone

## Documentation Standards

- Write for high school students (grades 9-12)
- Use clear, simple language
- Include code examples
- Provide multiple usage patterns (simple → advanced)
- Add comparison tables for decision-making
- Include gradle commands and file paths
- Test all code examples before documenting
