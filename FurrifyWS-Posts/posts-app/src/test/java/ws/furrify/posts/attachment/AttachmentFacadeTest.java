package ws.furrify.posts.attachment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.dto.AttachmentDtoFactory;
import ws.furrify.posts.attachment.strategy.AttachmentUploadStrategy;
import ws.furrify.posts.post.dto.PostServiceClient;
import ws.furrify.posts.post.dto.query.PostDetailsDTO;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AttachmentFacadeTest {

    private static AttachmentRepository attachmentRepository;
    private static AttachmentFacade attachmentFacade;
    private static AttachmentUploadStrategy attachmentUploadStrategy;
    private static PostServiceClient postServiceClient;

    private AttachmentDTO attachmentDTO;
    private Attachment attachment;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        attachmentDTO = AttachmentDTO.builder()
                .ownerId(UUID.randomUUID())
                .postId(UUID.randomUUID())
                .attachmentId(UUID.randomUUID())
                .extension(AttachmentExtension.PSD)
                .filename("example.psd")
                .fileUrl(new URL("https://example.com/"))
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .createDate(ZonedDateTime.now())
                .build();

        attachment = new AttachmentFactory().from(attachmentDTO);
    }

    @BeforeAll
    static void beforeAll() {
        attachmentRepository = mock(AttachmentRepository.class);
        var attachmentQueryRepository = mock(AttachmentQueryRepository.class);
        postServiceClient = mock(PostServiceClient.class);
        attachmentUploadStrategy = mock(AttachmentUploadStrategy.class);

        var attachmentFactory = new AttachmentFactory();
        var attachmentDtoFactory = new AttachmentDtoFactory(attachmentQueryRepository);

        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<AttachmentEvent>) mock(DomainEventPublisher.class);

        attachmentFacade = new AttachmentFacade(
                new CreateAttachmentImpl(postServiceClient, attachmentFactory, attachmentUploadStrategy, eventPublisher),
                new DeleteAttachmentImpl(eventPublisher, attachmentRepository),
                new UpdateAttachmentImpl(eventPublisher, attachmentRepository),
                new ReplaceAttachmentImpl(eventPublisher, attachmentRepository),
                attachmentRepository,
                attachmentFactory,
                attachmentDtoFactory
        );
    }

    @Test
    @DisplayName("Create attachment")
    void createAttachment() throws MalformedURLException {
        // Given ownerId, postId, attachmentDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        PostDetailsDTO postDetailsDTO = new PostDetailsDTO(null, null, null, null, null, null, null, null, null);

        MultipartFile attachmentFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "example.psd";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return getClass().getClassLoader().getResourceAsStream("example.psd");
            }

            @Override
            public void transferTo(final File file) throws IOException, IllegalStateException {

            }
        };
        // When createAttachment() method called
        when(postServiceClient.getUserPost(any(), any())).thenReturn(postDetailsDTO);
        when(attachmentUploadStrategy.uploadAttachment(any(), any())).thenReturn(new AttachmentUploadStrategy.UploadedAttachmentFile(
                new URL("https://example.com")
        ));
        // Then return generated uuid
        assertNotNull(attachmentFacade.createAttachment(userId, postId, attachmentDTO, attachmentFile), "AttachmentId was not returned.");
    }

    @Test
    @DisplayName("Create attachment with non existing postId")
    void createAttachment2() {
        // Given ownerId, non-existing postId, attachmentDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        MultipartFile attachmentFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "example.psd";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return getClass().getClassLoader().getResourceAsStream("example.psd");
            }

            @Override
            public void transferTo(final File file) throws IOException, IllegalStateException {

            }
        };
        // When createAttachment() method called
        when(postServiceClient.getUserPost(any(), any())).thenReturn(null);
        // Then throw exception
        assertThrows(
                RecordNotFoundException.class,
                () -> attachmentFacade.createAttachment(userId, postId, attachmentDTO, attachmentFile),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace attachment")
    void replaceAttachment() {
        // Given attachmentDto, userId and postId and attachmentId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();
        // When replaceAttachment() method called
        when(attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId))
                .thenReturn(Optional.of(attachment));
        // Then run successfully
        assertDoesNotThrow(() -> attachmentFacade.replaceAttachment(userId, postId, attachmentId, attachmentDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Replace attachment with non existing attachmentId")
    void replaceAttachment2() {
        // Given attachmentDTO, userId, postId and non existing attachmentId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();
        // When replaceAttachment() method called
        when(attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> attachmentFacade.replaceAttachment(userId, postId, attachmentId, attachmentDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update attachment")
    void updateAttachment() {
        // Given attachmentDTO, userId, postId and attachmentId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();
        // When updateAttachment() method called
        when(attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId))
                .thenReturn(Optional.of(attachment));
        // Then run successfully
        assertDoesNotThrow(() -> attachmentFacade.updateAttachment(userId, postId, attachmentId, attachmentDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Update attachment with non existing attachmentId")
    void updateAttachment2() {
        // Given attachmentDTO, userId, postId and non existing attachmentId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();
        // When updateAttachment() method called
        when(attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId))
                .thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> attachmentFacade.updateAttachment(userId, postId, attachmentId, attachmentDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Delete attachment")
    void deleteAttachment() {
        // Given userId, postId and attachmentId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();
        // When deleteAttachment() method called
        when(attachmentRepository.existsByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)).thenReturn(true);
        // Then run successfully
        assertDoesNotThrow(() -> attachmentFacade.deleteAttachment(userId, postId, attachmentId), "Exception was thrown");
    }

    @Test
    @DisplayName("Delete attachment with non existing attachmentId")
    void deleteAttachment2() {
        // Given userId, postId and non existing attachmentId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();
        // When deleteAttachment() method called
        when(attachmentRepository.existsByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)).thenReturn(false);
        // Then throw record not found exception
        assertThrows(RecordNotFoundException.class, () -> attachmentFacade.deleteAttachment(userId, postId, attachmentId), "Exception was not thrown");
    }
}
