package pl.allegro.tech.hermes.frontend.publishing.metadata;

import java.util.Optional;

public class ProduceMetadata {
    private final String broker;

    public ProduceMetadata(String broker) {
        this.broker = broker;
    }

    public Optional<String> getBroker() {
        return Optional.ofNullable(broker);
    }

    public static ProduceMetadata empty() {
        return new ProduceMetadata(null);
    }
}
