package pl.allegro.tech.hermes.common.metric.counter.zookeeper;

import io.micrometer.core.instrument.Counter;
import java.util.Optional;
import pl.allegro.tech.hermes.api.TopicName;

class CounterMatcher {

    private static final String GROUP_TAG_NAME = "group";
    private static final String TOPIC_TAG_NAME = "topic";
    private static final String SUBSCRIPTION_TAG_NAME = "subscription";

    private final Counter counter;
    private final String metricSearchPrefix;
    private TopicName topicName;
    private long value;
    private Optional<String> subscription;

    public CounterMatcher(Counter counter, String metricSearchPrefix) {
        this.counter = counter;
        this.metricSearchPrefix = metricSearchPrefix;
        parseCounter(this.counter);
    }

    private void parseCounter(Counter counter) {
        if (isTopicPublished() || isTopicThroughput()) {
            topicName = new TopicName(counter.getId().getTag(GROUP_TAG_NAME), counter.getId().getTag(TOPIC_TAG_NAME));
            subscription = Optional.empty();
        } else if (
                isSubscriptionDelivered()
                        || isSubscriptionThroughput()
                        || isSubscriptionDiscarded()
                        || isSubscriptionFiltered()
        ) {
            topicName = new TopicName(counter.getId().getTag(GROUP_TAG_NAME), counter.getId().getTag(TOPIC_TAG_NAME));
            subscription = Optional.ofNullable(counter.getId().getTag(SUBSCRIPTION_TAG_NAME));
        }
        value = (long) counter.count();
    }

    public boolean isTopicPublished() {
        return isTopicCounter() && nameStartsWith("topic.published"); // TODO extract it from Producer static variable
    }

    public boolean isTopicThroughput() {
        return isTopicCounter() && nameStartsWith("topic.throughput");
    }

    public boolean isSubscriptionThroughput() {
        return isSubscriptionCounter() && nameStartsWith("subscription.throughput");
    }

    public boolean isSubscriptionDelivered() {
        return isSubscriptionCounter() && nameStartsWith("subscription.delivered");
    }

    public boolean isSubscriptionDiscarded() {
        return isSubscriptionCounter() && nameStartsWith("subscription.discarded");
    }

    public boolean isSubscriptionFiltered() {
        return isSubscriptionCounter() && nameStartsWith("subscription.filtered");
    }

    public TopicName getTopicName() {
        return topicName;
    }

    public String getSubscriptionName() {
        return subscription.orElse("");
    }

    public long getValue() {
        return value;
    }

    private boolean isTopicCounter() {
        return counter.getId().getTag(TOPIC_TAG_NAME) != null;
    }

    private boolean isSubscriptionCounter() {
        return counter.getId().getTag(SUBSCRIPTION_TAG_NAME) != null;
    }

    private boolean nameStartsWith(String name) {
        return counter.getId().getName().startsWith(metricSearchPrefix + name);
    }
}
