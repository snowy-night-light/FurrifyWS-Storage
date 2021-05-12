package ws.furrify.tags.tag;

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
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.tags.tag.dto.query.TagDetailsQueryDTO;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/tags")
@RequiredArgsConstructor
class QueryUserTagController {

    private final SqlTagQueryRepositoryImpl tagQueryRepository;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public CollectionModel<EntityModel<TagDetailsQueryDTO>> getUserTags(
            @PathVariable UUID userId,
            @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

        // Get tags and add relations
        CollectionModel<EntityModel<TagDetailsQueryDTO>> tagResponses = tagQueryRepository.findAllByOwnerId(userId).stream()
                .map(EntityModel::of)
                .map(this::addTagRelations)
                .collect(Collectors.collectingAndThen(Collectors.toUnmodifiableList(), CollectionModel::of));

        // Add hateoas relation
        var tagsRel = linkTo(methodOn(QueryUserTagController.class).getUserTags(
                userId,
                null
        )).withSelfRel();

        tagResponses.add(tagsRel);

        return tagResponses;
    }

    @GetMapping("/{value}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public EntityModel<TagDetailsQueryDTO> getUserTag(@PathVariable UUID userId,
                                                      @PathVariable String value,
                                                      @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

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
                null
        )).withRel("userTags");

        tagQueryDtoModel.add(selfRel);
        tagQueryDtoModel.add(tagsRel);

        return tagQueryDtoModel;
    }

}
