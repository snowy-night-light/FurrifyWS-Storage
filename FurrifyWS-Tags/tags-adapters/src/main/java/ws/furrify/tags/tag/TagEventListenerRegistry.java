package ws.furrify.tags.tag;

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
class TagEventListenerRegistry {
    private final TagFacade tagFacade;

    @KafkaListener(topics = "tag_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload TagEvent tagEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        tagFacade.handleEvent(UUID.fromString(key), tagEvent);
    }
}
