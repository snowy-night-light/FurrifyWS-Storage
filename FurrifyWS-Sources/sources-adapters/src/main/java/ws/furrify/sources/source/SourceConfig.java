package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.sources.kafka.KafkaTopicEventPublisher;
import ws.furrify.sources.source.dto.SourceDtoFactory;

@Configuration
@RequiredArgsConstructor
class SourceConfig {

    private final SourceRepositoryImpl sourceRepository;
    private final SourceQueryRepository sourceQueryRepository;
    private final KafkaTopicEventPublisher<SourceEvent> eventPublisher;

    @Bean
    SourceFacade sourceFacade() {
        var sourceFactory = new SourceFactory();
        var sourceDtoFactory = new SourceDtoFactory(sourceQueryRepository);

        return new SourceFacade(
                new CreateSourceImpl(sourceRepository, sourceFactory, eventPublisher),
                new DeleteSourceImpl(sourceRepository, eventPublisher),
                new UpdateSourceImpl(sourceRepository, eventPublisher),
                new ReplaceSourceImpl(sourceRepository, eventPublisher),
                sourceRepository,
                sourceFactory,
                sourceDtoFactory
        );
    }
}
