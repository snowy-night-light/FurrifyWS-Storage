package ws.furrify.posts.media.dto.command;

import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.shared.dto.CommandDTO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Skyte
 */
@Value
@ToString
public class MediaUpdateCommandDTO implements CommandDTO<MediaDTO> {

    @Max(255)
    @Min(0)
    Integer priority;

    @Override
    public MediaDTO toDTO() {
        return MediaDTO.builder()
                .priority(priority)
                .build();
    }
}
