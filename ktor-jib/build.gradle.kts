plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jib)
    application
}

application {
    mainClass.set("com.zamolski.crkhello.MainKt")
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
        ports = listOf("8080")
    }
}

dependencies {
    implementation(platform(libs.ktor.bom))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation(libs.slf4j.simple)
}
