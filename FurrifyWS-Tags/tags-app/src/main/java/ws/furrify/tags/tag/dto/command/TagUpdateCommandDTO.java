package ws.furrify.tags.tag.dto.command;

import lombok.ToString;
import lombok.Value;
import ws.furrify.shared.dto.CommandDTO;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.vo.TagType;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Value
@ToString
public class TagUpdateCommandDTO implements CommandDTO<TagDTO> {

    @Size(min = 1, max = 255)
    String title;

    @Size(min = 1, max = 1024)
    String description;

    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-z_-]*$")
    String value;

    TagType type;

    @Override
    public TagDTO toDTO() {
        return TagDTO.builder()
                .value(value)
                .type(type)
                .build();
    }
}
