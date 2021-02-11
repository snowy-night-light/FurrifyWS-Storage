package ws.furrify.posts.post.dto.command;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Skyte
 */
@Data
public class PostArtistCreateCommandDTO {
    @NotNull
    UUID artistId;
}
