package ws.furrify.posts.tag.dto.query;

import lombok.Data;

/**
 * @author Skyte
 */
@Data
public class TagDetailsQueryDTO {
    /**
     * @return Tag unique value for user.
     */
    private String value;

    /**
     * @return Tag type from enum.
     */
    private String type;
}
