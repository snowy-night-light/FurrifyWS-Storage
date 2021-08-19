package ws.furrify.sources.source;

import java.util.UUID;

interface DeleteSource {
    void deleteSource(UUID ownerId, UUID sourceId);
}
