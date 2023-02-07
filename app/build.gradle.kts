/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.6/userguide/building_java_projects.html
 */

// enable dependency locking, per https://docs.gradle.org/current/userguide/dependency_locking.html
configurations {
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}

// enable dependency locking for build plugins, per https://docs.gradle.org/current/userguide/dependency_locking.html
buildscript {
    configurations.classpath {
        resolutionStrategy.activateDependencyLocking()
    }
}

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // code formatting. ref: https://github.com/diffplug/spotless/tree/main/plugin-gradle#quickstart
    id("com.diffplug.spotless") version "6.14.1"

    // static analysis via Soptbugs. ref: https://plugins.gradle.org/plugin/com.github.spotbugs
    id("com.github.spotbugs") version "5.0.13"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {

    implementation("org.immutables:value:2.9.2")
    implementation("com.google.guava:guava:31.1-jre")

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}

application {
    // Define the main class for the application.
    mainClass.set("fintx.App")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

spotless {

    format("misd") {
        target("*.gradle", "*.md", ".gitignore")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    java {
        googleJavaFormat("1.9").aosp().reflowLongStrings()
        formatAnnotations()
    }
}
