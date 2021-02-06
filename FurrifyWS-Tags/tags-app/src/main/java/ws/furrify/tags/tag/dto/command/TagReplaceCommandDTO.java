package ws.furrify.tags.tag.dto.command;

import lombok.Data;
import ws.furrify.shared.dto.CommandDTO;
import ws.furrify.tags.tag.TagType;
import ws.furrify.tags.tag.dto.TagDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Data
public class TagReplaceCommandDTO implements CommandDTO<TagDTO> {

    @NotBlank
    @Size(max = 64)
    private String title;

    @Size(max = 1024)
    private String description;

    @NotBlank
    @Size(max = 32)
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$")
    private String value;

    @NotNull
    private TagType type;

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
