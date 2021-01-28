package ws.furrify.posts.tag;

import java.util.UUID;

interface DeleteTagPort {
    void deleteTag(final UUID userId, final String value);
}
