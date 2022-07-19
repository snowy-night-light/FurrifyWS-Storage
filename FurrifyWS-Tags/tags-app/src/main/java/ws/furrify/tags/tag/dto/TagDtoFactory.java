package ws.furrify.tags.tag.dto;


import lombok.RequiredArgsConstructor;
import ws.furrify.tags.tag.TagEvent;
import ws.furrify.tags.tag.TagQueryRepository;
import ws.furrify.tags.tag.vo.TagType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Creates TagDTO from TagEvent.
 * `
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class TagDtoFactory {

    private final TagQueryRepository tagQueryRepository;

    public TagDTO from(UUID key, TagEvent tagEvent) {
        Instant createDateInstant = tagEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var value = tagEvent.getData().getValue();

        return TagDTO.builder()
                .id(
                        tagQueryRepository.getIdByValue(value)
                )
                .value(value)
                .ownerId(key)
                .type((tagEvent.getData().getType() == null) ? null : TagType.valueOf(tagEvent.getData().getType()))
                .createDate(createDate)
                .build();
    }

}
