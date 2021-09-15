package ws.furrify.sources.artists;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ws.furrify.sources.artists.dto.query.ArtistDetailsQueryDTO;

import java.util.UUID;

/**
 * Communication interface with artist service.
 *
 * @author Skyte
 */
public interface ArtistServiceClient {
    /**
     * Get artist.
     *
     * @param userId   Owner UUID.
     * @param artistId Artist UUID.
     * @return Artist details from other microservice.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/users/{userId}/artists/{artistId}")
    ArtistDetailsQueryDTO getUserArtist(@PathVariable UUID userId, @PathVariable UUID artistId);
}
