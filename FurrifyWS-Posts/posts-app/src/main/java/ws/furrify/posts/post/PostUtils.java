package ws.furrify.posts.post;

import ws.furrify.posts.artist.ArtistServiceClient;
import ws.furrify.posts.artist.dto.query.ArtistDetailsQueryDTO;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utils class regarding Post entity.
 *
 * @author Skyte
 */
class PostUtils {

    /**
     * Coverts PostTag with value to PostTag with value and type.
     *
     * @param ownerId          Owner UUID.
     * @param tags             Tags with values to convert.
     * @param tagServiceClient Tag Service Client instance.
     * @return Tags with value and type.
     */
    public static Set<PostTag> tagValueToTagVO(UUID ownerId,
                                               Set<PostTag> tags,
                                               TagServiceClient tagServiceClient) {
        return tags.stream()
                .map(tagWithValue -> {
                            TagDetailsQueryDTO tag = Optional.ofNullable(
                                    tagServiceClient.getUserTag(ownerId, tagWithValue.getValue())
                            ).orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(tagWithValue.getValue())));

                            return new PostTag(tag.getValue(), tag.getType());
                        }
                ).collect(Collectors.toSet());
    }

    /**
     * Coverts PostArtist with artistId to PostArtist with preferred nickname.
     *
     * @param ownerId             Owner UUID.
     * @param artists             Artists with artistIds to convert.
     * @param artistServiceClient Artist Service Client instance.
     * @return Artists with artistIds and preferred nicknames.
     */
    public static Set<PostArtist> artistWithArtistIdToArtistVO(UUID ownerId,
                                                               Set<PostArtist> artists,
                                                               ArtistServiceClient artistServiceClient) {
        return artists.stream()
                .map(oldArtist -> {
                            ArtistDetailsQueryDTO artist = Optional.ofNullable(
                                    artistServiceClient.getUserArtist(ownerId, oldArtist.getArtistId())
                            ).orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(oldArtist.getArtistId())));

                            return new PostArtist(artist.getArtistId(), artist.getPreferredNickname());
                        }
                ).collect(Collectors.toSet());
    }

}
