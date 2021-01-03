package ws.furrify.posts.tag;

import ws.furrify.posts.tag.dto.TagDTO;

import java.util.UUID;

interface ReplaceTagPort {

    void replaceTag(UUID userId, String value, TagDTO tagDTO);

}
