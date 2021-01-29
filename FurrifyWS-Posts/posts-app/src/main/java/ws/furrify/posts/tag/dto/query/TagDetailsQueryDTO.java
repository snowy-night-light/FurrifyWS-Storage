package ws.furrify.posts.tag.dto.query;

/**
 * @author Skyte
 */
public interface TagDetailsQueryDTO {
    /**
     * @return Tag unique value for user.
     */
    String getValue();

    /**
     * @return Tag type from enum.
     */
    String getType();
}
