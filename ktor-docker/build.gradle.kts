plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("com.zamolski.crkhello.app.MainKt")
}

dependencies {
    implementation(project(":ktor-app"))
}
