package ws.furrify.posts.post.dto.query;

import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostTag;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface PostDetailsQueryDTO extends Serializable {

    UUID getPostId();

    UUID getOwnerId();

    String getTitle();

    String getDescription();

    Set<PostTag> getTags();

    Set<PostArtist> getArtists();

    ZonedDateTime getCreateDate();
}
