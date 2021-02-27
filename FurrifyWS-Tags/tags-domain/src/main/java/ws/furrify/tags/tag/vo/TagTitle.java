package ws.furrify.tags.tag.vo;

import lombok.AllArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

/**
 * Tag title wrapper.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class TagTitle {

    private final static short MAX_LENGTH = 64;
    private final static short MIN_LENGTH = 1;

    String title;

    /**
     * Create tag title from string.
     * Validate given value.
     *
     * @param title Tag title.
     * @return Tag title instance.
     */
    public static TagTitle of(String title) {
        if (title.isBlank()) {
            throw new IllegalStateException("Tag title [title=" + title + "] can't be blank.");
        }

        if (title.length() < MIN_LENGTH || title.length() > MAX_LENGTH) {
            throw new IllegalStateException("Tag title [title=" + title + "] must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH + " but is " + title.length() + ".");
        }

        return new TagTitle(title);
    }
}
