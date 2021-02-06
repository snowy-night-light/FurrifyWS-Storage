package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ws.furrify.posts.PostEvent;
import ws.furrify.tags.TagEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log
class PostEventListener {
    private final PostFacade postFacade;

    @KafkaListener(groupId = "furrify-storage_posts", topics = "post_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload PostEvent postEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        postFacade.handleEvent(UUID.fromString(key), postEvent);
    }

    @KafkaListener(groupId = "furrify-storage_posts", topics = "tag_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload TagEvent tagEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        postFacade.handleEvent(UUID.fromString(key), tagEvent);
    }
}
