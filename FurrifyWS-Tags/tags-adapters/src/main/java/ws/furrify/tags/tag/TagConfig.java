package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.tags.kafka.KafkaTopicEventPublisher;
import ws.furrify.tags.tag.dto.TagDtoFactory;

@Configuration
@RequiredArgsConstructor
class TagConfig {

    private final TagRepositoryImpl tagRepository;
    private final KafkaTopicEventPublisher<TagEvent> eventPublisher;

    @Bean
    TagFacade tagFacade() {
        var tagFactory = new TagFactory();
        var tagDtoFactory = new TagDtoFactory();

        return new TagFacade(
                new CreateTagAdapter(tagFactory, eventPublisher, tagRepository),
                new DeleteTagAdapter(eventPublisher, tagRepository),
                new UpdateTagAdapter(eventPublisher, tagRepository),
                new ReplaceTagAdapter(eventPublisher, tagRepository),
                tagRepository,
                tagFactory,
                tagDtoFactory
        );
    }
}
