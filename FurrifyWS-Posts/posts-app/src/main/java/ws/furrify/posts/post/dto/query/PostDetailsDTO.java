package ws.furrify.posts.post.dto.query;

import lombok.AllArgsConstructor;
import lombok.Value;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostAttachment;
import ws.furrify.posts.post.vo.PostMedia;
import ws.furrify.posts.post.vo.PostTag;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Post class for queries.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor
public class PostDetailsDTO implements Serializable {

    UUID postId;

    UUID ownerId;

    String title;

    String description;

    Set<PostTag> tags;

    Set<PostArtist> artists;

    Set<PostMedia> mediaSet;

    Set<PostAttachment> attachments;

    ZonedDateTime createDate;
}