package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import ws.furrify.artists.artist.dto.ArtistDtoFactory;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
public class ArtistFacade {

    private final CreateArtistPort createArtistAdapter;
    private final DeleteArtistPort deleteArtistAdapter;
    private final UpdateArtistPort updateArtistAdapter;
    private final ReplaceArtistPort replaceArtistAdapter;
    private final ArtistRepository artistRepository;
    private final ArtistFactory artistFactory;
    private final ArtistDtoFactory artistDTOFactory;

}
