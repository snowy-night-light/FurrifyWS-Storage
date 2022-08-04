package ws.furrify.artists.avatar;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.artist.ArtistServiceClient;
import ws.furrify.artists.artist.query.ArtistDetailsQueryDTO;
import ws.furrify.artists.avatar.dto.AvatarDTO;
import ws.furrify.artists.avatar.dto.AvatarDtoFactory;
import ws.furrify.artists.avatar.strategy.AvatarUploadStrategy;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvatarFacadeTest {

    private static AvatarRepository avatarRepository;
    private static AvatarFacade avatarFacade;
    private static ArtistServiceClient artistServiceClient;
    private static AvatarUploadStrategy avatarUploadStrategy;

    private AvatarDTO avatarDTO;
    private Avatar avatar;
    private AvatarSnapshot avatarSnapshot;
    private MultipartFile avatarFile;

    @BeforeAll
    static void beforeAll() {
        avatarRepository = mock(AvatarRepository.class);
        artistServiceClient = mock(ArtistServiceClient.class);
        avatarUploadStrategy = mock(AvatarUploadStrategy.class);

        var avatarQueryRepository = mock(AvatarQueryRepository.class);

        var avatarFactory = new AvatarFactory();
        var avatarDTOFactory = new AvatarDtoFactory(avatarQueryRepository);
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<AvatarEvent>) mock(DomainEventPublisher.class);

        avatarFacade = new AvatarFacade(
                new CreateAvatarImpl(artistServiceClient, avatarRepository, avatarFactory, avatarUploadStrategy, eventPublisher),
                new ReplaceAvatarImpl(eventPublisher, avatarRepository, avatarUploadStrategy),
                new UpdateAvatarImpl(eventPublisher, avatarRepository, avatarUploadStrategy),
                new DeleteAvatarImpl(avatarRepository, eventPublisher, avatarUploadStrategy),
                avatarRepository,
                avatarFactory,
                avatarDTOFactory,
                avatarUploadStrategy
        );
    }

    @BeforeEach
    @SneakyThrows
    void setUp() {
        avatarDTO = AvatarDTO.builder()
                .ownerId(UUID.randomUUID())
                .avatarId(UUID.randomUUID())
                .artistId(UUID.randomUUID())
                .extension(AvatarExtension.EXTENSION_PNG)
                .filename("yes.png")
                .fileUri(new URI("/test"))
                .thumbnailUri(new URI("/test"))
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .createDate(ZonedDateTime.now())
                .build();

        avatar = new AvatarFactory().from(avatarDTO);
        avatarSnapshot = avatar.getSnapshot();

        avatarFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "example.png";
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
                return getClass().getClassLoader().getResourceAsStream("example.png");
            }

            @Override
            public void transferTo(final File file) throws IOException, IllegalStateException {

            }
        };
    }

    @Test
    @DisplayName("Create avatar")
    void createAvatar() throws MalformedURLException, URISyntaxException {
        // Given ownerId, avatarDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();

        ArtistDetailsQueryDTO artistQueryDTO = new ArtistDetailsQueryDTO(null);

        // When createAvatar() method called
        when(artistServiceClient.getUserArtist(any(), any())).thenReturn(artistQueryDTO);
        when(avatarUploadStrategy.uploadAvatarWithGeneratedThumbnail(any(), any())).thenReturn(new AvatarUploadStrategy.UploadedAvatarFile(
                new URI("https://example.com"),
                new URI("https://example.com")
        ));
        // Then return generated uuid
        assertNotNull(avatarFacade.createAvatar(userId, artistId, avatarDTO, avatarFile), "AvatarId was not returned.");
    }


    @Test
    @DisplayName("Create avatar with non existing artistId")
    void createAvatar2() {
        // Given ownerId, non-existing artistId, avatarDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();

        // When createAvatar() method called
        when(artistServiceClient.getUserArtist(any(), any())).thenReturn(null);
        // Then throw exception
        assertThrows(
                RecordNotFoundException.class,
                () -> avatarFacade.createAvatar(userId, artistId, avatarDTO, avatarFile),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace avatar")
    void replaceAvatar() throws URISyntaxException {
        // Given ownerId, postId, avatarId, avatarId and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID avatarId = UUID.randomUUID();

        // When replaceAvatar() method called
        when(avatarRepository.findByOwnerIdAndArtistIdAndAvatarId(any(), any(), any())).thenReturn(Optional.of(avatar));
        when(avatarUploadStrategy.uploadAvatarWithGeneratedThumbnail(any(), any())).thenReturn(new AvatarUploadStrategy.UploadedAvatarFile(
                new URI("/test"),
                new URI("/test2")
        ));
        // Then return generated uuid
        assertDoesNotThrow(() -> avatarFacade.replaceAvatar(userId, postId, avatarId, avatarDTO, avatarFile), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Replace avatar with non existing avatarId")
    void replaceAvatar2() {
        // Given ownerId, postId, non-existing avatarId, avatarDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID avatarId = UUID.randomUUID();

        // When replaceAvatar() method called
        when(avatarRepository.findByOwnerIdAndArtistIdAndAvatarId(any(), any(), any())).thenReturn(Optional.empty());
        // Then throw exception
        assertThrows(
                RecordNotFoundException.class,
                () -> avatarFacade.replaceAvatar(userId, postId, avatarId, avatarDTO, avatarFile),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update avatar")
    void updateAvatar() throws URISyntaxException {
        // Given ownerId, postId, avatarId, avatarDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID avatarId = UUID.randomUUID();

        // When replaceAvatar() method called
        when(avatarRepository.findByOwnerIdAndArtistIdAndAvatarId(any(), any(), any())).thenReturn(Optional.of(avatar));
        when(avatarUploadStrategy.uploadAvatarWithGeneratedThumbnail(any(), any())).thenReturn(new AvatarUploadStrategy.UploadedAvatarFile(
                new URI("/test"),
                new URI("/test2")
        ));
        // Then return generated uuid
        assertDoesNotThrow(() -> avatarFacade.updateAvatar(userId, postId, avatarId, avatarDTO, avatarFile), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Update avatar with non existing avatarId")
    void updateAvatar2() {
        // Given ownerId, postId, non-existing avatarId, avatarDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID avatarId = UUID.randomUUID();

        // When replaceAvatar() method called
        when(avatarRepository.findByOwnerIdAndArtistIdAndAvatarId(any(), any(), any())).thenReturn(Optional.empty());
        // Then throw exception
        assertThrows(
                RecordNotFoundException.class,
                () -> avatarFacade.updateAvatar(userId, postId, avatarId, avatarDTO, avatarFile),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Delete avatar")
    void deleteAvatar() {
        // Given userId, artistId and avatarId
        UUID userId = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();
        UUID avatarId = UUID.randomUUID();
        // When deleteAvatar() method called
        when(avatarRepository.existsByOwnerIdAndArtistIdAndAvatarId(userId, artistId, avatarId)).thenReturn(true);
        // Then run successfully
        assertDoesNotThrow(() -> avatarFacade.deleteAvatar(userId, artistId, avatarId), "Exception was thrown");
    }

    @Test
    @DisplayName("Delete avatar with non existing avatarId")
    void deleteAvatar2() {
        // Given userId, artistId and non existing avatarId
        UUID userId = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();
        UUID avatarId = UUID.randomUUID();
        // When deleteAvatar() method called
        when(avatarRepository.existsByOwnerIdAndArtistIdAndAvatarId(userId, artistId, avatarId)).thenReturn(false);
        // Then throw record not found exception
        assertThrows(RecordNotFoundException.class, () -> avatarFacade.deleteAvatar(userId, artistId, avatarId), "Exception was not thrown");
    }
}