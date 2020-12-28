package ws.furrify.posts.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.PostEvent;

import java.util.UUID;

/**
 * Topic publisher for kafka.
 *
 * @author Skyte
 */
@Log
@Service
@RequiredArgsConstructor
public class KafkaTopicEventPublisher implements DomainEventPublisher<PostEvent> {
    private final KafkaTemplate<String, PostEvent> kafkaTemplate;

    @Override
    public void publish(final Topic topic, final UUID targetId, final PostEvent event) {
        ListenableFuture<SendResult<String, PostEvent>> future = kafkaTemplate.send(topic.getTopicName(), targetId.toString(), event);

        future.addCallback(result -> {
        }, error -> {
            log.severe("Unable to send event to kafka.");

            throw new RuntimeException("Unable to send event to kafka.");
        });
    }
}
