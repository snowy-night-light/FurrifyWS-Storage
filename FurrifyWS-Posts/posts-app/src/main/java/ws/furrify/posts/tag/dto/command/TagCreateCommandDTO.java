package ws.furrify.posts.tag.dto.command;

import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.CommandDTO;
import ws.furrify.posts.tag.TagType;
import ws.furrify.posts.tag.dto.TagDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Value
@ToString
public class TagCreateCommandDTO implements CommandDTO<TagDTO> {

    @NotBlank
    @Size(max = 32)
    String value;

    @NotNull
    TagType type;

    @Override
    public TagDTO toDTO() {
        return TagDTO.builder()
                .value(value)
                .type(type)
                .build();
    }
}
