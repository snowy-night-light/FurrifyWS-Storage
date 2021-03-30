package ws.furrify.posts.post;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.artist.ArtistServiceClient;
import ws.furrify.posts.artist.dto.query.ArtistDetailsQueryDTO;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.dto.PostDtoFactory;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MediaFacadeTest {

    private static PostRepository postRepository;
    private static PostFacade postFacade;
    private static TagServiceClient tagServiceClient;
    private static ArtistServiceClient artistServiceClient;

    private PostDTO postDTO;
    private Post post;
    private PostTag postTag;
    private PostArtist postArtist;
    private TagDetailsQueryDTO tagDetailsQueryDTO;
    private ArtistDetailsQueryDTO artistDetailsQueryDTO;

    @BeforeEach
    void setUp() {
        postTag = new PostTag("walking", "ACTION");
        postArtist = new PostArtist(UUID.randomUUID(), "preferred_nickname");

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

        artistDetailsQueryDTO = new ArtistDetailsQueryDTO() {
            @Override
            public UUID getArtistId() {
                return UUID.randomUUID();
            }

            @Override
            public String getPreferredNickname() {
                return "preferred_nickname";
            }
        };

        postDTO = PostDTO.builder()
                .title("Test")
                .description("dsa")
                .ownerId(UUID.randomUUID())
                .tags(Collections.singleton(postTag))
                .artists(Collections.singleton(postArtist))
                .createDate(ZonedDateTime.now())
                .build();

        post = new PostFactory().from(postDTO);
    }

    @BeforeAll
    static void beforeAll() {
        postRepository = mock(PostRepository.class);
        tagServiceClient = mock(TagServiceClient.class);
        artistServiceClient = mock(ArtistServiceClient.class);

        var postFactory = new PostFactory();
        var postDTOFactory = new PostDtoFactory();
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<PostEvent>) mock(DomainEventPublisher.class);

        postFacade = new PostFacade(
                new CreatePostAdapter(postFactory, eventPublisher, tagServiceClient, artistServiceClient),
                new DeletePostAdapter(eventPublisher, postRepository),
                new UpdatePostAdapter(eventPublisher, postRepository, tagServiceClient, artistServiceClient),
                new ReplacePostAdapter(eventPublisher, postRepository, tagServiceClient, artistServiceClient),
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
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(tagDetailsQueryDTO);
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(artistDetailsQueryDTO);
        // Then return generated uuid
        assertNotNull(postFacade.createPost(userId, postDTO), "PostId was not returned.");
    }

    @Test
    @DisplayName("Create post with non existing tag")
    void createPost2() {
        // Given ownerId and postDTO with non existing tag
        UUID userId = UUID.randomUUID();
        // When createPost() method called
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(null);
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(artistDetailsQueryDTO);
        // Then return generated uuid
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.createPost(userId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create post with non existing artist")
    void createPost3() {
        // Given ownerId and postDTO with non existing artist
        UUID userId = UUID.randomUUID();
        // When createPost() method called
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(tagDetailsQueryDTO);
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(null);
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
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(tagDetailsQueryDTO);
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(artistDetailsQueryDTO);
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
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(artistDetailsQueryDTO);
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(null);
        // Then run successfully
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.replacePost(userId, postId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace post with non existing artist")
    void replacePost4() {
        // Given postDTO with non existing artist, userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When replacePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.of(post));
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(null);
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(tagDetailsQueryDTO);
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
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(tagDetailsQueryDTO);
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(artistDetailsQueryDTO);
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
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(artistDetailsQueryDTO);
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(null);
        // Then run successfully
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.updatePost(userId, postId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update post with non existing artist")
    void updatePost4() {
        // Given postDTO with non existing artist, userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When replacePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.of(post));
        when(tagServiceClient.getUserTag(userId, postTag.getValue())).thenReturn(tagDetailsQueryDTO);
        when(artistServiceClient.getUserArtist(userId, postArtist.getArtistId())).thenReturn(null);
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