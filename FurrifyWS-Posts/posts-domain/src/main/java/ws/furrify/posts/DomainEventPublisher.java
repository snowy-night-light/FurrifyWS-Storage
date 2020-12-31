package ws.furrify.posts;

import java.util.UUID;

/**
 * @author Skyte
 */
public interface DomainEventPublisher<T> {
    /**
     * Publish event to kafka topic.
     *
     * @param topic    Topic to send event to.
     * @param targetId UUID of entity event concerns.
     * @param event    Event that will be sent.
     */
    void publish(final Topic topic, final UUID targetId, final T event);

    enum Topic {
        /**
         * Represents post_events topic in kafka.
         */
        POST("post_events"),
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
}
