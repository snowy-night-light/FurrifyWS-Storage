package ws.furrify.artists.artist.query;

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
}
