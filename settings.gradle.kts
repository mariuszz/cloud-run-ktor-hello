dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "cloud-run-ktor-hello"
include("ktor-docker")
include("ktor-jib")