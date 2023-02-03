package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.posts.post.dto.query.PostDetailsQueryDTO;
import ws.furrify.shared.pageable.PageableRequest;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/artists/{artistId}/posts")
@RequiredArgsConstructor
class QueryArtistPostController {

    private final SqlPostQueryRepositoryImpl postQueryRepository;
    private final PagedResourcesAssembler<PostDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public PagedModel<EntityModel<PostDetailsQueryDTO>> getArtistPosts(
            @PathVariable UUID userId,
            @PathVariable UUID artistId,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer page,
            JwtAuthenticationToken jwtAuthenticationToken) {

        // Build page from page information
        Pageable pageable = PageableRequest.builder()
                .order(order)
                .sort(sort)
                .size(size)
                .page(page)
                .build().toPageable();

        PagedModel<EntityModel<PostDetailsQueryDTO>> posts = pagedResourcesAssembler.toModel(
                postQueryRepository.findAllByOwnerIdAndArtistId(userId, artistId, pageable)
        );

        posts.forEach(this::addPostRelations);

        // Add hateoas relation
        var postsRel = linkTo(methodOn(QueryArtistPostController.class).getArtistPosts(
                userId,
                artistId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        posts.add(postsRel);

        return posts;
    }

    private void addPostRelations(EntityModel<PostDetailsQueryDTO> postQueryDtoModel) {
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

        postQueryDtoModel.add(selfRel);
        postQueryDtoModel.add(postsRel);
    }
}
