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

        var userId = UUID.fromString("82722f67-ec52-461f-8294-158d8affe7a3");
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
                                                        UUID.fromString("566548cf-fb1d-4552-a880-c741a1eb9d0e")
                                                )
                                                .filename("test.psd")
                                                .extension("PSD")
                                                .fileUrl(new URL("https://example.com"))
                                                .build()
                                ))
                                .mediaSet(Set.of(
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("19c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(1)
                                                .extension("PNG")
                                                .fileUrl(new URL("https://example.com"))
                                                .thumbnailUrl(new URL("https://example.com"))
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
