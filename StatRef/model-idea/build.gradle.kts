plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "labuzhskiy.valery.statref"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java"))
}

dependencies {
    implementation(project(":model"))
    implementation(project(":model-builder"))
}