package ws.furrify.tags.tag;

import ws.furrify.tags.tag.dto.TagDTO;

import java.time.ZonedDateTime;

final class TagFactory {

    Tag from(TagDTO tagDTO) {
        TagSnapshot tagSnapshot = TagSnapshot.builder()
                .id(tagDTO.getId())
                .value(tagDTO.getValue())
                .ownerId(tagDTO.getOwnerId())
                .type(tagDTO.getType())
                .createDate(
                        (tagDTO.getCreateDate() != null) ? tagDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Tag.restore(tagSnapshot);
    }
}
