package ws.furrify.tags.tag.vo;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;

/**
 * Tag value wrapper.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class TagValue {

    private final static short MAX_LENGTH = 32;
    private final static short MIN_LENGTH = 1;

    /**
     * Tag value regex pattern
     */
    private final static String PATTERN = "^[a-zA-Z0-9_-]*$";

    @NonNull
    String value;

    /**
     * Create tag value from string.
     * Validate given value.
     *
     * @param value Tag value.
     * @return Tag value instance.
     */
    public static TagValue of(@NonNull String value) {
        if (value.isBlank()) {
            throw new IllegalStateException("Tag value [value=" + value + "] can't be blank.");
        }

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalStateException("Tag value [value=" + value + "] must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH + " but is " + value.length() + ".");
        }

        if (!Pattern.matches(PATTERN, value)) {
            throw new IllegalStateException("Tag value [value=" + value + "] must only consist of a-z, A-Z, \"-\" and \"_\".");
        }

        return new TagValue(value);
    }
}
