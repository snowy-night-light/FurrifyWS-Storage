package ws.furrify.posts.post;

import ws.furrify.posts.post.dto.PostDTO;

import java.util.UUID;

interface UpdatePost {

    void updatePost(UUID userId, UUID postId, PostDTO postDTO);

}
