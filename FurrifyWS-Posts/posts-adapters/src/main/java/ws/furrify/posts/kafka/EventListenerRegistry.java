package ws.furrify.posts.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ws.furrify.artists.artist.ArtistEvent;
import ws.furrify.posts.attachment.AttachmentEvent;
import ws.furrify.posts.attachment.AttachmentFacade;
import ws.furrify.posts.media.MediaEvent;
import ws.furrify.posts.media.MediaFacade;
import ws.furrify.posts.post.PostEvent;
import ws.furrify.posts.post.PostFacade;
import ws.furrify.tags.tag.TagEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log
class EventListenerRegistry {
    private final PostFacade postFacade;
    private final MediaFacade mediaFacade;
    private final AttachmentFacade attachmentFacade;

    @KafkaListener(topics = "post_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload PostEvent postEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        postFacade.handleEvent(UUID.fromString(key), postEvent);
    }

    @KafkaListener(topics = "tag_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload TagEvent tagEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        postFacade.handleEvent(UUID.fromString(key), tagEvent);
    }

    @KafkaListener(topics = "artist_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload ArtistEvent artistEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        postFacade.handleEvent(UUID.fromString(key), artistEvent);
    }

    @KafkaListener(topics = "media_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload MediaEvent mediaEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        UUID keyId = UUID.fromString(key);

        mediaFacade.handleEvent(keyId, mediaEvent);
        postFacade.handleEvent(keyId, mediaEvent);
    }

    @KafkaListener(topics = "attachment_events")
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload AttachmentEvent attachmentEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        UUID keyId = UUID.fromString(key);

        attachmentFacade.handleEvent(keyId, attachmentEvent);
        postFacade.handleEvent(keyId, attachmentEvent);
    }
}
