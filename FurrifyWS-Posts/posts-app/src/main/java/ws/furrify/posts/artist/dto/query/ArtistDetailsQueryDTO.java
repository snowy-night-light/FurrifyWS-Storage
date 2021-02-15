package ws.furrify.posts.artist.dto.query;

import java.util.UUID;

/**
 * @author Skyte
 */
public interface ArtistDetailsQueryDTO {
    /**
     * Artist UUID.
     */
    UUID getArtistId();

    /**
     * Artist preferred nickname.
     */
    String getPreferredNickname();
}
