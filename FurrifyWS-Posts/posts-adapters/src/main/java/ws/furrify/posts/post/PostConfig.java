package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.posts.kafka.KafkaTopicEventPublisher;

@Configuration
@RequiredArgsConstructor
class PostConfig {

    private final PostRepository postRepository;
    private final KafkaTopicEventPublisher eventPublisher;

    @Bean
    PostFacade postFacade() {
        var postFactory = new PostFactory();

        return new PostFacade(
                new CreatePostAdapter(postFactory, eventPublisher),
                new DeletePostAdapter(eventPublisher, postRepository),
                new UpdatePostDetailsDetailsAdapter(eventPublisher, postRepository),
                new ReplacePostDetailsDetailsAdapter(eventPublisher, postRepository),
                postRepository,
                postFactory
        );
    }
}
