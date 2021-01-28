package ws.furrify.posts.post.dto.command;

import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.CommandDTO;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostTag;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Skyte
 */
@Value
@ToString
public class PostReplaceCommandDTO implements CommandDTO<PostDTO> {

    @NotBlank
    @Size(max = 64)
    String title;

    @Size(max = 512)
    String description;

    @NotNull
    Set<PostTagCreateCommandDTO> tags;

    @Override
    public PostDTO toDTO() {
        return PostDTO.builder()
                .title(title)
                .description(description)
                .tags(
                        tags.stream()
                                .map(tag -> new PostTag(tag.getValue(), null))
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
