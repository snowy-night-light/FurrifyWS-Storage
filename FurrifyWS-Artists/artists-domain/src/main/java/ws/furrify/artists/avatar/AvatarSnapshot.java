package ws.furrify.artists.avatar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode
@ToString
@Getter(value = PACKAGE)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PACKAGE)
class AvatarSnapshot {
    private Long id;

    private UUID artistId;
    private UUID avatarId;
    private UUID ownerId;

    private String filename;
    private String md5;

    private AvatarExtension extension;

    private URI fileUri;
    private URI thumbnailUri;

    private ZonedDateTime createDate;
}
