package ws.furrify.tags.tag.dto;


import ws.furrify.tags.tag.TagEvent;
import ws.furrify.tags.tag.vo.TagType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Creates TagDTO from TagEvent.
 * `
 *
 * @author Skyte
 */
public class TagDtoFactory {

    public TagDTO from(UUID key, TagEvent tagEvent) {
        Long createDateMillis = tagEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateMillis != null) {
            createDate = new Date(createDateMillis).toInstant().atZone(ZoneId.systemDefault());
        }

        return TagDTO.builder()
                .id(tagEvent.getId())
                .title(tagEvent.getData().getTitle())
                .description(tagEvent.getData().getDescription())
                .value(tagEvent.getData().getValue())
                .ownerId(key)
                .type((tagEvent.getData().getType() == null) ? null : TagType.valueOf(tagEvent.getData().getType()))
                .createDate(createDate)
                .build();
    }

}
