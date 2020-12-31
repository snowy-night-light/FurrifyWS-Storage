package ws.furrify.posts.tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class Tag {
    private final Long id;
    private final String value;
    private final UUID ownerId;
    private final TagType type;
    private final ZonedDateTime createDate;

    static Tag restore(TagSnapshot tagSnapshot) {
        return new Tag(
                tagSnapshot.getId(),
                tagSnapshot.getValue(),
                tagSnapshot.getOwnerId(),
                tagSnapshot.getType(),
                tagSnapshot.getCreateDate()
        );
    }

    TagSnapshot getSnapshot() {
        return new TagSnapshot(
                id,
                value,
                ownerId,
                type,
                createDate
        );
    }
}
