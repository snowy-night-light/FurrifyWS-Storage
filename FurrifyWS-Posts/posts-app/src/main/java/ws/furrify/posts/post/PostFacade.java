package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.post.dto.PostDTO;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class PostFacade {

    private final CreatePostPort createPostAdapter;
    private final DeletePostPort deletePostAdapter;
    private final PostRepository postRepository;
    private final PostFactory postFactory;

    /**
     * Handle incoming events.
     *
     * @param postEvent Post event instance received form kafka.
     */
    public void handleEvent(final UUID key, final PostEvent postEvent) {
        UUID targetId = UUID.fromString(postEvent.getTargetId());

        switch (PostEventType.valueOf(postEvent.getState())) {
            case CREATED -> {
                PostDTO postDTO = PostDTO.builder()
                        .postId(targetId)
                        .ownerId(key)
                        .title(postEvent.getData().getTitle())
                        .description(postEvent.getData().getDescription())
                        .createDate(new Date(postEvent.getData().getCreateDate()).toInstant().atZone(ZoneId.systemDefault()))
                        .build();

                savePost(postDTO);
            }
            case REMOVED -> deletePostByPostId(targetId);
            default -> log.warning("State received from kafka is not defined. State=" + postEvent.getState());
        }
    }

    /**
     * Creates post.
     *
     * @param userId  User uuid to assign post to.
     * @param postDTO Post to create.
     * @return Created post UUID.
     */
    public UUID createPost(final UUID userId, final PostDTO postDTO) {
        return createPostAdapter.createPost(userId, postDTO);
    }

    /**
     * Deletes post.
     *
     * @param postId Post UUID.
     */
    public void deletePost(final UUID userId, final UUID postId) {
        deletePostAdapter.deletePost(userId, postId);
    }

    private void savePost(final PostDTO postDTO) {
        postRepository.save(postFactory.from(postDTO));
    }

    private void deletePostByPostId(final UUID postId) {
        postRepository.deleteByPostId(postId);
    }
}
