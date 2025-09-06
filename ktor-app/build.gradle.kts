plugins {
    alias(libs.plugins.kotlin.jvm)
}
dependencies {
    implementation(platform(libs.ktor.bom))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation(libs.slf4j.simple)
}