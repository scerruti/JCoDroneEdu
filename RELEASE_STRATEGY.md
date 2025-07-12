# 📦 Release Strategy

## 🎯 Dual Release Approach

This project provides **two distinct releases** to serve different audiences in the educational ecosystem:

### 📚 **Student Release** - Clean & Simple
- **Target Audience**: Students learning drone programming
- **Content**: Core CoDrone EDU library only
- **Distribution**: Maven Central + GitHub releases
- **Maven Coordinates**: `edu.codrone:codrone-edu-java:VERSION`

### 👨‍🏫 **Teacher Release** - Testing Framework Included
- **Target Audience**: Instructors creating assignments and tests
- **Content**: Core library + MockDrone + DroneTest + teacher documentation
- **Distribution**: GitHub releases only
- **Usage**: Download JAR, add to classpath for testing framework access

## 🚀 Using the Releases

### For Students

#### Option 1: Maven/Gradle (Recommended)
```xml
<!-- Maven -->
<dependency>
    <groupId>edu.codrone</groupId>
    <artifactId>codrone-edu-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

```kotlin
// Gradle
implementation("edu.codrone:codrone-edu-java:1.0.0")
```

#### Option 2: Direct JAR Download
1. Go to [Releases](https://github.com/scerruti/JCoDroneEdu/releases)
2. Download `codrone-edu-java-VERSION-student.jar`
3. Add to your project classpath

### For Teachers

#### Setting Up Testing Environment
1. **Download Teacher Edition**:
   - Go to [Releases](https://github.com/scerruti/JCoDroneEdu/releases)
   - Download `codrone-edu-java-VERSION-teacher.jar`

2. **Add to Student Project**:
   ```kotlin
   // In student project build.gradle.kts
   dependencies {
       implementation("edu.codrone:codrone-edu-java:1.0.0")  // Student library
       testImplementation(files("path/to/codrone-edu-java-1.0.0-teacher.jar"))  // Testing framework
   }
   ```

3. **Create Tests**:
   ```java
   // In student project src/test/java/
   import com.otabi.jcodroneedu.DroneTest;
   
   public class SquareFlightTest extends DroneTest {
       @Test
       public void testSquarePattern() {
           executeStudentDroneOperations();
           assertTrue(mockDrone.wasSquarePatternUsed());
       }
   }
   ```

## 📖 Teacher Resources Included

The teacher edition includes:
- **MockDrone**: Full drone simulation for testing
- **DroneTest**: Base class for creating assignment tests
- **TEACHER_COPILOT_GUIDE.md**: Using GitHub Copilot for test creation
- **TESTING_GUIDE.md**: Comprehensive testing documentation
- **CODRONE_EDU_METHOD_TRACKING.md**: API change tracking

## 🔄 Release Process

### Automated Releases
Releases are automated via GitHub Actions when you:
1. **Push a version tag**: `git tag v1.0.0 && git push origin v1.0.0`
2. **Trigger manual release**: Via GitHub Actions UI

### What Gets Published Where

| Artifact | Maven Central | GitHub Releases | Purpose |
|----------|---------------|-----------------|---------|
| Student JAR | ✅ | ✅ | Student coursework |
| Teacher JAR | ❌ | ✅ | Instructor testing |
| Javadoc | ✅ | ✅ | Documentation |
| Sources | ✅ | ✅ | Reference |

### Version Strategy
- **Student Release**: Clean semantic versioning (1.0.0, 1.1.0, 2.0.0)
- **Teacher Release**: Same version as student + teacher-specific resources
- **SNAPSHOT**: Development versions for testing

## 🎓 Educational Benefits

### For Students
- **Simple Dependency**: One clean library, no test pollution
- **Professional Practice**: Learn proper dependency management
- **Documentation**: Full Javadoc for learning API usage

### For Teachers
- **Testing Framework**: MockDrone for comprehensive assignment validation
- **GitHub Copilot Integration**: AI-assisted test creation
- **Student Project Separation**: Tests live in student projects, not library
- **Automated Validation**: Consistent grading through automated tests

## 🛠️ Development Workflow

### Making a Release
1. **Update version** in `build.gradle.kts`
2. **Commit changes**: `git commit -am "Release v1.0.0"`
3. **Create tag**: `git tag v1.0.0`
4. **Push**: `git push origin main --tags`
5. **GitHub Actions** handles the rest automatically

### Local Testing
```bash
# Build both releases locally
./gradlew build studentJar teacherJar

# Test student JAR
./gradlew publishToMavenLocal
# Then test in a separate student project

# Test teacher JAR
# Add build/libs/*-teacher.jar to a test project classpath
```

This dual-release strategy ensures that students get a clean, professional library experience while teachers have access to the full testing framework needed for comprehensive assignment validation.
