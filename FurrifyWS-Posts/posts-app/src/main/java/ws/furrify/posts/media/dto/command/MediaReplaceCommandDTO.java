package ws.furrify.posts.media.dto.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.shared.dto.CommandDTO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    @Override
    public MediaDTO toDTO() {
        return MediaDTO.builder()
                .priority(priority)
                .build();
    }
}
