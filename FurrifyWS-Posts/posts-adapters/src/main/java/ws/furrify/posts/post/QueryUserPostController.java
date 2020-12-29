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
        Page<PostDetailsQueryDTO> postResponses = posts.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageable, posts.getTotalElements())));


        return pagedResourcesAssembler.toModel(postResponses);
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

        return postQueryRepository.findByPostId(userId, postId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId)));
    }
}
