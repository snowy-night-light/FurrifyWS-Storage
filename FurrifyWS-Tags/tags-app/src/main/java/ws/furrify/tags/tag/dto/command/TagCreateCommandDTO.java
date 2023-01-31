package ws.furrify.tags.tag.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.ToString;
import lombok.Value;
import ws.furrify.shared.dto.CommandDTO;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.vo.TagType;

/**
 * @author Skyte
 */
@Value
@ToString
public class TagCreateCommandDTO implements CommandDTO<TagDTO> {

    @NotBlank
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-z0-9_-]*$")
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
