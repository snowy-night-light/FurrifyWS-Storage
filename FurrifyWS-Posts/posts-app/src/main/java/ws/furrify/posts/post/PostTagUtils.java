package ws.furrify.posts.post;

import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordNotFoundException;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagQueryRepository;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utils class regarding PostTag value object.
 *
 * @author Skyte
 */
public class PostTagUtils {

    /**
     * Coverts PostTag with value to PostTag with value and type.
     *
     * @param ownerId            Owner UUID.
     * @param tags               Tags with values to convert.
     * @param tagQueryRepository Tag Query repository instance.
     * @return Tags with value and type.
     */
    public static Set<PostTag> tagValueToTag(UUID ownerId,
                                             Set<PostTag> tags,
                                             TagQueryRepository tagQueryRepository) {
        return tags.stream()
                .map(tagWithValue -> {
                            TagDetailsQueryDTO tag = tagQueryRepository.findByOwnerIdAndValue(ownerId, tagWithValue.getValue())
                                    .orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(tagWithValue.getValue())));

                            return new PostTag(tag.getValue(), tag.getType().name());
                        }
                ).collect(Collectors.toSet());
    }

}
