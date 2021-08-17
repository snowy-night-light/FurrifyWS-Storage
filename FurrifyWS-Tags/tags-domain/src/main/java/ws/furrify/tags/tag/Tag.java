package ws.furrify.tags.tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.tags.tag.vo.TagDescription;
import ws.furrify.tags.tag.vo.TagTitle;
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
    private TagTitle title;
    private TagDescription description;
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
                TagTitle.of(tagSnapshot.getTitle()),
                TagDescription.of(tagSnapshot.getDescription()),
                TagValue.of(tagSnapshot.getValue()),
                tagSnapshot.getOwnerId(),
                tagSnapshot.getType(),
                tagSnapshot.getCreateDate()
        );
    }

    TagSnapshot getSnapshot() {
        return TagSnapshot.builder()
                .id(id)
                .title(title.getTitle())
                .description(description.getDescription())
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

    void updateTitle(@NonNull final TagTitle title) {
        this.title = title;
    }

    void updateDescription(final TagDescription description) {
        this.description = description;
    }

    void updateType(@NonNull final TagType type) {
        this.type = type;
    }
}
