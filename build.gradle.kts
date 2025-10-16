import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

// Ensure a consistent Java toolchain for local and CI builds. This makes Gradle
// request a JDK matching the language level used across subprojects (21), so
// compilation doesn't fail when runners provide an older JAVA_HOME.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// ------------------------------------------------------------
// Run Smoke Test - convenience task for the example SmokeTest
// ------------------------------------------------------------
tasks.register<JavaExec>("runSmokeTest") {
    group = "verification"
    description = "Runs the SmokeTest example to verify controller connection (no flight commands)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.SmokeTest")
    // Pass args using: ./gradlew runSmokeTest --args='/dev/cu.usbserial-XXXX'
}

// -----------------------------------------------------------------
// Run Flight Smoke Test - gated, requires explicit confirmation flags
// -----------------------------------------------------------------
tasks.register<JavaExec>("runFlightSmokeTest") {
    group = "verification"
    description = "Runs the guarded FlightSmokeTest (requires --allow-flight and --confirm=YES to actually fly)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.FlightSmokeTest")
    // Pass args using: ./gradlew runFlightSmokeTest --args='--allow-flight --confirm=YES'
    standardInput = System.`in`
}

// ------------------------------------------------------------
// Run Conservative Flight - gated example (requires --allow-flight)
// ------------------------------------------------------------
tasks.register<JavaExec>("runConservativeFlight") {
    group = "verification"
    description = "Runs the ConservativeFlight example (requires --allow-flight to actually fly)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.ConservativeFlight")
    // Usage: ./gradlew runConservativeFlight --args='--allow-flight'
}

// ------------------------------------------------------------
// Run Quick LED Test - exercises drone and controller LEDs
// ------------------------------------------------------------
tasks.register<JavaExec>("runQuickLEDTest") {
    group = "verification"
    description = "Runs the QuickLEDTest example to verify drone and controller LED functionality."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.QuickLEDTest")
}


// ------------------------------------------------------------
// Run Test Harness - interactive menu to exercise drone features
// ------------------------------------------------------------
tasks.register<JavaExec>("runTestHarness") {
    group = "verification"
    description = "Runs the interactive TestHarness example for manual testing."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.TestHarness")
    // Forward stdin so Scanner(System.in) in the harness can read user input when run via Gradle
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Sensor Display GUI - Swing-based non-flying telemetry monitor
// -----------------------------------------------------------------
tasks.register<JavaExec>("runSensorDisplayGui") {
    group = "verification"
    description = "Runs the Swing-based SensorDisplay GUI (non-flying telemetry monitor)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.SensorDisplayGui")
}

// -----------------------------------------------------------------
// Run Buzzer Test - tests drone and controller buzzer functionality
// -----------------------------------------------------------------
tasks.register<JavaExec>("runBuzzerTest") {
    group = "verification"
    description = "Tests drone and controller buzzers with notes and frequencies."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.BuzzerTest")
}

// -----------------------------------------------------------------
// Run Autonomous Ping Test - tests autonomous methods and ping feature
// -----------------------------------------------------------------
tasks.register<JavaExec>("runAutonomousPingTest") {
    group = "verification"
    description = "Comprehensive test for autonomous flight (avoidWall, keepDistance) and ping feature."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.AutonomousPingTest")
    standardInput = System.`in`  // Forward stdin for manual confirmations
}

// -----------------------------------------------------------------
// Run Single Note Test - plays one note repeatedly for audio testing
// -----------------------------------------------------------------
tasks.register<JavaExec>("runSingleNoteTest") {
    group = "verification"
    description = "Plays a single note (G4) repeatedly to test buzzer audio."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.SingleNoteTest")
}

// -----------------------------------------------------------------
// Run Melody Player - plays musical melodies on the buzzer
// -----------------------------------------------------------------
tasks.register<JavaExec>("runMelodyPlayer") {
    group = "verification"
    description = "Plays musical melodies using the drone buzzer."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.MelodyPlayer")
}

// -----------------------------------------------------------------
// Run Controller Input GUI - interactive controller testing
// -----------------------------------------------------------------
tasks.register<JavaExec>("runControllerInputGui") {
    group = "verification"
    description = "Interactive GUI for testing controller joysticks and buttons."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ControllerInputGui")
}

// -----------------------------------------------------------------
// Run Both Monitors - sensor and controller GUIs together
// -----------------------------------------------------------------
tasks.register<JavaExec>("runBothMonitors") {
    group = "verification"
    description = "Runs both sensor and controller monitors with L1 hold takeoff test."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.BothMonitors")
    // Forward stdin so keyboard input (Q to quit) works
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Controller Input Debug - console debug for controller
// -----------------------------------------------------------------
tasks.register<JavaExec>("runControllerInputDebug") {
    group = "verification"
    description = "Debug tool to see raw controller input data."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ControllerInputDebug")
}

// -----------------------------------------------------------------
// Run AccelTest - simple CLI accelerometer monitor
// -----------------------------------------------------------------
tasks.register<JavaExec>("runAccelTest") {
    group = "verification"
    description = "Runs the command-line AccelTest (prints accelerometer and angle data)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.AccelTest")
    // Forward stdin so interactive prompts (press Enter) work when run via Gradle
    standardInput = System.`in`
}

tasks.register<JavaExec>("runMotionDump") {
    group = "verification"
    description = "Dumps raw Motion short values and scaled accel/angle for 5s."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.MotionDump")
}

// -----------------------------------------------------------------
// Run Altitude/Pressure Test - displays altitude, pressure, and height
// -----------------------------------------------------------------
tasks.register<JavaExec>("runAltitudePressureTest") {
    group = "verification"
    description = "Displays altitude, pressure, and height sensor data with calculations."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.AltitudePressureTest")
}

// Run Elevation API Demo - demonstrates new elevation methods
// -----------------------------------------------------------------
tasks.register<JavaExec>("runElevationApiDemo") {
    group = "verification"
    description = "Demonstrates the elevation API (getUncorrectedElevation, getCorrectedElevation, getElevation)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.ElevationApiDemo")
}

// Run Calibrated Elevation Demo - shows weather-calibrated altitude
// -----------------------------------------------------------------
tasks.register<JavaExec>("runCalibratedElevationDemo") {
    group = "verification"
    description = "Demonstrates weather-calibrated elevation using real-time pressure data."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.CalibratedElevationDemo")
}

// Run Weather Service Test - tests API connectivity without drone
// -----------------------------------------------------------------
tasks.register<JavaExec>("runWeatherServiceTest") {
    group = "verification"
    description = "Tests weather API connectivity and data retrieval (no drone required)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.WeatherServiceTest")
}

// Run Relative Height Demo - demonstrates pressure-based relative height
// -----------------------------------------------------------------
tasks.register<JavaExec>("runRelativeHeightDemo") {
    group = "verification"
    description = "Demonstrates relative height measurement using pressure reference (Python compatibility)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.RelativeHeightDemo")
}

// Run Automatic Elevation Demo - demonstrates automatic location detection
// -----------------------------------------------------------------
tasks.register<JavaExec>("runAutomaticElevationDemo") {
    group = "verification"
    description = "Demonstrates automatic location detection and pressure calibration with fallback strategy."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.AutomaticElevationDemo")
}

// -----------------------------------------------------------------
// Run MultiSensorTest - range, flow, temperature, and color sensors
// -----------------------------------------------------------------
tasks.register<JavaExec>("runMultiSensorTest") {
    group = "verification"
    description = "Runs the MultiSensorTest example to snapshot range, optical flow, temperature, and color sensors."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.MultiSensorTest")
    // Forward stdin so interactive prompts (press Enter) work when run via Gradle
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run TemperatureCalibrationDemo - demonstrates temperature sensor calibration
// -----------------------------------------------------------------
tasks.register<JavaExec>("runTemperatureCalibrationDemo") {
    group = "verification"
    description = "Demonstrates temperature sensor calibration and the difference between raw sensor and calibrated ambient temperature."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.demos.TemperatureCalibrationDemo")
}

// -----------------------------------------------------------------
// Run TemperatureCalibrationExperiment - student research experiment
// -----------------------------------------------------------------
tasks.register<JavaExec>("runTemperatureCalibrationExperiment") {
    group = "research"
    description = "Runs a systematic temperature calibration experiment collecting data about warm-up and flight effects. Outputs CSV data for analysis."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.research.TemperatureCalibrationExperiment")
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run ColorSensorDebug - debug color sensor data
// -----------------------------------------------------------------
tasks.register<JavaExec>("runColorSensorDebug") {
    group = "verification"
    description = "Runs the ColorSensorDebug tool to show detailed color sensor data including HSVL values."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.tests.ColorSensorDebug")
    // Forward stdin so interactive prompts work
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run ControllerDisplayExample - demonstrates controller display functionality
// -----------------------------------------------------------------
tasks.register<JavaExec>("runControllerDisplayExample") {
    group = "verification"
    description = "Runs the ControllerDisplayExample to demonstrate controller display drawing capabilities."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ControllerDisplayExample")
}

// -----------------------------------------------------------------
// Run Example Menu - interactive menu to launch any example
// -----------------------------------------------------------------
tasks.register<JavaExec>("runExampleMenu") {
    group = "application"
    description = "Runs the interactive Example Menu to launch any available example program."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ExampleMenu")
    // Forward stdin so the menu can read user input
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run L0103 Turning Navigation - test position-based movement
// -----------------------------------------------------------------
tasks.register<JavaExec>("runL0103TurningNavigation") {
    group = "application"
    description = "Runs the L0103 Turning Navigation example to test position-based movement commands."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.L0103TurningNavigation")
}

// -----------------------------------------------------------------
// Run Error Monitoring Example - demonstrates error checking (demo mode)
// -----------------------------------------------------------------
tasks.register<JavaExec>("runErrorMonitoringDemo") {
    group = "verification"
    description = "Runs the ErrorMonitoringExample in demo mode (no hardware required)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ErrorMonitoringExample")
    // No arguments = demo mode
}

// -----------------------------------------------------------------
// Run Error Monitoring Example - connect mode
// -----------------------------------------------------------------
tasks.register<JavaExec>("runErrorMonitoringConnect") {
    group = "verification"
    description = "Runs the ErrorMonitoringExample with drone connection (reads real error data, no flight)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ErrorMonitoringExample")
    args = listOf("--connect")
}

// -----------------------------------------------------------------
// Run Error Monitoring Example - flight mode (CAUTION!)
// -----------------------------------------------------------------
tasks.register<JavaExec>("runErrorMonitoringFly") {
    group = "verification"
    description = "Runs the ErrorMonitoringExample with ACTUAL FLIGHT operations (CAUTION!)."
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ErrorMonitoringExample")
    args = listOf("--fly")
}

group = "com.otabi"

// Determine project version from (1) -Pversion= passed via Gradle invocation,
// (2) RELEASE_VERSION environment variable (used by CI), or (3) fallback literal.
// Gradle always exposes a 'version' property which may be 'unspecified' by default.
// Prefer a user-provided -Pversion=... (but ignore the default 'unspecified'),
// then an environment RELEASE_VERSION, then fall back to the literal.
val explicitVersionFromProp = project.findProperty("version")?.toString()
val explicitVersionEnv = System.getenv("RELEASE_VERSION")

val resolvedVersion = when {
    explicitVersionFromProp != null && explicitVersionFromProp.isNotBlank() && explicitVersionFromProp != "unspecified" -> explicitVersionFromProp
    explicitVersionEnv != null && explicitVersionEnv.isNotBlank() -> explicitVersionEnv
    else -> "1.0.0"
}

version = resolvedVersion
logger.lifecycle("Project version set to: $version")

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fazecast:jSerialComm:2.11.0")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("com.google.guava:guava:33.2.1-jre")
    
    // JSR 385 - Units of Measurement API for unit conversion
    implementation("tech.units:indriya:2.2")
    
    // JSON parsing for weather API
    implementation("org.json:json:20240303")

    // JUnit 5 (Jupiter) for testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")

    // Mockito for mocking in tests
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("net.bytebuddy:byte-buddy:1.15.10")

        // Silence logging during tests: provide a no-op SLF4J binding and test-specific log4j2 config
        testImplementation("org.slf4j:slf4j-nop:2.0.9")

    runtimeOnly("org.apache.logging.log4j:log4j-core:2.23.1")
    
    // Smile ML library for KNN and plotting
    implementation("com.github.haifengl:smile-core:3.0.1")
    implementation("com.github.haifengl:smile-plot:3.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}

// Make Javadoc generation tolerant of custom tags used for documentation (educational notes)
tasks.named<org.gradle.api.tasks.javadoc.Javadoc>("javadoc") {
    // Do not fail the build on Javadoc errors in CI
    // Use setter to avoid accessing a private property from Kotlin
    this.setFailOnError(false)
    // Disable strict doclint introduced in newer JDKs by passing -Xdoclint:none
    val stdOptions = options as org.gradle.external.javadoc.StandardJavadocDocletOptions
    stdOptions.addStringOption("-Xdoclint:none", "")
}

// --------------------------
// Release artifact tasks
// --------------------------
// sourcesJar
val sourcesJar by tasks.registering(Jar::class) {
    archiveBaseName.set("codrone-edu-java")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

// javadocJar
val javadocJar by tasks.registering(Jar::class) {
    archiveBaseName.set("codrone-edu-java")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("javadoc")
    val javadocTask = tasks.named("javadoc")
    // Use destinationDir for compatibility across Gradle versions
    from(javadocTask.map { (it as org.gradle.api.tasks.javadoc.Javadoc).destinationDir })
    dependsOn("javadoc")
}

// Configure the javadoc task to tolerate project custom tags/HTML and not fail the build
tasks.named<org.gradle.api.tasks.javadoc.Javadoc>("javadoc") {
    // Do not fail the build on Javadoc errors in CI
    this.setFailOnError(false)

    // Try to configure StandardJavadocDocletOptions when available
    try {
        val stdOptions = options as? org.gradle.external.javadoc.StandardJavadocDocletOptions
        if (stdOptions != null) {
            stdOptions.addStringOption("tag", "educational:a:")
            stdOptions.addStringOption("tag", "pythonEquivalent:a:")
            stdOptions.addStringOption("tag", "apiNote:a:")
            stdOptions.addStringOption("tag", "example:a:")

            // Disable doclint (suppress strict HTML/structure checks)
            // StandardJavadocDocletOptions expects option names without a leading dash.
            // To disable doclint use the Xdoclint option without passing an extra colon/flag value.
            // Some JDKs don't accept the form with a leading `--` so avoid that.
            try {
                stdOptions.addStringOption("Xdoclint", "none")
            } catch (e: Exception) {
                // Fall back to leaving doclint as-is; javadoc task is non-fatal so build will continue.
                logger.warn("Could not set Xdoclint option on this JDK: ${e.message}")
            }

            // Prefer HTML5 output when supported
            stdOptions.addBooleanOption("html5", true)
        }
    } catch (e: Exception) {
        logger.warn("Could not configure advanced javadoc options: ${e.message}")
    }
}

// studentJar: core library only (no test helpers)
val studentJar by tasks.registering(Jar::class) {
    archiveBaseName.set("codrone-edu-java")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("student")
    from(sourceSets.main.get().output)
}

// teacherJar: includes test helpers and teacher resources (packaged into a -teacher.jar)
val teacherJar by tasks.registering(Jar::class) {
    archiveBaseName.set("codrone-edu-java")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("teacher")
    // Include main classes
    from(sourceSets.main.get().output)
    // Include test-support classes from test source sets to provide DroneTest, MockDrone, etc.
    from(sourceSets.getByName("test").output)
    // Include teacher docs if present
    from("TEACHER_COPILOT_GUIDE.md") { into("docs") }
}

// Ensure publications include artifacts needed for Maven/Release
publishing {
    publications {
        create<MavenPublication>("student") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
            artifact(studentJar.get())
            artifactId = "codrone-edu-java"
            groupId = "edu.codrone"
            version = project.version.toString()
        }
        // Teacher publication is not published to Maven Central; keep for local packaging and GitHub release
        create<MavenPublication>("teacher") {
            from(components["java"])
            artifact(teacherJar.get())
            artifactId = "codrone-edu-java-teacher"
            groupId = "edu.codrone"
            version = project.version.toString()
        }
    }
}

// =============================================================================
// Python CoDrone EDU Library Management Tasks
// =============================================================================

val pythonVenvDir = file("$projectDir/python-venv")
val referenceDir = file("$projectDir/reference")
val pythonRequirements = """
codrone-edu
requests
beautifulsoup4
""".trimIndent()

/**
 * Creates a Python virtual environment for CoDrone EDU library management
 */
tasks.register("createPythonVenv") {
    group = "python"
    description = "Creates a Python virtual environment with CoDrone EDU library"
    
    inputs.property("requirements", pythonRequirements)
    outputs.dir(pythonVenvDir)
    
    doLast {
        if (pythonVenvDir.exists()) {
            delete(pythonVenvDir)
        }
        
        // Create virtual environment
        exec {
            commandLine("python3", "-m", "venv", pythonVenvDir.absolutePath)
        }
        
        // Upgrade pip
        exec {
            commandLine("$pythonVenvDir/bin/pip", "install", "--upgrade", "pip")
        }
        
        // Install requirements
        file("$pythonVenvDir/requirements.txt").writeText(pythonRequirements)
        exec {
            commandLine("$pythonVenvDir/bin/pip", "install", "-r", "$pythonVenvDir/requirements.txt")
        }
        
        println("✅ Python virtual environment created with CoDrone EDU library")
    }
}

/**
 * Updates the CoDrone EDU library to the latest version
 */
tasks.register("updateCodroneEdu") {
    group = "python"
    description = "Updates CoDrone EDU library to latest version"
    dependsOn("createPythonVenv")
    
    doLast {
        // Update the library
        exec {
            commandLine("$pythonVenvDir/bin/pip", "install", "--upgrade", "codrone-edu")
        }
        
        // Get version info
        val versionOutput = ByteArrayOutputStream()
        exec {
            commandLine("$pythonVenvDir/bin/pip", "show", "codrone-edu")
            standardOutput = versionOutput
        }
        
        val versionInfo = versionOutput.toString()
        val version = versionInfo.lines()
            .find { line: String -> line.startsWith("Version:") }
            ?.substringAfter("Version: ")
            ?.trim()
        
        println("📦 CoDrone EDU library updated to version: $version")
    }
}

/**
 * Copies the CoDrone EDU library from venv to reference directory
 */
tasks.register("updateReferenceCode") {
    group = "python"
    description = "Copies CoDrone EDU library code to reference directory"
    dependsOn("updateCodroneEdu")
    
    inputs.dir("$pythonVenvDir/lib")
    outputs.dir("$referenceDir/codrone_edu")
    
    doLast {
        // Find the site-packages directory dynamically (handles different Python versions)
        val libDir = File("$pythonVenvDir/lib")
        val pythonDirs = libDir.listFiles { file -> file.isDirectory && file.name.startsWith("python") }
            ?: throw GradleException("No Python directories found in: ${libDir.absolutePath}")
        
        val sitePackagesDir = pythonDirs.firstOrNull()?.let { File(it, "site-packages") }
            ?: throw GradleException("No Python lib directory found")
        
        val codroneEduSource = File(sitePackagesDir, "codrone_edu")
        
        if (!codroneEduSource.exists()) {
            throw GradleException("Could not find codrone_edu at: ${codroneEduSource.absolutePath}")
        }
        
        val codroneEduDest = File(referenceDir, "codrone_edu")
        
        // Clean and copy
        if (codroneEduDest.exists()) {
            delete(codroneEduDest)
        }
        copy {
            from(codroneEduSource)
            into(codroneEduDest)
            exclude("**/__pycache__/**", "**/*.pyc", "**/*.pyo")
        }
        
        // Get and save version information
        val versionOutput = ByteArrayOutputStream()
        exec {
            commandLine("$pythonVenvDir/bin/pip", "show", "codrone-edu")
            standardOutput = versionOutput
        }
        
        val versionInfo = versionOutput.toString()
        val version = versionInfo.lines()
            .find { line: String -> line.startsWith("Version:") }
            ?.substringAfter("Version: ")
            ?.trim() ?: "unknown"
        
        // Update version.txt
        val versionFile = File(referenceDir, "version.txt")
        val currentTime = LocalDateTime.now()
        versionFile.writeText("""
# CoDrone EDU Python Library Version Information

## Version Detection
**CONFIRMED VERSION: $version** ✅

Source: Automatically updated from PyPI via Gradle task

## Package Information
${versionInfo.lines().joinToString("\n") { line: String -> "- $line" }}

## Last Update
- **Date**: $currentTime
- **Method**: Gradle updateReferenceCode task
- **Source**: pip install codrone-edu

---
**Auto-generated**: Do not edit manually - run './gradlew updateReferenceCode'
        """.trimIndent())
        
        println("✅ Reference code updated to version $version")
        println("📁 Code copied to: $codroneEduDest")
    }
}

/**
 * Fetches and updates changelog from Robolink documentation
 */
tasks.register("updateChangelog") {
    group = "python"
    description = "Fetches latest changelog from Robolink docs and updates tracking document"
    dependsOn("createPythonVenv")
    
    doLast {
        // Python script to fetch changelog
        val fetchScript = """
import requests
from bs4 import BeautifulSoup
import sys
from datetime import datetime

def fetch_changelog():
    url = "https://docs.robolink.com/docs/CoDroneEDU/Python/Python-Changelog"
    
    try:
        response = requests.get(url)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.content, 'html.parser')
        
        # Find the main content area
        main_content = soup.find('main') or soup.find('article') or soup.find('div', class_='markdown')
        
        if not main_content:
            print("Could not find main content area")
            return None
        
        # Extract changelog content
        changelog_lines = []
        changelog_lines.append("# CoDrone EDU Python Library Changelog")
        changelog_lines.append("")
        changelog_lines.append(f"**Last Updated**: {datetime.now().strftime('%B %d, %Y')}")
        changelog_lines.append(f"**Source**: {url}")
        changelog_lines.append("")
        
        # Get all text content
        text_content = main_content.get_text()
        changelog_lines.append(text_content)
        
        return "\\n".join(changelog_lines)
        
    except Exception as e:
        print(f"Error fetching changelog: {e}")
        return None

if __name__ == "__main__":
    changelog = fetch_changelog()
    if changelog:
        print(changelog)
    else:
        sys.exit(1)
        """.trimIndent()
        
        // Write and run the script
        val scriptFile = File(pythonVenvDir, "fetch_changelog.py")
        scriptFile.writeText(fetchScript)
        
        try {
            val changelogOutput = ByteArrayOutputStream()
            exec {
                commandLine("$pythonVenvDir/bin/python", scriptFile.absolutePath)
                standardOutput = changelogOutput
            }
            
            val changelog = changelogOutput.toString()
            
            // Append to tracking document
            val trackingFile = File(projectDir, "CODRONE_EDU_METHOD_TRACKING.md")
            if (trackingFile.exists()) {
                val existingContent = trackingFile.readText()
                val currentTime = LocalDateTime.now()
                val updatedContent = existingContent + "\n\n" + """
---

## Python Library Changelog

$changelog

---
**Auto-updated**: $currentTime  
**Source**: Gradle updateChangelog task
                """.trimIndent()
                
                trackingFile.writeText(updatedContent)
                println("✅ Changelog updated in tracking document")
            }
            
        } catch (exception: Exception) {
            println("⚠️ Could not fetch changelog: ${exception.message}")
            println("📝 Manual update may be required")
        } finally {
            scriptFile.delete()
        }
    }
}

/**
 * Checks if the reference code is up to date and monitors for changelog updates
 */
tasks.register("checkCodroneVersion") {
    group = "python"
    description = "Checks if local reference code matches latest PyPI version and detects changelog updates"
    
    doLast {
        // Check PyPI for latest version
        val latestVersionOutput = ByteArrayOutputStream()
        
        try {
            exec {
                commandLine("python3", "-c", """
import requests
import json
response = requests.get('https://pypi.org/pypi/codrone-edu/json')
data = response.json()
print(data['info']['version'])
                """.trimIndent())
                standardOutput = latestVersionOutput
            }
            
            val latestVersion = latestVersionOutput.toString().trim()
            
            // Check local version
            val versionFile = File(referenceDir, "version.txt")
            val localVersion = if (versionFile.exists()) {
                versionFile.readText()
                    .lines()
                    .find { line: String -> line.contains("CONFIRMED VERSION:") }
                    ?.substringAfter("CONFIRMED VERSION: ")
                    ?.substringBefore("**")
                    ?.trim() ?: "unknown"
            } else {
                "not found"
            }
            
            println("🔍 Version Check:")
            println("   Latest PyPI: $latestVersion")
            println("   Local Reference: $localVersion")
            
            if (localVersion != latestVersion) {
                println("⚠️  UPDATE AVAILABLE! Run './gradlew updateReferenceCode' to update")
                println("📋 CHANGELOG REVIEW NEEDED:")
                println("   After updating, review new features in CODRONE_EDU_METHOD_TRACKING.md")
                println("   Check for new methods that need MockDrone implementation")
                println("   Look for breaking changes that affect existing tests")
            } else {
                println("✅ Reference code is up to date")
                
                // Even when versions match, check if changelog was recently updated
                val trackingFile = File(projectDir, "CODRONE_EDU_METHOD_TRACKING.md")
                if (trackingFile.exists()) {
                    val content = trackingFile.readText()
                    val lastUpdate = content.lines()
                        .find { line: String -> line.contains("Auto-updated:") }
                        ?.substringAfter("Auto-updated: ")
                        ?.trim()
                    
                    if (lastUpdate != null) {
                        println("📝 Last changelog update: $lastUpdate")
                        println("💡 Consider running './gradlew updateChangelog' periodically for documentation updates")
                    }
                }
            }
            
        } catch (exception: Exception) {
            println("⚠️ Could not check PyPI version: ${exception.message}")
        }
    }
}

/**
 * Complete update workflow
 */
tasks.register("updateCodroneDocs") {
    group = "python"
    description = "Complete workflow: update library, reference code, and documentation"
    dependsOn("updateReferenceCode", "updateChangelog")
    
    doLast {
        println("🎉 CoDrone EDU reference materials updated successfully!")
        println("📋 Next steps:")
        println("   1. Review changes in reference/codrone_edu/")
        println("   2. Check CODRONE_EDU_METHOD_TRACKING.md for new changelog entries")
        println("   3. Update MockDrone class if new methods were added")
        println("   4. Run tests to ensure compatibility")
        println("   5. Commit changes to version control")
    }
}

/**
 * Quick changelog check without full update
 */
tasks.register("checkChangelog") {
    group = "python"
    description = "Quick check for recent changelog updates without full library update"
    dependsOn("createPythonVenv")
    
    doLast {
        // Python script to check for recent changes
        val checkScript = """
import requests
from bs4 import BeautifulSoup
from datetime import datetime, timedelta

def check_recent_changelog():
    url = "https://docs.robolink.com/docs/CoDroneEDU/Python/Python-Changelog"
    
    try:
        response = requests.get(url)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.content, 'html.parser')
        
        # Look for version headers and dates
        version_headers = soup.find_all(['h3', 'h4'], string=lambda text: text and 'Version' in text)
        
        print("🔍 Recent CoDrone EDU Changelog Versions:")
        print("=" * 50)
        
        recent_found = False
        for header in version_headers[:5]:
            version_text = header.get_text().strip()
            print(f"- {version_text}")
            
            # Look for date information near the header
            next_sibling = header.next_sibling
            while next_sibling and hasattr(next_sibling, 'get_text'):
                text = next_sibling.get_text().strip()
                if any(month in text for month in ['January', 'February', 'March', 'April', 'May', 'June', 
                                                   'July', 'August', 'September', 'October', 'November', 'December']):
                    print(f"  Date: {text}")
                    recent_found = True
                    break
                next_sibling = next_sibling.next_sibling
                
        if not recent_found:
            print("No recent dates found in changelog")
            
        print("=" * 50)
        print("💡 Run './gradlew updateChangelog' for detailed changelog analysis")
        
    except Exception as e:
        print(f"Error checking changelog: {e}")

if __name__ == "__main__":
    check_recent_changelog()
        """.trimIndent()
        
        // Write and run the script
        val scriptFile = File(pythonVenvDir, "check_changelog.py")
        scriptFile.writeText(checkScript)
        
        try {
            exec {
                commandLine("$pythonVenvDir/bin/python", scriptFile.absolutePath)
            }
        } catch (exception: Exception) {
            println("⚠️ Could not check changelog: ${exception.message}")
            println("📝 Run './gradlew updateChangelog' for full changelog update")
        } finally {
            scriptFile.delete()
        }
    }
}

// =============================================================================
// Pre-Release Validation Tasks
// =============================================================================

/**
 * Compare Python and Java APIs to identify missing methods
 */
tasks.register("compareApis") {
    group = "verification"
    description = "Compare Python and Java APIs and generate report"
    
    doLast {
        println("=" .repeat(60))
        println("API COMPARISON REPORT")
        println("=" .repeat(60))
        println()
        
        val reportFile = file("API_COMPARISON.md")
        val report = StringBuilder()
        
        report.appendLine("# API Comparison Report")
        report.appendLine()
        report.appendLine("**Generated:** ${LocalDateTime.now()}")
        report.appendLine("**Python Library:** ${file("reference/version.txt").readText().lines().find { it.contains("Version:") }?.substringAfter("Version:")?.trim() ?: "Unknown"}")
        report.appendLine()
        
        // Parse Python API
        val pythonFile = file("reference/codrone_edu/drone.py")
        val pythonMethods = mutableSetOf<String>()
        
        if (pythonFile.exists()) {
            pythonFile.readLines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("def ") && !trimmed.startsWith("def _")) {
                    val methodName = trimmed.substringAfter("def ").substringBefore("(")
                    if (!methodName.startsWith("_")) {
                        pythonMethods.add(methodName)
                    }
                }
            }
        }
        
        // Parse Java API
        val javaFile = file("src/main/java/com/otabi/jcodroneedu/Drone.java")
        val javaMethods = mutableSetOf<String>()
        
        if (javaFile.exists()) {
            javaFile.readLines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("public ") && trimmed.contains("(")) {
                    val methodPart = trimmed.substringAfter("public ").trim()
                    if (!methodPart.startsWith("class ") && !methodPart.startsWith("interface ")) {
                        val methodName = methodPart.substringAfter(" ").substringBefore("(").trim()
                        javaMethods.add(methodName)
                    }
                }
            }
        }
        
        // Find differences
        val inPythonNotJava = pythonMethods.filterNot { javaMethods.contains(it) || javaMethods.contains(toCamelCase(it)) }
        val inJavaNotPython = javaMethods.filterNot { pythonMethods.contains(it) || pythonMethods.contains(toSnakeCase(it)) }
        
        // Report
        report.appendLine("## Summary")
        report.appendLine()
        report.appendLine("- **Python Methods:** ${pythonMethods.size}")
        report.appendLine("- **Java Methods:** ${javaMethods.size}")
        report.appendLine("- **In Python, Not Java:** ${inPythonNotJava.size}")
        report.appendLine("- **In Java, Not Python:** ${inJavaNotPython.size}")
        report.appendLine()
        
        report.appendLine("## Methods in Python but NOT in Java")
        report.appendLine()
        if (inPythonNotJava.isEmpty()) {
            report.appendLine("✅ All Python methods have Java equivalents!")
        } else {
            report.appendLine("⚠️ Consider implementing these methods:")
            report.appendLine()
            inPythonNotJava.sorted().forEach { method ->
                report.appendLine("- `$method()`")
            }
        }
        report.appendLine()
        
        report.appendLine("## Methods in Java but NOT in Python")
        report.appendLine()
        if (inJavaNotPython.isEmpty()) {
            report.appendLine("✅ No Java-only methods")
        } else {
            report.appendLine("ℹ️ Java-specific methods (expected):")
            report.appendLine()
            inJavaNotPython.sorted().forEach { method ->
                report.appendLine("- `$method()`")
            }
        }
        report.appendLine()
        
        // Highlight important missing methods
        val importantMissing = listOf(
            "get_information_data",
            "get_cpu_id_data",
            "get_address_data",
            "get_count_data",
            "get_flight_time",
            "get_takeoff_count",
            "get_landing_count",
            "get_accident_count"
        )
        
        val criticalMissing = importantMissing.filter { it in inPythonNotJava }
        
        if (criticalMissing.isNotEmpty()) {
            report.appendLine("## ⚠️ Important Missing Methods for Inventory Management")
            report.appendLine()
            criticalMissing.forEach { method ->
                report.appendLine("- `$method()` - **RECOMMENDED FOR IMPLEMENTATION**")
            }
            report.appendLine()
        }
        
        // Write report
        reportFile.writeText(report.toString())
        
        println("📊 Python Methods: ${pythonMethods.size}")
        println("📊 Java Methods: ${javaMethods.size}")
        println()
        println("⚠️  Missing in Java: ${inPythonNotJava.size}")
        if (criticalMissing.isNotEmpty()) {
            println("🔴 Critical missing: ${criticalMissing.size}")
            criticalMissing.forEach { println("   - $it") }
        }
        println()
        println("✅ Report saved to: API_COMPARISON.md")
        println("=" .repeat(60))
    }
}

/**
 * Pre-release verification checklist
 */
tasks.register("preReleaseCheck") {
    group = "verification"
    description = "Run pre-release checklist and verification"
    
    dependsOn("test", "build", "compareApis")
    
    doLast {
        println()
        println("=" .repeat(70))
        println("PRE-RELEASE CHECKLIST")
        println("=" .repeat(70))
        println()
        println("✅ Tests passed")
        println("✅ Build successful")
        println("✅ API comparison generated")
        println()
        println("📋 Manual Checks Required:")
        println("=" .repeat(70))
        println()
        println("□ Firmware updated to 25.2.1")
        println("□ Python reference code updated (./gradlew updateReferenceCode)")
        println("□ Reviewed API_COMPARISON.md for missing methods")
        println("□ All smoke tests pass with new firmware")
        println("□ Altitude offset documented")
        println("□ All examples tested and working")
        println("□ Documentation updated:")
        println("  □ CHANGELOG.md")
        println("  □ README.md")
        println("  □ Version numbers")
        println("□ Release notes created")
        println()
        println("📊 Next Steps:")
        println("=" .repeat(70))
        println()
        println("1. Review PRE_RELEASE_CHECKLIST.md")
        println("2. Check API_COMPARISON.md for missing methods")
        println("3. Test all examples: ./gradlew runSmokeTest")
        println("4. Update documentation")
        println("5. Create release tag")
        println()
        println("=" .repeat(70))
    }
}

// Helper functions for method name conversion
fun toCamelCase(snakeCase: String): String {
    return snakeCase.split("_").mapIndexed { index, s ->
        if (index == 0) s else s.replaceFirstChar { it.uppercase() }
    }.joinToString("")
}

fun toSnakeCase(camelCase: String): String {
    return camelCase.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}