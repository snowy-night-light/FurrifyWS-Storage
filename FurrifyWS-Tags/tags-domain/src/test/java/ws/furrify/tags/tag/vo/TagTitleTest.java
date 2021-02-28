package ws.furrify.tags.tag.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagTitleTest {

    @Test
    @DisplayName("Create TagTitle from string")
    void of() {
        // Given title string
        String title = "Test_title";
        // When of()
        // Then return created TagTitle
        assertEquals(
                title,
                TagTitle.of(title).getTitle(),
                "Created title is not the same."
        );
    }

    @Test
    @DisplayName("Create TagTitle from blank string")
    void of2() {
        // Given blank title string
        String title = "  ";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> TagTitle.of(title),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create TagTitle from too long string")
    void of3() {
        // Given too long title string
        String title = "YScyXVU7byjuFtBG86jKHTif4pvaVmMwv987alt4GoE64XrhSY27VFC09uD5LJqja";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> TagTitle.of(title),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create TagTitle from too short string")
    void of4() {
        // Given too short title string
        String title = "";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> TagTitle.of(title),
                "Exception was not thrown."
        );
    }
}