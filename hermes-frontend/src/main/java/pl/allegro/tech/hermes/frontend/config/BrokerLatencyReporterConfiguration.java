package pl.allegro.tech.hermes.frontend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.tech.hermes.common.metric.MetricsFacade;
import pl.allegro.tech.hermes.frontend.producer.BrokerLatencyReporter;


@Configuration
@EnableConfigurationProperties(BrokerLatencyReporterProperties.class)
public class BrokerLatencyReporterConfiguration {

    @Bean
    BrokerLatencyReporter brokerLatencyReporter(BrokerLatencyReporterProperties properties,
                                                MetricsFacade metricsFacade) {
        return new BrokerLatencyReporter(
                properties.isEnabled(),
                metricsFacade,
                properties.getSlowResponseLoggingThreshold()
        );
    }
}
