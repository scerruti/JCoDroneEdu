# CoDrone EDU Java API

A concise, classroom-focused Java library for programming the CoDrone EDU hardware. This repository contains the Java API (package `com.otabi.jcodroneedu`), example programs, documentation, and build tooling used to produce student and teacher JARs.

## Summary

CoDrone EDU Java is intended for educators and students. It exposes a simple, well-documented API to control the drone (flight, sensors, LEDs, buzzer), includes classroom-ready examples, and provides teacher utilities in a separate teacher JAR. The project aims for behavioral parity with the reference Python API while adapting to Java idioms and safety.

---

## 🎯 Project Vision

The CoDrone EDU Java API aims to provide educators and students with a robust, classroom-ready programming interface that matches the functionality and ease of use of the official Python API while leveraging Java's educational benefits for AP Computer Science A and university-level courses.

## 🧪 Testing & Quality Assurance

The project maintains high code quality standards with:
- **Comprehensive Test Coverage**: 134+ automated test cases
- **Educational Focus Testing**: Validates classroom usability and student experience
- **Python API Compatibility**: Ensures method parity and behavior consistency
- **Continuous Integration**: Automated testing on code changes

## 📚 Educational Use Cases

This API will support:

### **K-12 STEM Education**
- Introduction to programming concepts through drone control
- Physics demonstrations (motion, acceleration, color theory)
- Problem-solving through robotics challenges

### **AP Computer Science A**
- Object-oriented programming with real hardware
- Array and data structure manipulation with sensor data

# CoDrone EDU Java API

🎓 Educational Java API for CoDrone EDU — a classroom-focused Java library for teaching drone programming, aligned with common K–12 and AP Computer Science A learning objectives.

## Quick overview
CoDrone EDU Java provides a simple, well-documented interface to fly and program CoDrone EDU hardware from Java. The library exposes core flight control, basic sensors, LED control, and curated student examples so classrooms and instructors can teach programming and robotics with hands-on activities.

Artifacts produced by the build:
- Student JAR (for classroom/student use)
- Teacher JAR (includes teacher/testing utilities)
- Sources JAR and Javadoc JAR

## Development status (classroom-ready)
The CoDrone EDU Java API is classroom-ready for student use. Core flight controls, sensor access, LED control, and the student examples are implemented and tested. Teacher utilities are included in the teacher edition JAR. We continue to refine advanced features and documentation; check the release notes for details.

## Release: v1.0.16 — Classroom release (student edition)
This release focuses on providing a stable Java experience for classroom use.

Highlights:
- Core flight controls: user-friendly movement commands for classroom exercises.
- Sensors: accelerometer, gyroscope, and distance sensors exposed with simple APIs for lab work.
- LED control: full support for drone and controller LEDs, example patterns, and guided activities.
- Student examples: curated example projects suitable for K–12 and AP Computer Science lessons.
- Packaging: Student and Teacher JARs plus Javadoc and sources are produced by the build; teacher utilities are provided as a separate teacher JAR.

Notes:
- Teacher edition includes additional testing and classroom utilities — find it in the GitHub Release assets.
- Advanced topics such as swarm programming and autonomous research features are planned for future releases.

## Why use the Java API
- Designed for educators: simple, consistent APIs and examples aligned to classroom exercises.
- AP CSA-friendly: encourages object-oriented thinking and standard Java programming patterns.
- Portable: packaged JARs, sources, and Javadoc make it easy to integrate into existing Java curricula.

## Getting started (student)
1. Download the student JAR from the latest GitHub Release (look for `codrone-edu-java-<version>-student.jar`) or add the library to your project using the artifact published to Maven Central when available.
2. Example dependency (Maven; adjust group/artifact/version as needed):
```xml
<dependency>
  <groupId>edu.codrone</groupId>
  <artifactId>codrone-edu-java</artifactId>
  <version>1.0.16</version>
</dependency>
```
3. See the `src/main/java/com/otabi/jcodroneedu/examples` directory for hands-on sample programs and lesson starters.

## Teacher edition
The teacher edition JAR includes extra utilities for instructors such as testing helpers and test harnesses used in classroom assessments. The teacher JAR is distributed via the Release assets for instructors and course maintainers.

## Documentation & examples
- API docs (Javadoc) are produced with each release and included in release assets.
- Example programs are in `src/main/java/com/otabi/jcodroneedu/examples`.
- Implementation notes and design documents are in the `docs/` and `reference/` directories.

## Testing & quality
- We run automated tests as part of the build pipeline and maintain a suite of unit tests focused on classroom behaviors.
- Before adopting the library for production curricula, instructors should run the examples and tests in their environment to verify behavior with their hardware.

## Installing and building locally (developer convenience)
To build artifacts locally:
```bash
# build everything, run tests, and create student/teacher/sources/javadoc jars
./gradlew build studentJar teacherJar sourcesJar javadocJar
```
To produce a local Maven publish (dry run):
```bash
./gradlew publishStudentPublicationToMavenLocal -Pversion=1.0.16
```

## Contributing
We welcome contributions once the core API is stabilized. For now:
- Open issues for bugs or feature requests.
- Contribute educational examples or documentation improvements.
- If you'd like to help with teacher tooling or advanced features, open an issue and we’ll coordinate.

## License
This project is available under the MIT License. See the `LICENSE` file in the repository for details.

## Changelog & releases
See `CHANGELOG.md` for past release notes. The most recent release is v1.0.16 — check the GitHub Releases page for packaged artifacts (student JAR, teacher JAR, sources, javadoc).

## Contact & support
For timeline questions, classroom integration, or bug reports, open an issue on this repository. For private instructor access to teacher tooling, check the release assets or contact the maintainers through the project issues.
