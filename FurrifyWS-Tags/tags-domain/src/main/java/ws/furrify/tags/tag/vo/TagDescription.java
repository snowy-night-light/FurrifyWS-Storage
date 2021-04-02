package ws.furrify.tags.tag.vo;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

/**
 * Tag description wrapper.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class TagDescription {

    private final static short MAX_LENGTH = 1024;
    private final static short MIN_LENGTH = 1;

    @NonNull
    String description;

    /**
     * Create tag description from string.
     * Validate given value.
     *
     * @param description Tag description.
     * @return Tag description instance.
     */
    public static TagDescription of(@NonNull String description) {
        if (description.length() < MIN_LENGTH || description.length() > MAX_LENGTH) {
            throw new IllegalStateException("Tag description [description=" + description + "] must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH + " but is " + description.length() + ".");
        }

        return new TagDescription(description);
    }
}
