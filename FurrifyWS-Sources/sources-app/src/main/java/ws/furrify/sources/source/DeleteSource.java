package ws.furrify.sources.source;

import java.util.UUID;

interface DeleteSource {
    void deleteSource(final UUID ownerId, final UUID sourceId);
}
