package ws.furrify.artists.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ws.furrify.artists.artist.ArtistEvent;
import ws.furrify.artists.artist.ArtistFacade;
import ws.furrify.artists.avatar.AvatarFacade;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.sources.source.SourceEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log
class EventListenerRegistry {
    private final ArtistFacade artistFacade;
    private final AvatarFacade avatarFacade;

    @KafkaListener(topics = "artist_events")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10_000)
    )
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload ArtistEvent artistEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        artistFacade.handleEvent(UUID.fromString(key), artistEvent);
        avatarFacade.handleEvent(UUID.fromString(key), artistEvent);
    }

    @KafkaListener(topics = "avatar_events")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10_000)
    )
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload AvatarEvent avatarEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        artistFacade.handleEvent(UUID.fromString(key), avatarEvent);
        avatarFacade.handleEvent(UUID.fromString(key), avatarEvent);
    }

    @KafkaListener(topics = "source_events")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10_000)
    )
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload SourceEvent sourceEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        artistFacade.handleEvent(UUID.fromString(key), sourceEvent);
    }
}
