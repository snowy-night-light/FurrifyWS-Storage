package ws.furrify.artists.artist.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * @author Skyte
 */
@Data
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class ArtistDetailsQueryDTO {
    /**
     * Artist UUID.
     */
    private UUID artistId;
}
