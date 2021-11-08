package ws.furrify.sources.source;

import ws.furrify.sources.source.dto.SourceDTO;

import java.util.UUID;

interface UpdateSource {
    void updateSource(final UUID ownerId, final UUID sourceId, final SourceDTO sourceDTO);
}
