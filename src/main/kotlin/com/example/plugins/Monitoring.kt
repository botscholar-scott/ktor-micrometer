package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.*
import java.time.Duration

val UseHistograms: Boolean = false

fun Application.configureMonitoring() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
    if (UseHistograms) {
        setupHistograms(appMicrometerRegistry)
    }
    routing {
        get("/metrics") {
                call.respondText(
                    appMicrometerRegistry.scrape(),
                    ContentType.Text.Plain,
                    HttpStatusCode.OK
                )
        }
    }
}

fun setupHistograms(metricsRegistry: PrometheusMeterRegistry) {
    metricsRegistry.config().meterFilter(
        object : MeterFilter {
            override fun configure(id: Meter.Id, config: DistributionStatisticConfig): DistributionStatisticConfig =
                DistributionStatisticConfig.builder()
                    .percentilesHistogram(true)
                    .expiry(Duration.ofSeconds(15))
                    .build()
                    .merge(config)
        }
    )
}
