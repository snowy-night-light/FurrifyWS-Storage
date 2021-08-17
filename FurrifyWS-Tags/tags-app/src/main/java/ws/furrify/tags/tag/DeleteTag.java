package ws.furrify.tags.tag;

import java.util.UUID;

interface DeleteTag {
    void deleteTag(final UUID userId, final String value);
}
