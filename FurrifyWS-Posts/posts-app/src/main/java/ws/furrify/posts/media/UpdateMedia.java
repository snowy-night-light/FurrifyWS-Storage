package ws.furrify.posts.media;

import ws.furrify.posts.media.dto.MediaDTO;

import java.util.UUID;

interface UpdateMedia {

    void updateMedia(UUID userId, UUID postId, UUID mediaId, MediaDTO mediaDTO);

}
