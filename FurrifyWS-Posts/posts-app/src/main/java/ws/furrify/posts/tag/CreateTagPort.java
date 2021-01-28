package ws.furrify.posts.tag;

import ws.furrify.posts.tag.dto.TagDTO;

import java.util.UUID;

interface CreateTagPort {

    String createTag(UUID userId, TagDTO tagDTO);
}
