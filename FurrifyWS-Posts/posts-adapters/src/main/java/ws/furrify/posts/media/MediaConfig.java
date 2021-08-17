package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.posts.kafka.KafkaTopicEventPublisher;
import ws.furrify.posts.media.dto.MediaDtoFactory;
import ws.furrify.posts.media.strategy.LocalStorageMediaUploadStrategy;

@Configuration
@RequiredArgsConstructor
class MediaConfig {

    private final MediaRepositoryImpl mediaRepository;
    private final MediaQueryRepository mediaQueryRepository;
    private final KafkaTopicEventPublisher<MediaEvent> eventPublisher;

    @Bean
    MediaFacade mediaFacade() {
        var mediaFactory = new MediaFactory();
        var mediaDtoFactory = new MediaDtoFactory(mediaQueryRepository);

        return new MediaFacade(
                new CreateMediaImpl(mediaFactory, mediaUploadStrategy(), eventPublisher),
                new DeleteMediaImpl(eventPublisher, mediaRepository),
                new UpdateMediaImpl(eventPublisher, mediaRepository),
                new ReplaceMediaImpl(eventPublisher, mediaRepository),
                mediaRepository,
                mediaFactory,
                mediaDtoFactory
        );
    }

    @Bean
    LocalStorageMediaUploadStrategy mediaUploadStrategy() {
        return new LocalStorageMediaUploadStrategy();
    }
}
