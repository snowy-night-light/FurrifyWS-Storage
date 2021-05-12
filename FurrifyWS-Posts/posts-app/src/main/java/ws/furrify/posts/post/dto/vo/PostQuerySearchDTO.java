package ws.furrify.posts.post.dto.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * VO for query string search.
 * It is a parser for query string transforming it into pojo fields.
 * Note that artist with and without uses preferred nickname.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class PostQuerySearchDTO implements Serializable {

    private final static String WITH_ARTIST_SYMBOL = "@";
    private final static String WITHOUT_ARTIST_SYMBOL = "-@";

    private final static String WITH_TAG_SYMBOL = "#";
    private final static String WITHOUT_TAG_SYMBOL = "-#";

    private final static String WITH_MEDIA_EXTENSION_SYMBOL = "!";
    private final static String WITHOUT_MEDIA_EXTENSION_SYMBOL = "-!";

    Set<String> withArtists;
    Set<String> withoutArtists;

    Set<String> withTags;
    Set<String> withoutTags;

    Set<String> withMediaExtensions;
    Set<String> withoutMediaExtensions;

    public static PostQuerySearchDTO from(final String query) {
        String[] queryParams = query.split("\\s+");

        return new PostQuerySearchDTO(
                // With artists
                extractValuesFromQueryParams(queryParams, WITH_ARTIST_SYMBOL),
                // Without artists
                extractValuesFromQueryParams(queryParams, WITHOUT_ARTIST_SYMBOL),
                // With tags
                extractValuesFromQueryParams(queryParams, WITH_TAG_SYMBOL),
                // Without tags
                extractValuesFromQueryParams(queryParams, WITHOUT_TAG_SYMBOL),
                // With media extensions
                extractValuesFromQueryParams(queryParams, WITH_MEDIA_EXTENSION_SYMBOL),
                // Without media extensions
                extractValuesFromQueryParams(queryParams, WITHOUT_MEDIA_EXTENSION_SYMBOL)
        );
    }

    private static Set<String> extractValuesFromQueryParams(final String[] queryParams, final String symbol) {
        return Arrays.stream(queryParams)
                /* Filter all params that contain symbol from queryParams at position 0
                   so params symbols cannot overlap */
                .filter(param -> param.indexOf(symbol) == 0)
                // Replace symbol
                .map(param -> param.replace(symbol, ""))
                .collect(Collectors.toSet());
    }
}
