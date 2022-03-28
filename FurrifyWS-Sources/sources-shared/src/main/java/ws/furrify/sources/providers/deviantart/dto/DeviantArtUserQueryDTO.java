package ws.furrify.sources.providers.deviantart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Deviation dto recived from DeviantArt API.
 *
 * @author Skyte
 */
@Data
public class DeviantArtUserQueryDTO {
    /**
     * User object.
     */
    @JsonProperty("user")
    private User user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        /**
         * User unique id.
         */
        @JsonProperty("userid")
        private String userId;

        /**
         * User username.
         */
        @JsonProperty("username")
        private String username;
    }

}
