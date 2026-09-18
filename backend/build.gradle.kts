plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    application
}

group = "com.football.teammanager"
version = "1.0.0"

application {
    mainClass.set("com.football.teammanager.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "3.0.3"
    val mongoVersion = "5.3.1"

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.mongodb:mongodb-driver-sync:$mongoVersion")
    implementation("ch.qos.logback:logback-classic:1.5.15")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}
