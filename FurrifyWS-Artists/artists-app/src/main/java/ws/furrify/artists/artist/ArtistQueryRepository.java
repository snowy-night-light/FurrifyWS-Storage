package ws.furrify.artists.artist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.artists.artist.dto.query.ArtistDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface ArtistQueryRepository {
    Optional<ArtistDetailsQueryDTO> findByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    Page<ArtistDetailsQueryDTO> findAllByOwnerIdAndOptionalPreferredNickname(UUID ownerId, String preferredNickname, Pageable pageable);

    Page<ArtistDetailsQueryDTO> findAllByOwnerIdAndPreferredNicknameLike(UUID ownerId, String preferredNickname, Pageable pageable);

    Long getIdByArtistId(UUID artistId);
}
