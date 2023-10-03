package pl.allegro.tech.hermes.frontend.producer;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.tech.hermes.common.metric.MetricsFacade;
import pl.allegro.tech.hermes.frontend.publishing.message.Message;
import pl.allegro.tech.hermes.frontend.publishing.metadata.ProduceMetadata;
import pl.allegro.tech.hermes.metrics.HermesTimerContext;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

public class BrokerLatencyReporter {

    private static final Logger logger = LoggerFactory.getLogger(BrokerLatencyReporter.class);

    private final boolean perBrokerLatencyEnabled;
    private final MetricsFacade metricsFacade;
    private final Duration slowResponseThreshold;

    public BrokerLatencyReporter(boolean perBrokerLatencyEnabled,
                                 MetricsFacade metricsFacade,
                                 Duration slowResponseThreshold) {
        this.perBrokerLatencyEnabled = perBrokerLatencyEnabled;
        this.metricsFacade = metricsFacade;
        this.slowResponseThreshold = slowResponseThreshold;
    }

    public void report(Message message, @Nullable Supplier<ProduceMetadata> produceMetadata, HermesTimerContext timerContext) {
        Duration duration = timerContext.closeAndGet();
        if (!perBrokerLatencyEnabled) {
            return;
        }

        String broker = Optional.ofNullable(produceMetadata).flatMap(metadata -> metadata.get().getBroker()).orElse("unknown");

        if (duration.compareTo(slowResponseThreshold) > 0) {
            logger.info("Slow produce request, broker response time: {} ms, messageId: {}, broker: {}",
                    duration.toMillis(), message.getId(), broker);
        }

        metricsFacade.broker().recordBrokerLatency(broker, duration);
    }
}
