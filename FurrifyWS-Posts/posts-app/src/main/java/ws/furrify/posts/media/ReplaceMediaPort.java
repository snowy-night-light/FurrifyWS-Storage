package ws.furrify.posts.media;

import ws.furrify.posts.media.dto.MediaDTO;

import java.util.UUID;

interface ReplaceMediaPort {

    void replaceMedia(UUID userId, UUID postId, UUID mediaId, MediaDTO mediaDTO);

}
