package ws.furrify.posts.artist.dto.query;

import lombok.Data;

import java.net.URI;
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

    /**
     * Artist avatar.
     */
    private ArtistAvatar avatar;

    @Data
    public class ArtistAvatar {
        /**
         * Avatar thumbnail URI.
         */
        private URI thumbnailUri;
    }
}
