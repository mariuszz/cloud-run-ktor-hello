plugins {
    alias(libs.plugins.kotlin.jvm)
}
dependencies {
    implementation(platform(libs.ktor.bom))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-call-logging")
    implementation(libs.slf4j.api)
    implementation(libs.logback)
    implementation(libs.logback.logstash.encoder)
    implementation(platform(libs.opentelemetry.bom))
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")
    implementation("io.opentelemetry:opentelemetry-sdk-metrics")
    implementation(libs.opentelemetry.semconv)
    implementation(libs.opentelemetry.google.cloud)
}