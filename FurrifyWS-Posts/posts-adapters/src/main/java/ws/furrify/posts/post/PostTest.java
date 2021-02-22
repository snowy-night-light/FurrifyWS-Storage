package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostTag;

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

    private void createTestingPosts() {
        var postFactory = new PostFactory();

        var userId = UUID.fromString("d56abf09-6fe6-4a38-b758-444633b2d13f");
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
                                                .preferredNickname("example_nickname")
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
