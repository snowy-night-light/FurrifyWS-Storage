package ws.furrify.posts.post;

import ws.furrify.posts.post.dto.PostDTO;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

class PostFactory {

    Post from(PostDTO postDTO) {
        PostSnapshot postSnapshot = PostSnapshot.builder()
                .id(postDTO.getId())
                .postId(
                        (postDTO.getPostId() != null) ? postDTO.getPostId() : UUID.randomUUID()
                )
                .ownerId(postDTO.getOwnerId())
                .title(postDTO.getTitle())
                .description(postDTO.getDescription())
                .tags(
                        (postDTO.getTags() != null) ? postDTO.getTags() : new HashSet<>()
                )
                .artists(
                        (postDTO.getArtists() != null) ? postDTO.getArtists() : new HashSet<>()
                )
                .mediaSet(
                        (postDTO.getMediaSet() != null) ? postDTO.getMediaSet() : new HashSet<>()
                )
                .createDate(
                        (postDTO.getCreateDate() != null) ? postDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Post.restore(postSnapshot);
    }

}
