package pl.allegro.tech.hermes.consumers.consumer.sender

import pl.allegro.tech.hermes.api.Subscription
import pl.allegro.tech.hermes.api.SubscriptionName
import pl.allegro.tech.hermes.consumers.consumer.Message
import pl.allegro.tech.hermes.consumers.consumer.SendFutureProvider
import pl.allegro.tech.hermes.consumers.consumer.rate.ConsumerRateLimiter
import pl.allegro.tech.hermes.consumers.consumer.sender.timeout.FutureAsyncTimeout
import spock.lang.Specification
import static pl.allegro.tech.hermes.test.helper.builder.SubscriptionBuilder.subscription

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

import static pl.allegro.tech.hermes.consumers.test.MessageBuilder.testMessage

class SingleMessageSenderAdapterTest extends Specification {

    FutureAsyncTimeout futureAsyncTimeout = new FutureAsyncTimeout(Executors.newSingleThreadScheduledExecutor())

    CompletableFutureAwareMessageSender successfulMessageSender = new CompletableFutureAwareMessageSender() {
        void send(Message message, CompletableFuture<MessageSendingResult> resultFuture) {
            resultFuture.complete(MessageSendingResult.succeededResult())
        }

        void stop() {}
    }

    CompletableFutureAwareMessageSender failingMessageSender = new CompletableFutureAwareMessageSender() {
        void send(Message message, CompletableFuture<MessageSendingResult> resultFuture) {
            resultFuture.completeExceptionally(new IllegalStateException())
        }

        void stop() {}
    }

    SendFutureProvider futureProvider(ConsumerRateLimiter consumerRateLimiter) {
        Subscription subscription = subscription(SubscriptionName.fromString("group.topic\$subscription")).build()

        return new SendFutureProvider(
                consumerRateLimiter,
                subscription,
                futureAsyncTimeout,
                1000,
                1000,
        )
    }

    def "should register successful send in rate limiter"() {
        given:
        ConsumerRateLimiter consumerRateLimiter = Mock(ConsumerRateLimiter) {
            1 * acquire()
            1 * registerSuccessfulSending()
        }
        SendFutureProvider futureProvider = futureProvider(consumerRateLimiter)
        SingleMessageSenderAdapter adapter = new SingleMessageSenderAdapter(successfulMessageSender, futureProvider)

        when:
        CompletableFuture<MessageSendingResult> future = adapter.send(testMessage())

        then:
        future.get().succeeded()
    }

    def "should register unsuccessful send in rate limiter"() {
        given:
        ConsumerRateLimiter consumerRateLimiter = Mock(ConsumerRateLimiter) {
            1 * acquire()
            1 * registerFailedSending()
        }
        SendFutureProvider futureProvider = futureProvider(consumerRateLimiter)

        SingleMessageSenderAdapter adapter = new SingleMessageSenderAdapter(failingMessageSender, futureProvider)

        when:
        CompletableFuture<MessageSendingResult> future = adapter.send(testMessage())

        then:
        !future.get().succeeded()

    }

}
