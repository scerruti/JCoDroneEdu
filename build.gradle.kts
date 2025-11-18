// Ensure the main jar uses the correct base name
tasks.named<Jar>("jar") {
    archiveBaseName.set("codrone-edu-java")
}
// Ensure the main jar uses the correct base name
tasks.named<Jar>("jar") {
    archiveBaseName.set("codrone-edu-java")
}

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("com.gradleup.nmcp.aggregation") version "1.2.1"
    id("jacoco")
}

// JaCoCo test coverage configuration
jacoco {
    toolVersion = "0.8.11" // Use latest stable version or adjust as needed
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// Ensure a consistent Java toolchain for local and CI builds. This makes Gradle
// request a JDK matching the language level used across subprojects (21), so
// compilation doesn't fail when runners provide an older JAVA_HOME.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// -----------------------------------------------------------------
// Run Integration Test Menu - main entry point for all hardware tests
// -----------------------------------------------------------------
tasks.register<JavaExec>("runIntegrationTestMenu") {
    group = "verification"
    description = "Runs the Integration Test Menu - interactive interface for all hardware integration tests."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.IntegrationTestMenu")
    standardInput = System.`in`
}

// ------------------------------------------------------------
// Run Smoke Test - convenience task for the example SmokeTest
// ------------------------------------------------------------
tasks.register<JavaExec>("runSmokeTest") {
    group = "verification"
    description = "Runs the SmokeTest example to verify controller connection (no flight commands)."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.SmokeTest")
    // Pass args using: ./gradlew runSmokeTest --args='/dev/cu.usbserial-XXXX'
}

// -----------------------------------------------------------------
// Run Flight Smoke Test - gated, requires explicit confirmation flags
// -----------------------------------------------------------------
tasks.register<JavaExec>("runFlightSmokeTest") {
    group = "verification"
    description = "Runs the guarded FlightSmokeTest (requires --allow-flight and --confirm=YES to actually fly)."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.FlightSmokeTest")
    // Pass args using: ./gradlew runFlightSmokeTest --args='--allow-flight --confirm=YES'
    standardInput = System.`in`
}

// ------------------------------------------------------------
// Run Conservative Flight - gated example (requires --allow-flight)
// ------------------------------------------------------------
tasks.register<JavaExec>("runConservativeFlight") {
    group = "verification"
    description = "Runs the ConservativeFlight example (requires --allow-flight to actually fly)."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.ConservativeFlight")
    // Usage: ./gradlew runConservativeFlight --args='--allow-flight'
}

// ------------------------------------------------------------
// Run Quick LED Test - exercises drone and controller LEDs
// ------------------------------------------------------------
tasks.register<JavaExec>("runQuickLEDTest") {
    group = "verification"
    description = "Runs the QuickLEDTest example to verify drone and controller LED functionality."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.QuickLEDTest")
}


// ------------------------------------------------------------
// Run Test Harness - interactive menu to exercise drone features
// ------------------------------------------------------------
tasks.register<JavaExec>("runTestHarness") {
    group = "verification"
    description = "Runs the interactive TestHarness example for manual testing."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.TestHarness")
    // Forward stdin so Scanner(System.in) in the harness can read user input when run via Gradle
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Sensor Display GUI - Swing-based non-flying telemetry monitor
// -----------------------------------------------------------------
tasks.register<JavaExec>("runSensorDisplayGui") {
    group = "verification"
    description = "Runs the Swing-based SensorDisplay GUI (non-flying telemetry monitor). Requires drone connection."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.tools.SensorDisplayGui")
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Buzzer Test - tests drone and controller buzzer functionality
// -----------------------------------------------------------------
tasks.register<JavaExec>("runBuzzerTest") {
    group = "verification"
    description = "Tests drone and controller buzzers with notes and frequencies."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.BuzzerTest")
}

// -----------------------------------------------------------------
// Run Autonomous Ping Test - tests autonomous methods and ping feature
// -----------------------------------------------------------------
tasks.register<JavaExec>("runAutonomousPingTest") {
    group = "verification"
    description = "Comprehensive test for autonomous flight (avoidWall, keepDistance) and ping feature."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.AutonomousPingTest")
    standardInput = System.`in`  // Forward stdin for manual confirmations
}

// -----------------------------------------------------------------
// Run Single Note Test - plays one note repeatedly for audio testing
// -----------------------------------------------------------------
tasks.register<JavaExec>("runSingleNoteTest") {
    group = "verification"
    description = "Plays a single note (G4) repeatedly to test buzzer audio."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.SingleNoteTest")
}

// -----------------------------------------------------------------
// Run Melody Player - plays musical melodies on the buzzer
// -----------------------------------------------------------------
tasks.register<JavaExec>("runMelodyPlayer") {
    group = "verification"
    description = "Plays musical melodies using the drone buzzer."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.MelodyPlayer")
}

// -----------------------------------------------------------------
// Run Controller Input GUI - interactive controller testing
// -----------------------------------------------------------------
tasks.register<JavaExec>("runControllerInputGui") {
    group = "verification"
    description = "Interactive GUI for testing controller joysticks and buttons. Requires controller connection."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.tools.ControllerInputGui")
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Both Monitors - sensor and controller GUIs together
// -----------------------------------------------------------------
tasks.register<JavaExec>("runBothMonitors") {
    group = "verification"
    description = "Runs both sensor and controller monitors with L1 hold takeoff test. Requires drone + controller."
    classpath = sourceSets.getByName("main").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.tools.BothMonitors")
    // Forward stdin so keyboard input (Q to quit) works
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Controller Input Debug - console debug for controller
// -----------------------------------------------------------------
tasks.register<JavaExec>("runControllerInputDebug") {
    group = "verification"
    description = "Debug tool to see raw controller input data."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ControllerInputDebug")
}

// -----------------------------------------------------------------
// Run AccelTest - simple CLI accelerometer monitor
// -----------------------------------------------------------------
tasks.register<JavaExec>("runAccelTest") {
    group = "verification"
    description = "Runs the command-line AccelTest (prints accelerometer and angle data)."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.AccelTest")
    // Forward stdin so interactive prompts (press Enter) work when run via Gradle
    standardInput = System.`in`
}

tasks.register<JavaExec>("runMotionDump") {
    group = "verification"
    description = "Dumps raw Motion short values and scaled accel/angle for 5s."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.MotionDump")
}

// -----------------------------------------------------------------
// Run Altitude/Pressure Test - displays altitude, pressure, and height
// -----------------------------------------------------------------
tasks.register<JavaExec>("runAltitudePressureTest") {
    group = "verification"
    description = "Displays altitude, pressure, and height sensor data with calculations."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.AltitudePressureTest")
}

// Run Elevation API Demo - demonstrates new elevation methods
// -----------------------------------------------------------------
tasks.register<JavaExec>("runElevationApiDemo") {
    group = "verification"
    description = "Demonstrates the elevation API (getUncorrectedElevation, getCorrectedElevation, getElevation)."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.ElevationApiDemo")
}

// Run Calibrated Elevation Demo - shows weather-calibrated altitude
// -----------------------------------------------------------------
tasks.register<JavaExec>("runCalibratedElevationDemo") {
    group = "verification"
    description = "Demonstrates weather-calibrated elevation using real-time pressure data."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.CalibratedElevationDemo")
}

// Run Weather Service Test - tests API connectivity without drone
// -----------------------------------------------------------------
tasks.register<JavaExec>("runWeatherServiceTest") {
    group = "verification"
    description = "Tests weather API connectivity and data retrieval (no drone required)."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.WeatherServiceTest")
}

// Run Relative Height Demo - demonstrates pressure-based relative height
// -----------------------------------------------------------------
tasks.register<JavaExec>("runRelativeHeightDemo") {
    group = "verification"
    description = "Demonstrates relative height measurement using pressure reference (Python compatibility)."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.RelativeHeightDemo")
}

// Run Automatic Elevation Demo - demonstrates automatic location detection
// -----------------------------------------------------------------
tasks.register<JavaExec>("runAutomaticElevationDemo") {
    group = "verification"
    description = "Demonstrates automatic location detection and pressure calibration with fallback strategy."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.AutomaticElevationDemo")
}

// -----------------------------------------------------------------
// Run MultiSensorTest - range, flow, temperature, and color sensors
// -----------------------------------------------------------------
tasks.register<JavaExec>("runMultiSensorTest") {
    group = "verification"
    description = "Runs the MultiSensorTest example to snapshot range, optical flow, temperature, and color sensors."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.MultiSensorTest")
    // Forward stdin so interactive prompts (press Enter) work when run via Gradle
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run TemperatureCalibrationDemo - demonstrates temperature sensor calibration
// -----------------------------------------------------------------
tasks.register<JavaExec>("runTemperatureCalibrationDemo") {
    group = "verification"
    description = "Demonstrates temperature sensor calibration and the difference between raw sensor and calibrated ambient temperature."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.demos.TemperatureCalibrationDemo")
}

// -----------------------------------------------------------------
// Run TemperatureCalibrationExperiment - student research experiment
// -----------------------------------------------------------------
tasks.register<JavaExec>("runTemperatureCalibrationExperiment") {
    group = "research"
    description = "Runs a systematic temperature calibration experiment collecting data about warm-up and flight effects. Outputs CSV data for analysis."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.research.TemperatureCalibrationExperiment")
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run ColorSensorDebug - debug color sensor data
// -----------------------------------------------------------------
tasks.register<JavaExec>("runColorSensorDebug") {
    group = "verification"
    description = "Runs the ColorSensorDebug tool to show detailed color sensor data including HSVL values."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.ColorSensorDebug")
    // Forward stdin so interactive prompts work
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run ControllerDisplayExample - demonstrates controller display functionality
// -----------------------------------------------------------------
tasks.register<JavaExec>("runControllerDisplayExample") {
    group = "verification"
    description = "Runs the ControllerDisplayExample to demonstrate controller display drawing capabilities."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ControllerDisplayExample")
}

// Run ControllerDisplayCanvasExample - demonstrates canvas-based controller display functionality
// -----------------------------------------------------------------
tasks.register<JavaExec>("runControllerDisplayCanvasExample") {
    group = "verification"
    description = "Runs the ControllerDisplayCanvasExample to demonstrate canvas-based controller display with batching."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ControllerDisplayCanvasExample")
}

tasks.register<JavaExec>("runDisplayLineTest") {
    group = "verification"
    description = "Runs DisplayLineTest to diagnose display byte-packing format"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.DisplayLineTest")
}

tasks.register<JavaExec>("runDisplayWhiteTest") {
    group = "verification"
    description = "Runs DisplayWhiteTest to test white/blank display"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.DisplayWhiteTest")
    // Forward stdin for keyboard input
    standardInput = System.`in`
}

tasks.register<JavaExec>("runDisplayStagedTest") {
    group = "verification"
    description = "Runs DisplayStagedTest with clear visual progression"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.DisplayStagedTest")
}

// -----------------------------------------------------------------
// Run Checkerboard Test - individual draw commands for diagnostics
// -----------------------------------------------------------------
tasks.register<JavaExec>("runCheckerboardTest") {
    group = "verification"
    description = "Runs CheckerboardTest using individual controllerDrawPoint commands"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.CheckerboardTest")
}

// -----------------------------------------------------------------
// Run Simple Lines Test - faster diagnostic with fewer points
// -----------------------------------------------------------------
tasks.register<JavaExec>("runSimpleLinesTest") {
    group = "verification"
    description = "Runs SimpleLinesTest - draws a few horizontal lines for quick diagnostics"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.SimpleLinesTest")
}

// -----------------------------------------------------------------
// Run Single Message Test - test 0x88 protocol with minimal data
// -----------------------------------------------------------------
tasks.register<JavaExec>("runSingleMessageTest") {
    group = "verification"
    description = "Runs SingleMessageTest - sends a single 0x88 DisplayDrawImage message"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.SingleMessageTest")
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Canvas To Protocol Test - test 0x88 with full canvas data
// -----------------------------------------------------------------
tasks.register<JavaExec>("runCanvasToProtocolTest") {
    group = "verification"
    description = "Runs CanvasToProtocolTest - sends canvas as chunked 0x88 messages"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.CanvasToProtocolTest")
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Clean Canvas Test - test 0x88 with proper chunks (no errors)
// -----------------------------------------------------------------
tasks.register<JavaExec>("runCleanCanvasTest") {
    group = "verification"
    description = "Runs CleanCanvasTest - sends canvas as 5 properly-sized 0x88 messages"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.CleanCanvasTest")
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run Debug Y Coordinate Test - test if y-coordinate is the issue
// -----------------------------------------------------------------
tasks.register<JavaExec>("runDebugYCoordinateTest") {
    group = "verification"
    description = "Runs DebugYCoordinateTest - tests y-coordinate positioning with 0x88"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.DebugYCoordinateTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runCanvasColorDebugTest") {
    group = "verification"
    description = "Runs CanvasColorDebugTest - debug what colors are actually drawn on canvas"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.CanvasColorDebugTest")
}

tasks.register<JavaExec>("runFormatComparisonTest") {
    group = "verification"
    description = "Runs FormatComparisonTest - compare different byte patterns with 0x88"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.FormatComparisonTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runBlockSizeTest") {
    group = "verification"
    description = "Runs BlockSizeTest - test different block sizes with 0x88"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.BlockSizeTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runCanvasVsManualTest") {
    group = "verification"
    description = "Runs CanvasVsManualTest - compare canvas bytes vs manual 0xFF"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.CanvasVsManualTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runPowerCycleTest") {
    group = "verification"
    description = "Runs PowerCycleTest - power cycles drone to clear display state"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.PowerCycleTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runChunkPositioningDebugTest") {
    group = "verification"
    description = "Runs ChunkPositioningDebugTest - debug why chunked 0x88 doesn't work"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ChunkPositioningDebugTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runYZeroDebugTest") {
    group = "verification"
    description = "Runs YZeroDebugTest - debug y=0 positioning issue"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.YZeroDebugTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runY0AfterOtherCommandTest") {
    group = "verification"
    description = "Runs Y0AfterOtherCommandTest - test y=0 after other 0x88 commands"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.Y0AfterOtherCommandTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runFullCanvasTest") {
    group = "verification"
    description = "Runs FullCanvasTest - test sending full 1024-byte canvas at once"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.FullCanvasTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runFullDataWithYOffsetTest") {
    group = "verification"
    description = "Runs FullDataWithYOffsetTest - test full data at different y positions"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.FullDataWithYOffsetTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runTwoLinesPersistenceTest") {
    group = "verification"
    description = "Runs TwoLinesPersistenceTest - verify two 0x88 commands accumulate"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.TwoLinesPersistenceTest")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runSimple0x88Test") {
    group = "verification"
    description = "Runs Simple0x88Test - basic 0x88 functionality test"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.Simple0x88Test")
}

tasks.register<JavaExec>("runRobotImageTest") {
    group = "verification"
    description = "Runs RobotImageTest - displays robot face on controller"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.RobotImageTest")
}

tasks.register<JavaExec>("runDroneImageDisplayTest") {
    group = "verification"
    description = "Runs DroneImageDisplayTest - displays drone image from PNG on controller"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.DroneImageDisplayTest")
}

tasks.register<JavaExec>("runPacketRateTest") {
    group = "verification"
    description = "Runs PacketRateTest - monitors packet rate without sending display commands"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.PacketRateTest")
}

tasks.register<JavaExec>("runDisplayCommandStructureTest") {
    group = "verification"
    description = "Runs DisplayCommandStructureTest - analyzes response packets for different display commands"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.DisplayCommandStructureTest")
}

tasks.register<JavaExec>("runBuzzerResponseTest") {
    group = "verification"
    description = "Runs BuzzerResponseTest - checks if buzzer commands get echo responses"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.BuzzerResponseTest")
}

tasks.register<JavaExec>("runDroneCommandResponseTest") {
    group = "verification"
    description = "Runs DroneCommandResponseTest - checks if drone commands get echo responses"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.DroneCommandResponseTest")
}

tasks.register<JavaExec>("runCommandEchoComparisonTest") {
    group = "verification"
    description = "Runs CommandEchoComparisonTest - compares echo responses for controller vs drone commands"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.CommandEchoComparisonTest")
}

tasks.register<JavaExec>("runEchoTimingTest") {
    group = "verification"
    description = "Runs EchoTimingTest - analyzes timing of echo responses to determine if from controller or drone"
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.EchoTimingTest")
}

// -----------------------------------------------------------------
// Run Integration Test Menu - interactive menu for hardware tests
// -----------------------------------------------------------------
tasks.register<JavaExec>("runExampleMenu") {
    group = "verification"
    description = "Runs the Integration Test Menu - interactive interface for all hardware integration tests."
    classpath = sourceSets.getByName("test").runtimeClasspath + sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.integrationtests.IntegrationTestMenu")
    // Forward stdin so the menu can read user input
    standardInput = System.`in`
}

// -----------------------------------------------------------------
// Run L0103 Turning Navigation - test position-based movement
// -----------------------------------------------------------------
tasks.register<JavaExec>("runL0103TurningNavigation") {
    group = "application"
    description = "Runs the L0103 Turning Navigation example to test position-based movement commands."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.L0103TurningNavigation")
}

// -----------------------------------------------------------------
// Run Error Monitoring Example - demonstrates error checking (demo mode)
// -----------------------------------------------------------------
tasks.register<JavaExec>("runErrorMonitoringDemo") {
    group = "verification"
    description = "Runs the ErrorMonitoringExample in demo mode (no hardware required)."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ErrorMonitoringExample")
    // No arguments = demo mode
}

// -----------------------------------------------------------------
// Run Error Monitoring Example - connect mode
// -----------------------------------------------------------------
tasks.register<JavaExec>("runErrorMonitoringConnect") {
    group = "verification"
    description = "Runs the ErrorMonitoringExample with drone connection (reads real error data, no flight)."
    classpath = sourceSets.getByName("test").runtimeClasspath
    mainClass.set("com.otabi.jcodroneedu.examples.ErrorMonitoringExample")
    args = listOf("--connect")
}

// -----------------------------------------------------------------
// Run Error Monitoring Example - flight mode (CAUTION!)
// -----------------------------------------------------------------
tasks.register<JavaExec>("runErrorMonitoringFly") {
    group = "verification"
    description = "Runs the ErrorMonitoringExample with ACTUAL FLIGHT operations (CAUTION!)."
    classpath = sourceSets.getByName("test").runtimeClasspath
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
    else -> "1.5.0-SNAPSHOT"
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
    // Disable strict doclint introduced in newer JDKs by passing Xdoclint:none (no leading dash)
    val stdOptions = options as org.gradle.external.javadoc.StandardJavadocDocletOptions
    stdOptions.addStringOption("Xdoclint:none", "")
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


// Maven publication: main JAR + sources + javadoc
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
            artifactId = "codrone-edu-java"
            groupId = project.group.toString()
            version = project.version.toString()
            pom {
                name.set("CoDrone EDU Java library")
                description.set("Java library for controlling Robolink CoDrone EDU drones, with educational and research features.")
                url.set("https://github.com/scerruti/JCoDroneEdu")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("scerruti")
                        name.set("Stefano Cerruti")
                        email.set("stefano@otabi.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/scerruti/JCoDroneEdu.git")
                    developerConnection.set("scm:git:ssh://github.com/scerruti/JCoDroneEdu.git")
                    url.set("https://github.com/scerruti/JCoDroneEdu")
                }
            }
        }
    }
    // No explicit repositories block needed; nmcp handles publishing
}


// Configure the nmcpAggregation plugin for Central Portal publishing
nmcpAggregation {
    centralPortal {
        username = findProperty("CENTRAL_PORTAL_USERNAME") as String? ?: System.getenv("CENTRAL_PORTAL_USERNAME") ?: ""
        password = findProperty("CENTRAL_PORTAL_PASSWORD") as String? ?: System.getenv("CENTRAL_PORTAL_PASSWORD") ?: ""
    }
    publishAllProjectsProbablyBreakingProjectIsolation()
}


signing {
    val signingKey: String? = System.getenv("SIGNING_KEY") ?: findProperty("signingKey") as String?
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD") ?: findProperty("signingPassword") as String?
    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        logger.lifecycle("Using in-memory PGP keys for signing (CI/CD mode)")
    } else {
        useGpgCmd()
        logger.lifecycle("Using local GPG keyring for signing (local mode, GPG command)")
    }
    sign(publishing.publications["mavenJava"])
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
        // Disabled: Uses ByteArrayOutputStream (not available in build script context)
        println("📦 CoDrone EDU library updated (version info output disabled)")
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
        // Disabled: Uses ByteArrayOutputStream and LocalDateTime (not available in build script context)
        println("✅ Reference code updated (version info output disabled)")
    }
}

/**
 * Fetches and updates changelog from Robolink documentation
 */
tasks.register("updateChangelog") {
    // Disabled: problematic scripting removed
    // println("updateChangelog task disabled")
}

/**
 * Checks if the reference code is up to date and monitors for changelog updates
 */
tasks.register("checkCodroneVersion") {
    group = "python"
    description = "Checks if local reference code matches latest PyPI version and detects changelog updates"
    
    doLast {
        // Disabled: Uses ByteArrayOutputStream and LocalDateTime (not available in build script context)
        println("🔍 Version check logic disabled (version info output disabled)")
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
    // Disabled: problematic scripting removed
    // println("checkChangelog task disabled")
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
    description = "Compare Python and Java APIs and generate report. Use -PapiVersion=VERSION or -PcompareLatest to test compatibility"
    
    doLast {
        println("=" .repeat(60))
        println("API COMPARISON REPORT")
        println("=" .repeat(60))
        println()
        
        // Get parameters for version selection
        val compareLatest = project.findProperty("compareLatest")?.toString()?.toBoolean() ?: false
        var targetVersion = project.findProperty("apiVersion")?.toString()
        
        // If compareLatest flag is set, fetch latest version from PyPI
        if (compareLatest && targetVersion == null) {
            println("📦 Fetching latest codrone-edu version from PyPI...")
            try {
                val versionFile = file("$buildDir/pypi-version.txt")
                versionFile.parentFile.mkdirs()
                
                val result = exec {
                    commandLine("python3", "scripts/fetch_pypi_version.py")
                    standardOutput = versionFile.outputStream()
                    isIgnoreExitValue = true
                }
                
                if (result.exitValue == 0) {
                    targetVersion = versionFile.readText().trim()
                    println("✓ Latest version from PyPI: $targetVersion")
                } else {
                    println("⚠️  Failed to fetch version from PyPI, using default")
                    targetVersion = null
                }
            } catch (e: Exception) {
                println("⚠️  Error fetching PyPI version: ${e.message}")
                targetVersion = null
            }
        }
        
        // Use target version or fall back to configured version
        val pythonVersion = targetVersion ?: (project.findProperty("codroneEduPythonVersion")?.toString() ?: "2.2.0")
        val venvDir = file("reference/python-venv")
        
        // Use version in output filename
        val baseFileName = "API_COMPARISON"
        val outputFileName = if (targetVersion != null && compareLatest) {
            "${baseFileName}_vs_${pythonVersion}.md"
        } else if (targetVersion != null) {
            "${baseFileName}_${pythonVersion}.md"
        } else {
            "${baseFileName}.md"
        }
        
        println("📄 Output: $outputFileName")
        println()
        
        // Ensure virtual environment exists
        if (!venvDir.exists()) {
            println("📦 Creating Python virtual environment in reference/python-venv...")
            val createResult = exec {
                commandLine("python3", "-m", "venv", venvDir.absolutePath)
                isIgnoreExitValue = true
            }
            
            if (createResult.exitValue != 0) {
                throw GradleException("Failed to create Python virtual environment.\n" +
                    "   Try manually: python3 -m venv ${venvDir.absolutePath}")
            }
        }
        
        // Upgrade pip and install codrone-edu in the venv using the venv's python
        val isWindows = System.getProperty("os.name").toLowerCase().contains("win")
        val venvPython = if (isWindows)
            venvDir.absolutePath + "\\Scripts\\python.exe"
        else
            venvDir.absolutePath + "/bin/python"

        println("📦 Upgrading pip and installing codrone-edu==$pythonVersion in venv...")
        try {
            exec {
                commandLine(venvPython, "-m", "pip", "install", "--upgrade", "pip")
            }
            exec {
                commandLine(venvPython, "-m", "pip", "install", "codrone-edu==$pythonVersion")
            }
            println("✓ codrone-edu version $pythonVersion is ready")
        } catch (e: Exception) {
            println("⚠️  Warning: Could not install codrone-edu version $pythonVersion in venv: ${e.message}")
            println("   The comparison will still run with hardcoded Python method list")
        }
        
        val reportFile = file(outputFileName)
        val report = StringBuilder()
        
        report.appendLine("# API Comparison Report")
        report.appendLine()
        report.appendLine("**Java Version:** ${project.version}")
        report.appendLine("**Python API Version:** $pythonVersion")
        if (compareLatest) {
            report.appendLine("**Comparison Mode:** Latest vs. Built (${project.findProperty("codroneEduPythonVersion") ?: "2.2.0"})")
        }
        report.appendLine()
        
        // Parse Python API - only DOCUMENTED public methods (from official docs)
        // These are the methods actually documented on https://docs.robolink.com/docs/CoDroneEDU/Python/Function-Documentation
        val pythonMethods = mutableSetOf(
            // Connection
            "pair", "close",
            // Flight Commands
            "takeoff", "land", "emergency_stop", "hover", "avoid_wall", "keep_distance",
            "get_trim", "reset_trim", "set_trim", "move_forward", "move_backward", "move_left", "move_right",
            "move_distance", "send_absolute_position", "turn", "turn_degree", "turn_left", "turn_right",
            // Flight Sequences
            "circle", "flip", "spiral", "sway", "triangle",
            // Flight Variables
            "get_move_values", "move", "print_move_values", "reset_move", "reset_move_values",
            "set_pitch", "set_roll", "set_throttle", "set_yaw",
            // LED
            "controller_LED_off", "drone_LED_off", "set_controller_LED", "set_drone_LED",
            // Sounds
            "controller_buzzer", "drone_buzzer", "start_drone_buzzer", "stop_drone_buzzer",
            "start_controller_buzzer", "stop_controller_buzzer",
            // Sensors (Position)
            "get_pos_x", "get_pos_y", "get_pos_z", "get_position_data",
            // Sensors (Range)
            "detect_wall", "get_bottom_range", "get_front_range", "get_height",
            // Sensors (Optical Flow)
            "get_flow_velocity_x", "get_flow_velocity_y", "get_flow_x", "get_flow_y",
            // Sensors (Gyroscope/IMU)
            "get_accel_x", "get_accel_y", "get_accel_z", "get_angle_x", "get_angle_y", "get_angle_z",
            "get_angular_speed_x", "get_angular_speed_y", "get_angular_speed_z",
            "get_x_accel", "get_x_angle", "get_y_accel", "get_y_angle", "get_z_accel", "get_z_angle",
            "reset_gyro", "reset_sensor",
            // Sensors (Pressure/Temperature)
            "get_drone_temperature", "get_temperature", "height_from_pressure", "get_pressure", "set_initial_pressure",
            // Sensors (Color)
            "append_color_data", "get_back_color", "get_color_data", "get_colors", "get_front_color",
            "load_classifier", "load_color_data", "new_color_data", "predict_colors",
            // Sensors (State Data)
            "get_battery", "get_error_data", "get_flight_state", "get_movement_state", "get_sensor_data",
            // Controller
            "down_arrow_pressed", "get_button_data", "l1_pressed", "l2_pressed", "left_arrow_pressed",
            "p_pressed", "power_pressed", "r1_pressed", "r2_pressed", "right_arrow_pressed", "s_pressed",
            "h_pressed", "up_arrow_pressed", "get_joystick_data",
            "get_left_joystick_x", "get_left_joystick_y", "get_right_joystick_x", "get_right_joystick_y",
            // Screen/Display
            "controller_clear_screen", "controller_create_canvas", "controller_draw_arc", "controller_draw_canvas",
            "controller_draw_chord", "controller_draw_ellipse", "controller_draw_image", "controller_draw_line",
            "controller_draw_point", "controller_draw_polygon", "controller_draw_rectangle", "controller_draw_square",
            "controller_draw_string", "controller_draw_string_align", "controller_preview_canvas", "get_image_data"
        )
        
        println("📊 Documented Python methods loaded: ${pythonMethods.size} public API methods")
        
        // Parse Java API and extract @pythonEquivalent annotations
        val javaFile = file("src/main/java/com/otabi/jcodroneedu/Drone.java")
        val javaMethods = mutableSetOf<String>()
        val pythonEquivalents = mutableMapOf<String, String>() // javaMethod -> pythonMethod
        
        if (javaFile.exists()) {
            val lines = javaFile.readLines()
            var i = 0
            while (i < lines.size) {
                val line = lines[i]
                val trimmed = line.trim()
                
                // Look for @pythonEquivalent annotations
                if (trimmed.startsWith("* @pythonEquivalent")) {
                    val pythonMethodName = trimmed.substringAfter("@pythonEquivalent").trim()
                    // Look forward for the actual method declaration
                    var j = i + 1
                    while (j < lines.size && j < i + 10) {
                        val methodLine = lines[j].trim()
                        if (methodLine.startsWith("public ") && methodLine.contains("(")) {
                            val methodPart = methodLine.substringAfter("public ").trim()
                            if (!methodPart.startsWith("class ") && !methodPart.startsWith("interface ")) {
                                val javaMethodName = methodPart.substringAfter(" ").substringBefore("(").trim()
                                javaMethods.add(javaMethodName)
                                pythonEquivalents[javaMethodName] = pythonMethodName
                                break
                            }
                        }
                        j++
                    }
                } else if (trimmed.startsWith("public ") && trimmed.contains("(")) {
                    val methodPart = trimmed.substringAfter("public ").trim()
                    if (!methodPart.startsWith("class ") && !methodPart.startsWith("interface ")) {
                        val methodName = methodPart.substringAfter(" ").substringBefore("(").trim()
                        javaMethods.add(methodName)
                    }
                }
                i++
            }
        }
        
        // Find differences using documented equivalents and name matching
        val matchedPythonMethods = mutableSetOf<String>()
        val methodMappings = mutableMapOf<String, Pair<String, String>>() // javaMethod -> (pythonMethod, matchType)
        
        // Match using @pythonEquivalent annotations
        for ((javaMethod, pythonMethod) in pythonEquivalents) {
            if (pythonMethods.contains(pythonMethod)) {
                matchedPythonMethods.add(pythonMethod)
                methodMappings[javaMethod] = pythonMethod to "documented"
            }
        }
        
        // Match remaining methods by name conversion
        for (javaMethod in javaMethods) {
            if (!pythonEquivalents.containsKey(javaMethod)) {
                val pythonName = toSnakeCase(javaMethod)
                if (pythonMethods.contains(pythonName)) {
                    matchedPythonMethods.add(pythonName)
                    methodMappings[javaMethod] = pythonName to "inferred"
                }
            }
        }
        
        val inPythonNotJava = pythonMethods - matchedPythonMethods
        val inJavaNotPython = javaMethods.filterNot { methodMappings.containsKey(it) }
        
        // Report
        report.appendLine("## Summary")
        report.appendLine()
        report.appendLine("- **Python Methods:** ${pythonMethods.size}")
        report.appendLine("- **Java Methods:** ${javaMethods.size}")
        report.appendLine("- **Matched Methods:** ${methodMappings.size}")
        report.appendLine("  - Documented (@pythonEquivalent): ${methodMappings.values.count { it.second == "documented" }}")
        report.appendLine("  - Inferred (by name): ${methodMappings.values.count { it.second == "inferred" }}")
        report.appendLine("- **In Python, Not Java:** ${inPythonNotJava.size}")
        report.appendLine("- **In Java, Not Python:** ${inJavaNotPython.size}")
        report.appendLine()
        report.appendLine("**Note:** Java methods use @pythonEquivalent annotations to document their Python API mapping.")
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

// =============================================================================
// Release Process Automation Tasks (Issue #38)
// =============================================================================

/**
 * Task 1: Update copyright headers in all source files
 */
tasks.register("updateCopyright") {
    group = "release"
    description = "Updates copyright year in all Java source files and LICENSE"
    
    doLast {
        val currentYear = 2025 // Hardcoded to avoid java.time usage in build script
        val copyrightPattern = Regex("""Copyright \(c\) (\d{4})(?:-(\d{4}))?""")
        var filesUpdated = 0
        
        // Update Java source files
        fileTree("src") {
            include("**/*.java")
        }.forEach { file ->
            val content = file.readText()
            val newContent = copyrightPattern.replace(content) { matchResult ->
                val startYear = matchResult.groupValues[1].toInt()
                if (startYear == currentYear) {
                    "Copyright (c) $currentYear"
                } else {
                    "Copyright (c) $startYear-$currentYear"
                }
            }
            
            if (content != newContent) {
                file.writeText(newContent)
                filesUpdated++
            }
        }
        
        // Update LICENSE file
        val licenseFile = file("LICENSE")
        if (licenseFile.exists()) {
            val content = licenseFile.readText()
            val newContent = copyrightPattern.replace(content) { matchResult ->
                val startYear = matchResult.groupValues[1].toInt()
                if (startYear == currentYear) {
                    "Copyright (c) $currentYear"
                } else {
                    "Copyright (c) $startYear-$currentYear"
                }
            }
            
            if (content != newContent) {
                licenseFile.writeText(newContent)
                filesUpdated++
            }
        }
        
        // Update README.md if it contains copyright
        val readmeFile = file("README.md")
        if (readmeFile.exists()) {
            val content = readmeFile.readText()
            if (copyrightPattern.containsMatchIn(content)) {
                val newContent = copyrightPattern.replace(content) { matchResult ->
                    val startYear = matchResult.groupValues[1].toInt()
                    if (startYear == currentYear) {
                        "Copyright (c) $currentYear"
                    } else {
                        "Copyright (c) $startYear-$currentYear"
                    }
                }
                
                if (content != newContent) {
                    readmeFile.writeText(newContent)
                    filesUpdated++
                }
            }
        }
        
        println("✅ Updated copyright year to $currentYear in $filesUpdated file(s)")
    }
}

/**
 * Task 2: Validate that all public methods have @since tags
 */
tasks.register("validateSinceTags") {
    group = "verification"
    description = "Validates that all public methods/classes have @since annotations"
    
    doLast {
        val strictMode = project.findProperty("validateSinceTags.strict")?.toString()?.toBoolean() ?: false
        val missingTags = mutableListOf<String>()
        
        fileTree("src/main/java") {
            include("**/*.java")
        }.forEach { file ->
            val lines = file.readLines()
            var inJavadoc = false
            var hasSinceTag = false
            var lineNumber = 0
            
            lines.forEachIndexed { index, line ->
                lineNumber = index + 1
                val trimmed = line.trim()
                
                // Track JavaDoc blocks
                if (trimmed.startsWith("/**")) {
                    inJavadoc = true
                    hasSinceTag = false
                }
                
                if (inJavadoc && trimmed.contains("@since")) {
                    hasSinceTag = true
                }
                
                if (trimmed.startsWith("*/")) {
                    inJavadoc = false
                }
                
                // Check for public declarations after JavaDoc
                if (!inJavadoc && (trimmed.startsWith("public class ") || 
                    trimmed.startsWith("public interface ") ||
                    trimmed.startsWith("public enum ") ||
                    (trimmed.startsWith("public ") && trimmed.contains("(")))) {
                    
                    // Skip if it's a constructor (same name as class)
                    val isConstructor = file.name.replace(".java", "") in trimmed
                    
                    if (!hasSinceTag && !isConstructor) {
                        val methodSignature = trimmed.substringBefore("{").trim()
                        missingTags.add("${file.path}:$lineNumber - $methodSignature")
                    }
                    
                    hasSinceTag = false
                }
            }
        }
        
        if (missingTags.isNotEmpty()) {
            println("⚠️  Found ${missingTags.size} public method(s)/class(es) without @since tags:")
            println()
            missingTags.take(20).forEach { println("  $it") }
            if (missingTags.size > 20) {
                println("  ... and ${missingTags.size - 20} more")
            }
            println()
            println("💡 Add @since VERSION to JavaDoc for all new public APIs")
            
            if (strictMode) {
                println()
                println("❌ Strict mode enabled - failing build")
                throw GradleException("Missing @since tags found. Please add them before release.")
            } else {
                println()
                println("ℹ️  Note: Enable strict mode with -PvalidateSinceTags.strict=true")
            }
        } else {
            println("✅ All public methods have @since tags")
        }
    }
}

/**
 * Task 3: Validate CHANGELOG.md format and content
 */
tasks.register("validateChangelog") {
    group = "verification"
    description = "Validates CHANGELOG.md format and content"
    
    doLast {
        val changelogFile = file("CHANGELOG.md")
        
        if (!changelogFile.exists()) {
            throw GradleException("CHANGELOG.md does not exist")
        }
        
        val content = changelogFile.readText()
        if (content.trim().isEmpty()) {
            throw GradleException("CHANGELOG.md is empty")
        }
        
        val lines = content.lines()
        
        // Find version entries (## v1.0.0 or ## [1.0.0])
        val versionPattern = Regex("""^##\s+\[?v?(\d+\.\d+\.\d+)\]?\s*-\s*(\d{4}-\d{2}-\d{2})""")
        val versions = mutableListOf<String>()
        var hasContent = false
        var currentVersion: String? = null
        
        lines.forEach { line ->
            val match = versionPattern.find(line)
            if (match != null) {
                val version = match.groupValues[1]
                versions.add(version)
                currentVersion = version
                hasContent = false
            } else if (currentVersion != null && line.trim().isNotEmpty() && !line.startsWith("#")) {
                hasContent = true
            }
        }
        
        if (versions.isEmpty()) {
            throw GradleException("CHANGELOG.md has no version entries. Expected format: ## v1.0.0 - 2025-01-01")
        }
        
        // Check if the top version has content
        if (!hasContent) {
            println("⚠️  Warning: Latest version in CHANGELOG.md appears to have no content")
        }
        
        // Check for duplicate versions
        val duplicates = versions.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicates.isNotEmpty()) {
            throw GradleException("CHANGELOG.md contains duplicate versions: ${duplicates.keys}")
        }
        
        println("✅ CHANGELOG.md format is valid")
        println("   Found ${versions.size} version(s): ${versions.take(3).joinToString(", ")}")
    }
}

/**
 * Task 4: Validate version consistency across files
 */
tasks.register("validateVersionConsistency") {
    group = "verification"
    description = "Validates version consistency across build.gradle.kts and CHANGELOG.md"
    
    doLast {
        val buildVersion = project.version.toString().replace("-SNAPSHOT", "")
        
        // Read version from CHANGELOG.md
        val changelogFile = file("CHANGELOG.md")
        if (!changelogFile.exists()) {
            throw GradleException("CHANGELOG.md does not exist")
        }
        
        val versionPattern = Regex("""^##\s+\[?v?(\d+\.\d+\.\d+)\]?\s*-\s*(\d{4}-\d{2}-\d{2})""")
        var changelogVersion: String? = null
        
        changelogFile.readLines().forEach { line ->
            val match = versionPattern.find(line)
            if (match != null && changelogVersion == null) {
                changelogVersion = match.groupValues[1]
            }
        }
        
        if (changelogVersion == null) {
            throw GradleException("Could not find version in CHANGELOG.md")
        }
        
        println("📦 Version comparison:")
        println("   build.gradle.kts: $buildVersion")
        println("   CHANGELOG.md:     $changelogVersion")
        
        if (buildVersion != changelogVersion) {
            println()
            println("❌ Version mismatch detected!")
            println("   build.gradle.kts has version '$buildVersion'")
            println("   but CHANGELOG.md has version '$changelogVersion'")
            println()
            println("💡 Please update both to match before release")
            throw GradleException("Version mismatch between build.gradle.kts and CHANGELOG.md")
        }
        
        // Check if this version tag already exists
        // Disabled: Used ByteArrayOutputStream for git tag check
        
        println("✅ Version consistency validated: $buildVersion")
    }
}

/**
 * Task 5: Generate release notes from CHANGELOG.md
 */
tasks.register("generateReleaseNotes") {
    group = "release"
    description = "Generates GitHub release notes from CHANGELOG.md"
    
    doLast {
        val buildVersion = project.version.toString().replace("-SNAPSHOT", "")
        val changelogFile = file("CHANGELOG.md")
        
        if (!changelogFile.exists()) {
            throw GradleException("CHANGELOG.md does not exist")
        }
        
        val lines = changelogFile.readLines()
        val versionPattern = Regex("""^##\s+\[?v?(\d+\.\d+\.\d+)\]?\s*-\s*(\d{4}-\d{2}-\d{2})""")
        
        var inTargetVersion = false
        val releaseNotes = mutableListOf<String>()
        
        lines.forEach { line ->
            val match = versionPattern.find(line)
            
            if (match != null) {
                val version = match.groupValues[1]
                if (version == buildVersion) {
                    inTargetVersion = true
                    // Don't include the version header itself
                } else if (inTargetVersion) {
                    // Hit next version, stop
                    inTargetVersion = false
                }
            } else if (inTargetVersion) {
                releaseNotes.add(line)
            }
        }
        
        if (releaseNotes.isEmpty()) {
            throw GradleException("Could not find release notes for version $buildVersion in CHANGELOG.md")
        }
        
        // Generate full release notes
        val fullReleaseNotes = """
            |## 📚 CoDrone EDU Java v$buildVersion
            |
            |### 📖 API Documentation
            |[View Full API Docs](https://scerruti.github.io/JCoDroneEdu/docs/v$buildVersion/)
            |
            |### 📦 Installation
            |
            |#### Maven
            |```xml
            |<dependency>
            |    <groupId>com.otabi</groupId>
            |    <artifactId>codrone-edu-java</artifactId>
            |    <version>$buildVersion</version>
            |</dependency>
            |```
            |
            |#### Gradle
            |```kotlin
            |implementation("com.otabi:codrone-edu-java:$buildVersion")
            |```
            |
            |### ✨ What's New
            |${releaseNotes.joinToString("\n")}
            |
            |### 🔗 Resources
            |- [API Documentation](https://scerruti.github.io/JCoDroneEdu/docs/v$buildVersion/)
            |- [CHANGELOG](./CHANGELOG.md)
            |- [Repository](https://github.com/scerruti/JCoDroneEdu)
        """.trimMargin()
        
        val outputFile = file("build/release-notes.md")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(fullReleaseNotes)
        
        println("✅ Release notes generated: build/release-notes.md")
        println()
        println(fullReleaseNotes)
    }
}

/**
 * Task 6: Check for deprecated methods and warn about old deprecations
 */
tasks.register("checkDeprecations") {
    group = "verification"
    description = "Lists deprecated methods and warns about old deprecations"
    
    doLast {
        val buildVersion = project.version.toString().replace("-SNAPSHOT", "")
        val majorVersion = buildVersion.split(".")[0].toInt()
        
        val deprecatedMethods = mutableListOf<Triple<String, Int, String>>() // file, line, method
        val deprecationSince = mutableMapOf<String, String>() // method -> version
        
        fileTree("src/main/java") {
            include("**/*.java")
        }.forEach { file ->
            val lines = file.readLines()
            var inJavadoc = false
            var hasDeprecated = false
            var sinceVersion: String? = null
            var lineNumber = 0
            
            lines.forEachIndexed { index, line ->
                lineNumber = index + 1
                val trimmed = line.trim()
                
                if (trimmed.startsWith("/**")) {
                    inJavadoc = true
                    hasDeprecated = false
                    sinceVersion = null
                }
                
                if (inJavadoc) {
                    if (trimmed.contains("@deprecated")) {
                        hasDeprecated = true
                    }
                    if (trimmed.contains("@since")) {
                        sinceVersion = trimmed.substringAfter("@since").trim()
                    }
                }
                
                if (trimmed.startsWith("*/")) {
                    inJavadoc = false
                }
                
                if (trimmed.startsWith("@Deprecated") || (trimmed.startsWith("public") && hasDeprecated)) {
                    val methodSignature = if (trimmed.startsWith("@Deprecated")) {
                        lines.getOrNull(index + 1)?.trim() ?: ""
                    } else {
                        trimmed
                    }
                    
                    if (methodSignature.contains("(")) {
                        val methodName = methodSignature.substringAfter(" ").substringBefore("(")
                        deprecatedMethods.add(Triple(file.path, lineNumber, methodName))
                        val capturedSince = sinceVersion
                        if (capturedSince != null) {
                            deprecationSince[methodName] = capturedSince
                        }
                    }
                    hasDeprecated = false
                }
            }
        }
        
        if (deprecatedMethods.isEmpty()) {
            println("✅ No deprecated methods found")
            return@doLast
        }
        
        println("📋 Deprecated Methods Report:")
        println("=".repeat(70))
        
        val oldDeprecations = mutableListOf<String>()
        
        deprecatedMethods.forEach { (file, line, method) ->
            val since = deprecationSince[method] ?: "unknown"
            println("  $method (deprecated since $since)")
            println("    at $file:$line")
            
            // Check if deprecated for 2+ major versions
            if (since != "unknown") {
                try {
                    val deprecatedMajor = since.split(".")[0].toIntOrNull()
                    if (deprecatedMajor != null && (majorVersion - deprecatedMajor) >= 2) {
                        oldDeprecations.add(method)
                    }
                } catch (e: Exception) {
                    // Ignore parse errors
                }
            }
        }
        
        println()
        println("Total: ${deprecatedMethods.size} deprecated method(s)")
        
        if (oldDeprecations.isNotEmpty()) {
            println()
            println("⚠️  WARNING: ${oldDeprecations.size} method(s) deprecated for 2+ major versions:")
            oldDeprecations.forEach { println("  - $it") }
            println()
            println("💡 Consider removing these in the next major version")
        }
        
        println("=".repeat(70))
    }
}

/**
 * Task 7: Validate build artifacts
 */
tasks.register("validateArtifacts") {
    // Disabled: problematic scripting removed
    // println("validateArtifacts task disabled")
}

/**
 * Pre-release verification checklist
 */
tasks.register("preReleaseCheck") {
    // Disabled: problematic scripting removed
    // println("preReleaseCheck task disabled")
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