package ws.furrify.artists.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.artists.avatar.dto.AvatarDtoFactory;
import ws.furrify.artists.avatar.strategy.AvatarUploadStrategy;
import ws.furrify.artists.avatar.strategy.LocalStorageAvatarUploadStrategy;
import ws.furrify.artists.kafka.KafkaTopicEventPublisher;
import ws.furrify.posts.avatar.AvatarEvent;

@Configuration
@RequiredArgsConstructor
class AvatarConfig {

    private final AvatarRepositoryImpl avatarRepository;
    private final AvatarQueryRepository avatarQueryRepository;
    private final KafkaTopicEventPublisher<AvatarEvent> eventPublisher;
    private final ArtistServiceImpl artistServiceClient;

    @Bean
    AvatarFacade avatarFacade() {
        var avatarFactory = new AvatarFactory();
        var avatarDtoFactory = new AvatarDtoFactory(avatarQueryRepository);

        return new AvatarFacade(
                new CreateAvatarImpl(artistServiceClient, avatarRepository, avatarFactory, avatarUploadStrategy(), eventPublisher),
                new DeleteAvatarImpl(avatarRepository, eventPublisher),
                avatarRepository,
                avatarFactory,
                avatarDtoFactory
        );
    }

    @Bean
    AvatarUploadStrategy avatarUploadStrategy() {
        return new LocalStorageAvatarUploadStrategy();
    }
}

