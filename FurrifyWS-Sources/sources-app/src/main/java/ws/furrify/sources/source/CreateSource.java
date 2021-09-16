package ws.furrify.sources.source;

import ws.furrify.sources.source.dto.SourceDTO;

import java.util.UUID;

interface CreateSource {
    UUID createSource(UUID ownerId, SourceDTO sourceDTO);
}
