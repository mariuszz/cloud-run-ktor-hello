plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jib)
}


val isDockerBuild = gradle.startParameter.taskNames.any { it.contains("jibDockerBuild") }

val imageName: String? =
    if (!isDockerBuild)
        System.getProperty("jib.to.image", "jib.to.image-not-set")
    else
        "cloud-run-ktor-hello-local"

jib {
    from {
        image = "eclipse-temurin:21-jre"
    }
    to {
        image = imageName
        tags = System.getProperty("jib.to.tags", "latest").split(",").toSet()
    }
    container {
        mainClass = "com.zamolski.crkhello.app.MainKt"
        ports = listOf("8080")
        environment = mapOf(
            "BUILD_MODE" to "JIB",
            "PROJECT_ID" to providers.environmentVariable("PROJECT_ID").orElse("local").get()
        )
    }
}

dependencies {
    implementation(project(":ktor-app"))
}
