import java.util.Properties

plugins {
    `kotlin-dsl`
    kotlin("jvm") version embeddedKotlinVersion
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.31.4")
}
