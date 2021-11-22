package ws.furrify.artists.artist.vo;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.regex.Pattern;

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

    @NonNull
    String nickname;

    /**
     * Artist nickname regex pattern
     */
    public final static String PATTERN = "^[a-z0-9._\\-]+$";

    /**
     * Create artist nickname from string.
     * Validate given value.
     *
     * @param nickname Artist nickname.
     * @return Artist nickname instance.
     */
    public static ArtistNickname of(@NonNull final String nickname) {
        if (nickname.isBlank()) {
            throw new IllegalStateException("Nickname [nickname=" + nickname + "] can't be blank.");
        }

        if (nickname.length() < MIN_LENGTH || nickname.length() > MAX_LENGTH) {
            throw new IllegalStateException("Nickname [nickname=" + nickname + "] must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH + " but is " + nickname.length() + ".");
        }

        if (!Pattern.matches(PATTERN, nickname)) {
            throw new IllegalStateException("Artist preferred nickname [nickname=" + nickname + "] must only consist of a-z, A-Z, \"-\", \".\" and \"_\".");
        }

        return new ArtistNickname(nickname);
    }

    public static ArtistNickname ofNullable(final String preferredNickname) {
        return (preferredNickname == null) ? null : of(preferredNickname);
    }
}
