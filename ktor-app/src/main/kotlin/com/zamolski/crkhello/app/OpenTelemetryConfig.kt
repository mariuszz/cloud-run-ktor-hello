package com.zamolski.crkhello.app

import com.google.cloud.opentelemetry.metric.GoogleCloudMetricExporter
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader
import org.slf4j.LoggerFactory
import java.time.Duration

object OpenTelemetryConfig {

    val logger = LoggerFactory.getLogger("cloud-run-ktor-hello")

    private var sdk: OpenTelemetrySdk? = null

    fun initialize(): OpenTelemetrySdk {
        if (sdk != null) return sdk!!

        val exporter = GoogleCloudMetricExporter.createWithDefaultConfiguration()

        val reader = PeriodicMetricReader.builder(exporter)
            .setInterval(Duration.ofSeconds(30))
            .build()

        val meterProvider = SdkMeterProvider.builder()
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