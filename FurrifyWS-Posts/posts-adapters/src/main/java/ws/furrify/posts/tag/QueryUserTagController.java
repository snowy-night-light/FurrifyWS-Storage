package ws.furrify.posts.tag;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordNotFoundException;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/tags")
@RequiredArgsConstructor
class QueryUserTagController {


    private final SqlTagQueryRepository tagQueryRepository;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public CollectionModel<TagDetailsQueryDTO> getUserTags(
            @PathVariable UUID userId,
            @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

        // Get tags and add relations
        Set<TagDetailsQueryDTO> tags = tagQueryRepository.findAll(userId).stream()
                .peek(this::addTagRelations)
                .collect(Collectors.toSet());


        // Create collection model from tag set
        CollectionModel<TagDetailsQueryDTO> tagResponses = CollectionModel.of(tags);

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
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public TagDetailsQueryDTO getUserTag(@PathVariable UUID userId,
                                         @PathVariable String value,
                                         @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

        TagDetailsQueryDTO tagQueryDTO = tagQueryRepository.findByValue(userId, value)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value)));

        return addTagRelations(tagQueryDTO);
    }

    private TagDetailsQueryDTO addTagRelations(TagDetailsQueryDTO tagQueryDTO) {
/*        var selfRel = linkTo(methodOn(QueryUserTagController.class).getUserTag(
                tagQueryDTO.getOwnerId(),
                tagQueryDTO.getValue(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(Command.class).deletePost(
                        tagQueryDTO.getOwnerId(), tagQueryDTO.getPostId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserPostController.class).replacePostDetails(
                        tagQueryDTO.getOwnerId(), tagQueryDTO.getPostId(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserPostController.class).updatePostDetails(
                        tagQueryDTO.getOwnerId(), tagQueryDTO.getPostId(), null, null
                ))
        );

        var postsRel = linkTo(methodOn(QueryUserPostController.class).getUserPosts(
                tagQueryDTO.getOwnerId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("userPosts");

        tagQueryDTO.add(selfRel);
        tagQueryDTO.add(postsRel);*/

        return tagQueryDTO;
    }

}
