package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.dto.SourceDtoFactory;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class SourceFacade {

    private final CreateSource createSourceImpl;
    private final DeleteSource deleteSourceImpl;
    private final UpdateSource updateSourceImpl;
    private final ReplaceSourceImpl replaceSourceImpl;
    private final SourceRepository sourceRepository;
    private final SourceFactory sourceFactory;
    private final SourceDtoFactory sourceDTOFactory;

    /**
     * Handle incoming source events.
     *
     * @param sourceEvent Source event instance received from kafka.
     */
    public void handleEvent(final UUID key, final SourceEvent sourceEvent) {
        SourceDTO sourceDTO = sourceDTOFactory.from(key, sourceEvent);

        switch (DomainEventPublisher.SourceEventType.valueOf(sourceEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> saveSourceToDatabase(sourceDTO);
            case REMOVED -> deleteSourceBySourceIdFromDatabase(sourceDTO.getSourceId());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + sourceEvent.getState() + " Topic=source_events");
        }
    }

    /**
     * Creates Source.
     *
     * @param ownerId   Owner of source to be created.
     * @param sourceDTO SourceDTO.
     * @return Created record UUID.
     */
    public UUID createSource(UUID ownerId, SourceDTO sourceDTO) {
        return createSourceImpl.createSource(ownerId, sourceDTO);
    }

    /**
     * Replaces all fields in Source.
     *
     * @param ownerId   Owner UUID of Source.
     * @param sourceId  Source UUID to be replaced.
     * @param sourceDTO SourceDTO with replace details.
     */
    public void replaceSource(UUID ownerId, UUID sourceId, SourceDTO sourceDTO) {
        replaceSourceImpl.replaceSource(ownerId, sourceId, sourceDTO);
    }

    /**
     * Updates changed fields in Source from DTO.
     *
     * @param ownerId   Owner UUID of Source.
     * @param sourceId  Source UUID to be updated.
     * @param sourceDTO SourceDTO with some changes.
     */
    public void updateSource(UUID ownerId, UUID sourceId, SourceDTO sourceDTO) {
        updateSourceImpl.updateSource(ownerId, sourceId, sourceDTO);
    }

    /**
     * Deletes Source.
     *
     * @param ownerId  Owner UUID of Source.
     * @param sourceId Source UUID.
     */
    public void deleteSource(final UUID ownerId, final UUID sourceId) {
        deleteSourceImpl.deleteSource(ownerId, sourceId);
    }

    private void deleteSourceBySourceIdFromDatabase(final UUID sourceId) {
        sourceRepository.deleteBySourceId(sourceId);
    }

    private void saveSourceToDatabase(final SourceDTO sourceDTO) {
        sourceRepository.save(
                Source.restore(
                        SourceSnapshot.builder()
                                .id(sourceDTO.getId())
                                .postId(sourceDTO.getPostId())
                                .originId(sourceDTO.getOriginId())
                                .sourceId(sourceDTO.getSourceId())
                                .ownerId(sourceDTO.getOwnerId())
                                .strategy(sourceDTO.getStrategy())
                                .data(sourceDTO.getData())
                                .originType(sourceDTO.getOriginType())
                                .createDate(sourceDTO.getCreateDate())
                                .build()
                )
        );
    }

}
