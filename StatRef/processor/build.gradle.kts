plugins {
    id("java")
}

group = "labuzhskiy.valery.statref"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":client"))
    implementation(project(":model"))
    implementation(project(":model-builder"))
    implementation(project(":writer"))
    implementation(project(":model-mirror"))
}
