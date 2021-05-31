import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
}

group = "petercrawley"
version = "0.0.1"

repositories {
    mavenCentral()

    maven(url = "https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")

    implementation("net.dv8tion:JDA:4.2.1_253")

    implementation("org.reflections:reflections:0.9.12")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "13"
}