package ws.furrify.posts.post;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class PostTest {

    // Mocked in beforeAll()
    private static PostRepository postRepository;

    private PostSnapshot postSnapshot;
    private Post post;

    @BeforeEach
    void setUp() {
        postSnapshot = PostSnapshot.builder()
                .id(0L)
                .postId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .title("Test")
                .description("dsa")
                .tags(new HashSet<>())
                .createDate(ZonedDateTime.now())
                .build();

        post = Post.restore(postSnapshot);
    }

    @BeforeAll
    static void beforeAll() {
        postRepository = mock(PostRepository.class);
    }

    @Test
    @DisplayName("Post restore from snapshot")
    void restore() {
        // Given postSnapshot
        // When restore() method called
        Post post = Post.restore(postSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(postSnapshot, post.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from post")
    void getSnapshot() {
        // Given post
        // When getSnapshot() method called
        PostSnapshot postSnapshot = post.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.postSnapshot, postSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Update title and description")
    void updateDetails() {
        // Given title and description
        String newTitle = "Title2";
        String newDesc = "dsadasdsa";
        // When updateDetails() method called
        // Then update title and description
        post.updateDetails(newTitle, newDesc);

        assertAll(() -> {
            assertEquals(newTitle, post.getSnapshot().getTitle(), "Title was not updated.");
            assertEquals(newDesc, post.getSnapshot().getDescription(), "Description was not updated.");
        });
    }
}