package ws.furrify.posts.post.dto.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostQuerySearchDTOTest {

    @Test
    @DisplayName("Get query from string")
    void from() {
        // Given
        String query = "@artist1 @artist2 -@artist3 -@artist4 #tag1 #tag2 -#tag3 -#tag4 title1 title2";
        // When
        // Then
        var querySearchDTO = PostQuerySearchDTO.from(query);

        assertAll(
                () -> assertTrue(
                        querySearchDTO.getWithArtists().containsAll(Arrays.asList("artist1", "artist2")
                        ), "With artist is missing."),
                () -> assertTrue(
                        querySearchDTO.getWithoutArtists().containsAll(Arrays.asList("artist3", "artist4")
                        ), "Without artist is missing."),
                () -> assertTrue(
                        querySearchDTO.getWithTags().containsAll(Arrays.asList("tag1", "tag2")
                        ), "With tag is missing."),
                () -> assertTrue(
                        querySearchDTO.getWithoutTags().containsAll(Arrays.asList("tag3", "tag4")
                        ), "Without tag is missing."),
                () -> assertTrue(
                        querySearchDTO.getWords().containsAll(Arrays.asList("title1", "title2")
                        ), "Word is missing.")
        );
    }

    @Test
    @DisplayName("Get query from empty string")
    void from2() {
        // Given
        String query = "";
        // When
        // Then
        assertDoesNotThrow(() -> PostQuerySearchDTO.from(query));
    }
}