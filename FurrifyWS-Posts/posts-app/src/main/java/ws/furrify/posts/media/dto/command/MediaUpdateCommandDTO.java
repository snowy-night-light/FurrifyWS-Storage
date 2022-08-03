package ws.furrify.posts.media.dto.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.shared.dto.CommandDTO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Skyte
 */
@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@ToString
public class MediaUpdateCommandDTO implements CommandDTO<MediaDTO> {

    @Max(1000)
    @Min(0)
    Integer priority;

    MediaExtension extension;

    @Override
    public MediaDTO toDTO() {
        return MediaDTO.builder()
                .priority(priority)
                .extension(extension)
                .build();
    }
}
