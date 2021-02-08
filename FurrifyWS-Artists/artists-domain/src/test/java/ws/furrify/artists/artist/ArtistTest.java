package ws.furrify.artists.artist;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.shared.exception.InvalidDataGivenException;
import ws.furrify.shared.exception.RecordAlreadyExistsException;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArtistTest {

    // Mocked in beforeAll()
    private static ArtistRepository artistRepository;

    private ArtistSnapshot artistSnapshot;
    private Artist artist;

    @BeforeEach
    void setUp() {
        artistSnapshot = ArtistSnapshot.builder()
                .id(0L)
                .artistId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .preferredNickname("Test")
                .nicknames(new HashSet<>(Arrays.asList("Test", "Test2")))
                .createDate(ZonedDateTime.now())
                .build();

        artist = Artist.restore(artistSnapshot);
    }

    @BeforeAll
    static void beforeAll() {
        artistRepository = mock(ArtistRepository.class);
    }

    @Test
    @DisplayName("Artist restore from snapshot")
    void restore() {
        // Given artistSnapshot
        // When restore() method called
        Artist artist = Artist.restore(artistSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(artistSnapshot, artist.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from artist")
    void getSnapshot() {
        // Given artist
        // When getSnapshot() method called
        ArtistSnapshot artistSnapshot = artist.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.artistSnapshot, artistSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Update nicknames with new preferred nickname")
    void updateNicknames() {
        // Given new preferredNickname
        String newPreferredNickname = "Test2";
        // When updateNicknames() method called
        when(artistRepository.existsByOwnerIdAndPreferredNickname(artistSnapshot.getOwnerId(), newPreferredNickname)).thenReturn(false);
        // Then update preferred nickname
        artist.updateNicknames(null, newPreferredNickname, artistRepository);

        assertEquals(
                newPreferredNickname,
                artist.getSnapshot().getPreferredNickname(),
                "Artist preferred nickname was not updated."
        );
    }

    @Test
    @DisplayName("Update nicknames with existing new preferred nickname")
    void updateNicknames2() {
        // Given existing new preferredNickname
        String newPreferredNickname = "Test2";
        // When updateNicknames() method called
        when(artistRepository.existsByOwnerIdAndPreferredNickname(artistSnapshot.getOwnerId(), newPreferredNickname)).thenReturn(true);
        // Then throw record already exists exception

        assertThrows(
                RecordAlreadyExistsException.class,
                () -> artist.updateNicknames(null, newPreferredNickname, artistRepository),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update nicknames with new nicknames")
    void updateNicknames3() {
        // Given new nicknames
        Set<String> newNicknames = new HashSet<>(Arrays.asList("Test", "Test3"));
        // When updateNicknames() method called
        when(artistRepository.existsByOwnerIdAndPreferredNickname(artistSnapshot.getOwnerId(), artistSnapshot.getPreferredNickname())).thenReturn(false);
        // Then update preferred nickname
        artist.updateNicknames(newNicknames, null, artistRepository);

        assertEquals(
                newNicknames,
                artist.getSnapshot().getNicknames(),
                "Artist nicknames were not updated."
        );
    }

    @Test
    @DisplayName("Update nicknames with new nicknames without preferred nickname")
    void updateNicknames4() {
        // Given new nicknames without preferred nickname
        Set<String> newNicknames = Collections.singleton("Test3");
        // When updateNicknames() method called
        when(artistRepository.existsByOwnerIdAndPreferredNickname(artistSnapshot.getOwnerId(), artistSnapshot.getPreferredNickname())).thenReturn(true);
        // Then throw InvalidDataGivenException

        assertThrows(
                InvalidDataGivenException.class,
                () -> artist.updateNicknames(newNicknames, null, artistRepository),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update nicknames with new preferredNickname not contained in nicknames")
    void updateNicknames5() {
        // Given new preferredNickname not contained in nicknames
        String newPreferredNickname = "Test3";
        // When updateNicknames() method called
        when(artistRepository.existsByOwnerIdAndPreferredNickname(artistSnapshot.getOwnerId(), artistSnapshot.getPreferredNickname())).thenReturn(true);
        // Then throw InvalidDataGivenException

        assertThrows(
                InvalidDataGivenException.class,
                () -> artist.updateNicknames(null, newPreferredNickname, artistRepository),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update nicknames with empty set")
    void updateNicknames6() {
        // Given new nicknames without preferred nickname
        Set<String> newNicknames = Collections.emptySet();
        // When updateNicknames() method called
        when(artistRepository.existsByOwnerIdAndPreferredNickname(artistSnapshot.getOwnerId(), artistSnapshot.getPreferredNickname())).thenReturn(true);
        // Then throw InvalidDataGivenException

        assertThrows(
                InvalidDataGivenException.class,
                () -> artist.updateNicknames(newNicknames, null, artistRepository),
                "Exception was not thrown."
        );
    }
}