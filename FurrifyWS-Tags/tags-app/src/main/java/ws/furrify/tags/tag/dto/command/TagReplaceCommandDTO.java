package ws.furrify.tags.tag.dto.command;

import lombok.ToString;
import lombok.Value;
import ws.furrify.shared.dto.CommandDTO;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.vo.TagType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Value
@ToString
public class TagReplaceCommandDTO implements CommandDTO<TagDTO> {

    @NotBlank
    @Size(min = 1, max = 64)
    String title;

    @Size(min = 1, max = 1024)
    String description;

    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[a-z_-]*$")
    String value;

    @NotNull
    TagType type;

    @Override
    public TagDTO toDTO() {
        return TagDTO.builder()
                .title(title)
                .description(description)
                .value(value)
                .type(type)
                .build();
    }
}
