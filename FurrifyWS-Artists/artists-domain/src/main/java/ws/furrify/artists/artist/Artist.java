package ws.furrify.artists.artist;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.InvalidDataGivenException;
import ws.furrify.shared.exception.RecordAlreadyExistsException;

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
    private Set<String> nicknames;
    @NonNull
    private String preferredNickname;

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

    void updateNicknames(Set<String> newNicknames,
                         String newPreferredNickname,
                         @NonNull final ArtistRepository artistRepository) {
        // Check if both parameters are null
        if (newPreferredNickname == null && newNicknames == null) {
            throw new NullPointerException("Both parameters cannot be null.");
        }

        // Check if any parameters are null
        if (newNicknames == null) {
            newNicknames = new HashSet<>(this.nicknames);
        } else if (newPreferredNickname == null) {
            newPreferredNickname = this.preferredNickname;
        }

        // Copy fields to final fields
        final String preferredNickname = newPreferredNickname;
        final Set<String> nicknames = newNicknames;

        // Check if nicknames array is empty
        if (nicknames.size() == 0) {
            throw new InvalidDataGivenException(Errors.NICKNAMES_CANNOT_BE_EMPTY.getErrorMessage());
        }

        // Verify if preferred nickname is in nicknames array
        boolean isPreferredNicknameValid = nicknames.stream()
                .anyMatch(nick -> nick.equals(preferredNickname));

        if (!isPreferredNicknameValid) {
            throw new InvalidDataGivenException(Errors.PREFERRED_NICKNAME_IS_NOT_VALID.getErrorMessage(preferredNickname));
        }

        // Verify is preferred nickname is already taken
        boolean isPreferredNicknameTaken =
                // Is nickname different than original
                !this.preferredNickname.equals(preferredNickname) &&
                        // Do nickname is selected as preferred in other artists
                        artistRepository.existsByOwnerIdAndPreferredNickname(ownerId, preferredNickname);

        if (isPreferredNicknameTaken) {
            throw new RecordAlreadyExistsException(Errors.PREFERRED_NICKNAME_IS_TAKEN.getErrorMessage(preferredNickname));
        }

        this.nicknames = nicknames;
        this.preferredNickname = preferredNickname;
    }

}
