plugins {
    kotlin("jvm") version "2.2.0"
    id("com.google.cloud.tools.jib") version "3.4.5"
    application
}

application {
    mainClass.set("com.zamolski.crkhello.MainKt")
}

val isDockerBuild = gradle.startParameter.taskNames.any { it.contains("jibDockerBuild") }
val projectId: String? = if (!isDockerBuild) {
    System.getenv("PROJECT_ID")
        ?: findProperty("projectId") as? String
        ?: error("Missing project ID. Set env PROJECT_ID or use -PprojectId=...")
} else null
val imageName = if (!isDockerBuild)
    "gcr.io/$projectId/cloud-run-ktor-hello"
else
    "cloud-run-ktor-hello-local"

jib {
    from {
        image = "eclipse-temurin:21-jre"
    }
    to {
        image = imageName
        tags = setOf("latest")
    }
    container {
        ports = listOf("8080")
    }
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
