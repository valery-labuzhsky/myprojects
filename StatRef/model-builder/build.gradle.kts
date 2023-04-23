plugins {
    id("java")
}

group = "labuzhskiy.valery.statref"
version = "0.1.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":model"))
    implementation(project(":writer"))
}
