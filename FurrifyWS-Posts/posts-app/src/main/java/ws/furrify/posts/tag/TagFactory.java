package ws.furrify.posts.tag;

import ws.furrify.posts.tag.dto.TagDTO;

import java.time.ZonedDateTime;

class TagFactory {

    Tag from(TagDTO tagDTO) {
        TagSnapshot tagSnapshot = new TagSnapshot(
                tagDTO.getId(),
                tagDTO.getValue(),
                tagDTO.getOwnerId(),
                tagDTO.getType(),
                (tagDTO.getCreateDate() != null) ? tagDTO.getCreateDate() : ZonedDateTime.now()
        );

        return Tag.restore(tagSnapshot);
    }
}
