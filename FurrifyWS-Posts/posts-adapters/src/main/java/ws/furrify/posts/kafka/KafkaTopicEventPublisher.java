package ws.furrify.posts.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Topic publisher for kafka.
 *
 * @author Skyte
 */
@Log
@Service
@RequiredArgsConstructor
public class KafkaTopicEventPublisher<T extends SpecificRecord> implements DomainEventPublisher<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    @Override
    public void publish(final Topic topic, final UUID key, final T event) {
        CompletableFuture<SendResult<String, T>> future =
                kafkaTemplate.send(topic.getTopicName(), key.toString(), event);

        future.exceptionally(error -> {
            throw new RuntimeException("Unable to send event to kafka.");
        });
    }
}
