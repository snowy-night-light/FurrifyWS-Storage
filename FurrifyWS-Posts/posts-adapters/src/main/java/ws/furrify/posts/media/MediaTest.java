package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.vo.MediaSource;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
                .ifPresent((profile) -> createTestingMedia());
    }

    @SneakyThrows
    private void createTestingMedia() {
        var mediaFactory = new MediaFactory();

        var userId = UUID.fromString("f08e6027-b997-452d-85a6-0cf2d5a1741e");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");
        var mediaId = UUID.fromString("19c02f53-486e-4205-b1b7-74977ae13941");
        var mediaSourceId = UUID.fromString("482b628f-4ca9-4c96-a199-bf25e21b5bca");

        sqlMediaRepository.save(
                mediaFactory.from(
                        MediaDTO.builder()
                                .mediaId(mediaId)
                                .postId(postId)
                                .ownerId(userId)
                                .priority(0)
                                .extension(MediaExtension.PNG)
                                .filename("yes.png")
                                .fileUrl(new URL("https://example.com/"))
                                .thumbnailUrl(new URL("https://example.com/"))
                                .md5("3c518eeb674c71b30297f072fde7eba5")
                                .sources(Collections.singleton(
                                        new MediaSource(
                                                mediaSourceId,
                                                "DeviantArtV1SourceStrategy",
                                                new HashMap<>(1) {{
                                                    put("id", "525");
                                                }}
                                        )
                                ))
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("MediaId: " + mediaId);
    }

}
