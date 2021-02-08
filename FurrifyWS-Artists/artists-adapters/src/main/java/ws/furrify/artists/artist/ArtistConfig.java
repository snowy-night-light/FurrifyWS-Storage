package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.artists.ArtistEvent;
import ws.furrify.artists.artist.dto.ArtistDtoFactory;
import ws.furrify.artists.kafka.KafkaTopicEventPublisher;

@Configuration
@RequiredArgsConstructor
class ArtistConfig {

    private final ArtistRepositoryImpl artistRepository;
    private final KafkaTopicEventPublisher<ArtistEvent> eventPublisher;

    @Bean
    ArtistFacade artistFacade() {
        var artistFactory = new ArtistFactory();
        var artistDtoFactory = new ArtistDtoFactory();

        return new ArtistFacade(
                new CreateArtistAdapter(artistRepository, artistFactory, eventPublisher),
                new DeleteArtistAdapter(artistRepository, eventPublisher),
                new UpdateArtistAdapter(artistRepository, eventPublisher),
                new ReplaceArtistAdapter(artistRepository, eventPublisher),
                artistRepository,
                artistFactory,
                artistDtoFactory
        );
    }
}
