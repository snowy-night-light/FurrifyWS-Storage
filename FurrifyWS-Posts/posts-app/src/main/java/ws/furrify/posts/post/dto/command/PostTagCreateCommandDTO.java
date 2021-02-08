package ws.furrify.posts.post.dto.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Data
public class PostTagCreateCommandDTO {
    @NotBlank
    @Size(min = 1, max = 32)
    String value;
}
