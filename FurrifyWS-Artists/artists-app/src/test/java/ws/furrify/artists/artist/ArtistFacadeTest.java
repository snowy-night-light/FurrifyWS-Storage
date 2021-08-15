package ws.furrify.artists.artist;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.dto.ArtistDtoFactory;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArtistFacadeTest {

    private static ArtistRepository artistRepository;
    private static ArtistFacade artistFacade;

    private ArtistDTO artistDTO;
    private Artist artist;
    private ArtistSnapshot artistSnapshot;

    @BeforeEach
    void setUp() {
        artistDTO = ArtistDTO.builder()
                .nicknames(Collections.singleton("Test"))
                .preferredNickname("Test")
                .ownerId(UUID.randomUUID())
                .createDate(ZonedDateTime.now())
                .build();

        artist = new ArtistFactory().from(artistDTO);
        artistSnapshot = artist.getSnapshot();
    }

    @BeforeAll
    static void beforeAll() {
        artistRepository = mock(ArtistRepository.class);

        var artistQueryRepository = mock(ArtistQueryRepository.class);

        var artistFactory = new ArtistFactory();
        var artistDTOFactory = new ArtistDtoFactory(artistQueryRepository);
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<ArtistEvent>) mock(DomainEventPublisher.class);

        artistFacade = new ArtistFacade(
                new CreateArtistImpl(artistRepository, artistFactory, eventPublisher),
                new DeleteArtistImpl(artistRepository, eventPublisher),
                new UpdateArtistImpl(artistRepository, eventPublisher),
                new ReplaceArtistImpl(artistRepository, eventPublisher),
                artistRepository,
                artistFactory,
                artistDTOFactory
        );
    }

    @Test
    @DisplayName("Create artist")
    void createArtist() {
        // Given userId and artistDTO
        UUID userId = artistSnapshot.getOwnerId();
        // When createArtist() method called
        // Then create artist and return generated uuid
        assertNotNull(artistFacade.createArtist(userId, artistDTO), "ArtistId was not returned.");
    }

    @Test
    @DisplayName("Replace artist")
    void replaceArtist() {
        // Given existing userId and artistDTO
        UUID userId = artistSnapshot.getOwnerId();
        // When replaceArtist() method called
        when(artistRepository.findByOwnerIdAndArtistId(userId, artistSnapshot.getArtistId())).thenReturn(Optional.of(artist));
        // Then
        assertDoesNotThrow(() -> artistFacade.replaceArtist(userId, artistSnapshot.getArtistId(), artistDTO),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Replace artist with non existing artistId")
    void replaceArtist2() {
        // Given non existing userId and artistDTO
        UUID userId = artistSnapshot.getOwnerId();
        // When replaceArtist() method called
        when(artistRepository.findByOwnerIdAndArtistId(userId, artistSnapshot.getArtistId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> artistFacade.replaceArtist(userId, artistSnapshot.getArtistId(), artistDTO),
                "Exception was not thrown.");
    }

    @Test
    @DisplayName("Update artist")
    void updateArtist() {
        // Given existing userId and artistDTO
        UUID userId = artistSnapshot.getOwnerId();
        // When updateArtist() method called
        when(artistRepository.findByOwnerIdAndArtistId(userId, artistSnapshot.getArtistId())).thenReturn(Optional.of(artist));
        // Then
        assertDoesNotThrow(() -> artistFacade.updateArtist(userId, artistSnapshot.getArtistId(), artistDTO),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Update artist with non existing artistId")
    void updateArtist2() {
        // Given non existing userId and artistDTO
        UUID userId = artistSnapshot.getOwnerId();
        // When updateArtist() method called
        when(artistRepository.findByOwnerIdAndArtistId(userId, artistSnapshot.getArtistId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> artistFacade.updateArtist(userId, UUID.randomUUID(), artistDTO),
                "Exception was not thrown.");
    }


    @Test
    @DisplayName("Delete artist")
    void deleteArtist() {
        // Given artistId
        UUID userId = artistSnapshot.getOwnerId();
        // When deleteArtist() method called
        when(artistRepository.existsByOwnerIdAndArtistId(userId, artistSnapshot.getArtistId())).thenReturn(true);
        // Then
        assertDoesNotThrow(() -> artistFacade.deleteArtist(userId, artistSnapshot.getArtistId()),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Delete artist with non existing artistId")
    void deleteArtist2() {
        // Given artistId
        UUID userId = artistSnapshot.getOwnerId();
        // When deleteArtist() method called
        when(artistRepository.existsByOwnerIdAndArtistId(userId, artistSnapshot.getArtistId())).thenReturn(false);
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> artistFacade.deleteArtist(userId, artistSnapshot.getArtistId()),
                "Exception was not thrown.");
    }
}