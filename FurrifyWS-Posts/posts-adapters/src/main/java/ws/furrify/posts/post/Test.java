package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ws.furrify.posts.post.dto.PostDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class Test implements CommandLineRunner {

    private final SqlPostRepository sqlPostRepository;

    @Override
    public void run(final String... args) throws Exception {
        var postFactory = new PostFactory();

        var postId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        sqlPostRepository.save(
                postFactory.from(
                        PostDTO.builder()
                                .postId(postId)
                                .ownerId(userId)
                                .title("dsa")
                                .description("dsadas")
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println(postId);
        System.out.println(userId);
    }

}
