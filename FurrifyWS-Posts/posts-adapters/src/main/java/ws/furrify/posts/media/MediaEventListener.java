package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log
class MediaEventListener {
    private final MediaFacade mediaFacade;

    @KafkaListener(groupId = "furrify-storage_media", topics = "media_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload MediaEvent mediaEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        mediaFacade.handleEvent(UUID.fromString(key), mediaEvent);
    }
}
