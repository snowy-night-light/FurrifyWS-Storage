package ws.furrify.posts.post.dto.command;

import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.shared.dto.CommandDTO;

import javax.validation.Valid;
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
    @Size(min = 1, max = 64)
    String title;

    @Size(min = 1, max = 512)
    String description;

    @NotNull
    @Size(max = 256)
    Set<@Valid PostTagCreateCommandDTO> tags;

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
