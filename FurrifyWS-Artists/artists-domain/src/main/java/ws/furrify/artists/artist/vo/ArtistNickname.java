package ws.furrify.artists.artist.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author Skyte
 */
@Data
@Setter(value = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ArtistNickname {

    private String nickname;

    private boolean isPreferred;
}
