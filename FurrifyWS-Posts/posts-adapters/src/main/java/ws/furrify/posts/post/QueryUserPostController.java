package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordNotFoundException;
import ws.furrify.posts.pageable.PageableRequest;
import ws.furrify.posts.post.dto.query.PostDetailsQueryDTO;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/posts")
@RequiredArgsConstructor
class QueryUserPostController {

    private final SqlPostQueryRepository postQueryRepository;
    private final PagedResourcesAssembler<PostDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public PagedModel<EntityModel<PostDetailsQueryDTO>> getUserPosts(
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

        Page<PostDetailsQueryDTO> posts = postQueryRepository.findAll(userId, pageable);

        // Add relations
        PagedModel<EntityModel<PostDetailsQueryDTO>> postResponses = posts.stream()
                .peek(this::addPostRelations)
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) ->
                        pagedResourcesAssembler.toModel(
                                new PageImpl<>(list, pageable, posts.getTotalElements())
                        )));

        // Add hateoas relation
        var postsRel = linkTo(methodOn(QueryUserPostController.class).getUserPosts(
                userId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        postResponses.add(postsRel);

        return postResponses;
    }

    @GetMapping("/{postId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public PostDetailsQueryDTO getUserPost(@PathVariable UUID userId,
                                           @PathVariable UUID postId,
                                           @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {

        PostDetailsQueryDTO postQueryDTO = postQueryRepository.findByPostId(userId, postId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId)));

        return addPostRelations(postQueryDTO);
    }

    private PostDetailsQueryDTO addPostRelations(PostDetailsQueryDTO postQueryDTO) {
        var selfRel = linkTo(methodOn(QueryUserPostController.class).getUserPost(
                postQueryDTO.getOwnerId(),
                postQueryDTO.getPostId(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandUserPostController.class).deletePost(
                        postQueryDTO.getOwnerId(), postQueryDTO.getPostId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserPostController.class).replacePostDetails(
                        postQueryDTO.getOwnerId(), postQueryDTO.getPostId(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserPostController.class).updatePostDetails(
                        postQueryDTO.getOwnerId(), postQueryDTO.getPostId(), null, null
                ))
        );

        var postsRel = linkTo(methodOn(QueryUserPostController.class).getUserPosts(
                postQueryDTO.getOwnerId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("userPosts");

        postQueryDTO.add(selfRel);
        postQueryDTO.add(postsRel);

        return postQueryDTO;
    }
}
