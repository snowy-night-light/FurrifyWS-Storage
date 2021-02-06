package ws.furrify.tags.tag;

import ws.furrify.tags.tag.dto.TagDTO;

import java.util.UUID;

interface ReplaceTagPort {

    void replaceTag(UUID userId, String value, TagDTO tagDTO);

}
