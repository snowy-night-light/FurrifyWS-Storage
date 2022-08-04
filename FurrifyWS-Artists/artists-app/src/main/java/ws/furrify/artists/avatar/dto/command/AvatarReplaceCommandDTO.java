package ws.furrify.artists.avatar.dto.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import ws.furrify.artists.avatar.AvatarExtension;
import ws.furrify.artists.avatar.dto.AvatarDTO;
import ws.furrify.shared.dto.CommandDTO;

import javax.validation.constraints.NotNull;

/**
 * @author Skyte
 */
@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@ToString
public class AvatarReplaceCommandDTO implements CommandDTO<AvatarDTO> {

    @NotNull
    AvatarExtension extension;

    @Override
    public AvatarDTO toDTO() {
        return AvatarDTO.builder()
                .extension(extension)
                .build();
    }
}
