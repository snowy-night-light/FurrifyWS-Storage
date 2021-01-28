package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/posts")
@RequiredArgsConstructor
class CommandUserPostController {

    private final PostFacade postFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public ResponseEntity<?> createPost(@PathVariable UUID userId,
                                        @RequestBody PostCreateCommandDTO postCreateCommandDTO,
                                        @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken,
                                        HttpServletResponse response) {
        PostDTO postDTO = postCreateCommandDTO.toDTO();

        response.addHeader("Id",
                postFacade.createPost(userId, postDTO).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public ResponseEntity<?> deletePost(@PathVariable UUID userId,
                                        @PathVariable UUID postId,
                                        @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {
        postFacade.deletePost(userId, postId);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{postId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public ResponseEntity<?> updatePost(@PathVariable UUID userId,
                                        @PathVariable UUID postId,
                                        @RequestBody PostUpdateCommandDTO postUpdateCommandDTO,
                                        @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {
        postFacade.updatePost(userId, postId, postUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{postId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public ResponseEntity<?> replacePost(@PathVariable UUID userId,
                                         @PathVariable UUID postId,
                                         @RequestBody PostReplaceCommandDTO postReplaceCommandDTO,
                                         @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {
        postFacade.replacePost(userId, postId, postReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
