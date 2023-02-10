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

    jacoco
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    val picoliVersion = "4.7.1" // command line parsing: https://picocli.info
    val immutablesVersion = "2.9.2"

    implementation("info.picocli:picocli:${picoliVersion}")
    implementation("org.immutables:value:${immutablesVersion}")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.opencsv:opencsv:5.7.1")
    implementation("org.apache.commons:commons-text:1.10.0")

    annotationProcessor("info.picocli:picocli:${picoliVersion}")
    annotationProcessor("org.immutables:value:${immutablesVersion}")

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

    // report is always generated after tests run
    // ref: https://docs.gradle.org/current/userguide/jacoco_plugin.html
    finalizedBy(tasks.jacocoTestReport)
}

spotless {

    format("misc") {
        target("*.gradle", "*.md", ".gitignore")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    java {
        googleJavaFormat("1.15.0").aosp().reflowLongStrings()
        formatAnnotations()
    }
}

afterEvaluate {
    // automatically apply code formatting during the build
    // ref: https://stackoverflow.com/questions/40432291/gradle-spotless-task-not-firing-when-needed
    tasks.getByName("spotlessCheck").dependsOn(tasks.getByName("spotlessApply"))
}

spotbugs {
    excludeFilter.set(project.file("spotbugs-exclude.xml"))
}
