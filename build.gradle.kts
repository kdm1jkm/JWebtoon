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

dependencies {
    // jsoup HTML parser library @ https://jsoup.org/
    implementation("org.jsoup:jsoup:1.15.4")
}