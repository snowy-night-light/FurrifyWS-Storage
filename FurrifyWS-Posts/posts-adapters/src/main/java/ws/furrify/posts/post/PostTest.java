package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostAttachment;
import ws.furrify.posts.post.vo.PostMedia;
import ws.furrify.posts.post.vo.PostTag;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class PostTest implements CommandLineRunner {

    private final SqlPostRepository sqlPostRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingPosts());
    }

    @SneakyThrows
    private void createTestingPosts() {
        var postFactory = new PostFactory();

        var userId = UUID.fromString("f08e6027-b997-452d-85a6-0cf2d5a1741e");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");

        sqlPostRepository.save(
                postFactory.from(
                        PostDTO.builder()
                                .postId(postId)
                                .ownerId(userId)
                                .title("title")
                                .description("desc")
                                .tags(Set.of(
                                        PostTag.builder()
                                                .value("walking")
                                                .type("ACTION")
                                                .build()
                                ))
                                .artists(Set.of(
                                        PostArtist.builder()
                                                .artistId(
                                                        UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00")
                                                )
                                                .preferredNickname("test_nickname")
                                                .build()
                                ))
                                .attachments(Set.of(
                                        PostAttachment.builder()
                                                .attachmentId(
                                                        UUID.fromString("14925445-f5dc-43b9-a1a0-230cb0f10e51")
                                                )
                                                .filename("test.psd")
                                                .extension("PSD")
                                                .fileUrl(new URL("http://localhost/attachment/14925445-f5dc-43b9-a1a0-230cb0f10e51/furrify-horizontal.png"))
                                                .build()
                                ))
                                .mediaSet(Set.of(
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("19c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(1)
                                                .extension("PNG")
                                                .fileUrl(new URL("http://localhost/media/19c02f53-486e-4205-b1b7-74977ae13941/2620245.png"))
                                                .thumbnailUrl(new URL("http://localhost/media/19c02f53-486e-4205-b1b7-74977ae13941/thumbnail_2620245.png"))
                                                .build(),
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("29c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(1)
                                                .extension("PNG")
                                                .fileUrl(new URL("http://localhost/media/29c02f53-486e-4205-b1b7-74977ae13941/123.png"))
                                                .thumbnailUrl(new URL("http://localhost/media/29c02f53-486e-4205-b1b7-74977ae13941/thumbnail_123.png"))
                                                .build()
                                ))
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("UserId: " + userId);
        System.out.println("PostId: " + postId);
    }

}
