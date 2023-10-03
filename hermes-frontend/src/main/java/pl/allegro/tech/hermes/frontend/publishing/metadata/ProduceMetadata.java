package pl.allegro.tech.hermes.frontend.publishing.metadata;

import java.util.Optional;

public class ProduceMetadata {
    private final Optional<String> broker;

    public ProduceMetadata(Optional<String> broker) {
        this.broker = broker;
    }

    public Optional<String> getBroker() {
        return broker;
    }

    public static ProduceMetadata empty() {
        return new ProduceMetadata(Optional.empty());
    }
}
