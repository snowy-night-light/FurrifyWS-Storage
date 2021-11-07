package ws.furrify.artists.artist;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ws.furrify.artists.artist.query.ArtistDetailsQueryDTO;

import java.util.UUID;

/**
 * Communication interface with artist service.
 *
 * @author Skyte
 */
public interface ArtistServiceClient {
    /**
     * Get user artist.
     *
     * @param userId   Owner UUID.
     * @param artistId ArtistId.
     * @return Artist details from other microservice.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/users/{userId}/artists/{artistId}")
    ArtistDetailsQueryDTO getUserArtist(@PathVariable UUID userId, @PathVariable UUID artistId);
}
