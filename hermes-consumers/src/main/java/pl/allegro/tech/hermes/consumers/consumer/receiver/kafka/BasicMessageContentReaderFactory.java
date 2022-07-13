package pl.allegro.tech.hermes.consumers.consumer.receiver.kafka;

import java.time.Duration;
import pl.allegro.tech.hermes.api.Topic;
import pl.allegro.tech.hermes.common.config.ConfigFactory;
import pl.allegro.tech.hermes.common.config.Configs;
import pl.allegro.tech.hermes.common.message.wrapper.CompositeMessageContentWrapper;
import pl.allegro.tech.hermes.common.message.wrapper.SchemaOnlineChecksRateLimiter;
import pl.allegro.tech.hermes.consumers.consumer.receiver.SchemaExistenceEnsurer;
import pl.allegro.tech.hermes.schema.SchemaRepository;

public class BasicMessageContentReaderFactory implements MessageContentReaderFactory {

    private final CompositeMessageContentWrapper compositeMessageContentWrapper;
    private final KafkaHeaderExtractor kafkaHeaderExtractor;
    private final SchemaRepository schemaRepository;
    private final SchemaOnlineChecksRateLimiter rateLimiter;

    public BasicMessageContentReaderFactory(CompositeMessageContentWrapper compositeMessageContentWrapper,
                                            KafkaHeaderExtractor kafkaHeaderExtractor, SchemaRepository schemaRepository,
                                            ConfigFactory configFactory, SchemaOnlineChecksRateLimiter rateLimiter) {
        this.compositeMessageContentWrapper = compositeMessageContentWrapper;
        this.kafkaHeaderExtractor = kafkaHeaderExtractor;
        this.schemaRepository = schemaRepository;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public MessageContentReader provide(Topic topic) {
        SchemaExistenceEnsurer schemaExistenceEnsurer = new SchemaExistenceEnsurer(schemaRepository, rateLimiter);
        return new BasicMessageContentReader(compositeMessageContentWrapper, kafkaHeaderExtractor, topic, schemaExistenceEnsurer);
    }
}
