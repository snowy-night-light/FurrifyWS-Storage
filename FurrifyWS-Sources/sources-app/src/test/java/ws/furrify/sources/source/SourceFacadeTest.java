package ws.furrify.sources.source;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.dto.SourceDtoFactory;
import ws.furrify.sources.source.strategy.DefaultSourceStrategy;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SourceFacadeTest {

    private static SourceRepository sourceRepository;
    private static SourceFacade sourceFacade;

    private SourceDTO sourceDTO;
    private Source source;
    private SourceSnapshot sourceSnapshot;

    @BeforeEach
    void setUp() {
        sourceDTO = SourceDTO.builder()
                .ownerId(UUID.randomUUID())
                .strategy(new DefaultSourceStrategy())
                .data(new HashMap<>())
                .createDate(ZonedDateTime.now())
                .build();

        source = new SourceFactory().from(sourceDTO);
        sourceSnapshot = source.getSnapshot();
    }

    @BeforeAll
    static void beforeAll() {
        sourceRepository = mock(SourceRepository.class);

        var sourceQueryRepository = mock(SourceQueryRepository.class);

        var sourceStrategyAttributeConverter = new SourceStrategyAttributeConverter();

        var sourceFactory = new SourceFactory();
        var sourceDTOFactory = new SourceDtoFactory(sourceQueryRepository, sourceStrategyAttributeConverter);
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<SourceEvent>) mock(DomainEventPublisher.class);

        sourceFacade = new SourceFacade(
                new CreateSourceImpl(sourceFactory, eventPublisher, sourceStrategyAttributeConverter),
                new DeleteSourceImpl(sourceRepository, eventPublisher),
                new UpdateSourceImpl(sourceRepository, eventPublisher, sourceStrategyAttributeConverter),
                new ReplaceSourceImpl(sourceRepository, eventPublisher, sourceStrategyAttributeConverter),
                sourceRepository,
                sourceFactory,
                sourceDTOFactory
        );
    }

    @Test
    @DisplayName("Create source")
    void createSource() {
        // Given userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When createSource() method called
        // Then create source and return generated uuid
        assertNotNull(sourceFacade.createSource(userId, sourceDTO), "SourceId was not returned.");
    }

    @Test
    @DisplayName("Replace source")
    void replaceSource() {
        // Given existing userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When replaceSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.of(source));
        // Then
        assertDoesNotThrow(() -> sourceFacade.replaceSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Replace source with non existing sourceId")
    void replaceSource2() {
        // Given non existing userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When replaceSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.replaceSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was not thrown.");
    }

    @Test
    @DisplayName("Update source")
    void updateSource() {
        // Given existing userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When updateSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.of(source));
        // Then
        assertDoesNotThrow(() -> sourceFacade.updateSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Update source with non existing sourceId")
    void updateSource2() {
        // Given non existing userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When updateSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.updateSource(userId, UUID.randomUUID(), sourceDTO),
                "Exception was not thrown.");
    }


    @Test
    @DisplayName("Delete source")
    void deleteSource() {
        // Given sourceId
        UUID userId = sourceSnapshot.getOwnerId();
        // When deleteSource() method called
        when(sourceRepository.existsByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(true);
        // Then
        assertDoesNotThrow(() -> sourceFacade.deleteSource(userId, sourceSnapshot.getSourceId()),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Delete source with non existing sourceId")
    void deleteSource2() {
        // Given sourceId
        UUID userId = sourceSnapshot.getOwnerId();
        // When deleteSource() method called
        when(sourceRepository.existsByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(false);
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.deleteSource(userId, sourceSnapshot.getSourceId()),
                "Exception was not thrown.");
    }
}