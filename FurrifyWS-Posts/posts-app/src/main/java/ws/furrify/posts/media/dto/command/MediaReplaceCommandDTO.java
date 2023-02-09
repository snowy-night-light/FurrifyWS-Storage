package ws.furrify.posts.media.dto.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.shared.dto.CommandDTO;

/**
 * @author Skyte
 */
@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@ToString
public class MediaReplaceCommandDTO implements CommandDTO<MediaDTO> {

    @NotNull
    @Max(255)
    @Min(0)
    Integer priority;

    @NotNull
    MediaExtension extension;

    @Override
    public MediaDTO toDTO() {
        return MediaDTO.builder()
                .priority(priority)
                .extension(extension)
                .build();
    }
}
