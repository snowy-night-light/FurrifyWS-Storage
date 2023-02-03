package ws.furrify.artists.artist;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.dto.command.ArtistCreateCommandDTO;
import ws.furrify.artists.artist.dto.command.ArtistReplaceCommandDTO;
import ws.furrify.artists.artist.dto.command.ArtistUpdateCommandDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.HardLimitForEntityTypeException;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/artists")
@RequiredArgsConstructor
class CommandUserArtistController {

    private final ArtistFacade artistFacade;
    private final ArtistRepository artistRepository;

    @Value("${furrify.limits.artists}")
    private long artistsLimitPerUser;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('create_artist') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> createArtist(@PathVariable UUID userId,
                                          @RequestBody @Validated ArtistCreateCommandDTO artistCreateCommandDTO,
                                          JwtAuthenticationToken jwtAuthenticationToken,
                                          HttpServletResponse response) {
        // Hard limit for artist
        long userArtistsCount = artistRepository.countPostsByUserId(userId);
        if (userArtistsCount >= artistsLimitPerUser) {
            throw new HardLimitForEntityTypeException(
                    Errors.HARD_LIMIT_FOR_ENTITY_TYPE.getErrorMessage(artistsLimitPerUser, "Artist")
            );
        }

        ArtistDTO artistDTO = artistCreateCommandDTO.toDTO();

        response.addHeader("Id",
                artistFacade.createArtist(userId, artistDTO).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{artistId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('delete_artist') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> deleteArtist(@PathVariable UUID userId,
                                          @PathVariable UUID artistId,
                                          JwtAuthenticationToken jwtAuthenticationToken) {
        artistFacade.deleteArtist(userId, artistId);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{artistId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('update_artist') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> updateArtist(@PathVariable UUID userId,
                                          @PathVariable UUID artistId,
                                          @RequestBody @Validated ArtistUpdateCommandDTO artistUpdateCommandDTO,
                                          JwtAuthenticationToken jwtAuthenticationToken) {
        artistFacade.updateArtist(userId, artistId, artistUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{artistId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('update_artist') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> replaceArtist(@PathVariable UUID userId,
                                           @PathVariable UUID artistId,
                                           @RequestBody @Validated ArtistReplaceCommandDTO artistReplaceCommandDTO,
                                           JwtAuthenticationToken jwtAuthenticationToken) {
        artistFacade.replaceArtist(userId, artistId, artistReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
