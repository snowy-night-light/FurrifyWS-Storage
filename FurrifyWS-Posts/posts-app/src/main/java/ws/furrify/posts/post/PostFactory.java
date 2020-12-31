package ws.furrify.posts.post;

import ws.furrify.posts.post.dto.PostDTO;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

class PostFactory {

    Post from(PostDTO postDTO) {
        PostSnapshot postSnapshot = new PostSnapshot(
                postDTO.getId(),
                (postDTO.getPostId() != null) ? postDTO.getPostId() : UUID.randomUUID(),
                postDTO.getOwnerId(),
                postDTO.getTitle(),
                postDTO.getDescription(),
                (postDTO.getTags() != null) ? postDTO.getTags() : new HashSet<>(),
                (postDTO.getCreateDate() != null) ? postDTO.getCreateDate() : ZonedDateTime.now()
        );

        return Post.restore(postSnapshot);
    }

}
