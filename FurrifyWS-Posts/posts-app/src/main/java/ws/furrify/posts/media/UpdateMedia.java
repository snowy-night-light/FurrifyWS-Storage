package ws.furrify.posts.media;

import ws.furrify.posts.media.dto.MediaDTO;

import java.util.UUID;

interface UpdateMedia {

    void updateMedia(final UUID userId, final UUID postId, final UUID mediaId, final MediaDTO mediaDTO);

}
