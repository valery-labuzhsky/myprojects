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

    annotationProcessor("org.projectlombok:lombok:1.18.26")
    annotationProcessor(project(":processor"))

    compileOnly("org.projectlombok:lombok:1.18.26")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}