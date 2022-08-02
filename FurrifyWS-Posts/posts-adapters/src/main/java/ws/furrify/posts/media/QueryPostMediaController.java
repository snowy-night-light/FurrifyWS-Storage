package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.posts.media.dto.query.MediaDetailsQueryDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.pageable.PageableRequest;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/posts/{postId}/media")
@RequiredArgsConstructor
class QueryPostMediaController {

    private final SqlMediaQueryRepositoryImpl mediaQueryRepository;
    private final PagedResourcesAssembler<MediaDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_post_media') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public PagedModel<EntityModel<MediaDetailsQueryDTO>> getPostMediaList(
            @PathVariable UUID userId,
            @PathVariable UUID postId,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer page,
            KeycloakAuthenticationToken keycloakAuthenticationToken) {

        // Build page from page information
        Pageable pageable = PageableRequest.builder()
                .order(order)
                .sort(sort)
                .size(size)
                .page(page)
                .build().toPageable();

        PagedModel<EntityModel<MediaDetailsQueryDTO>> mediaResponses = pagedResourcesAssembler.toModel(
                mediaQueryRepository.findAllByOwnerIdAndPostId(userId, postId, pageable)
        );

        mediaResponses.forEach(this::addMediaRelations);


        // Add hateoas relation
        var mediaListRel = linkTo(methodOn(QueryPostMediaController.class).getPostMediaList(
                userId,
                postId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        mediaResponses.add(mediaListRel);

        return mediaResponses;
    }

    @GetMapping("/{mediaId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_post_media') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public EntityModel<MediaDetailsQueryDTO> getPostMedia(@PathVariable UUID userId,
                                                          @PathVariable UUID postId,
                                                          @PathVariable UUID mediaId,
                                                          KeycloakAuthenticationToken keycloakAuthenticationToken) {

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
                        mediaQueryDto.getOwnerId(), mediaQueryDto.getPostId(), mediaQueryDto.getMediaId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserMediaController.class).replaceMedia(
                        mediaQueryDto.getOwnerId(), mediaQueryDto.getPostId(), mediaQueryDto.getMediaId(), null, null, null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserMediaController.class).updateMedia(
                        mediaQueryDto.getOwnerId(), mediaQueryDto.getPostId(), mediaQueryDto.getMediaId(), null, null, null, null
                ))
        );

        var mediaListRel = linkTo(methodOn(QueryPostMediaController.class).getPostMediaList(
                mediaQueryDto.getOwnerId(),
                mediaQueryDto.getPostId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("postMediaList");

        mediaDetailsQueryDtoEntityModel.add(selfRel);
        mediaDetailsQueryDtoEntityModel.add(mediaListRel);

        return mediaDetailsQueryDtoEntityModel;
    }
}
