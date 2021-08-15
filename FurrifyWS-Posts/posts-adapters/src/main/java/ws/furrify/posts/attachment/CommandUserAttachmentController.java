package ws.furrify.posts.attachment;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.dto.command.AttachmentCreateCommandDTO;
import ws.furrify.posts.attachment.dto.command.AttachmentReplaceCommandDTO;
import ws.furrify.posts.attachment.dto.command.AttachmentUpdateCommandDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/posts/{postId}/attachments")
@RequiredArgsConstructor
class CommandUserAttachmentController {

    private final AttachmentFacade attachmentFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> createAttachment(@PathVariable UUID userId,
                                              @PathVariable UUID postId,
                                              @RequestPart("attachment") @Validated AttachmentCreateCommandDTO attachmentCreateCommandDTO,
                                              @RequestPart("file") MultipartFile mediaFile,
                                              KeycloakAuthenticationToken keycloakAuthenticationToken,
                                              HttpServletResponse response) {
        AttachmentDTO attachmentDTO = attachmentCreateCommandDTO.toDTO();

        response.addHeader("Id",
                attachmentFacade.createAttachment(userId, postId, attachmentDTO, mediaFile).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{attachmentId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> deleteAttachment(@PathVariable UUID userId,
                                              @PathVariable UUID postId,
                                              @PathVariable UUID attachmentId,
                                              KeycloakAuthenticationToken keycloakAuthenticationToken) {
        attachmentFacade.deleteAttachment(userId, postId, attachmentId);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{attachmentId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> updateAttachment(@PathVariable UUID userId,
                                              @PathVariable UUID postId,
                                              @PathVariable UUID attachmentId,
                                              @RequestBody @Validated AttachmentUpdateCommandDTO attachmentUpdateCommandDTO,
                                              KeycloakAuthenticationToken keycloakAuthenticationToken) {
        attachmentFacade.updateAttachment(userId, postId, attachmentId, attachmentUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{attachmentId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> replaceAttachment(@PathVariable UUID userId,
                                               @PathVariable UUID postId,
                                               @PathVariable UUID attachmentId,
                                               @RequestBody @Validated AttachmentReplaceCommandDTO attachmentReplaceCommandDTO,
                                               KeycloakAuthenticationToken keycloakAuthenticationToken) {
        attachmentFacade.replaceAttachment(userId, postId, attachmentId, attachmentReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
