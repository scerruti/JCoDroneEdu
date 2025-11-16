---
layout: guide
title: Contributing
category: Architecture
permalink: /architecture/design-guide/contributing.html
---

## Contributing to JCoDroneEdu

Welcome! This guide explains how to contribute code, documentation, or ideas to the project.

---

## Getting Started

### 1. Fork the Repository

Visit [GitHub repository](https://github.com/JCoDroneEdu/library)
- Click "Fork" button
- Creates your own copy

### 2. Clone Your Fork

```bash
git clone https://github.com/YOUR_USERNAME/library.git
cd library
```

### 3. Set Upstream

```bash
git remote add upstream https://github.com/JCoDroneEdu/library.git
git fetch upstream
```

### 4. Create Feature Branch

```bash
git checkout -b feature/add-circle-command
# Branch naming: feature/*, bugfix/*, docs/*
```

---

## Contribution Types

### Type 1: Bug Fixes

**Process:**
1. Create branch: `bugfix/drone-crash-on-disconnect`
2. Fix the bug
3. Add tests that verify the fix
4. Submit pull request

**Checklist:**
- [ ] Bug reproduction documented
- [ ] Root cause identified
- [ ] Fix tested
- [ ] No new bugs introduced
- [ ] Backwards compatible

### Type 2: New Features

**Process:**
1. Discuss in Issues first (get approval)
2. Create branch: `feature/altitude-hold`
3. Implement feature
4. Write tests
5. Write documentation
6. Submit pull request

**Checklist:**
- [ ] Feature follows design patterns
- [ ] Tests provided
- [ ] Documentation updated
- [ ] Examples work
- [ ] Performance acceptable
- [ ] Backwards compatible

### Type 3: Documentation

**Process:**
1. Create branch: `docs/add-glossary`
2. Update markdown files
3. Test links and formatting
4. Submit pull request

**No code review needed for docs.**

---

## Code Style

### Naming Conventions

```java
// Classes: PascalCase
public class DroneConnection { }

// Methods: camelCase
public void moveForward(double speed) { }

// Constants: UPPER_SNAKE_CASE
private static final int MAX_RETRIES = 3;

// Variables: camelCase
int batteryLevel = 95;
```

### Formatting

**Use Google Java Style:**
- 4-space indentation
- 100-character line limit
- One statement per line
- Spaces around operators

```java
// Good
if (battery > 50 && !isFlying) {
    drone.takeoff();
}

// Avoid
if(battery>50&&!isFlying){drone.takeoff();}
```

### Documentation

```java
/**
 * Brief description of what method does.
 * 
 * More detailed explanation if needed, including
 * important implementation notes or side effects.
 * 
 * @param paramName description of parameter
 * @return description of return value
 * @throws ExceptionType description of when thrown
 * @see #relatedMethod()
 * 
 * @example
 * <pre>
 * // Show how to use this method
 * int battery = drone.getBattery();
 * </pre>
 */
public int getBattery() throws IOException { ... }
```

---

## Testing Requirements

### New Features

```
Code coverage must be >= 85%

Must include:
- Unit tests for normal cases
- Unit tests for error cases
- Integration tests with real object
```

### Example PR Checklist

```
- [x] Unit tests pass
- [x] Integration tests pass
- [x] Code coverage > 85%
- [x] No checkstyle violations
- [x] Documentation updated
- [x] Examples work
```

---

## Pull Request Process

### Before Submitting

```bash
# Update your branch
git fetch upstream
git rebase upstream/main

# Run all tests
mvn clean test

# Check formatting
mvn checkstyle:check

# Generate coverage
mvn jacoco:report
# open target/site/jacoco/index.html
```

### Create Pull Request

1. **Title:** Clear, concise description
   - "Add circle movement command"
   - "Fix battery timeout issue"
   - "Document deployment process"

2. **Description:** Include:
   - What problem does this solve?
   - How does it work?
   - Any breaking changes?
   - Testing performed
   - Screenshots/videos if applicable

3. **Example PR Description:**

```markdown
## Problem
Drone couldn't fly in circle patterns, limiting patrol capabilities.

## Solution
Added `moveCircle()` method to support circular flight paths.

## Changes
- Added `moveCircle(radius, speed, direction)` to Drone API
- Implemented 0x88 protocol support for circle command
- Added comprehensive tests (90+ lines)
- Updated student API reference

## Testing
- Unit tests pass (10 new tests)
- Integration test on real hardware: passes
- Backwards compatible: yes

## Documentation
- Added to API reference guide
- Added code examples
- Added to student flight patterns guide

## Checklist
- [x] Tests added/updated
- [x] Documentation updated
- [x] Backwards compatible
- [x] Code formatting checked
- [x] Coverage > 85%

## Closes
#123 - Student request for circular patrol
```

---

## Code Review

### What Reviewers Look For

1. **Correctness**
   - Does code do what it's supposed to?
   - Any edge cases missed?
   - Error handling adequate?

2. **Design**
   - Follows project patterns?
   - API consistent with existing code?
   - Over-engineered or too simple?

3. **Performance**
   - Any efficiency issues?
   - Memory leaks possible?
   - Acceptable latency?

4. **Testing**
   - Test coverage sufficient?
   - Tests verify correct behavior?
   - Edge cases tested?

5. **Documentation**
   - Code clear without comments?
   - Public API documented?
   - Complex logic explained?

### Responding to Feedback

**Be collaborative:**
- Ask clarifying questions
- Explain your reasoning
- Discuss trade-offs
- Accept suggestions gracefully

**Example:**
```
Reviewer: "Why use synchronized block here?"

You: "Good catch! Actually, I don't think we need it 
because each Drone instance owns its thread. Let me 
remove that and add a comment explaining single-threaded 
design."

[Update code and push new commit]
```

---

## Release Process

### Version Numbering

Use Semantic Versioning: `MAJOR.MINOR.PATCH`

```
1.0.0  - Initial release
1.1.0  - New feature (backwards compatible)
1.1.1  - Bug fix (backwards compatible)
2.0.0  - Breaking changes
```

### Release Checklist

- [ ] All tests passing
- [ ] Coverage > 85%
- [ ] Documentation updated
- [ ] CHANGELOG updated
- [ ] Version number bumped
- [ ] Release notes written
- [ ] Tag created on GitHub
- [ ] Artifacts published to Maven Central

---

## Recognition

### Credits

Contributors listed in:
- `CONTRIBUTORS.md` - All contributors
- `CHANGELOG.md` - Per-release credits
- GitHub - Automatic contributor graph

### Examples of Contributions

We appreciate:
- Bug reports with reproduction steps
- Feature requests with use cases
- Code improvements
- Documentation enhancements
- Example programs
- Educational resources
- Translation work
- Classroom feedback

---

## Communication

### Where to Ask

**GitHub Issues:** Bug reports, feature requests
**Discussions:** General questions, ideas
**Email:** Contact maintainers for security issues
**Discord/Slack:** Real-time chat (if community grows)

### Code of Conduct

We are committed to providing a welcoming, inclusive environment.

**Be respectful:**
- Different experience levels
- Different perspectives
- Different time zones and languages
- Constructive feedback only

**Respect diversity:**
- No discrimination
- No harassment
- No hate speech

---

## Development Setup

### Prerequisites

```bash
# Java
java -version          # Should be 11+

# Maven
mvn -version          # Should be 3.6+

# Git
git --version         # Should be 2.0+
```

### IDE Setup

**IntelliJ IDEA:**
1. File → Open → Select library directory
2. Agree to enable Maven support
3. Wait for indexing to complete
4. Run → Run Tests

**Eclipse:**
1. File → Import → Existing Maven Projects
2. Select library directory
3. Finish import

**VS Code:**
1. Install Extension Pack for Java
2. File → Open Folder → Select library directory
3. Let extensions initialize

### Build Commands

```bash
# Clean build
mvn clean install

# Run tests only
mvn test

# Build with coverage
mvn clean test jacoco:report

# Run single test
mvn test -Dtest=DroneTest#testMoveForward

# Build without tests
mvn clean install -DskipTests
```

---

## Troubleshooting

### Issue: "I don't know where to start"

**Solution:**
1. Look for `good first issue` label in GitHub Issues
2. Start with documentation improvements
3. Fix a simple bug
4. Ask for mentorship in Discussions

### Issue: "My PR is taking too long to review"

**Possible reasons:**
- Large change (break into smaller PRs)
- Incomplete information (add more context)
- Requires domain expertise (mention specific reviewers)

**What to do:**
- Bump with comment after 1 week
- Ask maintainers directly
- Offer to help with other reviews

### Issue: "I disagree with feedback"

**Approach:**
1. Ask for clarification
2. Explain your perspective
3. Suggest alternatives
4. Let maintainers make final decision
5. Accept gracefully and learn

---

## Resources

- [GitHub Flow Guide](https://guides.github.com/introduction/flow/)
- [Semantic Versioning](https://semver.org/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Javadoc Documentation](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)

---

## Questions?

Create a Discussion or reach out to maintainers. We're here to help!

---

**Thank you for contributing to JCoDroneEdu! 🚁**
