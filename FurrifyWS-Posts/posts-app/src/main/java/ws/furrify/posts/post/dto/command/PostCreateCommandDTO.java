package ws.furrify.posts.post.dto.command;

import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.CommandDTO;
import ws.furrify.posts.post.dto.PostDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Value
@ToString
public class PostCreateCommandDTO implements CommandDTO<PostDTO> {

    @NotBlank
    @Size(max = 64)
    String title;

    @Size(max = 512)
    String description;

    @Override
    public PostDTO toDTO() {
        return PostDTO.builder()
                .title(title)
                .description(description)
                .build();
    }
}
