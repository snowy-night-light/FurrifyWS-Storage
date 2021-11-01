package ws.furrify.posts.post;

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
import ws.furrify.posts.post.dto.query.PostDetailsQueryDTO;
import ws.furrify.posts.post.dto.vo.PostQuerySearchDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.pageable.PageableRequest;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/posts")
@RequiredArgsConstructor
class QueryUserPostController {

    private final SqlPostQueryRepositoryImpl postQueryRepository;
    private final PagedResourcesAssembler<PostDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public PagedModel<EntityModel<PostDetailsQueryDTO>> getUserPosts(
            @PathVariable UUID userId,
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

        PagedModel<EntityModel<PostDetailsQueryDTO>> posts = pagedResourcesAssembler.toModel(
                postQueryRepository.findAllByOwnerId(userId, pageable)
        );

        posts.forEach(this::addPostRelations);

        // Add hateoas relation
        var postsRel = linkTo(methodOn(QueryUserPostController.class).getUserPosts(
                userId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        posts.add(postsRel);

        return posts;
    }

    @GetMapping("/{postId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public EntityModel<PostDetailsQueryDTO> getUserPost(@PathVariable UUID userId,
                                                        @PathVariable UUID postId,
                                                        KeycloakAuthenticationToken keycloakAuthenticationToken) {

        PostDetailsQueryDTO postQueryDTO = postQueryRepository.findByOwnerIdAndPostId(userId, postId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId)));

        return addPostRelations(
                EntityModel.of(postQueryDTO)
        );
    }

    @GetMapping("/search")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public PagedModel<EntityModel<PostDetailsQueryDTO>> getUserPostsByQuery(
            @PathVariable UUID userId,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer page,
            @RequestParam String query,
            KeycloakAuthenticationToken keycloakAuthenticationToken) {

        // Build page from page information
        Pageable pageable = PageableRequest.builder()
                .order(order)
                .sort(sort)
                .size(size)
                .page(page)
                .build().toPageable();


        // TODO Implement sql search with query
        PostQuerySearchDTO postQuerySearchDTO = PostQuerySearchDTO.from(query);

        var posts2 = postQueryRepository.findAllByOwnerIdAndQuery(userId, postQuerySearchDTO, pageable)
                .map(postSnapshot ->
                        PostDetailsQueryDTO.of(
                                postSnapshot.getPostId(),
                                postSnapshot.getOwnerId(),
                                postSnapshot.getTitle(),
                                postSnapshot.getDescription(),
                                postSnapshot.getTags(),
                                postSnapshot.getArtists(),
                                postSnapshot.getMediaSet(),
                                postSnapshot.getAttachments(),
                                postSnapshot.getCreateDate()
                        )
                );

        PagedModel<EntityModel<PostDetailsQueryDTO>> posts = pagedResourcesAssembler.toModel(
                posts2
        );

        posts.forEach(this::addPostRelations);

        // Add hateoas relation
        var postsRel = linkTo(methodOn(QueryUserPostController.class).getUserPostsByQuery(
                userId,
                null,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        posts.add(postsRel);

        return posts;
    }

    private EntityModel<PostDetailsQueryDTO> addPostRelations(EntityModel<PostDetailsQueryDTO> postQueryDtoModel) {
        var postQueryDto = postQueryDtoModel.getContent();
        // Check if model content is empty
        if (postQueryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QueryUserPostController.class).getUserPost(
                postQueryDto.getOwnerId(),
                postQueryDto.getPostId(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandUserPostController.class).deletePost(
                        postQueryDto.getOwnerId(), postQueryDto.getPostId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserPostController.class).replacePost(
                        postQueryDto.getOwnerId(), postQueryDto.getPostId(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserPostController.class).updatePost(
                        postQueryDto.getOwnerId(), postQueryDto.getPostId(), null, null
                ))
        );

        var postsRel = linkTo(methodOn(QueryUserPostController.class).getUserPosts(
                postQueryDto.getOwnerId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("userPosts");

        var postsQueryRel = linkTo(methodOn(QueryUserPostController.class).getUserPostsByQuery(
                postQueryDto.getOwnerId(),
                null,
                null,
                null,
                null,
                null,
                null
        )).withRel("userPostsByQuery");

        postQueryDtoModel.add(selfRel);
        postQueryDtoModel.add(postsRel);
        postQueryDtoModel.add(postsQueryRel);

        return postQueryDtoModel;
    }
}
