package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.kafka.KafkaTopicEventPublisher;
import ws.furrify.posts.post.dto.PostDtoFactory;
import ws.furrify.posts.tag.TagQueryRepository;

@Configuration
@RequiredArgsConstructor
class PostConfig {

    private final PostRepositoryImpl postRepository;
    private final KafkaTopicEventPublisher<PostEvent> eventPublisher;
    private final TagQueryRepository queryTagRepository;

    @Bean
    PostFacade postFacade() {
        var postFactory = new PostFactory();
        var postDtoFactory = new PostDtoFactory();

        return new PostFacade(
                new CreatePostAdapter(postFactory, eventPublisher, queryTagRepository),
                new DeletePostAdapter(eventPublisher, postRepository),
                new UpdatePostAdapter(eventPublisher, postRepository, queryTagRepository),
                new ReplacePostAdapter(eventPublisher, postRepository, queryTagRepository),
                postRepository,
                postFactory,
                postDtoFactory
        );
    }
}
