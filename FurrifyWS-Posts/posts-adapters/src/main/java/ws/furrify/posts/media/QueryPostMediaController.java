package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.posts.media.dto.query.MediaDetailsQueryDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/posts/{postId}/media")
@RequiredArgsConstructor
class QueryPostMediaController {

    private final SqlMediaQueryRepositoryImpl mediaQueryRepository;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public CollectionModel<EntityModel<MediaDetailsQueryDTO>> getPostMediaList(
            @PathVariable UUID userId,
            @PathVariable UUID postId,
            @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

        // Get tags and add relations
        CollectionModel<EntityModel<MediaDetailsQueryDTO>> mediaResponses = mediaQueryRepository.findAllByOwnerIdAndPostId(userId, postId).stream()
                .map(EntityModel::of)
                .map(this::addMediaRelations)
                .collect(Collectors.collectingAndThen(Collectors.toUnmodifiableList(), CollectionModel::of));

        // Add hateoas relation
        var mediaListRel = linkTo(methodOn(QueryPostMediaController.class).getPostMediaList(
                userId,
                postId,
                null
        )).withSelfRel();

        mediaResponses.add(mediaListRel);

        return mediaResponses;
    }

    @GetMapping("/{mediaId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public EntityModel<MediaDetailsQueryDTO> getPostMedia(@PathVariable UUID userId,
                                                          @PathVariable UUID postId,
                                                          @PathVariable UUID mediaId,
                                                          @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

        MediaDetailsQueryDTO mediaQueryDTO = mediaQueryRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(mediaId)));

        return addMediaRelations(
                EntityModel.of(mediaQueryDTO)
        );
    }

    private EntityModel<MediaDetailsQueryDTO> addMediaRelations(EntityModel<MediaDetailsQueryDTO> mediaDetailsQueryDtoEntityModel) {
        var mediaQueryDto = mediaDetailsQueryDtoEntityModel.getContent();
        // Check if model content is empty
        if (mediaQueryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QueryPostMediaController.class).getPostMedia(
                mediaQueryDto.getOwnerId(),
                mediaQueryDto.getPostId(),
                mediaQueryDto.getMediaId(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandUserMediaController.class).deleteMedia(
                        mediaQueryDto.getOwnerId(), mediaQueryDto.getOwnerId(), mediaQueryDto.getMediaId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserMediaController.class).replaceMedia(
                        mediaQueryDto.getOwnerId(), mediaQueryDto.getPostId(), mediaQueryDto.getMediaId(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserMediaController.class).updateMedia(
                        mediaQueryDto.getOwnerId(), mediaQueryDto.getPostId(), mediaQueryDto.getMediaId(), null, null
                ))
        );

        var mediaListRel = linkTo(methodOn(QueryPostMediaController.class).getPostMediaList(
                mediaQueryDto.getOwnerId(),
                mediaQueryDto.getPostId(),
                null
        )).withRel("postMediaList");

        mediaDetailsQueryDtoEntityModel.add(selfRel);
        mediaDetailsQueryDtoEntityModel.add(mediaListRel);

        return mediaDetailsQueryDtoEntityModel;
    }
}
