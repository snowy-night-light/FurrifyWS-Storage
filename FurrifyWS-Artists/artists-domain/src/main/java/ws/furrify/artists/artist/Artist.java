package ws.furrify.artists.artist;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class Artist {
    private final Long id;
    @NonNull
    private final UUID artistId;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private final Set<String> nicknames;
    @NonNull
    private final String preferredNickname;

    private final ZonedDateTime createDate;

    static Artist restore(ArtistSnapshot artistSnapshot) {
        return new Artist(
                artistSnapshot.getId(),
                artistSnapshot.getArtistId(),
                artistSnapshot.getOwnerId(),
                new HashSet<>(artistSnapshot.getNicknames()),
                artistSnapshot.getPreferredNickname(),
                artistSnapshot.getCreateDate()
        );
    }

    ArtistSnapshot getSnapshot() {
        return ArtistSnapshot.builder()
                .id(id)
                .artistId(artistId)
                .ownerId(ownerId)
                .nicknames(nicknames.stream().collect(Collectors.toUnmodifiableSet()))
                .preferredNickname(preferredNickname)
                .createDate(createDate)
                .build();
    }

}
