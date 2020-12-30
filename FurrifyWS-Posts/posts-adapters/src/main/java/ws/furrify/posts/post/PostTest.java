package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.post.dto.PostDTO;

import java.time.ZonedDateTime;
import java.util.Arrays;
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

        var userId = UUID.fromString("152af668-4e26-4f78-9d4a-30e05e546536");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");

        sqlPostRepository.save(
                postFactory.from(
                        PostDTO.builder()
                                .postId(postId)
                                .ownerId(userId)
                                .title("title")
                                .description("desc")
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("UserId: " + userId);
        System.out.println("PostId: " + postId);
    }

}
