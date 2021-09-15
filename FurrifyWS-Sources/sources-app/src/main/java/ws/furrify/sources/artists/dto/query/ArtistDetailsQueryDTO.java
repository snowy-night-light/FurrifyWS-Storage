package ws.furrify.sources.artists.dto.query;

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
