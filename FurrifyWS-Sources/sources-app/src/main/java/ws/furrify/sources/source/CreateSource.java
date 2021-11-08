package ws.furrify.sources.source;

import ws.furrify.sources.source.dto.SourceDTO;

import java.util.UUID;

interface CreateSource {
    UUID createSource(final UUID ownerId, final SourceDTO sourceDTO);
}
