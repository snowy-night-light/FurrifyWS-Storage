package ws.furrify.posts.tag;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode
@ToString
@Getter(value = PACKAGE)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PACKAGE)
class TagSnapshot {
    private Long id;

    private String title;
    private String description;

    private String value;
    private UUID ownerId;

    private TagType type;

    private ZonedDateTime createDate;
}
