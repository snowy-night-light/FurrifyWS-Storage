package ws.furrify.posts.tag;

import ws.furrify.posts.tag.dto.TagDTO;

import java.util.UUID;

interface UpdateTagPort {

    void updateTag(UUID userId, String value, TagDTO tagDTO);

}
