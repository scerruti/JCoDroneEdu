---
layout: guide
title: Building Custom Student Projects
category: Teacher Guide
permalink: /guides/teacher/custom-projects.html
---

## Building Custom Gradle/Maven Projects

For teachers creating their own project templates or custom curriculum, here's how to set up Gradle or Maven projects with the CoDrone EDU Java library.

### Why Custom Projects?

While we provide pre-built project templates with hidden Maven structures, some advanced teachers may want to:
- Create custom project scaffolds for specific curriculum needs
- Integrate with existing build infrastructure
- Modify dependencies or add specialized libraries
- Share project structures with other institutions

---

## Maven Setup

### Adding the Dependency

In your `pom.xml`, add the following to your `<dependencies>` section:

```xml
<dependency>
  <groupId>com.otabi</groupId>
  <artifactId>codrone-edu-java</artifactId>
  <version>1.3.2</version>
</dependency>
```

### IDE Integration (VS Code, IntelliJ, Eclipse)

Maven automatically downloads and attaches:
- **Source JAR** - For browsing source code in your IDE
- **Javadoc JAR** - For inline documentation and hover tooltips

Most IDEs discover these automatically. If not, manually configure your IDE to download `-sources.jar` and `-javadoc.jar` classifiers from Maven Central.

### Example Project Structure

```
my-drone-project/
├── pom.xml
├── src/
│   ├── main/java/
│   │   └── com/example/
│   │       └── FirstFlight.java
│   └── test/java/
│       └── com/example/
│           └── FirstFlightTest.java
└── README.md
```

### Sample pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-drone-project</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- CoDrone EDU Java API -->
        <dependency>
            <groupId>com.otabi</groupId>
            <artifactId>codrone-edu-java</artifactId>
            <version>1.3.2</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Gradle Setup

### Adding the Dependency

In your `build.gradle` (or `build.gradle.kts` for Kotlin DSL), add:

```gradle
dependencies {
    implementation 'com.otabi:codrone-edu-java:1.3.2'
}

repositories {
    mavenCentral()
}
```

### IDE Integration

Gradle automatically manages IDE attachments for sources and javadoc. Most IDEs (VS Code with Extension Pack for Java, IntelliJ IDEA, Eclipse) handle this automatically.

### Example Project Structure

```
my-drone-project/
├── build.gradle
├── settings.gradle
├── src/
│   ├── main/java/
│   │   └── com/example/
│   │       └── FirstFlight.java
│   └── test/java/
│       └── com/example/
│           └── FirstFlightTest.java
└── README.md
```

### Sample build.gradle

```gradle
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    // CoDrone EDU Java API
    implementation 'com.otabi:codrone-edu-java:1.3.2'

    // Testing
    testImplementation 'junit:junit:4.13.2'
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
```

---

## Verifying the Setup

After building your project, verify the dependency is available:

### Maven
```bash
mvn dependency:tree
```

### Gradle
```bash
gradle dependencies
```

Both should show `com.otabi:codrone-edu-java:1.3.2` in the output.

---

## IDE Configuration (If Manual Attachment Needed)

### VS Code
1. Install "Extension Pack for Java" (includes Maven/Gradle support)
2. The IDE automatically downloads and attaches sources/javadoc
3. Hover over Drone class methods to see inline documentation

### IntelliJ IDEA
1. Open Settings → Languages & Frameworks → Java → Maven
2. Check "Download Sources" and "Download Documentation"
3. Rebuild the project: Build → Rebuild Project

### Eclipse
1. Right-click project → Properties → Java Build Path → Libraries
2. Select the JAR → Edit → Attach Source/Javadoc
3. Specify the classifier or Maven Central URL

---

## Troubleshooting

### "Cannot find symbol" Errors
- Rebuild: `mvn clean compile` or `gradle clean build`
- Check Maven/Gradle cached: `mvn clean` or `gradle clean`
- Verify Maven Central connectivity (check firewall/proxy)

### Missing Javadoc in IDE
- Re-download dependencies: `mvn dependency:purge-local-repository` or delete `~/.gradle/caches`
- Manually specify in IDE: Download from Maven Central with `-javadoc` classifier

### Drone Class Not Found
- Verify `com.otabi:codrone-edu-java:1.3.2` appears in `dependency:tree`
- Check Java version (requires 21+)
- Ensure `mavenCentral()` repository is configured

---

## Next Steps

Now that you have a working project:
1. Create your first flight program (refer to [Student Guide - Getting Started]({{ '/guides/student/getting-started.html' | relative_url }}))
2. Review [API Reference]({{ '/guides/student/api-reference.html' | relative_url }}) for available methods
3. Share your project template with colleagues!

---

**Questions?** Refer to the [Teacher FAQ]({{ '/guides/teacher/philosophy.html' | relative_url }}) or check the [Javadoc]({{ '/javadoc/index.html' | relative_url }}).
