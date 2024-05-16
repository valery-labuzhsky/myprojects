plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.0"
}

group = "labuzhskiy.valery.statref"
version = "0.1.5"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("241.14494.240")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java"))
}

dependencies {
    implementation(project(":model"))
    implementation(project(":model-builder"))
}