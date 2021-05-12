package ws.furrify.posts.media;

import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;

import java.util.UUID;

interface CreateMediaPort {

    UUID createMedia(UUID userId, UUID postId, MediaDTO mediaDTO, MultipartFile mediaFile);
}
