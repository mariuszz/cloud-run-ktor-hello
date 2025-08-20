plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jib)
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

dependencies {
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.slf4j.simple)
}
