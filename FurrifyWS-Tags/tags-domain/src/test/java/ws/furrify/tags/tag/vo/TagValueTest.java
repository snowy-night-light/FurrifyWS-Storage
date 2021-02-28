package ws.furrify.tags.tag.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagValueTest {

    @Test
    @DisplayName("Create TagValue from string")
    void of() {
        // Given value string
        String value = "Test_value";
        // When of()
        // Then return created TagValue
        assertEquals(
                value,
                TagValue.of(value).getValue(),
                "Created value is not the same."
        );
    }

    @Test
    @DisplayName("Create TagValue from blank string")
    void of2() {
        // Given blank value string
        String value = "  ";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> TagValue.of(value),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create TagValue from too long string")
    void of3() {
        // Given too long value string
        String value = "YScyXVU7byjuFtBG86jKHTif4pvaVmMwv987alt4GoE64XrhSY27VFC09uD5LJqja";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> TagValue.of(value),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create TagValue from too short string")
    void of4() {
        // Given too short value string
        String value = "";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> TagValue.of(value),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create TagValue from string not matching regex pattern")
    void of5() {
        // Given not matching regex pattern value string
        String value = "sa|hgf";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> TagValue.of(value),
                "Exception was not thrown."
        );
    }

}