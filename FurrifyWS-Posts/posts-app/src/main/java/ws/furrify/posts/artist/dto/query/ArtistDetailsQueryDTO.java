package ws.furrify.posts.artist.dto.query;

import lombok.Data;

import java.util.UUID;

/**
 * @author Skyte
 */
@Data
public class ArtistDetailsQueryDTO {
    /**
     * Artist UUID.
     */
    private UUID artistId;

    /**
     * Artist preferred nickname.
     */
    private String preferredNickname;
}
