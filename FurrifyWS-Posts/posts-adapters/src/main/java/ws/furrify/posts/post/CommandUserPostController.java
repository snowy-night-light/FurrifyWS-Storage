package ws.furrify.posts.post;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.dto.command.PostCreateCommandDTO;
import ws.furrify.posts.post.dto.command.PostReplaceCommandDTO;
import ws.furrify.posts.post.dto.command.PostUpdateCommandDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.HardLimitForEntityTypeException;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/posts")
@RequiredArgsConstructor
class CommandUserPostController {

    private final PostFacade postFacade;
    private final PostRepository postRepository;

    @Value("${furrify.limits.posts}")
    private long postsLimitPerUser;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "hasAuthority(@keycloakConfig.clientId + '_admin') or " +
                    "(hasAuthority(@keycloakConfig.clientId + '_create_user_post') && #userId.equals(@jwtAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken)))"
    )
    public ResponseEntity<?> createPost(@PathVariable UUID userId,
                                        @RequestBody @Validated PostCreateCommandDTO postCreateCommandDTO,
                                        JwtAuthenticationToken jwtAuthenticationToken,
                                        HttpServletResponse response) {
        // Hard limit for posts
        long userPostsCount = postRepository.countPostsByUserId(userId);
        if (userPostsCount >= postsLimitPerUser) {
            throw new HardLimitForEntityTypeException(
                    Errors.HARD_LIMIT_FOR_ENTITY_TYPE.getErrorMessage(postsLimitPerUser, "Post")
            );
        }

        PostDTO postDTO = postCreateCommandDTO.toDTO();

        response.addHeader("Id",
                postFacade.createPost(userId, postDTO).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "hasAuthority(@keycloakConfig.clientId + '_admin') or " +
                    "(hasAuthority(@keycloakConfig.clientId + '_delete_user_post') && #userId.equals(@jwtAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken)))"
    )
    public ResponseEntity<?> deletePost(@PathVariable UUID userId,
                                        @PathVariable UUID postId,
                                        JwtAuthenticationToken jwtAuthenticationToken) {
        postFacade.deletePost(userId, postId);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{postId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "hasAuthority(@keycloakConfig.clientId + '_admin') or " +
                    "(hasAuthority(@keycloakConfig.clientId + '_update_user_post') && #userId.equals(@jwtAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken)))"
    )
    public ResponseEntity<?> updatePost(@PathVariable UUID userId,
                                        @PathVariable UUID postId,
                                        @RequestBody @Validated PostUpdateCommandDTO postUpdateCommandDTO,
                                        JwtAuthenticationToken jwtAuthenticationToken) {
        postFacade.updatePost(userId, postId, postUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{postId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "hasAuthority(@keycloakConfig.clientId + '_admin') or " +
                    "(hasAuthority(@keycloakConfig.clientId + '_replace_user_post') && #userId.equals(@jwtAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken)))"
    )
    public ResponseEntity<?> replacePost(@PathVariable UUID userId,
                                         @PathVariable UUID postId,
                                         @RequestBody @Validated PostReplaceCommandDTO postReplaceCommandDTO,
                                         JwtAuthenticationToken jwtAuthenticationToken) {
        postFacade.replacePost(userId, postId, postReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
