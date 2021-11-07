package ws.furrify.artists.artist.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvatarNicknameTest {

    @Test
    @DisplayName("Create AvatarNickname from string")
    void of() {
        // Given nickname string
        String nickname = "Test_nickname";
        // When of()
        // Then return created AvatarNickname
        assertEquals(
                nickname,
                ArtistNickname.of(nickname).getNickname(),
                "Created nickname is not the same."
        );
    }

    @Test
    @DisplayName("Create AvatarNickname from blank string")
    void of2() {
        // Given blank nickname string
        String nickname = "  ";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> ArtistNickname.of(nickname),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create AvatarNickname from too long string")
    void of3() {
        // Given too long nickname string
        String nickname = "0Kj8qXbjplzQJvBdte0Wdic5ybtkdu0w9VRlTgabpQX0d3xPa5HKnme5xAYpO4uarSE6GtUfAmvePxHUdjilAIFc7cR4ZxCjzEWKNy2fiLcfj7LCFf1dSwrfNodxwdg7GyJvKRf4QMaA9f2Otps7I6LTn3fi6sbFPrlWOjdarMfFXVJG0nJWUkYFsbl1RMaPqnN9xTv24l3cdTaykxkfp2K9Cv1LfkKxmAAXPJT12OGLJ66B1X7Dxc2om2Z4nzlGdn2";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> ArtistNickname.of(nickname),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create AvatarNickname from too short string")
    void of4() {
        // Given too short nickname string
        String nickname = "";
        // When of()
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> ArtistNickname.of(nickname),
                "Exception was not thrown."
        );
    }
}