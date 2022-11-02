plugins {
    kotlin("jvm")
    `java-library`
}

group = "codes.chizl.tools.testbuilder"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}