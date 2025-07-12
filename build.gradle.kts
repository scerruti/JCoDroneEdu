import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
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

    // JUnit 5 (Jupiter) for testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")

    runtimeOnly("org.apache.logging.log4j:log4j-core:2.23.1")
}

tasks.test {
    useJUnitPlatform()
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
        for header in version_headers[:5]