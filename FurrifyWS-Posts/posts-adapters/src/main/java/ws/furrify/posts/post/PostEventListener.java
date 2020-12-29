package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ws.furrify.posts.PostEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class PostEventListener {
    private final PostFacade postFacade;

    @KafkaListener(groupId = "furrify", topics = "post_events")
    public void on(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key, @Payload PostEvent postEvent) {
        postFacade.handleEvent(UUID.fromString(key), postEvent);
    }
}
