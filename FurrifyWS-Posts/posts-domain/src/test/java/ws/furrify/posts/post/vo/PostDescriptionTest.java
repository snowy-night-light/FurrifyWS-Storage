package ws.furrify.posts.post.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostDescriptionTest {

    @Test
    @DisplayName("Create PostDescription from string")
    void of() {
        // Given description string
        String description = "Test_nickname";
        // When of()
        // Then return created PostDescription
        assertEquals(
                description,
                PostDescription.of(description).getDescription(),
                "Created description is not the same."
        );
    }

    @Test
    @DisplayName("Create PostDescription from too long string")
    void of2() {
        // Given too long description string
        String description = "OUu4inbYkvYBV3TksucZGLjlKr7xGu96WeRMq8JnMQhTXLQd6lISBQ3fr4tEYYgMQYafXKAwX7QaQ0MGrHHTJGNCXV7lZHGJb4sCSQ8HP3QVymp5sAKZ9nfBb8EmkivrxyyaBU0VRVBIvKHqPhX93lfX38sNJgED1HCnUxrEdXn9CaPcQCbaRuX0jsUpAhJeVNSDkPq1a16qt0gCutVnPJGJ7BtmtiOCtgS8oIqKm7Zl1lb9xd8qgOfhCXXI3DnDdjJ5sSF4kJwdkthdgZ7G2lAjOWvqM1EhoQqx0ffsycuOmWjKj1FOmQphGpnqNAl5jhbvzY3k62kIRmolvOr10QSTMokNjyoRzmFFaF5Pno2Wq5aeBehuGDVSK6AodKfKLMg9cO4INt5UFsQIJmJmhW7uyrZUzgLGJtXdLyyrdZeUHmtmVOGrLlQgwt3aBwZeoRcT8oTrDgObfLkLauweENuDQwKWn5ubOhKxTx6aHM9EIJGAkQK7ryDLBrqT4gZ5SFRv4za24RQlFmVZDlgLvoEUJSJoljHvp2CpD47OLyIb3dynQieQBvzN1v8AyTCVuyG4RI9gzN98iagDRyyx7PIu7UW6jt21KLgXAw18Y1Ru9rAk0fblyCfFHTX0TXtmuNkqyDz2RpJ0oIj5vb0o1KtkFBnqo9dj95lVrUDyrgZphjyFwlHizMXNHiIF3k4yyC5c9430JVJ0XW4Pd9liDxVjdTrq4kFagqacvQ9c4xgsuP4O32pyjMzLrPGNrZ9s6SITej9MjtKBIvQrhwaFgs0K0voHNP8TcDf8BYIhjv9tJIFYxPzyOpK1RYjg1dl45MqeCHfvflkS63zqRLBGF2zK4z1b7WhSouRf0VgnBLZ2Tx30e8Q3FROIrCjpUFAjACdhDarQxLOLJQevtLuOBG30X2Nat1ONLB1QJ4kRFhQZlb4tTP1FrtGWZQrVrH3xjcP0sGwBOJlZEpBkLqPclclSDX81lUL2rdcjUlzLWyjOqrduPGpIAbQBm7i7sscUl;p";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> PostDescription.of(description),
                "Exception was not thrown."
        );
    }
}