package ws.furrify.tags.tag;

import ws.furrify.tags.tag.dto.TagDTO;

import java.util.UUID;

interface UpdateTagPort {

    void updateTag(UUID userId, String value, TagDTO tagDTO);

}
