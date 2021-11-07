package ws.furrify.artists.avatar;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.artists.avatar.dto.query.AvatarDetailsQueryDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/artists/{artistId}/avatar")
@RequiredArgsConstructor
class QueryArtistAvatarController {

    private final SqlAvatarQueryRepositoryImpl avatarQueryRepository;
    private final PagedResourcesAssembler<AvatarDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping("/{avatarId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_artist_avatar') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public EntityModel<AvatarDetailsQueryDTO> getArtistAvatar(@PathVariable UUID userId,
                                                              @PathVariable UUID artistId,
                                                              @PathVariable UUID avatarId,
                                                              KeycloakAuthenticationToken keycloakAuthenticationToken) {

        AvatarDetailsQueryDTO avatarQueryDTO = avatarQueryRepository.findByOwnerIdAndArtistIdAndAvatarId(userId, artistId, avatarId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(avatarId)));

        return addAvatarRelations(
                EntityModel.of(avatarQueryDTO)
        );
    }

    private EntityModel<AvatarDetailsQueryDTO> addAvatarRelations(EntityModel<AvatarDetailsQueryDTO> avatarDetailsQueryDtoEntityModel) {
        var avatarQueryDto = avatarDetailsQueryDtoEntityModel.getContent();
        // Check if model content is empty
        if (avatarQueryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QueryArtistAvatarController.class).getArtistAvatar(
                avatarQueryDto.getOwnerId(),
                avatarQueryDto.getArtistId(),
                avatarQueryDto.getAvatarId(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandArtistAvatarController.class).deleteAvatar(
                        avatarQueryDto.getOwnerId(), avatarQueryDto.getArtistId(), avatarQueryDto.getAvatarId(), null
                ))
        );

        avatarDetailsQueryDtoEntityModel.add(selfRel);

        return avatarDetailsQueryDtoEntityModel;
    }
}
