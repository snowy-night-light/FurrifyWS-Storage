package ws.furrify.posts.attachment;

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
import ws.furrify.posts.attachment.dto.query.AttachmentDetailsQueryDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.pageable.PageableRequest;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/posts/{postId}/attachments")
@RequiredArgsConstructor
class QueryPostAttachmentController {

    private final SqlAttachmentQueryRepositoryImpl attachmentQueryRepository;
    private final PagedResourcesAssembler<AttachmentDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public PagedModel<EntityModel<AttachmentDetailsQueryDTO>> getPostAttachments(
            @PathVariable UUID userId,
            @PathVariable UUID postId,
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

        PagedModel<EntityModel<AttachmentDetailsQueryDTO>> attachmentResponses = pagedResourcesAssembler.toModel(
                attachmentQueryRepository.findAllByOwnerIdAndPostId(userId, postId, pageable)
        );

        attachmentResponses.forEach(this::addAttachmentRelations);


        // Add hateoas relation
        var attachmentsRel = linkTo(methodOn(QueryPostAttachmentController.class).getPostAttachments(
                userId,
                postId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        attachmentResponses.add(attachmentsRel);

        return attachmentResponses;
    }

    @GetMapping("/{attachmentId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public EntityModel<AttachmentDetailsQueryDTO> getPostAttachment(@PathVariable UUID userId,
                                                                    @PathVariable UUID postId,
                                                                    @PathVariable UUID attachmentId,
                                                                    KeycloakAuthenticationToken keycloakAuthenticationToken) {

        AttachmentDetailsQueryDTO attachmentQueryDTO = attachmentQueryRepository.findByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId)));

        return addAttachmentRelations(
                EntityModel.of(attachmentQueryDTO)
        );
    }

    private EntityModel<AttachmentDetailsQueryDTO> addAttachmentRelations(EntityModel<AttachmentDetailsQueryDTO> attachmentDetailsQueryDtoEntityModel) {
        var attachmentQueryDto = attachmentDetailsQueryDtoEntityModel.getContent();
        // Check if model content is empty
        if (attachmentQueryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QueryPostAttachmentController.class).getPostAttachment(
                attachmentQueryDto.getOwnerId(),
                attachmentQueryDto.getPostId(),
                attachmentQueryDto.getAttachmentId(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandUserAttachmentController.class).deleteAttachment(
                        attachmentQueryDto.getOwnerId(), attachmentQueryDto.getOwnerId(), attachmentQueryDto.getAttachmentId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserAttachmentController.class).replaceAttachment(
                        attachmentQueryDto.getOwnerId(), attachmentQueryDto.getPostId(), attachmentQueryDto.getAttachmentId(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandUserAttachmentController.class).updateAttachment(
                        attachmentQueryDto.getOwnerId(), attachmentQueryDto.getPostId(), attachmentQueryDto.getAttachmentId(), null, null
                ))
        );

        var mediaListRel = linkTo(methodOn(QueryPostAttachmentController.class).getPostAttachments(
                attachmentQueryDto.getOwnerId(),
                attachmentQueryDto.getPostId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("postMediaList");

        attachmentDetailsQueryDtoEntityModel.add(selfRel);
        attachmentDetailsQueryDtoEntityModel.add(mediaListRel);

        return attachmentDetailsQueryDtoEntityModel;
    }
}
