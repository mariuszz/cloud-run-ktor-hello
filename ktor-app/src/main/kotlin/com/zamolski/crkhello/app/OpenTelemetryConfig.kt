package com.zamolski.crkhello.app

import com.google.cloud.opentelemetry.metric.GoogleCloudMetricExporter
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.semconv.ServiceAttributes
import org.slf4j.LoggerFactory
import java.time.Duration

object OpenTelemetryConfig {

    val logger = LoggerFactory.getLogger("cloud-run-ktor-hello")

    private var sdk: OpenTelemetrySdk? = null

    fun initialize(): OpenTelemetrySdk {
        if (sdk != null) return sdk!!

        val resource = Resource.builder()
            .put(ServiceAttributes.SERVICE_NAME, "cloud-run-ktor-hello")
            .put(ServiceAttributes.SERVICE_VERSION, "1.0.0")
            .build()

        val exporter = GoogleCloudMetricExporter.createWithDefaultConfiguration()

        val reader = PeriodicMetricReader.builder(exporter)
            .setInterval(Duration.ofSeconds(60))
            .build()

        val meterProvider = SdkMeterProvider.builder()
            .setResource(resource)
            .registerMetricReader(reader)
            .build()

        sdk = OpenTelemetrySdk.builder()
            .setMeterProvider(meterProvider)
            .buildAndRegisterGlobal()

        logger.info("🌐 Global OpenTelemetry initialized: $sdk")
        logger.info("🌐 Global MeterProvider: ${GlobalOpenTelemetry.get().meterProvider}")

        return sdk!!
    }

    fun shutdown() {
        sdk?.close()
    }
}