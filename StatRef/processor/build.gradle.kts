plugins {
    id("java")
}

group = "labuzhskiy.valery.statref"
version = "0.1.4"

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
