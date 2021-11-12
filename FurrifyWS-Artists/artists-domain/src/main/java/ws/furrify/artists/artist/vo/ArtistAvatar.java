package ws.furrify.artists.artist.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.net.URI;
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
public class ArtistAvatar {
    private long id;

    @NonNull
    private UUID avatarId;

    @NonNull
    private URI fileUri;

    @NonNull
    private URI thumbnailUri;

    @NonNull
    private String extension;
}