plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.github.kdm1jkm"
version = "1.0.0"

repositories {
    mavenCentral()
}



kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}