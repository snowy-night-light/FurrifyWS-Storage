package ws.furrify.posts.attachment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.posts.attachment.dto.query.AttachmentDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface AttachmentQueryRepository {

    Optional<AttachmentDetailsQueryDTO> findByOwnerIdAndPostIdAndAttachmentId(UUID ownerId, UUID artistId, UUID attachmentId);

    Page<AttachmentDetailsQueryDTO> findAllByOwnerIdAndPostId(UUID ownerId, UUID postId, Pageable pageable);

    Long getIdByAttachmentId(UUID attachmentId);
}
