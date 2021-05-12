package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.artists.artist.dto.query.ArtistDetailsQueryDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.pageable.PageableRequest;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/artists")
@RequiredArgsConstructor
class QueryUserArtistController {

    private final SqlArtistQueryRepositoryImpl artistQueryRepository;
    private final PagedResourcesAssembler<ArtistDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public PagedModel<EntityModel<ArtistDetailsQueryDTO>> getUserArtists(
            @PathVariable UUID userId,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer page,
            @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

        // Build page from page information
        Pageable pageable = PageableRequest.builder()
                .order(order)
                .sort(sort)
                .size(size)
                .page(page)
                .build().toPageable();

        PagedModel<EntityModel<ArtistDetailsQueryDTO>> posts = pagedResourcesAssembler.toModel(
                artistQueryRepository.findAllByOwnerId(userId, pageable)
        );

        posts.forEach(this::addArtistRelations);

        // Add hateoas relation
        var artistsRel = linkTo(methodOn(QueryUserArtistController.class).getUserArtists(
                userId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        posts.add(artistsRel);

        return posts;
    }

    @GetMapping("/{artistId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public EntityModel<ArtistDetailsQueryDTO> getUserArtist(@PathVariable UUID userId,
                                                            @PathVariable UUID artistId,
                                                            @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

        ArtistDetailsQueryDTO artistQueryDTO = artistQueryRepository.findByOwnerIdAndArtistId(userId, artistId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId)));

        return addArtistRelations(
                EntityModel.of(artistQueryDTO)
        );
    }

    private EntityModel<ArtistDetailsQueryDTO> addArtistRelations(EntityModel<ArtistDetailsQueryDTO> artistQueryDtoModel) {
        var artistQueryDto = artistQueryDtoModel.getContent();
        // Check if model content is empty
        if (artistQueryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QueryUserArtistController.class).getUserArtist(
                artistQueryDto.getOwnerId(),
                artistQueryDto.getArtistId(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandUserArtistController.class).deleteArtist(
                        artistQueryDto.getOwnerId(), artistQueryDto.getArtistId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserArtistController.class).replaceArtist(
                        artistQueryDto.getOwnerId(), artistQueryDto.getArtistId(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserArtistController.class).updateArtist(
                        artistQueryDto.getOwnerId(), artistQueryDto.getArtistId(), null, null
                ))
        );

        var artistsRel = linkTo(methodOn(QueryUserArtistController.class).getUserArtists(
                artistQueryDto.getOwnerId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("userArtists");

        artistQueryDtoModel.add(selfRel);
        artistQueryDtoModel.add(artistsRel);

        return artistQueryDtoModel;
    }
}
