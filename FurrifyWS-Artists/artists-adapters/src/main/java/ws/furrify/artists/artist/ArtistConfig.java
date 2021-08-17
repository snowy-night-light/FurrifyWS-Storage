package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.artists.artist.dto.ArtistDtoFactory;
import ws.furrify.artists.kafka.KafkaTopicEventPublisher;

@Configuration
@RequiredArgsConstructor
class ArtistConfig {

    private final ArtistRepositoryImpl artistRepository;
    private final ArtistQueryRepository artistQueryRepository;
    private final KafkaTopicEventPublisher<ArtistEvent> eventPublisher;

    @Bean
    ArtistFacade artistFacade() {
        var artistFactory = new ArtistFactory();
        var artistDtoFactory = new ArtistDtoFactory(artistQueryRepository);

        return new ArtistFacade(
                new CreateArtistImpl(artistRepository, artistFactory, eventPublisher),
                new DeleteArtistImpl(artistRepository, eventPublisher),
                new UpdateArtistImpl(artistRepository, eventPublisher),
                new ReplaceArtistImpl(artistRepository, eventPublisher),
                artistRepository,
                artistFactory,
                artistDtoFactory
        );
    }
}
