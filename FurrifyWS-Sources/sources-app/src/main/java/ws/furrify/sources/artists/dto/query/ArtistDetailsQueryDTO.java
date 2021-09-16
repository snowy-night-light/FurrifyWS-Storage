package ws.furrify.sources.artists.dto.query;

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
