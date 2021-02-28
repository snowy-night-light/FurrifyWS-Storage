package ws.furrify.posts.post;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostDescription;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.post.vo.PostTitle;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                .tags(Collections.singleton(new PostTag("tag_value", "ACTION")))
                .artists(Collections.singleton(new PostArtist(UUID.randomUUID(), "example_nickname")))
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
        post.updateDetails(
                PostTitle.of(newTitle),
                PostDescription.of(newDesc)
        );

        assertAll(() -> {
            assertEquals(newTitle, post.getSnapshot().getTitle(), "Title was not updated.");
            assertEquals(newDesc, post.getSnapshot().getDescription(), "Description was not updated.");
        });
    }

    @Test
    @DisplayName("Remove tag")
    void removeTag() {
        // Given tag value
        String tagValue = "tag_value";
        // When removeTag() method called
        // Then remove tag from tags
        post.removeTag(tagValue);

        assertEquals(
                0,
                post.getSnapshot().getTags().size(),
                "Tag was not removed."
        );
    }

    @Test
    @DisplayName("Update tag details in tags")
    void updateTagDetailsInTags() {
        // Given existing tag value nad new tag value and new tag type
        String tagValue = "tag_value";
        String newTagValue = "new_tag_value";
        String newTagType = "BACKGROUND";
        // When updateTagDetailsInTags() method called
        // Then update tag details in tags
        post.updateTagDetailsInTags(tagValue, new PostTag(newTagValue, newTagType));

        assertEquals(
                post.getSnapshot().getTags().toArray()[0],
                new PostTag(newTagValue, newTagType),
                "Tag was not removed."
        );
    }

    @Test
    @DisplayName("Update tag details in tags with non existing tag value")
    void updateTagDetailsInTags2() {
        // Given non existing tag value nad new tag value and new tag type
        String tagValue = "non_existing_tag_value";
        String newTagValue = "new_tag_value";
        String newTagType = "BACKGROUND";
        // When updateTagDetailsInTags() method called
        // Then throw IllegalStateException

        assertThrows(
                IllegalStateException.class,
                () -> post.updateTagDetailsInTags(tagValue, new PostTag(newTagValue, newTagType)),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace tags")
    void replaceTags() {
        // Given new tags set
        PostTag postTag = new PostTag("tag_value", "ACTION");

        Set<PostTag> newTags = Collections.singleton(postTag);
        // When replaceTags() method called
        // Then replace tags
        post.replaceTags(newTags);

        assertEquals(
                post.getSnapshot().getTags().toArray()[0],
                postTag,
                "Tags weren't replaced."
        );
    }

    @Test
    @DisplayName("Remove artist")
    void removeArtist() {
        // Given artistId
        UUID artistId = ((PostArtist) postSnapshot.getArtists().toArray()[0]).getArtistId();
        // When removeArtist() method called
        // Then remove artist from artists
        post.removeArtist(artistId);

        assertEquals(
                0,
                post.getSnapshot().getArtists().size(),
                "Artist was not removed."
        );
    }

    @Test
    @DisplayName("Update artist details in artists")
    void updateArtistDetailsInArtists() {
        // Given existing artistId nad new artist preferredNickname
        UUID artistId = ((PostArtist) postSnapshot.getArtists().toArray()[0]).getArtistId();
        String newPreferredNickname = "example_nickname2";
        // When updateArtistDetailsInArtists() method called
        // Then update artist details in artists
        post.updateArtistDetailsInArtists(artistId, newPreferredNickname);

        assertEquals(
                post.getSnapshot().getArtists().toArray()[0],
                new PostArtist(artistId, newPreferredNickname),
                "Tag was not removed."
        );
    }

    @Test
    @DisplayName("Update artist details in artists with non existing artistId")
    void updateArtistDetailsInArtists2() {
        // Given non existing artistId nad new artist preferredNickname
        UUID artistId = UUID.randomUUID();
        String newPreferredNickname = "example_nickname2";
        // When updateArtistDetailsInArtists() method called
        // Then throw IllegalStateException

        assertThrows(
                IllegalStateException.class,
                () -> post.updateArtistDetailsInArtists(artistId, newPreferredNickname),
                "Exception was not thrown."
        );
    }

    @Test
    void replaceArtists() {
        // Given new artists set
        PostArtist postArtist = new PostArtist(UUID.randomUUID(), "preferred_nickname");

        Set<PostArtist> newArtists = Collections.singleton(postArtist);
        // When replaceArtists() method called
        // Then replace artists
        post.replaceArtists(newArtists);

        assertEquals(
                post.getSnapshot().getArtists().toArray()[0],
                postArtist,
                "Artists weren't replaced."
        );
    }
}