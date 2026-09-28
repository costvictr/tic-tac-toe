plugins {
    kotlin("jvm") version "2.4.20"
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jline:jline-terminal-ffm:4.4.5")
}

kotlin {
    jvmToolchain(26)
}
