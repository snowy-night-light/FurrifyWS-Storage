package ws.furrify.posts.post.dto.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Skyte
 */
@Data
public class PostTagCreateCommandDTO {
    @NotBlank
    String value;
}
