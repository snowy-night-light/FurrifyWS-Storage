package ws.furrify.artists.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ws.furrify.artists.artist.vo.ArtistAvatar;
import ws.furrify.artists.artist.vo.ArtistSource;

import java.time.ZonedDateTime;
import java.util.Set;
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
class ArtistSnapshot {
    private Long id;

    private UUID artistId;
    private UUID ownerId;

    private Set<String> nicknames;

    private String preferredNickname;

    private Set<ArtistSource> sources;

    private ArtistAvatar avatar;

    private ZonedDateTime createDate;
}