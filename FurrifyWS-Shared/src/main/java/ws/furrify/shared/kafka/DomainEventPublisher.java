package ws.furrify.shared.kafka;

import java.util.UUID;

/**
 * @author Skyte
 */
public interface DomainEventPublisher<T> {
    /**
     * Publish event to kafka topic.
     *
     * @param topic    Topic to send event to.
     * @param targetId UUID of owner entity event concerns.
     * @param event    Event that will be sent.
     */
    void publish(final Topic topic, final UUID targetId, final T event);

    enum Topic {
        /**
         * Represents post_events topic in kafka.
         */
        ATTACHMENT("attachment_events"),
        MEDIA("media_events"),
        POST("post_events"),
        ARTIST("artist_events"),
        TAG("tag_events");

        /**
         * Topic name for kafka.
         */
        private final String topicName;

        Topic(final String topicName) {
            this.topicName = topicName;
        }

        public String getTopicName() {
            return topicName;
        }
    }

    enum ArtistEventType {
        /**
         * Events that can occur on Artist entity.
         */
        CREATED,
        REMOVED,
        REPLACED,
        UPDATED
    }

    enum PostEventType {
        /**
         * Events that can occur on Post entity.
         */
        CREATED,
        REMOVED,
        REPLACED,
        UPDATED
    }

    enum TagEventType {
        /**
         * Events that can occur on Tag entity.
         */
        CREATED,
        REMOVED,
        REPLACED,
        UPDATED
    }

    enum MediaEventType {
        /**
         * Events that can occur on Media entity.
         */
        CREATED,
        REMOVED,
        REPLACED,
        UPDATED
    }

    enum AttachmentEventType {
        /**
         * Events that can occur on Attachment entity.
         */
        CREATED,
        REMOVED,
        REPLACED,
        UPDATED
    }
}
