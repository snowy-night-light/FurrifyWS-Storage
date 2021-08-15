package ws.furrify.posts.post.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.net.URL;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author Skyte
 */
@Data
@Setter(value = PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = PROTECTED)
public class PostMedia {
    @NonNull
    private UUID mediaId;
    @NonNull
    private Integer priority;

    @NonNull
    private URL fileUrl;

    @NonNull
    private URL thumbnailUrl;

    @NonNull
    private String extension;
}