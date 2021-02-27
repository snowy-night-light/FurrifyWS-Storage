package ws.furrify.tags.tag;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.dto.TagDtoFactory;
import ws.furrify.tags.tag.vo.TagType;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TagFacadeTest {

    private static TagRepository tagRepository;
    private static TagFacade tagFacade;

    private TagDTO tagDTO;
    private Tag tag;

    @BeforeEach
    void setUp() {
        tagDTO = TagDTO.builder()
                .title("Walking")
                .description("Desc")
                .value("walking")
                .type(TagType.ACTION)
                .ownerId(UUID.randomUUID())
                .createDate(ZonedDateTime.now())
                .build();

        tag = new TagFactory().from(tagDTO);
    }

    @BeforeAll
    static void beforeAll() {
        tagRepository = mock(TagRepository.class);

        var tagFactory = new TagFactory();
        var tagDtoFactory = new TagDtoFactory();
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<TagEvent>) mock(DomainEventPublisher.class);

        tagFacade = new TagFacade(
                new CreateTagAdapter(tagFactory, eventPublisher, tagRepository),
                new DeleteTagAdapter(eventPublisher, tagRepository),
                new UpdateTagAdapter(eventPublisher, tagRepository),
                new ReplaceTagAdapter(eventPublisher, tagRepository),
                tagRepository,
                tagFactory,
                tagDtoFactory
        );
    }

    @Test
    @DisplayName("Create tag")
    void createTag() {
        // Given ownerId and tagDTO
        UUID userId = UUID.randomUUID();
        // When createTag() method called
        when(tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(false);
        // Then return generated uuid
        assertNotNull(tagFacade.createTag(userId, tagDTO), "TagId was not returned.");
    }

    @Test
    @DisplayName("Create post with existing value")
    void createTag2() {
        // Given ownerId and tagDTO with existing value
        UUID userId = UUID.randomUUID();
        // When createTag() method called
        when(tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(true);
        // Then return generated uuid
        assertThrows(
                RecordAlreadyExistsException.class,
                () -> tagFacade.createTag(userId, tagDTO),
                "Exception was not thrown."
        );
    }


    @Test
    @DisplayName("Replace tag")
    void replaceTag() {
        // Given tagDTO, userId and new value
        UUID userId = UUID.randomUUID();
        // When replaceTag() method called
        when(tagRepository.findByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(Optional.of(tag));
        when(tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(false);
        // Then run successfully
        assertDoesNotThrow(() -> tagFacade.replaceTag(userId, tagDTO.getValue(), tagDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Replace tag with existing new value")
    void replaceTag2() {
        // Given tagDTO, userId and existing new value
        UUID userId = tagDTO.getOwnerId();
        // When replaceTag() method called
        when(tagRepository.findByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(Optional.of(tag));
        when(tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(true);
        // Then throw no record found exception
        assertThrows(
                RecordAlreadyExistsException.class,
                () -> tagFacade.replaceTag(userId, tagDTO.getValue(), tagDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace tag with non existing value")
    void replaceTag3() {
        // Given tagDTO, userId and non existing value
        UUID userId = UUID.randomUUID();
        // When replaceTag() method called
        when(tagRepository.findByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> tagFacade.replaceTag(userId, tagDTO.getValue(), tagDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update tag")
    void updateTag() {
        // Given tagDTO, userId and new value
        UUID userId = UUID.randomUUID();
        // When updateTag() method called
        when(tagRepository.findByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(Optional.of(tag));
        when(tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(false);
        // Then run successfully
        assertDoesNotThrow(() -> tagFacade.updateTag(userId, tagDTO.getValue(), tagDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Update tag with existing new value")
    void updateTag2() {
        // Given tagDTO, userId and existing new value
        UUID userId = tagDTO.getOwnerId();
        // When updateTag() method called
        when(tagRepository.findByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(Optional.of(tag));
        when(tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(true);
        // Then throw no record found exception
        assertThrows(
                RecordAlreadyExistsException.class,
                () -> tagFacade.updateTag(userId, tagDTO.getValue(), tagDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update tag with non existing value")
    void updateTag3() {
        // Given tagDTO, userId and non existing value
        UUID userId = UUID.randomUUID();
        // When updateTag() method called
        when(tagRepository.findByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> tagFacade.updateTag(userId, tagDTO.getValue(), tagDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Delete tag")
    void deleteTag() {
        // Given userId and new value
        UUID userId = UUID.randomUUID();
        // When deletePost() method called
        when(tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(true);
        // Then run successfully
        assertDoesNotThrow(() -> tagFacade.deleteTag(userId, tagDTO.getValue()), "Exception was thrown");
    }

    @Test
    @DisplayName("Delete tag with non existing value")
    void deleteTag2() {
        // Given userId and new value
        UUID userId = UUID.randomUUID();
        // When deleteTag() method called
        when(tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())).thenReturn(false);
        // Then throw record not found exception
        assertThrows(RecordNotFoundException.class, () -> tagFacade.deleteTag(userId, tagDTO.getValue()), "Exception was not thrown");
    }
}