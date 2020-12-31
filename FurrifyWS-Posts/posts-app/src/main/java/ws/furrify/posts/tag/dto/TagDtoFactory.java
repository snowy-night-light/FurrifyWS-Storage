package ws.furrify.posts.tag.dto;

import ws.furrify.posts.TagEvent;
import ws.furrify.posts.tag.TagType;

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
        Long createDateMillis = tagEvent.getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateMillis != null) {
            createDate = new Date(createDateMillis).toInstant().atZone(ZoneId.systemDefault());
        }

        return TagDTO.builder()
                .id(tagEvent.getTagId())
                .value(tagEvent.getValue())
                .ownerId(key)
                .type(TagType.valueOf(tagEvent.getType()))
                .createDate(createDate)
                .build();
    }

}
