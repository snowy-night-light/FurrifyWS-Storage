package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.media.dto.MediaDTO;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class MediaTest implements CommandLineRunner {

    private final SqlMediaRepository sqlMediaRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingArtists());
    }

    private void createTestingArtists() {
        var mediaFactory = new MediaFactory();

        var userId = UUID.fromString("82722f67-ec52-461f-8294-158d8affe7a3");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");
        var mediaId = UUID.fromString("19c02f53-486e-4205-b1b7-74977ae13941");

        sqlMediaRepository.save(
                mediaFactory.from(
                        MediaDTO.builder()
                                .mediaId(mediaId)
                                .postId(postId)
                                .ownerId(userId)
                                .priority(0)
                                .extension(MediaExtension.PNG)
                                .filename("yes.png")
                                .md5("3c518eeb674c71b30297f072fde7eba5")
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("MediaId: " + mediaId);
    }

}
