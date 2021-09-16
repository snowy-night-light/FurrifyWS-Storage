package ws.furrify.sources.source;

import ws.furrify.sources.source.dto.SourceDTO;

import java.util.UUID;

interface ReplaceSource {
    void replaceSource(UUID ownerId, UUID sourceId, SourceDTO sourceDTO);
}
