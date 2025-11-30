package com.zamolski.crkhello.app

import com.google.cloud.opentelemetry.metric.GoogleCloudMetricExporter
import io.opentelemetry.exporter.logging.LoggingMetricExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.export.MetricExporter
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.semconv.ServiceAttributes
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

object OpenTelemetryConfig {

    val serviceName = System.getenv("SERVICE_NAME") ?: "cloud-run-ktor-hello"
    val serviceVersion = System.getenv("SERVICE_VERSION") ?: "1.0.0"
    val metricsInterval = System.getenv("METRICS_INTERVAL_SECONDS")
        ?.toLongOrNull()
        ?.coerceIn(1L, 3600L)
        ?: 60L

    private val logger = LoggerFactory.getLogger("cloud-run-ktor-hello")
    private val initLock = Any()

    private var sdk: OpenTelemetrySdk? = null
    private var metricReader: PeriodicMetricReader? = null
    private var exporter: MetricExporter? = null

    fun initialize(): OpenTelemetrySdk = synchronized(initLock) {
        sdk?.let { return it }

        val resource = Resource.getDefault()
            .merge(
                Resource.builder()
                    .put(ServiceAttributes.SERVICE_NAME, serviceName)
                    .put(ServiceAttributes.SERVICE_VERSION, serviceVersion)
                    .build()
            )

        val gcpExporter = try {
            GoogleCloudMetricExporter.createWithDefaultConfiguration().also {
                logger.info("✅ Using GoogleCloudMetricExporter")
            }
        } catch (ex: Exception) {
            logger.warn("⚠️ GCP exporter unavailable (${ex.message}), using LoggingMetricExporter")
            LoggingMetricExporter.create()
        }

        val reader = PeriodicMetricReader.builder(gcpExporter)
            .setInterval(Duration.ofSeconds(metricsInterval))
            .build()

        val meterProvider = SdkMeterProvider.builder()
            .setResource(resource)
            .registerMetricReader(reader)
            .build()

        try {
            // only metrics are exported, no traces
            sdk = OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .buildAndRegisterGlobal()
            metricReader = reader
            exporter = gcpExporter
        } catch (ex: Exception) {
            runCatching { reader.shutdown() }
            runCatching { gcpExporter.shutdown() }
            throw ex
        }

        logger.info(
            "OpenTelemetry initialized: service=$serviceName, version=$serviceVersion, " +
                    "exporter=${gcpExporter.javaClass.simpleName}, interval=${metricsInterval}s"
        )

        return sdk!!
    }

    fun shutdown() {
        val currentSdk = sdk ?: return

        try {
            val flushSuccess = metricReader?.forceFlush()?.join(10, TimeUnit.SECONDS) ?: false
            if (flushSuccess != true) logger.warn("MetricReader forceFlush timed out")

            val shutdownSuccess = metricReader?.shutdown()?.join(10, TimeUnit.SECONDS) ?: false
            if (shutdownSuccess != true) logger.warn("MetricReader shutdown timed out")

            val exporterShutdown = exporter?.shutdown()?.join(10, TimeUnit.SECONDS) ?: false
            if (exporterShutdown != true) logger.warn("MetricExporter shutdown timed out")

            currentSdk.close()
            logger.info("OpenTelemetry shut down successfully")
        } catch (e: Exception) {
            logger.error("Error during OpenTelemetry shutdown", e)
        } finally {
            sdk = null
            metricReader = null
            exporter = null
        }
    }
}