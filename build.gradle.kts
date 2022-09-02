import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-cio:2.1.0")
    implementation("io.ktor:ktor-client-auth:2.1.0")
    implementation("io.ktor:ktor-client-websockets:2.1.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}