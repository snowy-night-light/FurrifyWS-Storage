package ws.furrify.posts.post;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.dto.PostDtoFactory;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;
import ws.furrify.shared.DomainEventPublisher;
import ws.furrify.shared.exception.RecordNotFoundException;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostFacadeTest {

    private static PostRepository postRepository;
    private static PostFacade postFacade;
    private static TagServiceClient tagServiceClient;

    private PostDTO postDTO;
    private Post post;
    private PostTag postTag;
    private TagDetailsQueryDTO tagDetailsQueryDTO;

    @BeforeEach
    void setUp() {
        postTag = new PostTag("walking", "ACTION");

        tagDetailsQueryDTO = new TagDetailsQueryDTO() {
            @Override
            public String getValue() {
                return postTag.getValue();
            }

            @Override
            public String getType() {
                return postTag.getType();
            }
        };

        postDTO = PostDTO.builder()
                .title("Test")
                .description("dsa")
                .ownerId(UUID.randomUUID())
                .tags(Collections.singleton(postTag))
                .createDate(ZonedDateTime.now())
                .build();

        post = new PostFactory().from(postDTO);
    }

    @BeforeAll
    static void beforeAll() {
        postRepository = mock(PostRepository.class);
        tagServiceClient = mock(TagServiceClient.class);

        var postFactory = new PostFactory();
        var postDTOFactory = new PostDtoFactory();
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<PostEvent>) mock(DomainEventPublisher.class);

        postFacade = new PostFacade(
                new CreatePostAdapter(postFactory, eventPublisher, tagServiceClient),
                new DeletePostAdapter(eventPublisher, postRepository),
                new UpdatePostAdapter(eventPublisher, postRepository, tagServiceClient),
                new ReplacePostAdapter(eventPublisher, postRepository, tagServiceClient),
                postRepository,
                postFactory,
                postDTOFactory
        );
    }

    @Test
    @DisplayName("Create post")
    void createPost() {
        // Given ownerId and postDTO
        UUID userId = UUID.randomUUID();
        // When createPost() method called
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(Optional.of(tagDetailsQueryDTO));
        // Then return generated uuid
        assertNotNull(postFacade.createPost(userId, postDTO), "PostId was not returned.");
    }

    @Test
    @DisplayName("Create post with non existing tag")
    void createPost2() {
        // Given ownerId and postDTO with non existing tag
        UUID userId = UUID.randomUUID();
        // When createPost() method called
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(Optional.empty());
        // Then return generated uuid
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.createPost(userId, postDTO),
                "Exception was not thrown."
        );
    }


    @Test
    @DisplayName("Replace post")
    void replacePost() {
        // Given postDTO, userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When replacePost() method called
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(Optional.of(tagDetailsQueryDTO));
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.of(post));
        // Then run successfully
        assertDoesNotThrow(() -> postFacade.replacePost(userId, postId, postDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Replace post with non existing postId")
    void replacePost2() {
        // Given postDTO, userId and non existing postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When replacePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.replacePost(userId, postId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace post with non existing tag")
    void replacePost3() {
        // Given postDTO with non existing tag, userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When replacePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.of(post));
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(Optional.empty());
        // Then run successfully
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.replacePost(userId, postId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update post")
    void updatePost() {
        // Given postDTO, userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When updatePost() method called
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(Optional.of(tagDetailsQueryDTO));
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.of(post));
        // Then run successfully
        assertDoesNotThrow(() -> postFacade.updatePost(userId, postId, postDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Update post with non existing postId")
    void updatePost2() {
        // Given postDTO, userId and non existing postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When updatePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.updatePost(userId, postId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update post with non existing tag")
    void updatePost3() {
        // Given postDTO with non existing tag, userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When replacePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.of(post));
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(Optional.empty());
        // Then run successfully
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.updatePost(userId, postId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Delete post")
    void deletePost() {
        // Given userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When deletePost() method called
        when(postRepository.existsByOwnerIdAndPostId(userId, postId)).thenReturn(true);
        // Then run successfully
        assertDoesNotThrow(() -> postFacade.deletePost(userId, postId), "Exception was thrown");
    }

    @Test
    @DisplayName("Delete post with non existing postId")
    void deletePost2() {
        // Given userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When deletePost() method called
        when(postRepository.existsByOwnerIdAndPostId(userId, postId)).thenReturn(false);
        // Then throw record not found exception
        assertThrows(RecordNotFoundException.class, () -> postFacade.deletePost(userId, postId), "Exception was not thrown");
    }
}