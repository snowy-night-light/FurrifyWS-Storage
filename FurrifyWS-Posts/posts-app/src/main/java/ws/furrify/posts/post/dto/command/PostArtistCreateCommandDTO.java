package ws.furrify.posts.post.dto.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * @author Skyte
 */
@Data
public class PostArtistCreateCommandDTO {
    @NotNull
    UUID artistId;
}
