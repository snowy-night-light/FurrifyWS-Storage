package ws.furrify.tags.tag;

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
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.pageable.PageableRequest;
import ws.furrify.tags.tag.dto.query.TagDetailsQueryDTO;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/tags")
@RequiredArgsConstructor
class QueryUserTagController {

    private final SqlTagQueryRepositoryImpl tagQueryRepository;
    private final PagedResourcesAssembler<TagDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_user_tags') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public PagedModel<EntityModel<TagDetailsQueryDTO>> getUserTags(
            @PathVariable UUID userId,
            @RequestParam(required = false) String match,
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

        PagedModel<EntityModel<TagDetailsQueryDTO>> tagResponses = pagedResourcesAssembler.toModel(
                tagQueryRepository.findAllByOwnerIdAndLikeMatch(userId, match, pageable)
        );

        tagResponses.forEach(this::addTagRelations);


        // Add hateoas relation
        var tagsRelations = linkTo(methodOn(QueryUserTagController.class).getUserTags(
                userId,
                null,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        tagResponses.add(tagsRelations);

        return tagResponses;
    }

    @GetMapping("/{value}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_user_tags') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public EntityModel<TagDetailsQueryDTO> getUserTag(@PathVariable UUID userId,
                                                      @PathVariable String value,
                                                      KeycloakAuthenticationToken keycloakAuthenticationToken) {

        TagDetailsQueryDTO tagQueryDto = tagQueryRepository.findByOwnerIdAndValue(userId, value)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value)));

        return addTagRelations(
                EntityModel.of(tagQueryDto)
        );
    }

    private EntityModel<TagDetailsQueryDTO> addTagRelations(EntityModel<TagDetailsQueryDTO> tagQueryDtoModel) {
        var tagQueryDto = tagQueryDtoModel.getContent();
        // Check if model content is empty
        if (tagQueryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QueryUserTagController.class).getUserTag(
                tagQueryDto.getOwnerId(),
                tagQueryDto.getValue(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandUserTagController.class).deleteTag(
                        tagQueryDto.getOwnerId(), tagQueryDto.getValue(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserTagController.class).replaceTagDetails(
                        tagQueryDto.getOwnerId(), tagQueryDto.getValue(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserTagController.class).updateTagDetails(
                        tagQueryDto.getOwnerId(), tagQueryDto.getValue(), null, null
                ))
        );

        var tagsRel = linkTo(methodOn(QueryUserTagController.class).getUserTags(
                tagQueryDto.getOwnerId(),
                null,
                null,
                null,
                null,
                null,
                null
        )).withRel("userTags");

        tagQueryDtoModel.add(selfRel);
        tagQueryDtoModel.add(tagsRel);

        return tagQueryDtoModel;
    }

}
