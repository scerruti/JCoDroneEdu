plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "com.otabi"
version = project.findProperty("version") ?: "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // Depend on the published core JCoDroneEdu library from Maven Central
    // This teaches students about external dependencies
    api("com.otabi:jcodroneedu-core:${version}")
    
    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    options {
        (this as StandardJavadocDocletOptions).apply {
            addStringOption("Xdoclint:none", "-quiet")
            addBooleanOption("html5", true)
            encoding = "UTF-8"
            charSet = "UTF-8"
            addBooleanOption("author", true)
            addBooleanOption("version", true)
            addBooleanOption("use", true)
            windowTitle = "JCoDroneEdu Flight Patterns ${project.version}"
            docTitle = "JCoDroneEdu Flight Patterns API"
            bottom = """
                <div style="text-align: center; font-size: 12px; color: #666;">
                    <p>Educational drone programming library for CoDrone EDU</p>
                    <p>Created by Stephen Cerruti with AI assistance from GitHub Copilot</p>
                </div>
            """.trimIndent()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            artifactId = "jcodroneedu-flight-patterns"
            
            pom {
                name.set("JCoDroneEdu Flight Patterns")
                description.set("Educational flight patterns library with full source code - learn dependency management and study implementations for CoDrone EDU")
                url.set("https://github.com/scerruti/JCoDroneEdu-FlightPatterns")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("scerruti")
                        name.set("Stephen Cerruti")
                        email.set("scerruti@example.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/scerruti/JCoDroneEdu-FlightPatterns.git")
                    developerConnection.set("scm:git:ssh://github.com/scerruti/JCoDroneEdu-FlightPatterns.git")
                    url.set("https://github.com/scerruti/JCoDroneEdu-FlightPatterns")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: ""
                password = project.findProperty("ossrhPassword") as String? ?: ""
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
