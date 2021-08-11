package ws.furrify.posts.post.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostAttachment;
import ws.furrify.posts.post.vo.PostMedia;
import ws.furrify.posts.post.vo.PostTag;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class PostDTO {

    Long id;

    UUID postId;
    UUID ownerId;

    String title;
    String description;

    Set<PostTag> tags;

    Set<PostArtist> artists;

    Set<PostMedia> mediaSet;

    Set<PostAttachment> postAttachments;

    ZonedDateTime createDate;
}
