package ws.furrify.tags.tag;

import ws.furrify.tags.tag.dto.TagDTO;

import java.util.UUID;

interface CreateTag {

    String createTag(final UUID userId, final TagDTO tagDTO);
}
