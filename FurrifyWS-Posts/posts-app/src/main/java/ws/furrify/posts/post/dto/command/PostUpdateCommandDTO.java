package ws.furrify.posts.post.dto.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import ws.furrify.posts.CommandDTO;
import ws.furrify.posts.post.dto.PostDTO;

import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Data
@AllArgsConstructor
public class PostUpdateCommandDTO implements CommandDTO<PostDTO> {

    @Size(max = 64)
    private String title;

    @Size(max = 512)
    private String description;

    @Override
    public PostDTO toDTO() {
        return PostDTO.builder()
                .title(title)
                .description(description)
                .build();
    }
}
