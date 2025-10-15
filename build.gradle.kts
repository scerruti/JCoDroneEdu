import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
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

group = "com.otabi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fazecast:jSerialComm:2.11.0")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("com.google.guava:guava:33.2.1-jre")
    
    // JSR 385 - Units of Measurement API for unit conversion
    implementation("tech.units:indriya:2.2")

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
        // Find the site-packages directory
        val codroneEduSource = File("$pythonVenvDir/lib/python3.12/site-packages/codrone_edu")
        
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