package ws.furrify.posts.media.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * Media priority wrapper.
 *
 * @author Skyte
 */
@Data
@Setter(value = PRIVATE)
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class MediaPriority {

    private Integer priority;

    /**
     * Create media priority from int.
     * Validate given value.
     *
     * @param priority Media priority.
     * @return Media priority instance.
     */
    public static MediaPriority of(@NonNull Integer priority) {
        if (priority < 0) {
            throw new IllegalStateException("Media priority [priority=" + priority + "] must be a positive number.");
        }

        return new MediaPriority(priority);
    }
}
