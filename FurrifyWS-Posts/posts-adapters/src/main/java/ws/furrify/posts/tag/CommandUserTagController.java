package ws.furrify.posts.tag;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.posts.tag.dto.TagDTO;
import ws.furrify.posts.tag.dto.command.TagCreateCommandDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/tags")
@RequiredArgsConstructor
class CommandUserTagController {

    private final TagFacade tagFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "#userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()"
    )
    public ResponseEntity<?> createTag(@PathVariable UUID userId,
                                       @RequestBody TagCreateCommandDTO tagCreateCommandDTO,
                                       @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken,
                                       HttpServletResponse response) {
        TagDTO tagDTO = tagCreateCommandDTO.toDTO();

        response.addHeader("Id",
                tagFacade.createTag(userId, tagDTO)
        );

        return ResponseEntity.accepted().build();
    }

}
