package ws.furrify.tags.tag.dto.command;

import lombok.Data;
import ws.furrify.shared.dto.CommandDTO;
import ws.furrify.tags.tag.TagType;
import ws.furrify.tags.tag.dto.TagDTO;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Data
public class TagUpdateCommandDTO implements CommandDTO<TagDTO> {

    @Size(min = 1, max = 64)
    private String title;

    @Size(max = 1024)
    private String description;

    @Size(max = 32)
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$")
    private String value;

    private TagType type;

    @Override
    public TagDTO toDTO() {
        return TagDTO.builder()
                .value(value)
                .type(type)
                .build();
    }
}
