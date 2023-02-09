package ws.furrify.artists.artist.dto.command;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.ToString;
import lombok.Value;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.shared.dto.CommandDTO;

import java.util.Set;

/**
 * @author Skyte
 */
@Value
@ToString
public class ArtistReplaceCommandDTO implements CommandDTO<ArtistDTO> {

    @Size(min = 1, max = 64)
    Set<@NotBlank @Size(max = 256) @Pattern(regexp = "^[A-Za-z0-9._\\-]+$") String> nicknames;

    @NotBlank
    @Size(min = 1, max = 256)
    @Pattern(regexp = "^[A-Za-z0-9._\\-]+$")
    String preferredNickname;

    @Override
    public ArtistDTO toDTO() {
        return ArtistDTO.builder()
                .nicknames(nicknames)
                .preferredNickname(preferredNickname.strip())
                .build();
    }
}
