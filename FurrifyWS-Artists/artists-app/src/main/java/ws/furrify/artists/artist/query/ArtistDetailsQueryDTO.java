package ws.furrify.artists.artist.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * @author Skyte
 */
@Data
@AllArgsConstructor
public class ArtistDetailsQueryDTO {
    /**
     * Artist UUID.
     */
    private UUID artistId;
}
