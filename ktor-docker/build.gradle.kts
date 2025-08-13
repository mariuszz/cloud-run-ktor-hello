plugins {
    kotlin("jvm") version "2.2.0"
    application
}

application {
    mainClass.set("com.zamolski.crkhello.MainKt")
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.2.3")
    implementation("io.ktor:ktor-server-netty:3.2.3")
    implementation("org.slf4j:slf4j-simple:2.0.17")
}
