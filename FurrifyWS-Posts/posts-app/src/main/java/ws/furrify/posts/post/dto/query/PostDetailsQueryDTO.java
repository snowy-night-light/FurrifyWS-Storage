package ws.furrify.posts.post.dto.query;

import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostAttachment;
import ws.furrify.posts.post.vo.PostMedia;
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

    Set<PostMedia> getMediaSet();

    Set<PostAttachment> getAttachments();

    ZonedDateTime getCreateDate();

    /**
     * Create dto interface from given data.
     *
     * @return Interface instance.
     */
    static PostDetailsQueryDTO of(
            UUID postId,
            UUID ownerId,
            String title,
            String description,
            Set<PostTag> tags,
            Set<PostArtist> artists,
            Set<PostMedia> mediaSet,
            Set<PostAttachment> attachments,
            ZonedDateTime createDate
    ) {
        return new PostDetailsQueryDTO() {
            @Override
            public UUID getPostId() {
                return postId;
            }

            @Override
            public UUID getOwnerId() {
                return ownerId;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public Set<PostTag> getTags() {
                return tags;
            }

            @Override
            public Set<PostArtist> getArtists() {
                return artists;
            }

            @Override
            public Set<PostMedia> getMediaSet() {
                return mediaSet;
            }

            @Override
            public Set<PostAttachment> getAttachments() {
                return attachments;
            }

            @Override
            public ZonedDateTime getCreateDate() {
                return createDate;
            }
        };
    }
}
