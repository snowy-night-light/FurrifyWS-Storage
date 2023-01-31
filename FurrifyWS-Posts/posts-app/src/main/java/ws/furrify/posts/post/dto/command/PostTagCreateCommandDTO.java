package ws.furrify.posts.post.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Skyte
 */
@Data
public class PostTagCreateCommandDTO {
    @NotBlank
    String value;
}
