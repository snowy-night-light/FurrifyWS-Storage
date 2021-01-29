package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.kafka.KafkaTopicEventPublisher;
import ws.furrify.posts.post.dto.PostDtoFactory;

@Configuration
@RequiredArgsConstructor
class PostConfig {

    private final PostRepositoryImpl postRepository;
    private final KafkaTopicEventPublisher<PostEvent> eventPublisher;
    private final TagServiceClientImpl tagServiceClient;

    @Bean
    PostFacade postFacade() {
        var postFactory = new PostFactory();
        var postDtoFactory = new PostDtoFactory();

        return new PostFacade(
                new CreatePostAdapter(postFactory, eventPublisher, tagServiceClient),
                new DeletePostAdapter(eventPublisher, postRepository),
                new UpdatePostAdapter(eventPublisher, postRepository, tagServiceClient),
                new ReplacePostAdapter(eventPublisher, postRepository, tagServiceClient),
                postRepository,
                postFactory,
                postDtoFactory
        );
    }
}
