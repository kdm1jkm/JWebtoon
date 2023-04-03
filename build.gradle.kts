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
    jvmToolchain(18)
}

application {
    mainClass.set("MainKt")
}

dependencies {
    // jsoup HTML parser library @ https://jsoup.org/
    implementation("org.jsoup:jsoup:1.15.4")

    // https://mvnrepository.com/artifact/com.microsoft.playwright/playwright
    implementation("com.microsoft.playwright:playwright:1.32.0")
}