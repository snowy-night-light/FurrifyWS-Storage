package ws.furrify.artists.artist.vo;

import lombok.AllArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

/**
 * Artist nickname wrapper.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class ArtistNickname {

    private final static short MAX_LENGTH = 256;
    private final static short MIN_LENGTH = 1;

    String nickname;

    /**
     * Create artist nickname from string.
     * Validate given value.
     *
     * @param nickname Artist nickname.
     * @return Artist nickname instance.
     */
    public static ArtistNickname of(String nickname) {
        if (nickname.isBlank()) {
            throw new IllegalStateException("Nickname [nickname=" + nickname + "] can't be blank.");
        }

        if (nickname.length() < MIN_LENGTH || nickname.length() > MAX_LENGTH) {
            throw new IllegalStateException("Nickname [nickname=" + nickname + "] must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH + " but is " + nickname.length() + ".");
        }

        return new ArtistNickname(nickname);
    }
}
