package ws.furrify.posts.tag;

import ws.furrify.posts.tag.dto.TagDTO;

import java.time.ZonedDateTime;

class TagFactory {

    Tag from(TagDTO tagDTO) {
        TagSnapshot tagSnapshot = TagSnapshot.builder()
                .id(tagDTO.getId())
                .title(tagDTO.getTitle())
                .description(tagDTO.getDescription())
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
