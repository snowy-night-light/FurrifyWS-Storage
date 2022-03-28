package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.vo.MediaSource;

import java.net.URI;
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

        var userId = UUID.fromString("4b255497-0200-4ee1-8922-892233173c10");
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
                                .extension(MediaExtension.EXTENSION_PNG)
                                .filename("yes.png")
                                .fileUri(new URI("/test"))
                                .thumbnailUri(new URI("/test"))
                                .md5("3c518eeb674c71b30297f072fde7eba5")
                                .sources(Collections.singleton(
                                        new MediaSource(
                                                mediaSourceId,
                                                "DeviantArtV1SourceStrategy",
                                                new HashMap<>(2) {{
                                                    put("url", "https://www.deviantart.com/freak-side/art/C-h-i-l-l-i-n-911198824");
                                                    put("deviation_id", "EC0AAF26-D129-8165-7E5A-1B3E0B2BBF82");
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
