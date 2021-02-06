package ws.furrify.tags.tag;

import java.util.UUID;

interface DeleteTagPort {
    void deleteTag(final UUID userId, final String value);
}
