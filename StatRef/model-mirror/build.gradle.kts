plugins {
    id("java")
}

group = "labuzhskiy.valery.statref"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":model"))
}
