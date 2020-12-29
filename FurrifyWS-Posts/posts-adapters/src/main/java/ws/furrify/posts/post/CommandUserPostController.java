package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.dto.command.PostCreateCommandDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/posts")
@RequiredArgsConstructor
class CommandUserPostController {

    private final PostFacade postFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public void createPost(@PathVariable UUID userId,
                           @RequestBody PostCreateCommandDTO postCreateCommandDTO,
                           @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken,
                           HttpServletResponse response) {
        PostDTO postDTO = postCreateCommandDTO.toDTO();

        response.addHeader("Id",
                postFacade.createPost(userId, postDTO).toString()
        );
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public void deletePost(@PathVariable UUID userId,
                           @PathVariable UUID postId,
                           @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {
        postFacade.deletePost(userId, postId);
    }
}
