package pl.allegro.tech.hermes.consumers.consumer.receiver

import pl.allegro.tech.hermes.common.message.wrapper.SchemaOnlineChecksRateLimiter
import pl.allegro.tech.hermes.schema.CompiledSchema
import pl.allegro.tech.hermes.schema.CouldNotLoadSchemaException
import pl.allegro.tech.hermes.schema.SchemaId
import pl.allegro.tech.hermes.schema.SchemaNotFoundException
import pl.allegro.tech.hermes.schema.SchemaRepository
import pl.allegro.tech.hermes.schema.SchemaVersion
import pl.allegro.tech.hermes.schema.SchemaVersionDoesNotExistException
import pl.allegro.tech.hermes.test.helper.builder.TopicBuilder;
import spock.lang.Specification

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SchemaExistenceEnsurerTest extends Specification {
    SchemaRepository schemaRepository
    SchemaExistenceEnsurer schemaEnsurer
    SchemaOnlineChecksRateLimiter rateLimiter

    def setup() {
        schemaRepository = Mock(SchemaRepository)
        rateLimiter = Mock(SchemaOnlineChecksRateLimiter)
        schemaEnsurer = new SchemaExistenceEnsurer(schemaRepository, Duration.ofMillis(100), rateLimiter)
    }

    def "should retry pulling schema by version until it is successfully downloaded"() {
        given:
        def topic = TopicBuilder.topic("pl.allegro.someTestTopic").build()
        def compiledSchema = CompiledSchema.<String> of("schema", 1, 1)
        rateLimiter.tryAcquireOnlineCheckPermit() >> true

        when:
        schemaEnsurer.ensureSchemaExists(topic, SchemaVersion.valueOf(1))

        then:
        4 * schemaRepository.getAvroSchema(topic, SchemaVersion.valueOf(1)) >>
                { throw new SchemaNotFoundException(topic, SchemaVersion.valueOf(1)) } >>
                { throw new SchemaVersionDoesNotExistException(topic, SchemaVersion.valueOf(1)) } >>
                { throw new CouldNotLoadSchemaException(topic, SchemaVersion.valueOf(1), new RuntimeException()) } >>
                compiledSchema
        3 * schemaRepository.refreshVersions(topic)
    }

    def "should retry pulling schema by id until it is successfully downloaded"() {
        given:
        def topic = TopicBuilder.topic("pl.allegro.someTestTopic").build()
        def compiledSchema = CompiledSchema.<String> of("schema", 1, 1)
        rateLimiter.tryAcquireOnlineCheckPermit() >> true

        when:
        schemaEnsurer.ensureSchemaExists(topic, SchemaId.valueOf(1))

        then:
        3 * schemaRepository.getAvroSchema(topic, SchemaId.valueOf(1)) >>
                { throw new SchemaNotFoundException(SchemaId.valueOf(1)) } >>
                { throw new CouldNotLoadSchemaException(new RuntimeException()) } >>
                compiledSchema
    }

    def "should not pull online schema by version if it's already in cache"() {
        given:
        def topic = TopicBuilder.topic("pl.allegro.someTestTopic").build()
        def compiledSchema = CompiledSchema.<String> of("schema", 1, 1)
        rateLimiter.tryAcquireOnlineCheckPermit() >> true

        when:
        schemaEnsurer.ensureSchemaExists(topic, SchemaVersion.valueOf(1))

        then:
        1 * schemaRepository.getAvroSchema(topic, SchemaVersion.valueOf(1)) >> compiledSchema
        0 * schemaRepository.refreshVersions(topic)
    }

    def "should not pull online schema by id if it's already in cache"() {
        given:
        def topic = TopicBuilder.topic("pl.allegro.someTestTopic").build()
        def compiledSchema = CompiledSchema.<String> of("schema", 1, 1)
        rateLimiter.tryAcquireOnlineCheckPermit() >> true

        when:
        schemaEnsurer.ensureSchemaExists(topic, SchemaId.valueOf(1))

        then:
        1 * schemaRepository.getAvroSchema(topic, SchemaId.valueOf(1)) >> compiledSchema
    }

    def "should rate limit online schema pulls by version"() {
        given:
        def topic = TopicBuilder.topic("pl.allegro.someTestTopic").build()
        rateLimiter.tryAcquireOnlineCheckPermit() >> false

        when:
        ignoreException {
            Executors.newSingleThreadExecutor()
                    .submit({ schemaEnsurer.ensureSchemaExists(topic, SchemaVersion.valueOf(1)) })
                    .get(100, TimeUnit.MILLISECONDS)
        }
        then:
        0 * schemaRepository.getAvroSchema(topic, SchemaVersion.valueOf(1))
    }

    def "should rate limit online schema pulls by id"() {
        given:
        def topic = TopicBuilder.topic("pl.allegro.someTestTopic").build()
        rateLimiter.tryAcquireOnlineCheckPermit() >> false

        when:
        ignoreException {
            Executors.newSingleThreadExecutor()
                    .submit({ schemaEnsurer.ensureSchemaExists(topic, SchemaId.valueOf(1)) })
                    .get(100, TimeUnit.MILLISECONDS)
        }
        then:
        0 * schemaRepository.getAvroSchema(topic, SchemaId.valueOf(1))
    }

    void ignoreException(Closure<Object> actionThrowingException) {
        try {
            actionThrowingException.call()
        } catch (Exception ignored) {
        }
    }
}
