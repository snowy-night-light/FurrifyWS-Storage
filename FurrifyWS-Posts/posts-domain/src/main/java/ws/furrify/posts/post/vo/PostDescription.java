package ws.furrify.posts.post.vo;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

/**
 * Post description wrapper.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class PostDescription {

    private final static short MAX_LENGTH = 1024;
    private final static short MIN_LENGTH = 1;

    @NonNull
    String description;

    /**
     * Create post description from string.
     * Validate given value.
     *
     * @param description Post description.
     * @return Post description instance.
     */
    public static PostDescription of(@NonNull String description) {
        if (description.length() < MIN_LENGTH || description.length() > MAX_LENGTH) {
            throw new IllegalStateException("Post description [description=" + description + "] must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH + " but is " + description.length() + ".");
        }

        return new PostDescription(description);
    }
}
