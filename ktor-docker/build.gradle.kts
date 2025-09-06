plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.10"
    application
}

application {
    mainClass.set("com.zamolski.crkhello.app.MainKt")
}

dependencies {
    implementation(project(":ktor-app"))
}
