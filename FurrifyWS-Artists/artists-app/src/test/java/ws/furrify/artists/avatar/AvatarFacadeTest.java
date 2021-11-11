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

    @BeforeEach
    @SneakyThrows
    void setUp() {
        avatarDTO = AvatarDTO.builder()
                .ownerId(UUID.randomUUID())
                .avatarId(UUID.randomUUID())
                .artistId(UUID.randomUUID())
                .extension(AvatarExtension.PNG)
                .filename("yes.png")
                .fileUrl(new URI("/test"))
                .thumbnailUrl(new URI("/test"))
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .createDate(ZonedDateTime.now())
                .build();

        avatar = new AvatarFactory().from(avatarDTO);
        avatarSnapshot = avatar.getSnapshot();
    }

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
                new DeleteAvatarImpl(avatarRepository, eventPublisher),
                avatarRepository,
                avatarFactory,
                avatarDTOFactory
        );
    }

    @Test
    @DisplayName("Create avatar")
    void createAvatar() throws MalformedURLException, URISyntaxException {
        // Given ownerId, avatarDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();

        ArtistDetailsQueryDTO artistQueryDTO = new ArtistDetailsQueryDTO(null);

        MultipartFile avatarFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "test.png";
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
        // When createAvatar() method called
        when(artistServiceClient.getUserArtist(any(), any())).thenReturn(artistQueryDTO);
        when(avatarUploadStrategy.uploadAvatarWithGeneratedThumbnail(any(), any(), any())).thenReturn(new AvatarUploadStrategy.UploadedAvatarFile(
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

        MultipartFile avatarFile = new MultipartFile() {
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