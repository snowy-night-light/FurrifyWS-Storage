package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.dto.command.TagCreateCommandDTO;
import ws.furrify.tags.tag.dto.command.TagReplaceCommandDTO;
import ws.furrify.tags.tag.dto.command.TagUpdateCommandDTO;

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
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> createTag(@PathVariable UUID userId,
                                       @RequestBody @Validated TagCreateCommandDTO tagCreateCommandDTO,
                                       KeycloakAuthenticationToken keycloakAuthenticationToken,
                                       HttpServletResponse response) {
        TagDTO tagDTO = tagCreateCommandDTO.toDTO();

        response.addHeader("Id",
                tagFacade.createTag(userId, tagDTO)
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{value}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> deleteTag(@PathVariable UUID userId,
                                       @PathVariable String value,
                                       KeycloakAuthenticationToken keycloakAuthenticationToken) {
        tagFacade.deleteTag(userId, value);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{value}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> updateTagDetails(@PathVariable UUID userId,
                                              @PathVariable String value,
                                              @RequestBody @Validated TagUpdateCommandDTO tagUpdateCommandDTO,
                                              KeycloakAuthenticationToken keycloakAuthenticationToken) {
        tagFacade.updateTag(userId, value, tagUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{value}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> replaceTagDetails(@PathVariable UUID userId,
                                               @PathVariable String value,
                                               @RequestBody @Validated TagReplaceCommandDTO tagReplaceCommandDTO,
                                               KeycloakAuthenticationToken keycloakAuthenticationToken) {
        tagFacade.replaceTag(userId, value, tagReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
