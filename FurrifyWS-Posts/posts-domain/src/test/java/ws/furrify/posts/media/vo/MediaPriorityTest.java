package ws.furrify.posts.media.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MediaPriorityTest {

    @Test
    @DisplayName("Create MediaPriority from Integer")
    void of() {
        // Given integer priority
        Integer priority = 5;
        // When of()
        // Then return created MediaPriority
        assertEquals(
                priority,
                MediaPriority.of(priority).getPriority(),
                "Created priority is not the same."
        );
    }

    @Test
    @DisplayName("Create MediaPriority from negative Integer")
    void of2() {
        // Given negative integer priority
        Integer priority = -5;
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> MediaPriority.of(priority),
                "Exception was not thrown."
        );
    }
}