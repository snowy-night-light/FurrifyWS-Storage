package ws.furrify.tags.tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.tags.tag.vo.TagType;
import ws.furrify.tags.tag.vo.TagValue;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class Tag {
    private final Long id;
    @NonNull
    private TagValue value;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private TagType type;
    private final ZonedDateTime createDate;

    static Tag restore(TagSnapshot tagSnapshot) {
        return new Tag(
                tagSnapshot.getId(),
                TagValue.of(tagSnapshot.getValue()),
                tagSnapshot.getOwnerId(),
                tagSnapshot.getType(),
                tagSnapshot.getCreateDate()
        );
    }

    TagSnapshot getSnapshot() {
        return TagSnapshot.builder()
                .id(id)
                .value(value.getValue())
                .ownerId(ownerId)
                .type(type)
                .createDate(createDate)
                .build();
    }

    void updateValue(@NonNull final TagValue value,
                     @NonNull final TagRepository tagRepository) {
        if (tagRepository.existsByOwnerIdAndValue(ownerId, value.getValue())) {
            throw new RecordAlreadyExistsException(Errors.TAG_ALREADY_EXISTS.getErrorMessage(value.getValue()));
        }

        this.value = value;
    }

    void updateType(@NonNull final TagType type) {
        this.type = type;
    }
}
