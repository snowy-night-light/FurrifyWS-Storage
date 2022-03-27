package ws.furrify.sources.source;

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
import ws.furrify.shared.pageable.PageableRequest;
import ws.furrify.sources.source.dto.query.SourceDetailsQueryDTO;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/posts/{postId}/media/{mediaId}/sources")
@RequiredArgsConstructor
class QueryPostMediaSourceController {

    private final SqlSourceQueryRepositoryImpl sourceQueryRepository;
    private final PagedResourcesAssembler<SourceDetailsQueryDTO> pagedResourcesAssembler;


    @GetMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_post_media_sources') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public PagedModel<EntityModel<SourceDetailsQueryDTO>> getPostMediaSources(
            @PathVariable UUID userId,
            @PathVariable UUID postId,
            @PathVariable UUID mediaId,
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

        PagedModel<EntityModel<SourceDetailsQueryDTO>> sources = pagedResourcesAssembler.toModel(
                sourceQueryRepository.findAllByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId, pageable)
        );

        sources.forEach(this::addSourceRelations);

        // Add hateoas relation
        var sourcesRel = linkTo(methodOn(QueryPostMediaSourceController.class).getPostMediaSources(
                userId,
                postId,
                mediaId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        sources.add(sourcesRel);

        return sources;
    }

    private EntityModel<SourceDetailsQueryDTO> addSourceRelations(EntityModel<SourceDetailsQueryDTO> sourceQueryDtoModel) {
        var sourceQueryDto = sourceQueryDtoModel.getContent();
        // Check if model content is empty
        if (sourceQueryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QuerySourceController.class).getUserSource(
                sourceQueryDto.getOwnerId(),
                sourceQueryDto.getSourceId(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandSourceController.class).deleteSource(
                        sourceQueryDto.getOwnerId(), sourceQueryDto.getSourceId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandSourceController.class).replaceSource(
                        sourceQueryDto.getOwnerId(), sourceQueryDto.getSourceId(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandSourceController.class).updateSource(
                        sourceQueryDto.getOwnerId(), sourceQueryDto.getSourceId(), null, null
                ))
        );

        var sourcesRel = linkTo(methodOn(QuerySourceController.class).getUserSources(
                sourceQueryDto.getOwnerId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("userSources");

        sourceQueryDtoModel.add(selfRel);
        sourceQueryDtoModel.add(sourcesRel);

        return sourceQueryDtoModel;
    }
}
