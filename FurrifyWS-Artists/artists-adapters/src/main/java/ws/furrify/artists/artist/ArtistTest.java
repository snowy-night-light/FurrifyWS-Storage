package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.artists.artist.dto.ArtistDTO;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class ArtistTest implements CommandLineRunner {

    private final SqlArtistRepository sqlArtistRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingArtists());
    }

    private void createTestingArtists() {
        var artistFactory = new ArtistFactory();

        var userId = UUID.fromString("d56abf09-6fe6-4a38-b758-444633b2d13f");
        var artistId = UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00");

        sqlArtistRepository.save(
                artistFactory.from(
                        ArtistDTO.builder()
                                .artistId(artistId)
                                .ownerId(userId)
                                .nicknames(Collections.singleton("test_nickname"))
                                .preferredNickname("test_nickname")
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("ArtistId: " + artistId);
    }

}
