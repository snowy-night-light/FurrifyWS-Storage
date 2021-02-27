package ws.furrify.posts.post.vo;

import lombok.AllArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

/**
 * Post title wrapper.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class PostTitle {

    private final static short MAX_LENGTH = 64;
    private final static short MIN_LENGTH = 1;

    String nickname;

    /**
     * Create post title from string.
     * Validate given value.
     *
     * @param title Post title.
     * @return Post title instance.
     */
    public static PostTitle of(String title) {
        if (title.isBlank()) {
            throw new IllegalStateException("Post title [nickname=" + title + "] can't be blank.");
        }

        if (title.length() < MIN_LENGTH || title.length() > MAX_LENGTH) {
            throw new IllegalStateException("Title [title=" + title + "] must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH + " but is " + title.length() + ".");
        }

        return new PostTitle(title);
    }
}
